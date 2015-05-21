/*
   The similarity service exposes the SEED similarity server to the KBase. The
   similarity server stores precomputed all-to-all BLAST similarities for a
   large database of proteins; this database includes all genomes curated by the
   SEED project as well as a variety of third-party protein databases (NCBI
   nr, Uniprot/Swissprot, IMG, etc).

   While the SEED similarity server does not itself have knowledge of proteins
   with KBase identifiers, we use the MD5 signature of the protein sequence
   to perform lookups into the similarity server. Similarities returned from
   the similarity server are also identified with the MD5 signature, and are
   mapped back to KBase identifiers using the information in the KBase Central Store.
   */
 
module Sim {
    /*
       Each similarity returned is encapsulated in a sim_vec tuple. This tuple
       contains the similar protein identifiers, as well as the columns as seen in the
       blastall -m8 output..

       The columns in the tuple are defined as follows:

         0   id1        query sequence id
         1   id2        subject sequence id
         2   iden       percentage sequence identity
         3   ali_ln     alignment length
         4   mismatches  number of mismatch
         5   gaps       number of gaps
         6   b1         query seq match start
         7   e1         query seq match end
         8   b2         subject seq match start
         9   e2         subject seq match end
        10   psc        match e-value
        11   bsc        bit score
        12   ln1        query sequence length
        13   ln2        subject sequence length
        14   tool       tool used to produce similarities

       All following fields may vary by tool:

        15   loc1       query seq locations string (b1-e1,b2-e2,b3-e3)
        16   loc2       subject seq locations string (b1-e1,b2-e2,b3-e3)
        17   dist       tree distance

       We also return this column for any lookups when the kb_function2 flag
       is enabled:

        18  function2   The function associated with id2 in the KBase.
    */
       
	typedef tuple<
	    string id1,
	    string id2,
	    float iden,
	    int ali_ln,
	    int mismatches,
	    int gaps,
	    int b1,
	    int e1,
	    int b2,
	    int e2,
	    float psc,
	    float bsc,
	    int ln1,
	    int ln2,
	    string tool,
	    string def2,
	    string ali,
	    string function2
	> sim_vec;

    /*
       Option specification. The following options are available for the sims call:

         kb_only	Only return KBase identifiers (not raw MD5 or other external IDs).
	 kb_function2   For KB identifiers, return the function mapped to id2.
	 evalue_cutoff  Return similarities with an e-value better than this value.
	 max_sims       Return at most this many similarities. The number of values
	                may exceed this due to multiple identifiers mapping to the same sequence.
    */

	typedef structure {
	    int kb_only;
	    int kb_function2;
	    float evalue_cutoff;
	    int max_sims;
	} options;

    /*
	Retrieve precomputed protein similarities given a list of identifiers.

        The options parameter allows simple configuration of the call. The following
	values in the structure are interpreted:
	
         kb_only	Only return KBase identifiers (not raw MD5 or other external IDs).
	 kb_function2   For KB identifiers, return the function mapped to id2.
	 evalue_cutoff  Return similarities with an e-value better than this value.
	 max_sims       Return at most this many similarities. The number of values
	                may exceed this due to multiple identifiers mapping to the same sequence.

        Each similarity returned is encapsulated in a sim_vec tuple. This tuple
        contains the similar protein identifiers, as well as the columns as seen in the
        blastall -m8 output..

        The return is a list of tuples representing the similarity values. The indexes in the
        tuple are defined as follows:

          0   id1        query sequence id
          1   id2        subject sequence id
          2   iden       percentage sequence identity
          3   ali_ln     alignment length
          4   mismatches  number of mismatch
          5   gaps       number of gaps
          6   b1         query seq match start
          7   e1         query seq match end
          8   b2         subject seq match start
          9   e2         subject seq match end
         10   psc        match e-value
         11   bsc        bit score
         12   ln1        query sequence length
         13   ln2        subject sequence length
         14   tool       tool used to produce similarities

        All following fields may vary by tool:

         15   loc1       query seq locations string (b1-e1,b2-e2,b3-e3)
         16   loc2       subject seq locations string (b1-e1,b2-e2,b3-e3)
         17   dist       tree distance

        We also return this column for any lookups when the kb_function2 flag
        is enabled.

         18  function2   The function associated with id2 in the KBase.



    */
	funcdef sims(list<string> ids, options options) returns (list<sim_vec>);
};
