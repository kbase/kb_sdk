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

### <A NAME="ui-widget"></A>6. Specify User Interface

Control of Narrative interaction is accomplished in files in the ui/narrative/methods/<MyMethod> directory.

#### A. Configure the input interface.

The input options are specified in the "parameters" section of the spec.json file. In the following example, the user
supplies two required parameters, an input name and an output name. By specifying a 'valid_ws_types' the user will
be present a searchable dropdown of objects that match the specified type. By passing "is_output_name", the user is
warned if a name with overwrite an existing object or if the name contains invalid characters.

```
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
```
Another common input is a dropdown which is demonstrated below. For each option, the "value" is what will be passed to
the script while the UI Name and Display is what the user will see. In this example, the parameter is hidden be default 
because "advanced" is true.
```
{
            "id": "prune",
            "optional": false,
            "advanced": true,
            "allow_multiple": false,
            "default_values": [ "biochemistry" ],
            "field_type": "dropdown",
            "dropdown_options": {
                "options": [
                    {
                        "value": "biochemistry",
                        "display": "Known Biochemistry",
                        "id": "biochemistry",
                        "ui_name": "Known Biochemistry"
                    },
                    {
                        "value": "model",
                        "display": "Input Model",
                        "id": "model",
                        "ui_name": "Input Model"
                    },
                    {
                        "value": "none",
                        "display": "Do not prune",
                        "id": "none",
                        "ui_name": "Do not prune"
                    }
                ]
            }
        },
```
#### B . Configure passing variables from Narrative Input to SDK method.

In the 'behavior' section of the spec.json, the output of the user interface is mapped to input to your function.
If you have maintained a consistent naming though these mappings are pretty pro forma but you should make sure
that you accept and return "workspace" as a narrative_system_variable as shown below.
```
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

#### C. Naming fields in the input widget cell

The display.yml primarily contains text to explain the method in the narrative and especially in the app catalogue.
Options in this file will be further explored in step 10: [Complete Module Info](kb_sdk_complete_module_info.md) but
minimally this file should define:
* A module name
* A module tooltip
* A ui-name for each parameter
* A short hint for each parameter
