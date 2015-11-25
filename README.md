# KBase SDK

**This software is still in beta and should only be used for internal KBase development.**

The KBase SDK is a set of tools for developing new modules that can be dynamically registered and run on the KBase platform.  Modules include all code, specification files, and documentation needed to define and run a set of methods in the KBase Narrative interface.

There are a number of restrictions on module functionality that will gradually be lifted as the SDK and KBase platform are refined.  The current set of restrictions are:

- Runs on a standard KBase worker node (at least 2 cores and 22GB memory)
- Operates only on supported KBase data types
- Requires either no or fairly limited amounts of reference data
- Uses existing data visualization widgets
- Does not require new uploaders/downloaders
- Wrapper written in Python, Java, or Perl


For more details on developing modules for KBase see:

- KBase Developer Policies
- [Full Installation Guide](doc/installation.md)
- [Anatomy of a KBase Module](doc/module_overview.md)
- [Building and Registering Your First Module](doc/getting_started.md)
- [Old Module Builder CLI docs](doc/Module_builder.md) - still has good information, but getting merged into the Building your first module guide above
- Wrapping an Existing Command-Line Tool
- [Module Testing Framework](doc/testing.md)
- [Debugging Your Module](doc/Docker_deployment.md)
- KBase SDK Coding Style Guide and Best Practices
- Managing Your Module Release Cycle
- Working with KBase Data Types
- KBase Interface Description Language (KIDL) Guide
- Narrative Method Specification Guide
- (Visualization Widget Development Guide)
- SDK command-line interface quick reference
- FAQ


### Quick Install Guide

Below is a quick reference guide for installation.  For more complete details and troubleshooting, see the [Full Installation Guide](doc/installation.md).

System Dependencies:

- Mac OS X 10.8+ or Linux
- Java JDK 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- JAVA_HOME environment variable set to JDK installation path
- Apache Ant http://ant.apache.org
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com

Get the SDK and KBase Jar file dependencies:

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

Build the SDK tool from source:

    cd kb_sdk
    make

Download the local KBase SDK base Docker image:

    make sdkbase 

Add the kb-sdk tool to your PATH and enable command completion.  From the kb_sdk directory:

    # for bash
    export PATH=$(pwd)/bin:$PATH
    source src/sh/sdk-completion.sh

Test installation:

    kb-sdk help


### Quick Start Guide

Initialize a new module populated with the ContigCount example (module names need to be unique in KBase, so you should pick a different name):

    kb-sdk init --example -l python -u [your_kbase_user_name] ContigCount

Enter your new module directory and do the initial build:

    cd ContigCount
    make

Edit the local test config file (`test_local/test.cfg`) with a KBase user account name and password:

    test_user = TEST_USER_NAME
    test_password = TEST_PASSWORD

Run tests:

    cd test_local
    kb-sdk test

This will build your Docker container, run the method implementation running in the Docker container that fetches example ContigSet data from the KBase CI database and generates output.

Inspect the Docker container by dropping into a bash console and poke around, from the `test_local` directory:
    
    ./run_bash.sh

Add your repo to [GitHub](http://github.com) (or any other public git repository), from the ContigCount base directory:

    cd ContigCount
    git init
    git add .
    git commit -m 'initial commit'
    # go to github and create a new repo that is not initialized
    git remote add origin https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME].git
    git push -u origin master

Go to https://narrative-ci.kbase.us and start a new Narrative.  Search for the SDK Register Repo method, and click on it.  Enter your public git repo url (e.g. https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]) and register your repo.  Wait for the registration to complete.  Note that you must be an approved developer to register a new module.

Click on the 'R' in the method panel list until it switches to 'D' for methods still in development.  Find your new method by searching for your module, and run it to count some contigs.

Explore the other SDK methods in the Narrative method panel.  For finer-grain control of the KBase Catalog registration process, use a code cell:

    from biokbase.catalog.Client import Catalog
    catalog = Catalog(url="https://ci.kbase.us/services/catalog")
    catalog.version()

The KBase Catalog API is defined here: https://github.com/kbase/catalog/blob/master/catalog.spec


#### Example Modules

There are a number of modules that we continually update and modify to demonstrate best practices in code and documentation and present working examples of how to interact with the KBase API and data models.

 - [MegaHit](https://github.com/msneddon/kb_megahit) (Python) - assembles short metagenomic read data (Read Data -> Contigs)
 - [Trimmomatic](https://github.com/psdehal/Trimmomatic) (Python) - filters/trims short read data (Read Data -> Read Data)
 - [OrthoMCL](https://github.com/rsutormin/PangenomeOrthomcl) (Python) - identifies orthologous groups of protein sequences from a set of genomes (Annotated Genomes / GenomeSet -> Pangenome)



#### Need more?

If you have questions or comments, please create a GitHub issue or pull request, or contact us through http://kbase.us
