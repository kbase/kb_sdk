
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
 * <p>Original spec-file type: MethodFullInfo</p>
 * <pre>
 * Full information about a method suitable for displaying a method landing page.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "module_name",
    "name",
    "ver",
    "authors",
    "kb_contributors",
    "contact",
    "subtitle",
    "tooltip",
    "description",
    "technical_description",
    "suggestions",
    "icon",
    "categories",
    "screenshots",
    "publications"
})
public class MethodFullInfo {

    @JsonProperty("id")
    private java.lang.String id;
    @JsonProperty("module_name")
    private java.lang.String moduleName;
    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("ver")
    private java.lang.String ver;
    @JsonProperty("authors")
    private List<String> authors;
    @JsonProperty("kb_contributors")
    private List<String> kbContributors;
    @JsonProperty("contact")
    private java.lang.String contact;
    @JsonProperty("subtitle")
    private java.lang.String subtitle;
    @JsonProperty("tooltip")
    private java.lang.String tooltip;
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
    /**
     * <p>Original spec-file type: Icon</p>
     * 
     * 
     */
    @JsonProperty("icon")
    private Icon icon;
    @JsonProperty("categories")
    private List<String> categories;
    @JsonProperty("screenshots")
    private List<ScreenShot> screenshots;
    @JsonProperty("publications")
    private List<Publication> publications;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("id")
    public java.lang.String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(java.lang.String id) {
        this.id = id;
    }

    public MethodFullInfo withId(java.lang.String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("module_name")
    public java.lang.String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
    }

    public MethodFullInfo withModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
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

    public MethodFullInfo withName(java.lang.String name) {
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

    public MethodFullInfo withVer(java.lang.String ver) {
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

    public MethodFullInfo withAuthors(List<String> authors) {
        this.authors = authors;
        return this;
    }

    @JsonProperty("kb_contributors")
    public List<String> getKbContributors() {
        return kbContributors;
    }

    @JsonProperty("kb_contributors")
    public void setKbContributors(List<String> kbContributors) {
        this.kbContributors = kbContributors;
    }

    public MethodFullInfo withKbContributors(List<String> kbContributors) {
        this.kbContributors = kbContributors;
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

    public MethodFullInfo withContact(java.lang.String contact) {
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

    public MethodFullInfo withSubtitle(java.lang.String subtitle) {
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

    public MethodFullInfo withTooltip(java.lang.String tooltip) {
        this.tooltip = tooltip;
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

    public MethodFullInfo withDescription(java.lang.String description) {
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

    public MethodFullInfo withTechnicalDescription(java.lang.String technicalDescription) {
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

    public MethodFullInfo withSuggestions(Suggestions suggestions) {
        this.suggestions = suggestions;
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

    public MethodFullInfo withIcon(Icon icon) {
        this.icon = icon;
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

    public MethodFullInfo withCategories(List<String> categories) {
        this.categories = categories;
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

    public MethodFullInfo withScreenshots(List<ScreenShot> screenshots) {
        this.screenshots = screenshots;
        return this;
    }

    @JsonProperty("publications")
    public List<Publication> getPublications() {
        return publications;
    }

    @JsonProperty("publications")
    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public MethodFullInfo withPublications(List<Publication> publications) {
        this.publications = publications;
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
        return ((((((((((((((((((((((((((((((((((("MethodFullInfo"+" [id=")+ id)+", moduleName=")+ moduleName)+", name=")+ name)+", ver=")+ ver)+", authors=")+ authors)+", kbContributors=")+ kbContributors)+", contact=")+ contact)+", subtitle=")+ subtitle)+", tooltip=")+ tooltip)+", description=")+ description)+", technicalDescription=")+ technicalDescription)+", suggestions=")+ suggestions)+", icon=")+ icon)+", categories=")+ categories)+", screenshots=")+ screenshots)+", publications=")+ publications)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
