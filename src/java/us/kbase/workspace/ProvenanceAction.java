
package us.kbase.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.kbase.common.service.UObject;

//TODO Gavin update the workspace jar and import that instead of copying code

/**
 * <p>Original spec-file type: ProvenanceAction</p>
 * <pre>
 * A provenance action.
 *         A provenance action (PA) is an action taken while transforming one data
 *         object to another. There may be several PAs taken in series. A PA is
 *         typically running a script, running an api command, etc. All of the
 *         following fields are optional, but more information provided equates to
 *         better data provenance.
 *         
 *         resolved_ws_objects should never be set by the user; it is set by the
 *         workspace service when returning data.
 *         
 *         The maximum size of the entire provenance object, including all actions,
 *         is 1MB.
 *         
 *         timestamp time - the time the action was started.
 *         string caller - the name or id of the invoker of this provenance
 *                 action. In most cases, this will be the same for all PAs.
 *         string service - the name of the service that performed this action.
 *         string service_ver - the version of the service that performed this action.
 *         string method - the method of the service that performed this action.
 *         list<UnspecifiedObject> method_params - the parameters of the method
 *                 that performed this action. If an object in the parameters is a
 *                 workspace object, also put the object reference in the
 *                 input_ws_object list.
 *         string script - the name of the script that performed this action.
 *         string script_ver - the version of the script that performed this action.
 *         string script_command_line - the command line provided to the script
 *                 that performed this action. If workspace objects were provided in
 *                 the command line, also put the object reference in the
 *                 input_ws_object list.
 *         list<obj_ref> input_ws_objects - the workspace objects that
 *                 were used as input to this action; typically these will also be
 *                 present as parts of the method_params or the script_command_line
 *                 arguments.
 *         list<obj_ref> resolved_ws_objects - the workspace objects ids from 
 *                 input_ws_objects resolved to permanent workspace object references
 *                 by the workspace service.
 *         list<string> intermediate_incoming - if the previous action produced 
 *                 output that 1) was not stored in a referrable way, and 2) is
 *                 used as input for this action, provide it with an arbitrary and
 *                 unique ID here, in the order of the input arguments to this action.
 *                 These IDs can be used in the method_params argument.
 *         list<string> intermediate_outgoing - if this action produced output
 *                 that 1) was not stored in a referrable way, and 2) is
 *                 used as input for the next action, provide it with an arbitrary and
 *                 unique ID here, in the order of the output values from this action.
 *                 These IDs can be used in the intermediate_incoming argument in the
 *                 next action.
 *         list<ExternalDataUnit> external_data - data external to the workspace
 *                 that was either imported to the workspace or used to create a
 *                 workspace object.
 *         list<SubAction> subactions - the subactions taken as a part of this
 *                 action.
 *         mapping<string, string> custom - user definable custom provenance
 *                 fields and their values.
 *         string description - a free text description of this action.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "time",
    "caller",
    "service",
    "service_ver",
    "method",
    "method_params",
    "script",
    "script_ver",
    "script_command_line",
    "input_ws_objects",
    "resolved_ws_objects",
    "intermediate_incoming",
    "intermediate_outgoing",
    "external_data",
    "subactions",
    "custom",
    "description"
})
public class ProvenanceAction {

    @JsonProperty("time")
    private java.lang.String time;
    @JsonProperty("caller")
    private java.lang.String caller;
    @JsonProperty("service")
    private java.lang.String service;
    @JsonProperty("service_ver")
    private java.lang.String serviceVer;
    @JsonProperty("method")
    private java.lang.String method;
    @JsonProperty("method_params")
    private List<UObject> methodParams;
    @JsonProperty("script")
    private java.lang.String script;
    @JsonProperty("script_ver")
    private java.lang.String scriptVer;
    @JsonProperty("script_command_line")
    private java.lang.String scriptCommandLine;
    @JsonProperty("input_ws_objects")
    private List<String> inputWsObjects;
    @JsonProperty("resolved_ws_objects")
    private List<String> resolvedWsObjects;
    @JsonProperty("intermediate_incoming")
    private List<String> intermediateIncoming;
    @JsonProperty("intermediate_outgoing")
    private List<String> intermediateOutgoing;
    @JsonProperty("external_data")
    private List<ExternalDataUnit> externalData;
    @JsonProperty("subactions")
    private List<SubAction> subactions;
    @JsonProperty("custom")
    private Map<String, String> custom;
    @JsonProperty("description")
    private java.lang.String description;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("time")
    public java.lang.String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(java.lang.String time) {
        this.time = time;
    }

    public ProvenanceAction withTime(java.lang.String time) {
        this.time = time;
        return this;
    }

    @JsonProperty("caller")
    public java.lang.String getCaller() {
        return caller;
    }

    @JsonProperty("caller")
    public void setCaller(java.lang.String caller) {
        this.caller = caller;
    }

    public ProvenanceAction withCaller(java.lang.String caller) {
        this.caller = caller;
        return this;
    }

    @JsonProperty("service")
    public java.lang.String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(java.lang.String service) {
        this.service = service;
    }

    public ProvenanceAction withService(java.lang.String service) {
        this.service = service;
        return this;
    }

    @JsonProperty("service_ver")
    public java.lang.String getServiceVer() {
        return serviceVer;
    }

    @JsonProperty("service_ver")
    public void setServiceVer(java.lang.String serviceVer) {
        this.serviceVer = serviceVer;
    }

    public ProvenanceAction withServiceVer(java.lang.String serviceVer) {
        this.serviceVer = serviceVer;
        return this;
    }

    @JsonProperty("method")
    public java.lang.String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(java.lang.String method) {
        this.method = method;
    }

    public ProvenanceAction withMethod(java.lang.String method) {
        this.method = method;
        return this;
    }

    @JsonProperty("method_params")
    public List<UObject> getMethodParams() {
        return methodParams;
    }

    @JsonProperty("method_params")
    public void setMethodParams(List<UObject> methodParams) {
        this.methodParams = methodParams;
    }

    public ProvenanceAction withMethodParams(List<UObject> methodParams) {
        this.methodParams = methodParams;
        return this;
    }

    @JsonProperty("script")
    public java.lang.String getScript() {
        return script;
    }

    @JsonProperty("script")
    public void setScript(java.lang.String script) {
        this.script = script;
    }

    public ProvenanceAction withScript(java.lang.String script) {
        this.script = script;
        return this;
    }

    @JsonProperty("script_ver")
    public java.lang.String getScriptVer() {
        return scriptVer;
    }

    @JsonProperty("script_ver")
    public void setScriptVer(java.lang.String scriptVer) {
        this.scriptVer = scriptVer;
    }

    public ProvenanceAction withScriptVer(java.lang.String scriptVer) {
        this.scriptVer = scriptVer;
        return this;
    }

    @JsonProperty("script_command_line")
    public java.lang.String getScriptCommandLine() {
        return scriptCommandLine;
    }

    @JsonProperty("script_command_line")
    public void setScriptCommandLine(java.lang.String scriptCommandLine) {
        this.scriptCommandLine = scriptCommandLine;
    }

    public ProvenanceAction withScriptCommandLine(java.lang.String scriptCommandLine) {
        this.scriptCommandLine = scriptCommandLine;
        return this;
    }

    @JsonProperty("input_ws_objects")
    public List<String> getInputWsObjects() {
        return inputWsObjects;
    }

    @JsonProperty("input_ws_objects")
    public void setInputWsObjects(List<String> inputWsObjects) {
        this.inputWsObjects = inputWsObjects;
    }

    public ProvenanceAction withInputWsObjects(List<String> inputWsObjects) {
        this.inputWsObjects = inputWsObjects;
        return this;
    }

    @JsonProperty("resolved_ws_objects")
    public List<String> getResolvedWsObjects() {
        return resolvedWsObjects;
    }

    @JsonProperty("resolved_ws_objects")
    public void setResolvedWsObjects(List<String> resolvedWsObjects) {
        this.resolvedWsObjects = resolvedWsObjects;
    }

    public ProvenanceAction withResolvedWsObjects(List<String> resolvedWsObjects) {
        this.resolvedWsObjects = resolvedWsObjects;
        return this;
    }

    @JsonProperty("intermediate_incoming")
    public List<String> getIntermediateIncoming() {
        return intermediateIncoming;
    }

    @JsonProperty("intermediate_incoming")
    public void setIntermediateIncoming(List<String> intermediateIncoming) {
        this.intermediateIncoming = intermediateIncoming;
    }

    public ProvenanceAction withIntermediateIncoming(List<String> intermediateIncoming) {
        this.intermediateIncoming = intermediateIncoming;
        return this;
    }

    @JsonProperty("intermediate_outgoing")
    public List<String> getIntermediateOutgoing() {
        return intermediateOutgoing;
    }

    @JsonProperty("intermediate_outgoing")
    public void setIntermediateOutgoing(List<String> intermediateOutgoing) {
        this.intermediateOutgoing = intermediateOutgoing;
    }

    public ProvenanceAction withIntermediateOutgoing(List<String> intermediateOutgoing) {
        this.intermediateOutgoing = intermediateOutgoing;
        return this;
    }

    @JsonProperty("external_data")
    public List<ExternalDataUnit> getExternalData() {
        return externalData;
    }

    @JsonProperty("external_data")
    public void setExternalData(List<ExternalDataUnit> externalData) {
        this.externalData = externalData;
    }

    public ProvenanceAction withExternalData(List<ExternalDataUnit> externalData) {
        this.externalData = externalData;
        return this;
    }

    @JsonProperty("subactions")
    public List<SubAction> getSubactions() {
        return subactions;
    }

    @JsonProperty("subactions")
    public void setSubactions(List<SubAction> subactions) {
        this.subactions = subactions;
    }

    public ProvenanceAction withSubactions(List<SubAction> subactions) {
        this.subactions = subactions;
        return this;
    }

    @JsonProperty("custom")
    public Map<String, String> getCustom() {
        return custom;
    }

    @JsonProperty("custom")
    public void setCustom(Map<String, String> custom) {
        this.custom = custom;
    }

    public ProvenanceAction withCustom(Map<String, String> custom) {
        this.custom = custom;
        return this;
    }

    @JsonProperty("description")
    public java.lang.String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public ProvenanceAction withDescription(java.lang.String description) {
        this.description = description;
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
        return ((((((((((((((((((((((((((((((((((((("ProvenanceAction"+" [time=")+ time)+", caller=")+ caller)+", service=")+ service)+", serviceVer=")+ serviceVer)+", method=")+ method)+", methodParams=")+ methodParams)+", script=")+ script)+", scriptVer=")+ scriptVer)+", scriptCommandLine=")+ scriptCommandLine)+", inputWsObjects=")+ inputWsObjects)+", resolvedWsObjects=")+ resolvedWsObjects)+", intermediateIncoming=")+ intermediateIncoming)+", intermediateOutgoing=")+ intermediateOutgoing)+", externalData=")+ externalData)+", subactions=")+ subactions)+", custom=")+ custom)+", description=")+ description)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
