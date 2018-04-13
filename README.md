# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK

The KBase SDK is a set of tools for developing KBase Apps that can be dynamically registered and run on the KBase platform.  Apps are grouped into modules that include all code, dependencies, specification files, and documentation needed to define and run Apps in the KBase Narrative interface.  By using [Docker](https://www.docker.com) combined with the KBase App Catalog, you can build and run a new "Hello World!" App in KBase in minutes.

There are still some general restrictions that will gradually be lifted as the SDK and KBase platform are refined.  The current requirements for KBase apps are:
  		  
- Licensed under one of the OSI-approved licenses listed at: https://opensource.org/licenses
- Runs completely on a standard KBase worker node (at least 2 cores and 22GB memory)
- Operates only on supported KBase data types
- Uses existing data visualization widgets
- Does not require new uploaders/downloaders
- Wrapper written in Python, Java, or Perl

In addition to these technical restrictions, tools in KBase are subject to a [review process](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md) prior to public release. If you have a tool you would like to register with KBase that cannot meet these requirements, please <a href="http://kbase.us/contact-us">contact us</a> to discuss possible solutions.

If you want to develop apps using the SDK, please obtain a free [KBase user account](http://kbase.us/sign-up-for-a-kbase-account/) and then apply for a KBase developer account by going to https://accounts.kbase.us/index.php?tpl=request_identity.tpl.
If you are a US citizen, your account should be created within a few days. For foreign nationals, it may take several weeks (and, in a few cases, you may not be able to get a developer account). Non-US citizens will be asked for additional information in order to process their application.
Once your account is approved, contact us with your username and ask to be added to the developer list.


## <A NAME="steps"></A>SDK Step-by-step Tutorial
0. [Overview and Concepts](doc/tutorial/overview.md)
1. [Install SDK Dependencies](doc/tutorial/dependencies.md)
2. [Install SDK with Docker](doc/tutorial/dockerized_install.md)
3. [Create Module](doc/tutorial/create_module.md)
4. [Specify Module and Method(s)](doc/tutorial/edit_module.md)
5. [Implement Method(s)](doc/tutorial/impl_methods.md)
6. [Publish and Test on Appdev](doc/tutorial/publish.md)

### How-to articles
* [How to add UI elements](doc/howto/add_ui_elements.md)
* [How to create reports](doc/howto/create_a_report.md)
* [How to edit your Dockerfile](doc/howto/edit_your_dockerfile.md)
* [How to complete all your app's information and metadata](doc/howto/fill_out_app_information.md)
* [How to build the SDK manually](doc/howto/manual_build.md)
* [How to run a shell command in your app](doc/howto/run_a_shell_command.md)
* [How to work with reference data](doc/howto/work_with_reference_data.md)

### Additional Documentation
- [Examples](#examples)
- [FAQ](doc/FAQ.md)
- [Troubleshooting](doc/kb_sdk_troubleshooting.md)
- [Building SDK](doc/building_sdk.md)
- [KBase Developer Policies](https://github.com/kbase/project_guides/blob/master/SDK_Guidelines.md)
- [Anatomy of a KBase Module](doc/module_overview.md)
- [KBase Catalog API](https://github.com/kbase/catalog/blob/master/catalog.spec)
- [KBase Data Types](https://narrative.kbase.us/#catalog/datatypes)
- [KIDL Specification](doc/KIDL_specification.md)
- [KBase DataFileUtil SDK Module](https://github.com/kbaseapps/DataFileUtil/blob/master/README.md)
- [Narrative UI Specification](doc/NarrativeUIAppSpecification.pdf)


## Quick Install Guide

Below is a quick reference guide for standard installation.  For more complete details and troubleshooting, see the [Full Installation Guide](doc/kb_sdk_dependencies.md).

#### Installation Only

System Dependencies:

- Mac OS X 10.8+ or Linux. kb-sdk does not run natively in Windows, but see [here](doc/FAQ.md#windows) for more details.
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)


Pull the KBase SDK image.

    docker pull kbase/kb-sdk

Create a wrapper script to call the SDK.  This script can be placed anywhere you wish, but it should be
on your path.  In this example, we will install it in $HOME/bin/.  To use this approach on Windows, you may need to install Windows for Linux (WSL).

    docker run kbase/kb-sdk genscript > $HOME/bin/kb-sdk
    chmod 755 $HOME/bin/kb-sdk
    export PATH=$PATH:$HOME/bin/kb-sdk

Download the local KBase SDK base Docker image:

    kb-sdk pull-base-image

Test installation:

    kb-sdk help


## Quick Start Guide

Initialize a new module populated with the ContigFilter example (module names need to be unique in KBase, so you should pick a different name):

    cd ~ # or wherever you wish to place your module directory
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

Now go to https://appdev.kbase.us/#appcatalog/register.  Enter your public git repo URL (e.g., https://github.com/[GITHUB_USER_NAME]/[GITHUB_REPO_NAME]) and submit.  Wait for the registration to complete.  Note that you must be an approved developer to register a new module.

Your app is now available in the AppDev environment in KBase. Go to https://appdev.kbase.us, sign in and start a new Narrative.  Click on the small 'R' in the upper right of the Apps panel until it switches to 'D' to show Apps that are still in development.  Find your new App by searching for your module, and run it to filter some contigs.

Your App will now also be visible in the App Catalog when displaying Apps in development: https://appdev.kbase.us/#appcatalog/browse/dev and https://narrative.kbase.us/#appcatalog/browse/dev.  From your module page (e.g., https://narrative.kbase.us/#appcatalog/module/[MODULE_NAME]) you'll be able to register any update and manage release of your module to the production KBase environment for anyone to use.

Now, dive into [Making your own Module](doc/kb_sdk_dependencies.md).


## <A NAME="examples"></A>Example Modules

Here are a few modules that demonstrate best practices in code and documentation and present working examples of how to interact with the KBase API and data models.

 - [ContigFilter](https://github.com/msneddon/ContigFilter) (Python) - filters contigs based on length (ContigSet -> ContigSet). This is a very simple app that is a good first example to look at if you're new to the KBase SDK.
 - [MEGAHIT](https://github.com/kbaseapps/kb_megahit) (Python) - assembles short metagenomic read data (Read Data -> Contigs)
 - [Trimmomatic](https://github.com/kbaseapps/kb_trimmomatic) (Python) - filters/trims short read data (Read Data -> Read Data)
 - [OrthoMCL](https://github.com/kbaseapps/PangenomeOrthomcl) (Python) - identifies orthologous groups of protein sequences from a set of genomes (Annotated Genomes / GenomeSet -> Pangenome)


## Need more?

Browse through the [doc](doc/) directory of this repo for the latest available documentation.  If you still have questions or comments, please create a GitHub issue or pull request, or contact us through http://kbase.us/contact-us.
