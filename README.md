# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK

The KBase SDK is a set of tools for developing KBase Apps that can be dynamically registered and run on the KBase platform.  Apps are grouped into modules that include all code, dependencies, specification files, and documentation needed to define and run Apps in the KBase Narrative interface.

> **[SDK user documentation can be found here](http://kbase.github.io/kb_sdk_docs)**

Documentation in this readme is for developing the SDK codebase itself. If you want to develop an app using the SDK, please visit the documentation website linked above.

## Compiling executables

Run `make` or `make compile` to generate the binaries found in `/bin`

## Running tests

You can run the tests using a Vagrant image with `make test-vagrant`. You can enter the Vagrant VM for debugging by doing `vagrant ssh`. [Vagrant installation instructions](https://www.vagrantup.com/docs/installation/).

To run the tests directly, run `make test` in the project's root directory.

Python tests should currently pass. Java (and Perl) tests are currently in progress.

## Project anatomy

#### Root level

* `/bin` - holds the executable files generated on compilation (most importantly, `kb-sdk`).
* `/doc` - additional documentation about this codebase
* `Dockerfile` - The dockerfile for the container that runs the SDK
* `entrypoint` - the entrypoint bash script that is run for the SDK docker container
* `/img` - a collection of image templates for APP icons
* `JAR_DEPS` and `JAR_DEPS_BIN` - indexes of java jar dependencies
* `KBaseJobService.spec` - KIDL spec for a JSON RPC job service
* `Makefile` - commands for compiling, building docker images, and initializing submodules
* `Pipfile` and `Pipfile.lock` - python dependencies for pipenv
* `reports` - Files generated for JaCoCo test coverage reports.
* `sdkbase` - Docker files for the image used inside actual SDK apps
* `lib` - Some generic libraries that get included in SDK apps
* `src` - The main source code for this project. See below.
* `submodules` and `submodules_hacks` - jars and other dependencies

#### Source code in `/src/java/us/kbase`

* `/catalog` - Services for managing, registering, and building modules in the catalog. All code here is duplicated in the `njs_wrapper`.
* `/common/executionengine` - Code for executing jobs and sub-jobs. All the code here is duplicated in the `njs_wrapper` repo
* `/common/service` - Some tuple datatypes
* `/common/utils` - NetUtils for working with IP addresses and ports
* `/jkidl` - Functionality for parsing KIDL spec files
* `/kidl` - KIDL parser syntax types
* `/kbasejobservice` - Utils for handling RPC jobs
* `/mobu` - Module Builder (see below)
* `/narrativemethodstore` - JSON specifications for SDK app configurations (eg spec.json)
* `/scripts` - Various test files - See the TypeGeneratorTest.java class
* `/templates` - Template files for use in generating SDK app codebases on `kb-sdk init`
* `/tools` - Some general java utilities

#### Module builder in `src/java/us/kbase/mobu`

* `ModuleBuilder` - handles CLI commands and dispatches them to one of the below packages
* `/compiler` - parses the KIDL spec and compiles code in an SDK app
* `/initializer` - Initializes a new app, generating all templated files
* `/installer` - Installs other SDK apps as dependencies under the current one
* `/renamer` - Renames an app
* `/runner` - Runs an app in its docker container and the callback server
* `/tester` - Runs the test suite for an app
* `/util` - Generic utilities used by the module builder
* `/validator` - Validates an app using its KIDL spec, spec.json, etc

#### Miscellania

* `/src/java/name/fraser/neil/plaintext/diff_match_patch.java` - A utility computing the difference between two texts to create a patch. This is used in `src/java/us/kbase/mobu/compiler/test/html/HTMLGenTest.java`.
* `/src/sh/sdk-completion.sh` - A parameter-completion bash script for the `kb-sdk` CLI.

## Notes and references

#### Java Versions

The codebase currently uses Java 8 and is incompatible with Java 9. Details about this incompatibility can be found here: https://blog.codefx.org/java/jsr-305-java-9/. Specifically, the "@Generated" annotation is problematic.

#### Rebuilding the VM

To rebuild the VM for running tests, follow the steps in this document [doc/test_dependencies.md](doc/test_dependencies.md)
