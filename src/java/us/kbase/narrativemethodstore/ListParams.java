
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
 * <p>Original spec-file type: ListParams</p>
 * <pre>
 * These parameters do nothing currently, but are a placeholder for future options
 * on listing methods or apps
 * limit - optional field (default value is 0)
 * offset - optional field (default value is 0)
 * tag - optional access level for dynamic repos (one of 'dev', 'beta' or 'release').
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "limit",
    "offset",
    "tag"
})
public class ListParams {

    @JsonProperty("limit")
    private Long limit;
    @JsonProperty("offset")
    private Long offset;
    @JsonProperty("tag")
    private String tag;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("limit")
    public Long getLimit() {
        return limit;
    }

    @JsonProperty("limit")
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public ListParams withLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    @JsonProperty("offset")
    public Long getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public ListParams withOffset(Long offset) {
        this.offset = offset;
        return this;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }

    public ListParams withTag(String tag) {
        this.tag = tag;
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
        return ((((((((("ListParams"+" [limit=")+ limit)+", offset=")+ offset)+", tag=")+ tag)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
