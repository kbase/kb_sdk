package us.kbase.mobu.compiler.report;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Report {
    @JsonProperty("module_name")
    public String moduleName;
    @JsonProperty("sdk_version")
    public String sdkVersion;
    @JsonProperty("sdk_git_commit")
    public String sdkGitCommit;
    @JsonProperty("impl_file_path")
    public String implFilePath;
    @JsonProperty("kidl_specs")
    public Map<String, String> kidlSpecs;
    @JsonProperty("function_places")
    public Map<String, FunctionPlace> functionPlaces;
    @JsonProperty("functions")
    public Map<String, Function> functions;
}