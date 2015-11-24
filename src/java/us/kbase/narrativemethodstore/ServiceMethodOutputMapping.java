
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
import us.kbase.common.service.UObject;


/**
 * <p>Original spec-file type: ServiceMethodOutputMapping</p>
 * <pre>
 * input_parameter - parameter_id, if not specified then one of 'constant_value' or 
 *     'narrative_system_variable' should be set.
 * service_method_output_path - list of properties and array element positions defining JSON-path traversing
 *     through which we can find necessary value. 
 * constant_value - constant value, could be even map/array, if not specified then 'input_parameter' or
 *     'narrative_system_variable' should be set.
 * narrative_system_variable - name of internal narrative framework property, currently only these names are
 *     supported: 'workspace', 'token', 'user_id'; if not specified then one of 'input_parameter' or
 *     'constant_value' should be set.
 * target_property - name of field inside structure that will be send as arguement. Optional field,
 *     in case this field is not defined (or null) whole object will be sent as method argument instead of
 *     wrapping it by structure with inner property defined by 'target_property'.
 * target_type_transform - none/string/int/float/list<type>/mapping<type>/ref, optional field, default is 
 *     no transformation.
 * @optional input_parameter service_method_output_path constant_value narrative_system_variable 
 * @optional target_property target_type_transform
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "input_parameter",
    "service_method_output_path",
    "constant_value",
    "narrative_system_variable",
    "target_property",
    "target_type_transform"
})
public class ServiceMethodOutputMapping {

    @JsonProperty("input_parameter")
    private java.lang.String inputParameter;
    @JsonProperty("service_method_output_path")
    private List<String> serviceMethodOutputPath;
    @JsonProperty("constant_value")
    private UObject constantValue;
    @JsonProperty("narrative_system_variable")
    private java.lang.String narrativeSystemVariable;
    @JsonProperty("target_property")
    private java.lang.String targetProperty;
    @JsonProperty("target_type_transform")
    private java.lang.String targetTypeTransform;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("input_parameter")
    public java.lang.String getInputParameter() {
        return inputParameter;
    }

    @JsonProperty("input_parameter")
    public void setInputParameter(java.lang.String inputParameter) {
        this.inputParameter = inputParameter;
    }

    public ServiceMethodOutputMapping withInputParameter(java.lang.String inputParameter) {
        this.inputParameter = inputParameter;
        return this;
    }

    @JsonProperty("service_method_output_path")
    public List<String> getServiceMethodOutputPath() {
        return serviceMethodOutputPath;
    }

    @JsonProperty("service_method_output_path")
    public void setServiceMethodOutputPath(List<String> serviceMethodOutputPath) {
        this.serviceMethodOutputPath = serviceMethodOutputPath;
    }

    public ServiceMethodOutputMapping withServiceMethodOutputPath(List<String> serviceMethodOutputPath) {
        this.serviceMethodOutputPath = serviceMethodOutputPath;
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

    public ServiceMethodOutputMapping withConstantValue(UObject constantValue) {
        this.constantValue = constantValue;
        return this;
    }

    @JsonProperty("narrative_system_variable")
    public java.lang.String getNarrativeSystemVariable() {
        return narrativeSystemVariable;
    }

    @JsonProperty("narrative_system_variable")
    public void setNarrativeSystemVariable(java.lang.String narrativeSystemVariable) {
        this.narrativeSystemVariable = narrativeSystemVariable;
    }

    public ServiceMethodOutputMapping withNarrativeSystemVariable(java.lang.String narrativeSystemVariable) {
        this.narrativeSystemVariable = narrativeSystemVariable;
        return this;
    }

    @JsonProperty("target_property")
    public java.lang.String getTargetProperty() {
        return targetProperty;
    }

    @JsonProperty("target_property")
    public void setTargetProperty(java.lang.String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public ServiceMethodOutputMapping withTargetProperty(java.lang.String targetProperty) {
        this.targetProperty = targetProperty;
        return this;
    }

    @JsonProperty("target_type_transform")
    public java.lang.String getTargetTypeTransform() {
        return targetTypeTransform;
    }

    @JsonProperty("target_type_transform")
    public void setTargetTypeTransform(java.lang.String targetTypeTransform) {
        this.targetTypeTransform = targetTypeTransform;
    }

    public ServiceMethodOutputMapping withTargetTypeTransform(java.lang.String targetTypeTransform) {
        this.targetTypeTransform = targetTypeTransform;
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
        return ((((((((((((((("ServiceMethodOutputMapping"+" [inputParameter=")+ inputParameter)+", serviceMethodOutputPath=")+ serviceMethodOutputPath)+", constantValue=")+ constantValue)+", narrativeSystemVariable=")+ narrativeSystemVariable)+", targetProperty=")+ targetProperty)+", targetTypeTransform=")+ targetTypeTransform)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
