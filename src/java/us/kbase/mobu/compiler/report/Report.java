package us.kbase.mobu.compiler.report;

import java.util.List;
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
    @JsonProperty("spec_files")
    public List<SpecFile> specFiles;
    @JsonProperty("function_places")
    public Map<String, FunctionPlace> functionPlaces;
    @JsonProperty("functions")
    public Map<String, Function> functions;
}