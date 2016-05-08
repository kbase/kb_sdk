package us.kbase.mobu.tester;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.common.service.JacksonTupleModule;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.UObject;
import us.kbase.workspace.ProvenanceAction;

public class CallbackServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    private final File testLocalDir;
    private final int callbackPort;
    
    public CallbackServer(File testLocalDir, int callbackPort) {
        super("CallbackServer");
        this.testLocalDir = testLocalDir;
        this.callbackPort = callbackPort;
    }
    
    @JsonServerMethod(rpc = "CallbackServer.test")
    public UObject test(UObject input) throws IOException, JsonClientException {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = input.asClassInstance(Map.class);
        data.put("test", "passed");
        return new UObject(data);
    }
    
    @JsonServerMethod(rpc = "CallbackServer.get_provenance")
    public List<ProvenanceAction> getProvenance() throws IOException, JsonClientException {
        ProvenanceAction prov = new ProvenanceAction()
            .withService("Here's")
            .withMethod("some fake")
            .withMethodParams(Arrays.asList(new UObject(
                    Arrays.asList("test", "data"))));
        return new LinkedList<ProvenanceAction>(Arrays.asList(prov));
    }

    protected void processRpcCall(RpcCallData rpcCallData, String token, 
            JsonServerSyslog.RpcInfo info, String requestHeaderXForwardedFor, 
            ResponseStatusSetter response, OutputStream output,
            boolean commandLine) {
        if (rpcCallData.getMethod().startsWith("CallbackServer.")) {
            super.processRpcCall(rpcCallData, token, info, requestHeaderXForwardedFor, 
                    response, output, commandLine);
        } else {
            String rpcName = rpcCallData.getMethod();
            Map<String, Object> jsonRpcResponse = null;
            String errorMessage = null;
            ObjectMapper mapper = new ObjectMapper().registerModule(new JacksonTupleModule());
            try {
                String serviceVer = rpcCallData.getContext() == null ? null : 
                    (String)rpcCallData.getContext().getAdditionalProperties().get("service_ver");
                // Request docker image name from Catalog
                SubsequentCallRunner runner = new SubsequentCallRunner(testLocalDir, rpcName, 
                        serviceVer, callbackPort);
                // Run method in local docker container
                jsonRpcResponse = runner.run(rpcCallData);
            } catch (Exception ex) {
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
                mapper.writeValue(new UnclosableOutputStream(output), jsonRpcResponse);
            } catch (Exception ex) {
                ex.printStackTrace();
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

}
