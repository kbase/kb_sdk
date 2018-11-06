# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK

The KBase SDK is a set of tools for developing KBase Apps that can be dynamically registered and run on the KBase platform.  Apps are grouped into modules that include all code, dependencies, specification files, and documentation needed to define and run Apps in the KBase Narrative interface.

> **[SDK user documentation can be found here](http://kbase.github.io/kb_sdk_docs)**

Documentation in this readme is for developing the SDK codebase itself. If you want to develop an app using the SDK, please visit the documentation website linked above.

## Running the tests

#### Python tests

Create a virtual environment using virtualenv or conda and run `pip install -r requirements.txt`. Run the tests with `make test-python`.

## Notes and references

* [Building and compiling the SDK from source](doc/building_sdk.md)
* [Codebase anatomy](doc/codebase_anatomy.md)

#### Java Versions

The codebase currently uses Java 8 and is incompatible with Java 9. Details about this incompatibility can be found here: https://blog.codefx.org/java/jsr-305-java-9/. Specifically, the "@Generated" annotation is problematic.

#### The full build

See [doc/test_dependencies.md](doc/test_dependencies.md) for some information about how to build all the necessary dependencies to run tests.
