
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
 * <p>Original spec-file type: RepoVersion</p>
 * <pre>
 * timestamp will be epoch time
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "timestamp",
    "git_commit_hash",
    "include_disabled"
})
public class RepoVersion {

    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    @JsonProperty("include_disabled")
    private Long includeDisabled;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public RepoVersion withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public RepoVersion withGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("include_disabled")
    public Long getIncludeDisabled() {
        return includeDisabled;
    }

    @JsonProperty("include_disabled")
    public void setIncludeDisabled(Long includeDisabled) {
        this.includeDisabled = includeDisabled;
    }

    public RepoVersion withIncludeDisabled(Long includeDisabled) {
        this.includeDisabled = includeDisabled;
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
        return ((((((((("RepoVersion"+" [timestamp=")+ timestamp)+", gitCommitHash=")+ gitCommitHash)+", includeDisabled=")+ includeDisabled)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
