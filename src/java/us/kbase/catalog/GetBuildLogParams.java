
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
 * <p>Original spec-file type: GetBuildLogParams</p>
 * <pre>
 * must specify skip & limit, or first_n, or last_n.  If none given, this gets last 5000 lines
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "registration_id",
    "skip",
    "limit",
    "first_n",
    "last_n"
})
public class GetBuildLogParams {

    @JsonProperty("registration_id")
    private String registrationId;
    @JsonProperty("skip")
    private Long skip;
    @JsonProperty("limit")
    private Long limit;
    @JsonProperty("first_n")
    private Long firstN;
    @JsonProperty("last_n")
    private Long lastN;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("registration_id")
    public String getRegistrationId() {
        return registrationId;
    }

    @JsonProperty("registration_id")
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public GetBuildLogParams withRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    @JsonProperty("skip")
    public Long getSkip() {
        return skip;
    }

    @JsonProperty("skip")
    public void setSkip(Long skip) {
        this.skip = skip;
    }

    public GetBuildLogParams withSkip(Long skip) {
        this.skip = skip;
        return this;
    }

    @JsonProperty("limit")
    public Long getLimit() {
        return limit;
    }

    @JsonProperty("limit")
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public GetBuildLogParams withLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    @JsonProperty("first_n")
    public Long getFirstN() {
        return firstN;
    }

    @JsonProperty("first_n")
    public void setFirstN(Long firstN) {
        this.firstN = firstN;
    }

    public GetBuildLogParams withFirstN(Long firstN) {
        this.firstN = firstN;
        return this;
    }

    @JsonProperty("last_n")
    public Long getLastN() {
        return lastN;
    }

    @JsonProperty("last_n")
    public void setLastN(Long lastN) {
        this.lastN = lastN;
    }

    public GetBuildLogParams withLastN(Long lastN) {
        this.lastN = lastN;
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
        return ((((((((((((("GetBuildLogParams"+" [registrationId=")+ registrationId)+", skip=")+ skip)+", limit=")+ limit)+", firstN=")+ firstN)+", lastN=")+ lastN)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
