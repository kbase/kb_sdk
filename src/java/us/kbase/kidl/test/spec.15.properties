/*
Given a feature ID that is a protein, the protein info service
returns various annotations such as domain annotations, orthologs
in other genomes, and operons.
*/

module ProteinInfo {

	/*
	A fid is a unique identifier of a feature.
	*/
	typedef string fid;
	
	/*
	A domain_id is an identifier of a protein domain or family
	(e.g., COG593, TIGR00362). Most of these are stable identifiers
	that come from external curated libraries, such as COG or InterProScan,
	but some may be unstable identifiers that come from automated
	analyses like FastBLAST. (The current implementation includes only
	COG and InterProScan HMM libraries, such as TIGRFam and Pfam.)
	*/
	typedef string domain_id;


	/*
	A neighbor_threshold is a floating point number indicating a bound
	for the neighbor score */
	typedef float neighbor_threshold;

	/*
	Neighbor is a hash of fids to a neighbor score
	*/
	typedef mapping<fid, float> neighbor;

	/*
	Domains are a list of domain_ids.
	*/
	typedef list<string> domains;

	/*
	ECs are a list of Enzyme Commission identifiers.
	*/
	typedef list<string> ec;

	/*
	GOs are a list of Gene Ontology identifiers.
	*/
	typedef list<string> go;

	/*
	IPRs are a list of InterPro identifiers.
	*/
	typedef list<string> ipr;

	/*
	An operon is represented by a list of fids
	which make up that operon.  The order within the list is not
	defined; the fids_to_locations method can report on the
	order of the fids in the operon.
	*/
	typedef list<fid> operon;

	/*
	Orthologs are a list of fids which are orthologous to a given fid.
	*/
	typedef list<fid> orthologs;

	/*
	fids_to_operons takes as input a list of feature
	ids and returns a mapping of each fid to the operon
	in which it is found. The list of fids in the operon
	is not necessarily in the order that the fids are found
	on the genome.  (fids_to_operons is currently not properly
	implemented.)
	*/
	funcdef fids_to_operons (list<fid> fids) returns (mapping<fid, operon>);

	/*
	fids_to_domains takes as input a list of feature ids, and
	returns a mapping of each fid to its domains. (This includes COG,
	even though COG is not part of InterProScan.)
	*/
	funcdef fids_to_domains (list<fid> fids) returns (mapping<fid, domains>);

	/*
	domains_to_fids takes as input a list of domain_ids, and
	returns a mapping of each domain_id to the fids which have that
	domain. (This includes COG, even though COG is not part of
	InterProScan.)
	*/
	funcdef domains_to_fids (domains domain_ids) returns (mapping<domain_id, list<fid>>);

	/*
	fids_to_ipr takes as input a list of feature ids, and returns
	a mapping of each fid to its IPR assignments. These can come from
	HMMER or from non-HMM-based InterProScan results.
	*/
	funcdef fids_to_ipr (list<fid> fids) returns (mapping<fid,ipr>);

	/*
	fids_to_orthologs takes as input a list of feature ids, and
	returns a mapping of each fid to its orthologous fids in
	all genomes.
	*/
	funcdef fids_to_orthologs (list<fid> fids) returns (mapping<fid, orthologs>);

	/*
	fids_to_ec takes as input a list of feature ids, and returns
	a mapping of each fid to its Enzyme Commission numbers (EC).
	*/
	funcdef fids_to_ec (list<fid> fids) returns (mapping<fid,ec>);

	/*
	fids_to_go takes as input a list of feature ids, and returns
	a mapping of each fid to its Gene Ontology assignments (GO).
	*/
	funcdef fids_to_go (list<fid> fids) returns (mapping<fid,go>);

	/*
	fid_to_neighbor takes as input a single feature id, and
	a neighbor score threshold and returns a list of neighbors
	where neighbor score >= threshold */
	funcdef fid_to_neighbors(fid id, neighbor_threshold thresh) returns (neighbor);

	/*
	fidlist_to_neighbors takes as input a list of feature ids, and
	a minimal neighbor score, and returns a mapping of each fid to
	its neighbors, based on neighbor score >= threshold */
	funcdef fidlist_to_neighbors(list<fid> fids, neighbor_threshold thresh) returns (mapping<fid, list<neighbor>>);


};
