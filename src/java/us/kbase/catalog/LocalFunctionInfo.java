
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
 * <p>Original spec-file type: LocalFunctionInfo</p>
 * <pre>
 * todo: switch release_tag to release_tags
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "function_id",
    "git_commit_hash",
    "version",
    "release_tag",
    "name",
    "short_description",
    "tags"
})
public class LocalFunctionInfo {

    @JsonProperty("module_name")
    private java.lang.String moduleName;
    @JsonProperty("function_id")
    private java.lang.String functionId;
    @JsonProperty("git_commit_hash")
    private java.lang.String gitCommitHash;
    @JsonProperty("version")
    private java.lang.String version;
    @JsonProperty("release_tag")
    private List<String> releaseTag;
    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("short_description")
    private java.lang.String shortDescription;
    /**
     * <p>Original spec-file type: LocalFunctionTags</p>
     * 
     * 
     */
    @JsonProperty("tags")
    private LocalFunctionTags tags;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("module_name")
    public java.lang.String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
    }

    public LocalFunctionInfo withModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("function_id")
    public java.lang.String getFunctionId() {
        return functionId;
    }

    @JsonProperty("function_id")
    public void setFunctionId(java.lang.String functionId) {
        this.functionId = functionId;
    }

    public LocalFunctionInfo withFunctionId(java.lang.String functionId) {
        this.functionId = functionId;
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

    public LocalFunctionInfo withGitCommitHash(java.lang.String gitCommitHash) {
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

    public LocalFunctionInfo withVersion(java.lang.String version) {
        this.version = version;
        return this;
    }

    @JsonProperty("release_tag")
    public List<String> getReleaseTag() {
        return releaseTag;
    }

    @JsonProperty("release_tag")
    public void setReleaseTag(List<String> releaseTag) {
        this.releaseTag = releaseTag;
    }

    public LocalFunctionInfo withReleaseTag(List<String> releaseTag) {
        this.releaseTag = releaseTag;
        return this;
    }

    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public LocalFunctionInfo withName(java.lang.String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("short_description")
    public java.lang.String getShortDescription() {
        return shortDescription;
    }

    @JsonProperty("short_description")
    public void setShortDescription(java.lang.String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalFunctionInfo withShortDescription(java.lang.String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    /**
     * <p>Original spec-file type: LocalFunctionTags</p>
     * 
     * 
     */
    @JsonProperty("tags")
    public LocalFunctionTags getTags() {
        return tags;
    }

    /**
     * <p>Original spec-file type: LocalFunctionTags</p>
     * 
     * 
     */
    @JsonProperty("tags")
    public void setTags(LocalFunctionTags tags) {
        this.tags = tags;
    }

    public LocalFunctionInfo withTags(LocalFunctionTags tags) {
        this.tags = tags;
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
        return ((((((((((((((((((("LocalFunctionInfo"+" [moduleName=")+ moduleName)+", functionId=")+ functionId)+", gitCommitHash=")+ gitCommitHash)+", version=")+ version)+", releaseTag=")+ releaseTag)+", name=")+ name)+", shortDescription=")+ shortDescription)+", tags=")+ tags)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
