
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
 * <p>Original spec-file type: BuildLog</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "registration_id",
    "timestamp",
    "module_name_lc",
    "git_url",
    "error",
    "registration",
    "log"
})
public class BuildLog {

    @JsonProperty("registration_id")
    private String registrationId;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("module_name_lc")
    private String moduleNameLc;
    @JsonProperty("git_url")
    private String gitUrl;
    @JsonProperty("error")
    private String error;
    @JsonProperty("registration")
    private String registration;
    @JsonProperty("log")
    private List<BuildLogLine> log;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("registration_id")
    public String getRegistrationId() {
        return registrationId;
    }

    @JsonProperty("registration_id")
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public BuildLog withRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BuildLog withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("module_name_lc")
    public String getModuleNameLc() {
        return moduleNameLc;
    }

    @JsonProperty("module_name_lc")
    public void setModuleNameLc(String moduleNameLc) {
        this.moduleNameLc = moduleNameLc;
    }

    public BuildLog withModuleNameLc(String moduleNameLc) {
        this.moduleNameLc = moduleNameLc;
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

    public BuildLog withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(String error) {
        this.error = error;
    }

    public BuildLog withError(String error) {
        this.error = error;
        return this;
    }

    @JsonProperty("registration")
    public String getRegistration() {
        return registration;
    }

    @JsonProperty("registration")
    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public BuildLog withRegistration(String registration) {
        this.registration = registration;
        return this;
    }

    @JsonProperty("log")
    public List<BuildLogLine> getLog() {
        return log;
    }

    @JsonProperty("log")
    public void setLog(List<BuildLogLine> log) {
        this.log = log;
    }

    public BuildLog withLog(List<BuildLogLine> log) {
        this.log = log;
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
        return ((((((((((((((((("BuildLog"+" [registrationId=")+ registrationId)+", timestamp=")+ timestamp)+", moduleNameLc=")+ moduleNameLc)+", gitUrl=")+ gitUrl)+", error=")+ error)+", registration=")+ registration)+", log=")+ log)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
