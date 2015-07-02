/*
The KBase ID server provides access to the mappings between KBase identifiers and
external identifiers (the original identifiers for data that was migrated from
other databases into KBase).
*/
module IDServerAPI : IDServerAPI
{
/* 
A KBase ID is a string starting with the characters "kb|".

KBase IDs are typed. The types are designated using a short string. For instance,
"g" denotes a genome, "fp" denotes a feature representing a protein-encoding gene, etc.

KBase IDs may be hierarchical. If a KBase genome identifier is "kb|g.1234", a protein
within that genome may be represented as "kb|g.1234.fp.771".
*/
    typedef string kbase_id;

/* 
Each external database is represented using a short string. Microbes Online is "MOL",
the SEED is "SEED", etc.
*/
    typedef string external_db;

/*
External database identifiers are strings. They are the precise identifier used
by that database. It is important to note that if a database uses the same 
identifier space for more than one data type (for instance, if integers are used for
identifying both genomes and genes, and if the same number is valid for both a
genome and a gene) then the distinction must be made by using separate exgternal database
strings for the different types; e.g. DBNAME-GENE and DBNAME-GENOME for a 
database DBNAME that has overlapping namespace for genes and genomes).
*/
    typedef string external_id;

/*
A KBase identifier prefix. This is a string that starts with "kb|" and includes either a
single type designator (e.g. "kb|g") or is a prefix for a hierarchical identifier (e.g.
"kb|g.1234.fp").
*/
    typedef string kbase_id_prefix;

/*
Given a set of KBase identifiers, look up the associated external identifiers.
If no external ID is associated with the KBase id, no entry will be present in the return.
*/
    funcdef kbase_ids_to_external_ids(list<kbase_id> ids) returns (mapping<kbase_id, tuple<external_db, external_id>>);

/*
Given a set of external identifiers, look up the associated KBase identifiers.
If no KBase ID is associated with the external id, no entry will be present in the return.
*/
    funcdef external_ids_to_kbase_ids(external_db, list<external_id> ext_ids) returns (mapping<external_id, kbase_id>);

/*
Register a set of identifiers. All will be assigned identifiers with the given
prefix.

If an external ID has already been registered, the existing registration will be returned instead 
of a new ID being allocated.
*/
    funcdef register_ids(kbase_id_prefix prefix, external_db db_name, list<external_id> ids)
    	    returns (mapping<external_id ext_id, kbase_id kb_id>);

/*
Allocate a set of identifiers. This allows efficient registration of a large
number of identifiers (e.g. several thousand features in a genome).

The return is the first identifier allocated.
*/
    funcdef allocate_id_range(kbase_id_prefix, int count) returns (int starting_value);

/*
Register the mappings for a set of external identifiers. The
KBase identifiers used here were previously allocated using allocate_id_range.

Does not return a value.
*/
    funcdef register_allocated_ids(kbase_id_prefix prefix, external_db db_name, 
    	    	   		   mapping<external_id ext_id, int idx> assignments) returns();


};
