
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
 * <p>Original spec-file type: RoleInfo</p>
 * <pre>
 * privileges - list of privileges defined for given role.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "role_name",
    "privileges"
})
public class RoleInfo {

    @JsonProperty("role_name")
    private java.lang.String roleName;
    @JsonProperty("privileges")
    private List<String> privileges;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("role_name")
    public java.lang.String getRoleName() {
        return roleName;
    }

    @JsonProperty("role_name")
    public void setRoleName(java.lang.String roleName) {
        this.roleName = roleName;
    }

    public RoleInfo withRoleName(java.lang.String roleName) {
        this.roleName = roleName;
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

    public RoleInfo withPrivileges(List<String> privileges) {
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
        return ((((((("RoleInfo"+" [roleName=")+ roleName)+", privileges=")+ privileges)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
