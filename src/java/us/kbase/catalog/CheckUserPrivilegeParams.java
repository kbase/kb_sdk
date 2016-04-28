
package us.kbase.catalog;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: CheckUserPrivilegeParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "user",
    "privilege"
})
public class CheckUserPrivilegeParams {

    @JsonProperty("user")
    private String user;
    @JsonProperty("privilege")
    private String privilege;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    public CheckUserPrivilegeParams withUser(String user) {
        this.user = user;
        return this;
    }

    @JsonProperty("privilege")
    public String getPrivilege() {
        return privilege;
    }

    @JsonProperty("privilege")
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public CheckUserPrivilegeParams withPrivilege(String privilege) {
        this.privilege = privilege;
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
        return ((((((("CheckUserPrivilegeParams"+" [user=")+ user)+", privilege=")+ privilege)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
