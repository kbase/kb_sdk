package us.kbase.mobu.compiler.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FunctionPlace {
    @JsonProperty("start_line")
    public int startLine;
    @JsonProperty("end_line")
    public int endLine;
    
    public FunctionPlace with(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
        return this;
    }
}