/*	The translation service translates between different things. These are mainly
	mappings. A genbank taxonomic scientific name to a genbank taxonomic common
	name; A Seed role to a Geneontology term; MORE 

	Genbank taxonomic name translations.

	Genbank taxonomic name translations allow on the fly translation between
	the different name classes supported in the NCBI taxonomy database. These
	name classes include:

		misspelling, genbank anamorph, scientific name, synonym 
		blast name, genbank synonym, equivalent name, includes 
		acronym, in-part, anamorph, authority, genbank common name 
		genbank acronym, common name, misnomer, teleomorph 

	Although, the most common are translations between scientific name, synonym,
	equivelant name and common name.

*/
module TaxonomyTranslation {
    typedef string Name;
    typedef int Tax_id;

/*    typedef structure {
        string scientific_name;
        list<string> common_names;
        list<string> synonyms;
        list<string> misspellings;
        list<string> equivalent_names;
        list<string> in_parts;
        list<string> anamorphs;
        list<string> includes;
        list<string> acronyms;
        list<string> authorities;
        list<string> misnomers;
        list<string> teleomorphs;
        list<string> blast_names;
        list<string> genbank_synonyms;
        list<string> genbank_anamorphs;
        list<string> genbank_acronyms;
        list<string> genbank_common_names;
    } Names;
*/
typedef structure {
	string t;
} Names;
    typedef mapping<Tax_id tax_id, Names names> Translations;

    /* Returns all possible name translations for a given name. */
    funcdef get_all_translations(Name name) returns (Translations translations);

    /* Returns a mapping between tax_id and scientific name for a given name. */
    funcdef get_scientific_names_by_name(Name name) returns (mapping<Tax_id tax_id, string scientific_name>);

    /* Returns a mapping between tax_id and a list of all associated names for a given name. */
    funcdef get_all_names_by_name(Name name) returns (mapping<Tax_id tax_id, list<Name> names>);

    /* Returns the scientific name for a given tax id. */
    funcdef get_scientific_name_by_tax_id(Tax_id tax_id) returns (Name name);

    /* Returns the tax id for a given scientific name. */
    funcdef get_tax_id_by_scientific_name(Name name) returns (Tax_id tax_id);

    /* Returns a list of tax ids for a given name. */
    funcdef get_tax_ids_by_name(Name name) returns (list<Tax_id> tax_ids);
    
    /* Returns a list of names for a given tax id. */
    funcdef get_all_names_by_tax_id(Tax_id tax_id) returns (list<Name> names);


};
