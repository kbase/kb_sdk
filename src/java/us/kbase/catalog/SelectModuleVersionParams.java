
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
 * <p>Original spec-file type: SelectModuleVersionParams</p>
 * <pre>
 * only required: module_name or git_url, the rest are optional selectors
 * If no selectors given, returns current release version
 * version is one of: release | beta | dev
 * old release versions can only be retrieved individually by timestamp or git_commit_hash
 * Note: this method isn't particularly smart or effecient yet, because it pulls the info for a particular
 * module first, then searches in code for matches to the relevant query.  Instead, this should be
 * performed on the database side through queries.  Will optimize when this becomes an issue.
 * In the future, this will be extended so that you can retrieve version info by only
 * timestamp, git commit, etc, but the necessary indicies have not been setup yet.  In general, we will
 * need to add better search capabilities
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "git_url",
    "timestamp",
    "git_commit_hash",
    "version"
})
public class SelectModuleVersionParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("git_url")
    private String gitUrl;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    @JsonProperty("version")
    private String version;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public SelectModuleVersionParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("git_url")
    public String getGitUrl() {
        return gitUrl;
    }

    @JsonProperty("git_url")
    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public SelectModuleVersionParams withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
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

    public SelectModuleVersionParams withTimestamp(Long timestamp) {
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

    public SelectModuleVersionParams withGitCommitHash(String gitCommitHash) {
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

    public SelectModuleVersionParams withVersion(String version) {
        this.version = version;
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
        return ((((((((((((("SelectModuleVersionParams"+" [moduleName=")+ moduleName)+", gitUrl=")+ gitUrl)+", timestamp=")+ timestamp)+", gitCommitHash=")+ gitCommitHash)+", version=")+ version)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
