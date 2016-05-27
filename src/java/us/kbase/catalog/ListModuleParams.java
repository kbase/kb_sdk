
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
 * <p>Original spec-file type: ListModuleParams</p>
 * <pre>
 * Describes how to filter repositories.
 * include_released - optional flag indicated modules that are released are included (default:true)
 * include_unreleased - optional flag indicated modules that are not released are included (default:false)
 * with_disabled - optional flag indicating disabled repos should be included (default:false).
 * include_modules_with_no_name_set - default to 0, if set return modules that were never
 *                                     registered successfully (first registration failed, never
 *                                     got a module name, but there is a git_url)
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "owners",
    "include_released",
    "include_unreleased",
    "include_disabled",
    "include_modules_with_no_name_set"
})
public class ListModuleParams {

    @JsonProperty("owners")
    private List<String> owners;
    @JsonProperty("include_released")
    private Long includeReleased;
    @JsonProperty("include_unreleased")
    private Long includeUnreleased;
    @JsonProperty("include_disabled")
    private Long includeDisabled;
    @JsonProperty("include_modules_with_no_name_set")
    private Long includeModulesWithNoNameSet;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("owners")
    public List<String> getOwners() {
        return owners;
    }

    @JsonProperty("owners")
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public ListModuleParams withOwners(List<String> owners) {
        this.owners = owners;
        return this;
    }

    @JsonProperty("include_released")
    public Long getIncludeReleased() {
        return includeReleased;
    }

    @JsonProperty("include_released")
    public void setIncludeReleased(Long includeReleased) {
        this.includeReleased = includeReleased;
    }

    public ListModuleParams withIncludeReleased(Long includeReleased) {
        this.includeReleased = includeReleased;
        return this;
    }

    @JsonProperty("include_unreleased")
    public Long getIncludeUnreleased() {
        return includeUnreleased;
    }

    @JsonProperty("include_unreleased")
    public void setIncludeUnreleased(Long includeUnreleased) {
        this.includeUnreleased = includeUnreleased;
    }

    public ListModuleParams withIncludeUnreleased(Long includeUnreleased) {
        this.includeUnreleased = includeUnreleased;
        return this;
    }

    @JsonProperty("include_disabled")
    public Long getIncludeDisabled() {
        return includeDisabled;
    }

    @JsonProperty("include_disabled")
    public void setIncludeDisabled(Long includeDisabled) {
        this.includeDisabled = includeDisabled;
    }

    public ListModuleParams withIncludeDisabled(Long includeDisabled) {
        this.includeDisabled = includeDisabled;
        return this;
    }

    @JsonProperty("include_modules_with_no_name_set")
    public Long getIncludeModulesWithNoNameSet() {
        return includeModulesWithNoNameSet;
    }

    @JsonProperty("include_modules_with_no_name_set")
    public void setIncludeModulesWithNoNameSet(Long includeModulesWithNoNameSet) {
        this.includeModulesWithNoNameSet = includeModulesWithNoNameSet;
    }

    public ListModuleParams withIncludeModulesWithNoNameSet(Long includeModulesWithNoNameSet) {
        this.includeModulesWithNoNameSet = includeModulesWithNoNameSet;
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
        return ((((((((((((("ListModuleParams"+" [owners=")+ owners)+", includeReleased=")+ includeReleased)+", includeUnreleased=")+ includeUnreleased)+", includeDisabled=")+ includeDisabled)+", includeModulesWithNoNameSet=")+ includeModulesWithNoNameSet)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
