
package us.kbase.narrativemethodstore;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: AppStepInputMapping</p>
 * <pre>
 * Defines how any input to a particular step should be
 * populated based 
 * step_source - the id of the step to pull the parameter from
 * isFromInput - set to true (1) to indicate that the input should be pulled from the input
 *     parameters of the step_source.  This is the only supported option.  In the future, it
 *     may be possible to pull the input from the output of the previous step (which would
 *     require special handling of the app runner).
 * from - the id of the input parameter/output field in step_source to retrieve the value
 * to - the name of the parameter to automatically populate in this step
 * transformation - not supported yet, but may be used to indicate if a transformation of the
 *     value should occur when mapping the input to this step
 * //@optional transformation
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "step_source",
    "is_from_input",
    "from",
    "to"
})
public class AppStepInputMapping {

    @JsonProperty("step_source")
    private String stepSource;
    @JsonProperty("is_from_input")
    private Long isFromInput;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("step_source")
    public String getStepSource() {
        return stepSource;
    }

    @JsonProperty("step_source")
    public void setStepSource(String stepSource) {
        this.stepSource = stepSource;
    }

    public AppStepInputMapping withStepSource(String stepSource) {
        this.stepSource = stepSource;
        return this;
    }

    @JsonProperty("is_from_input")
    public Long getIsFromInput() {
        return isFromInput;
    }

    @JsonProperty("is_from_input")
    public void setIsFromInput(Long isFromInput) {
        this.isFromInput = isFromInput;
    }

    public AppStepInputMapping withIsFromInput(Long isFromInput) {
        this.isFromInput = isFromInput;
        return this;
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    public AppStepInputMapping withFrom(String from) {
        this.from = from;
        return this;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
    }

    public AppStepInputMapping withTo(String to) {
        this.to = to;
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
        return ((((((((((("AppStepInputMapping"+" [stepSource=")+ stepSource)+", isFromInput=")+ isFromInput)+", from=")+ from)+", to=")+ to)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
