# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. **Specify Module and Method(s)**
5. [Implement Method(s)](kb_sdk_impl_methods.md)
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)


### 4. Specify Module and Method(s)

- A. [Creating a Git Repo](#create-repo)
- B. [Scope Module & Methods](#scope-module)
- C. [Create KIDL specification for Module](#kidl-spec)
- D. [Validate](#validate)
- E. [Create stubs for methods](#stubs)

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


#### <A NAME="scope-module"></A>B. Scope Module & Methods

Open and edit the **kbase.yml** file to include a better description of your module. The default generated description 
isn't very good. At this point, it's necessary to make some design choices about the scope of your module and which 
individual methods it will contain. For each method, it is important to consider not just the desired functionality but 
also the inputs and outputs.

Potential Inputs:
* **Function Parameters** - Short parameters in map, list, float, string, and boolean form can be accepted by the 
function. See below for how to pass these these parameters as in input object.
* **Reference data** - Modest sized reference data can be committed to the /data directory.
* **KBase Typed Data** - If your function will work on one of [KBase's defined data types](https://narrative.kbase.us/#catalog/datatypes),
your method should accept a workspace reference to the objects as a string and access the data with DataFileUtil or a 
utility specific to your data type.
* **External web-assessable data** - You method may accept a URL as string and utilize the DataFileUtil to download the
file.

Potential Outputs:
* **KBase Typed Data** - Your method will accept the name of the objects as a string and save the data with DataFileUtils.
* **Graphical/Tabular Output** - Your method can use KBaseReports to generate a HTML report displaying results.
* **Data files for Download** - You method can use KBaseReports to save results to a file server for the user to download
* **New Data Types** - This is beyond the scope of SDK. Please <a href="http://kbase.us/contact-us">contact us</a>
so we can discuss your needs. 

[\[Back to top\]](#top)


#### <A NAME="kidl-spec"></A>C. Create KIDL specification for Module

You will define the programmatic interface to your code in a KIDL specification. This will include the parameters passed 
to the methods and the declaration of the methods.  **You must rerun *make* after each change to the KIDL specification 
to [create or update the implementation stubs](#stubs).**

Open the `ContigFilter.spec` file in a text editor, and you will see this:

    /*
    A KBase module: MikeContigFilter
    */
    module MikeContigFilter {
        /*
        Insert your typespec information here.
        */
    };

Comments are enclosed in `/* comment */`.  In this module, we want to define a function that counts contigs, so let's 
define that function and its inputs/outputs as:

    typedef string contigset_id;
    typedef structure {
        int contig_count;
        int filtered_contig_count;
    } FilterContigResults;

    funcdef filter_contigs(string workspace_name, contigset_id contigset)
                returns (FilterContigResults) authentication required;

There a few things introduced here that are part of the KBase Interface Description Language (KIDL).  First, use 
`typedef` to define the structure of input/output parameters using the syntax:

    typedef [type definition] [TypeName]

The type definition can either be another previously defined type name, a primitive type (string, int, float), a 
container type (list, mapping) or a structure.  In this example we define a string named `contigset_id` and a structure 
named `FilterContigResults` with two integer fields named `contig_count` and `filtered_contig_count`.

You can use any defined types as input/output parameters to functions but it's wise to pass an parameter object. This
allows additional parameters to be supplied in future implementations without breaking back-comparability. Functions 
are defined using the `funcdef` keyword in this syntax:

    funcdef method_name(input_parameter_object_type params) returns (output_parameter_object_type output) authentication required;

All methods that run in the Narrative will require authentication because they need to interact with a user's workspace).
Your method can require authentication by adding that declaration at the end of the method.

If you will be loading or saving any data from the workspace, make sure you accept a workspace reference in the input 
parameters. (You may have noticed the example module KIDL spec uses workspace names. This gets the job done but can cause race
conditions in rare cases which is why references are preferred)

```
    typedef structure {
    	string workspace_ref;
    	...
    } <Module>Params;
```

If you will be creating a KBase report(and you almost certainly will), your method should return the report name and 
reference in it's output object as shown below:
```
    typedef structure {
        string report_name;
        string report_ref;
    } compoundset_results;
```

Additional information on KIDL objects is available in the [here](KIDL_Specification.md)

[\[Back to top\]](#top)


#### <A NAME="validate"></A>D. Validate

When you make changes to the Narrative method specifications, you can validate them for syntax locally.  From the base 
directory of your module:

    kb-sdk validate

[\[Back to top\]](#top)


#### <A NAME="stubs"></A>E. Create stubs for methods

After editing the <MyModule>.spec KIDL file, generate the Python (or other language) implementation stubs (e.g. the 
\<MyModule\>Impl.py file) by running

    make

This will call `kb-sdk compile` with a set of parameters predefined for you.

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
