# KBase Module Builder documentation

The KBase Module Builder is an application that helps developers initialize, compile, test, and run KBase modules. This document details how to install and use it.

# Obtain prerequisites

1. Java JDK (http://java.oracle.com)
2. ant (http://ant.apache.org/)
  - Mac OS: Homebrew or MacPorts are the easiest ways to install
  - Linux: Depends on distribution

# Get the git repos

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

# Build the SDK

    cd kb_sdk
    make

You should now have the kb-mobu program built in kb_sdk/bin. It will be helpful to add this to your execution path:

    export PATH=$(pwd)/bin:$PATH

(or some variant for your OS - consider adding this to your .profile or .bashrc)

There are a few functions built into the module builder. Let's start with init.

    kb-mobu init [-ev] [-l language] [-u username] module_name

The kb-mobu init function creates a new directory with the given module name (any whitespace is replaced with underscores) and populates it with the basic structure of a KBase module. Optionally, this can also be filled with an executable example module. The module name is expected to be a single 

Options:
-v, --verbose    Show verbose output of which files and directories are being created.
-u, --username   Give a username (presumably a github username) that will slightly adjust some files in the module.
-e, --example    Populate your repo with an example module in the language set by -l
-l, --language   Choose a programming language to base your repo on. Currently, we support Perl, Python, and Java. Default is Python

Examples:

    kb-mobu init MyModule

Creates an empty module named MyModule. This has just the bare components needed to build a repo - just the Typespec file and a bare Makefile. You'll need to fill out the Makefile with some options.

    kb-mobu init -e -l Perl MyModule

Creates a module with an example written in Perl. With the example written, you should be able to just enter the module's directory and run 'make' to build the module.

    kb-mobu init -v -e -l Perl -u my_user_name MyModule

A complete example that also tailors the module to your user name.

