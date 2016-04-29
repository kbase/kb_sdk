
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
 * <p>Original spec-file type: SetUserRolesParams</p>
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
    "role_names"
})
public class SetUserRolesParams {

    @JsonProperty("user")
    private java.lang.String user;
    @JsonProperty("role_names")
    private List<String> roleNames;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("user")
    public java.lang.String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    public SetUserRolesParams withUser(java.lang.String user) {
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

    public SetUserRolesParams withRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
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
        return ((((((("SetUserRolesParams"+" [user=")+ user)+", roleNames=")+ roleNames)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
