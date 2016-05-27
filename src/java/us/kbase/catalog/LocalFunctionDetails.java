
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
 * <p>Original spec-file type: LocalFunctionDetails</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "info",
    "long_description"
})
public class LocalFunctionDetails {

    /**
     * <p>Original spec-file type: LocalFunctionInfo</p>
     * <pre>
     * todo: switch release_tag to release_tags
     * </pre>
     * 
     */
    @JsonProperty("info")
    private LocalFunctionInfo info;
    @JsonProperty("long_description")
    private String longDescription;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * <p>Original spec-file type: LocalFunctionInfo</p>
     * <pre>
     * todo: switch release_tag to release_tags
     * </pre>
     * 
     */
    @JsonProperty("info")
    public LocalFunctionInfo getInfo() {
        return info;
    }

    /**
     * <p>Original spec-file type: LocalFunctionInfo</p>
     * <pre>
     * todo: switch release_tag to release_tags
     * </pre>
     * 
     */
    @JsonProperty("info")
    public void setInfo(LocalFunctionInfo info) {
        this.info = info;
    }

    public LocalFunctionDetails withInfo(LocalFunctionInfo info) {
        this.info = info;
        return this;
    }

    @JsonProperty("long_description")
    public String getLongDescription() {
        return longDescription;
    }

    @JsonProperty("long_description")
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public LocalFunctionDetails withLongDescription(String longDescription) {
        this.longDescription = longDescription;
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
        return ((((((("LocalFunctionDetails"+" [info=")+ info)+", longDescription=")+ longDescription)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
