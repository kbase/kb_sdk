
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
 * <p>Original spec-file type: TextOptions</p>
 * <pre>
 * valid_ws_types  - list of valid ws types that can be used for input
 * validate_as     - int | float | nonnumeric | none
 * is_output_name  - true if the user is specifying an output name, false otherwise, default is false
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "valid_ws_types",
    "validate_as",
    "is_output_name",
    "placeholder",
    "min_int",
    "max_int",
    "min_float",
    "max_float",
    "regex_constraint"
})
public class TextOptions {

    @JsonProperty("valid_ws_types")
    private List<String> validWsTypes;
    @JsonProperty("validate_as")
    private java.lang.String validateAs;
    @JsonProperty("is_output_name")
    private Long isOutputName;
    @JsonProperty("placeholder")
    private java.lang.String placeholder;
    @JsonProperty("min_int")
    private Long minInt;
    @JsonProperty("max_int")
    private Long maxInt;
    @JsonProperty("min_float")
    private Double minFloat;
    @JsonProperty("max_float")
    private Double maxFloat;
    @JsonProperty("regex_constraint")
    private List<RegexMatcher> regexConstraint;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("valid_ws_types")
    public List<String> getValidWsTypes() {
        return validWsTypes;
    }

    @JsonProperty("valid_ws_types")
    public void setValidWsTypes(List<String> validWsTypes) {
        this.validWsTypes = validWsTypes;
    }

    public TextOptions withValidWsTypes(List<String> validWsTypes) {
        this.validWsTypes = validWsTypes;
        return this;
    }

    @JsonProperty("validate_as")
    public java.lang.String getValidateAs() {
        return validateAs;
    }

    @JsonProperty("validate_as")
    public void setValidateAs(java.lang.String validateAs) {
        this.validateAs = validateAs;
    }

    public TextOptions withValidateAs(java.lang.String validateAs) {
        this.validateAs = validateAs;
        return this;
    }

    @JsonProperty("is_output_name")
    public Long getIsOutputName() {
        return isOutputName;
    }

    @JsonProperty("is_output_name")
    public void setIsOutputName(Long isOutputName) {
        this.isOutputName = isOutputName;
    }

    public TextOptions withIsOutputName(Long isOutputName) {
        this.isOutputName = isOutputName;
        return this;
    }

    @JsonProperty("placeholder")
    public java.lang.String getPlaceholder() {
        return placeholder;
    }

    @JsonProperty("placeholder")
    public void setPlaceholder(java.lang.String placeholder) {
        this.placeholder = placeholder;
    }

    public TextOptions withPlaceholder(java.lang.String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @JsonProperty("min_int")
    public Long getMinInt() {
        return minInt;
    }

    @JsonProperty("min_int")
    public void setMinInt(Long minInt) {
        this.minInt = minInt;
    }

    public TextOptions withMinInt(Long minInt) {
        this.minInt = minInt;
        return this;
    }

    @JsonProperty("max_int")
    public Long getMaxInt() {
        return maxInt;
    }

    @JsonProperty("max_int")
    public void setMaxInt(Long maxInt) {
        this.maxInt = maxInt;
    }

    public TextOptions withMaxInt(Long maxInt) {
        this.maxInt = maxInt;
        return this;
    }

    @JsonProperty("min_float")
    public Double getMinFloat() {
        return minFloat;
    }

    @JsonProperty("min_float")
    public void setMinFloat(Double minFloat) {
        this.minFloat = minFloat;
    }

    public TextOptions withMinFloat(Double minFloat) {
        this.minFloat = minFloat;
        return this;
    }

    @JsonProperty("max_float")
    public Double getMaxFloat() {
        return maxFloat;
    }

    @JsonProperty("max_float")
    public void setMaxFloat(Double maxFloat) {
        this.maxFloat = maxFloat;
    }

    public TextOptions withMaxFloat(Double maxFloat) {
        this.maxFloat = maxFloat;
        return this;
    }

    @JsonProperty("regex_constraint")
    public List<RegexMatcher> getRegexConstraint() {
        return regexConstraint;
    }

    @JsonProperty("regex_constraint")
    public void setRegexConstraint(List<RegexMatcher> regexConstraint) {
        this.regexConstraint = regexConstraint;
    }

    public TextOptions withRegexConstraint(List<RegexMatcher> regexConstraint) {
        this.regexConstraint = regexConstraint;
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
        return ((((((((((((((((((((("TextOptions"+" [validWsTypes=")+ validWsTypes)+", validateAs=")+ validateAs)+", isOutputName=")+ isOutputName)+", placeholder=")+ placeholder)+", minInt=")+ minInt)+", maxInt=")+ maxInt)+", minFloat=")+ minFloat)+", maxFloat=")+ maxFloat)+", regexConstraint=")+ regexConstraint)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
