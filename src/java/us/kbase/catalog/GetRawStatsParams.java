
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
 * <p>Original spec-file type: GetRawStatsParams</p>
 * <pre>
 * Get raw usage metrics; available only to Admins.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "begin",
    "end"
})
public class GetRawStatsParams {

    @JsonProperty("begin")
    private Long begin;
    @JsonProperty("end")
    private Long end;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("begin")
    public Long getBegin() {
        return begin;
    }

    @JsonProperty("begin")
    public void setBegin(Long begin) {
        this.begin = begin;
    }

    public GetRawStatsParams withBegin(Long begin) {
        this.begin = begin;
        return this;
    }

    @JsonProperty("end")
    public Long getEnd() {
        return end;
    }

    @JsonProperty("end")
    public void setEnd(Long end) {
        this.end = end;
    }

    public GetRawStatsParams withEnd(Long end) {
        this.end = end;
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
        return ((((((("GetRawStatsParams"+" [begin=")+ begin)+", end=")+ end)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
