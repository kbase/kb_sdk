
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
 * <p>Original spec-file type: BasicModuleVersionInfo</p>
 * <pre>
 * DYNAMIC SERVICES SUPPORT Methods
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "version",
    "git_commit_hash",
    "docker_img_name"
})
public class BasicModuleVersionInfo {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("version")
    private String version;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    @JsonProperty("docker_img_name")
    private String dockerImgName;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public BasicModuleVersionInfo withModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public BasicModuleVersionInfo withVersion(String version) {
        this.version = version;
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

    public BasicModuleVersionInfo withGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("docker_img_name")
    public String getDockerImgName() {
        return dockerImgName;
    }

    @JsonProperty("docker_img_name")
    public void setDockerImgName(String dockerImgName) {
        this.dockerImgName = dockerImgName;
    }

    public BasicModuleVersionInfo withDockerImgName(String dockerImgName) {
        this.dockerImgName = dockerImgName;
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
        return ((((((((((("BasicModuleVersionInfo"+" [moduleName=")+ moduleName)+", version=")+ version)+", gitCommitHash=")+ gitCommitHash)+", dockerImgName=")+ dockerImgName)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
