
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
 * <p>Original spec-file type: FixedMethodParameter</p>
 * <pre>
 * a fixed parameter that does not appear in the method input forms, but is informational for users in describing
 * a backend parameter that cannot be changed (e.g. if a service picks a fixed parameter for say Blast)
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "ui_name",
    "description"
})
public class FixedMethodParameter {

    @JsonProperty("ui_name")
    private String uiName;
    @JsonProperty("description")
    private String description;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ui_name")
    public String getUiName() {
        return uiName;
    }

    @JsonProperty("ui_name")
    public void setUiName(String uiName) {
        this.uiName = uiName;
    }

    public FixedMethodParameter withUiName(String uiName) {
        this.uiName = uiName;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public FixedMethodParameter withDescription(String description) {
        this.description = description;
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
        return ((((((("FixedMethodParameter"+" [uiName=")+ uiName)+", description=")+ description)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
