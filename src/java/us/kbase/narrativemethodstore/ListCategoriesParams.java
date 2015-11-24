
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
 * <p>Original spec-file type: ListCategoriesParams</p>
 * <pre>
 * List all the categories.  Optionally, if load_methods or load_apps are set to 1,
 * information about all the methods and apps is provided.  This is important
 * load_methods - optional field (default value is 1).
 * tag - optional access level for dynamic repos (one of 'dev', 'beta' or 'release').
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "load_methods",
    "load_apps",
    "load_types",
    "tag"
})
public class ListCategoriesParams {

    @JsonProperty("load_methods")
    private Long loadMethods;
    @JsonProperty("load_apps")
    private Long loadApps;
    @JsonProperty("load_types")
    private Long loadTypes;
    @JsonProperty("tag")
    private String tag;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("load_methods")
    public Long getLoadMethods() {
        return loadMethods;
    }

    @JsonProperty("load_methods")
    public void setLoadMethods(Long loadMethods) {
        this.loadMethods = loadMethods;
    }

    public ListCategoriesParams withLoadMethods(Long loadMethods) {
        this.loadMethods = loadMethods;
        return this;
    }

    @JsonProperty("load_apps")
    public Long getLoadApps() {
        return loadApps;
    }

    @JsonProperty("load_apps")
    public void setLoadApps(Long loadApps) {
        this.loadApps = loadApps;
    }

    public ListCategoriesParams withLoadApps(Long loadApps) {
        this.loadApps = loadApps;
        return this;
    }

    @JsonProperty("load_types")
    public Long getLoadTypes() {
        return loadTypes;
    }

    @JsonProperty("load_types")
    public void setLoadTypes(Long loadTypes) {
        this.loadTypes = loadTypes;
    }

    public ListCategoriesParams withLoadTypes(Long loadTypes) {
        this.loadTypes = loadTypes;
        return this;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }

    public ListCategoriesParams withTag(String tag) {
        this.tag = tag;
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
        return ((((((((((("ListCategoriesParams"+" [loadMethods=")+ loadMethods)+", loadApps=")+ loadApps)+", loadTypes=")+ loadTypes)+", tag=")+ tag)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
