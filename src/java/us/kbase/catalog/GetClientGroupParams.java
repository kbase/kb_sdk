
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
 * <p>Original spec-file type: GetClientGroupParams</p>
 * <pre>
 * if app_ids is empty or null, all client groups are returned
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "app_ids"
})
public class GetClientGroupParams {

    @JsonProperty("app_ids")
    private List<String> appIds;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("app_ids")
    public List<String> getAppIds() {
        return appIds;
    }

    @JsonProperty("app_ids")
    public void setAppIds(List<String> appIds) {
        this.appIds = appIds;
    }

    public GetClientGroupParams withAppIds(List<String> appIds) {
        this.appIds = appIds;
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
        return ((((("GetClientGroupParams"+" [appIds=")+ appIds)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
