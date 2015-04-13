/*
PROM (Probabilistic Regulation of Metabolism) Service

This service enables the creation of FBA model constraints objects that are based on regulatory
networks and expression data, as described in [1].  Constraints are constructed by either automatically
aggregating necessary information from the CDS (if available for a given genome), or by adding user
expression and regulatory data.  PROM provides the capability to simulate transcription factor knockout
phenotypes.  PROM model constraint objects are created in a user's workspace, and can be operated on and
used in conjunction with an FBA model with the KBase FBA Modeling Service.

Note: for compatibility with the workspace service and legacy reasons, auth tokens are passed in as
parameters rather than handled automatically by the auto-generated client/server infrastructure.  This
will be fixed soon in one of the next builds.

[1] Chandrasekarana S. and Price ND. Probabilistic integrative modeling of genome-scale metabolic and
regulatory networks in Escherichia coli and Mycobacterium tuberculosis. PNAS (2010) 107:17845-50.

AUTHORS:
Michael Sneddon (mwsneddon@lbl.gov)
Matt DeJongh (dejongh@hope.edu)

created 11/27/2012 - msneddon
*/
module PROM
{

    /* ************************************************************************************* */
    /* * SIMPLE ID AND STRING TYPES **/
    /* ************************************************************************************* */

    /* indicates true or false values, false <= 0, true >=1 */
    typedef int bool;
    
    /* A KBase ID is a string starting with the characters "kb|".  KBase IDs are typed. The types are
    designated using a short string.  KBase IDs may be hierarchical.  See the standard KBase documentation
    for more information. */
    typedef string kbase_id;
    
    /* A KBase ID for a genome feature */
    typedef kbase_id feature_id;
    
    /* A KBase ID for a genome */
    typedef kbase_id genome_id;
    
    /* The name of a workspace */
    typedef string workspace_name;

    /* A workspace ID for a gene expression data object. */
    typedef string boolean_gene_expression_data_id;

    /* A workspace id for a set of expression data on/off calls */
    typedef string expression_data_collection_id;
    
    /* A workspace ID for a regulatory network object */
    typedef string regulatory_network_id;
    
    /* Status message used by this service to provide information on the final status of a step  */
    typedef string status;
    
    /* A workspace ID for the prom constraints object in a user's workpace */
    typedef string prom_constraints_id;
    
    /* A workspace ID for a genome object in a user's workspace, used to link a prom_constraint Object to a genome */
    typedef string genome_object_id;
    
    /* Specifies the source of a data object, e.g. KBase or MicrobesOnline */
    typedef string source;
    
    /* Specifies the ID of the data object in the source */
    typedef string source_id;
    
    /* The string representation of the bearer token needed to authenticate on the workspace service, this will eventually
    be eliminated when this service is updated to use the auto type-compiler auth functionality */
    typedef string auth_token;
    
    
    
    /* ************************************************************************************* */
    /* * EXPRESSION DATA TYPES * */
    /* ************************************************************************************* */
    
    /* Indicates on/off state of a gene, 1=on, -1=off, 0=unknown */
    typedef int on_off_state;
    
    /* A simplified representation of gene expression data under a SINGLE condition. Note that the condition
    information is not explicitly tracked here. also NOTE: this data object should be migrated to the Expression
    Data service, and simply imported here.
    
        mapping<feature_id,on_off_state> on_off_call - a mapping of genome features to on/off calls under the given
                                               condition (true=on, false=off).  It is therefore assumed that
                                               the features are protein coding genes.
        source expression_data_source        - the source of this collection of expression data
        source_id expression_data_source_id  - the id of this data object in the workspace
    */
    typedef structure {
        boolean_gene_expression_data_id id;
        mapping<feature_id,on_off_state> on_off_call;
        source expression_data_source;
        source expression_data_source_id;
    } boolean_gene_expression_data;
    
    /* A collection of gene expression data for a single genome under a range of conditions.  This data is returned
    as a list of IDs for boolean gene expression data objects in the workspace.  This is a simple object for creating
    a PROM Model. NOTE: this data object should be migrated to the Expression Data service, and simply imported here. */
    typedef structure {
        expression_data_collection_id id;
        list<boolean_gene_expression_data_id> expression_data_ids;
    } boolean_gene_expression_data_collection;
    
    
    
    /* ************************************************************************************* */
    /* * REGULATORY NETWORK TYPES * */
    /* ************************************************************************************* */

    /* A simplified representation of a regulatory interaction that also stores the probability of the interaction
    (specificially, as the probability the target is on given that the regulator is off), which is necessary for PROM
    to construct FBA constraints.  NOTE: this data object should be migrated to the Regulation service, and simply
    imported here. NOTE 2: feature_id may actually be a more general ID, as models can potentially be loaded that
    are not in the kbase namespace. In this case everything, including expression data and the fba model must be in
    the same namespace.
    
        feature_id TF            - the genome feature that is the regulator
        feature_id target        - the genome feature that is the target of regulation

    */
    typedef structure {
        feature_id TF;
        feature_id target;
    } regulatory_interaction;
    
    
    /* A collection of regulatory interactions that together form a regulatory network. This is an extremely
    simplified data object for use in constructing a PROM model.  NOTE: this data object should be migrated to
    the Regulation service, and simply imported here.
    */
    typedef list<regulatory_interaction> regulatory_network;
    
    
    
    /* ************************************************************************************* */
    /* * PROM CONSTRAINTS TYPE * */
    /* ************************************************************************************* */


    
    /*
    Object required by the prom_constraints object which defines the computed probabilities for a target gene.  The
    TF regulating this target can be deduced based on the tfMap object.
    
        string target_uuid        - id of the target gene in the annotation object namespace
        float tfOffProbability    - PROB(target=ON|TF=OFF)
                                    the probability that the transcriptional target is ON, given that the
                                    transcription factor is not expressed, as defined in Candrasekarana &
                                    Price, PNAS 2010 and used to predict cumulative effects of multiple
                                    regulatory interactions with a single target.  Set to null or empty if
                                    this probability has not been calculated yet.
        float probTTonGivenTFon   - PROB(target=ON|TF=ON)
                                    the probability that the transcriptional target is ON, given that the
                                    transcription factor is expressed.    Set to null or empty if
                                    this probability has not been calculated yet.
    */
    typedef structure {
        string target_uuid;
        float tfOnProbability;
        float tfOffProbability;
    } regulatory_target;
    
    /*
    Object required by the prom_constraints object, this maps a transcription factor by its uuid (in some
    annotation namespace) to a group of regulatory target genes.
    
        string transcriptionFactor_uuid                       - id of the TF in the annotation object namespace
        list <regulatory_target> transcriptionFactorMapTarget - collection of regulatory target genes for the TF
                                                                along with associated joint probabilities for each
                                                                target to be on given that the TF is on or off.
    */
    typedef structure {
        string transcriptionFactor_uuid;
        list <regulatory_target> transcriptionFactorMapTargets;
    } tfMap;
    
    
    /* the ID of the genome annotation object kept for reference in the prom_constraints object */
    typedef string annotation_uuid;
    
    /*
    An object that encapsulates the information necessary to apply PROM-based constraints to an FBA model. This
    includes a regulatory network consisting of a set of regulatory interactions (implied by the set of tfMap
    objects) and interaction probabilities as defined in each regulatory_target object.  A link the the annotation
    object is required in order to properly link to an FBA model object.  A reference to the expression_data_collection
    used to compute the interaction probabilities is provided for future reference.
    
        prom_constraints_id id                                         - the id of this prom_constraints object in a
                                                                        workspace
        annotation_uuid annotation_uuid                               - the id of the annotation object in the workspace
                                                                        which specfies how TFs and targets are named
        list <tfMap> transcriptionFactorMaps                          - the list of tfMaps which specifies both the
                                                                        regulatory network and interaction probabilities
                                                                        between TF and target genes
        expression_data_collection_id expression_data_collection_id   - the id of the expresion_data_collection object in
                                                                        the workspace which was used to compute the
                                                                        regulatory interaction probabilities
    */
    typedef structure {
        prom_constraints_id id;
        annotation_uuid annotation_uuid;
        list <tfMap> transcriptionFactorMaps;
        expression_data_collection_id expression_data_collection_id;
    } prom_contstraint;


    /* ************************************************************************************* */
    /* * METHODS * */
    /* ************************************************************************************* */

    /*
    This method fetches all gene expression data available in the CDS that is associated with the given genome id.  It then
    constructs an expression_data_collection object in the specified workspace.  The method returns the ID of the expression
    data collection in the workspace, along with a status message that provides details on what was retrieved and if anything
    failed.  If the method does fail, or if there is no data for the given genome, then no expression data collection is
    created and no ID is returned.
    
    Note 1: this method currently can take a long time to complete if there are many expression data sets in the CDS
    Note 2: the current implementation relies on on/off calls stored in the CDM (correct as of 1/2013).  This will almost
    certainly change, at which point logic for making on/off calls will be required as input
    Note 3: this method should be migrated to the expression service, which currently does not exist
    Note 4: this method should use the type compiler auth, but for simplicity  we now just pass an auth token directly.
    */
    funcdef get_expression_data_by_genome(genome_id genome_id, workspace_name workspace_name, auth_token token) returns (status status,expression_data_collection_id expression_data_collection_id);
    
    
    /*
    This method creates a new, empty, expression data collection in the specified workspace. If the method was successful,
    the ID of the expression data set will be returned.  The method also returns a status message providing additional
    details of the steps that occured or a message that indicates what failed.  If the method fails, no expression
    data ID is returned.
    */
    funcdef create_expression_data_collection(workspace_name workspace_name, auth_token token) returns (status status, expression_data_collection_id expression_data_collection_id);
    
    
    /*
    This method provides a way to attach a set of boolean expression data to an expression data collection object created
    in the current workspace.  Data collections can thus be composed of both CDS data and user data in this way.  The method
    returns a status message providing additional details of the steps that occured or a message that indicates what failed.
    If the method fails, then all updates to the expression_data_collection are not made, although some of the boolean gene
    expression data may have been created in the workspace (see status message for IDs of the new expession data objects).
    
    Note: when defining expression data, the id field must be explicitly defined.  This will be the ID used to save the expression
    data in the workspace.  If expression data with that ID already exists, this method will overwrite that data and you will
    have to use the workspace service revert method to undo the change.
    */
    funcdef add_expression_data_to_collection(list<boolean_gene_expression_data> expression_data, expression_data_collection_id expression_data_collecion_id, workspace_name workspace_name, auth_token token) returns (status status);
    
    
    /*
    Maps the expression data collection stored in a workspace in one genome namespace to an alternate genome namespace.  This is useful,
    for instance, if expression data is available for one genome, but you intend to use it for a related genome or a genome with different
    gene calls.  If a gene in the original expression data cannot be found in the translation mapping, then it is ignored and left as is
    so that the number of features in the expression data set is not altered.  NOTE!: this is different from the default behavior of
    change_regulatory_network_namespace, which will drop all genes that are not found in the mapping.  If successful, this method
    returns the expression collection ID of the newly created expression data colleion.  This method also returns a status message indicating
    what happened or what went wrong.
    
    The mapping<string,string> new_features_names should be defined so that existing IDs are the key and the replacement IDs are the
    values stored.
    */
    funcdef change_expression_data_namespace(expression_data_collection_id expression_data_collection_id, mapping<string,string> new_feature_names, workspace_name workspace_name, auth_token token) returns (status status);
    
    
    /*
    This method fetches a regulatory network from the regulation service that is associated with the given genome id.  If there
    are multiple regulome models available for the given genome, then the model with the most regulons is selected.  The method
    then constructs a regulatory network object in the specified workspace.  The method returns the ID of the regulatory network
    in the workspace, along with a status message that provides details on what was retrieved and if anything failed.  If the
    method does fail, or if there is no regulome for the given genome, then no regulatory network ID is returned.
    
    Note 1: this method should be migrated to the regulation service
    Note 2: this method should use the type compiler auth, but for simplicity  we now just pass an auth token directly.
    */
    funcdef get_regulatory_network_by_genome(genome_id genome_id, workspace_name workspace_name, auth_token token) returns (status status, regulatory_network_id regulatory_network_id);
    
    
    /*
    Maps the regulatory network stored in a workspace in one genome namespace to an alternate genome namespace.  This is useful,
    for instance, if a regulatory network was built and is available for one genome, but you intend to use it for
    a related genome or a genome with different gene calls.  If a gene in the original regulatory network cannot be found in
    the translation mapping, then it is simply removed from the new regulatory network.  Thus, if you are only changing the names
    of some genes, you still must provide an entry in the input mapping for the genes you wish to keep.  If successful, this method
    returns the regulatory network ID of the newly created regulatory network.  This method also returns a status message indicating
    what happened or what went wrong.
    
    The mapping<string,string> new_features_names should be defined so that existing IDs are the key and the replacement IDs are the
    values stored.    
    */
    funcdef change_regulatory_network_namespace(regulatory_network_id regulatory_network_id, mapping<string,string> new_feature_names, workspace_name workspace_name, auth_token token) returns (status status, regulatory_network_id new_regulatory_network_id);
    
    
    /*
    Named parameters for 'create_prom_constraints' method.  Currently all options are required.
    
        genome_object_id genome_object_id            - the workspace ID of the genome to link to the prom object
        expression_data_collection_id
                   expression_data_collection_id     - the workspace ID of the expression data collection needed to
                                                       build the PROM constraints.
        regulatory_network_id regulatory_network_id  - the workspace ID of the regulatory network data to use
        workspace_name workspace_name                - the name of the workspace to use
        auth_token token                             - the auth token that has permission to write in the specified workspace
    */
    typedef structure {
        genome_object_id genome_object_id;
        expression_data_collection_id expression_data_collection_id;
        regulatory_network_id regulatory_network_id;
        workspace_name workspace_name;
        auth_token token;
    } create_prom_constraints_parameters;
    
    
    /*
    This method creates a set of Prom constraints for a given genome annotation based on a regulatory network
    and a collection of gene expression data stored on a workspace.  Parameters are specified in the
    create_prom_constraints_parameters object.  A status object is returned indicating success or failure along
    with a message on what went wrong or statistics on the retrieved objects.  If the method was successful, the
    ID of the new Prom constraints object is also returned. The Prom constraints can then be used in conjunction
    with an FBA model using FBA Model Services.
    */
    funcdef create_prom_constraints(create_prom_constraints_parameters params) returns (status status, prom_constraints_id prom_constraints_id);
    
    
   
    
    
    /* methods not yet implemented, and not essential for end-to-end testing, but on the docket */
    /* funcdef merge_expression_data_collections(list <expression_data_collection_id> collections) returns (status,expression_data_collection_id); */
    /* funcdef add_regulatory_network(workspace_name, regulatory_network); */
    
    
};