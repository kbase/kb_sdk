
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
 * <p>Original spec-file type: CompilationReport</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "sdk_version",
    "sdk_git_commit",
    "impl_file_path",
    "function_places"
})
public class CompilationReport {

    @JsonProperty("sdk_version")
    private java.lang.String sdkVersion;
    @JsonProperty("sdk_git_commit")
    private java.lang.String sdkGitCommit;
    @JsonProperty("impl_file_path")
    private java.lang.String implFilePath;
    @JsonProperty("function_places")
    private Map<String, FunctionPlace> functionPlaces;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("sdk_version")
    public java.lang.String getSdkVersion() {
        return sdkVersion;
    }

    @JsonProperty("sdk_version")
    public void setSdkVersion(java.lang.String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public CompilationReport withSdkVersion(java.lang.String sdkVersion) {
        this.sdkVersion = sdkVersion;
        return this;
    }

    @JsonProperty("sdk_git_commit")
    public java.lang.String getSdkGitCommit() {
        return sdkGitCommit;
    }

    @JsonProperty("sdk_git_commit")
    public void setSdkGitCommit(java.lang.String sdkGitCommit) {
        this.sdkGitCommit = sdkGitCommit;
    }

    public CompilationReport withSdkGitCommit(java.lang.String sdkGitCommit) {
        this.sdkGitCommit = sdkGitCommit;
        return this;
    }

    @JsonProperty("impl_file_path")
    public java.lang.String getImplFilePath() {
        return implFilePath;
    }

    @JsonProperty("impl_file_path")
    public void setImplFilePath(java.lang.String implFilePath) {
        this.implFilePath = implFilePath;
    }

    public CompilationReport withImplFilePath(java.lang.String implFilePath) {
        this.implFilePath = implFilePath;
        return this;
    }

    @JsonProperty("function_places")
    public Map<String, FunctionPlace> getFunctionPlaces() {
        return functionPlaces;
    }

    @JsonProperty("function_places")
    public void setFunctionPlaces(Map<String, FunctionPlace> functionPlaces) {
        this.functionPlaces = functionPlaces;
    }

    public CompilationReport withFunctionPlaces(Map<String, FunctionPlace> functionPlaces) {
        this.functionPlaces = functionPlaces;
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
        return ((((((((((("CompilationReport"+" [sdkVersion=")+ sdkVersion)+", sdkGitCommit=")+ sdkGitCommit)+", implFilePath=")+ implFilePath)+", functionPlaces=")+ functionPlaces)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
