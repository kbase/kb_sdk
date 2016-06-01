
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
 * <p>Original spec-file type: Function</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "comment",
    "place",
    "input",
    "output"
})
public class Function {

    @JsonProperty("name")
    private String name;
    @JsonProperty("comment")
    private String comment;
    /**
     * <p>Original spec-file type: FunctionPlace</p>
     * 
     * 
     */
    @JsonProperty("place")
    private FunctionPlace place;
    @JsonProperty("input")
    private List<us.kbase.catalog.Parameter> input;
    @JsonProperty("output")
    private List<us.kbase.catalog.Parameter> output;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Function withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Function withComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * <p>Original spec-file type: FunctionPlace</p>
     * 
     * 
     */
    @JsonProperty("place")
    public FunctionPlace getPlace() {
        return place;
    }

    /**
     * <p>Original spec-file type: FunctionPlace</p>
     * 
     * 
     */
    @JsonProperty("place")
    public void setPlace(FunctionPlace place) {
        this.place = place;
    }

    public Function withPlace(FunctionPlace place) {
        this.place = place;
        return this;
    }

    @JsonProperty("input")
    public List<us.kbase.catalog.Parameter> getInput() {
        return input;
    }

    @JsonProperty("input")
    public void setInput(List<us.kbase.catalog.Parameter> input) {
        this.input = input;
    }

    public Function withInput(List<us.kbase.catalog.Parameter> input) {
        this.input = input;
        return this;
    }

    @JsonProperty("output")
    public List<us.kbase.catalog.Parameter> getOutput() {
        return output;
    }

    @JsonProperty("output")
    public void setOutput(List<us.kbase.catalog.Parameter> output) {
        this.output = output;
    }

    public Function withOutput(List<us.kbase.catalog.Parameter> output) {
        this.output = output;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((((((((("Function"+" [name=")+ name)+", comment=")+ comment)+", place=")+ place)+", input=")+ input)+", output=")+ output)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
