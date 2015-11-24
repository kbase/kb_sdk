
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
 * <p>Original spec-file type: CheckboxOptions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "checked_value",
    "unchecked_value"
})
public class CheckboxOptions {

    @JsonProperty("checked_value")
    private Long checkedValue;
    @JsonProperty("unchecked_value")
    private Long uncheckedValue;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("checked_value")
    public Long getCheckedValue() {
        return checkedValue;
    }

    @JsonProperty("checked_value")
    public void setCheckedValue(Long checkedValue) {
        this.checkedValue = checkedValue;
    }

    public CheckboxOptions withCheckedValue(Long checkedValue) {
        this.checkedValue = checkedValue;
        return this;
    }

    @JsonProperty("unchecked_value")
    public Long getUncheckedValue() {
        return uncheckedValue;
    }

    @JsonProperty("unchecked_value")
    public void setUncheckedValue(Long uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
    }

    public CheckboxOptions withUncheckedValue(Long uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
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
        return ((((((("CheckboxOptions"+" [checkedValue=")+ checkedValue)+", uncheckedValue=")+ uncheckedValue)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
