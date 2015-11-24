
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
 * <p>Original spec-file type: TextSubdataOptions</p>
 * <pre>
 * Defines a parameter field that allows autocomplete based on 
 * subdata of an existing object.  For instance, selection of feature ids
 * from a Genome object.  It will appear as a text field with dropdown
 * similar to selection of other WS data objects.
 *     placeholder - placeholder text to display in the field
 *     multiselection - if true, then multiple selections are allowed in
 *                      a single input field.  This will override the
 *                      allow_multiple option (which allows user addition)
 *                      of additional fields.  If true, then this parameter
 *                      will return a list. Default= false
 *     show_src_obj - if true, then the dropdown will indicate the ids along
 *                    with some text indicating what data object the subdata
 *                    was retrieved from. Default=true
 *     allow_custom - if true, then user specified inputs not found in the
 *                    list are accepted.  if false, users can only select from
 *                    the valid list of selections. Default=false
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "placeholder",
    "multiselection",
    "show_src_obj",
    "allow_custom",
    "subdata_selection"
})
public class TextSubdataOptions {

    @JsonProperty("placeholder")
    private String placeholder;
    @JsonProperty("multiselection")
    private Long multiselection;
    @JsonProperty("show_src_obj")
    private Long showSrcObj;
    @JsonProperty("allow_custom")
    private Long allowCustom;
    /**
     * <p>Original spec-file type: SubdataSelection</p>
     * <pre>
     * Information about a subdata selection
     *     constant_ref - can be set as a fixed reference(s) to data objects
     *                    so that the dropdown is always populated with a particular
     *                    WS object - useful for say populating based on an ontology
     *                    or some other library of default terms, such as compounds
     *     parameter_id - pick the terms from a user specified parameter in the same
     *                    method
     *     path_to_subdata - specific path to a list or map that should be used to
     *                    populate the fields
     *     selection_id - If the path_to_subdata is to a list of objects, use this to
     *                    specify which field of that object should be used as the
     *                    primary ID
     *     selection_description - Use this to specify (if the subdata is a list or map)
     *                     which fields should be included as a short description of
     *                     the selection.  For features, for instance, this may include
     *                     the feature function, or feature aliases.
     *     description_template - Defines how the description of items is rendered using
     *                     Handlebar templates (use the name of items in the 
     *                     selection_description list as variable names)
     * </pre>
     * 
     */
    @JsonProperty("subdata_selection")
    private SubdataSelection subdataSelection;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("placeholder")
    public String getPlaceholder() {
        return placeholder;
    }

    @JsonProperty("placeholder")
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public TextSubdataOptions withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @JsonProperty("multiselection")
    public Long getMultiselection() {
        return multiselection;
    }

    @JsonProperty("multiselection")
    public void setMultiselection(Long multiselection) {
        this.multiselection = multiselection;
    }

    public TextSubdataOptions withMultiselection(Long multiselection) {
        this.multiselection = multiselection;
        return this;
    }

    @JsonProperty("show_src_obj")
    public Long getShowSrcObj() {
        return showSrcObj;
    }

    @JsonProperty("show_src_obj")
    public void setShowSrcObj(Long showSrcObj) {
        this.showSrcObj = showSrcObj;
    }

    public TextSubdataOptions withShowSrcObj(Long showSrcObj) {
        this.showSrcObj = showSrcObj;
        return this;
    }

    @JsonProperty("allow_custom")
    public Long getAllowCustom() {
        return allowCustom;
    }

    @JsonProperty("allow_custom")
    public void setAllowCustom(Long allowCustom) {
        this.allowCustom = allowCustom;
    }

    public TextSubdataOptions withAllowCustom(Long allowCustom) {
        this.allowCustom = allowCustom;
        return this;
    }

    /**
     * <p>Original spec-file type: SubdataSelection</p>
     * <pre>
     * Information about a subdata selection
     *     constant_ref - can be set as a fixed reference(s) to data objects
     *                    so that the dropdown is always populated with a particular
     *                    WS object - useful for say populating based on an ontology
     *                    or some other library of default terms, such as compounds
     *     parameter_id - pick the terms from a user specified parameter in the same
     *                    method
     *     path_to_subdata - specific path to a list or map that should be used to
     *                    populate the fields
     *     selection_id - If the path_to_subdata is to a list of objects, use this to
     *                    specify which field of that object should be used as the
     *                    primary ID
     *     selection_description - Use this to specify (if the subdata is a list or map)
     *                     which fields should be included as a short description of
     *                     the selection.  For features, for instance, this may include
     *                     the feature function, or feature aliases.
     *     description_template - Defines how the description of items is rendered using
     *                     Handlebar templates (use the name of items in the 
     *                     selection_description list as variable names)
     * </pre>
     * 
     */
    @JsonProperty("subdata_selection")
    public SubdataSelection getSubdataSelection() {
        return subdataSelection;
    }

    /**
     * <p>Original spec-file type: SubdataSelection</p>
     * <pre>
     * Information about a subdata selection
     *     constant_ref - can be set as a fixed reference(s) to data objects
     *                    so that the dropdown is always populated with a particular
     *                    WS object - useful for say populating based on an ontology
     *                    or some other library of default terms, such as compounds
     *     parameter_id - pick the terms from a user specified parameter in the same
     *                    method
     *     path_to_subdata - specific path to a list or map that should be used to
     *                    populate the fields
     *     selection_id - If the path_to_subdata is to a list of objects, use this to
     *                    specify which field of that object should be used as the
     *                    primary ID
     *     selection_description - Use this to specify (if the subdata is a list or map)
     *                     which fields should be included as a short description of
     *                     the selection.  For features, for instance, this may include
     *                     the feature function, or feature aliases.
     *     description_template - Defines how the description of items is rendered using
     *                     Handlebar templates (use the name of items in the 
     *                     selection_description list as variable names)
     * </pre>
     * 
     */
    @JsonProperty("subdata_selection")
    public void setSubdataSelection(SubdataSelection subdataSelection) {
        this.subdataSelection = subdataSelection;
    }

    public TextSubdataOptions withSubdataSelection(SubdataSelection subdataSelection) {
        this.subdataSelection = subdataSelection;
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
        return ((((((((((((("TextSubdataOptions"+" [placeholder=")+ placeholder)+", multiselection=")+ multiselection)+", showSrcObj=")+ showSrcObj)+", allowCustom=")+ allowCustom)+", subdataSelection=")+ subdataSelection)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
