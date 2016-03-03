# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. **Edit Module and Method(s)**
5. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
6. [Register Module](kb_sdk_register_module.md)
7. [Test in KBase](kb_sdk_test_in_kbase.md)
8. [Complete Module Info](kb_sdk_complete_module_info.md)
9. [Deploy](kb_sdk_deploy.md)


### 4. Edit Module and create Method(s)

- A. [Creating a Git Repo](#create-repo)
- B. [Edit Module Description](#edit-desc)
- C. [Create KIDL specification for Module](#kidl-spec)
- D. [Validate](#validate)
- E. [Create stubs for methods](#stubs)
- F. [Edit Impl file](#impl)
- G. [Creating Narrative UI Input Widget](#ui-widget)


#### <A NAME="create-repo"></A>A. Creating a Git Repo

You will need to check your SDK Module into Git in order for it to be available for building into a custom Docker Image.  Since functionality in KBase is pulled into KBase from public git repositories, you will need to put your module code into a public git repository.  Here we'll show a brief example using [GitHub](http://github.com).  First you can commit your module code into a local git repository. Go into the directory where your module code is, git add all files created by kb-sdk, and commit with some commit message. This creates a git repository locally.

    cd MyModule
    git init
    git add .
    git commit -m 'initial commit'

Now you can sync this to a new GitHub repository. First create a new GitHub repository on github.com
(it can be in your personal GitHub account or in an organization, but it must be public),
but do not initialize it! Just go here to set up a new empty repository: https://github.com/new or see more
instructions here: https://help.github.com/articles/creating-a-new-repository .  You may wish to
use the name of your module as the name for your new repository.

    git remote add origin https://github.com/[GITHUB_USER_OR_ORG_NAME]/[GITHUB_MODULE_NAME].git
    git push -u origin master

*Remember to update the code in the Git Repo as you change it via "git commit / pull / push" cycles.*

[\[Back to top\]](#top)


#### <A NAME="edit-desc"></A>B. Edit Module Description

Open and edit the **kbase.yml** file to include a better description of your module.  The default generated description isn't very good.

[\[Back to top\]](#top)


#### <A NAME="kidl-spec"></A>C. Create KIDL specification for Module

The first step is to define the interface to your code in a KIDL specification, sometimes called the "Narrative Method Spec".  This will include the parameters passed to the methods and the declaration of the methods.  **You must rerun *make* after each change to the KIDL specification to [create or update the implementation stubs](#stubs).**

Open the `ContigFilter.spec` file in a text editor, and you will see this:

    /*
    A KBase module: MikeContigFilter
    */
    module MikeContigFilter {
        /*
        Insert your typespec information here.
        */
    };

Comments are enclosed in `/* comment */`.  In this module, we want to define a function that counts contigs, so let's define that function and its inputs/outputs as:

    typedef string contigset_id;
    typedef structure {
        int contig_count;
        int filtered_contig_count;
    } FilterContigResults;

    funcdef filter_contigs(string workspace_name, contigset_id contigset)
                returns (FilterContigResults) authentication required;

There a few things introduced here that are part of the KBase Interface Description Language (KIDL).  First, we use `typedef` to define the structure of input/output parameters using the syntax:

    typedef [type definition] [TypeName]

The type definition can either be another previously defined type name, a primitive type (string, int, float), a container type (list, mapping) or a structure.  In this example we define a string named `contigset_id` and a structure named `CountContigResults` with a single integer field named `contig_count`.

We can use any defined types as input/output parameters to functions.  We define functions using the `funcdef` keyword in this syntax:

    funcdef method_name([input parameter list]]) returns ([output parameter list]);

Optionally, as we have shown in the example, your method can require authentication by adding that declaration at the end of the method.  In general, all your methods will require authentication.

Make sure you pass in the *workspace_name* in the input parameters.

```
    typedef structure {
    	string workspace_name;
    	...
    } <Module>Params;
```
[\[Back to top\]](#top)


#### <A NAME="validate"></A>D. Validate

When you make changes to the Narrative method specifications, you can validate them for syntax locally.  From the base directory of your module:

    kb-sdk validate

[\[Back to top\]](#top)


#### <A NAME="stubs"></A>E. Create stubs for methods

After editing the <MyModule>.spec KIDL file, generate the Python (or other language) implementation stubs (e.g. the \<MyModule\>Impl.py file) by running

    make

This will call `kb-sdk compile` with a set of parameters predefined for you.

[\[Back to top\]](#top)


#### <A NAME="impl"></A>F. Edit Impl file

In the lib/\<MyModule\>/ directory, edit the <MyModule>Impl.py (or *.pl) "Implementation" file that defines the methods available in the module.  You can follow this guide for interacting with [KBase Data Types](doc/kb_sdk_data_types.md).  Basically, the process consists of obtaining data objects from the KBase workspace, and either operating on them directly in code or writing them to scratch files that the tool you are wrapping will operate on.  Result data should be collected into KBase data objects and stored back in the workspace.

- F.1. [Using Data Types](#impl-data-types)
- F.2. [Logging](#impl-logging)
- F.3. [Provenance](#impl-provenance)
- F.4. [Building Output Report](#impl-report)
- F.5. [Invoking Shell Tool](#impl-shell-tool)
- F.6. [Adding Data to Your Method](#impl-adding-data)

##### <A NAME="impl-data-types"></A>F.1. Using Data Types

Data objects are typed and structured in KBase.  You may write code that takes advantage of these structures, or extract the data from them to create files that the external tool you are wrapping requires (e.g. FASTA).  Please take advantage of the code snippets in the [KBase Data Types](doc/kb_sdk_data_types.md), you can also look at the [Examples](#examples) for syntax and style guidance.

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-logging"></A>F.2. Logging

Logging where you are is key to tracking progress and debugging.  Our recommended style is to log to a "console" list.  Here is some example code for accomplishing this.

```python
    from pprint import pprint, pformat

    # target is a list for collecting log messages
    def log(self, target, message):
        # we should do something better here...
        if target is not None:
            target.append(message)
        print(message)
        sys.stdout.flush()

    def run_<MyMethod>(self, ctx, params):
        console = []
        self.log(console,'Running run_<MyMethod> with params=')
        self.log(console, pformat(params))
```    

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-provenance"></A>F.3. Provenance

Data objects in KBase contain provenance (historical information of their creation and objects from which they are derived).  When you create new objects, you must carry forward and add provenance information to them.  Additionally, Report objects should receive that provenance data (see below).  Examples of adding provenance to objects can be found in the [KBase Data Types](docs/kb_sdk_data_types.md).

```python
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference
        provenance[0]['input_ws_objects']=[]
        provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['read_library_name'])
```

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-report"></A>F.4. Building Output Report

```python
        # create a Report
        report = ''
        report += 'ContigSet saved to: '+params['workspace_name']+'/'+params['output_contigset_name']+'\n'
        report += 'Assembled into '+str(len(contigset_data['contigs'])) + ' contigs.\n'
        report += 'Avg Length: '+str(sum(lengths)/float(len(lengths))) + ' bp.\n'

        # compute a simple contig length distribution
        bins = 10
        counts, edges = np.histogram(lengths, bins)
        report += 'Contig Length Distribution (# of contigs -- min to max basepairs):\n'
        for c in range(bins):
            report += '   '+str(counts[c]) + '\t--\t' + str(edges[c]) + ' to ' + str(edges[c+1]) + ' bp\n'

        reportObj = {
            'objects_created':[{'ref':params['workspace_name']+'/'+params['output_contigset_name'], 'description':'Assembled contigs'}],
            'text_message':report
        }

        reportName = 'megahit_report_'+str(hex(uuid.getnode()))
        report_obj_info = ws.save_objects({
                'id':info[6],
                'objects':[
                    {
                        'type':'KBaseReport.Report',
                        'data':reportObj,
                        'name':reportName,
                        'meta':{},
                        'hidden':1,
                        'provenance':provenance
                    }
                ]
            })[0]

        output = { 'report_name': reportName, 'report_ref': str(report_obj_info[6]) + '/' + str(report_obj_info[0]) + '/' + str(report_obj_info[4]) }
```

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-shell-tool"></A>F.5. Invoking Shell Tool

```python
        command_line_tool_params_str = " ".join(command_line_tool_params)
        command_line_tool_cmd_str = " ".join(command_line_tool_path, command_line_tool_params_str)
        
        # run <command_line_tool>, capture output as it happens
        self.log(console, 'running <command_line_tool>:')
        self.log(console, '    '+command_line_tool_cmd_str))
        p = subprocess.Popen(command_line_tool_cmd_str,
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
            raise ValueError('Error running <command_line_tool>, return code: '+str(p.returncode) + 
                '\n\n'+ '\n'.join(console))
 ```

[\[Back to Edit Impl\]](#impl)

##### <A NAME="impl-adding-data"></A>F.6. Adding Data To Your Method

Data that is supported by [KBase Data Types](doc/kb_sdk_data_types_table.md) should be added as a workspace object.  Other data that is used to configure a method may be added to the repo with the code.  Large data sets that exceed a reasonable limit (> 1 GB) should be added to a shared mount point.  This can be accomplished by contacting kbase administrators at http://kbase.us.

[\[Back to Edit Impl\]](#impl)

[\[Back to top\]](#top)


#### <A NAME="ui-widget"></A>G. Creating Narrative UI Input Widget

Control of Narrative interaction is accomplished in files in the ui/narrative/methods/<MyMethod> directory.

##### G.1. Creating fields in the input widget cell

Edit *display.yaml*:

```
name: MegaHit
tooltip: |
	Run megahit for metagenome assembly
screenshots: []

icon: kb_logo.png

#
# define a set of similar methods that might be useful to the user
#
suggestions:
	apps:
		related:
			[]
		next:
			[]
	methods:
		related:
			[]
		next:
			[]

#
# Configure the display and description of parameters
#
parameters :
    read_library_name :
        ui-name : Read Library
        short-hint : Read library (only PairedEnd Libs supported now)
    output_contigset_name:
        ui-name : Output ContigSet name
        short-hint : Enter a name for the assembled contigs data object

description : |
	<p>This is a KBase wrapper for MEGAHIT.</p>
    <p>MEGAHIT is a single node assembler for large and complex metagenomics NGS reads, such as soil. It makes use of succinct de Bruijn graph (SdBG) to achieve low memory assembly.</p>
publications :
    -
        pmid: 25609793
        display-text : |
            'Li, D., Liu, C-M., Luo, R., Sadakane, K., and Lam, T-W., (2015) MEGAHIT: An ultra-fast single-node solution for large and complex metagenomics assembly via succinct de Bruijn graph. Bioinformatics, doi: 10.1093/bioinformatics/btv033'
        link: http://www.ncbi.nlm.nih.gov/pubmed/25609793
    -
        link: https://github.com/voutcn/megahit
```

##### G.2. Configure passing variables from Narrative Input to SDK method.

Edit *spec.json*:

```
{
	"ver": "1.0.0",
	
	"authors": [
		"YourName"
	],
	"contact": "help@kbase.us",
	"visible": true,
	"categories": ["active","assembly","communities"],
	"widgets": {
		"input": null,
		"output": "kbaseReportView"
	},
	"parameters": [ 
		{
			"id": "read_library_name",
			"optional": false,
			"advanced": false,
			"allow_multiple": false,
			"default_values": [ "" ],
			"field_type": "text",
			"text_options": {
				"valid_ws_types": ["KBaseAssembly.PairedEndLibrary","KBaseFile.PairedEndLibrary"]
			}
		},
		{
		    "id" : "output_contigset_name",
		    "optional" : false,
		    "advanced" : false,
		    "allow_multiple" : false,
		    "default_values" : [ "MegaHit.contigs" ],
		    "field_type" : "text",
		    "text_options" : {
		     	"valid_ws_types" : [ "KBaseGenomes.ContigSet" ],
		    	"is_output_name":true
		    }
		}
	],
	"behavior": {
		"service-mapping": {
			"url": "",
			"name": "MegaHit",
			"method": "run_megahit",
			"input_mapping": [
				{
					"narrative_system_variable": "workspace",
					"target_property": "workspace_name"
				},
				{
					"input_parameter": "read_library_name",
          			"target_property": "read_library_name"
				},
				{
					"input_parameter": "output_contigset_name",
          			"target_property": "output_contigset_name"
				}
			],
			"output_mapping": [
				{
					"narrative_system_variable": "workspace",
					"target_property": "workspace_name"
				},
				{
					"service_method_output_path": [0,"report_name"],
					"target_property": "report_name"
				},
				{
					"service_method_output_path": [0,"report_ref"],
					"target_property": "report_ref"
				},
				{
					"constant_value": "16",
					"target_property": "report_window_line_height"
				}
			]
		}
	},
	"job_id_output_field": "docker"
}
```

Make sure you configure *workspace_name*

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

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
