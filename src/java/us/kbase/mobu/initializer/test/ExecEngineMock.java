package us.kbase.mobu.initializer.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.JacksonTupleModule;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;

//BEGIN_HEADER


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.common.service.UObject;
import us.kbase.kbasejobservice.FinishJobParams;
import us.kbase.kbasejobservice.JobState;
import us.kbase.kbasejobservice.JsonRpcError;
import us.kbase.kbasejobservice.MethodCall;
import us.kbase.kbasejobservice.RpcContext;
import us.kbase.kbasejobservice.RunJobParams;
import us.kbase.mobu.util.DirUtils;
import us.kbase.mobu.util.ProcessHelper;

//END_HEADER

/**
 * <p>Original spec-file module name: KBaseJobService</p>
 * <pre>
 * </pre>
 */
public class ExecEngineMock extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    private int lastJobId = 0;
    private String kbaseEndpoint = null;
    private Map<String, String> jobToModule = new LinkedHashMap<String, String>();
    private Map<String, File> jobToWorkDir = new LinkedHashMap<String, File>();
    private Map<String, Thread> jobToWorker = new LinkedHashMap<String, Thread>();
    private Map<String, FinishJobParams> jobToResults = new LinkedHashMap<String, FinishJobParams>();
    private Map<String, String> moduleToDockerImage = new LinkedHashMap<String, String>();
    private Map<String, File> moduleToRepoDir = new LinkedHashMap<String, File>();
    
    public ExecEngineMock withModule(String moduleName, String dockerImage, File repoDir) {
        moduleToDockerImage.put(moduleName, dockerImage);
        moduleToRepoDir.put(moduleName, repoDir);
        return this;
    }
    
    public ExecEngineMock withKBaseEndpoint(String endpoint) {
        this.kbaseEndpoint = endpoint;
        return this;
    }
    
    protected void processRpcCall(RpcCallData rpcCallData, String token, JsonServerSyslog.RpcInfo info, 
            String requestHeaderXForwardedFor, ResponseStatusSetter response, OutputStream output,
            boolean commandLine) {
        if (rpcCallData.getMethod().startsWith("NarrativeJobService.")) {
            super.processRpcCall(rpcCallData, token, info, requestHeaderXForwardedFor, response, output, commandLine);
        } else {
            String rpcName = rpcCallData.getMethod();
            List<UObject> paramsList = rpcCallData.getParams();
            List<Object> result = null;
            ObjectMapper mapper = new ObjectMapper().registerModule(new JacksonTupleModule());
            RpcContext context = UObject.transformObjectToObject(rpcCallData.getContext(),
                            RpcContext.class);
            Exception exc = null;
            try {
                if (rpcName.endsWith("_submit")) {
                    String origRpcName = rpcName.substring(0, rpcName.lastIndexOf('_'));
                    String[] parts = origRpcName.split(Pattern.quote("."));
                    if (!parts[1].startsWith("_"))
                        throw new IllegalStateException("Unexpected method name: " + rpcName);
                    origRpcName = parts[0] + "." + parts[1].substring(1);
                    RunJobParams runJobParams = new RunJobParams();
                    String serviceVer = rpcCallData.getContext() == null ? null : 
                        (String)rpcCallData.getContext().getAdditionalProperties().get("service_ver");
                    runJobParams.setServiceVer(serviceVer);
                    runJobParams.setMethod(origRpcName);
                    runJobParams.setParams(paramsList);
                    runJobParams.setRpcContext(context);
                    result = new ArrayList<Object>(); 
                    result.add(runJob(runJobParams, new AuthToken(token),
                            rpcCallData.getContext()));
                } else if (rpcName.endsWith("._check_job") && paramsList.size() == 1) {
                    String jobId = paramsList.get(0).asClassInstance(String.class);
                    JobState jobState = checkJob(jobId, new AuthToken(token),
                            rpcCallData.getContext());
                    Long finished = jobState.getFinished();
                    if (finished != 0L) {
                        Object error = jobState.getError();
                        if (error != null) {
                            Map<String, Object> ret = new LinkedHashMap<String, Object>();
                            ret.put("version", "1.1");
                            ret.put("error", error);
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            mapper.writeValue(new UnclosableOutputStream(output), ret);
                            return;
                        }
                    }
                    result = new ArrayList<Object>();
                    result.add(jobState);
                } else {
                    throw new IllegalArgumentException("Method [" + rpcName +
                            "] doesn't ends with \"_submit\" or \"_check_job\" suffix");
                }
                Map<String, Object> ret = new LinkedHashMap<String, Object>();
                ret.put("version", "1.1");
                ret.put("result", result);
                mapper.writeValue(new UnclosableOutputStream(output), ret);
                return;
            } catch (Exception ex) {
                exc = ex;
            }
            try {
                Map<String, Object> error = new LinkedHashMap<String, Object>();
                error.put("name", "JSONRPCError");
                error.put("code", -32601);
                error.put("message", exc.getLocalizedMessage());
                error.put("error", ExceptionUtils.getStackTrace(exc));
                Map<String, Object> ret = new LinkedHashMap<String, Object>();
                ret.put("version", "1.1");
                ret.put("error", error);
                mapper.writeValue(new UnclosableOutputStream(output), ret);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                new Exception("Error sending error: " +
                        exc.getLocalizedMessage(), ex).printStackTrace();
            }
        }
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
    
    public void waitAndCleanAllJobs() throws Exception {
        for (String jobId : new ArrayList<String>(jobToModule.keySet())) {
            String moduleName = jobToModule.get(jobId);
            Thread t = jobToWorker.get(jobId);
            t.join();
            jobToModule.remove(jobId);
            jobToResults.remove(jobId);
            jobToWorkDir.remove(jobId);
            jobToWorker.remove(jobId);
            moduleToDockerImage.remove(moduleName);
            moduleToRepoDir.remove(moduleName);
        }
    }
    //END_CLASS_HEADER

    public ExecEngineMock() throws Exception {
        super("NarrativeJobService");
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: run_job</p>
     * <pre>
     * Start a new job
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasejobservice.RunJobParams RunJobParams}
     * @return   parameter "job_id" of original type "job_id" (A job id.)
     */
    @JsonServerMethod(rpc = "KBaseJobService.run_job")
    public String runJob(RunJobParams params, AuthToken authPart, us.kbase.common.service.RpcContext... jsonRpcCallContext) throws Exception {
        String returnVal = null;
        //BEGIN run_job
        lastJobId++;
        final String jobId = "" + lastJobId;
        final String token = authPart.toString();
        returnVal = jobId;
        String moduleName = params.getMethod().split(Pattern.quote("."))[0];
        jobToModule.put(jobId, moduleName);
        File moduleDir = moduleToRepoDir.get(moduleName);
        File testLocalDir = new File(moduleDir, "test_local");
        final File jobDir = new File(testLocalDir, "job_" + jobId);
        if (!jobDir.exists())
            jobDir.mkdirs();
        jobToWorkDir.put(jobId, jobDir);
        File jobFile = new File(jobDir, "job.json");
        UObject.getMapper().writeValue(jobFile, params);
        final String dockerImage = moduleToDockerImage.get(moduleName);
        RpcContext context = params.getRpcContext();
        if (context == null)
            context = new RpcContext().withRunId("");
        if (context.getCallStack() == null)
            context.setCallStack(new ArrayList<MethodCall>());
        context.getCallStack().add(new MethodCall().withJobId(jobId).withMethod(params.getMethod())
                .withTime(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZoneUTC()
                        .print(new DateTime())));
        Map<String, Object> rpc = new LinkedHashMap<String, Object>();
        rpc.put("version", "1.1");
        rpc.put("method", params.getMethod());
        rpc.put("params", params.getParams());
        rpc.put("context", params.getRpcContext());
        File tokenFile = new File(jobDir, "token");
        FileUtils.writeStringToFile(tokenFile, token);
        File inputFile = new File(jobDir, "input.json");
        UObject.getMapper().writeValue(inputFile, rpc);
        final File outputFile = new File(jobDir, "output.json");
        final String containerName = "test_" + moduleName.toLowerCase() + "_" + 
                System.currentTimeMillis();
        File runDockerSh = new File(testLocalDir, "run_docker.sh");
        final String runDockerPath = DirUtils.getFilePath(runDockerSh);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessHelper.cmd("bash", runDockerPath, "run", "-v", 
                            jobDir.getCanonicalPath() + ":/kb/module/work", "--name", containerName, 
                            "-e", "KBASE_ENDPOINT=" + kbaseEndpoint, dockerImage, "async").exec(jobDir, 
                                    null, (PrintWriter)null, null);
                    if (!outputFile.exists()) {
                        ProcessHelper.cmd("bash", runDockerPath, "logs", containerName).exec(jobDir);
                        throw new IllegalStateException("Output file wasn't created");
                    }
                    FinishJobParams result = UObject.getMapper().readValue(outputFile, FinishJobParams.class);
                    finishJob(jobId, result, new AuthToken(token));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    FinishJobParams result = new FinishJobParams().withError(new JsonRpcError().withCode(-1L)
                            .withName("JSONRPCError").withMessage("Job service side error: " + ex.getMessage()));
                    try {
                        finishJob(jobId, result, new AuthToken(token));
                    } catch (Exception ignore) {}
                } finally {
                    try {
                        ProcessHelper.cmd("bash", runDockerPath, "rm", "-v", "-f", 
                                containerName).exec(jobDir, null, (PrintWriter)null, null);
                    } catch (Exception ex) {
                        System.out.println("Error deleting container [" + containerName + "]: " + ex.getMessage());
                    }
                }
            }
        });
        t.start();
        jobToWorker.put(jobId, t);
        //END run_job
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: get_job_params</p>
     * <pre>
     * Get job params necessary for job execution
     * </pre>
     * @param   jobId   instance of original type "job_id" (A job id.)
     * @return   parameter "params" of type {@link us.kbase.kbasejobservice.RunJobParams RunJobParams}
     */
    @JsonServerMethod(rpc = "KBaseJobService.get_job_params")
    public RunJobParams getJobParams(String jobId, AuthToken authPart, us.kbase.common.service.RpcContext... jsonRpcCallContext) throws Exception {
        RunJobParams returnVal = null;
        //BEGIN get_job_params
        final File jobDir = jobToWorkDir.get(jobId);
        File jobFile = new File(jobDir, "job.json");
        returnVal = UObject.getMapper().readValue(jobFile, RunJobParams.class);
        //END get_job_params
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: finish_job</p>
     * <pre>
     * Register results of already started job
     * </pre>
     * @param   jobId   instance of original type "job_id" (A job id.)
     * @param   params   instance of type {@link us.kbase.kbasejobservice.FinishJobParams FinishJobParams}
     */
    @JsonServerMethod(rpc = "KBaseJobService.finish_job")
    public void finishJob(String jobId, FinishJobParams params, AuthToken authPart, us.kbase.common.service.RpcContext... jsonRpcCallContext) throws Exception {
        //BEGIN finish_job
        jobToResults.put(jobId, params);
        //END finish_job
    }

    /**
     * <p>Original spec-file function name: check_job</p>
     * <pre>
     * Check if job is finished and get results/error
     * </pre>
     * @param   jobId   instance of original type "job_id" (A job id.)
     * @return   parameter "job_state" of type {@link us.kbase.kbasejobservice.JobState JobState}
     */
    @JsonServerMethod(rpc = "KBaseJobService.check_job")
    public JobState checkJob(String jobId, AuthToken authPart, us.kbase.common.service.RpcContext... jsonRpcCallContext) throws Exception {
        JobState returnVal = null;
        //BEGIN check_job
        returnVal = new JobState();
        FinishJobParams result = jobToResults.get(jobId);
        if (result == null) {
            returnVal.setFinished(0L);
        } else {
            returnVal.setFinished(1L);
            returnVal.setResult(result.getResult());
            returnVal.setError(result.getError());
        }
        //END check_job
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            new ExecEngineMock().startupServer(Integer.parseInt(args[0]));
        } else if (args.length == 3) {
            new ExecEngineMock().processRpcCall(new File(args[0]), new File(args[1]), args[2]);
        } else {
            System.out.println("Usage: <program> <server_port>");
            System.out.println("   or: <program> <context_json_file> <output_json_file> <token>");
            return;
        }
    }
}
