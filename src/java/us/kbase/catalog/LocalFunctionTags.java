
package us.kbase.catalog;

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
 * <p>Original spec-file type: LocalFunctionTags</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "categories",
    "input",
    "output"
})
public class LocalFunctionTags {

    @JsonProperty("categories")
    private List<String> categories;
    /**
     * <p>Original spec-file type: IOTags</p>
     * <pre>
     * Local Function Listing Support
     * </pre>
     * 
     */
    @JsonProperty("input")
    private IOTags input;
    /**
     * <p>Original spec-file type: IOTags</p>
     * <pre>
     * Local Function Listing Support
     * </pre>
     * 
     */
    @JsonProperty("output")
    private IOTags output;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public LocalFunctionTags withCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    /**
     * <p>Original spec-file type: IOTags</p>
     * <pre>
     * Local Function Listing Support
     * </pre>
     * 
     */
    @JsonProperty("input")
    public IOTags getInput() {
        return input;
    }

    /**
     * <p>Original spec-file type: IOTags</p>
     * <pre>
     * Local Function Listing Support
     * </pre>
     * 
     */
    @JsonProperty("input")
    public void setInput(IOTags input) {
        this.input = input;
    }

    public LocalFunctionTags withInput(IOTags input) {
        this.input = input;
        return this;
    }

    /**
     * <p>Original spec-file type: IOTags</p>
     * <pre>
     * Local Function Listing Support
     * </pre>
     * 
     */
    @JsonProperty("output")
    public IOTags getOutput() {
        return output;
    }

    /**
     * <p>Original spec-file type: IOTags</p>
     * <pre>
     * Local Function Listing Support
     * </pre>
     * 
     */
    @JsonProperty("output")
    public void setOutput(IOTags output) {
        this.output = output;
    }

    public LocalFunctionTags withOutput(IOTags output) {
        this.output = output;
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
        return ((((((((("LocalFunctionTags"+" [categories=")+ categories)+", input=")+ input)+", output=")+ output)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
