
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
 * <p>Original spec-file type: GetMethodParams</p>
 * <pre>
 * tag - optional access level for dynamic repos (one of 'dev', 'beta' or 'release').
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "ids",
    "tag"
})
public class GetMethodParams {

    @JsonProperty("ids")
    private List<String> ids;
    @JsonProperty("tag")
    private java.lang.String tag;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("ids")
    public List<String> getIds() {
        return ids;
    }

    @JsonProperty("ids")
    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public GetMethodParams withIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    @JsonProperty("tag")
    public java.lang.String getTag() {
        return tag;
    }

    @JsonProperty("tag")
    public void setTag(java.lang.String tag) {
        this.tag = tag;
    }

    public GetMethodParams withTag(java.lang.String tag) {
        this.tag = tag;
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
        return ((((((("GetMethodParams"+" [ids=")+ ids)+", tag=")+ tag)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
