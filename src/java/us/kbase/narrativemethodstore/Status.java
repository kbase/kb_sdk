
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
 * <p>Original spec-file type: Status</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "git_spec_url",
    "git_spec_branch",
    "git_spec_commit",
    "update_interval"
})
public class Status {

    @JsonProperty("git_spec_url")
    private String gitSpecUrl;
    @JsonProperty("git_spec_branch")
    private String gitSpecBranch;
    @JsonProperty("git_spec_commit")
    private String gitSpecCommit;
    @JsonProperty("update_interval")
    private String updateInterval;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("git_spec_url")
    public String getGitSpecUrl() {
        return gitSpecUrl;
    }

    @JsonProperty("git_spec_url")
    public void setGitSpecUrl(String gitSpecUrl) {
        this.gitSpecUrl = gitSpecUrl;
    }

    public Status withGitSpecUrl(String gitSpecUrl) {
        this.gitSpecUrl = gitSpecUrl;
        return this;
    }

    @JsonProperty("git_spec_branch")
    public String getGitSpecBranch() {
        return gitSpecBranch;
    }

    @JsonProperty("git_spec_branch")
    public void setGitSpecBranch(String gitSpecBranch) {
        this.gitSpecBranch = gitSpecBranch;
    }

    public Status withGitSpecBranch(String gitSpecBranch) {
        this.gitSpecBranch = gitSpecBranch;
        return this;
    }

    @JsonProperty("git_spec_commit")
    public String getGitSpecCommit() {
        return gitSpecCommit;
    }

    @JsonProperty("git_spec_commit")
    public void setGitSpecCommit(String gitSpecCommit) {
        this.gitSpecCommit = gitSpecCommit;
    }

    public Status withGitSpecCommit(String gitSpecCommit) {
        this.gitSpecCommit = gitSpecCommit;
        return this;
    }

    @JsonProperty("update_interval")
    public String getUpdateInterval() {
        return updateInterval;
    }

    @JsonProperty("update_interval")
    public void setUpdateInterval(String updateInterval) {
        this.updateInterval = updateInterval;
    }

    public Status withUpdateInterval(String updateInterval) {
        this.updateInterval = updateInterval;
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
        return ((((((((((("Status"+" [gitSpecUrl=")+ gitSpecUrl)+", gitSpecBranch=")+ gitSpecBranch)+", gitSpecCommit=")+ gitSpecCommit)+", updateInterval=")+ updateInterval)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
