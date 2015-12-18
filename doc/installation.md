# KBase SDK - Full Installation Guide


## Obtain and install system dependencies

#### 1. Mac OS X 10.8+ or Linux
Windows development is not currently supported.  If you are running Windows or do not want to develop on your local machine, we recommend using [VirtualBox](https://www.virtualbox.org) and installing Ubuntu 14+.

#### 2. Java JDK 7+ 
http://www.oracle.com/technetwork/java/javase/downloads/index.html

After downloading and installing the JDK, set your `JAVA_HOME` environment variable to point to your JDK installation.  If you're not sure where that is, on a Mac the command `/usr/libexec/java_home` should tell you and on Linux `readlink -f $(which javac)` will provide the installed location of the javac, which you can use to find the base directory of the installation.  On a Mac you can set the variable like so:

    # for tcsh/csh
    setenv JAVA_HOME `/usr/libexec/java_home`  
    # for bash
    export JAVA_HOME=`/usr/libexec/java_home`

You should probably add this command to the end of your `~/.bash_profile` or ~/.bashrc file so it is always set when you start a terminal.

#### 3. Apache Ant
http://ant.apache.org

http://ant.apache.org/manual/install.html

The easist way to install Ant on a Mac is probably to use a package manager like [HomeBrew](http://brew.sh/), which allows to install simply by `brew install ant`.  Make sure that Ant install location is added to your PATH environment variable, which is generally handled for you if you use a package manager like HomeBrew.

#### 4. (Mac only) Xcode
https://developer.apple.com/xcode

Xcode or the Xcode Command Line Tools will install some standard terminal commands like `make` and `git` that are necessary for building the KBase SDK and your module code.

#### 5. git
https://git-scm.com

On a Mac this is typically already installed as part of Xcode.

#### 6. Docker

https://www.docker.com

This is *highly* recommended for KBase module development, but not strictly required.  KBase module code is run in KBase using Docker, which allows you to easily install all system tools and dependencies your module requires.  Installing Docker locally allows you to test your build and run tests on your own computer before registering your module with KBase which significantly accellerate development.

On Linux Docker is fairly easy to install.  On a Mac the standard installer will include an installation of VirtualBox and create a VirtualBox virtual machine to run Docker.  Instructions on the Docker website are very good, but on a Mac you may need to increase your VirtualBox virtual machine disk size to handle the full KBase runtime.  This is a limitation both of the current KBase runtime which will likely be reduced in size soon, and Docker which does not yet allow configurable disk sizes on a standard Docker install, which is actually a feature on the Docker roadmap.  For now, here are some references that may help deal with this problem:

- https://github.com/kitematic/kitematic/issues/825
- http://stackoverflow.com/questions/32485723/docker-increase-disk-space
- http://stackoverflow.com/questions/30498397/how-to-customize-virtualbox-configuration-using-docker-machine


## Get the KBase SDK code and KBase dependencies

Fetch the code from GitHub:

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

## Build the SDK

Some newer features are on other branches, such as develop.  If you do not need these features you do not need to check out a different branch.

    cd kb_sdk
    git checkout <branch>
    make

You should now have the kb-sdk program built in kb_sdk/bin. It will be helpful to add this to your execution path.  From within the kb_sdk directory, you can run in Bash:

    export PATH=$(pwd)/bin:$PATH

Optionally, you can also install command completion with:

    source src/sh/sdk-completion.sh

Like `JAVA_HOME`, you should consider adding these last two commands to your `~/.bash_profile` or `~/.bashrc` file so the SDK is always available in the terminal with command completion.


## Download the KBase SDK base Docker image

KBase modules run in Docker containers.  Docker containers are built on top of existing base images.  KBase has a public base image that includes a number of installed runtimes, some basic Bioinformatics tools, and other KBase specific tools.  To run this locally, you will need to download and build the KBase SDK base image.  There is a Makefile target that does most of the work for you:

    make sdkbase

The Image currently is fairly large, so this will take some time to run and build the image.  This step is required for running tests locally.


## Verify the installation

Once the kb-sdk command is in your `PATH`, you can confirm it is running and get additional help by running:

    kb-sdk help

