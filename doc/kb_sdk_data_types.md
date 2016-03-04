# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK - Data Types

KBase is currently working on a Data API that will replace direct access of KBase Data Objects.  Access to Genome Types will be the first class of data supported.  The API is not yet available.  In the meantime, Community Developers should access the Data Objects directly, following the guidance below.


## KBase and Workspace IDs and references

#### kbase_id

A KBase ID is a string starting with the characters "kb|".  KBase IDs are typed. The types are designated using a short string. For instance," g" denotes a genome, "tree" denotes a Tree, and "aln" denotes a sequence alignment. KBase IDs may be hierarchical.  For example, if a KBase genome identifier is "kb|g.1234", a protein encoding gene within that genome may be represented as "kb|g.1234.peg.771".


#### Data object names, IDs, and references

Data objects are stored in an object database.  Individual Narratives have "workspaces" associated with them where all data is stored for a given Narrative analysis.  Methods should interact with the data objects within a single workspace.

The KBase workspace is documented here:<br>
https://kbase.us/services/ws/docs/

Workspaces have names and numerical IDs that are used to access them.  Workspace names are user-defined, mutable, and not guaranteed to be unique, whereas workspace IDs are system assigned upon instantiation, immutable, and guaranteed to be unique.  Data objects also have names and numerical IDs that are used to access them, with the same rules as workspace names/IDs.

Data object reference syntax is described here:<br>
https://kbase.us/services/ws/docs/fundamentals.html#addressing-workspaces-and-objects

A data object reference, used in retrieving and storing data objects, has the workspace name/ID, the data object name/ID, and possible a trailing version, delimited by slashes.
  
e.g. the following are equivalent objects

```
KBasePublicGenomesV5/kb|g.26833    # wsName/objName (PREFERRED FORM)
2907/15741                         # wsId/objId
KBasePublicGenomesV5/kb|g.26833/1  # wsName/objName/objVer
2907/15741/1                       # wsId/objId/objVer
```
Since IDs are system assigned, it is preferrable to use names in code when creating and accessing your objects.  Additionally, workspace names should be passed to SDK methods using the following code.

1) In the input parameter structure in the KIDL *\<module\>.spec* (see [SDK doc](../doc/README.md)):

```
    typedef structure {
    	string workspace_name;
    	...
    } <Module>Params;
```

2) Configure 'behavior' in *ui/\<method\>/spec.json* file (see [SDK doc](../README.md)):

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
- [DomainLibrary](#domain-library)
- [DomainModelSet](#domain-model-set)
- [DomainAnnotation](#domain-annotation)
- [MSA](#msa)
- [Tree](#tree)
- [Network](#network)

<!--
- [Pangenome](#pangenome) (MISSING)
- [GenomeComparison](#genome-comparison) (MISSING)
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
-->

### <A NAME="single-end-library"></A>SingleEndLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseFile.SingleEndLibrary<br>
https://narrative.kbase.us/functional-site/#/spec/type/KBaseAssembly.SingleEndLibrary

- [data structure](#single-end-library-ds)
- [setup](#single-end-library-setup)
- [obtaining](#single-end-library-obtaining)
- [using](#single-end-library-using)
- [storing](#single-end-library-storing)

SingleEndLibrary objects contain FASTA or FASTQ formatted read (or longer sequence) data.  It may be compressed with GZIP compression.  There are two types of SingleEndLibrary type definitions that bear large similarity with one another.  The KBaseFile definition of SingleEndReads contains fields that are consistent with the transfer of larger files (e.g. from JGI) and captures more information, whereas the KBaseAssembly definition is used by Assembly methods.  The KBaseAssembly version is essentially just the 'file' portion of the 'lib' structure in the KBaseFile SingleEndLibrary definition, but uses the field name 'handle' instead of 'file' (see below).  Which you choose to use is up to you (and may depend on which existing data objects you wish to interact with) but our intention is for the KBaseFile definition to become the solitary definition and is therefore more likely to persist.

SingleEndLibrary objects are often quite large and the bulky sequence data is therefore typically stored in the SHOCK filesystem.

##### <A NAME="single-end-library-ds"></A>data structure

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

##### <A NAME="single-end-library-setup"></A>setup
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
from requests_toolbelt import MultipartEncoder
from Bio import SeqIO
from biokbase.workspace.client import Workspace as workspaceService
from biokbase.AbstractHandle.Client import AbstractHandle as HandleService
        
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

##### <A NAME="single-end-library-obtaining"></A>obtaining
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

##### <A NAME="single-end-library-using"></A>using
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

##### <A NAME="single-end-library-storing"></A>storing
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

- [data structure](#paired-end-library-ds)
- [setup](#paired-end-library-setup)
- [obtaining](#paired-end-library-obtaining)
- [using](#paired-end-library-using)
- [storing](#paired-end-library-storing)

PairedEndLibrary objects contain FASTA or FASTQ formatted read (or longer sequence) data.  Typically there are two files, one for 'forward' reads and one for 'reverse' reads.  They may be compressed with GZIP compression.  There are two types of PairedEndLibrary type definitions that bear large similarity with one another.  The KBaseFile definition of PariedEndReads contains fields that are consistent with the transfer of larger files (e.g. from JGI) and captures more information, whereas the KBaseAssembly definition is used by Assembly methods.  The KBaseAssembly version is essentially just the 'file' portion of the 'lib' substructure in the KBaseFile PairedEndLibrary definition, but uses the field name 'handle' instead of 'file' (see below).  Which you choose to use is up to you (and may depend on which existing data objects you wish to interact with) but our intention is for the KBaseFile definition to become the solitary definition and is therefore more likely to persist.

PairedEndLibrary objects are often quite large and the bulky sequence data is therefore typically stored in the SHOCK filesystem.

##### <A NAME="paired-end-library-ds"></A>data structure

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

##### <A NAME="paired-end-library-setup"></A>setup
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

##### <A NAME="paired-end-library-obtaining"></A>obtaining
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

##### <A NAME="paired-end-library-using"></A>using
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

##### <A NAME="paired-end-library-storing"></A>storing
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

- [data structure](#contig-set-ds)
- [setup](#contig-set-setup)
- [obtaining](#contig-set-obtaining)
- [using](#contig-set-using)
- [storing](#contig-set-storing)

A ContigSet object contains contiguous regions of DNA/RNA sequences (e.g. a set of genome fragments)

##### <A NAME="contig-set-ds"></A>data structure
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

##### <A NAME="contig-set-setup"></A>setup
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

##### <A NAME="contig-set-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        contigSetRef = params['workspace_name']+'/'+params['contigset_name']
        self.log(console, 'getting contigset object: '+contigSetRef)
        contigSet = ws.get_objects([{'ref': contigSetRef}])[0]['data']
```

##### <A NAME="contig-set-using"></A>using
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

##### <A NAME="contig-set-storing"></A>storing
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

- [data structure](#feature-set-ds)
- [setup](#feature-set-setup)
- [obtaining](#feature-set-obtaining)
- [using](#feature-set-using)
- [storing](#feature-set-storing)

##### <A NAME="feature-set-ds"></A>data structure
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

##### <A NAME="feature-set-setup"></A>setup
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

##### <A NAME="feature-set-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        featureSetRef = params['workspace_name']+'/'+params['featureset_name']
        self.log(console, 'getting featureset object: '+featureSetRef)
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

##### <A NAME="feature-set-using"></A>using
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

##### <A NAME="feature-set-storing"></A>storing
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

- [data structure](#genome-set-ds)
- [setup](#genome-set-setup)
- [obtaining](#genome-set-obtaining)
- [using](#genome-set-using)
- [storing](#genome-set-storing)

The GenomeSet is a data object for grouping Genomes.

##### <A NAME="genome-set-ds"></A>data structure

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

##### <A NAME="genome-set-setup"></A>setup
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

##### <A NAME="genome-set-using"></A>using
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

##### <A NAME="genome-set-storing"></A>storing
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

- [data structure](#genome-ds)
- [setup](#genome-setup)
- [obtaining](#genome-obtaining)
- [using](#genome-using)
- [storing](#genome-storing)
- 

The Genome object stores genomes.  There is an API that will replace direct access to the Genome structure, but for the time being, have at it!

##### <A NAME="genome-ds"></A>data structure

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

##### <A NAME="genome-setup"></A>setup
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

##### <A NAME="genome-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        genomeRef = params['workspace_name']+'/'+params['genome_name']
        self.log(console, 'getting genome object: '+genomeRef)
        genome = ws.get_objects([{'ref':genomeRef}])[0]['data']
```

##### <A NAME="genome-using"></A>using
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

##### <A NAME="genome-storing"></A>storing
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


#### <A NAME="domain-library"></A>DomainLibrary
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainLibrary

- [data structure](#domain-library-ds)
- [setup](#domain-library-setup)
- [obtaining](#domain-library-obtaining)
- [using](#domain-library-using)
- [storing](#domain-library-storing)

A DomainLibrary is an object that stores data about a public set of
domain models, all obtained from the same source (e.g., a particular
version of Pfam or COGs).  All public DomainLibraries in KBase are in
the workspace KBasePublicGeneDomains.


##### <A NAME="domain-library-ds"></A>data structure
```
    { ## KBaseGeneFamilies.DomainLibrary
      id: 'domain_library_id',			    # KBase ID
      source: 'domain_source', 			    # string indicating source of library (e.g., CDD, SMART, Pfam, etc)
      source_url: 'string', 			    # ftp/http url where library can be downloaded
      version: 'string',					# version of library release
      release_date: 'date', 			    # release date of library; date in ISO 8601 format; e.g., 2014-11-26
      program: 'program_version', 		    # program for running domain search; must be either hmmscan-3.1b1 or rpsblast-2.2.30
      domain_prefix: 'string', 			    # prefix of domain accession defining library
      dbxref_prefix: 'string', 			    # url prefix for db-external referencing
      library_files: [ {					# library files stored in Shock storage 
						file_name: 'string', # file name, e.g., 'Pfam-A.hmm'
						shock_id: 'string'	# ID of the file in Shock storage
						},
						...
                     ], 
      domains: {						    # information about each domain in the library
       	       'accession_1': {	    	    # mapping of accessions to info about each domain
								accession: 'string', # accession of domain model (e.g., PF00244.1, or COG0001)
								cdd_id: 'string',    # (optional) in case of CDD, the id reported by rps-blast program
			      				name: 'string',		 # name of domain model,
								description: 'string', # description of domain model
								length: <int>,		 # length of profile
								model_type: 'string', # domain model type; one of PSSM, HMM-Family, HMM-Domain, HMM-Repeat, HMM-Motif
								trusted_cutoff: <float> # (optional) trusted cutoff of domain model for HMM libraries
							  },
				...
               }
    }
```

##### <A NAME="domain-library-setup"></A>setup
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
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
```

##### <A NAME="domain-library-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        self.log(console, 'getting DomainLibrary object: '+params['workspace_name']+'/'+params['domain_library_name'])
		domain_library_ref = params['workspace_name']+'/'+params['domain_library_name']
		domain_library = ws.get_objects([{'ref': domain_library_ref}])[0]['data']
						
```

##### <A NAME="domain-library-using"></A>using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
		# Write a list of domains in the library to a tab-delimited file
        #
		tab_file_location = os.path.join(self.scratch, params['file_name']+".txt")
		self.log(console, 'writing domains file: '+tab_file_location)
		tab_file = open(tab_file_location, 'w', 0) 
		for accession in domain_library['domains']:
			description = domain_library['domains'][accession]['description']
			tab_file.write("%s\t%s" % (accession, description))
		tab_file.close()
```

##### <A NAME="domain-library-storing"></A>storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
		self.log(console, 'storing DomainLibrary object: '+params['workspace_name']+'/'+params['output_domain_library_name'])

		# 1) upload files needed at runtime to shock, storing list of files
		token = ctx['token']
		library_files = []
		library_shock_file = self.upload_file_to_shock(
                                shock_service_url = self.shockURL,
                                filePath = 'data/Pfam-A.hmm',
                                token = token
							    )
		library_file = { 'file_name': library_shock_file['file']['name'],
						 'shock_id': library_shock_file['id'] }
		library_files.append(library_file);
		# not shown:
		# uploading other files needed at runtime, e.g., Pfam-A.hmm.h3f, etc.

		# 2) make a library; one domain in this example
		domain_library = {
			'source': 'Pfam',
			'source_url': 'ftp://ftp.ebi.ac.uk/pub/databases/Pfam/releases/Pfam29.0/Pfam-A.hmm.gz',
			'version': '29.0',
			'release_date': '2015-12-22',
			'program': 'hmmscan-3.1b2',
			'domain_prefix': 'PF',
			'dbxref_prefix': 'http://pfam.xfam.org/family/',
			'library_files': library_files,
			'domains': { 
				'PF11245.3': {
					'accession':'PF11245.3',
					'name':'DUF2544',
					'description':'Protein of unknown function (DUF2544)',
					'length':230,
					'model_type':'HMM-Family'
				}
			}
		}
		
		# 3) save object and provenance to workspace
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
	    provenance[0]['service'] = 'MyModule'
	    provenance[0]['method'] = 'MyMethod'
		
		# save object in workspace
        new_obj_info = ws.save_objects({
			'workspace': params['workspace_name'],
			'objects': [ {
				'type': 'KBaseGeneFamilies.DomainLibrary',
				'data': domain_library,
				'name': params['output_domain_library_name'],
				'meta': {},
				'provenance': provenance
				} ] })
```
[\[back to data type list\]](#data-type-list)


#### <A NAME="domain-model-set"></A>DomainModelSet
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainModelSet

- [data structure](#domain-model-set-ds)
- [setup](#domain-model-set-setup)
- [obtaining](#domain-model-set-obtaining)
- [using](#domain-model-set-using)
- [storing](#domain-model-set-storing)

A DomainModelSet object stores info about sets of DomainLibraries.
Each DomainModelSet, when scanned against a Genome, reslts in a single
DomainAnnotation object.  All public DomainModelSets in KBase are in
the workspace KBasePublicGeneDomains.

##### <A NAME="domain-model-set-ds"></A>data structure
```
    { ## KBaseGeneFamilies.DomainModelSet
      set_name: 'string',								# user defined name of set
      domain_libs: { 'domain_prefix_1': 'ws_lib_id_1',	# mapping of domain prefixes (e.g., "PF") to DomainLibrary objects stored in a workspace
					 'domain_prefix_2': 'ws_lib_id_2',
                     ...
                   },
      domain_prefix_to_dbxref_url: { 'domain_prefix_1': 'dbxref_prefix_1',  # mapping of domain prefixes (e.g., "PF") to URL prefixes for external links (e.g., "http://pfam.xfam.org/family/")
									 'domain_prefix_2': 'dbxref_prefix_2',
                                     ...
                                   },
      domain_accession_to_description: { 'domain_accession_1': 'description_1', # mapping of domain accession codes to descriptions
										 'domain_accession_2': 'description_2',
										 ...
                                       }
    }
```

##### <A NAME="domain-model-set-setup"></A>setup
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
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
```

##### <A NAME="domain-model-set-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        self.log(console, 'getting DomainModelSet object: '+params['workspace_name']+'/'+params['domain_model_set_name'])
		domain_model_set_ref = params['workspace_name']+'/'+params['domain_model_set_name']
		domain_model_set = ws.get_objects([{'ref': domain_model_set_ref}])[0]['data']
						
```

##### <A NAME="domain-model-set-using"></A>using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
		# Write a list of domains in the model set to a tab-delimited file
        #
		tab_file_location = os.path.join(self.scratch, params['file_name']+".txt")
		self.log(console, 'writing domains file: '+tab_file_location)
		tab_file = open(tab_file_location, 'w', 0) 
		for accession, description in domain_model_set['domain_accession_to_description']:
			tab_file.write("%s\t%s" % (accession, description))
		tab_file.close()
```

##### <A NAME="domain-model-set-storing"></A>storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
		self.log(console, 'storing DomainModelSet object: '+params['workspace_name']+'/'+params['output_domain_model_set_name']+' made from DomainLibrary '+params['input_domain_library_name'])
		
		# See above snippet on loading DomainLibrary from workspace.
		# Assuming you've done this and have an object called domain_library.
		# You can put more than one DomainLibrary in a DomainModelSet.

		# 1) make DomainModelSet from DomainLibrary
		domain_model_set = {
			'set_name': 'My DomainModelSet',
			'domain_libs': {},
			'domain_prefix_to_dbxref_url': {},
			'domain_accession_to_description': {}
			}
			
		domain_model_set['domain_libs'][domain_library.domain_prefix] =
			domain_library.id

		domain_model_set['domain_prefix_to_dbxref_url'][domain_library.domain_prefix] =
			domain_library.dbxref_prefix
		
		for accession in domain_library['domains']:
			description = domain_library['domains'][accession]['description']
			domain_model_set['domain_accession_to_description'][accession] =
				description
		
		# 2) save object and provenance to workspace
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
		provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['input_domain_library_name'])
	    provenance[0]['service'] = 'MyModule'
	    provenance[0]['method'] = 'MyMethod'
		
		# save object in workspace
        new_obj_info = ws.save_objects({
			'workspace': params['workspace_name'],
			'objects': [ {
				'type': 'KBaseGeneFamilies.DomainModelSet',
				'data': domain_model_set,
				'name': params['output_domain_model_set_name'],
				'meta': {},
				'provenance': provenance
				} ] })
```
[\[back to data type list\]](#data-type-list)



#### <A NAME="domain-annotation"></A>DomainAnnotation
https://narrative.kbase.us/functional-site/#/spec/type/KBaseGeneFamilies.DomainAnnotation

- [data structure](#domain-annotation-ds)
- [setup](#domain-annotation-setup)
- [obtaining](#domain-annotation-obtaining)
- [using](#domain-annotation-using)
- [storing](#domain-annotation-storing)

A DomainAnnotation object stores the results of annotating the proteins
in a genome with one or more domain databases (e.g., Pfam or COGs)

##### <A NAME="domain-annotation-ds"></A>data structure
```
    { ## KBaseGeneFamilies.DomainAnnotation
      genome_ref: 'genome_ref',			# reference to KBaseGenomes.Genome
      used_dms_ref: 'dms_ref', 			# reference to KBaseGeneFamilies.DomainModelSet
      data: { 'contig_id_1' : [ { feature_id: 'string', # feature with a domain annotation
								  feature_start: <int>, # start position of feature in contig, 0-indexed
								  feature_stop: <int>,  # stop position of feature in contig, 0-indexed, always greater than feature_start regardless of strand
								  feature_dir: <-1/1>, 	# direction of feature in contig (-1 is '-' strand, 1 is '+' strand)
				   	       		  # mapping of domain model accessions to hits in this feature
								  { 'domain_accession_1': [ { 'start_in_feature': <int>, # start position of domain relative to beginning of feature, 0-indexed
															  'stop_in_feature': <int>,  # stop position of domain relative to beginning of feature, 0-indexed
															  'evalue': <float>, 	       # E-value of hit
															  'bitscore': <float>, 	   # bit score of hit
															  'domain_coverage': <float>, # fraction of domain model aligned to protein (0-1)
														    },
														    ...
														  ],
									...
								  }
								},
								...
							  ],
			  'contig_id_2' : [ # same data structure as above ],
			  ...
            },
      contig_to_size_and_feature_count: { 'contig_id_1' : { size: <int>,    # size of contig in nucleotides
															features: <int> # number of features in the contig
														  },
										  ...
                                        },
      feature_to_contig_and_index: { 'feature_id_1' : { contig_id: 'contig_ref' # reference to the contig the feature is in
														feature_index: <int>	# 0-based index of where the feature is in the 'data' array for the contig
													  },
                                     ...
                                   }
    }
```

##### <A NAME="domain-annotation-setup"></A>setup
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
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()
```

##### <A NAME="domain-annotation-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        self.log(console, 'getting domain_annotation object: '+params['workspace_name']+'/'+params['domain_annotation_name'])
		domain_annotation_ref = params['workspace_name']+'/'+params['domain_annotation_name']
		domain_annotation = ws.get_objects([{'ref': domain_annotation_ref}])[0]['data']
```

##### <A NAME="domain-annotation-using"></A>using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.
```python
		# Write a list of domain hits in a DomainAnnotation object
		# to a tab-delimited file
		#
		tab_file_location = os.path.join(self.scratch, params['file_name']+".txt")
		self.log(console, 'writing domains file: '+tab_file_location)
		tab_file = open(tab_file_location, 'w', 0)
		for contig_id in domain_annotation['data']:
			for annotation in domain_annotation['data'][contig_id]:
				feature_id = annotation[0]
				feature_start = annotation[1]
				feature_stop = annotation[2]
				feature_dir = annotation[3]
				for hit in annotation[4]:
					start = hit[0]
					stop = hit[1]
					evalue = hit[2]
					bitscore = hit[3]
					domain_coverage = hit[4]
					tab_file.write("%s\t%s\t%d\t%d\t%s\t%d\t%d\t%f\t%f\t%f" % (contig_id, feature_id, feature_start, feature_stop, feature_dir, start, stop, evalue, bitscore, domain_coverage))
		tab_file.close()
```

##### <A NAME="domain-annotation-storing"></A>storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
		self.log(console, 'storing DomainAnnotation object: '+params['workspace_name']+'/'+params['output_domain_annotation_name'])
		
		# 1) make a sample DomainAnnotation containing a single Pfam hit
		#    to a feature in a genome
		#
		domain_annotation = {
			'genome_ref': genome_ref,
			'used_dms_ref': domain_model_set_ref,
			'data': {},
		    'contig_to_size_and_feature_count': {},
			'feature_to_contig_and_index': {}
			}
		genome = ws.get_objects([{'ref':genome_ref}])[0]['data']
		# use the first feature as an example
		my_feature = genome['features'][0]
		my_feature_id = my_feature['id']
		my_location = my_feature['location']
		my_contig_id = location[0]
		domain_annotation['feature_to_contig_and_index'][my_feature_id] = {
			'contig_id': my_contig_id,
			'feature_index': 0
			}
		contigset_ref = genome['contigset_ref']
		contigset = ws.get_objects([{'ref':contigset_ref}])[0]['data']
		for contig_id in contigset['contigs']:
			if contig_id == my_contig_id:
				domain_annotation['contig_to_size_and_feature_count'][contig_id] = {
					'size': contigset['contigs'][contig_id]['length'],
					'features': 1
					}
		# add a hit to my feature; assume to first domain in set
		domain_model_set = ws.get_objects([{'ref':domain_model_set_ref}])[0]['data']
		my_domain_accession = domain_model_set['domain_accession_to_description'].keys()[0]
		if my_location[2] == "+":
			my_feature_dir = 1
			my_feature_start = my_location[1]
			my_feature_stop = my_feature_start + my_location[3] - 1
		else
			my_feature_dir = -1
			my_feature_start = my_location[1] - my_location[3] + 1
			my_feature_stop = my_location[1]
		domain_annotation['data'][my_contig_id] = [ {
			'feature_id': my_feature_id,
			'feature_start': my_feature_start,
			'feature_stop': my_feature_stop,
			'feature_dir': my_feature_dir,
			{ my_domain_accession: [ {
				'start_in_feature': 0,
				'stop_in_feature': my_location[3] - 1,
				'evalue': 0.0,
				'bitscore': 100.0,
				'domain_coverage': 1.0
				} ] } } ]

		# 2) save object and provenance to workspace
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
	    provenance[0]['service'] = 'MyModule'
	    provenance[0]['method'] = 'MyMethod'
		
		# save object in workspace
        new_obj_info = ws.save_objects({
			'workspace': params['workspace_name'],
			'objects': [ {
				'type': 'KBaseGeneFamilies.DomainAnnotation',
				'data': domain_annotation,
				'name': params['output_domain_annotation_name'],
				'meta': {},
				'provenance': provenance
				} ] })
```
[\[back to data type list\]](#data-type-list)



### <A NAME="msa"></A>MSA
https://narrative.kbase.us/functional-site/#/spec/type/KBaseTrees.MSA

- [data structure](#msa-ds)
- [setup](#msa-setup)
- [obtaining](#msa-obtaining)
- [using](#msa-using)
- [storing](#msa-storing)

Multiple Sequence Alignments (MSA) are used for examining sequence variation across homologous sequences and as inputs to phylogenetic reconstructions, such as Trees.

##### <A NAME="msa-ds"></A>data structure
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
  parent_msa_ref: 'ws_msa_ref'                  # reference to parental alignment object to which 
                                                # this object adds some new aligned sequences 
}
```

##### <A NAME="msa-setup"></A>setup
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

##### <A NAME="msa-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        MSA_ref = params['workspace_name']+'/'+params['MSA_name']
        self.log(console, 'getting MSA object: '+MSA_ref)
        MSA = ws.get_objects([{'ref': MSA_ref}])[0]['data']
```

##### <A NAME="msa-using"></A>using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        # export contig sequences to FASTA file
        fasta_file_location = os.path.join(self.scratch, params['MSA_name']+".fasta")
        self.log(console, 'writing '+MSA_ref+' to fasta file: '+fasta_file_location)

        records = []
        for row_id in MSA['row_order']:
            aln_sequence = MSA['alignment'][row_id]
            record = SeqRecord(Seq(aln_sequence), id=row_id, description=contig_id)
            records.append(record)
        SeqIO.write(records, fasta_file_location, "fasta")
```

##### <A NAME="msa-storing"></A>storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
        self.log(console, 'storing MSA object: '+params['workspace_name']+'/'+params['output_MSA_name'])

        # parse the output and save back to KBase
        output_MSA = os.path.join(output_dir, 'final.msa.fa')

        MSA_data = {
            'name' : 'multiple alignmemnt for FeatureSet: ' + params['featureset_name'],
            'sequence_type': 'protein',
            'alignment_length': 0,
            'alignment': {},
            'row_order': []
        }
        for record in SeqIO.parse(output_MSA, "fasta"):
            sequence = str(record.seq)
            alignment_length = len(sequence)
            MSA_data['row_order'].append(record.id)
            MSA_data['alignment'][record.id] = sequence
            MSA_data['alignment_length'] = alignment_length
        
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
        if 'featureset_name' in params:
            provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['featureset_name'])
        if 'parent_msa_ref' in MSA:
            provenance[0]['input_ws_objects'].append(MSA['parent_msa_ref')
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseTrees.MSA',
									'data': MSA_data,
									'name': params['output_MSA_name'],
									'meta': {},
									'provenance': provenance
								}]
                        })
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
```
[\[back to data type list\]](#data-type-list)



### <A NAME="tree"></A>Tree
https://narrative.kbase.us/functional-site/#/spec/type/KBaseTrees.Tree

- [data structure](#tree-ds)
- [setup](#tree-setup)
- [obtaining](#tree-obtaining)
- [using](#tree-using)
- [storing](#tree-storing)

Phylogenetic trees may represent the evolutionary relationships of either species/genomes or genes.

##### <A NAME="tree-ds"></A>data structure
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
  type: ‘tree_type’, # 'SpeciesTree' or 'GeneTree'
  tree: ‘newick_string’,
  tree_attributes: { 'attr_1': 'val_1',
                     'attr_2': 'val_2',
                     ...
                   },
  default_node_labels: { 'node_id_1': 'label_1',
                         'node_id_2', 'label_2',
		         ...
                       },
  ws_refs: { ‘node_id_1’: { ‘ref_type’: [‘ws_obj_id_1’, ...] },
	     ‘node_id_2’: ...
 	   },
  kb_refs: { ‘node_id_1’: { ‘ref_type’: [‘kbase_id_1’, ...] },
	     ‘node_id_2’: ...
	   },
  leaf_list: [‘node_id_1’, ‘node_id_2’, ...]
}
```

##### <A NAME="tree-setup"></A>setup
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
from Bio import Phylo
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

##### <A NAME="tree-obtaining"></A>obtaining
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for retrieving the data object.

```python
        treeRef = params['workspace_name']+'/'+params['tree_name']
        self.log(console, 'getting featureset object: '+treeRef)
        tree = ws.get_objects([{'ref':treeRef}])[0]['data']
```

##### <A NAME="tree-using"></A>using
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for manipulating the data object.

```python
        # export tree to NEWICK file
        newick_file_location = os.path.join(self.scratch, params['tree_name']+".newick")
        self.log(console, 'writing fasta file: '+fasta_file_location)
        newick_file_handle = open(newick_file_location,'w')
        newick_file_handle.write(tree['tree'])
        newick_file_handle.close()

        # export 16S features from Genome to FASTA file
        fasta_file_location = os.path.join(self.scratch, params['tree_name']+"-16S"+".fasta")
        self.log(console, 'writing fasta file: '+fasta_file_location)
        records = []
        if tree['tree_type'] == 'species':
            for leaf_id in tree['leaf_list']:
                genomeRef = tree['ws_refs'][node_id]['genome'][0]
                self.log(console, 'getting genome object: '+genomeRef)
                genome = ws.get_objects([{'ref':genomeRef}])[0]['data']
                longest_feature = None
                longest_feature_len = 0
                for feature in genome['features']:
                    if feature['type'] == 'rna' and ('SSU rRNA' in feature['function']) or 'ssuRNA' in feature['function']:
                        if feature['dna_sequence_length'] > longest_feature_len:
                            longest_feature_len = feature['dna_sequence_length']
                            longest_feature = feature
                record = SeqRecord(Seq(longest_feature['dna_sequence']), id=longest_feature['id'], description=genomeRef+"."+longest_feature['id'])
                records.append(record)
        SeqIO.write(records, fasta_file_location, "fasta")
```

##### <A NAME="tree-storing"></A>storing
The following is a python snippet (e.g. for use in the SDK \<module_name\>Impl.py file) for storing the data object.

```python
        self.log(console, 'storing tree object: '+params['workspace_name']+'/'+params['output_tree_name'])

        output_tree_file_location = os.path.join(self.scratch, params['output_tree_name']+".newick")
        with open (output_tree_file_location, 'r') as newick_file_handle:
            output_newick_buf = newick_file_handle.read().replace('\n','')
        output_tree_data_format = 'newick'
        output_tree_phylo = Phylo.read(output_tree_file_location, output_tree_data_format)

        tree_type = 'SpeciesTree'  # or 'GeneTree'
        ref_type = 'genome'   # if ids parsed from output are genome ids
        #ref_type = 'protein'  # if ids parsed from output are protein ids
        #ref_type = 'dna'      # if ids parsed from output are any dna ids
        #ref_type = 'feature'  # if ids parsed from output are feature ids
	
        tree_data = { ## KBaseTrees.Tree
        		name: params['output_tree_name'],
        		description: params['output_tree_desc'],
        		type: tree_type,   
        		tree: output_newick_buf,
        		tree_attributes: {},
        		default_node_labels: {},
        		ws_refs: {},
        		kb_refs: {},
        		leaf_list: []
        }
        for leaf in output_tree_phylo.get_terminals():  # for just leaf nodes
            tree_data['leaf_list'].append(leaf.name)
        #for node in output_tree_phylo.get_nonterminals():  # for just non-leaf nodes
        #    print node.name
        for node in output_tree_phylo.find_clades():  # for all internal and leaf nodes
            tree_data['default_node_labels'][node.name] = node.name  # don't have an alternate label
            tree_data['ws_refs'][node.name] = { ref_type: [node.name] }  # if names in newick file are ws_refs
            #tree_data['kb_refs'][node.name] = { ref_type: [node.name] }  # if names in newick file are kbase_ids

        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference, service, and method
        provenance[0]['input_ws_objects'] = []
        provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['MSA_name'])
        provenance[0]['service'] = 'MyModule'
        provenance[0]['method'] = 'MyMethod'
        
        # save object in workspace
        new_obj_info = ws.save_objects({
							'workspace': params['workspace_name'],
							'objects':[{
									'type': 'KBaseTrees.Tree',
									'data': tree_data,
									'name': params['output_tree_name'],
									'meta': {},
									'provenance': provenance
								}]
                        })
        #return new_obj_info[0]  # obj_ID
        return new_obj_info[1]  # obj_NAME
```
[\[back to data type list\]](#data-type-list)


<!--
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

-->


### <A NAME="network"></A>Network
https://narrative.kbase.us/functional-site/#/spec/type/KBaseNetworks.Network
```

   { ## KBaseNetworks.Network
     id: "id of network",                                                               # e.g. "kb|net.0"
     name:"name of network",                                                            # e.g. "kb|netdataset.plant.cc.15: buildNetwork"
     properties: {"Other_properties_of_network"},                                       # e.g. {} 
     user_annotations: {"user annotations"},                                            # e.g. {}

     edges: [
              { id: "Unique identifier of edge",                                        # e.g. "kb|g.3907.nds.10.edge.0"
                name:       "Name of edge",                                             # e.g. "Member of cluster"              
                node_id1:   "id of first node (source node for directed)",              # e.g. "kb|netnode.165"
                node_id2:   "id of second node(target node for directed)",              # e.g. "kb|netnode.1"
                directed:   "boolean false/true indicating directedness",               # e.g. "false"
                confidence: "Probability between 0 and 1 that interaction is true",     # e.g. 0
                strength:   "Value between 0 and 1 for strength of interaction",        # e.g. 0.8
                dataset_id: "identifier of dataset that provided the edge information", # e.g. "kb|g.3907.nds.10"
                properties: {"other_edge_property":1},                                  # e.g. {}
                user_annotations:{"user_annotation_1":1},                               # e.g. {}
              }
            ],
     nodes: [
              {
                entity_id: "id of entity represented by node",                          # e.g. "kb|g.3907.CDS.83425"
                id: "id of node",                                                       # e.g. "kb|netnode.7137"
                name:"string representation of node",                                   # e.g. "POPTR_0002s00410"
                properties: "Other properties of node",                                 # e.g. {}
                type: "type of node",                                                   # e.g. "GENE"
                user_annotations:"user annotations of node",                            # e.g. {}
              }
            ],
     datasets: [
                 { description: "description",                                          # e.g. "Coexpression Cluster for Populus"
                   id : "id of dataset",                                                # e.g. "kb|g.3907.nds.10",
                   name: "name of dataset",                                             # e.g. "Coexpression Cluster - Populus",
                   network_type: "Type of network generated from a given dataset",      # e.g. "FUNCTIONAL_ASSOCIATION"
                   properties: {"Other_properties":1},                                  # e.g. {}
                   source_ref: "Reference to a dataset source",                         # e.g."GEO"
                   taxons: ["A list of NCBI taxonomy ids related to data"],             # e.g. ["kb|g.3907"]
                  }
               ]

```
