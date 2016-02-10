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
 
Additionally, if you are using Mac to develop, Docker requires Mac OS X >= 10.8.

In order to follow the SDK registration instructions, you have to be an approved KBase developer.  To become an approved KBase developer, first create a standard KBase user account through http://kbase.us.  Once you have an account, please contact us with your username and we will help with the next steps.


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
- [Working with KBase Data Types](doc/kb_sdk_data_types.md)
- Wrapping an Existing Command-Line Tool
- Using Custom Reference Data 
- [kb-sdk Command Line Interface](doc/Module_builder.md) (NEEDS UPDATING)
- (Combine [Module Testing Framework](doc/testing.md) with [Debugging Your Module](doc/Docker_deployment.md))
- Managing Your Module Release Cycle
- KBase Interface Description Language (KIDL) Guide
- (Combine "Narrative Method Specification Guide" with "Visualization Widget Development Guide")
- [KBase Catalog API](https://github.com/kbase/catalog/blob/master/catalog.spec)


### <A NAME="install-sdk-dependencies"></A>1. Install SDK Dependencies

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

#### Mac OS X 10.8+ or Linux
Windows development is not currently supported.  If you are running Windows or do not want to develop on your local machine, we recommend using [VirtualBox](https://www.virtualbox.org) and installing Ubuntu 14+.

#### Java JDK 7+ 
http://www.oracle.com/technetwork/java/javase/downloads/index.html

After downloading and installing the JDK, set your `JAVA_HOME` environment variable to point to your JDK installation.  If you're not sure where that is, on a Mac the command `/usr/libexec/java_home` should tell you and on Linux `readlink -f $(which javac)` will provide the installed location of the javac, which you can use to find the base directory of the installation.  On a Mac you can set the variable like so:

    # for tcsh/csh
    setenv JAVA_HOME `/usr/libexec/java_home`  
    # for bash
    export JAVA_HOME=`/usr/libexec/java_home`

You should probably add this command to the end of your `~/.bash_profile` or ~/.bashrc file so it is always set when you start a terminal.

#### Apache Ant (is this really necessary?)
http://ant.apache.org

http://ant.apache.org/manual/install.html

The easist way to install Ant on a Mac is probably to use a package manager like [HomeBrew](http://brew.sh/), which allows to install simply by `brew install ant`.  Make sure that Ant install location is added to your PATH environment variable, which is generally handled for you if you use a package manager like HomeBrew.

#### (Mac only) Xcode
https://developer.apple.com/xcode

Xcode or the Xcode Command Line Tools will install some standard terminal commands like `make` and `git` that are necessary for building the KBase SDK and your module code.

#### git
https://git-scm.com

On a Mac this is typically already installed as part of Xcode.

#### Docker

https://www.docker.com

This is *highly* recommended for KBase module development, but not strictly required.  KBase module code is run in KBase using Docker, which allows you to easily install all system tools and dependencies your module requires.  Installing Docker locally allows you to test your build and run tests on your own computer before registering your module with KBase which significantly accellerate development.

On Linux Docker is fairly easy to install.  On a Mac the standard installer will include an installation of VirtualBox and create a VirtualBox virtual machine to run Docker.  Instructions on the Docker website are very good, but on a Mac you may need to increase your VirtualBox virtual machine disk size to handle the full KBase runtime.  This is a limitation both of the current KBase runtime which will likely be reduced in size soon, and Docker which does not yet allow configurable disk sizes on a standard Docker install, which is actually a feature on the Docker roadmap.  For now, here are some references that may help deal with this problem:

- https://github.com/kitematic/kitematic/issues/825
- http://stackoverflow.com/questions/32485723/docker-increase-disk-space
- http://stackoverflow.com/questions/30498397/how-to-customize-virtualbox-configuration-using-docker-machine

[back to top](#steps)


### <A NAME="install-and-build-sdk"></A>2. Install and Build SDK

#### Fetch the code from GitHub:

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

Some newer features are on other branches, such as develop.  If you do not need these features you do not need to check out a different branch.

    cd kb_sdk
    git checkout <branch>
    make
    
You should now have the kb-sdk program built in kb_sdk/bin. It will be helpful to add this to your execution path.  From within the kb_sdk directory, you can run in Bash:

    export PATH=$(pwd)/bin:$PATH

Optionally, you can also install command completion with:

    source src/sh/sdk-completion.sh

Like `JAVA_HOME`, you should consider adding these last two commands to your `~/.bash_profile` or `~/.bashrc` file so the SDK is always available in the terminal with command completion.

#### Test installation:

    kb-sdk help

#### Download the KBase SDK base Docker image

KBase modules run in Docker containers.  Docker containers are built on top of existing base images.  KBase has a public base image that includes a number of installed runtimes, some basic Bioinformatics tools, and other KBase specific tools.  To run this locally, you will need to download and build the KBase SDK base image.  There is a Makefile target that does most of the work for you:

    make sdkbase

The Image currently is fairly large, so this will take some time to run and build the image.  This step is required for running tests locally.

[back to top](#steps)


### <A NAME="create-module-and-methods"></A>3. Create Module

The KBase SDK provides a way to quickly bootstrap a new module by generating most of the required components.

#### Initialize

The basic options of the command are:

    kb-sdk init [-ev] [-l language] [-u username] ModuleName

e.g.

    kb-sdk init --example -l python -u <your_kbase_user_name> <user_name>ContigCount

This command will create a new module with the specified `ModuleName` configured based on the options provided.  You should always provide a username option so that the kbase.yml configuration file for your module includes you as a module owner.  The other key option is to set the programming language that you want to write your implementation in.  You can select either Python, Perl or Java.

The other **kb-sdk** options are:

    -v, --verbose    Show verbose output about which files and directories
                     are being created.
    -u, --username   Set the KBase username of the first module owner
    -e, --example    Populate your repo with an example module in the language
                     set by -l
    -l, --language   Choose a programming language to base your repo on.
                     Currently, we support Python, Python, and Java. Default is
                     Python

For this guide, let's start by creating a new module to count the number of contigs in a KBase ContigSet object.  This is the same function that is defined in the basic example generated when you run `kbase init` with the `-e` flag, so you can always generate those files and compare.  In this example, we will instead put together the various pieces by hand.

We'll write our implementation in Python, but most steps should apply for all the languages.  Let's start by generating the module directory with the `init` method.  Open a terminal and change to a working directory of your choice.  Start by running:

    kb-sdk init -u [user_name] -l python ContigCount

Module names in KBase need to be unique accross the system (for now- they will likely be namespaced by a user or organization name soon).

#### Enter your new module directory and do the initial build:

    cd <user_name>ContigCount
    make
    
[back to top](#steps)


### <A NAME="edit-module-and-methods"></A>4. Edit Module and create Method(s)

#### Update kbase.yml

Open and edit the kbase.yml file to include a better description of your module.  The default generated description isn't very good.

#### Create KIDL specification for Module

The first step is to define the interface to your code in a KIDL specification, sometimes called the "Narrative Method Spec".  This will include the parameters passed to the methods and the declaration of the methods.

Open the `ContigCount.spec` file in a text editor, and you will see this:

    /*
    A KBase module: MikeContigCount
    */
    module MikeContigCount {
        /*
        Insert your typespec information here.
        */
    };

Comments are enclosed in `/* comment */`.  In this module, we want to define a function that counts contigs, so let's define that function and its inputs/outputs as:

    typedef string contigset_id;
    typedef structure {
        int contig_count;
    } CountContigsResults;

    funcdef count_contigs(string workspace_name, contigset_id contigset)
                returns (CountContigsResults) authentication required;

There a few things introduced here that are part of the KBase Interface Description Language (KIDL).  First, we use `typedef` to define the structure of input/output parameters using the syntax:

    typedef [type definition] [TypeName]

The type definition can either be another previously defined type name, a primitive type (string, int, float), a container type (list, mapping) or a structure.  In this example we define a string named `contigset_id` and a structure named `CountContigResults` with a single integer field named `contig_count`.

We can use any defined types as input/output parameters to functions.  We define functions using the `funcdef` keyword in this syntax:

    funcdef method_name([input parameter list]]) returns ([output parameter list]);

Optionally, as we have shown in the example, your method can require authentication by adding that declaration at the end of the method.  In general, all your methods will require authentication.

#### Validate

When you make changes to the Narrative method specifications, you can validate them for syntax locally.  From the base directory of your module:

    kb-sdk validate


#### Create stubs for methods

After editing the <MyModule>.spec KIDL file, generate the Python (or other language) implementation stubs by running

    make

This will call `kb-sdk compile` with a set of parameters predefined for you.

#### Edit Impl file

In the lib/<MyModule>/ directory, edit the <MyModule>Impl.py (or *.pl) "Implementation" file that defines the methods available in the module.  You can follow this guide for interacting with [KBase Data Types](doc/kb_sdk_data_types.md).  Basically, the process consists of obtaining data objects from the KBase workspace, and either operating on them directly in code or writing them to scratch files that the tool you are wrapping will operate on.  Result data should be collected into KBase data objects and stored back in the workspace.

##### Using Data Types

In addition to taking advantage of the code snippets in the [KBase Data Types](doc/kb_sdk_data_types.md), you can also look at the [Examples](#examples) for syntax and style guidance.

##### Logging

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

##### Building output Report

##### Invoking Command Line Tool


#### Creating Narrative UI Input Widget

#### Creating a Git Repo

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

[back to top](#steps)


### <A NAME="test-module-and-methods"></A>5. Locally Test Module and Method(s)

#### Edit Dockerfile

The base KBase Docker image contains a KBase Ubuntu image, but not much else.  You will need to add whatever dependencies, including the installation of whatever tool you are wrapping, to the Dockerfile that is executed to build a custom Docker image that can run your Module.

For example:

    RUN git clone https://github.com/torognes/vsearch
    WORKDIR vsearch
    RUN ./configure 
    RUN make
    RUN make install
    WORKDIR ../

You will also need to add your KBase SDK module to the Dockerfile.  For example:

    RUN mkdir -p /kb/module/test
    WORKDIR test
    RUN git clone https://github.com/dcchivian/kb_vsearch_test_data
    WORKDIR ../

#### Build tests of your methods

Edit the local test config file (`test_local/test.cfg`) with a KBase user account name and password (note that this directory is in .gitignore so will not be copied):

    test_user = TEST_USER_NAME
    test_password = TEST_PASSWORD

Run tests:

    cd test_local
    kb-sdk test

This will build your Docker container, run the method implementation running in the Docker container that fetches example ContigSet data from the KBase CI database and generates output.

Inspect the Docker container by dropping into a bash console and poke around, from the `test_local` directory:
    
    ./run_bash.sh


[back to top](#steps)

### <A NAME="register-module"></A>6. Register Module


If you haven't already, add your repo to [GitHub](http://github.com) (or any other public git repository), from the ContigCount base directory:

    cd ContigCount
    git init
    git add .
    git commit -m 'initial commit'
    # go to github and create a new repo that is not initialized
    git remote add origin https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME].git
    git push -u origin master

Go to https://narrative-ci.kbase.us and start a new Narrative.  Search for the SDK Register Repo method, and click on it.  Enter your public git repo url (e.g. https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]) and register your repo.  Wait for the registration to complete.  Note that you must be an approved developer to register a new module.



[back to top](#steps)

### <A NAME="test-in-kbase"></A>7. Test in KBase

#### Start a Narrative Session

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
