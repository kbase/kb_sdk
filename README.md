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

## Notes and references

* [Codebase anatomy](doc/codebase_anatomy.md)
* [Building the SDK from source](doc/building_sdk.md)
* [App spec.json and display.yaml reference (PDF](doc/NarrativeUIAppSpecification.pdf)
* [SDK Advanced Features Cheat Sheet (PDF)](doc/SDK_AdvancedFeaturesCheatSheet.pdf)

#### Java Versions

The codebase currently uses Java 8 and is incompatible with Java 9. Details about this incompatibility can be found here: https://blog.codefx.org/java/jsr-305-java-9/. Specifically, the "@Generated" annotation is problematic.

#### Rebuilding the VM

To rebuild the VM for running tests, follow the steps in this document: [doc/test_dependencies.md](doc/test_dependencies.md)
