/*
=head1 fbaModelServices

=head2 SYNOPSIS

The FBA Model Services include support related to the reconstruction, curation,
reconciliation, and analysis of metabolic models. This includes commands to:

1.) Load genome typed objects into a workspace

2.) Build a model from a genome typed object and curate the model

3.) Analyze a model with flux balance analysis

4.) Simulate and reconcile a model to an imported set of growth phenotype data

=head2 EXAMPLE OF API USE IN PERL

To use the API, first you need to instantiate a fbaModelServices client object:

my $client = Bio::KBase::fbaModelServices::Client->new;
   
Next, you can run API commands on the client object:
   
my $objmeta = $client->genome_to_workspace({
	genome => "kb|g.0",
	workspace => "myWorkspace"
});
my $objmeta = $client->genome_to_fbamodel({
	model => "myModel"
	workspace => "myWorkspace"
});

=head2 AUTHENTICATION

Each and every function in this service takes a hash reference as
its single argument. This hash reference may contain a key
C<auth> whose value is a bearer token for the user making
the request. If this is not provided a default user "public" is assumed.

=head2 WORKSPACE

A workspace is a named collection of objects owned by a specific
user, that may be viewable or editable by other users.Functions that operate
on workspaces take a C<workspace_id>, which is an alphanumeric string that
uniquely identifies a workspace among all workspaces.

*/
module fbaModelServices {
    /*********************************************************************************
    Universal simple type definitions
   	*********************************************************************************/
    /* indicates true or false values, false <= 0, true >=1 */
    typedef int bool;
    
    /* A string used as an ID for a workspace. Any string consisting of alphanumeric characters and "-" is acceptable  */
    typedef string workspace_id;
	
	/* A string indicating the "type" of an object stored in a workspace. Acceptable types are returned by the "get_types()" command in the workspace_service  */
	typedef string object_type;
	
	/* ID of an object stored in the workspace. Any string consisting of alphanumeric characters and "-" is acceptable */
	typedef string object_id;
	
	/* Login name of KBase useraccount to which permissions for workspaces are mapped */
	typedef string username;
	
	/* Exact time for workspace operations. e.g. 2012-12-17T23:24:06 */
	typedef string timestamp;
	
	/* An identifier for compounds in the KBase biochemistry database. e.g. cpd00001 */
	typedef string compound_id;
    
    /* A string used to identify a particular biochemistry database object in KBase. e.g. "default" is the ID of the standard KBase biochemistry */
    typedef string biochemistry_id;
    
    /* A string identifier for a genome in KBase. e.g. "kb|g.0" is the ID for E. coli */
    typedef string genome_id;
    
    /* A string identifier for a prommodel in KBase. */
    typedef string prommodel_id;
    
    /* A string identifier for a contiguous piece of DNA in KBase, representing a chromosome or an assembled fragment */
    typedef string contig_id;
    
    /* A string specifying the type of genome features in KBase */
    typedef string feature_type;
    
    /* A string identifier used for compartments in models in KBase. Compartments could represet organelles in a eukaryotic model, or entire cells in a community model */
    typedef string modelcompartment_id;
    
    /* A string identifier used for compounds in models in KBase. */
    typedef string modelcompound_id;
    
    /* A string identifier used for a feature in a genome. */
    typedef string feature_id;
    
    /* A string identifier used for a reaction in a KBase biochemistry. */
    typedef string reaction_id;
    
    /* A string identifier used for a reaction in a model in KBase. */
    typedef string modelreaction_id;
    
    /* A string identifier used for a biomass reaction in a KBase model. */
    typedef string biomass_id;
    
    /* A string identifier used for a media condition in the KBase database. */
    typedef string media_id;
    
    /* A string identifier used for a flux balance analysis study in KBase. */
    typedef string fba_id;
    
    /* A string identifier for a gap generation study in KBase. */
    typedef string gapgen_id;
    
    /* A string identifier for a gap filling study in KBase. */
    typedef string gapfill_id;
    
    /* A string identifier for a solution from a gap generation study in KBase. */
    typedef string gapgensolution_id;
    
    /* A string identifier for a solution from a gap filling study in KBase. */
    typedef string gapfillsolution_id;
    
    /* A string identifier for a metabolic model in KBase. */
    typedef string fbamodel_id;
    
    /* A string identifier for a Mapping object in KBase. */
    typedef string mapping_id;
    
    /* A string identifier for a regulatory model in KBase. */
    typedef string regmodel_id;
    
    /* A string identifier for a compartment in KBase. */
    typedef string compartment_id;
    
    /* A string identifier for an expression dataset in KBase. */
    typedef string expression_id;
    
    /* A string identifier used for a set of phenotype data loaded into KBase. */
    typedef string phenotypeSet_id;
    
    /* A permanent reference to an object in a workspace. */
    typedef string workspace_ref;
    
    /* A string identifier used for a probabilistic annotation in KBase. */
    typedef string probanno_id;
    
    /*********************************************************************************
    Object type definition
   	*********************************************************************************/
    /* Meta data associated with an object stored in a workspace.
	
		object_id id - ID of the object assigned by the user or retreived from the IDserver (e.g. kb|g.0)
		object_type type - type of the object (e.g. Genome)
		timestamp moddate - date when the object was modified by the user (e.g. 2012-12-17T23:24:06)
		int instance - instance of the object, which is equal to the number of times the user has overwritten the object
		timestamp date_created - time at which the alignment was built/loaded in seconds since the epoch
		string command - name of the command last used to modify or create the object
		username lastmodifier - name of the user who last modified the object
		username owner - name of the user who owns (who created) this object
		workspace_id workspace - ID of the workspace in which the object is currently stored
		workspace_ref ref - a 36 character ID that provides permanent undeniable access to this specific instance of this object
		string chsum - checksum of the associated data object
		mapping<string,string> metadata - custom metadata entered for data object during save operation 
	
	*/
	typedef tuple<object_id id,object_type type,timestamp moddate,int instance,string command,username lastmodifier,username owner,workspace_id workspace,workspace_ref ref,string chsum,mapping<string,string> metadata> object_metadata;
    
    /*********************************************************************************
    Probabilistic Annotation type definition
   	*********************************************************************************/
    typedef string md5;
    typedef list<md5> md5s;
    /* A region of DNA is maintained as a tuple of four components:

        the contig
        the beginning position (from 1)
        the strand
        the length

        We often speak of "a region".  By "location", we mean a sequence
        of regions from the same genome (perhaps from distinct contigs).
    */
    typedef tuple<contig_id, int begin, string strand,int length> region_of_dna;

    /*
        a "location" refers to a sequence of regions
    */
    typedef list<region_of_dna> location;
    typedef tuple<string comment, string annotator, int annotation_time> annotation;
    typedef tuple<feature_id gene, float blast_score> gene_hit;
    typedef tuple<string function, float probability, list<gene_hit> gene_hits > alt_func;

    typedef structure {
		feature_id id;
		location location;
		feature_type type;
		string function;
		list<alt_func> alternative_functions;
		string protein_translation;
		list<string> aliases;
		list<annotation> annotations;
    } feature;

    typedef structure {
		contig_id id;
		string dna;
    } contig;

    typedef structure {
		genome_id id;
		string scientific_name;
		string domain;
		int genetic_code;
		string source;
		string source_id;
		list<contig> contigs;
		list<feature> features;
    } GenomeObject;
        
    /* Data structures to hold a single annotation probability for a single gene
		
		feature_id feature - feature the annotation is associated with
		string function - the name of the functional role being annotated to the feature
		float probability - the probability that the functional role is associated with the feature

	*/
    typedef tuple<feature_id feature, string function,float probability> annotationProbability; 
    
    typedef string probanno_id;
    typedef tuple<string function, float probability> alt_func;
    
    /*
        Object to carry alternative functions for each feature
    
        feature_id id
        ID of the feature. Required.
    
        string function
        Primary annotated function of the feature in the genome annotation. Required.
    
        list<alt_func> alternative_functions
        List of tuples containing alternative functions and probabilities. Required.
    */
    typedef structure {
		feature_id id;
		list<alt_func> alternative_functions;
    } ProbAnnoFeature;
    
    /* Object to carry alternative functions and probabilities for genes in a genome
    
        probanno_id id - ID of the probabilistic annotation object. Required.    
        genome_id genome - ID of the genome the probabilistic annotation was built for. Required.
        workspace_ref genome_uuid - Reference to retrieve genome from workspace service. Required.
        list<ProbAnnoFeature> featureAlternativeFunctions - List of ProbAnnoFeature objects holding alternative functions for features. Required.
    
    */
    typedef structure {
		probanno_id id;
		genome_id genome;
		workspace_ref genome_uuid;
		list<ProbAnnoFeature> featureAlternativeFunctions;
    } ProbabilisticAnnotation;
    
    /*********************************************************************************
    Biochemistry type definition
   	*********************************************************************************/
    /* Data structures for biochemistry database
		
		biochemistry_id id - ID of biochemistry database
		string name - name of biochemistry database
		list<compound_id> compounds - list of compound IDs in biochemistry
		list<reaction_id> reactions - list of reaction IDs in biochemistry
		list<media_id> media - list of media formulations in biochemistry
						
	*/
    typedef structure {
		biochemistry_id id;
		string name;
		list<compound_id> compounds;
		list<reaction_id> reactions;
		list<media_id> media;
    } Biochemistry;
    
    /* Data structures for media formulation
		
		media_id id - ID of media formulation
		string name - name of media formulaiton
		list<compound_id> compounds - list of compounds in media formulation
		list<float> concentrations - list of compound concentrations
		float pH - pH of media condition
		float temperature - temperature of media condition
						
	*/
    typedef structure {
		media_id id;
		string name;
		list<compound_id> compounds;
		list<float> concentrations;
		float pH;
		float temperature;
    } Media;
    
    /* Data structures for media formulation
		
		compound_id id - ID of compound
		string abbrev - abbreviated name of compound
		string name - primary name of compound
		list<string> aliases - list of aliases for compound
		float charge - molecular charge of compound
		float deltaG - estimated compound delta G
		float deltaGErr - uncertainty in estimated compound delta G
		string formula - molecular formula of compound
						
	*/
    typedef structure {
		compound_id id;
		string abbrev;
		string name;
		list<string> aliases;
		float charge;
		float deltaG;
		float deltaGErr;
		string formula;
    } Compound;
    
    /* Data structures for media formulation
		
		reaction_id id - ID of reaction
		string name - primary name of reaction
		string abbrev - abbreviated name of reaction
		list<string> enzymes - list of EC numbers for reaction
		string direction - directionality of reaction
		string reversibility - reversibility of reaction
		float deltaG - estimated delta G of reaction
		float deltaGErr - uncertainty in estimated delta G of reaction
		string equation - reaction equation in terms of compound IDs
		string definition - reaction equation in terms of compound names
						
	*/
    typedef structure {
		reaction_id id;
		string name;
		string abbrev;
		list<string> enzymes;
		string direction;
		string reversibility;
		float deltaG;
		float deltaGErr;
		string equation;
		string definition;	
    } Reaction;
    /*********************************************************************************
    FBAModel type definition
   	*********************************************************************************/
    /* Data structures for a compartment in a model
		
		modelcompartment_id id - ID of the compartment
		string name - name of the compartment
		float pH - pH of the compartment
		float potential - electrochemical potential of the compartment
		int index - index of the compartment; multiple compartments of the same type can be in a model
						
	*/
    typedef structure {
		modelcompartment_id id;
		string name;
		float pH;
		float potential;
		int index;
    } ModelCompartment;
    
    /* Data structures for a compound in a model
		
		modelcompound_id id - ID of the specific instance of the compound in the model
		compound_id compound - ID of the compound associated with the model compound
		string name - name of the compound associated with the model compound
		modelcompartment_id compartment - ID of the compartment containing the compound
								
	*/
    typedef structure {
		modelcompound_id id;
		compound_id compound;
		string name;
		modelcompartment_id compartment;
    } ModelCompound;
    
    /* Data structures for a reaction in a model
		
		modelreaction_id id - ID of the specific instance of the reaction in the model
		reaction_id reaction - ID of the reaction
		string name - name of the reaction
		string direction - directionality of the reaction
		string equation - stoichiometric equation of the reaction in terms of compound IDs
		string definition - stoichiometric equation of the reaction in terms of compound names
		list<feature_id> features - list of features associated with the reaction
		modelcompartment_id compartment - ID of the compartment containing the reaction
								
	*/
    typedef structure {
		modelreaction_id id;
		reaction_id reaction;
		string name;
		string direction;
		string equation;
		string definition;
		list<feature_id> features;
		modelcompartment_id compartment;
    } ModelReaction;
    
    /* Data structures for a reaction in a model
		
		modelcompound_id modelcompound - ID of model compound in biomass reaction
		float coefficient - coefficient of compound in biomass reaction
		string name - name of compound in biomass reaction
								
	*/
    typedef tuple<modelcompound_id modelcompound,float coefficient,string name> BiomassCompound;
    
    /* Data structures for a reaction in a model
		
		biomass_id id - ID of biomass reaction
		string name - name of biomass reaction
		string definition - stoichiometric equation of biomass reaction in terms of compound names
		list<BiomassCompound> biomass_compounds - list of compounds in biomass reaction
								
	*/
    typedef structure {
		biomass_id id;
		string name;
		string definition;
		list<BiomassCompound> biomass_compounds;
    } ModelBiomass;
    
    /* Data structures for a reaction in a model
		
		fba_id id - ID of the FBA object
		workspace_id workspace - ID of the workspace containing the FBA object
		media_id media - ID of the media the FBA was performed in
		workspace_id media_workspace - ID of the workspace containing the media formulation
		float objective - optimized objective value of the FBA study
		list<feature_id> ko - list of genes knocked out in the FBA study
								
	*/
    typedef tuple<fba_id id,workspace_id workspace,media_id media,workspace_id media_workspace,float objective,list<feature_id> ko> FBAMeta;
    
    /* Metadata object providing a summary of a gapgen simulation
		
		gapgen_id id - ID of gapgen study object
		workspace_id workspace - workspace containing gapgen study
		media_id media - media formulation for gapgen study
		workspace_id media_workspace - ID of the workspace containing the media formulation
		bool done - boolean indicating if gapgen study is complete
		list<feature_id> ko - list of genes knocked out in gapgen study
								
	*/
    typedef tuple<gapgen_id id,workspace_id workspace,media_id media,workspace_id media_workspace,bool done,list<feature_id> ko> GapGenMeta;
    
    /* Metadata object providing a summary of a gapfilling simulation
		
		gapfill_id id - ID of gapfill study object
		workspace_id workspace - workspace containing gapfill study
		media_id media - media formulation for gapfill study
		workspace_id media_workspace - ID of the workspace containing the media formulation
		bool done - boolean indicating if gapfill study is complete
		list<feature_id> ko - list of genes knocked out in gapfill study
								
	*/
    typedef tuple<gapfill_id id,workspace_id workspace,media_id media,workspace_id media_workspace,bool done,list<feature_id> ko> GapFillMeta;
    
    typedef structure {
		string name;
		list<feature_id> feature;
    } Subsystem;
    
    /* Data structure holding data for metabolic model
		
		fbamodel_id id - ID of model
		workspace_id workspace - workspace containing model
		genome_id genome - ID of associated genome
		workspace_id genome_workspace - workspace with associated genome
		mapping_id map - ID of associated mapping database
		workspace_id map_workspace - workspace with associated mapping database
		biochemistry_id biochemistry - ID of associated biochemistry database
		workspace_id biochemistry_workspace - workspace with associated biochemistry database
		string name - name of the model
		string type - type of model (e.g. single genome, community)
		string status - status of model (e.g. under construction)
		list<ModelBiomass> biomasses - list of biomass reactions in model
		list<ModelCompartment> compartments - list of compartments in model
		list<ModelReaction> reactions - list of reactions in model
		list<ModelCompound> compounds - list of compounds in model
		list<FBAMeta> fbas - list of flux balance analysis studies for model
		list<GapFillMeta> integrated_gapfillings - list of integrated gapfilling solutions
		list<GapFillMeta> unintegrated_gapfillings - list of unintegrated gapfilling solutions
		list<GapGenMeta> integrated_gapgenerations - list of integrated gapgen solutions
		list<GapGenMeta> unintegrated_gapgenerations - list of unintegrated gapgen solutions
								
	*/
    typedef structure {
		fbamodel_id id;
		workspace_id workspace;
		genome_id genome;
		workspace_id genome_workspace;
		mapping_id map;
		workspace_id map_workspace;
		biochemistry_id biochemistry;
		workspace_id biochemistry_workspace;
		string name;
		string type;
		string status;
		
		list<ModelBiomass> biomasses;
		list<ModelCompartment> compartments;
		list<ModelReaction> reactions;
		list<ModelCompound> compounds;
		
		list<FBAMeta> fbas;
		list<GapFillMeta> integrated_gapfillings;
		list<GapFillMeta> unintegrated_gapfillings;
		list<GapGenMeta> integrated_gapgenerations;
		list<GapGenMeta> unintegrated_gapgenerations;
		list<Subsystem> modelSubsystems;
    } FBAModel;
    /*********************************************************************************
    Flux Balance Analysis type definition
   	*********************************************************************************/
    /* Assertion about gene feature produced by metabolic model
		
		feature_id feature - ID of the feature analyzed by the model
		float growthFraction - fraction of wildtype growth rate predicted when feature is knocked out
		float growth - growth rate predicted when feature is knocked out
		bool isEssential - boolean indicating if gene is essential
								
	*/
    typedef tuple<feature_id feature,float growthFraction,float growth,bool isEssential> GeneAssertion;
    
    /* Compound variable in FBA solution
		
		modelcompound_id compound - ID of compound in model in FBA solution
		float value - flux uptake of compound in FBA solution
		float upperBound - maximum uptake of compoundin FBA simulation
		float lowerBound - minimum uptake of compoundin FBA simulation
		float max - maximum uptake of compoundin FBA simulation
		float min - minimum uptake of compoundin FBA simulation
		string type - type of compound variable
		string name - name of compound
								
	*/ 
    typedef tuple<modelcompound_id compound,float value,float upperBound,float lowerBound,float max,float min,string type,string name> CompoundFlux;
    
    /* Reaction variable in FBA solution
    	
    	modelreaction_id reaction - ID of reaction in model in FBA solution
    	float value - flux through reaction in FBA solution
    	float upperBound - maximum flux through reaction in FBA simulation
    	float lowerBound -  minimum flux through reaction in FBA simulation
    	float max - maximum flux through reaction in FBA simulation
    	float min - minimum flux through reaction in FBA simulation
    	string type - type of reaction variable
    	string definition - stoichiometry of solution reaction in terms of compound names
								
	*/
    typedef tuple<modelreaction_id reaction,float value,float upperBound,float lowerBound,float max,float min,string type,string definition> ReactionFlux;
    
    /* Maximum production of compound in FBA simulation
    	
    	float maximumProduction - maximum production of compound
    	modelcompound_id modelcompound - ID of compound with production maximized
    	string name - name of compound with simulated production
								
	*/
    typedef tuple<float maximumProduction,modelcompound_id modelcompound,string name> MetaboliteProduction;

	/* Data structures for gapfilling solution
		
		list<compound_id> optionalNutrients - list of optional nutrients
		list<compound_id> essentialNutrients - list of essential nutrients
						
	*/
	typedef structure {
		list<compound_id> optionalNutrients;
		list<compound_id> essentialNutrients;
    } MinimalMediaPrediction;
    
    /* Term of constraint or objective in FBA simulation
    	
    	float min - minimum value of custom bound
    	float max - maximum value of custom bound
    	string varType - type of variable for custom bound
    	string variable - variable ID for custom bound
								
	*/
    typedef tuple<float min,float max,string varType,string variable> bound;
    
    /* Term of constraint or objective in FBA simulation
    	
    	float coefficient - coefficient of term in objective or constraint
    	string varType - type of variable for term in objective or constraint
    	string variable - variable ID for term in objective or constraint
								
	*/
    typedef tuple<float coefficient,string varType,string variable> term;
    
    /* Custom constraint in FBA simulation
    	
    	float rhs - right hand side of custom constraint
    	string sign - sign of custom constraint (e.g. <, >)
    	list<term> terms - terms in custom constraint
    	string name - name of custom constraint
								
	*/
    typedef tuple<float rhs,string sign,list<term> terms,string name> constraint;
	
	/* Data structures for gapfilling solution
		
		media_id media - ID of media formulation to be used
		list<compound_id> additionalcpds - list of additional compounds to allow update
		prommodel_id prommodel - ID of prommodel
		workspace_id prommodel_workspace - workspace containing prommodel
		workspace_id media_workspace - workspace containing media for FBA study
		float objfraction - fraction of objective to use for constraints
		bool allreversible - flag indicating if all reactions should be reversible
		bool maximizeObjective - flag indicating if objective should be maximized
		list<term> objectiveTerms - list of terms of objective function
		list<feature_id> geneko - list of gene knockouts
		list<reaction_id> rxnko - list of reaction knockouts
		list<bound> bounds - list of custom bounds
		list<constraint> constraints - list of custom constraints
		mapping<string,float> uptakelim - hash of maximum uptake for elements
		float defaultmaxflux - default maximum intracellular flux
		float defaultminuptake - default minimum nutrient uptake
		float defaultmaxuptake - default maximum nutrient uptake
		bool simplethermoconst - flag indicating if simple thermodynamic constraints should be used
		bool thermoconst - flag indicating if thermodynamic constraints should be used
		bool nothermoerror - flag indicating if no error should be allowed in thermodynamic constraints
		bool minthermoerror - flag indicating if error should be minimized in thermodynamic constraints
						
	*/
	typedef structure {
		media_id media;
		list<compound_id> additionalcpds;
		prommodel_id prommodel;
		workspace_id prommodel_workspace;
		workspace_id media_workspace;
		float objfraction;
		bool allreversible;
		bool maximizeObjective;
		list<term> objectiveTerms;
		list<feature_id> geneko;
		list<reaction_id> rxnko;
		list<bound> bounds;
		list<constraint> constraints;
		mapping<string,float> uptakelim;
		float defaultmaxflux;
		float defaultminuptake;
		float defaultmaxuptake;
		bool simplethermoconst;
		bool thermoconst;
		bool nothermoerror;
		bool minthermoerror;
    } FBAFormulation;
    
    /* Data structures for gapfilling solution
		
		fba_id id - ID of FBA study
		workspace_id workspace - workspace containing FBA study
        fbamodel_id model - ID of model FBA was run on
        workspace_id model_workspace - workspace with FBA model
        float objective - objective value of FBA study
        bool isComplete - flag indicating if job is complete
		FBAFormulation formulation - specs for FBA study
		list<MinimalMediaPrediction> minimalMediaPredictions - list of minimal media formulation
		list<MetaboliteProduction> metaboliteProductions - list of biomass component production
		list<ReactionFlux> reactionFluxes - list of reaction fluxes
		list<CompoundFlux> compoundFluxes - list of compound uptake fluxes
		list<GeneAssertion> geneAssertions - list of gene assertions
						
	*/
    typedef structure {
		fba_id id;
		workspace_id workspace;
        fbamodel_id model;
        workspace_id model_workspace;
        float objective;
        bool isComplete;
		FBAFormulation formulation;
		list<MinimalMediaPrediction> minimalMediaPredictions;
		list<MetaboliteProduction> metaboliteProductions;
		list<ReactionFlux> reactionFluxes;
		list<CompoundFlux> compoundFluxes;
		list<GeneAssertion> geneAssertions;
    } FBA;
    /*********************************************************************************
    Gapfilling type definition
   	*********************************************************************************/
    /* Data structures for gapfilling solution
		
		FBAFormulation formulation - specs for FBA of gapfilling study
		int num_solutions - maximum number of solutions to obtain
		bool nomediahyp - flag indicating media hypothesis should not be considered
		bool nobiomasshyp - flag indicating biomass hypothesis should not be considered
		bool nogprhyp - flag indicating GPR hypothesis should not be considered
		bool nopathwayhyp - flag indicating pathway hypothesis should not be considered
		bool allowunbalanced - flag indicating if we should allow unbalanced reactions to be gapfilled
		float activitybonus - bonus for activation of 'dead' reactions
		float drainpen - penalty for addition of drain reactions
		float directionpen - penalty for making irreversible reactions reversible
		float nostructpen - penalty for reactions with compounds with no structure
		float unfavorablepen - penalty for unfavorable reactions
		float nodeltagpen - penalty for reactions with compounds with no delta G
		float biomasstranspen - penalty for reactions transporting biomass components
		float singletranspen - penalty for reactions transporting single compounds
		float transpen - penalty for reactions with compounds with no structure
		list<reaction_id> blacklistedrxns - list of reactions excluded from gapfilling
		list<reaction_id> gauranteedrxns - list of reactions gauranteed to be allowed in gapfilling
		list<compartment_id> allowedcmps - list of compartments allowed in gapfilled reactions
		probanno_id probabilisticAnnotation - probabilistic annotations used to drive improved gapfilling
		workspace_id probabilisticAnnotation_workspace - workspace containing probabilistic annotations
						
	*/
    typedef structure {
		FBAFormulation formulation;
		int num_solutions;
		bool nomediahyp;
		bool nobiomasshyp;
		bool nogprhyp;
		bool nopathwayhyp;
		bool allowunbalanced;
		float activitybonus;
		float drainpen;
		float directionpen;
		float nostructpen;
		float unfavorablepen;
		float nodeltagpen;
		float biomasstranspen;
		float singletranspen;
		float transpen;
		list<reaction_id> blacklistedrxns;
		list<reaction_id> gauranteedrxns;
		list<compartment_id> allowedcmps;
		probanno_id probabilisticAnnotation;
		workspace_id probabilisticAnnotation_workspace;
    } GapfillingFormulation;
    
    /* Reactions removed in gapgen solution
		
		modelreaction_id reaction - ID of the removed reaction
		string direction - direction of reaction removed in gapgen solution
		string equation - stoichiometry of removed reaction in terms of compound IDs
		string definition - stoichiometry of removed reaction in terms of compound names
						
	*/
    typedef tuple<reaction_id reaction,string direction,string compartment_id,string equation,string definition> reactionAddition;
    
    /* Biomass component removed in gapfill solution
		
		compound_id compound - ID of biomass component removed
		string name - name of biomass component removed
						
	*/
    typedef tuple<compound_id compound,string name> biomassRemoval;
    
    /* Media component added in gapfill solution
		
		compound_id compound - ID of media component added
		string name - name of media component added
						
	*/
    typedef tuple<compound_id compound,string name> mediaAddition;
    
    /* Data structures for gapfilling solution
		
		gapfillsolution_id id - ID of gapfilling solution
        float objective - cost of gapfilling solution
		list<biomassRemoval> biomassRemovals - list of biomass components being removed
		list<mediaAddition> mediaAdditions - list of media components being added
		list<reactionAddition> reactionAdditions - list of reactions being added
						
	*/
    typedef structure {
    	gapfillsolution_id id;
        float objective;
		list<biomassRemoval> biomassRemovals;
		list<mediaAddition> mediaAdditions;
		list<reactionAddition> reactionAdditions;
    } GapFillSolution;
    
    /* Data structures for gapfilling analysis
		
		gapfill_id id - ID of gapfill analysis
		workspace_id workspace - workspace containing gapfill analysis
		fbamodel_id model - ID of model being gapfilled
        workspace_id model_workspace - workspace containing model
        bool isComplete - indicates if gapfilling is complete
		GapfillingFormulation formulation - formulation of gapfilling analysis
		list<GapFillSolution> solutions - list of gapfilling solutions
						
	*/
    typedef structure {
		gapfill_id id;
		workspace_id workspace;
		fbamodel_id model;
        workspace_id model_workspace;
        bool isComplete;
		GapfillingFormulation formulation;
		list<GapFillSolution> solutions;
    } GapFill;
    /*********************************************************************************
    Gap Generation type definition
   	*********************************************************************************/
    /* Data structures for gap generation solution
		
		FBAFormulation formulation - specs for FBA of gap generation
		media_id refmedia - reference media in which model must grow
		workspace_id refmedia_workspace - workspace containing reference media
		int num_solutions - number of gap generation solutions to be obtained
		bool nomediahyp - flag indicating media hypothesis should not be considered
		bool nobiomasshyp - flag indicating biomass hypothesis should not be considered
		bool nogprhyp - flag indicating GPR hypothesis should not be considered
		bool nopathwayhyp - flag indicating pathway hypothesis should not be considered
						
	*/
    typedef structure {
		FBAFormulation formulation;
		media_id refmedia;
		workspace_id refmedia_workspace;
		int num_solutions;
		bool nomediahyp;
		bool nobiomasshyp;
		bool nogprhyp;
		bool nopathwayhyp;
    } GapgenFormulation;
    
    /* Reactions removed in gapgen solution
		
		modelreaction_id reaction - ID of the removed reaction
		string direction - direction of reaction removed in gapgen solution
		string equation - stoichiometry of removed reaction in terms of compound IDs
		string definition - stoichiometry of removed reaction in terms of compound names
						
	*/
    typedef tuple<modelreaction_id reaction,string direction,string equation,string definition> reactionRemoval;
    
    /* Compounds added to biomass in gapgen solution
		
		compound_id compound - ID of biomass compound added
		string name - name of biomass compound added
						
	*/
    typedef tuple<compound_id compound,string name> biomassAddition;
    
    /* Media components removed in gapgen solution
		
		compound_id compound - ID of media component removed
		string name - name of media component removed
						
	*/
    typedef tuple<compound_id compound,string name> mediaRemoval;
    
    /* Data structures for gap generation solution
		
		gapgensolution_id id - ID of gapgen solution
        float objective - cost of gapgen solution
		list<biomassAddition> biomassAdditions - list of components added to biomass
		list<mediaRemoval> mediaRemovals - list of media components removed
		list<reactionRemoval> reactionRemovals - list of reactions removed
						
	*/
    typedef structure {
        gapgensolution_id id;
        float objective;
		list<biomassAddition> biomassAdditions;
		list<mediaRemoval> mediaRemovals;
		list<reactionRemoval> reactionRemovals;
    } GapgenSolution;
    
    /* Data structures for gap generation analysis
		
		gapgen_id id - ID of gapgen object
		workspace_id workspace - workspace containing gapgen object
		fbamodel_id model - ID of model being gap generated
        workspace_id model_workspace - workspace containing model
        bool isComplete - flag indicating if gap generation is complete
		GapgenFormulation formulation - formulation of gap generation analysis
		list<GapgenSolution> solutions - list of gap generation solutions
						
	*/
    typedef structure {
		gapgen_id id;
		workspace_id workspace;
		fbamodel_id model;
        workspace_id model_workspace;
        bool isComplete;
		GapgenFormulation formulation;
		list<GapgenSolution> solutions;
    } GapGen;
    
    /*********************************************************************************
    Phenotype type definitions
   	*********************************************************************************/
    /* Data structures for a single growth phenotype
		
		list<feature_id> geneKO - list of genes knocked out in the strain used with the growth phenotype
		media_id baseMedia - base media condition used with the growth phenotype
		workspace_id media_workspace - workspace containing the specified base media formulation
		list<compound_id> additionalCpd - list of additional compounds present in the base media with the growth phenotype 
		float normalizedGrowth - fraction of reference growth rate for growth phenotype
				
	*/
    typedef tuple<list<feature_id> geneKO,media_id baseMedia,workspace_id media_workspace,list<compound_id> additionalCpd,float normalizedGrowth> Phenotype;
    
    /* Data structures for set of growth phenotype observations
		
		phenotypeSet_id id - ID of the phenotype set
		genome_id genome - ID of the genome for the strain used with the growth phenotypes
		workspace_id genome_workspace - workspace containing the genome object
		list<Phenotype> phenotypes - list of phenotypes included in the phenotype set
		string importErrors - list of errors encountered during the import of the phenotype set
				
	*/
    typedef structure {
		phenotypeSet_id id;
		genome_id genome;
		workspace_id genome_workspace;
		list<Phenotype> phenotypes;
		string importErrors;
    } PhenotypeSet;
    
    /* ID of the phenotype simulation object */
    typedef string phenotypeSimulationSet_id;
    
    /* Data structures for a phenotype simulation
		
		Phenotype phenotypeData - actual phenotype data simulated
		float simulatedGrowth - actual simulated growth rate
		float simulatedGrowthFraction - fraction of wildtype simulated growth rate
		string class - class of the phenotype simulation (i.e. 'CP' - correct positive, 'CN' - correct negative, 'FP' - false positive, 'FN' - false negative)
				
	*/
    typedef tuple<Phenotype phenotypeData,float simulatedGrowth,float simulatedGrowthFraction,string class> PhenotypeSimulation;
    
    /* Data structures for phenotype simulations of a set of phenotype data
		
		phenotypeSimulationSet_id id - ID for the phenotype simulation set object
		fbamodel_id model - ID of the model used to simulate all phenotypes
		workspace_id model_workspace - workspace containing the model used for the simulation
		phenotypeSet_id phenotypeSet - set of observed phenotypes that were simulated
		list<PhenotypeSimulation> phenotypeSimulations - list of simulated phenotypes
						
	*/
    typedef structure {
    	phenotypeSimulationSet_id id;
		fbamodel_id model;
		workspace_id model_workspace;
		phenotypeSet_id phenotypeSet;
		list<PhenotypeSimulation> phenotypeSimulations;
    } PhenotypeSimulationSet;
    
    /*********************************************************************************
    Job object type definitions
   	*********************************************************************************/
    /* ID of the job object */
    typedef string job_id;
    
    /* Object to hold the arguments to be submitted to the post process command */
    typedef structure {
		string auth;
    } CommandArguments;
    
    /* Object to hold data required to run cluster job */
    typedef structure {
		string auth;
    } clusterjob;
    
    /* Data structures for an FBA job object
		
		job_id id - ID of the job object
		workspace_id workspace - workspace containing job object
		list<clusterjob> clusterjobs - list of data related to cluster jobs
		string postprocess_command - command to be run after the job is complete
		list<CommandArguments> postprocess_args - arguments to be submitted to the postprocess job
		string queuing_command - command used to queue job
		float clustermem - maximum memmory expected to be consumed by the job
		int clustertime - maximum time to spent running the job
		string clustertoken - token for submitted cluster job
		string queuetime - time when the job was queued
		string completetime - time when the job was completed
		bool complete - flag indicating if job is complete
		string owner - username of the user that queued the job
		
	*/
    typedef structure {
		job_id id;
		workspace_id workspace;
		list<clusterjob> clusterjobs;
		string postprocess_command;
		list<CommandArguments> postprocess_args;
		string queuing_command;
		float clustermem;
		int clustertime;
		string clustertoken;
		string queuetime;
		string completetime;
		bool complete;
		string owner;		
    } JobObject;
	/*********************************************************************************
    ETC object type definitions
   	*********************************************************************************/
    typedef structure {
		string resp;
		int y;
		int x;
		int width;
		int height;
		string shape;
		string label;
    } ETCNodes;
    
    typedef structure {
		list<ETCNodes> nodes;
		string media;
		string growth;
		string organism;
    } ETCDiagramSpecs;
    /*********************************************************************************
    Function definitions relating to data retrieval for Model Objects
   	*********************************************************************************/
    /* Input parameters for the "get_models" function.
	
		list<fbamodel_id> models - a list of the model IDs for the models to be returned (a required argument)
		list<workspace_id> workspaces - a list of the workspaces contianing the models to be returned (a required argument)
        string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<fbamodel_id> models;
		list<workspace_id> workspaces;
		string auth;
        string id_type;
    } get_models_params;
    /*
    	Returns model data for input ids
    */
    funcdef get_models(get_models_params input) returns (list<FBAModel> out_models);

	/* Input parameters for the "get_fbas" function.
	
		list<fba_id> fbas - a list of the FBA study IDs for the FBA studies to be returned (a required argument)
		list<workspace_id> workspaces - a list of the workspaces contianing the FBA studies to be returned (a required argument)
        string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<fba_id> fbas;
		list<workspace_id> workspaces; 
		string auth;
        string id_type;
    } get_fbas_params;
    /*
    	Returns data for the requested flux balance analysis formulations
    */
    funcdef get_fbas(get_fbas_params input) returns (list<FBA> out_fbas);

	/* Input parameters for the "get_gapfills" function.
	
		list<gapfill_id> gapfills - a list of the gapfill study IDs for the gapfill studies to be returned (a required argument)
		list<workspace_id> workspaces - a list of the workspaces contianing the gapfill studies to be returned (a required argument)
        string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<gapfill_id> gapfills;
		list<workspace_id> workspaces; 
		string auth;
        string id_type;
    } get_gapfills_params;
    /*
    	Returns data for the requested gap filling simulations
    */
    funcdef get_gapfills(get_gapfills_params input) returns (list<GapFill> out_gapfills);

	/* Input parameters for the "get_gapgens" function.
	
		list<gapgen_id> gapgens - a list of the gapgen study IDs for the gapgen studies to be returned (a required argument)
		list<workspace_id> workspaces - a list of the workspaces contianing the gapgen studies to be returned (a required argument)
        string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<gapgen_id> gapgens;
		list<workspace_id> workspaces;
		string auth;
        string id_type;
    } get_gapgens_params;
    /*
    	Returns data for the requested gap generation simulations
    */
    funcdef get_gapgens(get_gapgens_params input) returns (list<GapGen> out_gapgens);

	/* Input parameters for the "get_reactions" function.
	
		list<reaction_id> reactions - a list of the reaction IDs for the reactions to be returned (a required argument)
		string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<reaction_id> reactions;
		string auth;
        string id_type;
    } get_reactions_params;
    /*
    	Returns data for the requested reactions
    */
    funcdef get_reactions(get_reactions_params input) returns (list<Reaction> out_reactions);

	/* Input parameters for the "get_compounds" function.
	
		list<compound_id> compounds - a list of the compound IDs for the compounds to be returned (a required argument)
		string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<compound_id> compounds;
		string auth;
        string id_type;
    } get_compounds_params;
    /*
    	Returns data for the requested compounds
    */
    funcdef get_compounds(get_compounds_params input) returns (list<Compound> out_compounds);

    /* Input parameters for the "get_media" function.
	
		list<media_id> medias - a list of the media IDs for the media to be returned (a required argument)
		string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		list<media_id> medias;
		list<workspace_id> workspaces;
		string auth;
    } get_media_params;
    /*
    	Returns data for the requested media formulations
    */
    funcdef get_media(get_media_params input) returns (list<Media> out_media);

	/* Input parameters for the "get_biochemistry" function.
	
		biochemistry_id biochemistry - ID of the biochemistry database to be returned (a required argument)
		workspace_id biochemistry_workspace - workspace containing the biochemistry database to be returned (a required argument)
		string id_type - the type of ID that should be used in the output data (a optional argument; default is 'ModelSEED')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
        biochemistry_id biochemistry;
        workspace_id biochemistry_workspace;
        string id_type;
        string auth;
    } get_biochemistry_params;
    /*
    	Returns biochemistry object
    */
    funcdef get_biochemistry(get_biochemistry_params input) returns (Biochemistry out_biochemistry);
	
	/* Input parameters for the "genome_to_fbamodel" function.
	
		model_id model - ID of the model to retrieve ETC for
		workspace_id workspace - ID of the workspace containing the model 
		media_id media - ID of the media to retrieve ETC for
		workspace_id mediaws - workpace containing the specified media
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
				
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id workspace;
		media_id media;
		workspace_id mediaws;
		string auth;
    } get_ETCDiagram_params;
    /*
        This function retrieves an ETC diagram for the input model operating in the input media condition
    	The model must grow on the specified media in order to return a working ETC diagram
    */
    funcdef get_ETCDiagram (get_ETCDiagram_params input) returns (ETCDiagramSpecs output);
	/*********************************************************************************
    Code relating to reconstruction of metabolic models
   	*********************************************************************************/
    /* Input parameters for the "import_probanno" function.
	
		probanno_id probanno - id of the probabilistic annotation to be created (an optional parameter; default is 'undef')
		workspace_id workspace - id of the workspace where the probabilistic annotation will be stored (an essential parameter)
		genome_id genome - id of the genome that the probabilistic annotation will be associated with (an essential parameter)
		workspace_id genome_workspace - workspace containing the genome for the probabilistic annotation (an optional parameter; default is 'workspace' parameter)
		list<annotationProbability> annotationProbabilities - a list of the probabilistic annotations for all genes to be part of the prababilistic annotations (an essential parameter)
		bool ignore_errors - a flag indicating that even if errors are encountered, the probabilistic annotation should still be imported (an optional parameter; default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		probanno_id probanno;
		workspace_id workspace;		
		genome_id genome;
		workspace_id genome_workspace;
		list<annotationProbability> annotationProbabilities;
		bool ignore_errors;
		string auth;
		bool overwrite;
    } import_probanno_params;
    /*
        Loads an input genome object into the workspace.
    */
    funcdef import_probanno(import_probanno_params input) returns (object_metadata probannoMeta);
    
    /* Input parameters for the "genome_object_to_workspace" function.
	
		GenomeObject genomeobj - full genome typed object to be loaded into the workspace (a required argument)
		workspace_id workspace - ID of the workspace into which the genome typed object is to be loaded (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)

	*/
    typedef structure {
		GenomeObject genomeobj;
		workspace_id workspace;
		string auth;
		bool overwrite;
    } genome_object_to_workspace_params;
    /*
        Loads an input genome object into the workspace.
    */
    funcdef genome_object_to_workspace(genome_object_to_workspace_params input) returns (object_metadata genomeMeta);
    
    /* Input parameters for the "genome_to_workspace" function.
	
		genome_id genome - ID of the CDM genome that is to be loaded into the workspace (a required argument)
		string sourceLogin - login to pull private genome from source database
		string sourcePassword - password to pull private genome from source database
		string source - Source database for genome (i.e. seed, rast, kbase)
		workspace_id workspace - ID of the workspace into which the genome typed object is to be loaded (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)

	*/
    typedef structure {
		genome_id genome;
		workspace_id workspace;
		string sourceLogin;
		string sourcePassword;
		string source;
		string auth;
		bool overwrite;
    } genome_to_workspace_params;
    /*
        Retrieves a genome from the CDM and saves it as a genome object in the workspace.
    */
    funcdef genome_to_workspace(genome_to_workspace_params input) returns (object_metadata genomeMeta);
    
    /* A link between a KBase gene ID and the ID for the same gene in another database
	
		string foreign_id - ID of the gene in another database
		feature_id feature - ID of the gene in KBase
		
	*/
    typedef tuple<string foreign_id,feature_id feature> translation; 
    
    /* Input parameters for the "add_feature_translation" function.
	
		genome_id genome - ID of the genome into which the new aliases are to be loaded (a required argument)
		workspace_id workspace - ID of the workspace containing the target genome (a required argument)
		list<translation> translations - list of translations between KBase gene IDs and gene IDs in another database (a required argument)
		string id_type - type of the IDs being loaded (e.g. KEGG, NCBI) (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		genome_id genome;
		workspace_id workspace;
		list<translation> translations;
		string id_type;
		string auth;
		bool overwrite;
    } add_feature_translation_params;
    /*
        Adds a new set of alternative feature IDs to the specified genome typed object
    */
    funcdef add_feature_translation(add_feature_translation_params input) returns (object_metadata genomeMeta);
    
    /* Input parameters for the "genome_to_fbamodel" function.
	
		genome_id genome - ID of the genome for which a model is to be built (a required argument)
		workspace_id genome_workspace - ID of the workspace containing the target genome (an optional argument; default is the workspace argument)
		probanno_id probanno - ID of the probabilistic annotation to be used in building the model (an optional argument; default is 'undef')
		workspace_id probanno_workspace - ID of the workspace containing the probabilistic annotation (an optional argument; default is the workspace argument)
		float probannoThreshold - a threshold of the probability required for a probabilistic annotation to be accepted (an optional argument; default is '1')
		bool probannoOnly - a boolean indicating if only the probabilistic annotation should be used in building the model (an optional argument; default is '0')
		fbamodel_id model - ID that should be used for the newly constructed model (an optional argument; default is 'undef')
		bool coremodel - indicates that a core model should be constructed instead of a genome scale model (an optional argument; default is '0')
		workspace_id workspace - ID of the workspace where the newly developed model will be stored; also the default assumed workspace for input objects (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		genome_id genome;
		workspace_id genome_workspace;
		probanno_id probanno;
		workspace_id probanno_workspace;
		float probannoThreshold;
		bool probannoOnly;
		fbamodel_id model;
		bool coremodel;
		workspace_id workspace;
		string auth;
		bool overwrite;
    } genome_to_fbamodel_params;
    /*
        Build a genome-scale metabolic model based on annotations in an input genome typed object
    */
    funcdef genome_to_fbamodel (genome_to_fbamodel_params input) returns (object_metadata modelMeta);
	
	/* Input parameters for the "genome_to_fbamodel" function.
	
		genome_id genome - ID of the genome for which a model is to be built (a required argument)
		workspace_id genome_workspace - ID of the workspace containing the target genome (an optional argument; default is the workspace argument)
		string biomass - biomass equation for model (an essential argument)
		list<tuple<string id,string direction,string compartment,string gpr> reactions - list of reactions to appear in imported model (an essential argument)
		fbamodel_id model - ID that should be used for the newly imported model (an optional argument; default is 'undef')
		workspace_id workspace - ID of the workspace where the newly developed model will be stored; also the default assumed workspace for input objects (a required argument)
		bool ignore_errors - ignores missing genes or reactions and imports model anyway
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		genome_id genome;
		workspace_id genome_workspace;
		string biomass;
		list<tuple<string id,string direction,string compartment,string gpr>> reactions;
		fbamodel_id model;
		workspace_id workspace;
		bool ignore_errors;
		string auth;
		bool overwrite;
    } import_fbamodel_params;
    /*
        Import a model from an input table of model and gene IDs
    */
    funcdef import_fbamodel(import_fbamodel_params input) returns (object_metadata modelMeta);
	
	/* Input parameters for the "genome_to_fbamodel" function.
	
		genome_id genome - ID of the genome for which a model is to be built (a required argument)
		workspace_id genome_workspace - ID of the workspace containing the target genome (an optional argument; default is the workspace argument)
		probanno_id probanno - ID of the probabilistic annotation to be used in building the model (an optional argument; default is 'undef')
		workspace_id probanno_workspace - ID of the workspace containing the probabilistic annotation (an optional argument; default is the workspace argument)
		fbamodel_id model - ID that should be used for the newly constructed model (an optional argument; default is 'undef')
		workspace_id workspace - ID of the workspace where the newly developed model will be stored; also the default assumed workspace for input objects (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		genome_id genome;
		workspace_id genome_workspace;
		probanno_id probanno;
		workspace_id probanno_workspace;
		fbamodel_id model;
		workspace_id workspace;
		string auth;
    } genome_to_probfbamodel_params;
    /*
        Build a probabilistic genome-scale metabolic model based on annotations in an input genome and probabilistic annotation
    */
    funcdef genome_to_probfbamodel (genome_to_probfbamodel_params input) returns (object_metadata modelMeta);
	
    /* Input parameters for the "export_fbamodel" function.
	
		fbamodel_id model - ID of the model to be exported (a required argument)
		workspace_id workspace - workspace containing the model to be exported (a required argument)
		string format - format to which the model should be exported (sbml, html, json, readable, cytoseed) (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id workspace;
		string format;
		string auth;
    } export_fbamodel_params;
    /*
        This function exports the specified FBAModel to a specified format (sbml,html)
    */
    funcdef export_fbamodel(export_fbamodel_params input) returns (string output);
    
    /* Input parameters for the "export_object" function.
	
		workspace_ref reference - reference of object to print in html (a required argument)
		string type - type of the object to be exported (a required argument)
		string format - format to which data should be exported (an optional argument; default is html)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		workspace_ref reference;
		string type;
		string format;
    	string auth;
    } export_object_params;
    /*
        This function prints the object pointed to by the input reference in the specified format
    */
    funcdef export_object(export_object_params input) returns (string output);

	/* Input parameters for the "export_genome" function.
	
		genome_id genome - ID of the genome to be exported (a required argument)
		workspace_id workspace - workspace containing the model to be exported (a required argument)
		string format - format to which the model should be exported (sbml, html, json, readable, cytoseed) (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		genome_id genome;
		workspace_id workspace;
		string format;
		string auth;
    } export_genome_params;
    /*
        This function exports the specified FBAModel to a specified format (sbml,html)
    */
    funcdef export_genome(export_genome_params input) returns (string output);

    /* Input parameters for the "adjust_model_reaction" function.
	
		fbamodel_id model - ID of model to be adjusted
		workspace_id workspace - workspace containing model to be adjusted
		reaction_id reaction - ID of reaction to be added, removed, or adjusted
		string direction - direction to set for reaction being added or adjusted
		compartment_id compartment - ID of compartment containing reaction being added or adjusted
		int compartmentIndex - index of compartment containing reaction being altered or adjusted
		list<list<list<feature_id>>> gpr - array specifying gene-protein-reaction associations
		bool removeReaction - boolean indicating reaction should be removed
		bool addReaction - boolean indicating reaction should be added
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id workspace;
		reaction_id reaction;
		string direction;
		compartment_id compartment;
		int compartmentIndex;
		string gpr;
		bool removeReaction;
		bool addReaction;
		bool overwrite;
		string auth;
    } adjust_model_reaction_params;
    /*
        Enables the manual addition of a reaction to model
    */
    funcdef adjust_model_reaction(adjust_model_reaction_params input) returns (object_metadata modelMeta);
    
    /* Input parameters for the "adjust_biomass_reaction" function.
	
		fbamodel_id model - ID of model to be adjusted
		workspace_id workspace - workspace containing model to be adjusted
		biomass_id biomass - ID of biomass reaction to adjust
		float coefficient - coefficient of biomass compound
		compound_id compound - ID of biomass compound to adjust in biomass
		compartment_id compartment - ID of compartment containing compound to adjust in biomass
		int compartmentIndex - index of compartment containing compound to adjust in biomass
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
			
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id workspace;
		biomass_id biomass;
		float coefficient;
		compound_id compound;
		compartment_id compartment;
		int compartmentIndex;
		bool overwrite;
		string auth;
    } adjust_biomass_reaction_params;
    /*
        Enables the manual adjustment of model biomass reaction
    */
    funcdef adjust_biomass_reaction(adjust_biomass_reaction_params input) returns (object_metadata modelMeta);
    
    /*********************************************************************************
    Code relating to flux balance analysis
   	*********************************************************************************/
    /* Input parameters for the "addmedia" function.
	
		media_id media - ID of the new media to be added (a required argument)
		workspace_id workspace - workspace where the new media should be created (a required argument)
		string name - name of the new media to be added  (an optional argument: default is the value of the media argument)
		bool isDefined - boolean indicating if new media is defined (an optional argument: default is '0')
		bool isMinimal - boolean indicating if new media is mininal (an optional argument: default is '0')
		string type - the type of the new media (e.g. Biolog) (an optional argument: default is 'unknown')
		list<string> compounds - a list of the compounds to be included in the new media (a required argument)
		list<float> concentrations - a list of the concentrations for compounds in the new media (an optional argument: default is 0.001 M for all compounds)
		list<float> maxflux - a list of the maximum uptakes for compounds in the new media (an optional argument: default is 100 mmol/hr gm CDW for all compounds)
		list<float> minflux - a list of the minimum uptakes for compounds in the new media (an optional argument: default is 100 mmol/hr gm CDW for all compounds)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		media_id media;
		workspace_id workspace;
		string name;
		bool isDefined;
		bool isMinimal;
		string type;
		list<string> compounds;
		list<float> concentrations;
		list<float> maxflux;
		list<float> minflux;
		bool overwrite;
		string auth;
    } addmedia_params;
    /*
        Add media condition to workspace
    */
    funcdef addmedia(addmedia_params input) returns (object_metadata mediaMeta);
    
    /* Input parameters for the "export_media" function.
	
		media_id media - ID of the media to be exported (a required argument)
		workspace_id workspace - workspace containing the media to be exported (a required argument)
		string format - format to which the media should be exported (html, json, readable) (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		media_id media;
		workspace_id workspace;
		string format;
		string auth;
    } export_media_params;
    /*
        Exports media in specified format (html,readable)
    */
    funcdef export_media(export_media_params input) returns (string output);
    
    /* Input parameters for the "addmedia" function.
	
		fbamodel_id model - ID of the model that FBA should be run on (a required argument)
		workspace_id model_workspace - workspace where model for FBA should be run (an optional argument; default is the value of the workspace argument)
		FBAFormulation formulation - a hash specifying the parameters for the FBA study (an optional argument)
		bool fva - a flag indicating if flux variability should be run (an optional argument: default is '0')
		bool simulateko - a flag indicating if flux variability should be run (an optional argument: default is '0')
		bool minimizeflux - a flag indicating if flux variability should be run (an optional argument: default is '0')
		bool findminmedia - a flag indicating if flux variability should be run (an optional argument: default is '0')
		string notes - a string of notes to attach to the FBA study (an optional argument; defaul is '')
		fba_id fba - ID under which the FBA results should be saved (an optional argument; defaul is 'undef')
		workspace_id workspace - workspace where FBA results will be saved (a required argument)
		bool add_to_model - a flag indicating if the FBA study should be attached to the model to support viewing results (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
    	fbamodel_id model;
		workspace_id model_workspace;
		FBAFormulation formulation;
		bool fva;
		bool simulateko;
		bool minimizeflux;
		bool findminmedia;
		string notes;
		fba_id fba;
		workspace_id workspace;
		string auth;
		bool overwrite;
		bool add_to_model;
    } runfba_params;
    /*
        Run flux balance analysis and return ID of FBA object with results 
    */
    funcdef runfba(runfba_params input) returns (object_metadata fbaMeta);
    
    /* Input parameters for the "addmedia" function.
	
		fba_id fba - ID of the FBA study to be exported (a required argument)
		workspace_id workspace - workspace where FBA study is stored (a required argument)
		string format - format to which the FBA study should be exported (i.e. html, json, readable) (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fba_id fba;
		workspace_id workspace;
		string format;
		string auth;
    } export_fba_params;
    /*
        Export an FBA solution for viewing
    */
    funcdef export_fba(export_fba_params input) returns (string output);
    
    /*********************************************************************************
    Code relating to phenotype simulation and reconciliation
   	*********************************************************************************/
    /* Input parameters for the "import_phenotypes" function.
	
		phenotypeSet_id phenotypeSet - ID to be used for the imported phenotype set (an optional argument: default is 'undef')
		workspace_id workspace - workspace where the imported phenotype set should be stored (a required argument)
		genome_id genome - genome the imported phenotypes should be associated with (a required argument)
		workspace_id genome_workspace - workspace containing the genome object (an optional argument: default is value of the workspace argument)
		list<Phenotype> phenotypes - list of observed phenotypes to be imported (a required argument)
		bool ignore_errors - a flag indicating that any errors encountered during the import should be ignored (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		phenotypeSet_id phenotypeSet;
		workspace_id workspace;
		genome_id genome;
		workspace_id genome_workspace;
		list<Phenotype> phenotypes;
		bool ignore_errors;
		string auth;
    } import_phenotypes_params;
    /*
        Loads the specified phenotypes into the workspace
    */
    funcdef import_phenotypes(import_phenotypes_params input) returns (object_metadata output);
    
    /* Input parameters for the "simulate_phenotypes" function.
	
		fbamodel_id model - ID of the model to be used for the simulation (a required argument)
		workspace_id model_workspace - workspace containing the model for the simulation (an optional argument: default is value of workspace argument)
		phenotypeSet_id phenotypeSet - ID of the phenotypes set to be simulated (a required argument)
		workspace_id phenotypeSet_workspace - workspace containing the phenotype set to be simulated (an optional argument: default is value of workspace argument)
		FBAFormulation formulation - parameters for the simulation flux balance analysis (an optional argument: default is 'undef')
		string notes - string of notes to associate with the phenotype simulation (an optional argument: default is '')
		phenotypeSimulationSet_id phenotypeSimultationSet - ID of the phenotype simulation set to be generated (an optional argument: default is 'undef')
		workspace_id workspace - workspace where the phenotype simulation set should be saved (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id model_workspace;
		phenotypeSet_id phenotypeSet;
		workspace_id phenotypeSet_workspace;
		FBAFormulation formulation;
		string notes;
		phenotypeSimulationSet_id phenotypeSimultationSet;
		workspace_id workspace;
		bool overwrite;
		string auth;
    } simulate_phenotypes_params;
    /*
        Simulates the specified phenotype set
    */
    funcdef simulate_phenotypes (simulate_phenotypes_params input) returns (object_metadata output);
    
    /* Input parameters for the "export_phenotypeSimulationSet" function.
	
		phenotypeSimulationSet_id phenotypeSimultationSet - ID of the phenotype simulation set to be exported (a required argument)
		workspace_id workspace - workspace where the phenotype simulation set is stored (a required argument)
		string format - format to which phenotype simulation set should be exported (html, json)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		phenotypeSimulationSet_id phenotypeSimulationSet;
		workspace_id workspace;
		string format;
		string auth;
    } export_phenotypeSimulationSet_params;
    /*
        Export a PhenotypeSimulationSet for viewing
    */
    funcdef export_phenotypeSimulationSet (export_phenotypeSimulationSet_params input) returns (string output);
    
    /* Input parameters for the "integrate_reconciliation_solutions" function.
	
		fbamodel_id model - ID of model for which reconciliation solutions should be integrated (a required argument)
		workspace_id model_workspace - workspace containing model for which solutions should be integrated (an optional argument: default is value of workspace argument)
		list<gapfillsolution_id> gapfillSolutions - list of gapfill solutions to be integrated (a required argument)
		list<gapgensolution_id> gapgenSolutions - list of gapgen solutions to be integrated (a required argument)
		fbamodel_id out_model - ID to which modified model should be saved (an optional argument: default is value of workspace argument)
		workspace_id workspace - workspace where modified model should be saved (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id model_workspace;
		list<gapfillsolution_id> gapfillSolutions;
		list<gapgensolution_id> gapgenSolutions;
		fbamodel_id out_model;
		workspace_id workspace;
		string auth;
		bool overwrite;
    } integrate_reconciliation_solutions_params;
    /*
        Integrates the specified gapfill and gapgen solutions into the specified model
    */
    funcdef integrate_reconciliation_solutions(integrate_reconciliation_solutions_params input) returns (object_metadata modelMeta);
    
    /*********************************************************************************
    Code relating to queuing long running jobs
   	*********************************************************************************/ 
    /* Input parameters for the "queue_runfba" function.
	
		fbamodel_id model - ID of the model that FBA should be run on (a required argument)
		workspace_id model_workspace - workspace where model for FBA should be run (an optional argument; default is the value of the workspace argument)
		FBAFormulation formulation - a hash specifying the parameters for the FBA study (an optional argument)
		bool fva - a flag indicating if flux variability should be run (an optional argument: default is '0')
		bool simulateko - a flag indicating if flux variability should be run (an optional argument: default is '0')
		bool minimizeflux - a flag indicating if flux variability should be run (an optional argument: default is '0')
		bool findminmedia - a flag indicating if flux variability should be run (an optional argument: default is '0')
		string notes - a string of notes to attach to the FBA study (an optional argument; defaul is '')
		fba_id fba - ID under which the FBA results should be saved (an optional argument; defaul is 'undef')
		workspace_id workspace - workspace where FBA results will be saved (a required argument)
		bool add_to_model - a flag indicating if the FBA study should be attached to the model to support viewing results (an optional argument: default is '0')
		bool donot_submit_job - a flag indicating if the job should be submitted to the cluster (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
    	fbamodel_id model;
		workspace_id model_workspace;
		FBAFormulation formulation;
		bool fva;
		bool simulateko;
		bool minimizeflux;
		bool findminmedia;
		string notes;
		fba_id fba;
		workspace_id workspace;
		string auth;
		bool overwrite;
		bool add_to_model;
		bool donot_submit_job;
    } queue_runfba_params;
	/*
        Queues an FBA job in a single media condition
    */
	funcdef queue_runfba(queue_runfba_params input) returns (object_metadata output);
   
   /* Input parameters for the "queue_gapfill_model" function.
	
		fbamodel_id model - ID of the model that gapfill should be run on (a required argument)
		workspace_id model_workspace - workspace where model for gapfill should be run (an optional argument; default is the value of the workspace argument)
		GapfillingFormulation formulation - a hash specifying the parameters for the gapfill study (an optional argument)
		phenotypeSet_id phenotypeSet - ID of a phenotype set against which gapfilled model should be simulated (an optional argument: default is 'undef')
		workspace_id phenotypeSet_workspace - workspace containing phenotype set to be simulated (an optional argument; default is the value of the workspace argument)
		bool integrate_solution - a flag indicating if the first solution should be integrated in the model (an optional argument: default is '0')
		fbamodel_id out_model - ID where the gapfilled model will be saved (an optional argument: default is 'undef')
		gapfill_id gapFill - ID to which gapfill solution will be saved (an optional argument: default is 'undef')
		workspace_id workspace - workspace where gapfill results will be saved (a required argument)
		bool donot_submit_job - a flag indicating if the job should be submitted to the cluster (an optional argument: default is '0')
		int timePerSolution - maximum time to spend to obtain each solution
		int totalTimeLimit - maximum time to spend to obtain all solutions
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id model_workspace;
		GapfillingFormulation formulation;
		phenotypeSet_id phenotypeSet;
		workspace_id phenotypeSet_workspace;
		bool integrate_solution;
		fbamodel_id out_model;
		workspace_id workspace;
		gapfill_id gapFill;
		int timePerSolution;
		int totalTimeLimit;
		string auth;
		bool overwrite;
		bool donot_submit_job;
    } gapfill_model_params;
    /*
        Queues an FBAModel gapfilling job in single media condition
    */
    funcdef queue_gapfill_model(gapfill_model_params input) returns (object_metadata output);
    
    /* Input parameters for the "queue_gapfill_model" function.
	
		fbamodel_id model - ID of the model that gapgen should be run on (a required argument)
		workspace_id model_workspace - workspace where model for gapgen should be run (an optional argument; default is the value of the workspace argument)
		GapgenFormulation formulation - a hash specifying the parameters for the gapgen study (an optional argument)
		phenotypeSet_id phenotypeSet - ID of a phenotype set against which gapgened model should be simulated (an optional argument: default is 'undef')
		workspace_id phenotypeSet_workspace - workspace containing phenotype set to be simulated (an optional argument; default is the value of the workspace argument)
		bool integrate_solution - a flag indicating if the first solution should be integrated in the model (an optional argument: default is '0')
		fbamodel_id out_model - ID where the gapgened model will be saved (an optional argument: default is 'undef')
		gapgen_id gapGen - ID to which gapgen solution will be saved (an optional argument: default is 'undef')
		workspace_id workspace - workspace where gapgen results will be saved (a required argument)
		int timePerSolution - maximum time to spend to obtain each solution
		int totalTimeLimit - maximum time to spend to obtain all solutions
		bool donot_submit_job - a flag indicating if the job should be submitted to the cluster (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id model_workspace;
		GapgenFormulation formulation;
		phenotypeSet_id phenotypeSet;
		workspace_id phenotypeSet_workspace;
		bool integrate_solution;
		fbamodel_id out_model;
		workspace_id workspace;
		gapgen_id gapGen;
		string auth;
		int timePerSolution;
		int totalTimeLimit;
		bool overwrite;
		bool donot_submit_job;
    } gapgen_model_params;
    /*
        Queues an FBAModel gapfilling job in single media condition
    */
    funcdef queue_gapgen_model(gapgen_model_params input) returns (object_metadata output);
    
    /* Input parameters for the "queue_wildtype_phenotype_reconciliation" function.
	
		fbamodel_id model - ID of the model that reconciliation should be run on (a required argument)
		workspace_id model_workspace - workspace where model for reconciliation should be run (an optional argument; default is the value of the workspace argument)
		FBAFormulation formulation - a hash specifying the parameters for the reconciliation study (an optional argument)
		GapfillingFormulation gapfill_formulation - a hash specifying the parameters for the gapfill study (an optional argument)
		GapgenFormulation gapgen_formulation - a hash specifying the parameters for the gapgen study (an optional argument)
		phenotypeSet_id phenotypeSet - ID of a phenotype set against which reconciled model should be simulated (an optional argument: default is 'undef')
		workspace_id phenotypeSet_workspace - workspace containing phenotype set to be simulated (an optional argument; default is the value of the workspace argument)
		fbamodel_id out_model - ID where the reconciled model will be saved (an optional argument: default is 'undef')
		list<gapgen_id> gapGens - IDs of gapgen solutions (an optional argument: default is 'undef')
		list<gapfill_id> gapFills - IDs of gapfill solutions (an optional argument: default is 'undef')
		bool queueSensitivityAnalysis - flag indicating if sensitivity analysis should be queued to run on solutions (an optional argument: default is '0')
		bool queueReconciliationCombination - flag indicating if reconcilication combination should be queued to run on solutions (an optional argument: default is '0')
		workspace_id workspace - workspace where reconciliation results will be saved (a required argument)
		bool donot_submit_job - a flag indicating if the job should be submitted to the cluster (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id model_workspace;
		FBAFormulation fba_formulation;
		GapfillingFormulation gapfill_formulation;
		GapgenFormulation gapgen_formulation;
		phenotypeSet_id phenotypeSet;
		workspace_id phenotypeSet_workspace;
		fbamodel_id out_model;
		workspace_id workspace;
		list<gapfill_id> gapFills;
		list<gapgen_id> gapGens;
		bool queueSensitivityAnalysis;
		bool queueReconciliationCombination;
		string auth;
		bool overwrite;
		bool donot_submit_job;
    } wildtype_phenotype_reconciliation_params;
    /*
        Queues an FBAModel reconciliation job
    */
    funcdef queue_wildtype_phenotype_reconciliation(wildtype_phenotype_reconciliation_params input) returns (object_metadata output);
    
    /* Input parameters for the "queue_reconciliation_sensitivity_analysis" function.
	
		fbamodel_id model - ID of the model that sensitivity analysis should be run on (a required argument)
		workspace_id model_workspace - workspace where model for sensitivity analysis should be run (an optional argument; default is the value of the workspace argument)
		FBAFormulation formulation - a hash specifying the parameters for the sensitivity analysis study (an optional argument)
		GapfillingFormulation gapfill_formulation - a hash specifying the parameters for the gapfill study (an optional argument)
		GapgenFormulation gapgen_formulation - a hash specifying the parameters for the gapgen study (an optional argument)
		phenotypeSet_id phenotypeSet - ID of a phenotype set against which sensitivity analysis model should be simulated (an optional argument: default is 'undef')
		workspace_id phenotypeSet_workspace - workspace containing phenotype set to be simulated (an optional argument; default is the value of the workspace argument)
		fbamodel_id out_model - ID where the sensitivity analysis model will be saved (an optional argument: default is 'undef')
		list<gapgen_id> gapGens - IDs of gapgen solutions (an optional argument: default is 'undef')
		list<gapfill_id> gapFills - IDs of gapfill solutions (an optional argument: default is 'undef')
		bool queueReconciliationCombination - flag indicating if sensitivity analysis combination should be queued to run on solutions (an optional argument: default is '0')
		workspace_id workspace - workspace where sensitivity analysis results will be saved (a required argument)
		bool donot_submit_job - a flag indicating if the job should be submitted to the cluster (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id workspace;
		phenotypeSet_id phenotypeSet;
		
		FBAFormulation fba_formulation;
		workspace_id model_workspace;
		workspace_id phenotypeSet_workspace;
		
		list<gapfill_id> gapFills;
		list<gapgen_id> gapGens;
		bool queueReconciliationCombination;
		string auth;
		bool overwrite;
		bool donot_submit_job;
    } queue_reconciliation_sensitivity_analysis_params;
    /*
        Queues an FBAModel reconciliation job
    */
    funcdef queue_reconciliation_sensitivity_analysis(wildtype_phenotype_reconciliation_params input) returns (object_metadata output);
    
    /* Input parameters for the "queue_combine_wildtype_phenotype_reconciliation" function.
	
		fbamodel_id model - ID of the model that solution combination should be run on (a required argument)
		workspace_id model_workspace - workspace where model for solution combination should be run (an optional argument; default is the value of the workspace argument)
		FBAFormulation formulation - a hash specifying the parameters for the solution combination study (an optional argument)
		GapfillingFormulation gapfill_formulation - a hash specifying the parameters for the gapfill study (an optional argument)
		GapgenFormulation gapgen_formulation - a hash specifying the parameters for the gapgen study (an optional argument)
		phenotypeSet_id phenotypeSet - ID of a phenotype set against which solution combination model should be simulated (an optional argument: default is 'undef')
		workspace_id phenotypeSet_workspace - workspace containing phenotype set to be simulated (an optional argument; default is the value of the workspace argument)
		fbamodel_id out_model - ID where the solution combination model will be saved (an optional argument: default is 'undef')
		list<gapgen_id> gapGens - IDs of gapgen solutions (an optional argument: default is 'undef')
		list<gapfill_id> gapFills - IDs of gapfill solutions (an optional argument: default is 'undef')
		workspace_id workspace - workspace where solution combination results will be saved (a required argument)
		bool donot_submit_job - a flag indicating if the job should be submitted to the cluster (an optional argument: default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
    typedef structure {
		fbamodel_id model;
		workspace_id model_workspace;
		FBAFormulation fba_formulation;
		GapfillingFormulation gapfill_formulation;
		GapgenFormulation gapgen_formulation;
		phenotypeSet_id phenotypeSet;
		workspace_id phenotypeSet_workspace;
		fbamodel_id out_model;
		workspace_id workspace;
		list<gapfill_id> gapFills;
		list<gapgen_id> gapGens;
		string auth;
		bool overwrite;
		bool donot_submit_job;
    } combine_wildtype_phenotype_reconciliation_params;
    /*
        Queues an FBAModel reconciliation job
    */
    funcdef queue_combine_wildtype_phenotype_reconciliation(combine_wildtype_phenotype_reconciliation_params input) returns (object_metadata output);
    
    /* Input parameters for the "jobs_done" function.
	
		job_id jobid - ID of the job object (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
	typedef structure {
		job_id jobid;
		string auth;
    } jobs_done_params;
	/*
        Mark specified job as complete and run postprocessing
    */
	funcdef jobs_done(jobs_done_params input) returns (JobObject output);

	/* Input parameters for the "check_job" function.
	
		job_id jobid - ID of the job object (a required argument)
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
	typedef structure {
		job_id jobid;
		string auth;
    } check_job_params;
    /*
        Retreives job data given a job ID
    */
    funcdef check_job(check_job_params input) returns (JobObject output);       
	
	/* Input parameters for the "run_job" function.
	
		job_id jobid - ID of the job object (a required argument)
		int index - index of subobject to be run (an optional argument; default is '0')
		string auth - the authentication token of the KBase account changing workspace permissions; must have 'admin' privelages to workspace (an optional argument; user is "public" if auth is not provided)
		
	*/
	typedef structure {
		job_id jobid;
		int index;
		string auth;
    } run_job_params;
	/*
        Runs specified job
    */
	funcdef run_job(run_job_params input) returns (JobObject output);
};
