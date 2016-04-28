
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
 * <p>Original spec-file type: UpdateGitUrlParams</p>
 * <pre>
 * all fields are required to make sure you update the right one
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "current_git_url",
    "new_git_url"
})
public class UpdateGitUrlParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("current_git_url")
    private String currentGitUrl;
    @JsonProperty("new_git_url")
    private String newGitUrl;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public UpdateGitUrlParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("current_git_url")
    public String getCurrentGitUrl() {
        return currentGitUrl;
    }

    @JsonProperty("current_git_url")
    public void setCurrentGitUrl(String currentGitUrl) {
        this.currentGitUrl = currentGitUrl;
    }

    public UpdateGitUrlParams withCurrentGitUrl(String currentGitUrl) {
        this.currentGitUrl = currentGitUrl;
        return this;
    }

    @JsonProperty("new_git_url")
    public String getNewGitUrl() {
        return newGitUrl;
    }

    @JsonProperty("new_git_url")
    public void setNewGitUrl(String newGitUrl) {
        this.newGitUrl = newGitUrl;
    }

    public UpdateGitUrlParams withNewGitUrl(String newGitUrl) {
        this.newGitUrl = newGitUrl;
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
        return ((((((((("UpdateGitUrlParams"+" [moduleName=")+ moduleName)+", currentGitUrl=")+ currentGitUrl)+", newGitUrl=")+ newGitUrl)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
