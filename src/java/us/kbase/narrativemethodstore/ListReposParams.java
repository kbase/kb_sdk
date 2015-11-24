
package us.kbase.narrativemethodstore;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: ListReposParams</p>
 * <pre>
 * Describes how to filter repositories.
 * with_disabled - optional flag adding disabled repos (default value is false).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "with_disabled"
})
public class ListReposParams {

    @JsonProperty("with_disabled")
    private Long withDisabled;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("with_disabled")
    public Long getWithDisabled() {
        return withDisabled;
    }

    @JsonProperty("with_disabled")
    public void setWithDisabled(Long withDisabled) {
        this.withDisabled = withDisabled;
    }

    public ListReposParams withWithDisabled(Long withDisabled) {
        this.withDisabled = withDisabled;
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
        return ((((("ListReposParams"+" [withDisabled=")+ withDisabled)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
