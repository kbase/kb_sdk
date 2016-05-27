
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
    private String moduleName;
    @JsonProperty("function_id")
    private String functionId;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    @JsonProperty("version")
    private String version;
    @JsonProperty("release_tag")
    private String releaseTag;
    @JsonProperty("name")
    private String name;
    @JsonProperty("short_description")
    private String shortDescription;
    /**
     * <p>Original spec-file type: LocalFunctionTags</p>
     * 
     * 
     */
    @JsonProperty("tags")
    private LocalFunctionTags tags;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public LocalFunctionInfo withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("function_id")
    public String getFunctionId() {
        return functionId;
    }

    @JsonProperty("function_id")
    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public LocalFunctionInfo withFunctionId(String functionId) {
        this.functionId = functionId;
        return this;
    }

    @JsonProperty("git_commit_hash")
    public String getGitCommitHash() {
        return gitCommitHash;
    }

    @JsonProperty("git_commit_hash")
    public void setGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
    }

    public LocalFunctionInfo withGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    public LocalFunctionInfo withVersion(String version) {
        this.version = version;
        return this;
    }

    @JsonProperty("release_tag")
    public String getReleaseTag() {
        return releaseTag;
    }

    @JsonProperty("release_tag")
    public void setReleaseTag(String releaseTag) {
        this.releaseTag = releaseTag;
    }

    public LocalFunctionInfo withReleaseTag(String releaseTag) {
        this.releaseTag = releaseTag;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public LocalFunctionInfo withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("short_description")
    public String getShortDescription() {
        return shortDescription;
    }

    @JsonProperty("short_description")
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalFunctionInfo withShortDescription(String shortDescription) {
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
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((((((((((((((("LocalFunctionInfo"+" [moduleName=")+ moduleName)+", functionId=")+ functionId)+", gitCommitHash=")+ gitCommitHash)+", version=")+ version)+", releaseTag=")+ releaseTag)+", name=")+ name)+", shortDescription=")+ shortDescription)+", tags=")+ tags)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
