
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
 * <p>Original spec-file type: SetUserPrivilegesParams</p>
 * <pre>
 * user - may be '*' defining default roles.
 * roles - new set of roles (for existing user this set overrides old set completely)
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "user",
    "roles"
})
public class SetUserPrivilegesParams {

    @JsonProperty("user")
    private java.lang.String user;
    @JsonProperty("roles")
    private List<String> roles;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("user")
    public java.lang.String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    public SetUserPrivilegesParams withUser(java.lang.String user) {
        this.user = user;
        return this;
    }

    @JsonProperty("roles")
    public List<String> getRoles() {
        return roles;
    }

    @JsonProperty("roles")
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public SetUserPrivilegesParams withRoles(List<String> roles) {
        this.roles = roles;
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
        return ((((((("SetUserPrivilegesParams"+" [user=")+ user)+", roles=")+ roles)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
