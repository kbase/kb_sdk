
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
 * <p>Original spec-file type: AppClientGroup</p>
 * <pre>
 * app_id = full app id; if module name is used it will be case insensitive 
 * this will overwrite all existing client groups (it won't just push what's on the list)
 * If client_groups is empty or set to null, then the client_group mapping will be removed.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "app_id",
    "client_groups"
})
public class AppClientGroup {

    @JsonProperty("app_id")
    private java.lang.String appId;
    @JsonProperty("client_groups")
    private List<String> clientGroups;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("app_id")
    public java.lang.String getAppId() {
        return appId;
    }

    @JsonProperty("app_id")
    public void setAppId(java.lang.String appId) {
        this.appId = appId;
    }

    public AppClientGroup withAppId(java.lang.String appId) {
        this.appId = appId;
        return this;
    }

    @JsonProperty("client_groups")
    public List<String> getClientGroups() {
        return clientGroups;
    }

    @JsonProperty("client_groups")
    public void setClientGroups(List<String> clientGroups) {
        this.clientGroups = clientGroups;
    }

    public AppClientGroup withClientGroups(List<String> clientGroups) {
        this.clientGroups = clientGroups;
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
        return ((((((("AppClientGroup"+" [appId=")+ appId)+", clientGroups=")+ clientGroups)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
