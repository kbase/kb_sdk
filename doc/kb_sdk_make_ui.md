# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install SDK with Docker](kb_sdk_dockerized_install.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. [Implement Method(s)](kb_sdk_impl_methods.md)
6. **Specify User Interface**
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)

#### <A NAME="ui-widget"></A>6. Specify User Interface

Control of Narrative interaction is accomplished in files in the ui/narrative/methods/<MyMethod> directory.

##### A. Creating fields in the input widget cell

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

##### B. Configure passing variables from Narrative Input to SDK method.

Edit *spec.json*:

```
{
	"ver": "1.0.0",
	
	"authors": [
		"YourName"
	],
	"contact": "http://kbase.us/contact-us/",
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