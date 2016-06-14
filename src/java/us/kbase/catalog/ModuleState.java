
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
 * <p>Original spec-file type: ModuleState</p>
 * <pre>
 * active: True | False,
 * release_approval: approved | denied | under_review | not_requested, (all releases require approval)
 * review_message: str, (optional)
 * registration: complete | error | (build state status),
 * error_message: str (optional)
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "active",
    "released",
    "release_approval",
    "review_message",
    "registration",
    "error_message"
})
public class ModuleState {

    @JsonProperty("active")
    private Long active;
    @JsonProperty("released")
    private Long released;
    @JsonProperty("release_approval")
    private String releaseApproval;
    @JsonProperty("review_message")
    private String reviewMessage;
    @JsonProperty("registration")
    private String registration;
    @JsonProperty("error_message")
    private String errorMessage;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("active")
    public Long getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Long active) {
        this.active = active;
    }

    public ModuleState withActive(Long active) {
        this.active = active;
        return this;
    }

    @JsonProperty("released")
    public Long getReleased() {
        return released;
    }

    @JsonProperty("released")
    public void setReleased(Long released) {
        this.released = released;
    }

    public ModuleState withReleased(Long released) {
        this.released = released;
        return this;
    }

    @JsonProperty("release_approval")
    public String getReleaseApproval() {
        return releaseApproval;
    }

    @JsonProperty("release_approval")
    public void setReleaseApproval(String releaseApproval) {
        this.releaseApproval = releaseApproval;
    }

    public ModuleState withReleaseApproval(String releaseApproval) {
        this.releaseApproval = releaseApproval;
        return this;
    }

    @JsonProperty("review_message")
    public String getReviewMessage() {
        return reviewMessage;
    }

    @JsonProperty("review_message")
    public void setReviewMessage(String reviewMessage) {
        this.reviewMessage = reviewMessage;
    }

    public ModuleState withReviewMessage(String reviewMessage) {
        this.reviewMessage = reviewMessage;
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

    public ModuleState withRegistration(String registration) {
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

    public ModuleState withErrorMessage(String errorMessage) {
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
        return ((((((((((((((("ModuleState"+" [active=")+ active)+", released=")+ released)+", releaseApproval=")+ releaseApproval)+", reviewMessage=")+ reviewMessage)+", registration=")+ registration)+", errorMessage=")+ errorMessage)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
