
package us.kbase.kbasejobservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: RpcContext</p>
 * <pre>
 * call_stack - upstream calls details including nested service calls and 
 *     parent jobs where calls are listed in order from outer to inner.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "call_stack",
    "run_id"
})
public class RpcContext {

    @JsonProperty("call_stack")
    private List<MethodCall> callStack;
    @JsonProperty("run_id")
    private String runId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("call_stack")
    public List<MethodCall> getCallStack() {
        return callStack;
    }

    @JsonProperty("call_stack")
    public void setCallStack(List<MethodCall> callStack) {
        this.callStack = callStack;
    }

    public RpcContext withCallStack(List<MethodCall> callStack) {
        this.callStack = callStack;
        return this;
    }

    @JsonProperty("run_id")
    public String getRunId() {
        return runId;
    }

    @JsonProperty("run_id")
    public void setRunId(String runId) {
        this.runId = runId;
    }

    public RpcContext withRunId(String runId) {
        this.runId = runId;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((("RpcContext"+" [callStack=")+ callStack)+", runId=")+ runId)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
