
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
 * <p>Original spec-file type: Category</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "name",
    "ver",
    "tooltip",
    "description",
    "parent_ids",
    "loading_error"
})
public class Category {

    @JsonProperty("id")
    private java.lang.String id;
    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("ver")
    private java.lang.String ver;
    @JsonProperty("tooltip")
    private java.lang.String tooltip;
    @JsonProperty("description")
    private java.lang.String description;
    @JsonProperty("parent_ids")
    private List<String> parentIds;
    @JsonProperty("loading_error")
    private java.lang.String loadingError;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("id")
    public java.lang.String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(java.lang.String id) {
        this.id = id;
    }

    public Category withId(java.lang.String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public Category withName(java.lang.String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("ver")
    public java.lang.String getVer() {
        return ver;
    }

    @JsonProperty("ver")
    public void setVer(java.lang.String ver) {
        this.ver = ver;
    }

    public Category withVer(java.lang.String ver) {
        this.ver = ver;
        return this;
    }

    @JsonProperty("tooltip")
    public java.lang.String getTooltip() {
        return tooltip;
    }

    @JsonProperty("tooltip")
    public void setTooltip(java.lang.String tooltip) {
        this.tooltip = tooltip;
    }

    public Category withTooltip(java.lang.String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @JsonProperty("description")
    public java.lang.String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public Category withDescription(java.lang.String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("parent_ids")
    public List<String> getParentIds() {
        return parentIds;
    }

    @JsonProperty("parent_ids")
    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public Category withParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
        return this;
    }

    @JsonProperty("loading_error")
    public java.lang.String getLoadingError() {
        return loadingError;
    }

    @JsonProperty("loading_error")
    public void setLoadingError(java.lang.String loadingError) {
        this.loadingError = loadingError;
    }

    public Category withLoadingError(java.lang.String loadingError) {
        this.loadingError = loadingError;
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
        return ((((((((((((((((("Category"+" [id=")+ id)+", name=")+ name)+", ver=")+ ver)+", tooltip=")+ tooltip)+", description=")+ description)+", parentIds=")+ parentIds)+", loadingError=")+ loadingError)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
