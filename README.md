# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK

The KBase SDK is a set of tools for developing KBase Apps that can be dynamically registered and run on the KBase platform.  Apps are grouped into modules that include all code, dependencies, specification files, and documentation needed to define and run Apps in the KBase Narrative interface.  By using [Docker](https://www.docker.com) combined with the KBase App Catalog, you can build and run a new "Hello World!" App in KBase in minutes.

There are still some general restrictions on functionality that will gradually be lifted as the SDK and KBase platform are refined.  The current restrictions are:

- Runs completely on a standard KBase worker node (at least 2 cores and 22GB memory)
- Operates only on supported KBase data types
- Uses existing data visualization widgets
- Does not require new uploaders/downloaders
- Wrapper written in Python, Java, R, or Perl

In addition to these technical restrictions, tools in KBase are subject to a [review process](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md) prior to public release. If you have a tool you would like to register with KBase that cannot meet these requirements, please <a href="http://kbase.us/contact-us">contact us</a> to discuss possible solutions.

You can use the SDK to develop and test Apps with a regular KBase user account (and a Git repository).
When you'd like us to deploy your App publicly in KBase, fill out the <a href="https://accounts.kbase.us/index.php?tpl=request_identity.tpl">developer account request form</a>.
Once your account is approved, <a href="http://kbase.us/contact-us/">contact us</a> with your username and request to be added to the developer list.


## <A NAME="steps"></A>Steps in Using SDK
1. [Install SDK Dependencies](doc/kb_sdk_dependencies.md)
2. [Install SDK with Docker](doc/kb_sdk_dockerized_install.md)
3. [Create Module](doc/kb_sdk_create_module.md)
4. [Specify Module and Method(s)](doc/kb_sdk_edit_module.md)
5. [Implement Method(s)](doc/kb_sdk_impl_methods.md)
6. [Specify User Interface](doc/kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](doc/kb_sdk_local_test_module.md)
8. [Register Module](doc/kb_sdk_register_module.md)
9. [Test in KBase](doc/kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kdoc/b_sdk_complete_module_info.md)
11. [Deploy](doc/kb_sdk_deploy.md)

### Additional Documentation
- [Examples](#examples)
- [FAQ](doc/FAQ.md)
- [Troubleshooting](doc/kb_sdk_troubleshooting.md)
- [KBase Developer Policies](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md)
- [Anatomy of a KBase Module](doc/module_overview.md)
- [KBase Catalog API](https://github.com/kbase/catalog/blob/master/catalog.spec)
- [KBase Data Types](https://narrative.kbase.us/#catalog/datatypes)
- [KIDL Specification](doc/KIDL_specification.md)
- [KBase DataFileUtil SDK module](https://github.com/realmarcin/DataFileUtil/blob/master/README.md)
- [Narrative UI Specification](doc/NarrativeUIAppSpecification.pdf)


## Quick Install Guide

Below is a quick reference guide for standard installation.  For more complete details and troubleshooting, see the [Full Installation Guide](doc/kb_sdk_dependencies.md).

For the adventurous, there is an experimental Dockerized installation that only requires Docker.  It is known to work on at least Mac and Linux, but has not been extensively tested yet.  For details, see [Dockerized Installation](doc/kb_sdk_dockerized_install.md)

#### Installation Only

System Dependencies:

- Mac OS X 10.8+ or Linux. kb-sdk does not run natively in Windows, but see [here](doc/FAQ.md#windows) for more details.
- Java JRE 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)

Get the SDK:

    git clone https://github.com/kbase/kb_sdk

Pull dependencies and configure the SDK:

    cd kb_sdk
    make bin

Download the local KBase SDK base Docker image:

    make sdkbase 

Add the kb-sdk tool to your PATH and enable command completion.  From the kb_sdk directory:

    # for bash
    export PATH=$(pwd)/bin:$PATH
    source src/sh/sdk-completion.sh

Test installation:

    kb-sdk help


#### Build from source

Additional System Dependencies:

- Java JDK 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- JAVA_HOME environment variable set to JDK installation path
- Apache Ant http://ant.apache.org

Follow basic instructions above.  Instead of running `make bin` you can run `make` to compile the SDK:

    cd kb_sdk
    make


## Quick Start Guide

Initialize a new module populated with the ContigFilter example (module names need to be unique in KBase, so you should pick a different name):

    kb-sdk init --example -l python -u [your_kbase_user_name] MyContigFilter

Enter your new module directory and do the initial build:

    cd MyContigFilter
    make

Edit the local test config file (`test_local/test.cfg`) with a KBase developer token:

    test_token = TEST_TOKEN

Run tests:

    cd test_local
    kb-sdk test

This will build your Docker container, run the method implementation running in the Docker container that fetches example ContigSet data from the KBase CI database and generates output.

Inspect the Docker container by dropping into a bash console and poke around, from the `test_local` directory:
    
    ./run_bash.sh

When you make changes to the Narrative method specifications, you can validate them for syntax locally.  From the base directory of your module:

    kb-sdk validate

Add your repo to [GitHub](http://github.com) (or any other public git repository), from the MyContigFilter base directory:

    cd MyContigFilter
    git init
    git add .
    git commit -m 'initial commit'
    # go to github and create a new repo that is not initialized
    git remote add origin https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME].git
    git push -u origin master

Now go to https://appdev.kbase.us/#appcatalog/register.  Enter your public git repo url (e.g. https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]) and submit.  Wait for the registration to complete.  Note that you must be an approved developer to register a new module.

Your app is now available in the AppDev environment in KBase. Go to https://appdev.kbase.us, sign in and start a new Narrative.  Click on the small 'R' in the upper right of the Apps panel until it switches to 'D' to show Apps that are still in development.  Find your new App by searching for your module, and run it to filter some contigs.

Your App will now also be visible in the App Catalog when displaying Apps in development: https://appdev.kbase.us/#appcatalog/browse/dev and https://narrative.kbase.us/#appcatalog/browse/dev.  From your module page (e.g., https://narrative.kbase.us/#appcatalog/module/[MODULE_NAME]) you'll be able to register any update and manage release of your module to the production KBase environment for anyone to use.

Now, dive into [Making your own Module](doc/kb_sdk_dependencies.md).


## <A NAME="examples"></A>Example Modules

There are a number of modules that we continually update and modify to demonstrate best practices in code and documentation and present working examples of how to interact with the KBase API and data models.

 - [MegaHit](https://github.com/msneddon/kb_megahit) (Python) - assembles short metagenomic read data (Read Data -> Contigs)
 - [Trimmomatic](https://github.com/psdehal/Trimmomatic) (Python) - filters/trims short read data (Read Data -> Read Data)
 - [OrthoMCL](https://github.com/rsutormin/PangenomeOrthomcl) (Python) - identifies orthologous groups of protein sequences from a set of genomes (Annotated Genomes / GenomeSet -> Pangenome)
 - [ContigFilter](https://github.com/msneddon/ContigFilter) (Python) - filters contigs based on length (ContigSet -> ContigSet)


## Need more?

Browse through the [doc](doc/) directory of this repo for the latest available documentation.  If you still have questions or comments, please create a GitHub issue or pull request, or contact us through http://kbase.us/contact-us
