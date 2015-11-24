
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
 * <p>Original spec-file type: MethodSpec</p>
 * <pre>
 * The method specification which should provide enough information to render a default
 * input widget for the method.
 * replacement_text indicates the text that should replace the input boxes after the method
 * has run.  You can refer to parameters by putting them in double curly braces (on the front
 * end we will use the handlebars library).
 *    for example:  Ran flux balance analysis on model {{model_param}} with parameter 2 set to {{param2}}.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "info",
    "replacement_text",
    "widgets",
    "parameters",
    "fixed_parameters",
    "behavior",
    "job_id_output_field"
})
public class MethodSpec {

    /**
     * <p>Original spec-file type: MethodBriefInfo</p>
     * <pre>
     * Minimal information about a method suitable for displaying the method in a menu or navigator. 
     * input_types and output_types - sets of valid_ws_types occured in input/output parameters.
     * </pre>
     * 
     */
    @JsonProperty("info")
    private MethodBriefInfo info;
    @JsonProperty("replacement_text")
    private String replacementText;
    /**
     * <p>Original spec-file type: WidgetSpec</p>
     * <pre>
     * specify the input / ouput widgets used for rendering
     * </pre>
     * 
     */
    @JsonProperty("widgets")
    private WidgetSpec widgets;
    @JsonProperty("parameters")
    private List<MethodParameter> parameters;
    @JsonProperty("fixed_parameters")
    private List<FixedMethodParameter> fixedParameters;
    /**
     * <p>Original spec-file type: MethodBehavior</p>
     * <pre>
     * Determines how the method is handled when run.
     * kb_service_name - name of service which will be part of fully qualified method name, optional field (in
     *     case it's not defined developer should enter fully qualified name with dot into 'kb_service_method'.
     * kb_service_version - optional git commit hash defining version of repo registered dynamically.
     * kb_service_input_mapping - mapping from input parameters to input service method arguments.
     * kb_service_output_mapping - mapping from output of service method to final output of narrative method.
     * output_mapping - mapping from input to final output of narrative method to support steps without back-end operations.
     * kb_service_input_mapping - mapping from input parameters to input service method arguments.
     * kb_service_output_mapping - mapping from output of service method to final output of narrative method.
     * @optional python_function kb_service_name kb_service_method kb_service_input_mapping kb_service_output_mapping
     * </pre>
     * 
     */
    @JsonProperty("behavior")
    private MethodBehavior behavior;
    @JsonProperty("job_id_output_field")
    private String jobIdOutputField;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * <p>Original spec-file type: MethodBriefInfo</p>
     * <pre>
     * Minimal information about a method suitable for displaying the method in a menu or navigator. 
     * input_types and output_types - sets of valid_ws_types occured in input/output parameters.
     * </pre>
     * 
     */
    @JsonProperty("info")
    public MethodBriefInfo getInfo() {
        return info;
    }

    /**
     * <p>Original spec-file type: MethodBriefInfo</p>
     * <pre>
     * Minimal information about a method suitable for displaying the method in a menu or navigator. 
     * input_types and output_types - sets of valid_ws_types occured in input/output parameters.
     * </pre>
     * 
     */
    @JsonProperty("info")
    public void setInfo(MethodBriefInfo info) {
        this.info = info;
    }

    public MethodSpec withInfo(MethodBriefInfo info) {
        this.info = info;
        return this;
    }

    @JsonProperty("replacement_text")
    public String getReplacementText() {
        return replacementText;
    }

    @JsonProperty("replacement_text")
    public void setReplacementText(String replacementText) {
        this.replacementText = replacementText;
    }

    public MethodSpec withReplacementText(String replacementText) {
        this.replacementText = replacementText;
        return this;
    }

    /**
     * <p>Original spec-file type: WidgetSpec</p>
     * <pre>
     * specify the input / ouput widgets used for rendering
     * </pre>
     * 
     */
    @JsonProperty("widgets")
    public WidgetSpec getWidgets() {
        return widgets;
    }

    /**
     * <p>Original spec-file type: WidgetSpec</p>
     * <pre>
     * specify the input / ouput widgets used for rendering
     * </pre>
     * 
     */
    @JsonProperty("widgets")
    public void setWidgets(WidgetSpec widgets) {
        this.widgets = widgets;
    }

    public MethodSpec withWidgets(WidgetSpec widgets) {
        this.widgets = widgets;
        return this;
    }

    @JsonProperty("parameters")
    public List<MethodParameter> getParameters() {
        return parameters;
    }

    @JsonProperty("parameters")
    public void setParameters(List<MethodParameter> parameters) {
        this.parameters = parameters;
    }

    public MethodSpec withParameters(List<MethodParameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    @JsonProperty("fixed_parameters")
    public List<FixedMethodParameter> getFixedParameters() {
        return fixedParameters;
    }

    @JsonProperty("fixed_parameters")
    public void setFixedParameters(List<FixedMethodParameter> fixedParameters) {
        this.fixedParameters = fixedParameters;
    }

    public MethodSpec withFixedParameters(List<FixedMethodParameter> fixedParameters) {
        this.fixedParameters = fixedParameters;
        return this;
    }

    /**
     * <p>Original spec-file type: MethodBehavior</p>
     * <pre>
     * Determines how the method is handled when run.
     * kb_service_name - name of service which will be part of fully qualified method name, optional field (in
     *     case it's not defined developer should enter fully qualified name with dot into 'kb_service_method'.
     * kb_service_version - optional git commit hash defining version of repo registered dynamically.
     * kb_service_input_mapping - mapping from input parameters to input service method arguments.
     * kb_service_output_mapping - mapping from output of service method to final output of narrative method.
     * output_mapping - mapping from input to final output of narrative method to support steps without back-end operations.
     * kb_service_input_mapping - mapping from input parameters to input service method arguments.
     * kb_service_output_mapping - mapping from output of service method to final output of narrative method.
     * @optional python_function kb_service_name kb_service_method kb_service_input_mapping kb_service_output_mapping
     * </pre>
     * 
     */
    @JsonProperty("behavior")
    public MethodBehavior getBehavior() {
        return behavior;
    }

    /**
     * <p>Original spec-file type: MethodBehavior</p>
     * <pre>
     * Determines how the method is handled when run.
     * kb_service_name - name of service which will be part of fully qualified method name, optional field (in
     *     case it's not defined developer should enter fully qualified name with dot into 'kb_service_method'.
     * kb_service_version - optional git commit hash defining version of repo registered dynamically.
     * kb_service_input_mapping - mapping from input parameters to input service method arguments.
     * kb_service_output_mapping - mapping from output of service method to final output of narrative method.
     * output_mapping - mapping from input to final output of narrative method to support steps without back-end operations.
     * kb_service_input_mapping - mapping from input parameters to input service method arguments.
     * kb_service_output_mapping - mapping from output of service method to final output of narrative method.
     * @optional python_function kb_service_name kb_service_method kb_service_input_mapping kb_service_output_mapping
     * </pre>
     * 
     */
    @JsonProperty("behavior")
    public void setBehavior(MethodBehavior behavior) {
        this.behavior = behavior;
    }

    public MethodSpec withBehavior(MethodBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    @JsonProperty("job_id_output_field")
    public String getJobIdOutputField() {
        return jobIdOutputField;
    }

    @JsonProperty("job_id_output_field")
    public void setJobIdOutputField(String jobIdOutputField) {
        this.jobIdOutputField = jobIdOutputField;
    }

    public MethodSpec withJobIdOutputField(String jobIdOutputField) {
        this.jobIdOutputField = jobIdOutputField;
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
        return ((((((((((((((((("MethodSpec"+" [info=")+ info)+", replacementText=")+ replacementText)+", widgets=")+ widgets)+", parameters=")+ parameters)+", fixedParameters=")+ fixedParameters)+", behavior=")+ behavior)+", jobIdOutputField=")+ jobIdOutputField)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
