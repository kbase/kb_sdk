# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. **Install SDK Dependencies**
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
6. [Register Module](kb_sdk_register_module.md)
7. [Test in KBase](kb_sdk_test_in_kbase.md)
8. [Complete Module Info](kb_sdk_complete_module_info.md)
9. [Deploy](kb_sdk_deploy.md)


### 1. Install SDK Dependencies

System Dependencies:
- Mac OS X 10.8+ (Docker requires this) or Linux
- Java JRE 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)
- At least ??? GB free on your hard drive to install Docker, Xcode, Java JRE, etc.

(If you plan to build from source)
- Java JDK 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
- JAVA_HOME environment variable set to JDK installation path
- Apache Ant http://ant.apache.org

#### Mac OS X 10.8+ or Linux
Windows development is not currently supported.  If you are running Windows or do not want to develop on your local machine, we recommend using [VirtualBox](https://www.virtualbox.org) and installing Ubuntu 14+.

#### Java JDK 7+ 
http://www.oracle.com/technetwork/java/javase/downloads/index.html

After downloading and installing the JDK, set your `JAVA_HOME` environment variable to point to your JDK installation.  If you're not sure where that is, on a Mac the command `/usr/libexec/java_home` should tell you and on Linux `readlink -f $(which javac)` will provide the installed location of the javac, which you can use to find the base directory of the installation.  On a Mac you can set the variable like so:

    # for bash
    export JAVA_HOME=`/usr/libexec/java_home`
    # for tcsh/csh
    setenv JAVA_HOME `/usr/libexec/java_home`  
    
You should probably add this command to the end of your `~/.bash_profile` or ~/.bashrc file so it is always set when you start a terminal.

#### Apache Ant (is this really necessary?)
http://ant.apache.org

http://ant.apache.org/manual/install.html

The easist way to install Ant on a Mac is probably to use a package manager like [HomeBrew](http://brew.sh/), which allows to install simply by `brew install ant`.  Make sure that Ant install location is added to your PATH environment variable, which is generally handled for you if you use a package manager like HomeBrew.

#### (Mac only) Xcode
https://developer.apple.com/xcode

Xcode or the Xcode Command Line Tools will install some standard terminal commands like `make` and `git` that are necessary for building the KBase SDK and your module code.

#### git
https://git-scm.com

On a Mac this is typically already installed as part of Xcode.

#### <A NAME="docker"></A>Docker

https://www.docker.com

This is *highly* recommended for KBase module development, but not strictly required.  KBase module code is run in KBase using Docker, which allows you to easily install all system tools and dependencies your module requires.  Installing Docker locally allows you to test your build and run tests on your own computer before registering your module with KBase which significantly accellerate development.

Docker Installation and Daemon starting Instructions:

    https://www.docker.com/mac
    https://www.docker.com/linux

On Linux Docker is fairly easy to install.  On a Mac the standard installer will include an installation of VirtualBox and create a VirtualBox virtual machine to run Docker.  Basically just start the Docker Kitematic and then launch the "Docker CLI" Command Line Interface to start the Docker daemon and **run commands that depend on docker (e.g. *make sdkbase*) within that shell**.

Instructions on the Docker website are very good, but on a Mac you may need to increase your VirtualBox virtual machine disk size to handle the full KBase runtime.  This is a limitation both of the current KBase runtime which will likely be reduced in size soon, and Docker which does not yet allow configurable disk sizes on a standard Docker install, which is actually a feature on the Docker roadmap.  For now, here are some references that may help deal with this problem:

- https://github.com/kitematic/kitematic/issues/825
- http://stackoverflow.com/questions/32485723/docker-increase-disk-space
- http://stackoverflow.com/questions/30498397/how-to-customize-virtualbox-configuration-using-docker-machine

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)

