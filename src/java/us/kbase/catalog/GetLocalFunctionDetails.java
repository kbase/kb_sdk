
package us.kbase.catalog;

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
 * <p>Original spec-file type: GetLocalFunctionDetails</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "functions"
})
public class GetLocalFunctionDetails {

    @JsonProperty("functions")
    private List<SelectOneLocalFunction> functions;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("functions")
    public List<SelectOneLocalFunction> getFunctions() {
        return functions;
    }

    @JsonProperty("functions")
    public void setFunctions(List<SelectOneLocalFunction> functions) {
        this.functions = functions;
    }

    public GetLocalFunctionDetails withFunctions(List<SelectOneLocalFunction> functions) {
        this.functions = functions;
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
        return ((((("GetLocalFunctionDetails"+" [functions=")+ functions)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
