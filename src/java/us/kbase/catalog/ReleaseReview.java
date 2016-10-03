
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
 * <p>Original spec-file type: ReleaseReview</p>
 * <pre>
 * decision - approved | denied
 * review_message -
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "git_url",
    "decision",
    "review_message"
})
public class ReleaseReview {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("git_url")
    private String gitUrl;
    @JsonProperty("decision")
    private String decision;
    @JsonProperty("review_message")
    private String reviewMessage;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ReleaseReview withModuleName(String moduleName) {
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

    public ReleaseReview withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    @JsonProperty("decision")
    public String getDecision() {
        return decision;
    }

    @JsonProperty("decision")
    public void setDecision(String decision) {
        this.decision = decision;
    }

    public ReleaseReview withDecision(String decision) {
        this.decision = decision;
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

    public ReleaseReview withReviewMessage(String reviewMessage) {
        this.reviewMessage = reviewMessage;
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
        return ((((((((((("ReleaseReview"+" [moduleName=")+ moduleName)+", gitUrl=")+ gitUrl)+", decision=")+ decision)+", reviewMessage=")+ reviewMessage)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
