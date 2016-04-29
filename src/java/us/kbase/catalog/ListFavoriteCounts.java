
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
 * <p>Original spec-file type: ListFavoriteCounts</p>
 * <pre>
 * if favorite item is given, will return stars just for that item.  If a module
 * name is given, will return stars for all methods in that module.  If none of
 * those are given, then will return stars for every method that there is info on 
 * parameters to add:
 *     list<FavoriteItem> items;
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "modules"
})
public class ListFavoriteCounts {

    @JsonProperty("modules")
    private List<String> modules;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("modules")
    public List<String> getModules() {
        return modules;
    }

    @JsonProperty("modules")
    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public ListFavoriteCounts withModules(List<String> modules) {
        this.modules = modules;
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
        return ((((("ListFavoriteCounts"+" [modules=")+ modules)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
