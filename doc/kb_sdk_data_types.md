# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK - Data Types

## Data Type Definitions

Some, but not all, of the KBase data type definitions are available at

https://github.com/kbase/data_api/tree/develop/experiments/workspace_typespecs

Others may be examined by selecting an object of that type in a Narrative and clicking on the type to open the Landing Page for that type (make sure you click on the object type on the right, not the grouping on the left).
Then click on the 'Spec-file' tab.


## Base KBase and Workspace IDs and references

#### kbase_id

A KBase ID is a string starting with the characters "kb|".  KBase IDs are typed. The types are designated using a short string. For instance," g" denotes a genome, "tree" denotes a Tree, and "aln" denotes a sequence alignment. KBase IDs may be hierarchical.  For example, if a KBase genome identifier is "kb|g.1234", a protein encoding gene within that genome may be represented as "kb|g.1234.peg.771".


#### ref_type

An enumeration of reference types for a node.  Either the one letter abbreviation or full name can be given.

Supported types include:
- g | genome  => genome typed object or CDS data
- p | protein => protein sequence object or CDS data, often given as the MD5 of the sequence
- n | dna     => dna sequence object or CDS data, often given as the MD5 of the sequence
- f | feature => feature object or CDS data

**NOTE: THERE ARE MORE THAT SHOULD BE LISTED HERE**


#### ws_obj_id

The fully qualified name of a workspace object that includes the workspace name.  For example:

    dylan:1424477476805/Carsonella_10.genome_set


#### workspace_id / workspace?


#### workspace_name / workspace?



## KBase Data Objects

- [SingleEndLibrary](#single-end-library)
- [PairedEndLibrary](#paired-end-library)
- [ContigSet](#contig-set)
- [ReferenceAssembly](#reference-assembly)
- [FeatureSet](#feature-set)
- [GenomeSet](#genome-set)
- [Genome](#genome)
- [DomainAnnotation](#domain-annotation)
- [Pangenome](#pangenome)
- [ProteomeComparison](#proteome-comparison)
- [FBAModel](#fba-model)
- [FBA](#fba)
- [Media](#media)
- [RxnProbs](#rxn-probs)
- [ProbAnno](#prob-anno)
- [ExpressionSeries](#expression-series)
- [FloatDataTable](#float-data-table)
- [RNASeqSample](#rna-seq-sample)
- [RNASeqSampleAlignment](#rna-seq-sample-alignment)
- [PhenotypeSet](#phenotype-set)
- [PhenotypeSimulationSet](#phenotype-simulation-set)
- [Network](#network)


#### <A NAME="single-end-library"></A>SingleEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.SingleEndLibrary-2.1

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="paired-end-library"></A>PairedEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.PairedEndLibrary-2.1

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="contig-set"></A>ContigSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.ContigSet-3.0

A ContigSet object contains contiguous regions of DNA sequences (e.g. a set of genome fragments)

##### data structure

```
    { id: 'ContigSet_id',
      name: '',
      md5: '',
      source_id: '',
      source: '',
      type: '',
      reads_ref: '',
      fasta_ref: '',
      contigs: [ { id: '',
                   length: <seq_bp_len>,
                    md5: '',
                    sequence: '',
                    genetic_code: <genetic_code (def: 11)>,
                    cell_compartment: '',
                    replicon_type: '',
                    replicon_geometry: '',
                    name: '',
                    description: '',
                    complete: <T/F> },
                  ...
               ]
    }
```

##### setup
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService
    from Bio import SeqIO

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for retrieving the data object.

```
    def method_name(self, ctx, input):
        token = ctx['token']
        wsClient = workspaceService(self.workspaceURL, token=token)
        contigSet = wsClient.get_objects([{'ref': input['input_ws']+'/'+input['contigset_id']}])[0]['data']
```

##### using
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for manipulating the data object.

```
        for contig in contigSet['contigs']:
            contig_count += 1
            if len(contig['sequence']) >= int(input['min_length']):
                passing_contigs.append(contig)
```

##### storing
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for storing the data object.

```
        # parse the output and save back to KBase
        output_contigs = os.path.join(output_dir, 'final.contigs.fa')

        # Warning: this reads everything into memory!  Will not work if 
        # the contigset is very large!
        contigset_data = {
            'id':'megahit.contigset',
            'source':'User assembled contigs from reads in KBase',
            'source_id':'none',
            'md5': 'md5 of what? concat seq? concat md5s?',
            'contigs':[]
        }

        lengths = []
        for seq_record in SeqIO.parse(output_contigs, 'fasta'):
            contig = {
                'id':seq_record.id,
                'name':seq_record.name,
                'description':seq_record.description,
                'length':len(seq_record.seq),
                'sequence':str(seq_record.seq),
                'md5':hashlib.md5(str(seq_record.seq)).hexdigest()
            }
            lengths.append(contig['length'])
            contigset_data['contigs'].append(contig)


        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference
        provenance[0]['input_ws_objects']=[params['workspace_name']+'/'+params['read_library_name']]

        # save the contigset output
        new_obj_info = ws.save_objects({
                'id':info[6], # set the output workspace ID
                'objects':[
                    {
                        'type':'KBaseGenomes.ContigSet',
                        'data':contigset_data,
                        'name':params['output_contigset_name'],
                        'meta':{},
                        'provenance':provenance
                    }
                ]
            })
```


#### <A NAME="reference-assembly"></A>ReferenceAssembly
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.ReferenceAssembly-2.1

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="feature-set"></A>FeatureSet

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="genome-set"></A>GenomeSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseSearch.GenomeSet-2.1

The GenomeSet is a data object for grouping Genomes.

##### data structure

Note: either *ref* or *data* is defined for an element, but not both.
- optional metadata
- optional ref
- optional data

```
    { description: ‘genome_set_name’,
      elements : { ‘genome_name1_1’: { metadata: {‘f1’: ‘v1’,
                                                  ‘f2’: ‘v2’,
                                                   ...
                                                 }
                                       ref: ‘genome_ws_ref’,
                                       data: KBaseGenomes.Genome instance
                                     },
                    ‘genome_name_2’: ...
                  }
    }
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for retrieving the data object.

##### using
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for manipulating the data object.

##### storing
The following is a python snippet (e.g. for use in the SDK <module_name>Impl.py file) for storing the data object.


#### <A NAME="genome"></A>Genome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Genome-8.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="domain-annotation"></A>DomainAnnotation
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainAnnotation-2.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="pangenome"></A>Pangenome

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="proteome-comparison"></A>ProteomeComparison
https://narrative.kbase.us/functional-site/#/spec/type/GenomeComparison.ProteomeComparison-2.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="fba-model"></A>FBAModel
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBAModel-7.1

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="fba"></A>FBA
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBA-12.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="media"></A>Media
https://narrative.kbase.us/functional-site/#/spec/type/KBaseBiochem.Media-1.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="rxn-probs"></A>RxnProbs
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.RxnProbs-1.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="prob-anno"></A>ProbAnno
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.ProbAnno-1.0

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="expression-series"></A>ExpressionSeries

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="float-data-table"></A>FloatDataTable

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="rna-seq-sample"></A>RNASeqSample

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="rna-seq-sample-alignment"></A>RNASeqSampleAlignment

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="phenotype-set"></A>PhenotypeSet

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="phenotype-simulation-set"></A>PhenotypeSimulationSet

##### data structure
##### obtaining
##### using
##### storing


#### <A NAME="network"></A>Network

##### data structure
##### obtaining
##### using
##### storing
