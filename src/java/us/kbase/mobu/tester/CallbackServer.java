package us.kbase.mobu.tester;

import java.io.IOException;
import java.util.Map;

import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.UObject;

public class CallbackServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;
    
    public CallbackServer() {
        super("CallbackServer");
    }
    
    @JsonServerMethod(rpc = "CallbackServer.test")
    public UObject test(UObject input) throws IOException, JsonClientException {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = input.asClassInstance(Map.class);
        data.put("test", "passed");
        return new UObject(data);
    }
}
