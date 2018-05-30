# Codebase Anatomy

This document describes the the file structure of the `kb_sdk` codebase.

#### Root level

* `build.xml` - main Ant build configuration file for compiling, running tests, etc.
* `bin/` - holds the executable files generated on compilation (most importantly, `kb-sdk`)
* `doc/` - additional documentation about this codebase
* `Dockerfile` - the docker configuration for the container that runs the SDK
* `entrypoint` - the entrypoint bash script that is run for the SDK docker container
* `JAR_DEPS` and `JAR_DEPS_BIN` - indexes of java jar dependencies
* `javacc/` - shell scripts used to generate code related to parsing KIDL specs
* `KBaseJobService.spec` - KIDL spec for a JSON RPC job service
* `lib` - some generic libraries that get included in SDK apps
* `Makefile` - commands for compiling, building docker images, and initializing submodules
* `Pipfile` and `Pipfile.lock` - python dependencies for pipenv
* `reports/` - files generated for JaCoCo test coverage reports
* `sdkbase/` - Docker files for the image used inside actual SDK apps
* `src/` - the main source code for this project; see below
* `submodules/` and `submodules_hacks` - jars and other dependencies
* `test_scripts/` - test helpers in perl, python, and js

#### Source code in `/src/java/us/kbase`

* `catalog/` - A client for the KBase catalog service compiled from the catalog service KIDL specification
* `common/executionengine/` - Code for executing jobs and sub-jobs. All the code here is duplicated in the `njs_wrapper` repo
* `common/service/` - Some tuple datatypes
* `common/utils/` - NetUtils for working with IP addresses and ports
* `jkidl/` - Functionality for parsing KIDL spec files
* `kidl/` - KIDL parser syntax types
* `kbasejobservice/` - Mocks used in testing the narrative job service
* `mobu/` - Module Builder (see below)
* `narrativemethodstore/` - A client for the KBase narrative method store service compiled from the NMS service KIDL specification
* `scripts/` - Various test files - See the TypeGeneratorTest.java class
* `templates/` - Template files for use in generating SDK app codebases on `kb-sdk init`
* `tools/` - Some general java utilities

#### Module builder in `src/java/us/kbase/mobu`

* `ModuleBuilder` - handles CLI commands and dispatches them to one of the below packages
* `compiler/` - parses the KIDL spec and compiles code in an SDK app
* `initializer/` - Initializes a new app, generating all templated files
* `installer/` - Installs other SDK apps as dependencies under the current one
* `renamer/` - Renames an app
* `runner/` - Runs an app in its docker container and the callback server
* `tester/` - Runs the test suite for an app
* `util/` - Generic utilities used by the module builder
* `validator/` - Validates an app using its KIDL spec, spec.json, etc

#### Miscellania

* `src/java/name/fraser/neil/plaintext/diff_match_patch.java` - A utility computing the difference between two texts to create a patch. This is used in `src/java/us/kbase/mobu/compiler/test/html/HTMLGenTest.java`.
* `src/sh/sdk-completion.sh` - A parameter-completion bash script for the `kb-sdk` CLI.
