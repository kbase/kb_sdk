
package us.kbase.narrativemethodstore;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: Publication</p>
 * <pre>
 * Publication info can get complicated.  To keep things simple, we only allow a few things now:
 * pmid - pubmed id, if present, we can use this id to pull all publication info we want
 * display_text - what is shown to the user if there is no pubmed id, or if the pubmed id is not valid
 * link - a link to the paper, also not needed if pmid is valid, but could be used if pubmed is down
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "pmid",
    "display_text",
    "link"
})
public class Publication {

    @JsonProperty("pmid")
    private String pmid;
    @JsonProperty("display_text")
    private String displayText;
    @JsonProperty("link")
    private String link;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pmid")
    public String getPmid() {
        return pmid;
    }

    @JsonProperty("pmid")
    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public Publication withPmid(String pmid) {
        this.pmid = pmid;
        return this;
    }

    @JsonProperty("display_text")
    public String getDisplayText() {
        return displayText;
    }

    @JsonProperty("display_text")
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Publication withDisplayText(String displayText) {
        this.displayText = displayText;
        return this;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    public Publication withLink(String link) {
        this.link = link;
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
        return ((((((((("Publication"+" [pmid=")+ pmid)+", displayText=")+ displayText)+", link=")+ link)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
