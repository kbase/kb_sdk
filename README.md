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
3. [Create Module and Method(s)](#create-module-and-methods)
4. [Edit Module and Method(s)](#edit-module-and-methods)
5. [Locally Test Module and Method(s)](#test-module-and-methods)
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
- Mac OS X 10.8+ (Docker requires this) or Linux
- Java JRE 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)
- At least ??? GB free on your hard drive to install Docker, Xcode, Java JRE, etc.

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

Create a directory in which you want to work.  All your work should go here.  All commands that follow are assuming you are using a UNIX shell.

    cd <working_dir>
    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

Some newer features are on other branches, such as develop.  If you do not need these features you do not need to check out a different branch.

    cd kb_sdk
    git checkout <branch>
    make bin  # or "make" to compile from scratch
    
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

#### 4A. Update kbase.yml

Open and edit the kbase.yml file to include a better description of your module.  The default generated description isn't very good.

#### 4B. Create KIDL specification for Module

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

#### 4C. Validate

When you make changes to the Narrative method specifications, you can validate them for syntax locally.  From the base directory of your module:

    kb-sdk validate


#### 4D. Create stubs for methods

After editing the <MyModule>.spec KIDL file, generate the Python (or other language) implementation stubs by running

    make

This will call `kb-sdk compile` with a set of parameters predefined for you.

#### 4E. Edit Impl file

In the lib/\<MyModule\>/ directory, edit the <MyModule>Impl.py (or *.pl) "Implementation" file that defines the methods available in the module.  You can follow this guide for interacting with [KBase Data Types](doc/kb_sdk_data_types.md).  Basically, the process consists of obtaining data objects from the KBase workspace, and either operating on them directly in code or writing them to scratch files that the tool you are wrapping will operate on.  Result data should be collected into KBase data objects and stored back in the workspace.

##### Using Data Types

Data objects are typed and structured in KBase.  You may write code that takes advantage of these structures, or extract the data from them to create files that the external tool you are wrapping requires (e.g. FASTA).  Please take advantage of the code snippets in the [KBase Data Types](doc/kb_sdk_data_types.md), you can also look at the [Examples](#examples) for syntax and style guidance.

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

##### Provenance

Data objects in KBase contain provenance (historical information of their creation and objects from which they are derived).  When you create new objects, you must carry forward and add provenance information to them.  Additionally, Report objects should receive that provenance data (see below).  Examples of adding provenance to objects can be found in the [KBase Data Types](docs/kb_sdk_data_types.md).

```python
        # load the method provenance from the context object
        provenance = [{}]
        if 'provenance' in ctx:
            provenance = ctx['provenance']
        # add additional info to provenance here, in this case the input data object reference
        provenance[0]['input_ws_objects']=[]
        provenance[0]['input_ws_objects'].append(params['workspace_name']+'/'+params['read_library_name'])
```

##### Building output Report

```python
        # create a Report
        report = ''
        report += 'ContigSet saved to: '+params['workspace_name']+'/'+params['output_contigset_name']+'\n'
        report += 'Assembled into '+str(len(contigset_data['contigs'])) + ' contigs.\n'
        report += 'Avg Length: '+str(sum(lengths)/float(len(lengths))) + ' bp.\n'

        # compute a simple contig length distribution
        bins = 10
        counts, edges = np.histogram(lengths, bins)
        report += 'Contig Length Distribution (# of contigs -- min to max basepairs):\n'
        for c in range(bins):
            report += '   '+str(counts[c]) + '\t--\t' + str(edges[c]) + ' to ' + str(edges[c+1]) + ' bp\n'

        reportObj = {
            'objects_created':[{'ref':params['workspace_name']+'/'+params['output_contigset_name'], 'description':'Assembled contigs'}],
            'text_message':report
        }

        reportName = 'megahit_report_'+str(hex(uuid.getnode()))
        report_obj_info = ws.save_objects({
                'id':info[6],
                'objects':[
                    {
                        'type':'KBaseReport.Report',
                        'data':reportObj,
                        'name':reportName,
                        'meta':{},
                        'hidden':1,
                        'provenance':provenance
                    }
                ]
            })[0]

        output = { 'report_name': reportName, 'report_ref': str(report_obj_info[6]) + '/' + str(report_obj_info[0]) + '/' + str(report_obj_info[4]) }
```

##### Invoking Command Line Tool

```python
        command_line_tool_params_str = " ".join(command_line_tool_params)
        command_line_tool_cmd_str = " ".join(command_line_tool_path, command_line_tool_params_str)
        
        # run <command_line_tool>, capture output as it happens
        self.log(console, 'running <command_line_tool>:')
        self.log(console, '    '+command_line_tool_cmd_str))
        p = subprocess.Popen(command_line_tool_cmd_str,
                    cwd = self.scratch,
                    stdout = subprocess.PIPE, 
                    stderr = subprocess.STDOUT, shell = False)

        while True:
            line = p.stdout.readline()
            if not line: break
            self.log(console, line.replace('\n', ''))

        p.stdout.close()
        p.wait()
        self.log(console, 'return code: ' + str(p.returncode))
        if p.returncode != 0:
            raise ValueError('Error running <command_line_tool>, return code: '+str(p.returncode) + 
                '\n\n'+ '\n'.join(console))
 ```

##### Adding Reference Data


#### 4F. Creating Narrative UI Input Widget

Control of Narrative interaction is accomplished in files in the ui/narrative/methods/<MyMethod> directory.

##### Creating fields in the input widget cell

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
```

##### Configure passing variables from Narrative Input to SDK method.

Edit *spec.json*:

```
{
	"ver": "1.0.0",
	
	"authors": [
		"YourName"
	],
	"contact": "help@kbase.us",
	"visible": true,
	"categories": ["active","assembly"],
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

#### 4G. Creating a Git Repo

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

#### 5A. Edit Dockerfile

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

#### 5B. Build tests of your methods

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
