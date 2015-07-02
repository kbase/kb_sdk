/*
This module will translate KBase ids to MicrobesOnline ids and
vice-versa. For features, it will initially use MD5s to perform
the translation.

The MOTranslation module will ultimately be deprecated, once all
MicrobesOnline data types are natively stored in KBase. In general
the module and methods should not be publicized, and are mainly intended
to be used internally by other KBase services (specifically the protein
info service).
*/


module MOTranslation {

	/*
	protein is an MD5 in KBase. It is the primary lookup between
	KBase fids and MicrobesOnline locusIds.
	*/
        typedef string protein;
	/*
	kbaseId can represent any object with a KBase identifier. 
	In the future this may be used to translate between other data
	types, such as contig or genome.
	*/
        typedef string kbaseId;
	/*
	genomeId is a kbase id of a genome
	*/
        typedef kbaseId genomeId;
	
	
	/*
	fid is a feature id in KBase.
	*/
        typedef string fid;
	/*
	moLocusId is a locusId in MicrobesOnline. It is analogous to a fid
	in KBase.
	*/
        typedef int moLocusId;
	/*
	moScaffoldId is a scaffoldId in MicrobesOnline.  It is analogous to
	a contig kbId in KBase.
	*/
        typedef int moScaffoldId;
	/*
	moTaxonomyId is a taxonomyId in MicrobesOnline.  It is somewhat analogous
	to a genome kbId in KBase.  It generally stores the NCBI taxonomy ID,
	though sometimes can store an internal identifier instead.
	*/
        typedef int moTaxonomyId;


	/*
	fids_to_moLocusIds translates a list of fids into MicrobesOnline
	locusIds. It uses proteins_to_moLocusIds internally.
	*/
        funcdef fids_to_moLocusIds(list<fid> fids) returns (mapping<fid,list<moLocusId>>);
	/*
	proteins_to_moLocusIds translates a list of proteins (MD5s) into
	MicrobesOnline locusIds.
	*/
        funcdef proteins_to_moLocusIds(list<protein> proteins) returns (mapping<protein,list<moLocusId>>);

	/*
	moLocusIds_to_fids translates a list of MicrobesOnline locusIds
	into KBase fids. It uses moLocusIds_to_proteins internally.
	*/
        funcdef moLocusIds_to_fids(list<moLocusId> moLocusIds) returns (mapping<moLocusId,list<fid>>);
	/*
	moLocusIds_to_proteins translates a list of MicrobesOnline locusIds
	into proteins (MD5s).
	*/
        funcdef moLocusIds_to_proteins(list<moLocusId> moLocusIds) returns (mapping<moLocusId,protein>);



	
	/* AA sequence of a protein */
	typedef string protein_sequence;
	
	/* internally consistant and unique id of a protein (could just be integers 0..n), necessary
	for returning results */
	typedef string protein_id;
	
	/* Used to indicate a single nucleotide/residue location in a sequence */ 
	typedef int position;
	
	/* A short note used to convey the status or explanaton of a result, or in some cases a log of the
	method that was run */
	typedef string status;
	
	/*
	A structure for specifying the input sequence queries for the map_to_fid method.  This structure, for
	now, assumes you will be making queries with identical genomes, so it requires the start and stop.  In the
	future, if this assumption is relaxed, then start and stop will be optional parameters.  We should probably
	also add an MD5 string which can optionally be provided so that we don't have to compute it on the fly.
	
		protein_id id         - arbitrary ID that must be unique within the set of query sequences
		protein_sequence seq  - the one letter code AA sequence of the protein
		position start        - the start position of the start codon in the genome contig (may be a larger
		                        number than stop if the gene is on the reverse strand)
		position stop         - the last position of he stop codon in the genome contig
	*/
	typedef structure {
		protein_id id;
		protein_sequence seq;
		position start;
		position stop;
	} query_sequence;
	
	
	/*
	A structure for specifying the input md5 queries for the map_to_fid_fast method.  This structure assumes
	you will be making queries with identical genomes, so it requires the start and stop.
	
		protein_id id         - arbitrary ID that must be unique within the set of query sequences
		protein md5           - the computed md5 of the protein sequence
		position start        - the start position of the start codon in the genome contig (may be a larger
		                        number than stop if the gene is on the reverse strand)
		position stop         - the last position of he stop codon in the genome contig
	*/
	typedef structure {
		protein_id id;
		protein md5;
		position start;
		position stop;
	} query_md5;
	
	/*
	A simple structure which returns the best matching FID to a given query (see query_sequence) and attaches
	a short status string indicating how the match was made, or which consoles you after a match could not
	be made.
	
		fid best_match - the feature ID of a KBase feature that offers the best mapping to your query
		status status  - a short note explaining how the match was made
	*/
	typedef structure {
	    fid best_match;
	    status status;
	} result;
	
	
	/*
	A general method to lookup the best matching feature id in a specific genome for a given protein sequence.
	
	NOTE: currently the intended use of this method is to map identical genomes with different gene calls, although it still
	can work for fairly similar genomes.  But be warned!!  It may produce incorrect results for genomes that differ!
	
	This method operates by first checking the MD5 and position of each sequence and determining if there is an exact match,
	(or an exact MD5 match +- 30bp).  If none are found, then a simple blast search is performed.  Currently the blast search
	is completely overkill as it is used simply to look for 50% overlap of genes. Blast was chosen, however, because it is
	anticipated that this, or a very similar implementation of this method, will be used more generally for mapping features
	on roughly similar genomes.  Keep very much in mind that this method is not designed to be a general homology search, which
	should be done with more advanced methods.  Rather, this method is designed more for bookkeeping purposes when data based on
	one genome with a set of gene calls needs to be applied to a genome with a second set of gene calls.
	
	see also the cooresponds method of the CDMI.
	*/
	funcdef map_to_fid(list<query_sequence>query_sequences, genomeId genomeId) returns (mapping<protein_id,result>, status log);
	
	
	/*
	Performs the same function as map_to_fid, except it does not require protein sequences to be defined. Instead, it assumes
	genomes are identical and simply looks for genes on the same strand that overlap by at least 50%. Since no sequences are
	compared, this method is fast.  But, since no sequences are compared, this method only makes sense for identical genomes
	*/
	funcdef map_to_fid_fast(list<query_md5>query_md5s, genomeId genomeId) returns (mapping<protein_id,result>, status log);
	
	
	/*
	A method designed to map MicrobesOnline locus ids to the features of a specific target genome in kbase.  Under the hood, this
	method simply fetches MicrobesOnline data and calls the 'map_to_fid' method defined in this service.  Therefore, all the caveats
	and disclaimers of the 'map_to_fid' method apply to this function as well, so be sure to read the documenation for the 'map_to_fid'
	method as well!
	*/
	funcdef moLocusIds_to_fid_in_genome(list<moLocusId> moLocusIds, genomeId genomeId) returns (mapping<moLocusId,result>, status log);
	
	/*
	Performs the same function as moLocusIds_to_fid_in_genome, but does not retrieve protein sequences for the locus Ids - it simply
	uses md5 information and start/stop positions to identify matches.  It is therefore faster, but will not work if genomes are not
	identical.
	*/
	funcdef moLocusIds_to_fid_in_genome_fast(list<moLocusId> moLocusIds, genomeId genomeId) returns (mapping<moLocusId,result>, status log);
	
	
	
	/*
	A method to map a MicrobesOnline genome (identified by taxonomy Id) to the set of identical kbase genomes based on an MD5 checksum
	of the contig sequences.  If you already know your MD5 value for your genome (computed in the KBase way), then you should avoid this
	method and directly query the CDS using the CDMI API, which includes a method 'md5s_to_genomes'.
	*/
	funcdef moTaxonomyId_to_genomes(moTaxonomyId moTaxonomyId) returns (list<genomeId>);
	
	
};
