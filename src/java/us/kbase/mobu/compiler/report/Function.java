package us.kbase.mobu.compiler.report;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Function {
    @JsonProperty("name")
    public String name;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("place")
    public FunctionPlace place;
    @JsonProperty("input")
    public List<Parameter> input;
    @JsonProperty("output")
    public List<Parameter> output;
}