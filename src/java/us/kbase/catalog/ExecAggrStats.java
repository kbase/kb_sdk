
package us.kbase.catalog;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: ExecAggrStats</p>
 * <pre>
 * full_app_id - optional fully qualified method-spec id including module_name prefix followed
 *     by slash in case of dynamically registered repo (it could be absent or null in case
 *     original execution was started through API call without app ID defined),
 * time_range - one of supported time ranges (currently it could be either '*' for all time
 *     or ISO-encoded week like "2016-W01")
 * total_queue_time - summarized time difference between exec_start_time and creation_time moments
 *     defined in seconds since Epoch (POSIX),
 * total_exec_time - summarized time difference between finish_time and exec_start_time moments 
 *     defined in seconds since Epoch (POSIX).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "full_app_id",
    "time_range",
    "number_of_calls",
    "number_of_errors",
    "total_queue_time",
    "total_exec_time"
})
public class ExecAggrStats {

    @JsonProperty("full_app_id")
    private String fullAppId;
    @JsonProperty("time_range")
    private String timeRange;
    @JsonProperty("number_of_calls")
    private Long numberOfCalls;
    @JsonProperty("number_of_errors")
    private Long numberOfErrors;
    @JsonProperty("total_queue_time")
    private Double totalQueueTime;
    @JsonProperty("total_exec_time")
    private Double totalExecTime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("full_app_id")
    public String getFullAppId() {
        return fullAppId;
    }

    @JsonProperty("full_app_id")
    public void setFullAppId(String fullAppId) {
        this.fullAppId = fullAppId;
    }

    public ExecAggrStats withFullAppId(String fullAppId) {
        this.fullAppId = fullAppId;
        return this;
    }

    @JsonProperty("time_range")
    public String getTimeRange() {
        return timeRange;
    }

    @JsonProperty("time_range")
    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public ExecAggrStats withTimeRange(String timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    @JsonProperty("number_of_calls")
    public Long getNumberOfCalls() {
        return numberOfCalls;
    }

    @JsonProperty("number_of_calls")
    public void setNumberOfCalls(Long numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }

    public ExecAggrStats withNumberOfCalls(Long numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
        return this;
    }

    @JsonProperty("number_of_errors")
    public Long getNumberOfErrors() {
        return numberOfErrors;
    }

    @JsonProperty("number_of_errors")
    public void setNumberOfErrors(Long numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public ExecAggrStats withNumberOfErrors(Long numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
        return this;
    }

    @JsonProperty("total_queue_time")
    public Double getTotalQueueTime() {
        return totalQueueTime;
    }

    @JsonProperty("total_queue_time")
    public void setTotalQueueTime(Double totalQueueTime) {
        this.totalQueueTime = totalQueueTime;
    }

    public ExecAggrStats withTotalQueueTime(Double totalQueueTime) {
        this.totalQueueTime = totalQueueTime;
        return this;
    }

    @JsonProperty("total_exec_time")
    public Double getTotalExecTime() {
        return totalExecTime;
    }

    @JsonProperty("total_exec_time")
    public void setTotalExecTime(Double totalExecTime) {
        this.totalExecTime = totalExecTime;
    }

    public ExecAggrStats withTotalExecTime(Double totalExecTime) {
        this.totalExecTime = totalExecTime;
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
        return ((((((((((((((("ExecAggrStats"+" [fullAppId=")+ fullAppId)+", timeRange=")+ timeRange)+", numberOfCalls=")+ numberOfCalls)+", numberOfErrors=")+ numberOfErrors)+", totalQueueTime=")+ totalQueueTime)+", totalExecTime=")+ totalExecTime)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
