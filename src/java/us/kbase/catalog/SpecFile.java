
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
 * <p>Original spec-file type: SpecFile</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "file_name",
    "content",
    "is_main"
})
public class SpecFile {

    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("content")
    private String content;
    @JsonProperty("is_main")
    private Long isMain;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("file_name")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SpecFile withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    public SpecFile withContent(String content) {
        this.content = content;
        return this;
    }

    @JsonProperty("is_main")
    public Long getIsMain() {
        return isMain;
    }

    @JsonProperty("is_main")
    public void setIsMain(Long isMain) {
        this.isMain = isMain;
    }

    public SpecFile withIsMain(Long isMain) {
        this.isMain = isMain;
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
        return ((((((((("SpecFile"+" [fileName=")+ fileName)+", content=")+ content)+", isMain=")+ isMain)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
