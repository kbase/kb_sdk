
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
 * <p>Original spec-file type: RequestedReleaseInfo</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "git_url",
    "git_commit_hash",
    "git_commit_message",
    "timestamp",
    "owners"
})
public class RequestedReleaseInfo {

    @JsonProperty("module_name")
    private java.lang.String moduleName;
    @JsonProperty("git_url")
    private java.lang.String gitUrl;
    @JsonProperty("git_commit_hash")
    private java.lang.String gitCommitHash;
    @JsonProperty("git_commit_message")
    private java.lang.String gitCommitMessage;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("owners")
    private List<String> owners;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("module_name")
    public java.lang.String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
    }

    public RequestedReleaseInfo withModuleName(java.lang.String moduleName) {
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

    public RequestedReleaseInfo withGitUrl(java.lang.String gitUrl) {
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

    public RequestedReleaseInfo withGitCommitHash(java.lang.String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("git_commit_message")
    public java.lang.String getGitCommitMessage() {
        return gitCommitMessage;
    }

    @JsonProperty("git_commit_message")
    public void setGitCommitMessage(java.lang.String gitCommitMessage) {
        this.gitCommitMessage = gitCommitMessage;
    }

    public RequestedReleaseInfo withGitCommitMessage(java.lang.String gitCommitMessage) {
        this.gitCommitMessage = gitCommitMessage;
        return this;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public RequestedReleaseInfo withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public RequestedReleaseInfo withOwners(List<String> owners) {
        this.owners = owners;
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
        return ((((((((((((((("RequestedReleaseInfo"+" [moduleName=")+ moduleName)+", gitUrl=")+ gitUrl)+", gitCommitHash=")+ gitCommitHash)+", gitCommitMessage=")+ gitCommitMessage)+", timestamp=")+ timestamp)+", owners=")+ owners)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
