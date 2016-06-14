
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
 * <p>Original spec-file type: UserRoles</p>
 * <pre>
 * role_names - list of roles defined for given user by set_user_roles (doesn't include
 *     roles of default user).
 * privileges - combined privileges of roles defined for given user and for default user.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "user",
    "role_names",
    "privileges"
})
public class UserRoles {

    @JsonProperty("user")
    private java.lang.String user;
    @JsonProperty("role_names")
    private List<String> roleNames;
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

    public UserRoles withUser(java.lang.String user) {
        this.user = user;
        return this;
    }

    @JsonProperty("role_names")
    public List<String> getRoleNames() {
        return roleNames;
    }

    @JsonProperty("role_names")
    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public UserRoles withRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
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

    public UserRoles withPrivileges(List<String> privileges) {
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
        return ((((((((("UserRoles"+" [user=")+ user)+", roleNames=")+ roleNames)+", privileges=")+ privileges)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
