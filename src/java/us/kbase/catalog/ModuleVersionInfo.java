
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "timestamp",
    "registration_id",
    "version",
    "git_commit_hash",
    "git_commit_message",
    "dynamic_service",
    "narrative_method_ids",
    "local_function_ids",
    "docker_img_name",
    "data_folder",
    "data_version",
    "compilation_report"
})
public class ModuleVersionInfo {

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
    @JsonProperty("narrative_method_ids")
    private List<String> narrativeMethodIds;
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

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ModuleVersionInfo withTimestamp(Long timestamp) {
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

    public ModuleVersionInfo withRegistrationId(java.lang.String registrationId) {
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

    public ModuleVersionInfo withVersion(java.lang.String version) {
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

    public ModuleVersionInfo withGitCommitHash(java.lang.String gitCommitHash) {
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

    public ModuleVersionInfo withGitCommitMessage(java.lang.String gitCommitMessage) {
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

    public ModuleVersionInfo withDynamicService(Long dynamicService) {
        this.dynamicService = dynamicService;
        return this;
    }

    @JsonProperty("narrative_method_ids")
    public List<String> getNarrativeMethodIds() {
        return narrativeMethodIds;
    }

    @JsonProperty("narrative_method_ids")
    public void setNarrativeMethodIds(List<String> narrativeMethodIds) {
        this.narrativeMethodIds = narrativeMethodIds;
    }

    public ModuleVersionInfo withNarrativeMethodIds(List<String> narrativeMethodIds) {
        this.narrativeMethodIds = narrativeMethodIds;
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

    public ModuleVersionInfo withLocalFunctionIds(List<String> localFunctionIds) {
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

    public ModuleVersionInfo withDockerImgName(java.lang.String dockerImgName) {
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

    public ModuleVersionInfo withDataFolder(java.lang.String dataFolder) {
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

    public ModuleVersionInfo withDataVersion(java.lang.String dataVersion) {
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

    public ModuleVersionInfo withCompilationReport(CompilationReport compilationReport) {
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
        return ((((((((((((((((((((((((((("ModuleVersionInfo"+" [timestamp=")+ timestamp)+", registrationId=")+ registrationId)+", version=")+ version)+", gitCommitHash=")+ gitCommitHash)+", gitCommitMessage=")+ gitCommitMessage)+", dynamicService=")+ dynamicService)+", narrativeMethodIds=")+ narrativeMethodIds)+", localFunctionIds=")+ localFunctionIds)+", dockerImgName=")+ dockerImgName)+", dataFolder=")+ dataFolder)+", dataVersion=")+ dataVersion)+", compilationReport=")+ compilationReport)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
