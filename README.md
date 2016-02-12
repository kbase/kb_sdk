# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK

**This software is still in beta and should only be used for internal KBase development.**

The KBase SDK is a set of tools for developing new modules that can be dynamically registered and run on the KBase platform.  Modules include all code, specification files, and documentation needed to define and run a set of methods in the KBase Narrative interface.

There are a number of restrictions on module functionality that will gradually be lifted as the SDK and KBase platform are refined.  The current set of restrictions are:

- Runs on a standard KBase worker node (at least 2 cores and 22GB memory)
- Operates only on supported KBase data types
- Requires either no or fairly limited amounts of reference data
- Uses existing data visualization widgets
- Does not require new uploaders/downloaders
- Wrapper written in Python, Java, or Perl
 
In order to register your SDK module, you have to be an approved KBase developer.  To become an approved KBase developer, first create a standard KBase user account through http://kbase.us.  Once you have an account, please contact us with your username and we will help with the next steps.


## <A NAME="steps"></A>Steps in Using SDK
1. [Install SDK Dependencies](#install-sdk-dependencies)
2. [Install and Build SDK](#install-and-build-sdk)
3. [Create Module](#create-module)
4. [Edit Module and Method(s)](#edit-module-and-methods)
5. [Locally Test Module and Method(s)](#test-module-and-methods)
6. [Register Module](#register-module)
7. [Test in KBase](#test-in-kbase)
8. [Complete Module Info](#complete-module-info)
9. [Deploy](#deploy)

### Additional Documentation
- [FAQ](doc/FAQ.md)
- Troubleshooting
- [Examples](#examples)
- KBase Developer Policies
- KBase SDK Coding Style Guide and Best Practices
- [Anatomy of a KBase Module](doc/module_overview.md)
- [KBase Data Types Table](#doc/kb_sdk_data_types_table.md)
- [Working with KBase Data Types](doc/kb_sdk_data_types.md)
- Wrapping an Existing Command-Line Tool
- Using Custom Reference Data 
- [kb-sdk Command Line Interface](doc/Module_builder.md) (NEEDS UPDATING)
- (Combine [Module Testing Framework](doc/testing.md) with [Debugging Your Module](doc/Docker_deployment.md))
- Managing Your Module Release Cycle
- KBase Interface Description Language (KIDL) Guide
- Visualization Widget Development Guide
- [KBase Catalog API](https://github.com/kbase/catalog/blob/master/catalog.spec)


## Quick Start

### <A NAME="install-sdk-dependencies"></A>1. Install SDK Dependencies

More Detail on [Install SDK Dependencies](doc/kb_sdk_dependencies.md)

System Dependencies:
- Mac OS X 10.8+ (Docker requires this) or Linux
- Java JRE 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)
- At least ??? GB free on your hard drive to install Docker, Xcode, Java JRE, etc.

[back to top](#steps)


### <A NAME="install-and-build-sdk"></A>2. Install and Build SDK

More Detail on [Install and Build SDK](doc/kb_sdk_install_and_build.md)

Create a directory in which you want to work.  All your work should go here.

##### Fetch the code from GitHub:

```
    cd <working_dir>
    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars
```

##### Some newer features are on other branches, such as *develop* (currently please do use the *develop* branch).

```
    cd kb_sdk
    git checkout <branch>
    make bin  # or "make" to compile from scratch

    export PATH=$(pwd)/bin:$PATH
    source src/sh/sdk-completion.sh
```

##### Test installation:

    kb-sdk help

##### Download the KBase SDK base Docker image

    make sdkbase

[back to top](#steps)


### <A NAME="create-module"></A>3. Create Module

More Detail on [Create Module](doc/kb_sdk_create_module.md)

The KBase SDK provides a way to quickly bootstrap a new module by generating most of the required components.

##### Initialize module:

    kb-sdk init --example -l python -u <your_kbase_user_name> <MyModule>

##### Enter your new module directory and do the initial build:

    cd <MyModule>
    make
    
[back to top](#steps)


### <A NAME="edit-module-and-methods"></A>4. Edit Module and create Method(s)

More Detail on [Edit Module and Create Methods](doc/kb_sdk_edit_module.md)

##### 4A) Add a description of your Module

```
    edit kbase.yaml
```

##### 4B) Create KIDL specification for Module

```
    edit <MyModule>.spec
```

e.g.

```
	typedef structure {
		string workspace_name;
		string read_library_name;
		string output_contigset_name;
		
		int min_count;
	} MegaHitParams;
	
	typedef structure {
		string report_name;
		string report_ref;
	} MegaHitOutput;
	
	funcdef run_megahit(MegaHitParams params) returns(MegaHitOutput output)
		authentication required;
```

##### 4C) Validate that KIDL file is syntactically correct

    kb-sdk validate


##### 4D) Create stubs for methods

After editing the <MyModule>.spec KIDL file, generate the Python (or other language) implementation stubs by running

    make

This will call `kb-sdk compile` with a set of parameters predefined for you.


##### 4E) Edit Impl file

```
    cd lib/<MyModule>/
    edit <MyMethod>Impl.py
```

###### 4E.1) Using Data Types

Follow guidance and code snippets for using [KBase Data Types](doc/kb_sdk_data_types.md).

###### 4E.2) Logging

Please use logging to track progress and aid debugging your code.  See More Detail on [Edit Module and Create Methods](doc/kb_sdk_edit_module.md).


###### 4E.3) Provenance

We track historical information for Data objects using provenance.  See More Detail on [Edit Module and Create Methods](doc/kb_sdk_edit_module.md).


###### 4E.4) Building output Report

We use a Report Data object type to return results from SDK methods.  See More Detail on [Edit Module and Create Methods](doc/kb_sdk_edit_module.md).


##### 4F) Creating Narrative UI Input Widget

Control of Narrative interaction is accomplished in files in the ui/narrative/methods/<MyMethod> directory.  See More Detail on [Edit Module and Create Methods](doc/kb_sdk_edit_module.md).


##### 4G) Creating a Git Repo

You will need to check your SDK Module into Git in order for it to be available for building into a custom Docker Image.  Register for a git account and set that as your username.

    cd <MyModule>
    git init
    git add .
    git commit -m 'initial commit'
    git remote add origin https://github.com/<GITHUB_USER_OR_ORG_NAME>/<MyModule>.git
    git push -u origin master

*Remember to update your code in the Git Repo as you change it via "git commit / pull / push" cycles.*

[back to top](#steps)


### <A NAME="test-module-and-methods"></A>5. Locally Test Module and Method(s)

More Detail on [Locally Test Module and Methods](doc/kb_sdk_local_test_module.md)

##### Edit Dockerfile

Add whatever dependencies and installation commands for the tool you are wrapping.

    edit Dockerfile

##### Build tests of your methods

Edit the local test config file (`test_local/test.cfg`) with a KBase user account name and password (note that test_local is in .gitignore so will not be copied):

    test_user = TEST_USER_NAME
    test_password = TEST_PASSWORD

Create and Run tests:

    cd test
    edit <MyModule>_server_test.py
    kb-sdk test

[back to top](#steps)


### <A NAME="register-module"></A>6. Register Module

More Detail on [Register Module](doc/kb_sdk_register_module.md)

#### 6A. Create Git Repo

If you haven't already, add your repo to [GitHub](http://github.com) (or any other public git repository), from the ContigCount base directory:

    cd ContigCount
    git init
    git add .
    git commit -m 'initial commit'
    # go to github and create a new repo that is not initialized
    git remote add origin https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME].git
    git push -u origin master


#### 6B. Register with KBase

Go to

    https://narrative-ci.kbase.us

and start a new Narrative.  Search for the SDK Register Repo method, and click on it.  Enter your public git repo url

e.g.

    https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]
    
and register your repo.  Wait for the registration to complete.  Note that you must be an approved developer to register a new module.


[back to top](#steps)


### <A NAME="test-in-kbase"></A>7. Test in KBase

#### 7A. Start a Narrative Session

Go to https://narrative-ci.kbase.us and start a new Narrative.

Click on the 'R' in the method panel list until it switches to 'D' for methods still in development.  Find your new method by searching for your module, and run it to count some contigs.

Explore the other SDK methods in the Narrative method panel.  For finer-grain control of the KBase Catalog registration process, use a code cell:

    from biokbase.catalog.Client import Catalog
    catalog = Catalog(url="https://ci.kbase.us/services/catalog")
    catalog.version()

The KBase Catalog API is defined here: https://github.com/kbase/catalog/blob/master/catalog.spec

[back to top](#steps)


### <A NAME="complete-module-info"></A>8. Complete Module Info

Icons, Publications, Original tool authors, Institutional Affiliations, Contact Information, and most importantly, Method Documentation must be added to your module before it can be deployed.  This information will show up in the App Catalog Browser.

#### 8A. Adding an Icon

#### 8B. Adding Text Info


[back to top](#steps)


### <A NAME="deploy"></A>9. Deploy

Please email us when you think your module is ready for public use.


### <A NAME="examples"></A>Example Modules

There are a number of modules that we continually update and modify to demonstrate best practices in code and documentation and present working examples of how to interact with the KBase API and data models.

 - [MegaHit](https://github.com/msneddon/kb_megahit) (Python) - assembles short metagenomic read data (Read Data -> Contigs)
 - [Trimmomatic](https://github.com/psdehal/Trimmomatic) (Python) - filters/trims short read data (Read Data -> Read Data)
 - [OrthoMCL](https://github.com/rsutormin/PangenomeOrthomcl) (Python) - identifies orthologous groups of protein sequences from a set of genomes (Annotated Genomes / GenomeSet -> Pangenome)
 - [ContigFilter](https://github.com/msneddon/ContigFilter) (Python) - filters contigs based on length (ContigSet -> ContigSet)


### Need more?

If you have questions or comments, please create a GitHub issue or pull request, or contact us through http://kbase.us
