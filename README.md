## KBase SDK

#### overview

A set of tools for developing new modules in KBase.  The main tool
included is the module builder, which includes tooling to help define
standard KBase service/function interface.

The module builder tooling is desinged to allow complete local testing
of long running jobs without any external registration or configuration
of the deployed job execution engine.  Implmentation of the code for
long running jobs is handled identically to implementations of server
code in the current standard KBase server generation.

Server and long running job stubs can be generated in Python, Java and
Perl.  Clients can be generated in Python, Java, JavaScript and Perl.


#### installation

The tool is accessable as a command-line program named `kb-mobu`.

To install (either standalone or within a dev_container environment):

    git clone https://github.com/kbaseIncubator/kb_sdk
    git clone https://github.com/kbase/jars
    cd module_builder
    make

If you are building in a KBase dev_container, running make will
automatically install the `kb-module-builder` command to `dev_container/bin`
which will be on your path.  If you are outside of the dev_container,
then you can add `module_builder/bin` to your path.  That is, from within
the base module_builder directory, run:

    export PATH=$(pwd)/bin:$PATH

You will need a java JDK installed in your system either way, with the 
`JAVA_HOME` environment variable defined pointing to your java installation.


#### getting started

Once the `kb-module-builder` command is on the path, you can get additional
help by running:

    kb-mobu help

Additional documentation and tutorials will slowly be added to the 'docs' 
directory of this repository as it is needed.

For an example KBase service with a Perl backend with an asynchronous, long
running job, see code and automated tests here:

  -  https://github.com/msneddon/service_test/tree/perl
  -  https://travis-ci.org/msneddon/service_test

Examples KBase services written in Python and Java will be available soon.


#### basic usage

Currently, the key new feature of the module builder that is not available
in KBase is the ability to generate an executable wrapper for long running
code (i.e. code that runs for more than ~20 seconds and should be queued
by some execution engine).  This basic usage guide will focus on this feature.

Note that this is a prototype, and some of the steps below can almost certainly
be improved, made more robust, and made to be more automated...  If you have
suggestions, please post an issue to start a conversation. 

First, the module builder operates on a interface specification file written
in KIDL (KBase Interface Description Language).  We assume you have some
basic understanding of this format and how to define types and functions.

The new KIDL feature supported by Module Builder is the ability to tag a
method as asynchronous using the new `async` keyword placed before a
function definition:

    async funcdef long_task(string input) returns (string output)
        authentication required;

Note that async methods should always require authentication because we
assume that compute resources will always have to be tracked per user.

To compile the KIDL file, there is a `compile` subcommand of `kb-module-builder`
which takes as input the KIDL file, and generates the client/server/executable
wrapper.  Run it as:

    kb-module-builder compile [MyModule.spec]

If the KIDL file is valid, you will not see any output.  If there are syntax
errors, those will be printed.  The command does not actually generate any
code until the appropriate parameters are set. Run the `kb-module-builder`
help for full parameter options, but for example, we can generate a Python
server and client created in the 'lib' directory by running this:

    kb-module-builder compile [MyModule.spec] \
        --out lib
        --pysrvname biokbase.mymodule.MyModuleServer
        --pyclname biokbase.mymodule.MyModuleClient

Previously the Server/Implementation stubs that are generated could only be
run as a Service.  The module_builder allows us to run that code directly
as a command line script in Python, Perl or as an executable jar in Java.

For instance, in a Perl example, you could directly invoke a Perl server
function from the command line as:

    perl Bio/KBase/MyModuleServer.pm [input.json] [output.json] [token]

The input file is exactly the JSON information sent to the server in a
standard KBase JSON RPC call.  The output saved to the output json file 
is the output response of that server call.  The token is the kbase user
token generated when a user is authenticated.

Right now, `kb-module-builder` does not wrap the command above as a
shell script, although it may be able to in the future depending on how
we setup our development environment.  To use this prototype for now, 
though, you should package necessary environment variables needed by
your language/script manually into a shell script. For instance, see
[this example](https://github.com/msneddon/service_test/blob/perl/Makefile#L40)

When a standard KBase server method is called on a running server, it
is handled identically to the current KBase service architecture.  In fact,
clients generated by kb-module-builder on an existing service KIDL will
be completely compatible with that existing and running KBase service.  The
difference is only when calling an async task.  In this case, the Server
code automatically forwards the request in a standard way to an Execution
Engine.  That Execution Engine then is responsible for queuing and executing
the job, and returning any results.

We have a prototype interface to the Execution Engine that has the information
necessary to submit a job in a general way.  See [KBaseJobService.spec](/KBaseJobService.spec).
The interface is something we can iterate on based on the functionality in KBase
we need.

For testing, however, the module builder includes a mock Execution Engine that
is easy to setup and run locally.  To start/stop the mock Execution Engine, build
the module_builder using `make`, which will create start/stop scripts in the
test_scripts/ee_mock_service directory.

Once the mock Execution Engine is running, you can start your server configured
to forward requests for the long running async method to the mock Execution Engine
by setting the `job-service-url` config variable in `deploy.cfg` file of your
module.  For example, see https://github.com/msneddon/service_test/blob/perl/deploy.cfg

The location of the deployment.cfg config file used by KBase services can be set
by an environment variable named KB_DEPLOYMENT_CONFIG.

Again, to see all this in action (for a Perl server for now), see: 
https://travis-ci.org/msneddon/service_test

Finally, a few brief words on the methods generated in the client code for
async methods.  Instead of generating just one client function per method,
async methods will generate 3 functions:

- **[method_name]** - a method that submits the long running job, and checks
  the state of the method until it completes, finally returning the output.
      For the client, it appears as a standard synchronous call.

- **[method_name]_async** - submits a long running job and returns a job
  id.  The job id can be used later to check the status of the job.

- **[method_name]_check** - given a job id, checks the status of the job. If
  the job is complete and has output, this method returns the output
  data marshalled into the types specified in KIDL.  This marshalling of
  output data is a key reason why this is not generated as a single method
  for all jobs, which would force the user of the client to map some generic
  output into the specific return data types by hand.


#### running tests

This code includes a test suite that builds a number of KBase services from 
KIDL specifications and test compatibility for clients in each supported 
language with the servers in each supported back-end language.

In these tests, the servers in each language are run and clients in each
language connect to those servers, so additional dependencies are required
to start/stop/run code in each of these languages.  These dependencies are
essentially the same dependencies required to run the current set of KBase
servers.  Eventually, the minimal dependencies to run a server or client
in each language should be documented here.

For now, you should probably just run tests in the dev_container environment,
or look at example KBase services with Travis CI configs to determine what you
need to install.

All that said, starting tests is relatively easy.  First edit the test config
file with a valid KBase user in [test_scripts/test.cfg](test_scripts/test.cfg),
which is necessary for testing service authentication.  Then, from the base 
module_builder directory, simply run:

    make test


#### need more?

For questions or comments, please create a github issue or pull request.

