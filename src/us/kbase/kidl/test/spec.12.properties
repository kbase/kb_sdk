/*
 * This service wraps the PhiSpy service as defined at
 * http://sourceforge.net/projects/phispy.
 */
module Phispy
{

    /*
     * Following are the standard genome typed object defintions as copied from
     * the genome_annotation service specification.
     */

    typedef string genome_id;
    typedef string feature_id;
    typedef string contig_id;
    typedef string feature_type;

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

    /* represents a feature on the genome
       location on the contig with a type,
       and if a protein has translation,
       any aliases associated
       current history of annoation in style of SEED
    */
    typedef structure {
	feature_id id;
	location location;
	feature_type type;
	string function;
	string protein_translation;
	list<string> aliases;
	list<annotation> annotations;
    } feature;

    /* Data for DNA contig */
    typedef structure {
	contig_id id;
	string dna;
    } contig;

    /* All of the information about particular genome */
    typedef structure {
	genome_id id;
	string scientific_name;
	string domain;
	int genetic_code;
	string source;
	string source_id;
	
	list<contig> contigs;
	list<feature> features;
    } genomeTO;


    /* 
     * We define a type that represents the available phispy training sets.
     */
    typedef structure
    {
	int training_set_id;
	string genome_name;
    } TrainingSet;

    /*
     * Retrieve all available training sets.
     */
    funcdef get_all_training_sets() returns (list<TrainingSet>);

    /*
     * Analysis results from the prophage finder.
     */
    typedef tuple<feature_id, contig_id, int start, int stop, int position, 
    	float rank, float my_status, float pp, int final_status,
	int start_of_attL, int end_of_attL, int start_of_attR, int end_of_attR, 
	string sequence_of_attL, string sequence_of_attR> prophage_feature;

    /* 
     * Find prophages in the given genome.
     * 
     * The underlying tool uses the following files from a SEED directory:
     *   contigs
     *   Features/peg/tbl
     *   assigned_functions
     *   Feature/rna/tbl
     */

    funcdef find_prophages(int training_set_id, genomeTO) 
    		returns (genomeTO, list<prophage_feature> analysis);
};
