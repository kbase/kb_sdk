
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
 * <p>Original spec-file type: TextAreaOptions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "n_rows",
    "placeholder"
})
public class TextAreaOptions {

    @JsonProperty("n_rows")
    private Long nRows;
    @JsonProperty("placeholder")
    private String placeholder;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("n_rows")
    public Long getNRows() {
        return nRows;
    }

    @JsonProperty("n_rows")
    public void setNRows(Long nRows) {
        this.nRows = nRows;
    }

    public TextAreaOptions withNRows(Long nRows) {
        this.nRows = nRows;
        return this;
    }

    @JsonProperty("placeholder")
    public String getPlaceholder() {
        return placeholder;
    }

    @JsonProperty("placeholder")
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public TextAreaOptions withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
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
        return ((((((("TextAreaOptions"+" [nRows=")+ nRows)+", placeholder=")+ placeholder)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
