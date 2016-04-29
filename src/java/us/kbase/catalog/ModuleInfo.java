
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
 * <p>Original spec-file type: ModuleInfo</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "git_url",
    "description",
    "language",
    "owners",
    "release",
    "beta",
    "dev"
})
public class ModuleInfo {

    @JsonProperty("module_name")
    private java.lang.String moduleName;
    @JsonProperty("git_url")
    private java.lang.String gitUrl;
    @JsonProperty("description")
    private java.lang.String description;
    @JsonProperty("language")
    private java.lang.String language;
    @JsonProperty("owners")
    private List<String> owners;
    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("release")
    private ModuleVersionInfo release;
    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("beta")
    private ModuleVersionInfo beta;
    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("dev")
    private ModuleVersionInfo dev;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("module_name")
    public java.lang.String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleInfo withModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("git_url")
    public java.lang.String getGitUrl() {
        return gitUrl;
    }

    @JsonProperty("git_url")
    public void setGitUrl(java.lang.String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public ModuleInfo withGitUrl(java.lang.String gitUrl) {
        this.gitUrl = gitUrl;
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

    public ModuleInfo withDescription(java.lang.String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("language")
    public java.lang.String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(java.lang.String language) {
        this.language = language;
    }

    public ModuleInfo withLanguage(java.lang.String language) {
        this.language = language;
        return this;
    }

    @JsonProperty("owners")
    public List<String> getOwners() {
        return owners;
    }

    @JsonProperty("owners")
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public ModuleInfo withOwners(List<String> owners) {
        this.owners = owners;
        return this;
    }

    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("release")
    public ModuleVersionInfo getRelease() {
        return release;
    }

    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("release")
    public void setRelease(ModuleVersionInfo release) {
        this.release = release;
    }

    public ModuleInfo withRelease(ModuleVersionInfo release) {
        this.release = release;
        return this;
    }

    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("beta")
    public ModuleVersionInfo getBeta() {
        return beta;
    }

    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("beta")
    public void setBeta(ModuleVersionInfo beta) {
        this.beta = beta;
    }

    public ModuleInfo withBeta(ModuleVersionInfo beta) {
        this.beta = beta;
        return this;
    }

    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("dev")
    public ModuleVersionInfo getDev() {
        return dev;
    }

    /**
     * <p>Original spec-file type: ModuleVersionInfo</p>
     * <pre>
     * data_folder - optional field representing unique module name (like <module_name> transformed to
     *     lower cases) used for reference data purposes (see description for data_version field). This
     *     value will be treated as part of file system path relative to the base that comes from the 
     *     config (currently base is supposed to be "/kb/data" defined in "ref-data-base" parameter).
     * data_version - optional field, reflects version of data defined in kbase.yml (see "data-version" 
     *     key). In case this field is set data folder with path "/kb/data/<data_folder>/<data_version>"
     *     should be initialized by running docker image with "init" target from catalog. And later when
     *     async methods are run it should be mounted on AWE worker machine into "/data" folder inside 
     *     docker container by execution engine.
     * </pre>
     * 
     */
    @JsonProperty("dev")
    public void setDev(ModuleVersionInfo dev) {
        this.dev = dev;
    }

    public ModuleInfo withDev(ModuleVersionInfo dev) {
        this.dev = dev;
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
        return ((((((((((((((((((("ModuleInfo"+" [moduleName=")+ moduleName)+", gitUrl=")+ gitUrl)+", description=")+ description)+", language=")+ language)+", owners=")+ owners)+", release=")+ release)+", beta=")+ beta)+", dev=")+ dev)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
