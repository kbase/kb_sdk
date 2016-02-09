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


## <A NAME="steps"></A>Steps in Using SDK
1. [Install SDK Dependencies](#install-sdk-dependencies)
2. [Install and Build SDK](#install-and-build-sdk)
3. [Create Module and Method(s)](#create-module-and-method)
4. [Edit Module and Method(s)](#edit-module-and-method)
5. [Locally Test Module and Method(s)](#test-module-and-method)
6. [Register Module](#register-module)
7. [Test in KBase](#test-in-kbase)
8. [Complete Module Info](#complete-module-info)
9. [Deploy](#deploy)

### Additional Documentation
- FAQ
- Troubleshooting
- [Examples](#examples)
- KBase Developer Policies
- KBase SDK Coding Style Guide and Best Practices
- [Anatomy of a KBase Module](doc/module_overview.md)
- Working with KBase Data Types
- Wrapping an Existing Command-Line Tool
- Using Custom Reference Data 
- [kb-sdk Command Line Interface](doc/Module_builder.md) (NEEDS UPDATING)
- KBase Interface Definition Language (KIDL)
- (Combine [Module Testing Framework](doc/testing.md) with [Debugging Your Module](doc/Docker_deployment.md))
- Managing Your Module Release Cycle
- KBase Interface Description Language (KIDL) Guide
- (Combine "Narrative Method Specification Guide" with "Visualization Widget Development Guide")
- [KBase Catalog API](#https://github.com/kbase/catalog/blob/master/catalog.spec)

BRING INTO TOP-LEVEL DOC
- [Full Installation Guide](doc/installation.md)
- [Building and Registering Your First Module](doc/getting_started.md)


#### <A NAME="install-sdk-dependencies"></A>1. Install SDK Dependencies

System Dependencies:
- Mac OS X 10.8+ or Linux
- Java JRE 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)

(If you plan to build from source)
- Java JDK 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- JAVA_HOME environment variable set to JDK installation path
- Apache Ant http://ant.apache.org

[back to top](#steps)

#### <A NAME="install-and-build-sdk"></A>2. Install and Build SDK

Get the SDK:

    git clone https://github.com/kbase/kb_sdk

Pull dependencies and configure the SDK:

    cd kb_sdk
    make bin  # or just 'make' if you are building from source

Download the local KBase SDK base Docker image:

    make sdkbase 

Add the kb-sdk tool to your PATH and enable command completion.  From the kb_sdk directory:

    # for bash
    export PATH=$(pwd)/bin:$PATH
    source src/sh/sdk-completion.sh

Test installation:

    kb-sdk help

[back to top](#steps)

#### <A NAME="create-module-and-methods"></A>3. Create Module and Method(s)

Initialize a new module populated with the ContigCount example (module names need to be unique in KBase, so you should pick a different name):

    kb-sdk init --example -l python -u [your_kbase_user_name] ContigCount

Enter your new module directory and do the initial build:

    cd ContigCount
    make

[back to top](#steps)

#### <A NAME="edit-module-and-methods"></A>4. Edit Module and Method(s)

[back to top](#steps)

#### <A NAME="test-module-and-methods"></A>5. Locally Test Module and Method(s)

Edit the local test config file (`test_local/test.cfg`) with a KBase user account name and password (note that this directory is in .gitignore so will not be copied):

    test_user = TEST_USER_NAME
    test_password = TEST_PASSWORD

Run tests:

    cd test_local
    kb-sdk test

This will build your Docker container, run the method implementation running in the Docker container that fetches example ContigSet data from the KBase CI database and generates output.

Inspect the Docker container by dropping into a bash console and poke around, from the `test_local` directory:
    
    ./run_bash.sh

When you make changes to the Narrative method specifications, you can validate them for syntax locally.  From the base directory of your module:

    kb-sdk validate

[back to top](#steps)

#### <A NAME="register-module"></A>6. Register Module


Add your repo to [GitHub](http://github.com) (or any other public git repository), from the ContigCount base directory:

    cd ContigCount
    git init
    git add .
    git commit -m 'initial commit'
    # go to github and create a new repo that is not initialized
    git remote add origin https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME].git
    git push -u origin master

Go to https://narrative-ci.kbase.us and start a new Narrative.  Search for the SDK Register Repo method, and click on it.  Enter your public git repo url (e.g. https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]) and register your repo.  Wait for the registration to complete.  Note that you must be an approved developer to register a new module.



[back to top](#steps)

#### <A NAME="test-in-kbase"></A>7. Test in KBase

Click on the 'R' in the method panel list until it switches to 'D' for methods still in development.  Find your new method by searching for your module, and run it to count some contigs.

Explore the other SDK methods in the Narrative method panel.  For finer-grain control of the KBase Catalog registration process, use a code cell:

    from biokbase.catalog.Client import Catalog
    catalog = Catalog(url="https://ci.kbase.us/services/catalog")
    catalog.version()

The KBase Catalog API is defined here: https://github.com/kbase/catalog/blob/master/catalog.spec

[back to top](#steps)

#### <A NAME="complete-module-info"></A>8. Complete Module Info

[back to top](#steps)

#### <A NAME="deploy"></A>9. Deploy


### <A NAME="examples"></A>Example Modules

There are a number of modules that we continually update and modify to demonstrate best practices in code and documentation and present working examples of how to interact with the KBase API and data models.

 - [MegaHit](https://github.com/msneddon/kb_megahit) (Python) - assembles short metagenomic read data (Read Data -> Contigs)
 - [Trimmomatic](https://github.com/psdehal/Trimmomatic) (Python) - filters/trims short read data (Read Data -> Read Data)
 - [OrthoMCL](https://github.com/rsutormin/PangenomeOrthomcl) (Python) - identifies orthologous groups of protein sequences from a set of genomes (Annotated Genomes / GenomeSet -> Pangenome)
 - [ContigFilter](https://github.com/msneddon/ContigFilter) (Python) - filters contigs based on length (ContigSet -> ContigSet)


### Need more?

If you have questions or comments, please create a GitHub issue or pull request, or contact us through http://kbase.us
