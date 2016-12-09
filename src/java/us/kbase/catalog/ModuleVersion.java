
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
 * <p>Original spec-file type: ModuleVersion</p>
 * <pre>
 * module_name            - the name of the module
 * module_description     - (optionally returned) html description in KBase YAML of this module
 * git_url                - the git url of the source for this module
 * released               - 1 if this version has been released, 0 otherwise
 * release_tags           - list of strings of: 'dev', 'beta', or 'release', or empty list
 *                          this is a list because the same commit version may be the version in multiple release states
 * release_timestamp      - time in ms since epoch when this module was approved and moved to release, null otherwise
 *                          note that a module was released before v1.0.0, the release timestamp may not have been
 *                          recorded and will default to the registration timestamp
 * timestamp              - time in ms since epoch when the registration for this version was started
 * registration_id        - id of the last registration for this version, used for fetching registration logs and state
 * version                - validated semantic version number as indicated in the KBase YAML of this version
 *                          semantic versions are unique among released versions of this module
 * git_commit_hash        - the full git commit hash of the source for this module
 * git_commit_message     - the message attached to this git commit
 * dynamic_service        - 1 if this version is available as a web service, 0 otherwise
 * narrative_app_ids      - list of Narrative App ids registered with this module version
 * local_function_ids     - list of Local Function ids registered with this module version
 * docker_img_name        - name of the docker image for this module created on registration
 * data_folder            - name of the data folder used 
 * compilation_report     - (optionally returned) summary of the KIDL specification compilation
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "module_name",
    "module_description",
    "git_url",
    "released",
    "release_tags",
    "timestamp",
    "registration_id",
    "version",
    "git_commit_hash",
    "git_commit_message",
    "dynamic_service",
    "narrative_app_ids",
    "local_function_ids",
    "docker_img_name",
    "data_folder",
    "data_version",
    "compilation_report"
})
public class ModuleVersion {

    @JsonProperty("module_name")
    private java.lang.String moduleName;
    @JsonProperty("module_description")
    private java.lang.String moduleDescription;
    @JsonProperty("git_url")
    private java.lang.String gitUrl;
    @JsonProperty("released")
    private Long released;
    @JsonProperty("release_tags")
    private List<String> releaseTags;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("registration_id")
    private java.lang.String registrationId;
    @JsonProperty("version")
    private java.lang.String version;
    @JsonProperty("git_commit_hash")
    private java.lang.String gitCommitHash;
    @JsonProperty("git_commit_message")
    private java.lang.String gitCommitMessage;
    @JsonProperty("dynamic_service")
    private Long dynamicService;
    @JsonProperty("narrative_app_ids")
    private List<String> narrativeAppIds;
    @JsonProperty("local_function_ids")
    private List<String> localFunctionIds;
    @JsonProperty("docker_img_name")
    private java.lang.String dockerImgName;
    @JsonProperty("data_folder")
    private java.lang.String dataFolder;
    @JsonProperty("data_version")
    private java.lang.String dataVersion;
    /**
     * <p>Original spec-file type: CompilationReport</p>
     * 
     * 
     */
    @JsonProperty("compilation_report")
    private CompilationReport compilationReport;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("module_name")
    public java.lang.String getModuleName() {
        return moduleName;
    }

    @JsonProperty("module_name")
    public void setModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleVersion withModuleName(java.lang.String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @JsonProperty("module_description")
    public java.lang.String getModuleDescription() {
        return moduleDescription;
    }

    @JsonProperty("module_description")
    public void setModuleDescription(java.lang.String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }

    public ModuleVersion withModuleDescription(java.lang.String moduleDescription) {
        this.moduleDescription = moduleDescription;
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

    public ModuleVersion withGitUrl(java.lang.String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    @JsonProperty("released")
    public Long getReleased() {
        return released;
    }

    @JsonProperty("released")
    public void setReleased(Long released) {
        this.released = released;
    }

    public ModuleVersion withReleased(Long released) {
        this.released = released;
        return this;
    }

    @JsonProperty("release_tags")
    public List<String> getReleaseTags() {
        return releaseTags;
    }

    @JsonProperty("release_tags")
    public void setReleaseTags(List<String> releaseTags) {
        this.releaseTags = releaseTags;
    }

    public ModuleVersion withReleaseTags(List<String> releaseTags) {
        this.releaseTags = releaseTags;
        return this;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ModuleVersion withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("registration_id")
    public java.lang.String getRegistrationId() {
        return registrationId;
    }

    @JsonProperty("registration_id")
    public void setRegistrationId(java.lang.String registrationId) {
        this.registrationId = registrationId;
    }

    public ModuleVersion withRegistrationId(java.lang.String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    @JsonProperty("version")
    public java.lang.String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    public ModuleVersion withVersion(java.lang.String version) {
        this.version = version;
        return this;
    }

    @JsonProperty("git_commit_hash")
    public java.lang.String getGitCommitHash() {
        return gitCommitHash;
    }

    @JsonProperty("git_commit_hash")
    public void setGitCommitHash(java.lang.String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
    }

    public ModuleVersion withGitCommitHash(java.lang.String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
        return this;
    }

    @JsonProperty("git_commit_message")
    public java.lang.String getGitCommitMessage() {
        return gitCommitMessage;
    }

    @JsonProperty("git_commit_message")
    public void setGitCommitMessage(java.lang.String gitCommitMessage) {
        this.gitCommitMessage = gitCommitMessage;
    }

    public ModuleVersion withGitCommitMessage(java.lang.String gitCommitMessage) {
        this.gitCommitMessage = gitCommitMessage;
        return this;
    }

    @JsonProperty("dynamic_service")
    public Long getDynamicService() {
        return dynamicService;
    }

    @JsonProperty("dynamic_service")
    public void setDynamicService(Long dynamicService) {
        this.dynamicService = dynamicService;
    }

    public ModuleVersion withDynamicService(Long dynamicService) {
        this.dynamicService = dynamicService;
        return this;
    }

    @JsonProperty("narrative_app_ids")
    public List<String> getNarrativeAppIds() {
        return narrativeAppIds;
    }

    @JsonProperty("narrative_app_ids")
    public void setNarrativeAppIds(List<String> narrativeAppIds) {
        this.narrativeAppIds = narrativeAppIds;
    }

    public ModuleVersion withNarrativeAppIds(List<String> narrativeAppIds) {
        this.narrativeAppIds = narrativeAppIds;
        return this;
    }

    @JsonProperty("local_function_ids")
    public List<String> getLocalFunctionIds() {
        return localFunctionIds;
    }

    @JsonProperty("local_function_ids")
    public void setLocalFunctionIds(List<String> localFunctionIds) {
        this.localFunctionIds = localFunctionIds;
    }

    public ModuleVersion withLocalFunctionIds(List<String> localFunctionIds) {
        this.localFunctionIds = localFunctionIds;
        return this;
    }

    @JsonProperty("docker_img_name")
    public java.lang.String getDockerImgName() {
        return dockerImgName;
    }

    @JsonProperty("docker_img_name")
    public void setDockerImgName(java.lang.String dockerImgName) {
        this.dockerImgName = dockerImgName;
    }

    public ModuleVersion withDockerImgName(java.lang.String dockerImgName) {
        this.dockerImgName = dockerImgName;
        return this;
    }

    @JsonProperty("data_folder")
    public java.lang.String getDataFolder() {
        return dataFolder;
    }

    @JsonProperty("data_folder")
    public void setDataFolder(java.lang.String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public ModuleVersion withDataFolder(java.lang.String dataFolder) {
        this.dataFolder = dataFolder;
        return this;
    }

    @JsonProperty("data_version")
    public java.lang.String getDataVersion() {
        return dataVersion;
    }

    @JsonProperty("data_version")
    public void setDataVersion(java.lang.String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public ModuleVersion withDataVersion(java.lang.String dataVersion) {
        this.dataVersion = dataVersion;
        return this;
    }

    /**
     * <p>Original spec-file type: CompilationReport</p>
     * 
     * 
     */
    @JsonProperty("compilation_report")
    public CompilationReport getCompilationReport() {
        return compilationReport;
    }

    /**
     * <p>Original spec-file type: CompilationReport</p>
     * 
     * 
     */
    @JsonProperty("compilation_report")
    public void setCompilationReport(CompilationReport compilationReport) {
        this.compilationReport = compilationReport;
    }

    public ModuleVersion withCompilationReport(CompilationReport compilationReport) {
        this.compilationReport = compilationReport;
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
        return ((((((((((((((((((((((((((((((((((((("ModuleVersion"+" [moduleName=")+ moduleName)+", moduleDescription=")+ moduleDescription)+", gitUrl=")+ gitUrl)+", released=")+ released)+", releaseTags=")+ releaseTags)+", timestamp=")+ timestamp)+", registrationId=")+ registrationId)+", version=")+ version)+", gitCommitHash=")+ gitCommitHash)+", gitCommitMessage=")+ gitCommitMessage)+", dynamicService=")+ dynamicService)+", narrativeAppIds=")+ narrativeAppIds)+", localFunctionIds=")+ localFunctionIds)+", dockerImgName=")+ dockerImgName)+", dataFolder=")+ dataFolder)+", dataVersion=")+ dataVersion)+", compilationReport=")+ compilationReport)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
