
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
 * <p>Original spec-file type: ListBuildParams</p>
 * <pre>
 * Always sorted by time, oldest builds are last.
 * only one of these can be set to true:
 *     only_running - if true, only show running builds
 *     only_error - if true, only show builds that ended in an error
 *     only_complete - if true, only show builds that are complete
 * skip - skip these first n records, default 0
 * limit - limit result to the most recent n records, default 1000
 * modules - only include builds from these modules based on names/git_urls
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "only_runnning",
    "only_error",
    "only_complete",
    "skip",
    "limit",
    "modules"
})
public class ListBuildParams {

    @JsonProperty("only_runnning")
    private Long onlyRunnning;
    @JsonProperty("only_error")
    private Long onlyError;
    @JsonProperty("only_complete")
    private Long onlyComplete;
    @JsonProperty("skip")
    private Long skip;
    @JsonProperty("limit")
    private Long limit;
    @JsonProperty("modules")
    private List<SelectOneModuleParams> modules;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("only_runnning")
    public Long getOnlyRunnning() {
        return onlyRunnning;
    }

    @JsonProperty("only_runnning")
    public void setOnlyRunnning(Long onlyRunnning) {
        this.onlyRunnning = onlyRunnning;
    }

    public ListBuildParams withOnlyRunnning(Long onlyRunnning) {
        this.onlyRunnning = onlyRunnning;
        return this;
    }

    @JsonProperty("only_error")
    public Long getOnlyError() {
        return onlyError;
    }

    @JsonProperty("only_error")
    public void setOnlyError(Long onlyError) {
        this.onlyError = onlyError;
    }

    public ListBuildParams withOnlyError(Long onlyError) {
        this.onlyError = onlyError;
        return this;
    }

    @JsonProperty("only_complete")
    public Long getOnlyComplete() {
        return onlyComplete;
    }

    @JsonProperty("only_complete")
    public void setOnlyComplete(Long onlyComplete) {
        this.onlyComplete = onlyComplete;
    }

    public ListBuildParams withOnlyComplete(Long onlyComplete) {
        this.onlyComplete = onlyComplete;
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

    public ListBuildParams withSkip(Long skip) {
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

    public ListBuildParams withLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    @JsonProperty("modules")
    public List<SelectOneModuleParams> getModules() {
        return modules;
    }

    @JsonProperty("modules")
    public void setModules(List<SelectOneModuleParams> modules) {
        this.modules = modules;
    }

    public ListBuildParams withModules(List<SelectOneModuleParams> modules) {
        this.modules = modules;
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
        return ((((((((((((((("ListBuildParams"+" [onlyRunnning=")+ onlyRunnning)+", onlyError=")+ onlyError)+", onlyComplete=")+ onlyComplete)+", skip=")+ skip)+", limit=")+ limit)+", modules=")+ modules)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
