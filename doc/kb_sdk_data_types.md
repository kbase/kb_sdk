# KBase SDK - Data Types

## Data Type Definitions

Some, but not all, of the KBase data type definitions are available at
  https://github.com/kbase/data_api/tree/develop/experiments/workspace_typespecs.
Others may be examined by selecting an object of that type in a Narrative and clicking on the type to open the Landing Page for that type (make sure you click on the object type on the right, not the grouping on the left).
Then click on the 'Spec-file' tab.


## Base KBase and Workspace IDs and references

### kbase_id

A KBase ID is a string starting with the characters "kb|".  KBase IDs are typed. The types are designated using a short string. For instance," g" denotes a genome, "tree" denotes a Tree, and "aln" denotes a sequence alignment. KBase IDs may be hierarchical.  For example, if a KBase genome identifier is "kb|g.1234", a protein encoding gene within that genome may be represented as "kb|g.1234.peg.771".


### ref_type

An enumeration of reference types for a node.  Either the one letter abbreviation or full name can be given.

Supported types include:
- g | genome  => genome typed object or CDS data
- p | protein => protein sequence object or CDS data, often given as the MD5 of the sequence
- n | dna     => dna sequence object or CDS data, often given as the MD5 of the sequence
- f | feature => feature object or CDS data

*NOTE: THERE ARE MORE THAT SHOULD BE LISTED HERE*


### ws_obj_id

The fully qualified name of a workspace object that includes the workspace name.  For example:

    dylan:1424477476805/Carsonella_10.genome_set


### workspace_id / workspace?


### workspace_name / workspace?



## KBase Data Objects


#### SingleEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.SingleEndLibrary-2.1

##### data structure
##### obtaining
##### using
##### storing


### PairedEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.PairedEndLibrary-2.1

#### data structure
#### obtaining
#### using
#### storing


### ContigSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.ContigSet-3.0

#### data structure
#### obtaining
#### using
#### storing


### ReferenceAssembly
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.ReferenceAssembly-2.1

### FeatureSet

#### data structure
#### obtaining
#### using
#### storing


### GenomeSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseSearch.GenomeSet-2.1

#### data structure

Note: either ref or data is defined for an element, but not both.
- @optional metadata
- @optional ref
- @optional data


    { description: \‘genome_set_name\’,
      elements : { \‘genome_name1_1\’: { metadata: { \‘f1\’\: \‘v1\’,
                                                   ‘f2’: ‘v2’,
                                                   ...
                                                 }
                                       ref: ‘genome_ws_ref’,
                                       data: KBaseGenomes.Genome instance
                                     },
                    ‘genome_name_2’: ...
                  }
    }


#### obtaining
#### using
#### storing


### Genome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Genome-8.0

#### data structure
#### obtaining
#### using
#### storing


### DomainAnnotation
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainAnnotation-2.0

#### data structure
#### obtaining
#### using
#### storing


### Pangenome

#### data structure
#### obtaining
#### using
#### storing


### ProteomeComparison
https://narrative.kbase.us/functional-site/#/spec/type/GenomeComparison.ProteomeComparison-2.0

#### data structure
#### obtaining
#### using
#### storing


### FBAModel
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBAModel-7.1

#### data structure
#### obtaining
#### using
#### storing


### FBA
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBA-12.0

#### data structure
#### obtaining
#### using
#### storing


### Media
https://narrative.kbase.us/functional-site/#/spec/type/KBaseBiochem.Media-1.0

#### data structure
#### obtaining
#### using
#### storing


### RxnProbs
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.RxnProbs-1.0

#### data structure
#### obtaining
#### using
#### storing


### ProbAnno
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.ProbAnno-1.0

#### data structure
#### obtaining
#### using
#### storing
