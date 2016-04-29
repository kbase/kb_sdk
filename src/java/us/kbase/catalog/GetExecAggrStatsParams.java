
package us.kbase.catalog;

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
 * <p>Original spec-file type: GetExecAggrStatsParams</p>
 * <pre>
 * full_app_ids - list of fully qualified app IDs (including module_name prefix followed by
 *     slash in case of dynamically registered repo).
 * per_week - optional flag switching results to weekly data rather than one row per app for 
 *     all time (default value is false)
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "full_app_ids",
    "per_week"
})
public class GetExecAggrStatsParams {

    @JsonProperty("full_app_ids")
    private List<String> fullAppIds;
    @JsonProperty("per_week")
    private Long perWeek;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("full_app_ids")
    public List<String> getFullAppIds() {
        return fullAppIds;
    }

    @JsonProperty("full_app_ids")
    public void setFullAppIds(List<String> fullAppIds) {
        this.fullAppIds = fullAppIds;
    }

    public GetExecAggrStatsParams withFullAppIds(List<String> fullAppIds) {
        this.fullAppIds = fullAppIds;
        return this;
    }

    @JsonProperty("per_week")
    public Long getPerWeek() {
        return perWeek;
    }

    @JsonProperty("per_week")
    public void setPerWeek(Long perWeek) {
        this.perWeek = perWeek;
    }

    public GetExecAggrStatsParams withPerWeek(Long perWeek) {
        this.perWeek = perWeek;
        return this;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public java.lang.String toString() {
        return ((((((("GetExecAggrStatsParams"+" [fullAppIds=")+ fullAppIds)+", perWeek=")+ perWeek)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
