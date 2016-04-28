
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
 * <p>Original spec-file type: BuildInfo</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "timestamp",
    "registration_id",
    "registration",
    "error_message",
    "module_name_lc",
    "git_url"
})
public class BuildInfo {

    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("registration_id")
    private String registrationId;
    @JsonProperty("registration")
    private String registration;
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("module_name_lc")
    private String moduleNameLc;
    @JsonProperty("git_url")
    private String gitUrl;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BuildInfo withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("registration_id")
    public String getRegistrationId() {
        return registrationId;
    }

    @JsonProperty("registration_id")
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public BuildInfo withRegistrationId(String registrationId) {
        this.registrationId = registrationId;
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

    public BuildInfo withRegistration(String registration) {
        this.registration = registration;
        return this;
    }

    @JsonProperty("error_message")
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty("error_message")
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BuildInfo withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public BuildInfo withModuleNameLc(String moduleNameLc) {
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

    public BuildInfo withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
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
        return ((((((((((((((("BuildInfo"+" [timestamp=")+ timestamp)+", registrationId=")+ registrationId)+", registration=")+ registration)+", errorMessage=")+ errorMessage)+", moduleNameLc=")+ moduleNameLc)+", gitUrl=")+ gitUrl)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
