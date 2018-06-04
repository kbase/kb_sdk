# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK

The KBase SDK is a set of tools for developing KBase Apps that can be dynamically registered and run on the KBase platform.  Apps are grouped into modules that include all code, dependencies, specification files, and documentation needed to define and run Apps in the KBase Narrative interface.

> **[SDK user documentation can be found here](http://kbase.github.io/kb_sdk_docs)**

Documentation in this readme is for developing the SDK codebase itself. If you want to develop an app using the SDK, please visit the documentation website linked above.

## Running the tests

To run the tests, first make sure [Vagrant](https://www.vagrantup.com/docs/installation/) is installed.

* Run all tests with `make test-vagrant`
* Run just the Python tests with `make test-python`
* You can SSH into the vagrant machine for debugging with `vagrant ssh`
* To run the tests directly, without vagrant, do `make test`.

Python tests should currently pass. Java (and Perl) tests are currently in progress.

To rebuild your vagrant image, run `vagrant halt` and `vagrant up` within this repo's root directory.

## Notes and references

* [Building and compiling the SDK from source](doc/building_sdk.md)
* [Codebase anatomy](doc/codebase_anatomy.md)

#### Java Versions

The codebase currently uses Java 8 and is incompatible with Java 9. Details about this incompatibility can be found here: https://blog.codefx.org/java/jsr-305-java-9/. Specifically, the "@Generated" annotation is problematic.

#### Rebuilding the VM

To rebuild the VM for running tests, follow the steps in this document: [doc/test_dependencies.md](doc/test_dependencies.md)
