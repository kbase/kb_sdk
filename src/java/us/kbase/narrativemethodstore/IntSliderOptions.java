
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
 * <p>Original spec-file type: IntSliderOptions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "min",
    "max",
    "step"
})
public class IntSliderOptions {

    @JsonProperty("min")
    private Long min;
    @JsonProperty("max")
    private Long max;
    @JsonProperty("step")
    private Long step;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("min")
    public Long getMin() {
        return min;
    }

    @JsonProperty("min")
    public void setMin(Long min) {
        this.min = min;
    }

    public IntSliderOptions withMin(Long min) {
        this.min = min;
        return this;
    }

    @JsonProperty("max")
    public Long getMax() {
        return max;
    }

    @JsonProperty("max")
    public void setMax(Long max) {
        this.max = max;
    }

    public IntSliderOptions withMax(Long max) {
        this.max = max;
        return this;
    }

    @JsonProperty("step")
    public Long getStep() {
        return step;
    }

    @JsonProperty("step")
    public void setStep(Long step) {
        this.step = step;
    }

    public IntSliderOptions withStep(Long step) {
        this.step = step;
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
        return ((((((((("IntSliderOptions"+" [min=")+ min)+", max=")+ max)+", step=")+ step)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
