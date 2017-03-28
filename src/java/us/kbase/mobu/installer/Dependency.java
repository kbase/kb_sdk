package us.kbase.mobu.installer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "module_name",
    "type",
    "version_tag",
    "file_path"
})
public class Dependency {
    @JsonProperty("module_name")
    public String moduleName;
    @JsonProperty("type")
    public String type;
    @JsonProperty("version_tag")
    public String versionTag;
    @JsonProperty("file_path")
    public String filePath;
}
