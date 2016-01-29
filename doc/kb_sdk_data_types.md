# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK - Data Types

KBase is currently working on a Data API that will replace direct access of KBase Data Objects.  Access to Genome Types will be the first class of data supported.  The API is not yet available.  In the meantime, Community Developers should access the Data Objects directly, following the guidance below.


## Data Type Definitions

Some, but not all, of the KBase data type definitions are available at

https://github.com/kbase/data_api/tree/develop/experiments/workspace_typespecs

Others may be examined by selecting an object of that type in a Narrative and clicking on the type to open the Landing Page for that type (make sure you click on the object type on the right, not the grouping on the left).
Then click on the 'Spec-file' tab.

e.g.
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Genome


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
- [FeatureSet](#feature-set)
- [GenomeSet](#genome-set)
- [Genome](#genome)
- [DomainAnnotation](#domain-annotation)
- [Tree](#tree)
- [Pangenome](#pangenome) (MISSING)
- [ProteomeComparison](#proteome-comparison) (MISSING)
- [ReferenceAssembly](#reference-assembly) (MISSING)
- [FBAModel](#fba-model) (MISSING)
- [FBA](#fba) (MISSING)
- [Media](#media) (MISSING)
- [RxnProbs](#rxn-probs) (MISSING)
- [ProbAnno](#prob-anno) (MISSING)
- [ExpressionMatrix](#expression-matrix) (MISSING)
- [FeatureClusters](#feature-clusters) (MISSING)
- [EstimateKResult](#estimate-k-result) (MISSING)
- [ExpressionSeries](#expression-series) (MISSING)
- [FloatDataTable](#float-data-table) (MISSING)
- [RNASeqSample](#rna-seq-sample) (MISSING)
- [RNASeqSampleAlignment](#rna-seq-sample-alignment) (MISSING)
- [PhenotypeSet](#phenotype-set) (MISSING)
- [PhenotypeSimulationSet](#phenotype-simulation-set) (MISSING)
- [Network](#network) (MISSING)


#### <A NAME="single-end-library"></A>SingleEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.SingleEndLibrary

##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
        #### Get the read library
        try:
            ws = workspaceService(self.workspaceURL, token=ctx['token'])
            objects = ws.get_objects([{'ref': params['workspace_name']+'/'+params['read_library_name']}])
            data = objects[0]['data']
            info = objects[0]['info']
            # Object Info Contents
            # absolute ref = info[6] + '/' + info[0] + '/' + info[4]
            # 0 - obj_id objid
            # 1 - obj_name name
            # 2 - type_string type
            # 3 - timestamp save_date
            # 4 - int version
            # 5 - username saved_by
            # 6 - ws_id wsid
            # 7 - ws_name workspace
            # 8 - string chsum
            # 9 - int size 
            # 10 - usermeta meta
            type_name = info[2].split('.')[1].split('-')[0]
        except Exception as e:
            raise ValueError('Unable to fetch read library object from workspace: ' + str(e))
            #to get the full stack trace: traceback.format_exc()


        #### Download the paired end library
        if type_name == 'PairedEndLibrary':
            try:
                if 'lib1' in data:
                    forward_reads = data['lib1']['file']
                elif 'handle_1' in data:
                    forward_reads = data['handle_1']
                if 'lib2' in data:
                    reverse_reads = data['lib2']['file']
                elif 'handle_2' in data:
                    reverse_reads = data['handle_2']
                else:
                    reverse_reads={}

                ### NOTE: this section is what could be replaced by the transform services
                forward_reads_file_location = os.path.join(self.scratch,forward_reads['file_name'])
                forward_reads_file = open(forward_reads_file_location, 'w', 0)
                self.log(console, 'downloading reads file: '+str(forward_reads_file_location))
                headers = {'Authorization': 'OAuth '+ctx['token']}
                r = requests.get(forward_reads['url']+'/node/'+forward_reads['id']+'?download', stream=True, headers=headers)
                for chunk in r.iter_content(1024):
                    forward_reads_file.write(chunk)
                forward_reads_file.close();
                self.log(console, 'done')
                ### END NOTE

                if 'interleaved' in data and data['interleaved']:
                    self.log(console, 'extracting forward/reverse reads into separate files')
                    if re.search('gz', forward_reads['file_name'], re.I):
                        bcmdstring = 'gunzip -c ' + forward_reads_file_location
                    else:    
                        bcmdstring = 'cat ' + forward_reads_file_location 

                    cmdstring = bcmdstring + '| (paste - - - - - - - -  | tee >(cut -f 1-4 | tr "\t" "\n" > '+self.scratch+'/forward.fastq) | cut -f 5-8 | tr "\t" "\n" > '+self.scratch+'/reverse.fastq )'
                    cmdProcess = subprocess.Popen(cmdstring, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True, executable='/bin/bash')
                    stdout, stderr = cmdProcess.communicate()

                    self.log(console, "cmdstring: " + cmdstring + " stdout: " + stdout + " stderr: " + stderr)
                    
                    forward_reads['file_name']='forward.fastq'
                    reverse_reads['file_name']='reverse.fastq'
                else:
                    ### NOTE: this section is what could also be replaced by the transform services
                    reverse_reads_file_location = os.path.join(self.scratch,reverse_reads['file_name'])
                    reverse_reads_file = open(reverse_reads_file_location, 'w', 0)
                    self.log(console, 'downloading reverse reads file: '+str(reverse_reads_file_location))
                    r = requests.get(reverse_reads['url']+'/node/'+reverse_reads['id']+'?download', stream=True, headers=headers)
                    for chunk in r.iter_content(1024):
                        reverse_reads_file.write(chunk)
                    reverse_reads_file.close()
                    self.log(console, 'done')
                    ### END NOTE
            except Exception as e:
                print(traceback.format_exc())
                raise ValueError('Unable to download paired-end read library files: ' + str(e))
        else:
            raise ValueError('Cannot yet handle library type of: '+type_name)
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
```

#### <A NAME="paired-end-library"></A>PairedEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.PairedEndLibrary

##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
        #### Get the read library
        try:
            ws = workspaceService(self.workspaceURL, token=ctx['token'])
            objects = ws.get_objects([{'ref': params['workspace_name']+'/'+params['read_library_name']}])
            data = objects[0]['data']
            info = objects[0]['info']
            # Object Info Contents
            # absolute ref = info[6] + '/' + info[0] + '/' + info[4]
            # 0 - obj_id objid
            # 1 - obj_name name
            # 2 - type_string type
            # 3 - timestamp save_date
            # 4 - int version
            # 5 - username saved_by
            # 6 - ws_id wsid
            # 7 - ws_name workspace
            # 8 - string chsum
            # 9 - int size 
            # 10 - usermeta meta
            type_name = info[2].split('.')[1].split('-')[0]
        except Exception as e:
            raise ValueError('Unable to fetch read library object from workspace: ' + str(e))
            #to get the full stack trace: traceback.format_exc()


        #### Download the paired end library
        if type_name == 'PairedEndLibrary':
            try:
                if 'lib1' in data:
                    forward_reads = data['lib1']['file']
                elif 'handle_1' in data:
                    forward_reads = data['handle_1']
                if 'lib2' in data:
                    reverse_reads = data['lib2']['file']
                elif 'handle_2' in data:
                    reverse_reads = data['handle_2']
                else:
                    reverse_reads={}

                ### NOTE: this section is what could be replaced by the transform services
                forward_reads_file_location = os.path.join(self.scratch,forward_reads['file_name'])
                forward_reads_file = open(forward_reads_file_location, 'w', 0)
                self.log(console, 'downloading reads file: '+str(forward_reads_file_location))
                headers = {'Authorization': 'OAuth '+ctx['token']}
                r = requests.get(forward_reads['url']+'/node/'+forward_reads['id']+'?download', stream=True, headers=headers)
                for chunk in r.iter_content(1024):
                    forward_reads_file.write(chunk)
                forward_reads_file.close();
                self.log(console, 'done')
                ### END NOTE

                if 'interleaved' in data and data['interleaved']:
                    self.log(console, 'extracting forward/reverse reads into separate files')
                    if re.search('gz', forward_reads['file_name'], re.I):
                        bcmdstring = 'gunzip -c ' + forward_reads_file_location
                    else:    
                        bcmdstring = 'cat ' + forward_reads_file_location 

                    cmdstring = bcmdstring + '| (paste - - - - - - - -  | tee >(cut -f 1-4 | tr "\t" "\n" > '+self.scratch+'/forward.fastq) | cut -f 5-8 | tr "\t" "\n" > '+self.scratch+'/reverse.fastq )'
                    cmdProcess = subprocess.Popen(cmdstring, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True, executable='/bin/bash')
                    stdout, stderr = cmdProcess.communicate()

                    self.log(console, "cmdstring: " + cmdstring + " stdout: " + stdout + " stderr: " + stderr)
                    
                    forward_reads['file_name']='forward.fastq'
                    reverse_reads['file_name']='reverse.fastq'
                else:
                    ### NOTE: this section is what could also be replaced by the transform services
                    reverse_reads_file_location = os.path.join(self.scratch,reverse_reads['file_name'])
                    reverse_reads_file = open(reverse_reads_file_location, 'w', 0)
                    self.log(console, 'downloading reverse reads file: '+str(reverse_reads_file_location))
                    r = requests.get(reverse_reads['url']+'/node/'+reverse_reads['id']+'?download', stream=True, headers=headers)
                    for chunk in r.iter_content(1024):
                        reverse_reads_file.write(chunk)
                    reverse_reads_file.close()
                    self.log(console, 'done')
                    ### END NOTE
            except Exception as e:
                print(traceback.format_exc())
                raise ValueError('Unable to download paired-end read library files: ' + str(e))
        else:
            raise ValueError('Cannot yet handle library type of: '+type_name)
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
        # construct the command
        megahit_cmd = [self.MEGAHIT]

        # we only support PE reads, so add that
        megahit_cmd.append('-1')
        megahit_cmd.append(forward_reads['file_name'])
        megahit_cmd.append('-2')
        megahit_cmd.append(reverse_reads['file_name'])

        # if a preset is defined, use that:
        if 'megahit_parameter_preset' in params:
            if params['megahit_parameter_preset']:
                megahit_cmd.append('--presets')
                megahit_cmd.append(params['megahit_parameter_preset'])

        if 'min_count' in params:
            if params['min_count']:
                megahit_cmd.append('--min-count')
                megahit_cmd.append(str(params['min_count']))
        if 'k_min' in params:
            if params['k_min']:
                megahit_cmd.append('--k-min')
                megahit_cmd.append(str(params['k_min']))
        if 'k_max' in params:
            if params['k_max']:
                megahit_cmd.append('--k-max')
                megahit_cmd.append(str(params['k_max']))
        if 'k_step' in params:
            if params['k_step']:
                megahit_cmd.append('--k-step')
                megahit_cmd.append(str(params['k_step']))
        if 'k_list' in params:
            if params['k_list']:
                k_list = []
                for k_val in params['k_list']:
                    k_list.append(str(k_val))
                megahit_cmd.append('--k-list')
                megahit_cmd.append(','.join(k_list))
        if 'min_contig_len' in params:
            if params['min_contig_len']:
                megahit_cmd.append('--min-contig-len')
                megahit_cmd.append(str(params['min_contig_len']))



        # set the output location
        timestamp = int((datetime.utcnow() - datetime.utcfromtimestamp(0)).total_seconds()*1000)
        output_dir = os.path.join(self.scratch,'output.'+str(timestamp))
        megahit_cmd.append('-o')
        megahit_cmd.append(output_dir)

        # run megahit, capture output as it happens
        self.log(console, 'running megahit:')
        self.log(console, '    '+' '.join(megahit_cmd))
        p = subprocess.Popen(megahit_cmd,
                    cwd = self.scratch,
                    stdout = subprocess.PIPE, 
                    stderr = subprocess.STDOUT, shell = False)

        while True:
            line = p.stdout.readline()
            if not line: break
            self.log(console, line.replace('\n', ''))

        p.stdout.close()
        p.wait()
        self.log(console, 'return code: ' + str(p.returncode))
        if p.returncode != 0:
            raise ValueError('Error running megahit, return code: '+str(p.returncode) + 
                '\n\n'+ '\n'.join(console))
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
    #upload reads
    cmdstring = " ".join( ('ws-tools fastX2reads --inputfile', 'trimmed_' + readLibrary['data']['handle']['file_name'], 
                            '--wsurl', self.workspaceURL, '--shockurl', self.shockURL, '--outws', input_params['output_ws'],
                            '--outobj', input_params['output_read_library'], '--readcount', readcount ) )

    cmdProcess = subprocess.Popen(cmdstring, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True, env=env)
    stdout, stderr = cmdProcess.communicate()
```


#### <A NAME="contig-set"></A>ContigSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.ContigSet

A ContigSet object contains contiguous regions of DNA/RNA sequences (e.g. a set of genome fragments)

##### data structure
optional:
- name
- type 
- reads_ref
- fasta_ref
- length
- md5
- genetic_code
- cell_compartment
- replicon_geometry
- replicon_type
- description
- complete

```
    { id: 'ContigSet_kbase_id',
      name: 'ContigSet_name',                    # user defined
      md5: 'md5_chksum',                         # md5 checksum of the contigs sequences
      source_id: 'source_kbase_id',              # source kbase_id of the ContigSet
      source: 'source',                          # source name of the ContigSet
      type: 'ContigSet_type',                    # values are Genome,Transcripts,Environment,Collection
      reads_ref: 'reads_kbase_ref ',             # ref to shock node with the raw reads from which contigs were assembled
      fasta_ref: 'fasta_kbase_ref',              # ref to fasta file source
      contigs: [ { id: 'contig_id',              # id (not kbase_id) of the contig in the set
                   length: <seq_bp_len>,         # length in bases of the contig sequence
                   md5: 'md5_chksum',            # md5 checksum of the individual contig sequence
                   sequence: 'ACGTACGT...',      # the contig sequence itself
                   genetic_code: <genetic_code>  # def: 11
                   cell_compartment: 'compart',  # e.g. "nucleus"
                   replicon_type: 'rep_type',    # e.g. plasmid
                   replicon_geometry: 'geom',    # e.g. "linear", "circular"
                   name: 'contig_name',          # name
                   description: 'desc',          # contig description
                   complete: <0/1>               # is contig a complete chromosome/plasmid?  0=FALSE, 1=TRUE
                 },
                  ...
               ]
    }
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService
    from Bio import SeqIO

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
    def method_name(self, ctx, input):
        token = ctx['token']
        wsClient = workspaceService(self.workspaceURL, token=token)
        contigSet = wsClient.get_objects([{'ref': input['input_ws']+'/'+input['contigset_id']}])[0]['data']
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
        for contig in contigSet['contigs']:
            contig_count += 1
            if len(contig['sequence']) >= int(input['min_length']):
                passing_contigs.append(contig)
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

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


#### <A NAME="feature-set"></A>FeatureSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseCollections.FeatureSet

##### data structure
optional:
- description
- element_ordering

```
    { description: 'user_defined_name_or_desc_for_set',
      element_ordering: ['feature_1_kbase_id', 'feature_2_kbase_id', ...],
      elements: { 'feature_1_kbase_id': ['source_A_genome_ref', 'source_B_genome_ref', ...]
    }
      
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
```


#### <A NAME="genome-set"></A>GenomeSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseSearch.GenomeSet

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
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
```


#### <A NAME="genome"></A>Genome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Genome

##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
```


#### <A NAME="domain-annotation"></A>DomainAnnotation
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainAnnotation
##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
```



#### <A NAME="tree"></A>Tree
https://narrative.kbase.us/functional-site/#/spec/type/KBaseTrees.Tree

Phylogenetic trees may represent the evolutionary relationships of either species/genomes or genes.

##### data structure
optional:
- name
- description
- type
- tree_attributes
- default_node_labels
- ws_refs
- kb_refs
- leaf_list

```
{ name: ‘tree_name’,
  description: ‘tree_desc’,
  type: ‘tree_type’,
  tree: ‘newick_string’,
  tree_attributes: { 'attr_1': 'val_1',
                     'attr_2': 'val_2',
                     ...
                   },
  default_node_labels: { ‘node_id_1’: ‘label_1’,
                         'node_id_2', 'label_2',
		                ...
                       }
  ws_refs: { ‘node_id_1’: { ‘ref_type’: [‘ws_obj_id_1’, ...]
			              }
	         ‘node_id_2’: ...
 		    }
  kb_refs: { ‘node_id_1’: { ‘ref_type’: [‘kbase_id_1’, ...]
                          }
	         ‘node_id_2’: ...
	       }
  leaf_list: [‘node_id_1’, ‘node_id_2’, ...]
}
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```
```


#### <A NAME="pangenome"></A>Pangenome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Pangenome



#### <A NAME="proteome-comparison"></A>GenomeComparison
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.GenomeComparison



#### <A NAME="proteome-comparison"></A>ProteomeComparison
https://narrative.kbase.us/functional-site/#/spec/type/GenomeComparison.ProteomeComparison



#### <A NAME="reference-assembly"></A>ReferenceAssembly
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.ReferenceAssembly



#### <A NAME="fba-model"></A>FBAModel
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBAModel



#### <A NAME="fba"></A>FBA
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBA



#### <A NAME="media"></A>Media
https://narrative.kbase.us/functional-site/#/spec/type/KBaseBiochem.Media



#### <A NAME="rxn-probs"></A>RxnProbs
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.RxnProbs



#### <A NAME="prob-anno"></A>ProbAnno
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.ProbAnno



#### <A NAME="expression-matrix"></A>ExpressionMatrix
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFeatureValues.ExpressionMatrix



#### <A NAME="feature-clusters"></A>FeatureClusters



#### <A NAME="estimate-k-result"></A>EstimateKResult
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFeatureValues.EstimateKResult






#### <A NAME="expression-series"></A>ExpressionSeries



#### <A NAME="float-data-table"></A>FloatDataTable



#### <A NAME="rna-seq-sample"></A>RNASeqSample



#### <A NAME="rna-seq-sample-alignment"></A>RNASeqSampleAlignment



#### <A NAME="phenotype-set"></A>PhenotypeSet
https://narrative.kbase.us/functional-site/#/spec/type/KBasePhenotypes.PhenotypeSet



#### <A NAME="phenotype-simulation-set"></A>PhenotypeSimulationSet



#### <A NAME="network"></A>Network

