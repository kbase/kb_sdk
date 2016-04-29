
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
 * <p>Original spec-file type: LogExecStatsParams</p>
 * <pre>
 * user_id - GlobusOnline login of invoker,
 * app_module_name - optional module name of registered repo (could be absent of null for
 *     old fashioned services) where app_id comes from,
 * app_id - optional method-spec id without module_name prefix (could be absent or null
 *     in case original execution was started through API call without app ID defined),
 * func_module_name - optional module name of registered repo (could be absent of null for
 *     old fashioned services) where func_name comes from,
 * func_name - name of function in KIDL-spec without module_name prefix,
 * git_commit_hash - optional service version (in case of dynamically registered repo),
 * creation_time, exec_start_time and finish_time - defined in seconds since Epoch (POSIX),
 * is_error - indicates whether execution was finished with error or not.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "user_id",
    "app_module_name",
    "app_id",
    "func_module_name",
    "func_name",
    "git_commit_hash",
    "creation_time",
    "exec_start_time",
    "finish_time",
    "is_error"
})
public class LogExecStatsParams {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("app_module_name")
    private String appModuleName;
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("func_module_name")
    private String funcModuleName;
    @JsonProperty("func_name")
    private String funcName;
    @JsonProperty("git_commit_hash")
    private String gitCommitHash;
    @JsonProperty("creation_time")
    private Double creationTime;
    @JsonProperty("exec_start_time")
    private Double execStartTime;
    @JsonProperty("finish_time")
    private Double finishTime;
    @JsonProperty("is_error")
    private Long isError;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LogExecStatsParams withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @JsonProperty("app_module_name")
    public String getAppModuleName() {
        return appModuleName;
    }

    @JsonProperty("app_module_name")
    public void setAppModuleName(String appModuleName) {
        this.appModuleName = appModuleName;
    }

    public LogExecStatsParams withAppModuleName(String appModuleName) {
        this.appModuleName = appModuleName;
        return this;
    }

    @JsonProperty("app_id")
    public String getAppId() {
        return appId;
    }

    @JsonProperty("app_id")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    public LogExecStatsParams withAppId(String appId) {
        this.appId = appId;
        return this;
    }

    @JsonProperty("func_module_name")
    public String getFuncModuleName() {
        return funcModuleName;
    }

    @JsonProperty("func_module_name")
    public void setFuncModuleName(String funcModuleName) {
        this.funcModuleName = funcModuleName;
    }

    public LogExecStatsParams withFuncModuleName(String funcModuleName) {
        this.funcModuleName = funcModuleName;
        return this;
    }

    @JsonProperty("func_name")
    public String getFuncName() {
        return funcName;
    }

    @JsonProperty("func_name")
    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public LogExecStatsParams withFuncName(String funcName) {
        this.funcName = funcName;
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

    public LogExecStatsParams withGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("creation_time")
    public Double getCreationTime() {
        return creationTime;
    }

    @JsonProperty("creation_time")
    public void setCreationTime(Double creationTime) {
        this.creationTime = creationTime;
    }

    public LogExecStatsParams withCreationTime(Double creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @JsonProperty("exec_start_time")
    public Double getExecStartTime() {
        return execStartTime;
    }

    @JsonProperty("exec_start_time")
    public void setExecStartTime(Double execStartTime) {
        this.execStartTime = execStartTime;
    }

    public LogExecStatsParams withExecStartTime(Double execStartTime) {
        this.execStartTime = execStartTime;
        return this;
    }

    @JsonProperty("finish_time")
    public Double getFinishTime() {
        return finishTime;
    }

    @JsonProperty("finish_time")
    public void setFinishTime(Double finishTime) {
        this.finishTime = finishTime;
    }

    public LogExecStatsParams withFinishTime(Double finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    @JsonProperty("is_error")
    public Long getIsError() {
        return isError;
    }

    @JsonProperty("is_error")
    public void setIsError(Long isError) {
        this.isError = isError;
    }

    public LogExecStatsParams withIsError(Long isError) {
        this.isError = isError;
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
        return ((((((((((((((((((((((("LogExecStatsParams"+" [userId=")+ userId)+", appModuleName=")+ appModuleName)+", appId=")+ appId)+", funcModuleName=")+ funcModuleName)+", funcName=")+ funcName)+", gitCommitHash=")+ gitCommitHash)+", creationTime=")+ creationTime)+", execStartTime=")+ execStartTime)+", finishTime=")+ finishTime)+", isError=")+ isError)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
