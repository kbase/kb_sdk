# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install SDK with Docker](kb_sdk_dockerized_install.md)
3. **Create Module**
4. [Specify Module and Method(s)](kb_sdk_edit_module.md)
5. [Implement Method(s)](kb_sdk_impl_methods.md)
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)


### 3. Create Module

The KBase SDK provides a way to quickly bootstrap a new module by generating most of the required components.

#### Initialize

The basic options of the command are:

    kb-sdk init [-ev] [-l language] [-u username] ModuleName

... where `ModuleName` must be unique across all SDK modules registered in KBase. For example:

    kb-sdk init --example -l python -u <your_kbase_user_name> <user_name>ContigFilter

This command will create a new module with the specified name `ModuleName`, configured based on the options provided.  You should always provide a username option so that the kbase.yml configuration file for your module includes you as a module owner.  The other key option is to set the programming language that you want to write your implementation in.  You can select either Python, Perl, or Java.

The other **kb-sdk** options are:

    -v, --verbose    Show verbose output about which files and directories
                     are being created.
    -u, --username   Set the KBase username of the first module owner
    -e, --example    Populate your repo with an example module in the language
                     set by -l
    -l, --language   Choose a programming language to base your repo on.
                     Currently, we support Perl, Python, and Java. Default is
                     Python

For a simple example, let's create a new module to count the number of contigs in a KBase ContigSet object.  This is the same function that is defined in the basic example generated when you run `kbase init` with the `-e` flag, so you can always generate those files and compare.  In this example, we will instead put together the various pieces by hand.

We'll write our implementation in Python, but most steps should apply for all the languages.  Let's start by generating the module directory with the `init` method.  Open a terminal and change to a working directory of your choice.  Start by running:

    kb-sdk init -u [user_name] -l python <user_name>ContigFilter

Module names in KBase need to be unique accross the system (for now--they will likely be namespaced by a user or organization name soon).

#### Enter your new module directory and do the initial build:

    cd <user_name>ContigFilter
    make
    
    
[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)

