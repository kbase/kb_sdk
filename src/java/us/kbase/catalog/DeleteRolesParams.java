
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
 * <p>Original spec-file type: DeleteRolesParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "role_names"
})
public class DeleteRolesParams {

    @JsonProperty("role_names")
    private List<String> roleNames;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("role_names")
    public List<String> getRoleNames() {
        return roleNames;
    }

    @JsonProperty("role_names")
    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public DeleteRolesParams withRoleNames(List<String> roleNames) {
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
        return ((((("DeleteRolesParams"+" [roleNames=")+ roleNames)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
