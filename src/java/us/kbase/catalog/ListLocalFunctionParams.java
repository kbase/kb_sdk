
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
 * <p>Original spec-file type: ListLocalFunctionParams</p>
 * <pre>
 * Allows various ways to filter.
 * Release tag = dev/beta/release
 * module_names = only include modules in the list
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "release_tag",
    "module_names"
})
public class ListLocalFunctionParams {

    @JsonProperty("release_tag")
    private java.lang.String releaseTag;
    @JsonProperty("module_names")
    private List<String> moduleNames;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("release_tag")
    public java.lang.String getReleaseTag() {
        return releaseTag;
    }

    @JsonProperty("release_tag")
    public void setReleaseTag(java.lang.String releaseTag) {
        this.releaseTag = releaseTag;
    }

    public ListLocalFunctionParams withReleaseTag(java.lang.String releaseTag) {
        this.releaseTag = releaseTag;
        return this;
    }

    @JsonProperty("module_names")
    public List<String> getModuleNames() {
        return moduleNames;
    }

    @JsonProperty("module_names")
    public void setModuleNames(List<String> moduleNames) {
        this.moduleNames = moduleNames;
    }

    public ListLocalFunctionParams withModuleNames(List<String> moduleNames) {
        this.moduleNames = moduleNames;
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
        return ((((((("ListLocalFunctionParams"+" [releaseTag=")+ releaseTag)+", moduleNames=")+ moduleNames)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
