package us.kbase.mobu.compiler.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameter {
    @JsonProperty("type")
    public String type;
    @JsonProperty("comment")
    public String comment;
}