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


### 4. Edit Module and create Method(s)

- A. [Creating a Git Repo](#create-repo)
- B. [Edit Module Description](#edit-desc)
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

The type definition can either be another previously defined type name, a primitive type (string, int, float), a container type (list, mapping) or a structure.  In this example we define a string named `contigset_id` and a structure named `FilterContigResults` with two integer fields named `contig_count` and `filtered_contig_count`.

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

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
