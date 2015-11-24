
package us.kbase.narrativemethodstore;

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
 * <p>Original spec-file type: AppFullInfo</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "name",
    "ver",
    "authors",
    "contact",
    "subtitle",
    "tooltip",
    "header",
    "description",
    "technical_description",
    "suggestions",
    "categories",
    "icon",
    "screenshots"
})
public class AppFullInfo {

    @JsonProperty("id")
    private java.lang.String id;
    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("ver")
    private java.lang.String ver;
    @JsonProperty("authors")
    private List<String> authors;
    @JsonProperty("contact")
    private java.lang.String contact;
    @JsonProperty("subtitle")
    private java.lang.String subtitle;
    @JsonProperty("tooltip")
    private java.lang.String tooltip;
    @JsonProperty("header")
    private java.lang.String header;
    @JsonProperty("description")
    private java.lang.String description;
    @JsonProperty("technical_description")
    private java.lang.String technicalDescription;
    /**
     * <p>Original spec-file type: Suggestions</p>
     * 
     * 
     */
    @JsonProperty("suggestions")
    private Suggestions suggestions;
    @JsonProperty("categories")
    private List<String> categories;
    /**
     * <p>Original spec-file type: Icon</p>
     * 
     * 
     */
    @JsonProperty("icon")
    private Icon icon;
    @JsonProperty("screenshots")
    private List<ScreenShot> screenshots;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("id")
    public java.lang.String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(java.lang.String id) {
        this.id = id;
    }

    public AppFullInfo withId(java.lang.String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public AppFullInfo withName(java.lang.String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("ver")
    public java.lang.String getVer() {
        return ver;
    }

    @JsonProperty("ver")
    public void setVer(java.lang.String ver) {
        this.ver = ver;
    }

    public AppFullInfo withVer(java.lang.String ver) {
        this.ver = ver;
        return this;
    }

    @JsonProperty("authors")
    public List<String> getAuthors() {
        return authors;
    }

    @JsonProperty("authors")
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public AppFullInfo withAuthors(List<String> authors) {
        this.authors = authors;
        return this;
    }

    @JsonProperty("contact")
    public java.lang.String getContact() {
        return contact;
    }

    @JsonProperty("contact")
    public void setContact(java.lang.String contact) {
        this.contact = contact;
    }

    public AppFullInfo withContact(java.lang.String contact) {
        this.contact = contact;
        return this;
    }

    @JsonProperty("subtitle")
    public java.lang.String getSubtitle() {
        return subtitle;
    }

    @JsonProperty("subtitle")
    public void setSubtitle(java.lang.String subtitle) {
        this.subtitle = subtitle;
    }

    public AppFullInfo withSubtitle(java.lang.String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    @JsonProperty("tooltip")
    public java.lang.String getTooltip() {
        return tooltip;
    }

    @JsonProperty("tooltip")
    public void setTooltip(java.lang.String tooltip) {
        this.tooltip = tooltip;
    }

    public AppFullInfo withTooltip(java.lang.String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @JsonProperty("header")
    public java.lang.String getHeader() {
        return header;
    }

    @JsonProperty("header")
    public void setHeader(java.lang.String header) {
        this.header = header;
    }

    public AppFullInfo withHeader(java.lang.String header) {
        this.header = header;
        return this;
    }

    @JsonProperty("description")
    public java.lang.String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public AppFullInfo withDescription(java.lang.String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("technical_description")
    public java.lang.String getTechnicalDescription() {
        return technicalDescription;
    }

    @JsonProperty("technical_description")
    public void setTechnicalDescription(java.lang.String technicalDescription) {
        this.technicalDescription = technicalDescription;
    }

    public AppFullInfo withTechnicalDescription(java.lang.String technicalDescription) {
        this.technicalDescription = technicalDescription;
        return this;
    }

    /**
     * <p>Original spec-file type: Suggestions</p>
     * 
     * 
     */
    @JsonProperty("suggestions")
    public Suggestions getSuggestions() {
        return suggestions;
    }

    /**
     * <p>Original spec-file type: Suggestions</p>
     * 
     * 
     */
    @JsonProperty("suggestions")
    public void setSuggestions(Suggestions suggestions) {
        this.suggestions = suggestions;
    }

    public AppFullInfo withSuggestions(Suggestions suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    @JsonProperty("categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public AppFullInfo withCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    /**
     * <p>Original spec-file type: Icon</p>
     * 
     * 
     */
    @JsonProperty("icon")
    public Icon getIcon() {
        return icon;
    }

    /**
     * <p>Original spec-file type: Icon</p>
     * 
     * 
     */
    @JsonProperty("icon")
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public AppFullInfo withIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    @JsonProperty("screenshots")
    public List<ScreenShot> getScreenshots() {
        return screenshots;
    }

    @JsonProperty("screenshots")
    public void setScreenshots(List<ScreenShot> screenshots) {
        this.screenshots = screenshots;
    }

    public AppFullInfo withScreenshots(List<ScreenShot> screenshots) {
        this.screenshots = screenshots;
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
        return ((((((((((((((((((((((((((((((("AppFullInfo"+" [id=")+ id)+", name=")+ name)+", ver=")+ ver)+", authors=")+ authors)+", contact=")+ contact)+", subtitle=")+ subtitle)+", tooltip=")+ tooltip)+", header=")+ header)+", description=")+ description)+", technicalDescription=")+ technicalDescription)+", suggestions=")+ suggestions)+", categories=")+ categories)+", icon=")+ icon)+", screenshots=")+ screenshots)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
