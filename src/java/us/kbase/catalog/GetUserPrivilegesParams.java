
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
 * <p>Original spec-file type: GetUserPrivilegesParams</p>
 * <pre>
 * users - list of interesting users, may include '*' user defining default roles.
 * list_every_user - optional flag available only for admins.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "users",
    "list_every_user"
})
public class GetUserPrivilegesParams {

    @JsonProperty("users")
    private List<String> users;
    @JsonProperty("list_every_user")
    private Long listEveryUser;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("users")
    public List<String> getUsers() {
        return users;
    }

    @JsonProperty("users")
    public void setUsers(List<String> users) {
        this.users = users;
    }

    public GetUserPrivilegesParams withUsers(List<String> users) {
        this.users = users;
        return this;
    }

    @JsonProperty("list_every_user")
    public Long getListEveryUser() {
        return listEveryUser;
    }

    @JsonProperty("list_every_user")
    public void setListEveryUser(Long listEveryUser) {
        this.listEveryUser = listEveryUser;
    }

    public GetUserPrivilegesParams withListEveryUser(Long listEveryUser) {
        this.listEveryUser = listEveryUser;
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
        return ((((((("GetUserPrivilegesParams"+" [users=")+ users)+", listEveryUser=")+ listEveryUser)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
