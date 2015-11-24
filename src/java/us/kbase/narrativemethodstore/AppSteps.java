
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
 * <p>Original spec-file type: AppSteps</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "step_id",
    "method_id",
    "input_mapping",
    "description"
})
public class AppSteps {

    @JsonProperty("step_id")
    private String stepId;
    @JsonProperty("method_id")
    private String methodId;
    @JsonProperty("input_mapping")
    private List<AppStepInputMapping> inputMapping;
    @JsonProperty("description")
    private String description;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("step_id")
    public String getStepId() {
        return stepId;
    }

    @JsonProperty("step_id")
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public AppSteps withStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }

    @JsonProperty("method_id")
    public String getMethodId() {
        return methodId;
    }

    @JsonProperty("method_id")
    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public AppSteps withMethodId(String methodId) {
        this.methodId = methodId;
        return this;
    }

    @JsonProperty("input_mapping")
    public List<AppStepInputMapping> getInputMapping() {
        return inputMapping;
    }

    @JsonProperty("input_mapping")
    public void setInputMapping(List<AppStepInputMapping> inputMapping) {
        this.inputMapping = inputMapping;
    }

    public AppSteps withInputMapping(List<AppStepInputMapping> inputMapping) {
        this.inputMapping = inputMapping;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public AppSteps withDescription(String description) {
        this.description = description;
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
        return ((((((((((("AppSteps"+" [stepId=")+ stepId)+", methodId=")+ methodId)+", inputMapping=")+ inputMapping)+", description=")+ description)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
