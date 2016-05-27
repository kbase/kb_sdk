
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
 * <p>Original spec-file type: BuildLogLine</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "content",
    "error"
})
public class BuildLogLine {

    @JsonProperty("content")
    private String content;
    @JsonProperty("error")
    private Long error;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    public BuildLogLine withContent(String content) {
        this.content = content;
        return this;
    }

    @JsonProperty("error")
    public Long getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(Long error) {
        this.error = error;
    }

    public BuildLogLine withError(Long error) {
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
        return ((((((("BuildLogLine"+" [content=")+ content)+", error=")+ error)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
