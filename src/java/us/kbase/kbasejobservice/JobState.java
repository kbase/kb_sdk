
package us.kbase.kbasejobservice;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.kbase.common.service.UObject;


/**
 * <p>Original spec-file type: JobState</p>
 * <pre>
 * finished - indicates whether job is done (including error cases) or not,
 *     if the value is true then either of 'returned_data' or 'detailed_error'
 *     should be defined;
 * ujs_url - url of UserAndJobState service used by job service
 * status - tuple returned by UserAndJobState.get_job_status method
 * result - keeps exact copy of what original server method puts
 *     in result block of JSON RPC response;
 * error - keeps exact copy of what original server method puts
 *     in error block of JSON RPC response.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "finished",
    "ujs_url",
    "status",
    "result",
    "error"
})
public class JobState {

    @JsonProperty("finished")
    private Long finished;
    @JsonProperty("ujs_url")
    private String ujsUrl;
    @JsonProperty("status")
    private UObject status;
    @JsonProperty("result")
    private UObject result;
    /**
     * <p>Original spec-file type: JsonRpcError</p>
     * <pre>
     * Error block of JSON RPC response
     * </pre>
     * 
     */
    @JsonProperty("error")
    private JsonRpcError error;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("finished")
    public Long getFinished() {
        return finished;
    }

    @JsonProperty("finished")
    public void setFinished(Long finished) {
        this.finished = finished;
    }

    public JobState withFinished(Long finished) {
        this.finished = finished;
        return this;
    }

    @JsonProperty("ujs_url")
    public String getUjsUrl() {
        return ujsUrl;
    }

    @JsonProperty("ujs_url")
    public void setUjsUrl(String ujsUrl) {
        this.ujsUrl = ujsUrl;
    }

    public JobState withUjsUrl(String ujsUrl) {
        this.ujsUrl = ujsUrl;
        return this;
    }

    @JsonProperty("status")
    public UObject getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(UObject status) {
        this.status = status;
    }

    public JobState withStatus(UObject status) {
        this.status = status;
        return this;
    }

    @JsonProperty("result")
    public UObject getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(UObject result) {
        this.result = result;
    }

    public JobState withResult(UObject result) {
        this.result = result;
        return this;
    }

    /**
     * <p>Original spec-file type: JsonRpcError</p>
     * <pre>
     * Error block of JSON RPC response
     * </pre>
     * 
     */
    @JsonProperty("error")
    public JsonRpcError getError() {
        return error;
    }

    /**
     * <p>Original spec-file type: JsonRpcError</p>
     * <pre>
     * Error block of JSON RPC response
     * </pre>
     * 
     */
    @JsonProperty("error")
    public void setError(JsonRpcError error) {
        this.error = error;
    }

    public JobState withError(JsonRpcError error) {
        this.error = error;
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
        return ((((((((((((("JobState"+" [finished=")+ finished)+", ujsUrl=")+ ujsUrl)+", status=")+ status)+", result=")+ result)+", error=")+ error)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
