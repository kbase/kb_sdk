
package us.kbase.narrativemethodstore;

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
 * <p>Original spec-file type: Suggestions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "related_methods",
    "next_methods",
    "related_apps",
    "next_apps"
})
public class Suggestions {

    @JsonProperty("related_methods")
    private List<String> relatedMethods;
    @JsonProperty("next_methods")
    private List<String> nextMethods;
    @JsonProperty("related_apps")
    private List<String> relatedApps;
    @JsonProperty("next_apps")
    private List<String> nextApps;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("related_methods")
    public List<String> getRelatedMethods() {
        return relatedMethods;
    }

    @JsonProperty("related_methods")
    public void setRelatedMethods(List<String> relatedMethods) {
        this.relatedMethods = relatedMethods;
    }

    public Suggestions withRelatedMethods(List<String> relatedMethods) {
        this.relatedMethods = relatedMethods;
        return this;
    }

    @JsonProperty("next_methods")
    public List<String> getNextMethods() {
        return nextMethods;
    }

    @JsonProperty("next_methods")
    public void setNextMethods(List<String> nextMethods) {
        this.nextMethods = nextMethods;
    }

    public Suggestions withNextMethods(List<String> nextMethods) {
        this.nextMethods = nextMethods;
        return this;
    }

    @JsonProperty("related_apps")
    public List<String> getRelatedApps() {
        return relatedApps;
    }

    @JsonProperty("related_apps")
    public void setRelatedApps(List<String> relatedApps) {
        this.relatedApps = relatedApps;
    }

    public Suggestions withRelatedApps(List<String> relatedApps) {
        this.relatedApps = relatedApps;
        return this;
    }

    @JsonProperty("next_apps")
    public List<String> getNextApps() {
        return nextApps;
    }

    @JsonProperty("next_apps")
    public void setNextApps(List<String> nextApps) {
        this.nextApps = nextApps;
    }

    public Suggestions withNextApps(List<String> nextApps) {
        this.nextApps = nextApps;
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
        return ((((((((((("Suggestions"+" [relatedMethods=")+ relatedMethods)+", nextMethods=")+ nextMethods)+", relatedApps=")+ relatedApps)+", nextApps=")+ nextApps)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
