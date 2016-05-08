
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
 * <p>Original spec-file type: ExternalDataUnit</p>
 * <pre>
 * An external data unit. A piece of data from a source outside the
 * Workspace.
 * string resource_name - the name of the resource, for example JGI.
 * string resource_url - the url of the resource, for example
 *         http://genome.jgi.doe.gov
 * string resource_version - version of the resource
 * timestamp resource_release_date - the release date of the resource
 * string data_url - the url of the data, for example
 *         http://genome.jgi.doe.gov/pages/dynamicOrganismDownload.jsf?
 *                 organism=BlaspURHD0036
 * string data_id - the id of the data, for example
 *         7625.2.79179.AGTTCC.adnq.fastq.gz
 * string description - a free text description of the data.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "resource_name",
    "resource_url",
    "resource_version",
    "resource_release_date",
    "data_url",
    "data_id",
    "description"
})
public class ExternalDataUnit {

    @JsonProperty("resource_name")
    private String resourceName;
    @JsonProperty("resource_url")
    private String resourceUrl;
    @JsonProperty("resource_version")
    private String resourceVersion;
    @JsonProperty("resource_release_date")
    private String resourceReleaseDate;
    @JsonProperty("data_url")
    private String dataUrl;
    @JsonProperty("data_id")
    private String dataId;
    @JsonProperty("description")
    private String description;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("resource_name")
    public String getResourceName() {
        return resourceName;
    }

    @JsonProperty("resource_name")
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ExternalDataUnit withResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    @JsonProperty("resource_url")
    public String getResourceUrl() {
        return resourceUrl;
    }

    @JsonProperty("resource_url")
    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public ExternalDataUnit withResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    @JsonProperty("resource_version")
    public String getResourceVersion() {
        return resourceVersion;
    }

    @JsonProperty("resource_version")
    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public ExternalDataUnit withResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
        return this;
    }

    @JsonProperty("resource_release_date")
    public String getResourceReleaseDate() {
        return resourceReleaseDate;
    }

    @JsonProperty("resource_release_date")
    public void setResourceReleaseDate(String resourceReleaseDate) {
        this.resourceReleaseDate = resourceReleaseDate;
    }

    public ExternalDataUnit withResourceReleaseDate(String resourceReleaseDate) {
        this.resourceReleaseDate = resourceReleaseDate;
        return this;
    }

    @JsonProperty("data_url")
    public String getDataUrl() {
        return dataUrl;
    }

    @JsonProperty("data_url")
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public ExternalDataUnit withDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
        return this;
    }

    @JsonProperty("data_id")
    public String getDataId() {
        return dataId;
    }

    @JsonProperty("data_id")
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public ExternalDataUnit withDataId(String dataId) {
        this.dataId = dataId;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public ExternalDataUnit withDescription(String description) {
        this.description = description;
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
        return ((((((((((((((((("ExternalDataUnit"+" [resourceName=")+ resourceName)+", resourceUrl=")+ resourceUrl)+", resourceVersion=")+ resourceVersion)+", resourceReleaseDate=")+ resourceReleaseDate)+", dataUrl=")+ dataUrl)+", dataId=")+ dataId)+", description=")+ description)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
