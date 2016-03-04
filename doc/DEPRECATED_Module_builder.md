# KBase Module Builder documentation

The KBase Module Builder (kb-sdk) is an application that helps developers initialize, compile, test, and run KBase modules. This document details how to install and use it.

The module builder tooling is being designed to allow complete local testing of long running jobs without any external registration or configuration of the deployed job execution engine. Implementation of the code for long running jobs is handled identically to implementations of server code in the current standard KBase server generation.

Stubs for services can be generated in Python, Java and Perl. Clients can be generated in Python, Java, JavaScript and Perl.
For an example KBase service with an asynchronous, long running job, see code and automated tests here:
- Perl: https://github.com/kbaseIncubator/contigcount/tree/develop
- Python: https://github.com/kbaseIncubator/onerepotest

An example KBase service written in Java will be available soon.

# Obtain and install prerequisites

1. Mac: OS X 10.8 or higher.
 - The SDK should also work on Linux but has not yet been extensively tested. We are not yet supporting Windows.
2. Java JDK (version 7 or above): http://java.oracle.com/
 - You will also need to set the JAVA_HOME environment variable to point to your Java installation.
3. Latest version of XCode (includes make and other developer tools)
4. ant: http://ant.apache.org/
 - You will also need to add the directory with the ant binary to your PATH environment variable.
5. git (command-line version): https://git-scm.com/ (OS X users should install Xcode to get git)
6. Create a GitHub account if you do not have one already: https://github.com
 - All modules in KBase are pulled and registered from public GitHub repositories

# Set environment variables

Set JAVA_HOME to the directory where you installed JDK. If you're not sure where that is, the command `/usr/libexec/java_home` will tell you.  

    # for tcsh/csh
    setenv JAVA_HOME `/usr/libexec/java_home`  
    # for bash
    export JAVA_HOME=`/usr/libexec/java_home`

Also, be sure your PATH includes the directory that has the *ant* executable.

# Get the git repos

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

# Build the SDK

Some newer features are on other branches, such as develop.  If you do not need these features you do not need to check out a different branch.

    cd kb_sdk
    git checkout <branch>
    make

You should now have the kb-sdk program built in kb_sdk/bin. It will be helpful to add this to your execution path:

    # for bash
    export PATH=$(pwd)/bin:$PATH

(or some variant for your shell -- consider adding this to your .profile or .bashrc)

## Enable command completion (optional)
Optionally, you can install command completion with:

    source src/sh/mobu-completion.sh

# Get started
Once the kb-sdk command is in your PATH, you can get additional help by running:

    kb-sdk help
    
The module builder can generate an executable wrapper for long-running code (i.e., code that runs for more than ~20 seconds and should be queued by some execution engine). This basic usage guide will focus on this feature.

The module builder operates on an interface specification file ("spec file") written in KIDL (KBase Interface Description Language). We assume you have some basic understanding of this format and how to define types and functions. The best way to get started is to create an example module in your language of choice (Perl, Python or Java), look at its structure and contents, and structure your module similarly. The next section describes how to use `kb-sdk init` to set up your module directory, including the option to set up an example.

# kb-sdk init

There are a few functions built into the module builder. Let's start with `init`.

    kb-sdk init [-ev] [-l language] [-u username] module_name

The kb-sdk init function creates a new directory with the given module name (any whitespace is replaced with underscores) and populates it with the basic structure of a KBase module. Optionally, this can also be filled with an executable example module.

_Options:_<br/>
-v, --verbose&nbsp;&nbsp;&nbsp;&nbsp;Show verbose output about which files and directories are being created.<br/>
-u, --username&nbsp;&nbsp;&nbsp;&nbsp;Set the username (presumably a GitHub username) that will be filled in in some files in the module.<br/>
-e, --example&nbsp;&nbsp;&nbsp;&nbsp;Populate your repo with an example module in the language set by -l<br/>
-l, --language&nbsp;&nbsp;&nbsp;&nbsp;Choose a programming language to base your repo on. Currently, we support Perl, Python, and Java. Default is Python<br/>

## Examples:

    kb-sdk init MyModule

Creates an empty module named MyModule. This has just the bare components needed to build a repo - just the Typespec file and a bare Makefile. You'll need to fill out the Makefile with some options.

    kb-sdk init -e -l Perl MyModule

Creates a module with an example written in Perl. With the example written, you should be able to just enter the module's directory and run 'make' to build the module.

    kb-sdk init -v -e -l Perl -u my_user_name MyModule

Creates a complete example that also tailors the module to your user name.

# Create a GitHub repository for your module

Since functionality in KBase is pulled into KBase from public GitHub repositories, you will need to put your module code into a  GitHub repository. With git installed, this is easy to do. First you can commit your module code into a local git repository. Go into the directory where your module code is, git add all files created by kb-sdk, and commit with some commit message. This creates a git repository locally.

    cd MyModule
    git init
    git add .
    git commit -m 'initial commit'

Now you can sync this to a new GitHub repository. First create a new GitHub repository on github.com
(it can be in your personal GitHub account or in an organization, but it must be public),
but do not initialize it! Just go here to set up a new empty repository: https://github.com/new or see more
instructions here: https://help.github.com/articles/creating-a-new-repository .  You may wish to
use the name of your module as the name for your new repository.

Once the repository is set up, you can push your local KBase module to GitHub.  When you create a new
repository you can copy and paste the URL to your repository.

    git remote add origin https://github.com/[GITHUB_USER_OR_ORG_NAME]/[GITHUB_MODULE_NAME].git
    git push -u origin master

# What to edit

You will need to modify the following files in your repository in order to be able to run your code.

1. kbase.yaml.  Define metadata about your module here, such as your module name (which will be used to register with KBase), a short description, the implementation language, a version, and the KBase ids of the owners of the module (only these listed ids will also be permitted to register the module).
2. MyModule.spec. Add your funcdef definitions here, then run kb-sdk compile.
3. The implementation file for your language.  This is where you actually write the code for your defined methods.
4. The method specs.  This is where you define your narrative widgets.
5. Dockerfile.  This is used to build a Docker image.  Add any prerequisites here.

# (below here, out of date and probably won't work!) Build and run examples

To build and run the module examples, you'll need to do the following.

1. Run `make` in the new module directory

    `cd MyModule`<br/>
    `make`

2. Start the Mockup job service. This is a small web service that is a part of the SDK, and used for testing for now (note - this is under construction).  

    `cd [Path_to_kb_sdk]/test_scripts/ee_mock_service`<br/>
    `./start_service.sh`

3. Start your module's server. The Perl example is given below.  

    `cd [Path_to_MyModule]/scripts`<br/>
    `sh ./start_perl_server.sh > server.log 2>&1 &`

4. Now you can run your tests against your running server. The example test directory contains some basic client tests, but others can be added here.  

    `cd MyModule/test`<br/>
    `./test_all_clients.sh`

# kb-sdk compile

To compile a KIDL spec file, there is a `compile` subcommand of `kb-sdk` that takes as an argument the KIDL file, and generates the client/server/executable wrapper. Run it as:

    kb-sdk compile [MyModule.spec]

If the KIDL file is valid, you will not see any output. If there are syntax errors, those will be printed. The command does not actually generate any code until the appropriate parameters are set. For example, we can generate a Python server and client in the 'lib' directory by running this:

    kb-sdk compile [MyModule.spec] \
        --out lib
        --pysrvname biokbase.mymodule.MyModuleServer
        --pyclname biokbase.mymodule.MyModuleClient

Run `kb-sdk help` for full parameter options.

The module builder allows us to run server code directly as a command line script in Python or Perl or as an executable jar in Java. For instance, in a Perl example, you could directly invoke a Perl server function from the command line as:

    perl Bio/KBase/MyModuleServer.pm [input.json] [output.json] [token]

The input file is exactly the JSON information sent to the server in a standard KBase JSON RPC call. The output saved to the output JSON file is the output response of that server call. The token is the kbase user token generated when a user is authenticated.

Right now, `kb-sdk` does not wrap the command above as a shell script, although it may be able to in the future depending on how we set up our development environment. To use this prototype for now, though, you should package the necessary environment variables needed by your language/script manually into a shell script. You can build the shell script in the Makefile. For instance, see this example from https://github.com/msneddon/service_test/blob/perl/Makefile#L40:

    build-executable-script-perl:
    	mkdir -p $(LBIN_DIR)
    	echo '#!/bin/bash' > $(LBIN_DIR)/$(EXECUTABLE_SCRIPT_NAME)
    	echo 'export PERL5LIB=$(DIR)/$(LIB_DIR):$$PATH:$$PERL5LIB' >> $(LBIN_DIR)/$(EXECUTABLE_SCRIPT_NAME)
    	echo 'perl $(DIR)/$(LIB_DIR)/Bio/KBase/$(SERVICE_CAPS)/$(SERVICE_CAPS)Server.pm $$1 $$2 $$3' >>   $(LBIN_DIR)/$(EXECUTABLE_SCRIPT_NAME)
    	chmod +x $(LBIN_DIR)/$(EXECUTABLE_SCRIPT_NAME)
    	ifeq ($(TOP_DIR_NAME), dev_container)
    	  cp $(LBIN_DIR)/$(EXECUTABLE_SCRIPT_NAME) $(TOP_DIR)/bin/.
    	endif

# Synchronous vs. asynchronous methods
A new KIDL feature supported by Module Builder is the ability to tag a method as asynchronous using the new async keyword placed before a function definition:

    async funcdef long_task(string input) returns (string output)
       authentication required;

Note that async methods should always require authentication because we assume that compute resources will always have to be tracked per user.

When a standard KBase server method is called on a running server, it is handled identically to the current KBase service architecture. In fact, clients generated by kb-sdk on an existing service KIDL will be completely compatible with that existing and running KBase service. The difference is only when calling an async task. In this case, the Server code automatically forwards the request in a standard way to an Execution Engine. That Execution Engine then is responsible for queuing and executing the job, and returning any results.

We have a prototype interface to the Execution Engine that has the information necessary to submit a job in a general way--see [KBaseJobService.spec](https://github.com/kbase/kb_sdk/blob/master/KBaseJobService.spec). The interface is something we can iterate on based on the functionality in KBase we need.

For testing, however, the module builder includes a mock Execution Engine that is easy to set up and run locally. To start/stop the mock Execution Engine, build the kb-sdk using `make`, which will create start/stop scripts in the test_scripts/ee_mock_service directory.

Once the mock Execution Engine is running, you can start your server configured to forward requests for the long running async method to the mock Execution Engine by setting the job-service-url config variable in the deploy.cfg file of your module--for example,

    job-service-url = http://localhost:8000
    
The location of the deploy.cfg config file used by KBase services can be set by an environment variable named `KB_DEPLOYMENT_CONFIG`.

Again, to see all this in action (for a Perl server for now), see [https://travis-ci.org/msneddon/service_test](https://travis-ci.org/msneddon/service_test).

Finally, a few brief words on the methods generated in the client code for async methods. Instead of generating just one client function per method, async methods will generate three functions:

 - [method_name] - a method that submits the long running job and checks the state of the method until it completes, finally returning the output. For the client, it appears as a standard synchronous call.
 - [method_name]_async - submits a long running job and returns a job id. The job id can be used later to check the status of the job.
 - [method_name]_check - given a job id, checks the status of the job. If the job is complete and has output, this method returns the output data marshalled into the types specified in KIDL. This marshalling of output data is a key reason why this is not generated as a single method for all jobs, which would force the user of the client to map some generic output into the specific return data types by hand.

# Running tests
This code includes a test suite that builds a number of KBase services from KIDL specifications and tests compatibility for clients in each supported language with the servers in each supported back-end language.

In these tests, the servers in each language are run and clients in each language connect to those servers, so additional dependencies are required to start/stop/run code in each of these languages. These dependencies are essentially the same dependencies required to run the current set of KBase servers. Eventually, the minimal dependencies to run a server or client in each language should be documented here.
For now, you should probably just run tests in the dev_container environment, or look at example KBase services with Travis CI configs to determine what you need to install.
All that said, starting tests is relatively easy. First edit the test config file `test_scripts/test.cfg`, putting in a valid username, which is necessary for testing service authentication. Then, from the base kb_sdk directory, simply run:

    make test

## Local Docker deployments

You can optionally build and run a Docker image locally on your own workstation or laptop.  To do this see [Docker_deployment.md](Docker_deployment.md).
