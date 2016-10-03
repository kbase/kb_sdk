
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
 * <p>Original spec-file type: SelectModuleVersion</p>
 * <pre>
 * Get a specific module version.
 * Requires either a module_name or git_url.  If both are provided, they both must match.
 * If no other options are specified, then the latest 'release' version is returned.  If
 * the module has not been released, then the latest 'beta' or 'dev' version is returned.
 * You can check in the returned object if the version has been released (see is_released)
 * and what release tags are pointing to this version (see release_tags).
 * Optionally, a 'version' parameter can be provided that can be either:
 *     1) release tag: 'dev' | 'beta' | 'release'
 *     2) specific semantic version of a released version (you cannot pull dev/beta or other
 *        unreleased versions by semantic version)
 *         - e.g. 2.0.1
 *     3) semantic version requirement specification, see: https://pypi.python.org/pypi/semantic_version/
 *        which will return the latest released version that matches the criteria.  You cannot pull
 *        dev/beta or other unreleased versions this way.
 *         - e.g.:
 *             - '>1.0.0'
 *             - '>=2.1.1,<3.3.0'
 *             - '!=0.2.4-alpha,<0.3.0'
 *     4) specific full git commit hash
 * include_module_description - set to 1 to include the module description in the YAML file of this version;
 *                              default is 0
 * include_compilation_report - set to 1 to include the module compilation report, default is 0
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "git_url",
    "version",
    "include_module_description",
    "include_compilation_report"
})
public class SelectModuleVersion {

    @JsonProperty("module_name")
    private String moduleName;
    @JsonProperty("git_url")
    private String gitUrl;
    @JsonProperty("version")
    private String version;
    @JsonProperty("include_module_description")
    private Long includeModuleDescription;
    @JsonProperty("include_compilation_report")
    private Long includeCompilationReport;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("module_name")
    public String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public SelectModuleVersion withModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("git_url")
    public String getGitUrl() {
        return gitUrl;
    }

    @JsonProperty("git_url")
    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public SelectModuleVersion withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    public SelectModuleVersion withVersion(String version) {
        this.version = version;
        return this;
    }

    @JsonProperty("include_module_description")
    public Long getIncludeModuleDescription() {
        return includeModuleDescription;
    }

    @JsonProperty("include_module_description")
    public void setIncludeModuleDescription(Long includeModuleDescription) {
        this.includeModuleDescription = includeModuleDescription;
    }

    public SelectModuleVersion withIncludeModuleDescription(Long includeModuleDescription) {
        this.includeModuleDescription = includeModuleDescription;
        return this;
    }

    @JsonProperty("include_compilation_report")
    public Long getIncludeCompilationReport() {
        return includeCompilationReport;
    }

    @JsonProperty("include_compilation_report")
    public void setIncludeCompilationReport(Long includeCompilationReport) {
        this.includeCompilationReport = includeCompilationReport;
    }

    public SelectModuleVersion withIncludeCompilationReport(Long includeCompilationReport) {
        this.includeCompilationReport = includeCompilationReport;
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
        return ((((((((((((("SelectModuleVersion"+" [moduleName=")+ moduleName)+", gitUrl=")+ gitUrl)+", version=")+ version)+", includeModuleDescription=")+ includeModuleDescription)+", includeCompilationReport=")+ includeCompilationReport)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
