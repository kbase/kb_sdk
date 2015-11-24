
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
 * <p>Original spec-file type: AppSpec</p>
 * <pre>
 * typedef structure {
 * } AppBehavior;
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "info",
    "steps"
})
public class AppSpec {

    /**
     * <p>Original spec-file type: AppBriefInfo</p>
     * 
     * 
     */
    @JsonProperty("info")
    private AppBriefInfo info;
    @JsonProperty("steps")
    private List<AppSteps> steps;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * <p>Original spec-file type: AppBriefInfo</p>
     * 
     * 
     */
    @JsonProperty("info")
    public AppBriefInfo getInfo() {
        return info;
    }

    /**
     * <p>Original spec-file type: AppBriefInfo</p>
     * 
     * 
     */
    @JsonProperty("info")
    public void setInfo(AppBriefInfo info) {
        this.info = info;
    }

    public AppSpec withInfo(AppBriefInfo info) {
        this.info = info;
        return this;
    }

    @JsonProperty("steps")
    public List<AppSteps> getSteps() {
        return steps;
    }

    @JsonProperty("steps")
    public void setSteps(List<AppSteps> steps) {
        this.steps = steps;
    }

    public AppSpec withSteps(List<AppSteps> steps) {
        this.steps = steps;
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
        return ((((((("AppSpec"+" [info=")+ info)+", steps=")+ steps)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
