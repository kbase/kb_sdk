
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
 * <p>Original spec-file type: RadioOptions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id_order",
    "ids_to_options",
    "ids_to_tooltip"
})
public class RadioOptions {

    @JsonProperty("id_order")
    private List<String> idOrder;
    @JsonProperty("ids_to_options")
    private Map<String, String> idsToOptions;
    @JsonProperty("ids_to_tooltip")
    private Map<String, String> idsToTooltip;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("id_order")
    public List<String> getIdOrder() {
        return idOrder;
    }

    @JsonProperty("id_order")
    public void setIdOrder(List<String> idOrder) {
        this.idOrder = idOrder;
    }

    public RadioOptions withIdOrder(List<String> idOrder) {
        this.idOrder = idOrder;
        return this;
    }

    @JsonProperty("ids_to_options")
    public Map<String, String> getIdsToOptions() {
        return idsToOptions;
    }

    @JsonProperty("ids_to_options")
    public void setIdsToOptions(Map<String, String> idsToOptions) {
        this.idsToOptions = idsToOptions;
    }

    public RadioOptions withIdsToOptions(Map<String, String> idsToOptions) {
        this.idsToOptions = idsToOptions;
        return this;
    }

    @JsonProperty("ids_to_tooltip")
    public Map<String, String> getIdsToTooltip() {
        return idsToTooltip;
    }

    @JsonProperty("ids_to_tooltip")
    public void setIdsToTooltip(Map<String, String> idsToTooltip) {
        this.idsToTooltip = idsToTooltip;
    }

    public RadioOptions withIdsToTooltip(Map<String, String> idsToTooltip) {
        this.idsToTooltip = idsToTooltip;
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
        return ((((((((("RadioOptions"+" [idOrder=")+ idOrder)+", idsToOptions=")+ idsToOptions)+", idsToTooltip=")+ idsToTooltip)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
