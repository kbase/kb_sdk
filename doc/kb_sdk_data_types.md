# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK - Data Types

KBase is currently working on a Data API that will replace direct access of KBase Data Objects.  Access to Genome Types will be the first class of data supported.  The API is not yet available.  In the meantime, Community Developers should access the Data Objects directly, following the guidance below.


## KBase and Workspace IDs and references

#### kbase_id

A KBase ID is a string starting with the characters "kb|".  KBase IDs are typed. The types are designated using a short string. For instance," g" denotes a genome, "tree" denotes a Tree, and "aln" denotes a sequence alignment. KBase IDs may be hierarchical.  For example, if a KBase genome identifier is "kb|g.1234", a protein encoding gene within that genome may be represented as "kb|g.1234.peg.771".


#### Data object names, IDs, and references

Data objects are stored in an object database.  Individual Narratives have "workspaces" associated with them where all data is stored for a given Narrative analysis.  Methods should interact with the data objects within a single workspace.

The KBase workspace is documented here:<br>
https://ci.kbase.us/services/ws/docs/

Workspaces have names and numerical IDs that are used to access them.  Workspace names are user-defined, mutable, and not guaranteed to be unique, whereas workspace IDs are system assigned upon instantiation, immutable, and guaranteed to be unique.  Data objects also have names and numerical IDs that are used to access them, with the same rules as workspace names/IDs.

Data object reference syntax is described here:<br>
https://ci.kbase.us/services/ws/docs/fundamentals.html#addressing-workspaces-and-objects

A data object reference, used in retrieving and storing data objects, has the workspace name/ID, the data object name/ID, and possible a trailing version, delimited by slashes.
  
e.g. the following are equivalent objects

```
KBasePublicGenomesV5/kb|g.26833    # wsName/objName 
2907/15741                         # wsId/objId
KBasePublicGenomesV5/kb|g.26833/1  # wsName/objName/objVer
2907/15741/1                       # wsId/objId/objVer
```
Since IDs are system assigned, it is preferrable to use names in code when creating and accessing your objects.  Additionally, workspace names should be passed to SDK methods using the following code.

1) In the input parameter structure in the KIDL *\<module\>.spec* (see SDK doc):

```
    typedef structure {
    	string workspace_name;
    	...
    } <Module>Params;
```

2) Configure 'behavior' in *ui/\<method\>/spec.json* file (see SDK doc):

```
	"behavior": {
		"service-mapping": {
			"input_mapping": [
				{
					"narrative_system_variable": "workspace",
					"target_property": "workspace_name"
				}
			]
		}
	}
```



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
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFile.SingleEndLibrary<br>
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
    { ## KBaseFile.SingleEndLibrary
      lib: { file: { hid: 'ws_handle_id',
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
    { ## KBaseAssembly.SingleEndLibrary
      handle: { hid: 'ws_handle_id',
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
import os
import sys
import shutil
import hashlib
import subprocess
import requests
import re
import traceback
import uuid
from datetime import datetime
from pprint import pprint, pformat
import numpy as np
from Bio import SeqIO
from biokbase.workspace.client import Workspace as workspaceService
        
class <ModuleName>:

    workspaceURL = None
    shockURL = None
    handleURL = None
    
    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.shockURL = config['shock-url']
        self.handleURL = config['handle-service-url']

        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
        
    def run_<method_name> (self, ctx, params):
        console = []
        self.log(console,'Running run_<method_name> with params=')
        self.log(console, pformat(params))

        token = ctx['token']
        ws = workspaceService(self.workspaceURL, token=token)
        
    	...
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.  This will work for both KBaseFile and KBaseAssembly SingleEndLibrary type definitions.

```python
        #### Get the read library
        try:
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
                    stderr = subprocess.STDOUT,
                    shell = False)
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
        self.log(console, 'storing SingleEndLibrary object: '+params['workspace_name']+'/'+params['output_read_library_name'])

        # 1) upload files to shock
        token = ctx['token']
        forward_shock_file = self.upload_file_to_shock(
				shock_service_url = self.shockURL,
				filePath = 'data/small.forward.fq',
				token = token
				)
        #pprint(forward_shock_file)

        # 2) create handle
        hs = HandleService(url=self.handleURL, token=token)
        forward_handle = hs.persist_handle({
	    'id': forward_shock_file['id'], 
	    'type': 'shock',
	    'url': self.shockURL,
	    'file_name': forward_shock_file['file']['name'],
	    'remote_md5': forward_shock_file['file']['checksum']['md5']})

        # 3) save to WS
        single_end_library = {
            'lib': {
                'file': {
                    'hid': forward_handle,
                    'file_name': forward_shock_file['file']['name'],
                    'id': forward_shock_file['id'],
                    'url': self.shockURL,
                    'type':'shock',
                    'remote_md5': forward_shock_file['file']['checksum']['md5']
                },
                'encoding': 'UTF8',
                'type': 'fastq',
                'size': forward_shock_file['file']['size']
            },
            'sequencing_tech': 'artificial reads'
        }

        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
        provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['read_library_name'])
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseFile.SingleEndLibrary',
									'data': single_end_library,
									'name': params['output_read_library_name'],
									'meta': {},
									'provenance': provenance
								}]
			})
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
        
    def upload_file_to_shock(self,
                             shock_service_url = None,
                             filePath = None,
                             ssl_verify = True,
                             token = None):
        #
        # Use HTTP multi-part POST to save a file to a SHOCK instance.
        #
        if token is None:
            raise Exception("Authentication token required!")
            
        # build the header
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
```
[\[back to data type list\]](#data-type-list)
 

### <A NAME="paired-end-library"></A>PairedEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFile.PairedEndLibrary<br>
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
    { ## KBaseFile.PairedEndLibrary
      lib1: { file: { hid: 'ws_handle_id',                              # e.g. for 'forward' reads
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

*KBaseAssembly PairedEndLibrary* definition

optional:
- hid
- file_name
- type
- url
- remote_md5
- remote_sha1

```
    { ## KBaseAssembly.PairedEndLibrary
      handle_1: { hid: 'ws_handle_id',
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
import os
import sys
import shutil
import hashlib
import subprocess
import requests
import re
import traceback
import uuid
from datetime import datetime
from pprint import pprint, pformat
import numpy as np
from Bio import SeqIO
from biokbase.workspace.client import Workspace as workspaceService
        
class <ModuleName>:

    workspaceURL = None
    shockURL = None
    handleURL = None
    
    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.shockURL = config['shock-url']
        self.handleURL = config['handle-service-url']

        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
        
    def run_<method_name> (self, ctx, params):
        console = []
        self.log(console,'Running run_<method_name> with params=')
        self.log(console, pformat(params))

        token = ctx['token']
        ws = workspaceService(self.workspaceURL, token=token)
        
    	...
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.  This will work for both KBaseFile and KBaseAssembly PairedEndLibrary type definitions.

```python
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
                    stderr = subprocess.STDOUT,
                    shell = False)

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
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.  Note that two read files must be present, one for forward reads and one for reverse reads.

```python
        self.log(console, 'storing PairedEndLibrary object: '+params['workspace_name']+'/'+params['output_read_library_name'])

        # 1) upload files to shock
        token = ctx['token']
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
	    'id': forward_shock_file['id'], 
	    'type': 'shock',
	    'url': self.shockURL,
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
                    'hid': forward_handle,
                    'file_name': forward_shock_file['file']['name'],
                    'id': forward_shock_file['id'],
                    'url': self.shockURL,
                    'type': 'shock',
                    'remote_md5': forward_shock_file['file']['checksum']['md5']
                },
                'encoding': 'UTF8',
                'type': 'fastq',
                'size': forward_shock_file['file']['size']
            },
            'lib2': {
                'file': {
                    'hid': reverse_handle,
                    'file_name': reverse_shock_file['file']['name'],
                    'id': reverse_shock_file['id'],
                    'url': self.shockURL,
                    'type': 'shock',
                    'remote_md5': reverse_shock_file['file']['checksum']['md5']
                },
                'encoding': 'UTF8',
                'type': 'fastq',
                'size': reverse_shock_file['file']['size']

            },
            'interleaved': 0,
            'sequencing_tech': 'artificial reads'
        }
        
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
        provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['read_library_name'])
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseFile.PairedEndLibrary',
									'data': paired_end_library,
									'name': params['output_read_library_name'],
									'meta': {},
									'provenance': provenance
								}]
			})
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
        
    def upload_file_to_shock(self,
                             shock_service_url = None,
                             filePath = None,
                             ssl_verify = True,
                             token = None):
        #
        # Use HTTP multi-part POST to save a file to a SHOCK instance.
        #
        if token is None:
            raise Exception("Authentication token required!")

        # build the header
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
    { ## KBaseGenomes.ContigSet
      id: 'ContigSet_kbase_id',
      name: 'ContigSet_name',                    # user defined
      md5: 'md5_chksum',                         # md5 checksum of the contigs sequences
      source_id: 'source_kbase_id',              # source kbase_id of the ContigSet
      source: 'source',                          # source name of the ContigSet
      type: 'ContigSet_type',                    # values are Genome,Transcripts,Environment,Collection
      reads_ref: 'reads_kbase_ref ',             # ref to shock node with the raw reads from which contigs were assembled
      fasta_ref: 'fasta_kbase_ref',              # ref to fasta file source
      contigs: [ { id: 'contig_id',              # kbase_id of the contig in the set
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
import os
import sys
import shutil
import hashlib
import subprocess
import requests
import re
import traceback
import uuid
from datetime import datetime
from pprint import pprint, pformat
import numpy as np
from Bio import SeqIO
from biokbase.workspace.client import Workspace as workspaceService
        
class <ModuleName>:

    workspaceURL = None
    shockURL = None
    handleURL = None
    
    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.shockURL = config['shock-url']
        self.handleURL = config['handle-service-url']

        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
        
    def run_<method_name> (self, ctx, params):
        console = []
        self.log(console,'Running run_<method_name> with params=')
        self.log(console, pformat(params))

        token = ctx['token']
        ws = workspaceService(self.workspaceURL, token=token)
        
    	...
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        self.log(console, 'getting contigset object: '+params['workspace_name']+'/'+params['contigset_name'])
        contigSetRef = params['workspace_name']+'/'+params['contigset_name']
        contigSet = ws.get_objects([{'ref': contigSetRef}])[0]['data']
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        # export contig sequences to FASTA file
        fasta_file_location = os.path.join(self.scratch, params['contigset_name']+".fasta")
        self.log(console, 'writing '+contigSetRef+' to fasta file: '+fasta_file_location)

        records = []
        for contig_id in contigSet['contigs'].keys():
            contig_sequence = contigSet['contigs'][contig_id]['sequence']
            record = SeqRecord(Seq(contig_sequence), id=contig_id, description=contig_id)
            records.append(record)
        SeqIO.write(records, fasta_file_location, "fasta")
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
        self.log(console, 'storing contigset object: '+params['workspace_name']+'/'+params['output_contigset_name'])

        # parse the output and save back to KBase
        output_contigs = os.path.join(output_dir, 'final.contigs.fa')

        # Warning: this reads everything into memory!  Will not work if the contigset is very large!
        contigset_data = {
            'id': 'MyMethod.ContigSet',
            'source': 'User assembled contigs from reads in KBase',
            'source_id': 'none',
            'md5': 'md5 of what? concat seq? concat md5s?',
            'contigs': []
        }

        for seq_record in SeqIO.parse(output_contigs, 'fasta'):
            contig = {
                'id': seq_record.id,
                'name': seq_record.name,
                'description': seq_record.description,
                'length': len(seq_record.seq),
                'sequence': str(seq_record.seq),
                'md5': hashlib.md5(str(seq_record.seq)).hexdigest()
            }
            contigset_data['contigs'].append(contig)

        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = [params['workspace_name']+'/'+params['read_library_name']]
        # OR e.g.
        # provenance[0]['input_ws_objects'] = [params['workspace_name']+'/'+params['contigset_name']]
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseGenomes.ContigSet',
									'data': contigset_data,
									'name': params['output_contigset_name'],
									'meta': {},
									'provenance': provenance
								}]
                        })
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
```
[\[back to data type list\]](#data-type-list)


### <A NAME="feature-set"></A>FeatureSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseCollections.FeatureSet

##### data structure
optional:
- description
- element_ordering

```
    { ## KBaseCollections.FeatureSet
      description: 'user_defined_name_or_desc_for_set',
      element_ordering: ['feature_1_kbase_id', 'feature_2_kbase_id', ...],
      elements: { 'feature_1_kbase_id': ['source_1A_genome_ref', 'source_1B_genome_ref', ...],
      		  'feature_2_kbase_id': ['source_2A_genome_ref', 'source_2B_genome_ref', ...],
      		  ...
      		}
    }
      
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
import os
import sys
import shutil
import hashlib
import subprocess
import requests
import re
import traceback
import uuid
from datetime import datetime
from pprint import pprint, pformat
import numpy as np
from Bio import SeqIO
from Bio.Seq import Seq
from Bio.SeqRecord import SeqRecord
from Bio.Alphabet import generic_protein
from biokbase.workspace.client import Workspace as workspaceService
        
class <ModuleName>:

    workspaceURL = None
    shockURL = None
    handleURL = None
    
    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.shockURL = config['shock-url']
        self.handleURL = config['handle-service-url']

        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
        
    def run_<method_name> (self, ctx, params):
        console = []
        self.log(console,'Running run_<method_name> with params=')
        self.log(console, pformat(params))

        token = ctx['token']
        ws = workspaceService(self.workspaceURL, token=token)
        
    	...
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        self.log(console, 'getting featureset object: '+params['workspace_name']+'/'+params['featureset_name'])
        featureSetRef = params['workspace_name']+'/'+params['featureset_name']
        featureSet = ws.get_objects([{'ref':featureSetRef}])[0]['data']

        genome2Features = {}
        features = featureSet['elements']
        for fId in features.keys():
            genomeRef = features[fId][0]
            if genomeRef not in genome2Features:
                genome2Features[genomeRef] = []
            genome2Features[genomeRef].append(fId)
        return genome2Features
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        # export features to FASTA file
        fasta_file_location = os.path.join(self.scratch, params['featureset_name']+".fasta")
        self.log(console, 'writing fasta file: '+fasta_file_location)
        records = []
        for genomeRef in genome2Features:
            genome = ws.get_objects([{'ref':genomeRef}])[0]['data']
            these_genomeFeatureIds = genome2Features[genomeRef]
            for feature in genome['features']:
                if feature['id'] in these_genomeFeatureIds:
                    record = SeqRecord(Seq(feature['protein_translation']), id=feature['id'], description=genomeRef+"."+feature['id'])
                    records.append(record)
        SeqIO.write(records, fasta_file_location, "fasta")
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
        self.log(console, 'storing featureset object: '+params['workspace_name']+'/'+params['output_featureset_name'])

        featureset_data = {
            'description': params['output_featureset_name'],
            'element_ordering': [],
            'elements': {}
        }
        genomes = []
        for feature in feature_list:
            feature_id = feature['feature_id']
            featureset_data['element_ordering'].append(feature_id);
            featureset_data['elements'][feature_id] = []
            for genome_ref in feature['genomes']:
                featureset_data['elements'][feature_id].append(genome_ref)
		if not genome_ref in genomes:
		    genomes.append(genome_ref)

        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
        for genome_ref in genomes:
            provenance[0]['input_ws_objects'].append(genome_ref)
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseCollections.FeatureSet',
									'data': featureset_data,
									'name': params['output_featureset_name'],
									'meta': {},
									'provenance': provenance
								}]
                        })
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
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
    { ## KBaseSearch.GenomeSet
      description: ‘genome_set_name’,
      elements : { ‘genome_name1_1’: { metadata: {‘f1’: ‘v1’,
                                                  ‘f2’: ‘v2’,
                                                   ...
                                                 }
                                       ref: ‘genome_ws_ref’,
                                       data: { KBaseGenomes.Genome instance_1 }   # (see below)
                                     },
                    ‘genome_name_2’: ...
                  }
    }
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
import os
import sys
import shutil
import hashlib
import subprocess
import requests
import re
import traceback
import uuid
from datetime import datetime
from pprint import pprint, pformat
import numpy as np
from Bio import SeqIO
from Bio.Seq import Seq
from Bio.SeqRecord import SeqRecord
from Bio.Alphabet import generic_protein
from biokbase.workspace.client import Workspace as workspaceService
        
class <ModuleName>:

    workspaceURL = None
    shockURL = None
    handleURL = None
    
    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.shockURL = config['shock-url']
        self.handleURL = config['handle-service-url']

        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
        
    def run_<method_name> (self, ctx, params):
        console = []
        self.log(console,'Running run_<method_name> with params=')
        self.log(console, pformat(params))

        token = ctx['token']
        ws = workspaceService(self.workspaceURL, token=token)
        
    	...
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        # get genomeSet
        genomeSetRef = params['workspace_name']+'/'+params['genomeset_name']
        self.log(console, 'getting genomeset object: '+genomeSetRef)
        genomeSet = ws.get_objects([{'ref':genomeSetRef}])[0]['data']
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        # Write sequences of genomeset contigs to fasta files, one per genome
        #   (an example of writing translated features to file is in Genome Data Type below) 
        #
        for genome_name in genomeSet['elements'].keys():
            fasta_file_location = os.path.join(self.scratch, params['genomeset_name']+'-'+genome_name+".fasta")
            self.log(console, 'writing fasta file: '+fasta_file_location)
            genomeRef = genomeSet['elements'][genome_name]['ref']
            genome = ws.get_objects([{'ref':genomeRef}])[0]['data']

            records = []
            contigSetRef = genome['contigset_ref']
            contigSet = ws.get_objects([{'ref':contigSetRef}])[0]['data']
            for contig_id in contigSet['contigs'].keys():
                contig_sequence = contigSet['contigs'][contig_id]['sequence']
            	record = SeqRecord(Seq(contig_sequence), id=genome_name+contig_id, description=genome_name+" "+contig_id)
            	records.append(record)
            SeqIO.write(records, fasta_file_location, "fasta")
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
        self.log(console, 'storing featureset object: '+params['workspace_name']+'/'+params['output_genomeset_name'])

        genome_names = []
        for genome in genomes_list:
            genome_names.append(genome['name'])
        genomeSet = {
            'description': 'genome set: ' + ",".join(genome_names)
            'elements': {}
        }
        for genome in genomes_list:
            genomeSet[genome['name']] = { 'ref': genome['ref'] }

        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
        for genome in genomes_list:
            provenance[0]['input_ws_objects'].append(genome['ref'])
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseSearch.GenomeSet',
									'data': genomeset_data,
									'name': params['output_genomeset_name'],
									'meta': {},
									'provenance': provenance
								}]
                        })
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
```
[\[back to data type list\]](#data-type-list)



### <A NAME="genome"></A>Genome
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGenomes.Genome

##### data structure

optional:
- quality
- close_genomes
- analysis_events
- features
- source_id
- source
- contigs
- contig_ids
- publications
- md5
- taxonomy
- gc_content
- complete
- dna_size
- num_contigs
- contig_lengths
- contigset_ref

```
    { ## KBaseGenomes.Genome
        id: 'genome_kbase_id',                       # e.g. "kb|g.26833"
        scientific_name: 'Genus_species_STRAIN',     # e.g. "Escherichia coli str. K-12 substr. MG1655"
        domain: 'domain_of_life',                    # Bacteria OR Archaea OR Eukaryote
        genetic_code: <code>,                        # typically 11
        dna_size: <sum_of_contig_lens>,              # in bases
        num_contigs: <num_contigs>,                  # contig count
        contigs: [                                   # preferably not used.  Use separate ContigSet object instead
                   { id: 'contig_id',                # e.g. "kb|g.26833.c.0"
                     length: <seq_bp_len>,           # length in bases of the contig sequence
                     md5: 'md5_chksum',              # md5 checksum of the individual contig sequence
                     sequence: 'ACGTACGT...',        # the contig sequence itself
                     genetic_code: <genetic_code>    # def: 11
                     cell_compartment: 'compart',    # e.g. "nucleus"
                     replicon_type: 'rep_type',      # e.g. plasmid
                     replicon_geometry: 'geom',      # e.g. "linear", "circular"
                     name: 'contig_name',            # name
                     description: 'desc',            # contig description
                     complete: <0/1>                 # is contig a complete chromosome/plasmid?  0=FALSE, 1=TRUE
                   },
                   ...
                 ],
        contig_lengths: [ <contig_1_len>, <contig_2_len>, ...],
        contig_ids: [ <contig_1_kbase_id>, <contig_2_kbase_id>, ...],  # e.g. ["kb|g.26833.c.0"]
        source: 'source_of_genome',                   # e.g. NCBI, JGI, etc.
        source_id: 'id_of_genome_at_source',          # e.g. NCBI id
        md5: 'md5_chksum_of_concat_contigs',          
        taxonomy: 'taxonomic string',                 # e.g. "Bacteria; Proteobacteria; Gammaproteobacteria; Enterobacteriales; Enterobacteriaceae; Escherichia; Escherichia coli str. K-12 substr. MG1655"
        gc_content: <float_avg_gc>,                   # 0.0-100.0
        complete: <0/1>,                              # is genome complete?  0=FALSE, 1=TRUE
        publications: [tuple<int, string, string, string, string, string, string>],
        features: [ {
		      id: 'feature_kbase_id',         # e.g. "kb|g.26833.CDS.3983"
		      location: [tuple<'Contig_id', <int_beg_pos>, 'strand', <len>>], # e.g. [["kb|g.26833.c.0",3820423,"+",2109]]
		      type: 'feature_type',           # e.g. 'CDS'
		      function: 'func',               # usually assigned from SEED
		      md5: 'md5_chksum',              # of DNA sequence?
		      protein_translation: 'aa_seq',  # IN CAPS
		      dna_sequence: 'dna_seq',        # in lower case.  Following gene direction
		      protein_translation_length: <aa_len>,  # int
		      dna_sequence_length: <dna_len>,        # int
		      publications: [tuple<int, string, string, string, string, string, string>],
		      subsystems: ['SEED_subsystem_1', ...],
		      protein_families: [ { id: 'prot_family_id',
		      			    subject_db: 'prot_family_db',
		      			    release_version: 'prot_family_db_version',
		      			    subject_description: 'prot_family_desc',
		      			    query_begin: <feature_match_beg>,
		      			    query_end: <feature_match_end>,
		      			    subject_begin: <prot_family_match_beg>,
		      			    subject_end: <prot_family_match_end>,
		      			    score: <float_score>,
		      			    evalue: <float_evalue>
		      			  },
		      			  ...
		      	                ],
		      aliases: ['alias_1', 'alias_2', ...],      # e.g. ["P0AG24","spoT","EG10966","NP_418107.1","P0AG24","ABE-0011935","b3650","2.7.6.5","3.1.7.2","SPOT_ECOLI","GO:0005515"]
		      orthologs: [tuple<string, float>],
		      annotations: [tuple<string, string, float>],
		      subsystem_data: [tuple<string, string, string>],
		      regulon_data: [tuple<string, list<Feature_id>, list<Feature_id>],
		      atomic_regulons: [tuple<string, int>],
		      coexpressed_fids: [tuple<Feature_id, float>],
		      co_occuring_fids: [tuple<Feature_id, float>],
		      quality: { truncated_begin: <0/1>,     # 0=FALSE, 1=TRUE
		      		 truncated_end: <0/1>,       # 0=FALSE, 1=TRUE
		      		 existence_confidence: <float_conf>,
		      		 frameshifted: <0/1>,
		      		 selenoprotein: <0/1>,
		      		 pyrrolysylprotein: <0/1>,
		      		 overlap_rules: ['overlap_rule_1', ...],
		      		 existence_priority: <float_priority>,
		      		 hit_count: <float_hit_count>,
		      		 weighted_hit_count: <weighted_hits>
		      	       },
		      feature_creation_event: { id: 'analysis_event_id',
						tool_name: 'analysis_tool_name',
						execution_time: <float_time>,
						parameters: [ 'param1', 'param2', ... ],
						hostname: 'exec_host'
					      },
        	    },
        	    ...
        	  ],
        contigset_ref: <contigset_kbase_ws_ref>,
        quality: { frameshift_error_rate: <float_err_rate>,
        	   sequence_error_rate: <seq_err_rate>
        	 },
        close_genomes: [ { genome: 'genome_kbase_id',
        		   closeness_measure: <float_measure>    # usually identity
        		 },
        		 ...
        	       ],
        analysis_events: [ { id: 'analysis_event_id',
        		     tool_name: 'analysis_tool_name',
        		     execution_time: <float_time>,
        		     parameters: [ 'param1', 'param2', ... ],
        		     hostname: 'exec_host'
        		   },
        		   ...
        	         ],
    }
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
import os
import sys
import shutil
import hashlib
import subprocess
import requests
import re
import traceback
import uuid
from datetime import datetime
from pprint import pprint, pformat
import numpy as np
from Bio import SeqIO
from Bio.Seq import Seq
from Bio.SeqRecord import SeqRecord
from Bio.Alphabet import generic_protein
from biokbase.workspace.client import Workspace as workspaceService
        
class <ModuleName>:

    workspaceURL = None
    shockURL = None
    handleURL = None
    
    def __init__(self, config):
        self.workspaceURL = config['workspace-url']
        self.shockURL = config['shock-url']
        self.handleURL = config['handle-service-url']

        self.scratch = os.path.abspath(config['scratch'])
        if not os.path.exists(self.scratch):
            os.makedirs(self.scratch)
           
    # target is a list for collecting log messages
    def log(self, target, message):
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
        
    def run_<method_name> (self, ctx, params):
        console = []
        self.log(console,'Running run_<method_name> with params=')
        self.log(console, pformat(params))

        token = ctx['token']
        ws = workspaceService(self.workspaceURL, token=token)
        
    	...
```

##### obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        genomeRef = params['workspace_name']+'/'+params['genome_name']
        self.log(console, 'getting genome object: '+genomeRef)
        genome = ws.get_objects([{'ref':genomeRef}])[0]['data']
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        # Write translated feature sequences (CDS only) of genome fasta files
        #
	fasta_file_location = os.path.join(self.scratch, params['genome_name']+".fasta")
	self.log(console, 'writing fasta file: '+fasta_file_location)
            
    	records = []
    	for feature in genome['features']:
    	    if feature['type'] == 'CDS':
    	        record = SeqRecord(Seq(feature['protein_translation']), \
					id=feature['id'], \
					description=feature['type']+"."+feature['function'])
		records.append(record)
    	SeqIO.write(records, fasta_file_location, "fasta")
```

##### storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
        self.log(console, 'storing genome object: '+params['workspace_name']+'/'+params['output_genome_name'])

        genome_data = {
			"complete":1,
			"id":"kb|g.26833",
			"scientific_name":"Escherichia coli str. K-12 substr. MG1655",
			"domain":"Bacteria","
			"taxonomy":"Bacteria; Proteobacteria; Gammaproteobacteria; Enterobacteriales; Enterobacteriaceae; Escherichia; Escherichia coli str. K-12 substr. MG1655"
			"contig_ids":["kb|g.26833.c.0"],
			"num_contigs":1,
			"contig_lengths":[4639675],
			"contigset_ref":"2907/15736/1",
			"dna_size":4639675,
			"gc_content":50.7896997095702,
			"genetic_code":11,
			"md5":"86e17907fb68a0a0447aa495085bff13",
			"source":"KBase Central Store",
			"source_id":"511145.6",
			"features":[  { "aliases":["P0AG24","spoT","EG10966","NP_418107.1","P0AG24","ABE-0011935"],
					"dna_sequence":"ttg...taa",
					"dna_sequence_length":2109,
					"function":"GTP pyrophosphokinase (EC 2.7.6.5), (p)ppGpp synthetase II / Guanosine-3',5'-bis(diphosphate) 3'-pyrophosphohydrolase (EC 3.1.7.2)",
					"id":"kb|g.26833.CDS.3983",
					"location":[["kb|g.26833.c.0",3820423,"+",2109]],
					"md5":"f94417311ccd06512cc8ecfa521bbbe6",
					"protein_translation":"MYL...NRN",
					"protein_translation_length":702,
					"subsystem_data":[["Stringent Response, (p)ppGpp metabolism","1","GTP pyrophosphokinase (EC 2.7.6.5), (p)ppGpp synthetase II"],["Stringent Response, (p)ppGpp metabolism","1","Guanosine-3',5'-bis(diphosphate) 3'-pyrophosphohydrolase (EC 3.1.7.2)"]],
					"subsystems":["Stringent Response, (p)ppGpp metabolism"],
					"type":"CDS"
				      },
				      ...
				   ]
		 }
        
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
	provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['contigset_name'])
	# OR e.g.
	#provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['genome_name'])
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseGenome.Genome',
									'data': genome_data,
									'name': params['output_genome_name'],
									'meta': {},
									'provenance': provenance
								}]
                        })
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
```
[\[back to data type list\]](#data-type-list)



#### <A NAME="domain-annotation"></A>DomainAnnotation
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainAnnotation
##### data structure

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
    from biokbase.workspace.client import Workspace as workspaceService

    def getContext(self):
        return self.__class__.ctx
        
    def __init__(self, config):
        ctx = self.getContext()
        self.workspaceURL = config['workspace-url']
        self.ws = workspaceService(self.workspaceURL, token=ctx['token'])

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
- name
- description
- sequence_type
- trim_info
- alignment_attributes
- row_order
- default_row_labels
- ws_refs
- kb_refs
- parent_msa_ref

```
{ ## KBaseTrees.MSA
  name: ‘MSA_name’,
  description: ‘tree_desc’,
  sequence_type: ‘sequence_type’,            # 'protein' or 'dna'
  alignment_length: <aln_len>,               # number of columns in alignment, including gaps
  alignment: { 'row_id_1': 'aligned_seq_1',
               'row_id_2': 'aligned_seq_2',
               ...
             },
  trim_info: { 'row_id_1': tuple(<start_pos_in_parent>,<end_pos_in_parent>,<parent_len>,<parent_md5>),
  	       'row_id_2': ...
  	     },
  alignment_attributes: { 'attr_1': 'val_1',
                          'attr_2': 'val_2',
                          ...
                        },
  row_order: ['row_id_1', row_id_2', ...],
  default_row_labels: { 'row_id_1': 'label_1',
                        'row_id_2', 'label_2',
		        ...
                       },
  ws_refs: { ‘row_id_1’: { ‘ref_type’: [‘ws_obj_id_1’, ...]
			 },
	     ‘row_id_2’: ...
 	   },
  kb_refs: { ‘row_id_1’: { ‘ref_type’: [‘kbase_id_1’, ...]
                         },
	     ‘row_id_2’: ...
	   },
  parent_msa_ref: ws_msa_ref                  # reference to parental alignment object to which 
                                              # this object adds some new aligned sequences 
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

    def getContext(self):
        return self.__class__.ctx
        
    def __init__(self, config):
        ctx = self.getContext()
        self.workspaceURL = config['workspace-url']
        self.ws = workspaceService(self.workspaceURL, token=ctx['token'])

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
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
```

##### using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
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
ref_type:
An enumeration of reference types for a node.  Either the one letter abbreviation or full name can be given.<br>
Supported types include:<br>
- g | genome  => genome typed object or CDS data
- p | protein => protein sequence object or CDS data, often given as the MD5 of the sequence
- n | dna     => dna sequence object or CDS data, often given as the MD5 of the sequence
- f | feature => feature object or CDS data

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
{ ## KBaseTrees.Tree
  name: ‘tree_name’,
  description: ‘tree_desc’,
  type: ‘tree_type’,
  tree: ‘newick_string’,
  tree_attributes: { 'attr_1': 'val_1',
                     'attr_2': 'val_2',
                     ...
                   },
  default_node_labels: { 'node_id_1': 'label_1',
                         'node_id_2', 'label_2',
		         ...
                       },
  ws_refs: { ‘node_id_1’: { ‘ref_type’: [‘ws_obj_id_1’, ...]
			  },
	      ‘node_id_2’: ...
 	   },
  kb_refs: { ‘node_id_1’: { ‘ref_type’: [‘kbase_id_1’, ...]
                          },
	     ‘node_id_2’: ...
	   },
  leaf_list: [‘node_id_1’, ‘node_id_2’, ...]
}
```

##### setup
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for preparing to work with the data object.

```python
    from biokbase.workspace.client import Workspace as workspaceService

    def getContext(self):
        return self.__class__.ctx
        
    def __init__(self, config):
        ctx = self.getContext()
        self.workspaceURL = config['workspace-url']
        self.ws = workspaceService(self.workspaceURL, token=ctx['token'])

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

