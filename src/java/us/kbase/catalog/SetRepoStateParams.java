
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
 * <p>Original spec-file type: SetRepoStateParams</p>
 * <pre>
 * Describes how to find repository details.
 * module_name - name of module defined in kbase.yaml file;
 * multiple state fields? (approvalState, buildState, versionState)
 * state - one of 'pending', 'ready', 'building', 'testing', 'disabled'.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "github_repo",
    "registration_state",
    "error_message"
})
public class SetRepoStateParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("github_repo")
    private String githubRepo;
    @JsonProperty("registration_state")
    private String registrationState;
    @JsonProperty("error_message")
    private String errorMessage;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public SetRepoStateParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("github_repo")
    public String getGithubRepo() {
        return githubRepo;
    }

    @JsonProperty("github_repo")
    public void setGithubRepo(String githubRepo) {
        this.githubRepo = githubRepo;
    }

    public SetRepoStateParams withGithubRepo(String githubRepo) {
        this.githubRepo = githubRepo;
        return this;
    }

    @JsonProperty("registration_state")
    public String getRegistrationState() {
        return registrationState;
    }

    @JsonProperty("registration_state")
    public void setRegistrationState(String registrationState) {
        this.registrationState = registrationState;
    }

    public SetRepoStateParams withRegistrationState(String registrationState) {
        this.registrationState = registrationState;
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

    public SetRepoStateParams withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
        return ((((((((((("SetRepoStateParams"+" [moduleName=")+ moduleName)+", githubRepo=")+ githubRepo)+", registrationState=")+ registrationState)+", errorMessage=")+ errorMessage)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
