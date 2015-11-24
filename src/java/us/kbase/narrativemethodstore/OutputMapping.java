
package us.kbase.narrativemethodstore;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.kbase.common.service.UObject;


/**
 * <p>Original spec-file type: OutputMapping</p>
 * <pre>
 * This structure should be used in case narrative method doesn't run any back-end code. 
 * See docs for ServiceMethodOutputMapping type for details.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "input_parameter",
    "constant_value",
    "narrative_system_variable",
    "target_property",
    "target_type_transform"
})
public class OutputMapping {

    @JsonProperty("input_parameter")
    private String inputParameter;
    @JsonProperty("constant_value")
    private UObject constantValue;
    @JsonProperty("narrative_system_variable")
    private String narrativeSystemVariable;
    @JsonProperty("target_property")
    private String targetProperty;
    @JsonProperty("target_type_transform")
    private String targetTypeTransform;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("input_parameter")
    public String getInputParameter() {
        return inputParameter;
    }

    @JsonProperty("input_parameter")
    public void setInputParameter(String inputParameter) {
        this.inputParameter = inputParameter;
    }

    public OutputMapping withInputParameter(String inputParameter) {
        this.inputParameter = inputParameter;
        return this;
    }

    @JsonProperty("constant_value")
    public UObject getConstantValue() {
        return constantValue;
    }

    @JsonProperty("constant_value")
    public void setConstantValue(UObject constantValue) {
        this.constantValue = constantValue;
    }

    public OutputMapping withConstantValue(UObject constantValue) {
        this.constantValue = constantValue;
        return this;
    }

    @JsonProperty("narrative_system_variable")
    public String getNarrativeSystemVariable() {
        return narrativeSystemVariable;
    }

    @JsonProperty("narrative_system_variable")
    public void setNarrativeSystemVariable(String narrativeSystemVariable) {
        this.narrativeSystemVariable = narrativeSystemVariable;
    }

    public OutputMapping withNarrativeSystemVariable(String narrativeSystemVariable) {
        this.narrativeSystemVariable = narrativeSystemVariable;
        return this;
    }

    @JsonProperty("target_property")
    public String getTargetProperty() {
        return targetProperty;
    }

    @JsonProperty("target_property")
    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public OutputMapping withTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
        return this;
    }

    @JsonProperty("target_type_transform")
    public String getTargetTypeTransform() {
        return targetTypeTransform;
    }

    @JsonProperty("target_type_transform")
    public void setTargetTypeTransform(String targetTypeTransform) {
        this.targetTypeTransform = targetTypeTransform;
    }

    public OutputMapping withTargetTypeTransform(String targetTypeTransform) {
        this.targetTypeTransform = targetTypeTransform;
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
        return ((((((((((((("OutputMapping"+" [inputParameter=")+ inputParameter)+", constantValue=")+ constantValue)+", narrativeSystemVariable=")+ narrativeSystemVariable)+", targetProperty=")+ targetProperty)+", targetTypeTransform=")+ targetTypeTransform)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
