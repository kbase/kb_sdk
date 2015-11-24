
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
 * <p>Original spec-file type: TypeInfo</p>
 * <pre>
 * @optional icon landing_page_url_prefix loading_error
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "type_name",
    "name",
    "subtitle",
    "tooltip",
    "description",
    "icon",
    "view_method_ids",
    "import_method_ids",
    "landing_page_url_prefix",
    "loading_error"
})
public class TypeInfo {

    @JsonProperty("type_name")
    private java.lang.String typeName;
    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("subtitle")
    private java.lang.String subtitle;
    @JsonProperty("tooltip")
    private java.lang.String tooltip;
    @JsonProperty("description")
    private java.lang.String description;
    /**
     * <p>Original spec-file type: ScreenShot</p>
     * 
     * 
     */
    @JsonProperty("icon")
    private ScreenShot icon;
    @JsonProperty("view_method_ids")
    private List<String> viewMethodIds;
    @JsonProperty("import_method_ids")
    private List<String> importMethodIds;
    @JsonProperty("landing_page_url_prefix")
    private java.lang.String landingPageUrlPrefix;
    @JsonProperty("loading_error")
    private java.lang.String loadingError;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("type_name")
    public java.lang.String getTypeName() {
        return typeName;
    }

    @JsonProperty("type_name")
    public void setTypeName(java.lang.String typeName) {
        this.typeName = typeName;
    }

    public TypeInfo withTypeName(java.lang.String typeName) {
        this.typeName = typeName;
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

    public TypeInfo withName(java.lang.String name) {
        this.name = name;
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

    public TypeInfo withSubtitle(java.lang.String subtitle) {
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

    public TypeInfo withTooltip(java.lang.String tooltip) {
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

    public TypeInfo withDescription(java.lang.String description) {
        this.description = description;
        return this;
    }

    /**
     * <p>Original spec-file type: ScreenShot</p>
     * 
     * 
     */
    @JsonProperty("icon")
    public ScreenShot getIcon() {
        return icon;
    }

    /**
     * <p>Original spec-file type: ScreenShot</p>
     * 
     * 
     */
    @JsonProperty("icon")
    public void setIcon(ScreenShot icon) {
        this.icon = icon;
    }

    public TypeInfo withIcon(ScreenShot icon) {
        this.icon = icon;
        return this;
    }

    @JsonProperty("view_method_ids")
    public List<String> getViewMethodIds() {
        return viewMethodIds;
    }

    @JsonProperty("view_method_ids")
    public void setViewMethodIds(List<String> viewMethodIds) {
        this.viewMethodIds = viewMethodIds;
    }

    public TypeInfo withViewMethodIds(List<String> viewMethodIds) {
        this.viewMethodIds = viewMethodIds;
        return this;
    }

    @JsonProperty("import_method_ids")
    public List<String> getImportMethodIds() {
        return importMethodIds;
    }

    @JsonProperty("import_method_ids")
    public void setImportMethodIds(List<String> importMethodIds) {
        this.importMethodIds = importMethodIds;
    }

    public TypeInfo withImportMethodIds(List<String> importMethodIds) {
        this.importMethodIds = importMethodIds;
        return this;
    }

    @JsonProperty("landing_page_url_prefix")
    public java.lang.String getLandingPageUrlPrefix() {
        return landingPageUrlPrefix;
    }

    @JsonProperty("landing_page_url_prefix")
    public void setLandingPageUrlPrefix(java.lang.String landingPageUrlPrefix) {
        this.landingPageUrlPrefix = landingPageUrlPrefix;
    }

    public TypeInfo withLandingPageUrlPrefix(java.lang.String landingPageUrlPrefix) {
        this.landingPageUrlPrefix = landingPageUrlPrefix;
        return this;
    }

    @JsonProperty("loading_error")
    public java.lang.String getLoadingError() {
        return loadingError;
    }

    @JsonProperty("loading_error")
    public void setLoadingError(java.lang.String loadingError) {
        this.loadingError = loadingError;
    }

    public TypeInfo withLoadingError(java.lang.String loadingError) {
        this.loadingError = loadingError;
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
        return ((((((((((((((((((((((("TypeInfo"+" [typeName=")+ typeName)+", name=")+ name)+", subtitle=")+ subtitle)+", tooltip=")+ tooltip)+", description=")+ description)+", icon=")+ icon)+", viewMethodIds=")+ viewMethodIds)+", importMethodIds=")+ importMethodIds)+", landingPageUrlPrefix=")+ landingPageUrlPrefix)+", loadingError=")+ loadingError)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
