
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
 * <p>Original spec-file type: RepoDetails</p>
 * <pre>
 * method_ids - list of method ids (each id is fully qualified, i.e. contains module
 *     name prefix followed by slash);
 * widget_ids - list of widget ids (each id is name of JavaScript file stored in
 *     repo's 'ui/widgets' folder).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "git_url",
    "git_commit_hash",
    "version",
    "module_description",
    "service_language",
    "owners",
    "readme",
    "method_ids",
    "widget_ids"
})
public class RepoDetails {

    @JsonProperty("module_name")
    private java.lang.String moduleName;
    @JsonProperty("git_url")
    private java.lang.String gitUrl;
    @JsonProperty("git_commit_hash")
    private java.lang.String gitCommitHash;
    @JsonProperty("version")
    private java.lang.String version;
    @JsonProperty("module_description")
    private java.lang.String moduleDescription;
    @JsonProperty("service_language")
    private java.lang.String serviceLanguage;
    @JsonProperty("owners")
    private List<String> owners;
    @JsonProperty("readme")
    private java.lang.String readme;
    @JsonProperty("method_ids")
    private List<String> methodIds;
    @JsonProperty("widget_ids")
    private List<String> widgetIds;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("module_name")
    public java.lang.String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
    }

    public RepoDetails withModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("git_url")
    public java.lang.String getGitUrl() {
        return gitUrl;
    }

    @JsonProperty("git_url")
    public void setGitUrl(java.lang.String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public RepoDetails withGitUrl(java.lang.String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    @JsonProperty("git_commit_hash")
    public java.lang.String getGitCommitHash() {
        return gitCommitHash;
    }

    @JsonProperty("git_commit_hash")
    public void setGitCommitHash(java.lang.String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
    }

    public RepoDetails withGitCommitHash(java.lang.String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("version")
    public java.lang.String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    public RepoDetails withVersion(java.lang.String version) {
        this.version = version;
        return this;
    }

    @JsonProperty("module_description")
    public java.lang.String getModuleDescription() {
        return moduleDescription;
    }

    @JsonProperty("module_description")
    public void setModuleDescription(java.lang.String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }

    public RepoDetails withModuleDescription(java.lang.String moduleDescription) {
        this.moduleDescription = moduleDescription;
        return this;
    }

    @JsonProperty("service_language")
    public java.lang.String getServiceLanguage() {
        return serviceLanguage;
    }

    @JsonProperty("service_language")
    public void setServiceLanguage(java.lang.String serviceLanguage) {
        this.serviceLanguage = serviceLanguage;
    }

    public RepoDetails withServiceLanguage(java.lang.String serviceLanguage) {
        this.serviceLanguage = serviceLanguage;
        return this;
    }

    @JsonProperty("owners")
    public List<String> getOwners() {
        return owners;
    }

    @JsonProperty("owners")
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public RepoDetails withOwners(List<String> owners) {
        this.owners = owners;
        return this;
    }

    @JsonProperty("readme")
    public java.lang.String getReadme() {
        return readme;
    }

    @JsonProperty("readme")
    public void setReadme(java.lang.String readme) {
        this.readme = readme;
    }

    public RepoDetails withReadme(java.lang.String readme) {
        this.readme = readme;
        return this;
    }

    @JsonProperty("method_ids")
    public List<String> getMethodIds() {
        return methodIds;
    }

    @JsonProperty("method_ids")
    public void setMethodIds(List<String> methodIds) {
        this.methodIds = methodIds;
    }

    public RepoDetails withMethodIds(List<String> methodIds) {
        this.methodIds = methodIds;
        return this;
    }

    @JsonProperty("widget_ids")
    public List<String> getWidgetIds() {
        return widgetIds;
    }

    @JsonProperty("widget_ids")
    public void setWidgetIds(List<String> widgetIds) {
        this.widgetIds = widgetIds;
    }

    public RepoDetails withWidgetIds(List<String> widgetIds) {
        this.widgetIds = widgetIds;
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
        return ((((((((((((((((((((((("RepoDetails"+" [moduleName=")+ moduleName)+", gitUrl=")+ gitUrl)+", gitCommitHash=")+ gitCommitHash)+", version=")+ version)+", moduleDescription=")+ moduleDescription)+", serviceLanguage=")+ serviceLanguage)+", owners=")+ owners)+", readme=")+ readme)+", methodIds=")+ methodIds)+", widgetIds=")+ widgetIds)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
