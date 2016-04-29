
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
 * <p>Original spec-file type: FunctionPlace</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "start_line",
    "end_line"
})
public class FunctionPlace {

    @JsonProperty("start_line")
    private Long startLine;
    @JsonProperty("end_line")
    private Long endLine;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("start_line")
    public Long getStartLine() {
        return startLine;
    }

    @JsonProperty("start_line")
    public void setStartLine(Long startLine) {
        this.startLine = startLine;
    }

    public FunctionPlace withStartLine(Long startLine) {
        this.startLine = startLine;
        return this;
    }

    @JsonProperty("end_line")
    public Long getEndLine() {
        return endLine;
    }

    @JsonProperty("end_line")
    public void setEndLine(Long endLine) {
        this.endLine = endLine;
    }

    public FunctionPlace withEndLine(Long endLine) {
        this.endLine = endLine;
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
        return ((((((("FunctionPlace"+" [startLine=")+ startLine)+", endLine=")+ endLine)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
