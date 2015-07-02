/*
Plant Expression Service APIs 

 This module provides services for plant expression data in support of the coexpression
 network and ontology driven data needs of the plant sciences community. This version of
 the modules supports retrieval of the following information:
 1. Retrieval of GEO sample ID list for given EO (environmental ontology) and/or PO (plant ontology -plant tissues/organs of interest).
 2. Retrieval of the expression values for given GEO sample ID list.  
 3. For given expression values tables, it computes co-expression clusters or network (CLI only).

It will serve queries for tissue and condition specific gene expression co-expression network for biologically interesting genes/samples. Users can search differentially expressed genes in different tissues or in numerous experimental conditions or treatments (e.g various biotic or abiotic stresses). Currently the metadata annotation is provided for a subset of gene expression experiments from the NCBI GEO microarray experiments for Arabidopsis and Poplar. The samples of these experiments are manually annotated using plant ontology (PO) [http://www.plantontology.org/] and environment ontology (EO) [http://obo.cvs.sourceforge.net/viewvc/obo/obo/ontology/phenotype/environment/environment_ontology.obo]

*/

module PlantExpression : PlantExpression
{
  /* external environment ontology id 

     The supported EO ids are :
       EO:0007001	UV-B light regimen
       EO:0007002	UV-A light regimen
       EO:0007041	antibiotic regimen
       EO:0007049	soil environment
       EO:0007066	cytokinin regimen
       EO:0007067	hydroponic plant culture media
       EO:0007071	suspension cell culture media
       EO:0007075	high light intensity regimen
       EO:0007079	sulfate fertilizer regimen
       EO:0007105	abscisic acid regimen
       EO:0007106	Stramenopiles
       EO:0007107	Ascomycota
       EO:0007108	Proteobacteria
       EO:0007116	Hemiptera
       EO:0007128	intermittent light regimen
       EO:0007144	Pseudomonas spp.
       EO:0007149	chemical mutagen
       EO:0007158	sandy soil
       EO:0007162	continuous light regimen
       EO:0007173	warm/hot temperature regimen
       EO:0007174	cold temperature regimen
       EO:0007175	temperature environment
       EO:0007183	herbicide regimen
       EO:0007185	salt regimen
       EO:0007193	day light intensity
       EO:0007199	long day length regimen
       EO:0007200	short day length regimen
       EO:0007203	far red light regimen
       EO:0007207	red light regimen
       EO:0007218	blue light regimen
       EO:0007221	visible light regimen
       EO:0007233	Fungi
       EO:0007265	liquid growth media
       EO:0007266	tissue culture growth media
       EO:0007270	continuous dark (no light) regimen
       EO:0007271	low light intensity regimen
       EO:0007303	carbon nutrient regimen
       EO:0007373	mechanical damage
       EO:0007404	drought environment
       EO:0007409	brassinosteroid
*/
  typedef string EOID;                 
  /* external plant ontology id 

     The supported PO ids are :
       PO:0000003	whole plant
       PO:0000005	cultured cell
       PO:0000006	cultured protoplast
       PO:0009005	root
       PO:0009006	shoot
       PO:0009025	leaf
       PO:0009046	flower
       PO:0009049	Inflorescence
       PO:0009001	fruit
       PO:0009010	seed 

*/
  
  typedef string POID;                 
  /* external gene id */
  typedef string GeneID;               
  /* expression sample id from GEO (GSM#) */
  typedef string SampleID;             
  /* Series id from GEO (GSE#) */
  typedef string SeriesID;         
  /* list of external gene ids from species of interest */
  typedef list<GeneID> GeneIDList;     
 
  /* list of Sample ids */
  typedef list<SampleID> SampleIDList; 
  /* list of environment ontologies */
  typedef list<EOID> EOIDList;         
  /* list of plant ontologies */
  typedef list<POID> POIDList;         

  typedef list<SeriesID> SeriesIDList;
  typedef list<float> ValueList;
  typedef mapping<SampleID,ValueList> Sample;
  typedef list<Sample> SampleList;

  typedef structure {
    Sample series;
    GeneIDList genes;
  } Experiment;

  typedef mapping<SeriesID,Experiment> Experiments;
  typedef mapping<EOID,SampleIDList> EOID2Sample;
  typedef mapping<POID,SampleIDList> POID2Sample;
  typedef mapping<POID,string> POID2Description;
  typedef mapping<EOID,string> EOID2Description;
  

/*
 This function takes a list a GSM id. The GSM id can be stored as a csv file, containing one line. The output is the corresponding replicate id
*/
 funcdef get_repid_by_sampleid(SampleIDList ids) returns (Sample results);


/*  
 This function provides the expression data for each experiment corresponding to the given list of series (GSE#s). It first retrieves the experiments sample ids (GSM#s) for each series and subsequently, it extracts the expressed genes and their corresponding expression values for each experiment. It then returns a table of data containing GSE#, GSM#, Expressed Gene ID, and Expression Value.
*/
  funcdef get_experiments_by_seriesid(SeriesIDList ids) returns (Experiments results);

  
/*
This function provides the expression values  corresponding to each given experiment sample in the input list of sample ids (GSM#s).For each sample in the input list of samples, it extracts the expressed genes (kbase gene identifier) and their corresponding expression values.
*/
  funcdef get_experiments_by_sampleid(SampleIDList ids) returns (Experiment results);

/*
This function provides the expression values corresponding to the given sample and for given list of kbase gene identifiers.
Retrieve the expression values corresponding to each given sample in the input list of samples ((typically NCBI GEO series sample ids: GSM#s) for given list of genes (kbase identifier). 
*/
  funcdef get_experiments_by_sampleid_geneid(SampleIDList ids, GeneIDList gl) returns (Experiment results);

/*
Retrieve the list of expression samples (GSM#s) that correspond to one or more of the environmental conditions (EO) of interest.
*/
  funcdef get_eo_sampleidlist(EOIDList lst) returns (EOID2Sample results);

/*
Retrieve the list of expression samples (GSM#s) that corresponds to one or more of the plant tissue/organ (PO) type of interest.  
*/
  funcdef get_po_sampleidlist(POIDList lst) returns (POID2Sample results);

/*
=head2 getAllPO

 Retrieve the list of all plant ontology IDs (POIDs) currently available in the database.
*/
  funcdef get_all_po() returns (POID2Description results);


/*
 Retrieve the list of all plant environment ontology IDs (EOIDs) currently available in the database.
*/
  funcdef get_all_eo() returns (EOID2Description results);

/*
 Retrieve the list of selected plant ontology IDs (POIDs) description corresponding to an input
 list of POIDs.
*/
  funcdef get_po_descriptions(POIDList ids) returns (POID2Description results);

/*
 Retrieve the list of selected plant environment ontology IDs (EOIDs) description corresponding to an input list of EOIDs.
*/
  funcdef get_eo_descriptions(EOIDList ids) returns (EOID2Description results);
};
