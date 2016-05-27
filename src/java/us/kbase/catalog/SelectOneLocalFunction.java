
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
 * <p>Original spec-file type: SelectOneLocalFunction</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "function_id",
    "release_tag",
    "git_commit_hash"
})
public class SelectOneLocalFunction {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("function_id")
    private String functionId;
    @JsonProperty("release_tag")
    private String releaseTag;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public SelectOneLocalFunction withModuleName(String moduleName) {
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

    public SelectOneLocalFunction withFunctionId(String functionId) {
        this.functionId = functionId;
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

    public SelectOneLocalFunction withReleaseTag(String releaseTag) {
        this.releaseTag = releaseTag;
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

    public SelectOneLocalFunction withGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
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
        return ((((((((((("SelectOneLocalFunction"+" [moduleName=")+ moduleName)+", functionId=")+ functionId)+", releaseTag=")+ releaseTag)+", gitCommitHash=")+ gitCommitHash)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
