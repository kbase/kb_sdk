package us.kbase.common.executionengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import us.kbase.auth.AuthToken;
import us.kbase.common.executionengine.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.common.service.JacksonTupleModule;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.JsonTokenStream;
import us.kbase.common.service.RpcContext;
import us.kbase.common.service.UObject;
import us.kbase.common.utils.NetUtils;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SubAction;

public abstract class CallbackServer extends JsonServerServlet {
    //TODO NJS_SDK move to common repo
    
    // should probably go in java_common or make a common repo for shared
    // NJSW & KB_SDK code, since they're tightly coupled
    private static final long serialVersionUID = 1L;
    
    private static final int MAX_JOBS = 10;
    private static volatile int currentJobs = 0;
    
    private final AuthToken token;
    private final CallbackServerConfig config;
    private ProvenanceAction prov = new ProvenanceAction();
    
    private final static DateTimeFormatter DATE_FORMATTER =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZoneUTC();
    // IMPORTANT: don't access outside synchronized block
    private final Map<String, ModuleRunVersion> vers =
            Collections.synchronizedMap(
                    new LinkedHashMap<String, ModuleRunVersion>());
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private final Map<UUID, FutureTask<Map<String, Object>>> runningJobs =
            new HashMap<UUID, FutureTask<Map<String, Object>>>();
    
    private final Cache<UUID, FutureTask<Map<String, Object>>> resultsCache =
            CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    
    private final Object getRunnerLock = new Object();
    private final Object removeJobLock = new Object();
    
    public CallbackServer(
            final AuthToken token,
            final CallbackServerConfig config,
            final ModuleRunVersion runver,
            final List<UObject> methodParameters,
            final List<String> inputWorkspaceObjects) {
        super("CallbackServer");
        this.token = token;
        this.config = config;
        vers.put(runver.getModuleMethod().getModule(), runver);
        prov.setTime(DATE_FORMATTER.print(new DateTime()));
        prov.setService(runver.getModuleMethod().getModule());
        prov.setMethod(runver.getModuleMethod().getMethod());
        prov.setDescription(
                "KBase SDK method run via the KBase Execution Engine");
        prov.setMethodParams(methodParameters);
        prov.setInputWsObjects(inputWorkspaceObjects);
        prov.setServiceVer(runver.getVersionAndRelease());
        initSilentJettyLogger();
        /* might want to consider a reaper thread that moves jobs to the cache
         * if they've been done for X amount of time
         * Might want to increase the cache lifetime or have a separate
         * cache for jobs that are done but haven't been checked by the user
         */
    }
    
    protected void resetProvenanceAndMethods(final ProvenanceAction newProv) {
        if (newProv == null) {
            throw new NullPointerException("Provenance cannot be null");
        }
        synchronized (this) {
            vers.clear();
            prov = newProv;
        }
    }
    
    @JsonServerMethod(rpc = "CallbackServer.get_provenance")
    public List<ProvenanceAction> getProvenance()
            throws IOException, JsonClientException {
        /* Would be more efficient if provenance was updated online
           although I can't imagine this making a difference compared to
           serialization / transport
         */
        final List<SubAction> sas = new LinkedList<SubAction>();
        for (final ModuleRunVersion mrv: vers.values()) {
           sas.add(new SubAction()
               .withCodeUrl(mrv.getGitURL().toExternalForm())
               .withCommit(mrv.getGitHash())
               .withName(mrv.getModuleMethod().getModule())
               .withVer(mrv.getVersionAndRelease()));
        }
        return new LinkedList<ProvenanceAction>(Arrays.asList(
                new ProvenanceAction()
                    .withSubactions(sas)
                    .withTime(prov.getTime())
                    .withService(prov.getService())
                    .withMethod(prov.getMethod())
                    .withDescription(prov.getDescription())
                    .withMethodParams(prov.getMethodParams())
                    .withInputWsObjects(prov.getInputWsObjects())
                    .withServiceVer(prov.getServiceVer())
                 ));
    }
    
    @JsonServerMethod(rpc = "CallbackServer.status")
    public UObject status() throws IOException, JsonClientException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("state", "OK");
        return new UObject(data);
    }

    protected void cbLog(String log) {
        config.getLogger().logNextLine(String.format("%.2f - CallbackServer: %s",
                (System.currentTimeMillis() / 1000.0), log), false);
    }
    
    protected void processRpcCall(RpcCallData rpcCallData, String token, JsonServerSyslog.RpcInfo info, 
            String requestHeaderXForwardedFor, ResponseStatusSetter response, OutputStream output,
            boolean commandLine) {
        if (rpcCallData.getMethod().startsWith("CallbackServer.")) {
            super.processRpcCall(rpcCallData, token, info, requestHeaderXForwardedFor, response, output, commandLine);
        } else {
            String errorMessage = null;
            Map<String, Object> jsonRpcResponse = null;
            try {
                jsonRpcResponse = handleCall(rpcCallData);
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = ex.getMessage();
            }
            try {
                if (jsonRpcResponse == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    if (errorMessage == null)
                        errorMessage = "Unknown server error";
                    Map<String, Object> error = new LinkedHashMap<String, Object>();
                    error.put("name", "JSONRPCError");
                    error.put("code", -32601);
                    error.put("message", errorMessage);
                    error.put("error", errorMessage);
                    jsonRpcResponse = new LinkedHashMap<String, Object>();
                    jsonRpcResponse.put("version", "1.1");
                    jsonRpcResponse.put("error", error);
                } else {
                    if (jsonRpcResponse.containsKey("error"))
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                final ObjectMapper mapper = new ObjectMapper().registerModule(
                        new JacksonTupleModule());
                mapper.writeValue(new UnclosableOutputStream(output), jsonRpcResponse);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Map<String, Object> handleCall(
            final RpcCallData rpcCallData) throws IOException,
            JsonClientException, InterruptedException {

        final ModuleMethod modmeth = new ModuleMethod(
                rpcCallData.getMethod());
        final Map<String, Object> jsonRpcResponse;
        
        if (modmeth.isCheck()) {
            jsonRpcResponse = runCheck(rpcCallData);
        } else {
            final UUID jobId = UUID.randomUUID();
            cbLog(String.format("Subjob method: %s JobID: %s",
                    modmeth.getModuleDotMethod(), jobId));
            final SubsequentCallRunner runner = getJobRunner(
                    jobId, rpcCallData.getContext(), modmeth);

            // update method name to get rid of suffixes
            rpcCallData.setMethod(modmeth.getModuleDotMethod());
            incrementJobCount();
            if (modmeth.isStandard()) {
                try {
                    jsonRpcResponse = runner.run(rpcCallData);
                } finally {
                    decrementJobCount();
                }
            } else {
                startJob(rpcCallData, jobId, runner);
                jsonRpcResponse = new HashMap<String, Object>();
                jsonRpcResponse.put("version", "1.1");
                jsonRpcResponse.put("id", rpcCallData.getId());
                jsonRpcResponse.put("result", Arrays.asList(jobId));
            }
        }
        return jsonRpcResponse;
    }

    private void startJob(final RpcCallData rpcCallData, final UUID jobId,
            final SubsequentCallRunner runner) throws IOException {
        FutureTask<Map<String, Object>> task = null;
        try {
            /* need to make a copy of the RPC data because it contains
             * a list of UObjects which each contain a reference to a
             * JsonTokenStream. The JTS is closed by the superclass
             * when this call returns and can't be read when the subjob
             * runner starts. 
             * This implementation assumes the UObjects are reasonably
             * small. If they're really big need to do something
             * smarter, or check the size before serializing them and
             * throw an error.
             * 
             * Winds up with 3 copies of the object - the UObject,
             * the ByteArrayOutputStream, and the new byte array
             * returned by toByteArray().
             * 
             * At least this doesn't instantiate the objects which
             * uses 5-20x memory.
             * 
             * Possible improvement:
             * 1) Add constructor to UObject that allows specifying
             * the size (currently runs through the object).
             * 2) Make class like BAOS but with a specific size,
             * then write JTS into exact size array. toByteArray can
             * then just return the array.
             * 3) Also gets rid of BAOS synchronization which isn't
             * needed here.
             * 
             */
            final List<UObject> newobjs = new LinkedList<UObject>();
            for (final UObject uo: rpcCallData.getParams()) {
                final ByteArrayOutputStream baos =
                        new ByteArrayOutputStream();
                uo.write(baos);
                final JsonTokenStream jts = new JsonTokenStream(
                        baos.toByteArray()); //makes another copy
                jts.setTrustedWholeJson(true);
                newobjs.add(new UObject(jts));
            }
            rpcCallData.setParams(newobjs);
            task = new FutureTask<Map<String, Object>>(
                    new SubsequentCallRunnerRunner(
                            runner, rpcCallData));
            executor.execute(task);
            runningJobs.put(jobId, task);
        } catch (IOException | RuntimeException | Error e) {
            decrementJobCount();
            if (task != null) {
                task.cancel(true);
            }
            throw e;
        }
    }

    private synchronized void incrementJobCount() {
        if (currentJobs >= MAX_JOBS) {
            throw new IllegalStateException(String.format(
                    "No more than %s concurrently running methods " +
                            "are allowed", MAX_JOBS));
        }
        currentJobs++;
    }
    
    private synchronized void decrementJobCount() {
        //decrement is not atomic
        currentJobs--;
    }

    private Map<String, Object> runCheck(final RpcCallData rpcCallData)
            throws InterruptedException, IOException {
        
        if (rpcCallData.getParams().size() != 1) {
            throw new IllegalArgumentException(
                    "Check methods take exactly one argument");
        }
        final String jobIdStr;
        try {
            jobIdStr = rpcCallData.getParams().get(0)
                    .asClassInstance(String.class);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("The job ID must be a string");
        }
        final UUID jobId;
        try {
            jobId = UUID.fromString(jobIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid job ID: " + jobIdStr);
        }
        boolean finished = true;
        final FutureTask<Map<String, Object>> task;
        synchronized (removeJobLock) {
            if (runningJobs.containsKey(jobId)) {
                if (runningJobs.get(jobId).isDone()) {
                    task = runningJobs.get(jobId);
                    resultsCache.put(jobId, task);
                    runningJobs.remove(jobId);
                    decrementJobCount();
                } else {
                    finished = false;
                    task = null;
                }
            } else {
                task = resultsCache.getIfPresent(jobId);
            }
        }
        final Map<String, Object> resp;
        if (finished) {
            if (task == null) {
                throw new IllegalStateException(
                        "Either there is no job with ID " + jobId +
                        " or it has expired from the cache");
            }
            resp = getResults(task);
            
        } else {
            resp = new HashMap<String, Object>();
            resp.put("version", "1.1");
            resp.put("id", rpcCallData.getId());
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("finished", finished ? 1 : 0);
        if (resp.containsKey("result")) { // otherwise an error occurred
            result.put("result", resp.get("result"));
        }
        final Map<String, Object> copy = new HashMap<String, Object>(resp);
        // Adding this check makes java client happy because it doesn't like
        // to see both "error" and "result" blocks in JSON-RPC response.
        if (!copy.containsKey("error")) {
            copy.put("result", Arrays.asList(result));
        }
        return copy;
    }

    private Map<String, Object> getResults(
            final FutureTask<Map<String, Object>> task)
            throws InterruptedException, IOException {
        try {
            return Collections.unmodifiableMap(task.get());
        } catch (ExecutionException ee) {
            final Throwable cause = ee.getCause();
            if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw (Error) cause;
            }
        }
    }

    private static class SubsequentCallRunnerRunner
            implements Callable<Map<String, Object>> {

        private final SubsequentCallRunner scr;
        private final RpcCallData rpc;

        public SubsequentCallRunnerRunner(
                final SubsequentCallRunner scr,
                final RpcCallData rpcData) {
            this.scr = scr;
            this.rpc = rpcData;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            return scr.run(rpc);
        }
    }
    
    private SubsequentCallRunner getJobRunner(
            final UUID jobId,
            final RpcContext rpcContext,
            final ModuleMethod modmeth)
            throws IOException, JsonClientException {
        final SubsequentCallRunner runner;
        synchronized (getRunnerLock) {
            final String serviceVer;
            if (vers.containsKey(modmeth.getModule())) {
                final ModuleRunVersion v = vers.get(modmeth.getModule());
                serviceVer = v.getGitHash();
                cbLog(String.format(
                        "WARNING: Module %s was already used once " +
                                "for this job. Using cached " +
                                "version:\n" +
                                "url: %s\n" +
                                "commit: %s\n" +
                                "version: %s\n" +
                                "release: %s\n",
                                modmeth.getModule(), v.getGitURL(),
                                v.getGitHash(), v.getVersion(),
                                v.getRelease()));
            } else {
                serviceVer = rpcContext == null ? null : 
                    (String)rpcContext.getAdditionalProperties()
                        .get("service_ver");
            }
            // Request docker image name from Catalog
            runner = createJobRunner(token, config, jobId, modmeth,
                    serviceVer);
            if (!vers.containsKey(modmeth.getModule())) {
                final ModuleRunVersion v = runner.getModuleRunVersion();
                cbLog(String.format("Running module %s:\n" +
                        "url: %s\n" +
                        "commit: %s\n" +
                        "version: %s\n" +
                        "release: %s\n",
                        modmeth.getModule(), v.getGitURL(),
                        v.getGitHash(), v.getVersion(),
                        v.getRelease()));
                vers.put(modmeth.getModule(), v);
            }
        }
        return runner;
    }

    protected abstract SubsequentCallRunner createJobRunner(
            final AuthToken token,
            final CallbackServerConfig config,
            final UUID jobId,
            final ModuleMethod modmeth,
            final String serviceVer)
            throws IOException, JsonClientException;
    
    @Override
    public void destroy() {
        cbLog("Shutting down executor service");
        final List<Runnable> failed = executor.shutdownNow();
        if (!failed.isEmpty()) {
            cbLog(String.format("Failed to stop %s tasks", failed.size()));
        }
    }

    public static URL getCallbackUrl(int callbackPort)
            throws SocketException {
        return getCallbackUrl(callbackPort, null);
    }

    public static URL getCallbackUrl(int callbackPort, String[] networkInterfaces)
            throws SocketException {
        if (networkInterfaces == null || networkInterfaces.length == 0) {
            networkInterfaces = new String[] {"docker0", "vboxnet0", "vboxnet1",
                    "VirtualBox Host-Only Ethernet Adapter", "en0"};
        }
        final List<String> hostIps = NetUtils.findNetworkAddresses(
                networkInterfaces);
        final String hostIp;
        if (hostIps.isEmpty()) {
            return null;
        } else {
            hostIp = hostIps.get(0);
            if (hostIps.size() > 1) {
                System.out.println("WARNING! Several Docker host IP " +
                        "addresses detected, used first:  " + hostIp);
            }
        }
        try {
            return new URL("http://" + hostIp + ":" + callbackPort);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    "The discovered docker host IP address is invalid: " +
                    hostIp, e);
        }
    }

    public static void initSilentJettyLogger() {
        Log.setLog(new Logger() {
            @Override
            public void warn(String arg0, Object arg1, Object arg2) {}
            @Override
            public void warn(String arg0, Throwable arg1) {}
            @Override
            public void warn(String arg0) {}
            @Override
            public void setDebugEnabled(boolean arg0) {}
            @Override
            public boolean isDebugEnabled() {
                return false;
            }
            @Override
            public void info(String arg0, Object arg1, Object arg2) {}
            @Override
            public void info(String arg0) {}
            @Override
            public String getName() {
                return null;
            }
            @Override
            public Logger getLogger(String arg0) {
                return this;
            }
            @Override
            public void debug(String arg0, Object arg1, Object arg2) {}
            @Override
            public void debug(String arg0, Throwable arg1) {}
            @Override
            public void debug(String arg0) {}
        });
    }

    private static class UnclosableOutputStream extends OutputStream {
        OutputStream inner;
        boolean isClosed = false;
        
        public UnclosableOutputStream(OutputStream inner) {
            this.inner = inner;
        }
        
        @Override
        public void write(int b) throws IOException {
            if (isClosed)
                return;
            inner.write(b);
        }
        
        @Override
        public void close() throws IOException {
            isClosed = true;
        }
        
        @Override
        public void flush() throws IOException {
            inner.flush();
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            if (isClosed)
                return;
            inner.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (isClosed)
                return;
            inner.write(b, off, len);
        }
    }
    
    public static class CallbackRunner implements Runnable {

        private final CallbackServer server;
        private final int port;
        
        public CallbackRunner(CallbackServer server, int port) {
            super();
            this.server = server;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                server.startupServer(port);
            } catch (Exception e) {
                System.err.println("Can't start server:");
                e.printStackTrace();
            }
        }
    }
}
