
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
 * <p>Original spec-file type: GetTypeParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "type_names"
})
public class GetTypeParams {

    @JsonProperty("type_names")
    private List<String> typeNames;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("type_names")
    public List<String> getTypeNames() {
        return typeNames;
    }

    @JsonProperty("type_names")
    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public GetTypeParams withTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
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
        return ((((("GetTypeParams"+" [typeNames=")+ typeNames)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
