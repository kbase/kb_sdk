
package us.kbase.narrativemethodstore;

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
 * <p>Original spec-file type: DropdownOptions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "options"
})
public class DropdownOptions {

    @JsonProperty("options")
    private List<DropdownOption> options;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("options")
    public List<DropdownOption> getOptions() {
        return options;
    }

    @JsonProperty("options")
    public void setOptions(List<DropdownOption> options) {
        this.options = options;
    }

    public DropdownOptions withOptions(List<DropdownOption> options) {
        this.options = options;
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
        return ((((("DropdownOptions"+" [options=")+ options)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
