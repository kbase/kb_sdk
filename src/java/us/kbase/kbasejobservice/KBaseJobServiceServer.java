package us.kbase.kbasejobservice;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import us.kbase.auth.AuthService;
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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.common.service.UObject;
import us.kbase.mobu.util.ProcessHelper;

//END_HEADER

/**
 * <p>Original spec-file module name: KBaseJobService</p>
 * <pre>
 * </pre>
 */
public class KBaseJobServiceServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    private int lastJobId = 0;
    private Map<String, FinishJobParams> results = new LinkedHashMap<String, FinishJobParams>();
    private File binDir = null;
    private File tempDir = null;
    
    public KBaseJobServiceServer withBinDir(File binDir) {
        this.binDir = binDir;
        return this;
    }
    
    public KBaseJobServiceServer withTempDir(File tempDir) {
        this.tempDir = tempDir;
        return this;
    }
    
    public File getTempDir() {
        return tempDir == null ? new File(".") : tempDir;
    }
    
    public String getBinScript(String scriptName) {
        File ret = null;
        if (binDir == null) {
            ret = new File(".", scriptName);
            if (!ret.exists())
                return scriptName;
        } else {
            ret = new File(binDir, scriptName);
        }
        return ret.getAbsolutePath();
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
                //TODO AUTH make configurable?
                final AuthToken t = AuthService.validateToken(token);
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
                    result.add(runJob(runJobParams, t,
                            rpcCallData.getContext()));
                } else if (rpcName.endsWith("._check_job") && paramsList.size() == 1) {
                    String jobId = paramsList.get(0).asClassInstance(String.class);
                    JobState jobState = checkJob(jobId, t,
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
    //END_CLASS_HEADER

    public KBaseJobServiceServer() throws Exception {
        super("KBaseJobService");
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
        final AuthToken token = authPart;
        returnVal = jobId;
        final File jobDir = new File(tempDir, "job_" + jobId);
        if (!jobDir.exists())
            jobDir.mkdirs();
        File jobFile = new File(jobDir, "job.json");
        UObject.getMapper().writeValue(jobFile, params);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RunJobParams job = getJobParams(jobId, token);
                    String serviceName = job.getMethod().split("\\.")[0];
                    RpcContext context = job.getRpcContext();
                    if (context == null)
                        context = new RpcContext().withRunId("");
                    if (context.getCallStack() == null)
                        context.setCallStack(new ArrayList<MethodCall>());
                    context.getCallStack().add(new MethodCall().withJobId(jobId).withMethod(job.getMethod())
                            .withTime(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZoneUTC()
                                    .print(new DateTime())));
                    File jobDir = new File(tempDir, "job_" + jobId);
                    if (!jobDir.exists())
                        jobDir.mkdirs();
                    Map<String, Object> rpc = new LinkedHashMap<String, Object>();
                    rpc.put("version", "1.1");
                    rpc.put("method", job.getMethod());
                    rpc.put("params", job.getParams());
                    rpc.put("context", job.getRpcContext());
                    File inputFile = new File(jobDir, "input.json");
                    UObject.getMapper().writeValue(inputFile, rpc);
                    String scriptFilePath = getBinScript("run_" + serviceName + "_async_job.sh");
                    File outputFile = new File(jobDir, "output.json");
                    ProcessHelper.cmd("bash", scriptFilePath, inputFile.getCanonicalPath(),
                            outputFile.getCanonicalPath(), token.getToken()).exec(jobDir);
                    FinishJobParams result = UObject.getMapper().readValue(outputFile, FinishJobParams.class);
                    finishJob(jobId, result, token);
                } catch (Exception ex) {
                    FinishJobParams result = new FinishJobParams().withError(new JsonRpcError().withCode(-1L)
                            .withName("JSONRPCError").withMessage("Job service side error: " + ex.getMessage()));
                    try {
                        finishJob(jobId, result, token);
                    } catch (Exception ignore) {}
                }
            }
        }).start();
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
        final File jobDir = new File(tempDir, "job_" + jobId);
        if (!jobDir.exists())
            jobDir.mkdirs();
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
        results.put(jobId, params);
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
        FinishJobParams result = results.get(jobId);
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
            new KBaseJobServiceServer().startupServer(Integer.parseInt(args[0]));
        } else if (args.length == 3) {
            new KBaseJobServiceServer().processRpcCall(new File(args[0]), new File(args[1]), args[2]);
        } else {
            System.out.println("Usage: <program> <server_port>");
            System.out.println("   or: <program> <context_json_file> <output_json_file> <token>");
            return;
        }
    }
}
