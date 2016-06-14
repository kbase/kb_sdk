
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
 * <p>Original spec-file type: DefineRolesParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "role_infos"
})
public class DefineRolesParams {

    @JsonProperty("role_infos")
    private List<RoleInfo> roleInfos;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("role_infos")
    public List<RoleInfo> getRoleInfos() {
        return roleInfos;
    }

    @JsonProperty("role_infos")
    public void setRoleInfos(List<RoleInfo> roleInfos) {
        this.roleInfos = roleInfos;
    }

    public DefineRolesParams withRoleInfos(List<RoleInfo> roleInfos) {
        this.roleInfos = roleInfos;
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
        return ((((("DefineRolesParams"+" [roleInfos=")+ roleInfos)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
