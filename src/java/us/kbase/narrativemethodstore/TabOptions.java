
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
 * <p>Original spec-file type: TabOptions</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "tab_id_order",
    "tab_id_to_tab_name",
    "tab_id_to_param_ids"
})
public class TabOptions {

    @JsonProperty("tab_id_order")
    private List<String> tabIdOrder;
    @JsonProperty("tab_id_to_tab_name")
    private Map<String, String> tabIdToTabName;
    @JsonProperty("tab_id_to_param_ids")
    private Map<String, List<String>> tabIdToParamIds;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("tab_id_order")
    public List<String> getTabIdOrder() {
        return tabIdOrder;
    }

    @JsonProperty("tab_id_order")
    public void setTabIdOrder(List<String> tabIdOrder) {
        this.tabIdOrder = tabIdOrder;
    }

    public TabOptions withTabIdOrder(List<String> tabIdOrder) {
        this.tabIdOrder = tabIdOrder;
        return this;
    }

    @JsonProperty("tab_id_to_tab_name")
    public Map<String, String> getTabIdToTabName() {
        return tabIdToTabName;
    }

    @JsonProperty("tab_id_to_tab_name")
    public void setTabIdToTabName(Map<String, String> tabIdToTabName) {
        this.tabIdToTabName = tabIdToTabName;
    }

    public TabOptions withTabIdToTabName(Map<String, String> tabIdToTabName) {
        this.tabIdToTabName = tabIdToTabName;
        return this;
    }

    @JsonProperty("tab_id_to_param_ids")
    public Map<String, List<String>> getTabIdToParamIds() {
        return tabIdToParamIds;
    }

    @JsonProperty("tab_id_to_param_ids")
    public void setTabIdToParamIds(Map<String, List<String>> tabIdToParamIds) {
        this.tabIdToParamIds = tabIdToParamIds;
    }

    public TabOptions withTabIdToParamIds(Map<String, List<String>> tabIdToParamIds) {
        this.tabIdToParamIds = tabIdToParamIds;
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
        return ((((((((("TabOptions"+" [tabIdOrder=")+ tabIdOrder)+", tabIdToTabName=")+ tabIdToTabName)+", tabIdToParamIds=")+ tabIdToParamIds)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
