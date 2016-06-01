package us.kbase.mobu.compiler.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpecFile {
    @JsonProperty("file_name")
    public String fileName;
    @JsonProperty("content")
    public String content;
    @JsonProperty("is_main")
    public long isMain;
}