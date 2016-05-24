package us.kbase.mobu.tester.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static us.kbase.narrativejobservice.test.AweClientDockerJobScriptTest.loadConfig;
import static us.kbase.narrativejobservice.test.AweClientDockerJobScriptTest.getMVI;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.ini4j.InvalidFileFormatException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.catalog.CatalogClient;
import us.kbase.catalog.SelectOneModuleParams;
import us.kbase.common.service.JacksonTupleModule;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.ServerException;
import us.kbase.common.service.UObject;
import us.kbase.common.test.controllers.ControllerCommon;
import us.kbase.common.utils.ModuleMethod;
import us.kbase.narrativejobservice.LineLogger;
import us.kbase.narrativejobservice.NarrativeJobServiceServer;
import us.kbase.narrativejobservice.subjobs.CallbackServer;
import us.kbase.narrativejobservice.subjobs.NJSCallbackServer;
import us.kbase.narrativejobservice.subjobs.CallbackServerConfigBuilder;
import us.kbase.narrativejobservice.subjobs.ModuleRunVersion;
import us.kbase.narrativejobservice.subjobs.CallbackServerConfigBuilder.CallbackServerConfig;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SubAction;

public class CallbackServerTest {

    public static final Path TEST_DIR = Paths.get("temp_test_callback");
    
    public static AuthToken token;

    
    private final static DateTimeFormatter DATE_PARSER =
            new DateTimeFormatterBuilder()
                .append(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"))
                .appendOptional(DateTimeFormat.forPattern(".SSS").getParser())
                .append(DateTimeFormat.forPattern("Z"))
                .toFormatter();
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        FileUtils.deleteDirectory(TEST_DIR.toFile());
        Files.deleteIfExists(TEST_DIR);
        Files.createDirectories(TEST_DIR);
        final Properties props = TesterUtils.props();
        token =  AuthService.login(get(props, "user"),
                get(props, "password")).getToken();
        
    }

    private static CallbackStuff startCallBackServer()
            throws Exception {
        final ModuleRunVersion runver = new ModuleRunVersion(
                new URL("https://fakefakefake.com"),
                new ModuleMethod("foo.bar"),
                "githash", "0.0.5", "dev");
        final List<UObject> params = new LinkedList<UObject>();
        final List<String> wsobjs = new ArrayList<String>();
        return startCallBackServer(runver, params, wsobjs);
    }
    
    private static CallbackStuff startCallBackServer(
            final ModuleRunVersion runver,
            final List<UObject> params,
            final List<String> wsobjs)
            throws Exception {
        final LineLogger log = new LineLogger() {
            
            @Override
            public void logNextLine(String line, boolean isError) {
                System.out.println("Docker logger std" +
                        (isError ? "err" : "out") + ": " + line);
                
            }
        };
        final int callbackPort = ControllerCommon.findFreePort();
        final URL callbackUrl = CallbackServer.getCallbackUrl(callbackPort);
        final Path temp = Files.createTempDirectory(TEST_DIR, "cbt");
        final CallbackServerConfig cbcfg =
                new CallbackServerConfigBuilder(
                AweClientDockerJobScriptTest.loadConfig(), callbackUrl,
                        temp, log).build();
        final JsonServerServlet callback = new NJSCallbackServer(
                token, cbcfg, runver, params, wsobjs);
        final Server callbackServer = new Server(callbackPort);
        final ServletContextHandler srvContext =
                new ServletContextHandler(
                        ServletContextHandler.SESSIONS);
        srvContext.setContextPath("/");
        callbackServer.setHandler(srvContext);
        srvContext.addServlet(new ServletHolder(callback),"/*");
        callbackServer.start();
        Thread.sleep(1000);
        return new CallbackStuff(callbackUrl, temp, callbackServer);
    }
    
    private static class CallbackStuff {
        final public URL callbackURL;
        final public Path tempdir;
        final public Server server;
        
        final private ObjectMapper mapper = new ObjectMapper().registerModule(
                new JacksonTupleModule());

        private CallbackStuff(URL callbackURL, Path tempdir,
                Server server) {
            super();
            this.callbackURL = callbackURL;
            this.tempdir = tempdir;
            this.server = server;
        }
        
        public List<ProvenanceAction> getProvenance() throws Exception {
            final TypeReference<List<ProvenanceAction>> retType =
                    new TypeReference<List<ProvenanceAction>>() {};
            final List<Object> arg = new ArrayList<Object>();
            final String method = "CallbackServer.get_provenance";
            
            return callServer(method, arg, null, retType);
        }
        
        public Map<String, Object> callMethod(
                final String method,
                final Map<String, Object> params,
                final String serviceVer)
                throws Exception {
            return callServer(method, Arrays.asList(params), serviceVer,
                    new TypeReference<Map<String,Object>>() {});
        }
        
        public UUID callAsync(
                final String method,
                final Map<String, Object> params,
                final String serviceVer)
                throws Exception {
            return callServer(method + "_async", Arrays.asList(params),
                    serviceVer, new TypeReference<UUID>() {});
        }
        
        public Map<String, Object> checkAsync(final UUID jobId)
                throws Exception {
            return callServer("foo.bar_check", Arrays.asList(jobId), "fake",
                    new TypeReference<Map<String,Object>>() {});
        }
        
        public Map<String, Object> checkAsync(final List<?> params)
                throws Exception {
            return callServer("foo.bar_check", params, "fake",
                    new TypeReference<Map<String,Object>>() {});
        }

        private <RET> RET callServer(
                final String method,
                final List<?> args,
                final String serviceVer,
                final TypeReference<RET> retType)
                throws Exception {
            final HttpURLConnection conn =
                    (HttpURLConnection) callbackURL.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            try (final OutputStream os = conn.getOutputStream()) {
                final Map<String, Object> req = new HashMap<String, Object>();
                final String id = ("" + Math.random()).replace(".", "");
                req.put("params", args);
                req.put("method", method);
                req.put("version", "1.1");
                req.put("id", id);
                if (serviceVer != null) {
                    req.put("context", ImmutableMap.<String, String>builder()
                                .put("service_ver", serviceVer).build());
                }
                mapper.writeValue(os, req);
                os.flush();
            }
            final int code = conn.getResponseCode();
            if (code == 500) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> msg = mapper.readValue(
                        conn.getErrorStream(), Map.class);
                @SuppressWarnings("unchecked")
                final Map<String, Object> err =
                        (Map<String, Object>) msg.get("error"); 
                final String data = (String) (err.get("data") == null ?
                        err.get("error") : err.get("data"));
                System.out.println("got traceback from server in test:");
                System.out.println(data);
                throw new ServerException((String) err.get("message"),
                        (Integer) err.get("code"), (String) err.get("name"),
                        data);
            } else {
                @SuppressWarnings("unchecked")
                final Map<String, Object> msg = mapper.readValue(
                        conn.getInputStream(), Map.class);
                @SuppressWarnings("unchecked")
                final List<List<Object>> ret =
                        (List<List<Object>>) msg.get("result");
                final RET res = UObject.transformObjectToObject(
                        ret.get(0), retType);
                return res;
            }
        }
    }
    
    private static String get(Properties props, String propName) {
        final String ret = props.getProperty(propName);
        if (ret == null)
            throw new IllegalStateException("Property is not defined: " +
                    propName);
        return ret;
    }
    
    private void checkResults(Map<String, Object> got,
            Map<String, Object> params, String name) {
        assertThat("incorrect name", (String) got.get("name"), is(name));
        if (params.containsKey("wait")) {
            assertThat("incorrect wait time", (Integer) got.get("wait"),
                    is(params.get("wait")));
        }
        assertThat("incorrect id", (String) got.get("id"),
                is(params.get("id")));
        assertNotNull("missing hash", (String) got.get("hash"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> parjobs =
                (List<Map<String, Object>>) params.get("jobs");
        if (params.containsKey("jobs")) {
            @SuppressWarnings("unchecked")
            List<List<Map<String,Object>>> gotjobs =
                    (List<List<Map<String, Object>>>) got.get("jobs");
            assertNotNull("missing jobs", gotjobs);
            assertThat("not same number of jobs", gotjobs.size(),
                    is(parjobs.size()));
            Iterator<List<Map<String, Object>>> gotiter = gotjobs.iterator();
            Iterator<Map<String, Object>> pariter = parjobs.iterator();
            while (gotiter.hasNext()) {
                Map<String, Object> p = pariter.next();
                String modmeth = (String) p.get("method");
                String module = modmeth.split("\\.")[0];
                //params are always wrapped in a list
                @SuppressWarnings("unchecked")
                final Map<String, Object> innerparams =
                    ((List<Map<String, Object>>) p.get("params")).get(0);
                //as are results
                checkResults(gotiter.next().get(0), innerparams,
                        (String) module);
            }
        }
    }
    
    @Test
    public void maxJobs() throws Exception {
        final CallbackStuff res = startCallBackServer();
        System.out.println("Running maxJobs in dir " + res.tempdir);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "outer");
        params.put("wait", 1);
        LinkedList<Map<String, Object>> jobs =
                new LinkedList<Map<String, Object>>();
        params.put("jobs", jobs);
        params.put("run_jobs_async", true);
        for (int i = 0; i < 4; i++) {
            Map<String, Object> inner2 = new HashMap<String, Object>();
            inner2.put("wait", 3);
            inner2.put("id", "inner2-" + i);
            Map<String, Object> injob = new HashMap<String, Object>();
            injob.put("method", "njs_sdk_test_1.run");
            injob.put("ver", "dev");
            injob.put("params", Arrays.asList(inner2));
            if (i % 2 == 0) {
                injob.put("cli_async", true);
            }
            Map<String, Object> innerparams = new HashMap<String, Object>();
            innerparams.put("wait", 2);
            innerparams.put("id", "inner-" + i);
            innerparams.put("jobs", Arrays.asList(injob));
            
            Map<String, Object> outerjob = new HashMap<String, Object>();
            outerjob.put("method", "njs_sdk_test_1.run");
            outerjob.put("ver", "dev");
            outerjob.put("params", Arrays.asList(innerparams));
            if (i % 2 == 0) {
                outerjob.put("cli_async", true);
            };
            jobs.add(outerjob);
        }
        final ImmutableMap<String, Object> singlejob =
                ImmutableMap.<String, Object>builder()
                    .put("method", "njs_sdk_test_1.run")
                    .put("ver", "dev")
                    .put("params", Arrays.asList(
                        ImmutableMap.<String, Object>builder()
                            .put("id", "singlejob")
                            .put("wait", 2)
                            .build()))
                    .build();
        jobs.add(singlejob);
        
        // should run
        Map<String, Object> r = res.callMethod(
                "njs_sdk_test_1.run", params, "dev");
        checkResults(r, params, "njs_sdk_test_1");
        
        //throw an error during a sync job to check the job counter is
        // decremented
        Map<String, Object> errparam = new HashMap<String, Object>();
        errparam.put("id", "errjob");
        errparam.put("except", "planned exception");
        try {
            res.callMethod("njs_sdk_test_1.run", errparam, "dev");
        } catch (ServerException se) {
            assertThat("incorrect error message", se.getLocalizedMessage(),
                    is("planned exception errjob"));
        }
        
        // run again to ensure the job counter is back to 0
        r = res.callMethod("njs_sdk_test_1.run", params, "dev");
        checkResults(r, params, "njs_sdk_test_1");
        
        // run with 11 jobs to force an exception
        jobs.add(ImmutableMap.<String, Object>builder()
                .put("method", "njs_sdk_test_1.run")
                .put("ver", "dev")
                .put("params", Arrays.asList(
                        ImmutableMap.<String, Object>builder()
                            .put("id", "singlejob2")
                            .put("wait", 2)
                            .build()))
                .build());
        try {
            res.callMethod("njs_sdk_test_1.run", params, "dev");
        } catch (ServerException se) {
            assertThat("incorrect error message", se.getLocalizedMessage(),
                    is("No more than 10 concurrently running methods are allowed"));
        }
        
        res.server.stop();
    }

    @Test
    public void async() throws Exception {
        final CallbackStuff res = startCallBackServer();
        System.out.println("Running async in dir " + res.tempdir);
        final Map<String, Object> simplejob =
                ImmutableMap.<String, Object>builder()
                    .put("id", "simplejob")
                    .put("wait", 10)
                    .build();
        UUID jobId = res.callAsync("njs_sdk_test_1.run", simplejob, "dev");
        int attempts = 1;
        List<Map<String, Object>> got;
        while (true) {
            if (attempts > 20) {
                fail("timed out waiting for async results");
            }
            Map<String, Object> status = res.checkAsync(jobId);
            if (((Integer) status.get("finished")) == 1) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> tempgot =
                        (List<Map<String, Object>>) status.get("result");
                got = tempgot;
                break;
            }
            Thread.sleep(1000);
            attempts++;
        }
        checkResults(got.get(0), simplejob, "njs_sdk_test_1");
        
        // now the result should be in the cache, so check again
        Map<String, Object> status = res.checkAsync(jobId);
        assertThat("job not done", (Integer) status.get("finished"), is(1));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tempgot =
                (List<Map<String, Object>>) status.get("result");
        checkResults(tempgot.get(0), simplejob, "njs_sdk_test_1");
        
        final UUID randomUUID = UUID.randomUUID();
        try {
            res.checkAsync(randomUUID);
        } catch (ServerException ise) {
            assertThat("wrong exception message", ise.getLocalizedMessage(),
                   is(String.format("Either there is no job with ID %s " + 
                           "or it has expired from the cache", randomUUID)));
        }
        res.server.stop();
    }
    
    @Test
    public void checkWithBadArgs() throws Exception {
        final CallbackStuff res = startCallBackServer();
        System.out.println("Running checkwithBadArgs in dir " + res.tempdir);
        String badUUID = UUID.randomUUID().toString();
        badUUID = badUUID.substring(0, badUUID.length() - 1) + "g";
        
        try {
            res.checkAsync(Arrays.asList(badUUID));
        } catch (ServerException ise) {
            assertThat("wrong exception message", ise.getLocalizedMessage(),
                   is("Invalid job ID: " + badUUID));
        }
        try {
            res.checkAsync(Arrays.asList(new HashMap<>()));
        } catch (ServerException ise) {
            assertThat("wrong exception message", ise.getLocalizedMessage(),
                   is("The job ID must be a string"));
        }
        try {
            res.checkAsync(Arrays.asList(1, 2));
        } catch (ServerException ise) {
            assertThat("wrong exception message", ise.getLocalizedMessage(),
                   is("Check methods take exactly one argument"));
        }
        
        res.server.stop();
    }
    
    @Test
    public void badRelease() throws Exception {
        final CallbackStuff res = startCallBackServer();
        System.out.println("Running badRelease in dir " + res.tempdir);
        // note that dev and beta releases can only have one version each,
        // version tracking only happens for prod
        
        failJob(res, "njs_sdk_test_1foo.run", "beta",
                "Error looking up module njs_sdk_test_1foo: Operation " +
                "failed - module/repo is not registered.");
        failJob(res, "njs_sdk_test_1.run", "beta",
                "There is no release version 'beta' for module njs_sdk_test_1");
        failJob(res, "njs_sdk_test_1.run", "release",
                "There is no release version 'release' for module " +
                "njs_sdk_test_1");
        failJob(res, "njs_sdk_test_1.run", null,
                "There is no release version 'release' for module " +
                "njs_sdk_test_1");

        //TODO fix these when catalog is fixed
        //this is the newest git commit and was registered in dev but 
        //then the previous git commit was registered in dev
        String git = "b0d487271c22f793b381da29e266faa9bb0b2d1b";
        failJob(res, "njs_sdk_test_1.run", git,
                "Error looking up module njs_sdk_test_1 with version " +
                git + ": 'NoneType' object has no attribute '__getitem__'");
        failJob(res, "njs_sdk_test_1.run", "foo",
                "Error looking up module njs_sdk_test_1 with version foo: " +
                "'NoneType' object has no attribute '__getitem__'");
        
        res.server.stop();
    }
    
    @Test
    public void badMethod() throws Exception {
        final CallbackStuff res = startCallBackServer();
        System.out.println("Running badMethod in dir " + res.tempdir);
        failJob(res, "njs_sdk_test_1run", "foo",
                "Can not find method [CallbackServer.njs_sdk_test_1run_async] " +
                "in server class us.kbase.narrativejobservice.subjobs.NJSCallbackServer");
        failJob(res, "njs_sdk_test_1.r.un", "foo",
                "Illegal method name: njs_sdk_test_1.r.un_async");
        res.server.stop();
    }
    
    private void failJob(CallbackStuff cbs, String moduleMeth, String release,
            String exp)
            throws Exception{
        try {
            cbs.callAsync(moduleMeth, new HashMap<String, Object>(), release);
            fail("Ran bad job");
        } catch (ServerException se) {
            assertThat("correct exception", se.getLocalizedMessage(), is(exp));
        }
    }
    
    @Test
    public void multiCallProvenance() throws Exception {
        String moduleName = "njs_sdk_test_1";
        String methodName = "run";
        String release = "dev";
        String ver = "0.0.1";
        final ModuleRunVersion runver = new ModuleRunVersion(
                new URL("https://github.com/kbasetest/njs_sdk_test_1"),
                new ModuleMethod(moduleName + "." + methodName),
                "ed8038b12b9ebd424c60697204fb49f89a9df906", ver, release);
        List<String> wsobjs = Arrays.asList("foo", "bar", "baz");
        List<UObject> params = new ArrayList<UObject>();
        params.add(new UObject(Arrays.asList("foo", "bar")));
        params.add(new UObject(ImmutableMap.<String, String>builder()
                        .put("foo", "bar").build()));
        final CallbackStuff res = startCallBackServer(runver, params, wsobjs);
        System.out.println("Running multiCallProvenance in dir " + res.tempdir);
        String moduleName2 = "njs_sdk_test_2";
        @SuppressWarnings("unchecked")
        Map<String, Object> methparams = UObject.transformStringToObject(
                String.format(
            "{\"jobs\": [{\"method\": \"%s\"," +
                         "\"params\": [{\"id\": \"id1\", \"wait\": 3}]," +
                         "\"ver\": \"%s\"" +
                         "}," +
                        "{\"method\": \"%s\"," +
                         "\"params\": [{\"id\": \"id2\", \"wait\": 3}]," +
                         "\"ver\": \"%s\"" +
                         "}," +
                        "{\"method\": \"%s\"," +
                         "\"params\": [{\"id\": \"id3\", \"wait\": 3}]," +
                         "\"ver\": \"%s\"" +
                         "}" +
                        "]," +
             "\"id\": \"myid\"" + 
             "}",
             moduleName2 + "." + methodName,
             // dev is on this commit
             "570b5963d50710d4e15621a77673a9bc0c7a7857",
             moduleName + "." + methodName,
             // this is the latest commit, but a prior commit is registered
             //for dev
             "17f87270741e6b59bdfc083f143137d208e3f135",
             moduleName2 + "." + methodName,
             "dev"), Map.class);
        List<SubActionSpec> expsas = new LinkedList<SubActionSpec>();
        expsas.add(new SubActionSpec()
            .withMod(moduleName)
            .withVer("0.0.1")
            .withRel("dev")
        );
        expsas.add(new SubActionSpec()
            .withMod(moduleName2)
            .withVer("0.0.5")
            .withCommit("570b5963d50710d4e15621a77673a9bc0c7a7857")
        );
        Map<String, Object> results = res.callMethod(
                moduleName + '.' + methodName, methparams, "dev");
        List<ProvenanceAction> p = res.getProvenance();
        checkProvenance(moduleName, methodName, release, ver, params,
                expsas, wsobjs, p);
        checkResults(results, methparams, moduleName);
        
        res.server.stop();
    }
    
    private static class SubActionSpec {
        public String module;
        public String release;
        public String ver;
        public String commit;
        
        public SubActionSpec (){}
        public SubActionSpec withMod(String mod) {
            this.module = mod;
            return this;
        }
        
        public SubActionSpec withRel(String rel) {
            this.release = rel;
            return this;
        }
        
        public SubActionSpec withVer(String ver) {
            this.ver = ver;
            return this;
        }
        
        public SubActionSpec withCommit(String commit) {
            this.commit = commit;
            return this;
        }
        public String getVerRel() {
            if (release == null) {
                return ver;
            }
            return ver + "-" + release;
        }
    }

    private void checkProvenance(
            String moduleName,
            String methodName,
            String release,
            String ver,
            List<UObject> methparams,
            List<SubActionSpec> subs,
            List<String> wsobjs,
            List<ProvenanceAction> prov)
            throws Exception, IOException, InvalidFileFormatException,
            JsonClientException {
        if (release != null) {
            ver = ver + "-" + release;
        }

        assertThat("number of provenance actions",
                prov.size(), is(1));
        ProvenanceAction pa = prov.get(0);
        long got = DATE_PARSER.parseDateTime(pa.getTime()).getMillis();
        long now = new Date().getTime();
        assertTrue("got prov time < now ", got < now);
        assertTrue("got prov time > now - 5m", got > now - (5 * 60 * 1000));
        assertThat("correct service", pa.getService(), is(moduleName));
        assertThat("correct service version", pa.getServiceVer(),
                is(ver));
        assertThat("correct method", pa.getMethod(), is(methodName));
        assertThat("number of params", pa.getMethodParams().size(),
                is(methparams.size()));
        for (int i = 1; i < methparams.size(); i++) {
            assertThat("params not equal",
                    pa.getMethodParams().get(i).asClassInstance(Object.class),
                    is(methparams.get(i).asClassInstance(Object.class)));
        }
        assertThat("correct incoming ws objs",
                new HashSet<String>(pa.getInputWsObjects()),
                is(new HashSet<String>(wsobjs)));
        checkSubActions(pa.getSubactions(), subs);
    }
    
    private void checkSubActions(List<SubAction> gotsas,
            List<SubActionSpec> expsas) throws Exception {
        CatalogClient cat = getCatalogClient(token, loadConfig());
        assertThat("correct # of subactions",
                gotsas.size(), is(expsas.size()));
        for (SubActionSpec sa: expsas) {
            if (sa.commit == null) {
                sa.commit = getMVI(cat.getModuleInfo(
                        new SelectOneModuleParams().withModuleName(sa.module)),
                        sa.release).getGitCommitHash();
            }
        }
        Iterator<SubAction> giter = gotsas.iterator();
        Iterator<SubActionSpec> eiter = expsas.iterator();
        while (giter.hasNext()) {
            SubAction got = giter.next();
            SubActionSpec sa = eiter.next();
            assertThat("correct code url", got.getCodeUrl(),
                    is("https://github.com/kbasetest/" + sa.module));
            assertThat("correct commit", got.getCommit(), is(sa.commit));
            assertThat("correct name", got.getName(), is(sa.module + ".run"));
            assertThat("correct version", got.getVer(), is(sa.getVerRel()));
        }
    }
    
    private static CatalogClient getCatalogClient(AuthToken auth, 
            Map<String, String> config) throws Exception {
        String catUrl = config.get(
                NarrativeJobServiceServer.CFG_PROP_CATALOG_SRV_URL);
        CatalogClient ret = new CatalogClient(new URL(catUrl), auth);
        ret.setIsInsecureHttpConnectionAllowed(true);
        return ret;
    }
}
