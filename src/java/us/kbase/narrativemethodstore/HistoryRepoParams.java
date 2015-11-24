
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
 * <p>Original spec-file type: HistoryRepoParams</p>
 * <pre>
 * Describes how to find repository details (including old versions).
 * module_name - name of module defined in kbase.yaml file;
 * version - optional parameter limiting search by certain version timestamp;
 * with_disabled - optional flag adding disabled repos (default value is false).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "version",
    "with_disabled"
})
public class HistoryRepoParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("version")
    private Long version;
    @JsonProperty("with_disabled")
    private Long withDisabled;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public HistoryRepoParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("version")
    public Long getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Long version) {
        this.version = version;
    }

    public HistoryRepoParams withVersion(Long version) {
        this.version = version;
        return this;
    }

    @JsonProperty("with_disabled")
    public Long getWithDisabled() {
        return withDisabled;
    }

    @JsonProperty("with_disabled")
    public void setWithDisabled(Long withDisabled) {
        this.withDisabled = withDisabled;
    }

    public HistoryRepoParams withWithDisabled(Long withDisabled) {
        this.withDisabled = withDisabled;
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
        return ((((((((("HistoryRepoParams"+" [moduleName=")+ moduleName)+", version=")+ version)+", withDisabled=")+ withDisabled)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
