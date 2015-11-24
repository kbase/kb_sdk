
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
 * <p>Original spec-file type: LoadWidgetParams</p>
 * <pre>
 * Describes how to find repository widget JavaScript.
 * module_name - name of module defined in kbase.yaml;
 * version - optional parameter limiting search by certain version timestamp;
 * widget_id - name of java-script file stored in repo's 'ui/widgets' folder.
 * tag - optional access level for dynamic repos (one of 'dev', 'beta', 'release').
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "version",
    "widget_id",
    "tag"
})
public class LoadWidgetParams {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("version")
    private Long version;
    @JsonProperty("widget_id")
    private String widgetId;
    @JsonProperty("tag")
    private String tag;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public LoadWidgetParams withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("version")
    public Long getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Long version) {
        this.version = version;
    }

    public LoadWidgetParams withVersion(Long version) {
        this.version = version;
        return this;
    }

    @JsonProperty("widget_id")
    public String getWidgetId() {
        return widgetId;
    }

    @JsonProperty("widget_id")
    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public LoadWidgetParams withWidgetId(String widgetId) {
        this.widgetId = widgetId;
        return this;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }

    public LoadWidgetParams withTag(String tag) {
        this.tag = tag;
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
        return ((((((((((("LoadWidgetParams"+" [moduleName=")+ moduleName)+", version=")+ version)+", widgetId=")+ widgetId)+", tag=")+ tag)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
