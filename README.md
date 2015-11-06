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
- [Building and Registering Your First Module](doc/Module_builder.md)
- Wrapping an Existing Command-Line Tool
- [Debugging Your Module](doc/Docker_deployment.md)
- Module Testing Framework
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

Add the kb-sdk tool to your PATH and enable command completion.  From the kb_sdk directory:

    # for bash
    export PATH=$(pwd)/bin:$PATH
    source src/sh/kb-sdk-completion.sh

Test installation:

    kb-sdk help


#### Need more?

If you have questions or comments, please create a GitHub issue or pull request, or contact us through http://kbase.us
