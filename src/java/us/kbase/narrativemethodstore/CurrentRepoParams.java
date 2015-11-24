
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
 * <p>Original spec-file type: CurrentRepoParams</p>
 * <pre>
 * *********************************** Registry API *********************************
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "with_disabled"
})
public class CurrentRepoParams {

    @JsonProperty("module_name")
    private String moduleName;
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

    public CurrentRepoParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public CurrentRepoParams withWithDisabled(Long withDisabled) {
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
        return ((((((("CurrentRepoParams"+" [moduleName=")+ moduleName)+", withDisabled=")+ withDisabled)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
