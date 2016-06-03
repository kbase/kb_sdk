
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
 * <p>Original spec-file type: IOTags</p>
 * <pre>
 * Local Function Listing Support
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "file_types",
    "kb_types"
})
public class IOTags {

    @JsonProperty("file_types")
    private List<String> fileTypes;
    @JsonProperty("kb_types")
    private List<String> kbTypes;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("file_types")
    public List<String> getFileTypes() {
        return fileTypes;
    }

    @JsonProperty("file_types")
    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public IOTags withFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
        return this;
    }

    @JsonProperty("kb_types")
    public List<String> getKbTypes() {
        return kbTypes;
    }

    @JsonProperty("kb_types")
    public void setKbTypes(List<String> kbTypes) {
        this.kbTypes = kbTypes;
    }

    public IOTags withKbTypes(List<String> kbTypes) {
        this.kbTypes = kbTypes;
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
        return ((((((("IOTags"+" [fileTypes=")+ fileTypes)+", kbTypes=")+ kbTypes)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
