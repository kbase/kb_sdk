
package us.kbase.narrativemethodstore;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: RegexMatcher</p>
 * <pre>
 * regex - regular expression in javascript syntax
 * error_text - message displayed if the input does not statisfy this constraint
 * match - set to 1 to check if the input matches this regex, set to 0 to check
 *         if input does not match this regex.  default is 1
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "regex",
    "error_text",
    "match"
})
public class RegexMatcher {

    @JsonProperty("regex")
    private String regex;
    @JsonProperty("error_text")
    private String errorText;
    @JsonProperty("match")
    private Long match;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("regex")
    public String getRegex() {
        return regex;
    }

    @JsonProperty("regex")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    public RegexMatcher withRegex(String regex) {
        this.regex = regex;
        return this;
    }

    @JsonProperty("error_text")
    public String getErrorText() {
        return errorText;
    }

    @JsonProperty("error_text")
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public RegexMatcher withErrorText(String errorText) {
        this.errorText = errorText;
        return this;
    }

    @JsonProperty("match")
    public Long getMatch() {
        return match;
    }

    @JsonProperty("match")
    public void setMatch(Long match) {
        this.match = match;
    }

    public RegexMatcher withMatch(Long match) {
        this.match = match;
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
        return ((((((((("RegexMatcher"+" [regex=")+ regex)+", errorText=")+ errorText)+", match=")+ match)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
