
package us.kbase.kbasejobservice;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: MethodCall</p>
 * <pre>
 * time - the time the call was started;
 * method - service defined in standard JSON RPC way, typically it's
 *     module name from spec-file followed by '.' and name of funcdef
 *     from spec-file corresponding to running method (e.g.
 *     'KBaseTrees.construct_species_tree' from trees service);
 * job_id - job id if method is asynchronous (optional field).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "time",
    "method",
    "job_id"
})
public class MethodCall {

    @JsonProperty("time")
    private String time;
    @JsonProperty("method")
    private String method;
    @JsonProperty("job_id")
    private String jobId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    public MethodCall withTime(String time) {
        this.time = time;
        return this;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
    }

    public MethodCall withMethod(String method) {
        this.method = method;
        return this;
    }

    @JsonProperty("job_id")
    public String getJobId() {
        return jobId;
    }

    @JsonProperty("job_id")
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public MethodCall withJobId(String jobId) {
        this.jobId = jobId;
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
        return ((((((((("MethodCall"+" [time=")+ time)+", method=")+ method)+", jobId=")+ jobId)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
