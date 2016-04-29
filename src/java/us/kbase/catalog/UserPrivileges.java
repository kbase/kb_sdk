
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
 * <p>Original spec-file type: UserPrivileges</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "user",
    "roles",
    "privileges"
})
public class UserPrivileges {

    @JsonProperty("user")
    private java.lang.String user;
    @JsonProperty("roles")
    private List<UserRole> roles;
    @JsonProperty("privileges")
    private List<String> privileges;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("user")
    public java.lang.String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    public UserPrivileges withUser(java.lang.String user) {
        this.user = user;
        return this;
    }

    @JsonProperty("roles")
    public List<UserRole> getRoles() {
        return roles;
    }

    @JsonProperty("roles")
    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public UserPrivileges withRoles(List<UserRole> roles) {
        this.roles = roles;
        return this;
    }

    @JsonProperty("privileges")
    public List<String> getPrivileges() {
        return privileges;
    }

    @JsonProperty("privileges")
    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public UserPrivileges withPrivileges(List<String> privileges) {
        this.privileges = privileges;
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
        return ((((((((("UserPrivileges"+" [user=")+ user)+", roles=")+ roles)+", privileges=")+ privileges)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
