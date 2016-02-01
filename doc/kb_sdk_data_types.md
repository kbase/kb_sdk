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

**NEED CLARIFICATION BETWEEN THESE**

#### workspace_id / workspace?

**NEED CLARIFICATION BETWEEN THESE**

#### workspace_name / workspace?

**NEED CLARIFICATION BETWEEN THESE**


## <A NAME="data-type-list"></A>KBase Data Types

- [SingleEndLibrary ](#single-end-library)
- [PairedEndLibrary](#paired-end-library)
- [ContigSet](#contig-set)
- [FeatureSet](#feature-set)
- [GenomeSet](#genome-set)
- [Genome](#genome)
- [DomainAnnotation](#domain-annotation)
- [MSA](#msa)
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


### <A NAME="single-end-library"></A>SingleEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFile.SingleEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.SingleEndLibrary

SingleEndLibrary objects contain FASTA or FASTQ formatted read (or longer sequence) data.  It may be compressed with GZIP compression.  There are two types of SingleEndLibrary type definitions that bear large similarity with one another.  The KBaseFile definition of SingleEndReads contains fields that are consistent with the transfer of larger files (e.g. from JGI) and captures more information, whereas the KBaseAssembly definition is used by Assembly methods.  The KBaseAssembly version is essentially just the 'file' portion of the 'lib' structure in the KBaseFile SingleEndLibrary definition, but uses the field name 'handle' instead of 'file' (see below).  Which you choose to use is up to you (and may depend on which existing data objects you wish to interact with) but our intention is for the KBaseFile definition to become the solitary definition and is therefore more likely to persist.

SingleEndLibrary objects are often quite large and the bulky sequence data is therefore typically stored in the SHOCK filesystem.

##### data structure

*KBaseFile SingleEndLibrary* definition

optional:
- file_name
- remote_md5
- remote_sha1
- description
- genetic_code
- source
- ncbi_taxid
- organelle
- location
- gc_content
- source
- strain
- read_count
- read_size
- single_genome

```
    { lib: { file: { hid: 'ws_handle_id',
    		     file_name: 'user_defined_file_name',
    		     id: 'shock_node_id',
    		     url: 'url_of_shock_server',
    		     type: 'shock',
    		     remote_md5: 'md5_hash_of_contents',
    		     remote_sha1: 'sha1_hash_of_contents'
    		   },
    	      encoding: 'file_encoding',				# e.g. UTF8
    	      type: 'file_type',					# e.g. FASTA, FASTQ
    	      size: <file_byte_size>,
    	      description: 'user_defined_description'
    	    },
       strain: { genetic_code: <genetic_code>,				# typically 11
       		 genus: 'genus_name',
       		 species: 'species_name',
       		 strain: 'strain_name',
       		 organelle: 'organelle_name',
       		 source: { source: 'name_of_strain_source',		# e.g. NCBI, JGI, Swiss-Prot
			   source_id: 'id_of_strain_data_at_source',
			   project_id: 'id_of_strain_project_at_source'
       		         },
       		 ncbi_taxid: <tax_id>,
       		 location: { lat: <float_lat>,
       		 	     lon: <float_lon>,
       		 	     elevation: <float_elevation>
       		 	     date: 'data_stamp',
       		 	     description: 'location_description'
       		 	   },
       		},
       source: { source: 'name_of_sequence_source',			# e.g. NCBI, JGI, Swiss-Prot
		 source_id: 'id_of_sequence_data_at_source',
		 project_id: 'id_of_sequence_project_at_source'
       	       },
       single_genome: <0/1>,						# flag to indicate single organism, 0=FALSE, 1=TRUE
       sequencing_tech: 'tech_used_for_sequencing',
       read_count: <number_of_sequences>,
       read_size: <total_sum_of_readlengths_in_bases>,
       gc_content: <float_average_gc>
    }
```

*KBaseAssembly SingleEndLibrary* definition

optional:
- hid
- file_name
- type
- url
- remote_md5
- remote_sha1

```
    { handle: { hid: 'ws_handle_id',
    		file_name: 'user_defined_file_name',
    		id: 'shock_node_id',
    		url: 'url_of_shock_server',
    		type: 'shock',
    		remote_md5: 'md5_hash_of_contents',
    		remote_sha1: 'sha1_hash_of_contents'
    	      }
    }
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.  This will work for both KBaseFile and KBaseAssembly SingleEndLibrary type definitions.

```python
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        # we should do something better here...
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.  This will work for both KBaseFile and KBaseAssembly SingleEndLibrary type definitions.

```python
	console = []

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

        #### Download the single end library
        if type_name == 'SingleEndLibrary':
            try:
                if 'lib' in data:
                    forward_reads = data['lib1']['file']
                elif 'handle' in data:
                    forward_reads = data['handle_1']

                forward_reads_file_location = os.path.join(self.scratch,forward_reads['file_name'])
                forward_reads_file = open(forward_reads_file_location, 'w', 0)
                self.log(console, 'downloading reads file: '+str(forward_reads_file_location))
                headers = {'Authorization': 'OAuth '+ctx['token']}
                r = requests.get(forward_reads['url']+'/node/'+forward_reads['id']+'?download', stream=True, headers=headers)
                for chunk in r.iter_content(1024):
                    forward_reads_file.write(chunk)
                forward_reads_file.close();
                self.log(console, 'done')
            except Exception as e:
                print(traceback.format_exc())
                raise ValueError('Unable to download paired-end read library files: ' + str(e))
        else:
            raise ValueError('Cannot yet handle library type of: '+type_name)
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.  This will work for both KBaseFile and KBaseAssembly SingleEndLibrary type definitions.

```python
        # construct the command
        megahit_cmd = [self.MEGAHIT]

        # we only support PE reads, so add that
        megahit_cmd.append('-1')
        megahit_cmd.append(forward_reads['file_name'])

        for arg in params['args'].keys():
            megahit_cmd.append('--'+arg)
            megahit_cmd.append(params['args'][arg])

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
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.  It will only store a single read file at a time.

```python
    def getContext(self):
        return self.__class__.ctx

    def upload_file_to_shock(self,
                             shock_service_url = None,
                             filePath = None,
                             ssl_verify = True,
                             token = None):
        """
        Use HTTP multi-part POST to save a file to a SHOCK instance.
        """

        if token is None:
            raise Exception("Authentication token required!")

        #build the header
        header = dict()
        header["Authorization"] = "Oauth {0}".format(token)

        if filePath is None:
            raise Exception("No file given for upload to SHOCK!")

        dataFile = open(os.path.abspath(filePath), 'rb')
        m = MultipartEncoder(fields={'upload': (os.path.split(filePath)[-1], dataFile)})
        header['Content-Type'] = m.content_type

        #logger.info("Sending {0} to {1}".format(filePath,shock_service_url))
        try:
            response = requests.post(shock_service_url + "/node", headers=header, data=m, allow_redirects=True, verify=ssl_verify)
            dataFile.close()
        except:
            dataFile.close()
            raise

        if not response.ok:
            response.raise_for_status()

        result = response.json()

        if result['error']:
            raise Exception(result['error'][0])
        else:
            return result["data"]

    def getSingleEndLibInfo(self):
        if hasattr(self.__class__, 'singleEndLibInfo'):
            return self.__class__.singleEndLibInfo
        # 1) upload files to shock
        token = self.ctx['token']
        forward_shock_file = self.upload_file_to_shock(
            shock_service_url = self.shockURL,
            filePath = 'data/small.forward.fq',
            token = token
            )
        #pprint(forward_shock_file)

        # 2) create handle
        hs = HandleService(url=self.handleURL, token=token)
        forward_handle = hs.persist_handle({
                                        'id' : forward_shock_file['id'], 
                                        'type' : 'shock',
                                        'url' : self.shockURL,
                                        'file_name': forward_shock_file['file']['name'],
                                        'remote_md5': forward_shock_file['file']['checksum']['md5']})

        # 3) save to WS
        single_end_library = {
            'lib': {
                'file': {
                    'hid':forward_handle,
                    'file_name': forward_shock_file['file']['name'],
                    'id': forward_shock_file['id'],
                    'url': self.shockURL,
                    'type':'shock',
                    'remote_md5':forward_shock_file['file']['checksum']['md5']
                },
                'encoding':'UTF8',
                'type':'fastq',
                'size':forward_shock_file['file']['size']
            },
            'sequencing_tech':'artificial reads'
        }

        new_obj_info = self.ws.save_objects({
                        'workspace':self.getWsName(),
                        'objects':[
                            {
                                'type':'KBaseFile.SingleEndLibrary',
                                'data':single_end_library,
                                'name':'test.pe.reads',
                                'meta':{},
                                'provenance':[
                                    {
                                        'service':'MegaHit',
                                        'method':'test_megahit'
                                    }
                                ]
                            }]
                        })
        self.__class__.singleEndLibInfo = new_obj_info[0]
        return new_obj_info[0]
```
[\[back to data type list\]](#data-type-list)
 

### <A NAME="paired-end-library"></A>PairedEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFile.PairedEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.PairedEndLibrary

PairedEndLibrary objects contain FASTA or FASTQ formatted read (or longer sequence) data.  Typically there are two files, one for 'forward' reads and one for 'reverse' reads.  They may be compressed with GZIP compression.  There are two types of PairedEndLibrary type definitions that bear large similarity with one another.  The KBaseFile definition of PariedEndReads contains fields that are consistent with the transfer of larger files (e.g. from JGI) and captures more information, whereas the KBaseAssembly definition is used by Assembly methods.  The KBaseAssembly version is essentially just the 'file' portion of the 'lib' substructure in the KBaseFile PairedEndLibrary definition, but uses the field name 'handle' instead of 'file' (see below).  Which you choose to use is up to you (and may depend on which existing data objects you wish to interact with) but our intention is for the KBaseFile definition to become the solitary definition and is therefore more likely to persist.

PairedEndLibrary objects are often quite large and the bulky sequence data is therefore typically stored in the SHOCK filesystem.

##### data structure

*KBaseFile PairedEndLibrary* definition

optional:
- lib2
- file_name
- remote_md5
- remote_sha1
- description
- genetic_code
- source
- ncbi_taxid
- organelle
- location
- gc_content
- source
- strain
- read_count
- read_size
- insert_size_mean
- insert_size_std_dev
- single_genome

```
    { lib1: { file: { hid: 'ws_handle_id',                              # e.g. for 'forward' reads
    		      file_name: 'user_defined_file_name',
    		      id: 'shock_node_id',
    		      url: 'url_of_shock_server',
    		      type: 'shock',
    		      remote_md5: 'md5_hash_of_contents',
    		      remote_sha1: 'sha1_hash_of_contents'
    		    },
    	       encoding: 'file_encoding',				# e.g. UTF8
    	       type: 'file_type',					# e.g. FASTA, FASTQ
    	       size: <file_byte_size>,
    	       description: 'user_defined_description'
    	     },
       lib2: { file: { hid: 'ws_handle_id',                              # e.g. for 'reverse' reads
    		      file_name: 'user_defined_file_name',
    		      id: 'shock_node_id',
    		      url: 'url_of_shock_server',
    		      type: 'shock',
    		      remote_md5: 'md5_hash_of_contents',
    		      remote_sha1: 'sha1_hash_of_contents'
    		    },
    	       encoding: 'file_encoding',				# e.g. UTF8
    	       type: 'file_type',					# e.g. FASTA, FASTQ
    	       size: <file_byte_size>,
    	       description: 'user_defined_description'
    	     },
       strain: { genetic_code: <genetic_code>,				# typically 11
       		 genus: 'genus_name',
       		 species: 'species_name',
       		 strain: 'strain_name',
       		 organelle: 'organelle_name',
       		 source: { source: 'name_of_strain_source',		# e.g. NCBI, JGI, Swiss-Prot
			   source_id: 'id_of_strain_data_at_source',
			   project_id: 'id_of_strain_project_at_source'
       		         },
       		 ncbi_taxid: <tax_id>,
       		 location: { lat: <float_lat>,
       		 	     lon: <float_lon>,
       		 	     elevation: <float_elevation>
       		 	     date: 'data_stamp',
       		 	     description: 'location_description'
       		 	   },
       		},
       source: { source: 'name_of_sequence_source',			# e.g. NCBI, JGI, Swiss-Prot
		 source_id: 'id_of_sequence_data_at_source',
		 project_id: 'id_of_sequence_project_at_source'
       	       },
       insert_size_mean: <average_insert_size>,
       insert_size_std_dev: <std_dev_of_insert_size_distribution>,
       interleaved: <0/1>,						# flag to indicate mate pairs interleaved in lib1, 0=FALSE, 1=TRUE
       read_orientation_outward: <0/1>,					# flag to indicate read orientation, 0=FALSE, 1=TRUE
       single_genome: <0/1>,						# flag to indicate single organism, 0=FALSE, 1=TRUE
       sequencing_tech: 'tech_used_for_sequencing',
       read_count: <number_of_sequences>,
       read_size: <total_sum_of_readlengths_in_bases>,
       gc_content: <float_average_gc>
    }
```

*KBaseAssembly PariedEndLibrary* definition

optional:
- hid
- file_name
- type
- url
- remote_md5
- remote_sha1

```
    { handle_1: { hid: 'ws_handle_id',
    	  	  file_name: 'user_defined_file_name',
    		  id: 'shock_node_id',
    		  url: 'url_of_shock_server',
    		  type: 'shock',
    		  remote_md5: 'md5_hash_of_contents',
    		  remote_sha1: 'sha1_hash_of_contents'
    	        },
      handle_2: { hid: 'ws_handle_id',
    	  	  file_name: 'user_defined_file_name',
    		  id: 'shock_node_id',
    		  url: 'url_of_shock_server',
    		  type: 'shock',
    		  remote_md5: 'md5_hash_of_contents',
    		  remote_sha1: 'sha1_hash_of_contents'
    	        },
       insert_size_mean: <average_insert_size>,
       insert_size_std_dev: <std_dev_of_insert_size_distribution>,
       interleaved: <0/1>,						# flag to indicate mate pairs interleaved in lib1, 0=FALSE, 1=TRUE
       read_orientation_outward: <0/1>,					# flag to indicate read orientation, 0=FALSE, 1=TRUE
    }
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.  This will work for both KBaseFile and KBaseAssembly PairedEndLibrary type definitions.

```python
    from biokbase.workspace.client import Workspace as workspaceService

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        # we should do something better here...
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.  This will work for both KBaseFile and KBaseAssembly PairedEndLibrary type definitions.

```python
	console = []

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

                forward_reads_file_location = os.path.join(self.scratch,forward_reads['file_name'])
                forward_reads_file = open(forward_reads_file_location, 'w', 0)
                self.log(console, 'downloading reads file: '+str(forward_reads_file_location))
                headers = {'Authorization': 'OAuth '+ctx['token']}
                r = requests.get(forward_reads['url']+'/node/'+forward_reads['id']+'?download', stream=True, headers=headers)
                for chunk in r.iter_content(1024):
                    forward_reads_file.write(chunk)
                forward_reads_file.close();
                self.log(console, 'done')

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
                    reverse_reads_file_location = os.path.join(self.scratch,reverse_reads['file_name'])
                    reverse_reads_file = open(reverse_reads_file_location, 'w', 0)
                    self.log(console, 'downloading reverse reads file: '+str(reverse_reads_file_location))
                    r = requests.get(reverse_reads['url']+'/node/'+reverse_reads['id']+'?download', stream=True, headers=headers)
                    for chunk in r.iter_content(1024):
                        reverse_reads_file.write(chunk)
                    reverse_reads_file.close()
                    self.log(console, 'done')
            except Exception as e:
                print(traceback.format_exc())
                raise ValueError('Unable to download paired-end read library files: ' + str(e))
        else:
            raise ValueError('Cannot yet handle library type of: '+type_name)
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.  This will work for both KBaseFile and KBaseAssembly PairedEndLibrary type definitions.

```python
        # construct the command
        megahit_cmd = [self.MEGAHIT]

        # we only support PE reads, so add that
        megahit_cmd.append('-1')
        megahit_cmd.append(forward_reads['file_name'])
        megahit_cmd.append('-2')
        megahit_cmd.append(reverse_reads['file_name'])

        for arg in params['args'].keys():
            megahit_cmd.append('--'+arg)
            megahit_cmd.append(params['args'][arg])

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
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.  It will only store a single read file at a time.  **(HOW DO WE STORE A PAIREDENDLIBRARY OBJECT?)**

```python
    def getContext(self):
        return self.__class__.ctx

    def upload_file_to_shock(self,
                             shock_service_url = None,
                             filePath = None,
                             ssl_verify = True,
                             token = None):
        """
        Use HTTP multi-part POST to save a file to a SHOCK instance.
        """

        if token is None:
            raise Exception("Authentication token required!")

        #build the header
        header = dict()
        header["Authorization"] = "Oauth {0}".format(token)

        if filePath is None:
            raise Exception("No file given for upload to SHOCK!")

        dataFile = open(os.path.abspath(filePath), 'rb')
        m = MultipartEncoder(fields={'upload': (os.path.split(filePath)[-1], dataFile)})
        header['Content-Type'] = m.content_type

        #logger.info("Sending {0} to {1}".format(filePath,shock_service_url))
        try:
            response = requests.post(shock_service_url + "/node", headers=header, data=m, allow_redirects=True, verify=ssl_verify)
            dataFile.close()
        except:
            dataFile.close()
            raise

        if not response.ok:
            response.raise_for_status()

        result = response.json()

        if result['error']:
            raise Exception(result['error'][0])
        else:
            return result["data"]

    def getPairedEndLibInfo(self):
        if hasattr(self.__class__, 'pairedEndLibInfo'):
            return self.__class__.pairedEndLibInfo
        # 1) upload files to shock
        token = self.ctx['token']
        forward_shock_file = self.upload_file_to_shock(
            shock_service_url = self.shockURL,
            filePath = 'data/small.forward.fq',
            token = token
            )
        reverse_shock_file = self.upload_file_to_shock(
            shock_service_url = self.shockURL,
            filePath = 'data/small.reverse.fq',
            token = token
            )
        #pprint(forward_shock_file)
        #pprint(reverse_shock_file)

        # 2) create handle
        hs = HandleService(url=self.handleURL, token=token)
        forward_handle = hs.persist_handle({
                                        'id' : forward_shock_file['id'], 
                                        'type' : 'shock',
                                        'url' : self.shockURL,
                                        'file_name': forward_shock_file['file']['name'],
                                        'remote_md5': forward_shock_file['file']['checksum']['md5']})

        reverse_handle = hs.persist_handle({
                                        'id' : reverse_shock_file['id'], 
                                        'type' : 'shock',
                                        'url' : self.shockURL,
                                        'file_name': reverse_shock_file['file']['name'],
                                        'remote_md5': reverse_shock_file['file']['checksum']['md5']})

        # 3) save to WS
        paired_end_library = {
            'lib1': {
                'file': {
                    'hid':forward_handle,
                    'file_name': forward_shock_file['file']['name'],
                    'id': forward_shock_file['id'],
                    'url': self.shockURL,
                    'type':'shock',
                    'remote_md5':forward_shock_file['file']['checksum']['md5']
                },
                'encoding':'UTF8',
                'type':'fastq',
                'size':forward_shock_file['file']['size']
            },
            'lib2': {
                'file': {
                    'hid':reverse_handle,
                    'file_name': reverse_shock_file['file']['name'],
                    'id': reverse_shock_file['id'],
                    'url': self.shockURL,
                    'type':'shock',
                    'remote_md5':reverse_shock_file['file']['checksum']['md5']
                },
                'encoding':'UTF8',
                'type':'fastq',
                'size':reverse_shock_file['file']['size']

            },
            'interleaved':0,
            'sequencing_tech':'artificial reads'
        }

        new_obj_info = self.ws.save_objects({
                        'workspace':self.getWsName(),
                        'objects':[
                            {
                                'type':'KBaseFile.PairedEndLibrary',
                                'data':paired_end_library,
                                'name':'test.pe.reads',
                                'meta':{},
                                'provenance':[
                                    {
                                        'service':'MegaHit',
                                        'method':'test_megahit'
                                    }
                                ]
                            }]
                        })
        self.__class__.pairedEndLibInfo = new_obj_info[0]
        return new_obj_info[0]
```
[\[back to data type list\]](#data-type-list)


### <A NAME="contig-set"></A>ContigSet
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

```python
    from biokbase.workspace.client import Workspace as workspaceService
    from Bio import SeqIO

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
    def method_name(self, ctx, input):
        token = ctx['token']
        wsClient = workspaceService(self.workspaceURL, token=token)
        contigSet = wsClient.get_objects([{'ref': input['input_ws']+'/'+input['contigset_id']}])[0]['data']
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        for contig in contigSet['contigs']:
            contig_count += 1
            if len(contig['sequence']) >= int(input['min_length']):
                passing_contigs.append(contig)
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
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
[\[back to data type list\]](#data-type-list)


### <A NAME="feature-set"></A>FeatureSet
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

```python
    from biokbase.workspace.client import Workspace as workspaceService
    from Bio import SeqIO
    from Bio.Seq import Seq
    from Bio.SeqRecord import SeqRecord
    from Bio.Alphabet import generic_protein

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
    def buildGenome2Features(self, ws, workspace_name, featureset_id):
        genome2Features = {}
        featureSet = ws.get_objects([{'ref':workspace_name+'/'+featureset_id}])[0]['data']
        features = featureSet['elements']
        for fId in features:
            genomeRef = features[fId][0]
            if genomeRef not in genome2Features:
                genome2Features[genomeRef] = []
            genome2Features[genomeRef].append(fId)
        return genome2Features
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
    def exportFasta(self, ws, workspace_name, featureset_id):
        # Process each genome one by one
        records = []
        for genomeRef in genome2Features:
            genome = ws.get_objects([{'ref':genomeRef}])[0]['data']
            featureIds = genome2Features[genomeRef]
            for feature in genome['features']:
                for fId in featureIds:
                    if fId == feature['id']:
                        record = SeqRecord(Seq(feature['protein_translation']), id=fId, description=genomeRef)
                        records.append(record)
        SeqIO.write(records, self.fileFastaName, "fasta")
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
    def createMSA(self, ws, workspace_name, featureset_id, msa_id):
        alignment_length = 0
        msa = {
            'name' : 'multiple alignmemnt for FeatureSet: ' + featureset_id,
            'sequence_type': 'protein',
            'alignment_length': 0,
            'alignment': {},
            'row_order': []
        }
        for record in SeqIO.parse( self.fileOutputName, "fasta"):
            msa['row_order'].append(record.id)
            sequence = str(record.seq)
            msa['alignment'][record.id] = sequence
            alignment_length = len(sequence)
        msa['alignment_length'] = alignment_length
        
        ws.save_objects({'workspace':workspace_name, 'objects':[{'name':msa_id, 'type':'KBaseTrees.MSA', 'data': msa}]})
        return str(msa)
```
[\[back to data type list\]](#data-type-list)


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
[\[back to data type list\]](#data-type-list)


### <A NAME="genome"></A>Genome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Genome

##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
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
[\[back to data type list\]](#data-type-list)


#### <A NAME="domain-annotation"></A>DomainAnnotation
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainAnnotation
##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
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
[\[back to data type list\]](#data-type-list)



### <A NAME="msa"></A>MSA
https://narrative.kbase.us/functional-site/#/spec/type/KBaseTrees.MSA

Multiple Sequence Alignments (MSA) are used for examining sequence variation across homologous sequences and as inputs to phylogenetic reconstructions, such as Trees.

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

```python
    from biokbase.workspace.client import Workspace as workspaceService
    from Bio import SeqIO
    from Bio.Seq import Seq
    from Bio.SeqRecord import SeqRecord
    from Bio.Alphabet import generic_protein

    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
    def buildGenome2Features(self, ws, workspace_name, featureset_id):
        genome2Features = {}
        featureSet = ws.get_objects([{'ref':workspace_name+'/'+featureset_id}])[0]['data']
        features = featureSet['elements']
        for fId in features:
            genomeRef = features[fId][0]
            if genomeRef not in genome2Features:
                genome2Features[genomeRef] = []
            genome2Features[genomeRef].append(fId)
        return genome2Features
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
    def exportFasta(self, ws, workspace_name, featureset_id):
        # Process each genome one by one
        records = []
        for genomeRef in genome2Features:
            genome = ws.get_objects([{'ref':genomeRef}])[0]['data']
            featureIds = genome2Features[genomeRef]
            for feature in genome['features']:
                for fId in featureIds:
                    if fId == feature['id']:
                        record = SeqRecord(Seq(feature['protein_translation']), id=fId, description=genomeRef)
                        records.append(record)
        SeqIO.write(records, self.fileFastaName, "fasta")
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
    def createMSA(self, ws, workspace_name, featureset_id, msa_id):
        alignment_length = 0
        msa = {
            'name' : 'multiple alignmemnt for FeatureSet: ' + featureset_id,
            'sequence_type': 'protein',
            'alignment_length': 0,
            'alignment': {},
            'row_order': []
        }
        for record in SeqIO.parse( self.fileOutputName, "fasta"):
            msa['row_order'].append(record.id)
            sequence = str(record.seq)
            msa['alignment'][record.id] = sequence
            alignment_length = len(sequence)
        msa['alignment_length'] = alignment_length
        
        ws.save_objects({'workspace':workspace_name, 'objects':[{'name':msa_id, 'type':'KBaseTrees.MSA', 'data': msa}]})
        return str(msa)
```
[\[back to data type list\]](#data-type-list)



### <A NAME="tree"></A>Tree
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

```python
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
[\[back to data type list\]](#data-type-list)


### <A NAME="pangenome"></A>Pangenome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Pangenome



### <A NAME="proteome-comparison"></A>GenomeComparison
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.GenomeComparison



### <A NAME="proteome-comparison"></A>ProteomeComparison
https://narrative.kbase.us/functional-site/#/spec/type/GenomeComparison.ProteomeComparison



### <A NAME="reference-assembly"></A>ReferenceAssembly
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.ReferenceAssembly



### <A NAME="fba-model"></A>FBAModel
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBAModel



### <A NAME="fba"></A>FBA
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFBA.FBA



### <A NAME="media"></A>Media
https://narrative.kbase.us/functional-site/#/spec/type/KBaseBiochem.Media



### <A NAME="rxn-probs"></A>RxnProbs
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.RxnProbs



### <A NAME="prob-anno"></A>ProbAnno
https://narrative.kbase.us/functional-site/#/spec/type/ProbabilisticAnnotation.ProbAnno



### <A NAME="expression-matrix"></A>ExpressionMatrix
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFeatureValues.ExpressionMatrix



### <A NAME="feature-clusters"></A>FeatureClusters



### <A NAME="estimate-k-result"></A>EstimateKResult
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFeatureValues.EstimateKResult



### <A NAME="expression-series"></A>ExpressionSeries



### <A NAME="float-data-table"></A>FloatDataTable



### <A NAME="rna-seq-sample"></A>RNASeqSample



### <A NAME="rna-seq-sample-alignment"></A>RNASeqSampleAlignment



### <A NAME="phenotype-set"></A>PhenotypeSet
https://narrative.kbase.us/functional-site/#/spec/type/KBasePhenotypes.PhenotypeSet



### <A NAME="phenotype-simulation-set"></A>PhenotypeSimulationSet



### <A NAME="network"></A>Network

