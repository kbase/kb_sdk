/* 
	Module KBaseNetworks version 1.0
	This module provides access to various types of network-related datasets across all domains of KBase in a unified format.

	Networks are composed of Nodes and Edges. Nodes represent entities from the datasets (e.g., genes, proteins,
	biclusters, subystems, etc.), and edges represent relationships (e.g., protein-protein interactions,
	gene-subsystem membership, etc.). Networks can contain Nodes and Edges from multiple datasets.

	All methods in this module can be classified into two types: 
	i. getting general information about datasets and network types currently available via KBaseNetworks API
	   For example: allDatasetSources(), allNetworkTypes(), datasetSource2Datasets()
	ii. building various types of Network objects
	   For example: buildFirstNeighborNetwork(), buildInternalNetwork()
*/

module KBaseNetworks : KBaseNetworks
{

	/* The name of a dataset that can be accessed as a source for creating a network */
	typedef string DatasetSourceRef;
	
	/* Type of network that can be created from a dataset */
	typedef string NetworkType;
	
	/* Type of node in a network */
	typedef string NodeType;
	
	/* Type of edge in a network */
	typedef string EdgeType;
	
	/* NCBI taxonomy id */	
	typedef string Taxon;
		
	/* Helper type to represent boolean values; either "true" or "false" */	
	typedef string Boolean; 
   
	/* Provides detailed information about the source of a dataset.
		string id - A unique KBase identifier of a dataset source
		string name - A name of a dataset source
    	    	DatasetSourceRef reference - Reference to a dataset source
    	    	string description - General description of a dataset source
    	    	string resourceURL - URL of the public web resource hosting the data represented by this dataset source
		
	*/	
  	typedef structure {
		string id;
    		string name;
		DatasetSourceRef reference;    
		string description;
		string resourceURL;
  	} DatasetSource;  
  
  
  	/* Represents a particular dataset.
		string id - A unique KBase identifier of a dataset 
    	    	string name - The name of a dataset
    	    	string description - Description of a dataset
    	    	NetworkType networkType - Type of network that can be generated from a given dataset
		DatasetSourceRef sourceReference - Reference to a dataset source
		list<Taxon> taxons - A list of NCBI taxonomy ids of all organisms for which genomic features (genes, proteins, etc) are used in a given dataset 
    	    	mapping<string,string> properties - Other properties  		  		
  	*/
 	typedef structure {
	    	string id;
    		string name;
		string description;
		NetworkType networkType;
		DatasetSourceRef sourceReference;
		list<Taxon> taxons;
		mapping<string,string> properties;
  	} Dataset;
  

	/* Represents a node in a network.
	   	string id - A unique KBase identifier of a node 
		string name - String representation of a node. It should be a concise but informative representation that is easy for a person to read.
    	    	string entityId - The identifier of a KBase entity represented by a given node 
		NodeType type - The type of a node
    	    	mapping<string,string> properties - Other properties of a node
    	    	mapping<string,string> userAnnotations - User annotations of a node		
	*/  
  	typedef structure {
   		string id;  
		string name;
		string entityId;
		NodeType type;
		mapping<string,string> properties;
		mapping<string,string> userAnnotations;
  	} Node;
  
  	/* Represents an edge in a network.
	   	string id - A unique KBase identifier of an edge 
    	    	string name - String representation of an edge. It should be a concise but informative representation that is easy for a person to read.
    	    	string nodeId1 - Identifier of the first node (source node, if the edge is directed) connected by a given edge 
    	    	string nodeId2 - Identifier of the second node (target node, if the edge is directed) connected by a given edge
    	    	Boolean	directed - Specify whether the edge is directed or not. "true" if it is directed, "false" if it is not directed
    	    	float confidence - Value from 0 to 1 representing a probability that the interaction represented by a given edge is a true interaction
    	    	float strength - Value from 0 to 1 representing a strength of an interaction represented by a given edge
    	    	string datasetId - The identifier of a dataset that provided an interaction represented by a given edge
		mapping<string,string> properties - Other edge properties
    	    	mapping<string,string> userAnnotations - User annotations of an edge    	    		
  	*/
  	typedef structure {
	    	string id;  
    		string name;
		string nodeId1;
		string nodeId2;
		Boolean	directed;
		float confidence;
		float strength;
		string datasetId;
		mapping<string,string> properties;
		mapping<string,string> userAnnotations;  
  	} Edge;
  

	/* Represents a network
	        string id - A unique KBase identifier of a network 
    	    	string name - String representation of a network. It should be a concise but informative representation that is easy for a person to read.
		list<Edge> edges - A list of all edges in a network
		list<Node> nodes - A list of all nodes in a network
		list<Dataset> datasets - A list of all datasets used to build a network
		mapping<string,string> properties - Other properties of a network
		mapping<string,string> userAnnotations - User annotations of a network  
	*/  
  	typedef structure {    
		string id;
		string name;
		list<Edge> edges;
		list<Node> nodes;
		list<Dataset> datasets;
		mapping<string,string> properties;
		mapping<string,string> userAnnotations;  
  	} Network;
  



 	/* 
	   Returns a list of all datasets that can be used to create a network 
	*/
	funcdef allDatasets() returns(list<Dataset> datasets);
    
   	/* 
	   Returns a list of all dataset sources available in KBase via KBaseNetworks API 
	*/  
	funcdef allDatasetSources() returns(list<DatasetSource> datasetSources);
	
 	/* 
	   Returns a list of all types of networks that can be created 
	*/	
	funcdef allNetworkTypes() returns(list<NetworkType> networkTypes);

   	/* 
	   Returns a list of all datasets from a given dataset source   		
	   	   DatasetSourceRef datasetSourceRef - A reference to a dataset source   		   		
   	*/
  	funcdef datasetSource2Datasets(DatasetSourceRef datasetSourceRef) returns(list<Dataset> datasets);
  	
  	/*
	   Returns a list of all datasets that can be used to build a network for a particular genome represented by NCBI taxonomy id. 
  		Taxon taxon - NCBI taxonomy id
  	*/
  	funcdef taxon2Datasets(Taxon taxon) returns(list<Dataset> datasets);
  	
  	/*
	   Returns a list of all datasets that can be used to build a network of a given type.
  	   	NetworkType networkType - The type of network
  	
  	*/
  	funcdef networkType2Datasets(NetworkType networkType) returns(list<Dataset> datasets);
  	
	/*
	   Returns a list of all datasets that have at least one interaction for a given KBase entity
		
	*/  	
  	funcdef entity2Datasets(string entityId) returns(list<Dataset> datasets);

  
	/*
	   Returns a "first-neighbor" network constructed based on a given list of datasets. A first-neighbor network contains 
	   "source" nodes and all other nodes that have at least one interaction with the "source" nodes. 
	   Only interactions of given types are considered.    
  	   	list<string> datasetIds - List of dataset identifiers to be used for building a network
  		list<string> entityIds - List of entity identifiers to be used as source nodes
  	   	list<EdgeType> edgeTypes - List of possible edge types to be considered for building a network
	*/    
  	funcdef buildFirstNeighborNetwork(list<string> datasetIds, list<string> entityIds, list<EdgeType> edgeTypes) returns(Network network);
  	
	/*
	   Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
	   "source" nodes and all other nodes that have at least one interaction with the "source" nodes. 
	   Only interactions of given types are considered. Additional cutOff parameter allows setting a threshold
	   on the strength of edges to be considered.   
  	   	list<string> datasetIds - List of dataset identifiers to be used for building a network
  		list<string> entityIds - List of entity identifiers to be used as source nodes
  	   	list<EdgeType> edgeTypes - List of possible edge types to be considered for building a network
  	   	float cutOff - The threshold on the strength of edges to be considered for building a network
  	   			
	*/  	  	
  	funcdef buildFirstNeighborNetworkLimtedByStrength(list<string> datasetIds, list<string> entityIds, list<EdgeType> edgeTypes, float cutOff) returns(Network network);
  	
  	
	/*
	   Returns an "internal" network constructed based on a given list of datasets. 
	   Internal network contains only the nodes defined by the geneIds parameter, 
	   and edges representing interactions between these nodes.  Only interactions of given types are considered.    
  	   	list<string> datasetIds - List of dataset identifiers to be used for building a network
  		list<string> geneIds - Identifiers of genes of interest for building a network 	
  	   	list<EdgeType> edgeTypes - List of possible edge types to be considered for building a network
  	   			
	*/    	
  	funcdef buildInternalNetwork(list<string> datasetIds, list<string> geneIds, list<EdgeType> edgeTypes) returns(Network network);
  	
  	
	/*
	   Returns an "internal" network constructed based on a given list of datasets. 
	   Internal network contains the only nodes defined by the geneIds parameter, 
	   and edges representing interactions between these nodes.  Only interactions of given types are considered. 
	   Additional cutOff parameter allows to set a threshold on the strength of edges to be considered.     
  	   	list<string> datasetIds - List of dataset identifiers to be used for building a network
  		list<string> geneIds - Identifiers of genes of interest for building a network 	
  	   	list<EdgeType> edgeTypes - List of possible edge types to be considered for building a network
 	   	float cutOff - The threshold on the strength of edges to be considered for building a network
  	   			
	*/     	
  	funcdef buildInternalNetworkLimitedByStrength(list<string> datasetIds, list<string> geneIds, list<EdgeType> edgeTypes, float cutOff) returns(Network network);

};
