
package us.kbase.workspace;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: SubAction</p>
 * <pre>
 * Information about a subaction that is invoked by a provenance action.
 *         A provenance action (PA) may invoke subactions (SA), e.g. calling a
 *         separate piece of code, a service, or a script. In most cases these
 *         calls are the same from PA to PA and so do not need to be listed in
 *         the provenance since providing information about the PA alone provides
 *         reproducibility.
 *         
 *         In some cases, however, SAs may change over time, such that invoking
 *         the same PA with the same parameters may produce different results.
 *         For example, if a PA calls a remote server, that server may be updated
 *         between a PA invoked on day T and another PA invoked on day T+1.
 *         
 *         The SubAction structure allows for specifying information about SAs
 *         that may dynamically change from PA invocation to PA invocation.
 *         
 *         string name - the name of the SA.
 *         string ver - the version of SA.
 *         string code_url - a url pointing to the SA's codebase.
 *         string commit - a version control commit ID for the SA.
 *         string endpoint_url - a url pointing to the access point for the SA -
 *                 a server url, for instance.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "ver",
    "code_url",
    "commit",
    "endpoint_url"
})
public class SubAction {

    @JsonProperty("name")
    private String name;
    @JsonProperty("ver")
    private String ver;
    @JsonProperty("code_url")
    private String codeUrl;
    @JsonProperty("commit")
    private String commit;
    @JsonProperty("endpoint_url")
    private String endpointUrl;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public SubAction withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("ver")
    public String getVer() {
        return ver;
    }

    @JsonProperty("ver")
    public void setVer(String ver) {
        this.ver = ver;
    }

    public SubAction withVer(String ver) {
        this.ver = ver;
        return this;
    }

    @JsonProperty("code_url")
    public String getCodeUrl() {
        return codeUrl;
    }

    @JsonProperty("code_url")
    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public SubAction withCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
        return this;
    }

    @JsonProperty("commit")
    public String getCommit() {
        return commit;
    }

    @JsonProperty("commit")
    public void setCommit(String commit) {
        this.commit = commit;
    }

    public SubAction withCommit(String commit) {
        this.commit = commit;
        return this;
    }

    @JsonProperty("endpoint_url")
    public String getEndpointUrl() {
        return endpointUrl;
    }

    @JsonProperty("endpoint_url")
    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public SubAction withEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
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
        return ((((((((((((("SubAction"+" [name=")+ name)+", ver=")+ ver)+", codeUrl=")+ codeUrl)+", commit=")+ commit)+", endpointUrl=")+ endpointUrl)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
