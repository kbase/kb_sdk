
package us.kbase.narrativemethodstore;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "constant_ref",
    "parameter_id",
    "subdata_included",
    "path_to_subdata",
    "selection_id",
    "selection_description",
    "description_template"
})
public class SubdataSelection {

    @JsonProperty("constant_ref")
    private List<String> constantRef;
    @JsonProperty("parameter_id")
    private java.lang.String parameterId;
    @JsonProperty("subdata_included")
    private List<String> subdataIncluded;
    @JsonProperty("path_to_subdata")
    private List<String> pathToSubdata;
    @JsonProperty("selection_id")
    private java.lang.String selectionId;
    @JsonProperty("selection_description")
    private List<String> selectionDescription;
    @JsonProperty("description_template")
    private java.lang.String descriptionTemplate;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("constant_ref")
    public List<String> getConstantRef() {
        return constantRef;
    }

    @JsonProperty("constant_ref")
    public void setConstantRef(List<String> constantRef) {
        this.constantRef = constantRef;
    }

    public SubdataSelection withConstantRef(List<String> constantRef) {
        this.constantRef = constantRef;
        return this;
    }

    @JsonProperty("parameter_id")
    public java.lang.String getParameterId() {
        return parameterId;
    }

    @JsonProperty("parameter_id")
    public void setParameterId(java.lang.String parameterId) {
        this.parameterId = parameterId;
    }

    public SubdataSelection withParameterId(java.lang.String parameterId) {
        this.parameterId = parameterId;
        return this;
    }

    @JsonProperty("subdata_included")
    public List<String> getSubdataIncluded() {
        return subdataIncluded;
    }

    @JsonProperty("subdata_included")
    public void setSubdataIncluded(List<String> subdataIncluded) {
        this.subdataIncluded = subdataIncluded;
    }

    public SubdataSelection withSubdataIncluded(List<String> subdataIncluded) {
        this.subdataIncluded = subdataIncluded;
        return this;
    }

    @JsonProperty("path_to_subdata")
    public List<String> getPathToSubdata() {
        return pathToSubdata;
    }

    @JsonProperty("path_to_subdata")
    public void setPathToSubdata(List<String> pathToSubdata) {
        this.pathToSubdata = pathToSubdata;
    }

    public SubdataSelection withPathToSubdata(List<String> pathToSubdata) {
        this.pathToSubdata = pathToSubdata;
        return this;
    }

    @JsonProperty("selection_id")
    public java.lang.String getSelectionId() {
        return selectionId;
    }

    @JsonProperty("selection_id")
    public void setSelectionId(java.lang.String selectionId) {
        this.selectionId = selectionId;
    }

    public SubdataSelection withSelectionId(java.lang.String selectionId) {
        this.selectionId = selectionId;
        return this;
    }

    @JsonProperty("selection_description")
    public List<String> getSelectionDescription() {
        return selectionDescription;
    }

    @JsonProperty("selection_description")
    public void setSelectionDescription(List<String> selectionDescription) {
        this.selectionDescription = selectionDescription;
    }

    public SubdataSelection withSelectionDescription(List<String> selectionDescription) {
        this.selectionDescription = selectionDescription;
        return this;
    }

    @JsonProperty("description_template")
    public java.lang.String getDescriptionTemplate() {
        return descriptionTemplate;
    }

    @JsonProperty("description_template")
    public void setDescriptionTemplate(java.lang.String descriptionTemplate) {
        this.descriptionTemplate = descriptionTemplate;
    }

    public SubdataSelection withDescriptionTemplate(java.lang.String descriptionTemplate) {
        this.descriptionTemplate = descriptionTemplate;
        return this;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public java.lang.String toString() {
        return ((((((((((((((((("SubdataSelection"+" [constantRef=")+ constantRef)+", parameterId=")+ parameterId)+", subdataIncluded=")+ subdataIncluded)+", pathToSubdata=")+ pathToSubdata)+", selectionId=")+ selectionId)+", selectionDescription=")+ selectionDescription)+", descriptionTemplate=")+ descriptionTemplate)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
