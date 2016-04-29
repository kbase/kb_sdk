
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
 * <p>Original spec-file type: ModuleVersionLookupParams</p>
 * <pre>
 * module_name - required for module lookup
 * lookup - a lookup string, if empty will get the latest released module
 *             1) version tag = dev | beta | release
 *             2) semantic version match identifiier
 *             not supported yet: 3) exact commit hash
 *             not supported yet: 4) exact timestamp
 * only_service_versions - 1/0, default is 1
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "lookup",
    "only_service_versions"
})
public class ModuleVersionLookupParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("lookup")
    private String lookup;
    @JsonProperty("only_service_versions")
    private Long onlyServiceVersions;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleVersionLookupParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("lookup")
    public String getLookup() {
        return lookup;
    }

    @JsonProperty("lookup")
    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public ModuleVersionLookupParams withLookup(String lookup) {
        this.lookup = lookup;
        return this;
    }

    @JsonProperty("only_service_versions")
    public Long getOnlyServiceVersions() {
        return onlyServiceVersions;
    }

    @JsonProperty("only_service_versions")
    public void setOnlyServiceVersions(Long onlyServiceVersions) {
        this.onlyServiceVersions = onlyServiceVersions;
    }

    public ModuleVersionLookupParams withOnlyServiceVersions(Long onlyServiceVersions) {
        this.onlyServiceVersions = onlyServiceVersions;
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
        return ((((((((("ModuleVersionLookupParams"+" [moduleName=")+ moduleName)+", lookup=")+ lookup)+", onlyServiceVersions=")+ onlyServiceVersions)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
