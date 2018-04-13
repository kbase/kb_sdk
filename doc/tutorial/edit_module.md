# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

0. [Overview and Concepts](overview.md)
1. [Install SDK Dependencies](dependencies.md)
2. [Install SDK with Docker](dockerized_install.md)
3. [Create Module](create_module.md)
4. **Specify Module and Method(s)**
5. [Implement Method(s)](impl_methods.md)
6. [Publish and Test on Appdev](publish.md)


#### Basic app config

Open and edit the **kbase.yml** file to include a better description of your app. Be sure to describe what your app actually does.

#### Think about your app's input and output

To start, we can consider our inputs and outputs. Our tutorial app will do the following:

* Take a reference to an assembly file as input
* Take `min_length` as input (the minimum contig length that the user wants)
* Download their assembly file from the KBase servers using the reference
* Filter out the contigs in the assembly file that are below `min_length`
* Upload the filtered assembly file
* Return a reference to the new assembly, plus some data about what we filtered

#### Create input parameters

In SDK apps, we can have input parameters in the form of maps (dicts/hashes/objects), lists (arrays), floats, integers, strings, or booleans.

Sometimes, input strings will actually be **reference addresses** to files on KBase's workspace servers.

SDK apps can output the following data:

* **KBase Typed Data** - You can output typed data objects, like assemblies or genomes
* **HTML Pages** - Your method can use KBaseReports to generate an HTML report displaying results.
* **Misc. files for download** - You method can use KBaseReports to save results to a file server for the user to download

Let's start by defining our input parameters. We need a `min_length` parameter (an integer), and an `assembly_ref` parameter (a string reference to an assembly file in the workspace).

To add an input parameter to your app, you need to update three configuration files:

1. `module_name.spec` -- a type specification file
1. `ui/narrative/example_method/spec.json` -- a UI configuration file
1. `ui/narrative/example_method/display.yaml` -- a text content file

We'll start with the `module_name.spec` file.

#### Add some KIDL type definitions

First, add the input parameter types to your app's KIDL specification, which lives in `<module_name>.spec` in the root directory of your codebase. 

Open your KIDL spec file, and you will see something like this:

```
/*
A KBase module: MyContigFilter
*/
module MyContigFilter {
    /*
        Insert your typespec information here.
    */
};
```

The above syntax comes from a custom type language called KIDL that is similar to languages like C++, but is specific to KBase. KIDL is used as a common interface definition language, allowing different apps to communicate with one another, regardless of programming languages.

[View the KIDL tutorial and reference](/doc/KIDL_specification.md)

Our input and output types need to be in `structure` types. Add these type structures inside your module section:

```
    /* Input parameter types */
    typedef structure {
        int min_length;
        string assembly_ref;
    } ContigFilterParams;

    /* Output result types */
    typedef structure {
    } ContigFilterResults;
```

Above, we've added a couple input parameters: an `int` for the minimum length that the user wants, and a `string` that will reference an assembly data file on KBase's servers.

We also added a placeholder type structure for our output results, which we will return to later. For now, it can be blank.

Now insert a function type for our app's main method, which we can call `filter_contigs`. Refer to the [KIDL specification](/doc/KIDL_specification.md) for details about function types.

```
funcdef filter_contigs(string workspace, ContigFilterParams params)
    returns (ContigFilterResults) authentication required;
```

In SDK apps, we want to set the function as `authentication required` because all SDK apps that run in the Narrative will require authentication since they need to interact with a user's workspace.

Now return to your app's root directory and run `make`

 **You must rerun *make* after each change to the KIDL specification to regenerate client and server code used in the codebase**

#### Validate your app

When you make changes to your KIDL `.spec` file, validate the syntax of your changes by running:

```
$ kb-sdk validate
```

For now, you will get an error that looks something like this:

```
**ERROR** - unknown method "your_method" defined within path [behavior/service-mapping/method] in spec.json
```

That's because we need to set up some things in our `/ui/narrative` directory in the app.

#### Update spec.json

The directory named `/ui/narrative/methods/example_method` is a placeholder. Rename it to the name of the actual function we defined in our KIDL .spec file:

```sh
# From your app's root directory:
$ mv ui/narrative/methods/example_method ui/narrative/methods/filter_contigs
```

`filter_contigs` matches the `funcdef` name we used in the KIDL `MyContigFilter.spec` file.

Now open up `ui/narrative/methods/filter_contigs/spec.json`.

This file defines a mapping between our KIDL .spec file and how our parameters will show up in the app's user interface.

Find line 29 where it says `"your_method"` -- change that to say `"filter_contigs"` instead.

In the section under `"parameters"`, add an entry for each of our input parameters:

```json
...
"parameters": [ 
    {
        "id": "assembly_ref",
        "optional": false,
        "advanced": false,
        "allow_multiple": false,
        "default_values": [ "" ],
        "field_type": "text",
        "text_options": {
            "valid_ws_types": [ "KBaseGenomeAnnotations.Assembly", "KBaseGenomes.ContigSet" ]
        }
    },
    {
        "id": "min_length",
        "optional": false,
        "advanced": false,
        "allow_multiple": false,
        "default_values": [ "" ],
        "field_type": "text",
        "text_options": {
            "validate_as": "int",
            "min_integer": "0"
        }
    }
]
...
```

We created an entry for each type that we defined in the `ContigFilterParams` structure in our KIDL file.

Each parameter object has a number of options. 

* We want both parameters to be required (`"optional": false`)
* We want the "assembly_ref" to be a reference to either an Assembly or ContigSet object (view the [type catalog](https://narrative.kbase.us/#catalog/datatypes) to see all KBase tjypes)
* We want the "min_length" parameter to be validated as an integer, and we don't want to allow negative numbers.

Below that section, you will see some default `"input_mapping"` options. Change that section so that it contains entries for each of your input parameters, and for now we can leave the output section empty:

```json
...
"input_mapping": [
    {
        "narrative_system_variable": "workspace",
        "target_argument_position": 0
    },
    {
        "input_parameter": "assembly_ref",
        "target_type_transform": "resolved-ref",
        "target_argument_position": 1
    },
    {
        "input_parameter": "min_length",
        "target_argument_position": 1
    }
],
"output_mapping": [ ]
...
```

Notice that we added a `"target_type_transform"` option with the value `"resolved-ref"` for the `"assembly_ref"`. This indicates to the narrative that this parameter needs to be a valid reference to a file in the workspace.

When you run `kb-sdk validate` again, you will get an error about your `display.yaml`, which we can update next.

#### Update display.yaml

The YAML file found in `ui/narrative/methods/filter_contigs/display.yaml` holds text content for your app.

Open it and update its default fields to match the purpose your app. Change `name` and `tooltip` to say something related to filtering assembly files based on contig length.

You can leave the "screenshots" and "icon" fields to their default values. For now, set empty lists as the values in the "suggestions" section.

Moving down to the "parameters" section, add parameter entries for "assembly_ref" and "min_length" with some helpful descriptions of each.

Example "parameters" section:

```yaml
parameters:
    assembly_ref:
        ui-name: Assembly to filter
        short-hint: |
            Genome assembly with contiguous fragments
        long-hint: |
            Genome assembly where we want to filter out fragments that are below a minimum
    min_length:
        ui-name: |
            Min contig length
        short-hint: |
            Minimum required length of every contig in the assembly
        long-hint: |
            All contigs will be filtered out of the assembly that are shorter than the given length
```

Finally, run `kb-sdk validate` again and it should pass! Now we can start to actually work on the functionality of the app.

**For a more exhaustive overview of the `spec.json` and `display.yaml` files, visit their [specification PDF](https://github.com/kbase/kb_sdk/blob/master/doc/NarrativeUIAppSpecification.pdf).**

[\[Next tutorial page\]](impl_methods.md)<br>
[\[Back to top\]](#top)<br>
[\[Back to steps\]](/README.md#steps)
