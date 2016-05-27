
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
 * <p>Original spec-file type: HistoryRepoParams</p>
 * <pre>
 * Describes how to find repository details (including old versions). In case neither of
 *     version and git_commit_hash is specified last version is returned.
 * module_name - name of module defined in kbase.yaml file;
 * timestamp - optional parameter limiting search by certain version timestamp;
 * git_commit_hash - optional parameter limiting search by certain git commit hash;
 * with_disabled - optional flag adding disabled repos (default value is false).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "timestamp",
    "git_commit_hash",
    "include_disabled"
})
public class HistoryRepoParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    @JsonProperty("include_disabled")
    private Long includeDisabled;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public HistoryRepoParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public HistoryRepoParams withTimestamp(Long timestamp) {
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

    public HistoryRepoParams withGitCommitHash(String gitCommitHash) {
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

    public HistoryRepoParams withIncludeDisabled(Long includeDisabled) {
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
        return ((((((((((("HistoryRepoParams"+" [moduleName=")+ moduleName)+", timestamp=")+ timestamp)+", gitCommitHash=")+ gitCommitHash)+", includeDisabled=")+ includeDisabled)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
