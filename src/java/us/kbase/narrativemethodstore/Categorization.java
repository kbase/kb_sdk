
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
 * <p>Original spec-file type: categorization</p>
 * <pre>
 * Organization of where in a menu the method should appear
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "category_id",
    "named_path"
})
public class Categorization {

    @JsonProperty("category_id")
    private java.lang.String categoryId;
    @JsonProperty("named_path")
    private List<String> namedPath;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("category_id")
    public java.lang.String getCategoryId() {
        return categoryId;
    }

    @JsonProperty("category_id")
    public void setCategoryId(java.lang.String categoryId) {
        this.categoryId = categoryId;
    }

    public Categorization withCategoryId(java.lang.String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    @JsonProperty("named_path")
    public List<String> getNamedPath() {
        return namedPath;
    }

    @JsonProperty("named_path")
    public void setNamedPath(List<String> namedPath) {
        this.namedPath = namedPath;
    }

    public Categorization withNamedPath(List<String> namedPath) {
        this.namedPath = namedPath;
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
        return ((((((("Categorization"+" [categoryId=")+ categoryId)+", namedPath=")+ namedPath)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
