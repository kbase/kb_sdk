## module builder

#### overview

A prototype set of tools for building a KBase module that includes both
a standard KBase service interface, which optionally includes methods
for submitting long running jobs to a job execution engine.

The module builder tooling is desinged to allow complete local testing
of long running jobs without any external registration or configuration
of the deployed job execution engine.  Implmentation of the code for
long running jobs is handled identically to implementations of server
code in the current standard KBase server generation.

Server and long running job stubs can be generated in Python, Java and
Perl.  Clients can be generated in Python, Java, JavaScript and Perl.


#### installation

The tool is accessable as a command-line program named kb-module-builder.

To install (either standalone or within a dev_container environment):

    git clone https://github.com/kbaseIncubator/module_builder
    git clone https://github.com/kbase/jars
    cd module_builder
    make

If you are building in a KBase dev_container, running make will
automatically install the kb-module-builder command to dev_container/bin
which will be on your path.  If you are outside of the dev_container,
then you can module_builder/bin to your path.  That is, from within
the base module_builder directory, run:

    export PATH=$(pwd)/bin:$PATH

You will need a java JDK installed in your system either way, with the 
JAVA_HOME environment variable defined pointing to your java installation.


#### getting started

Once the kb-module-builder command is on the path, you can get additional
help by running:

    kb-module-builder help

Additional documentation and tutorials will slowly be added to the 'docs' 
directory of this repository as it is needed.

For an example KBase service with a Perl backend with an asynchronous, long
running job, see code and automated tests here:

  -  https://github.com/msneddon/service_test/tree/perl
  -  https://travis-ci.org/msneddon/service_test

Examples KBase services written in Python and Java will be available soon.


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

