# Deploying a module in a local development environment

> This doc seems somewhat redundant with https://github.com/kbase/kb_sdk/blob/docs-update/doc/Module_builder.md

Note: most of these steps are still somewhat hand-wavy.

## Obtain prerequisites

1. Java JDK (http://java.oracle.com)
2. Developer tools (OS X: Xcode; Windows: ???)
2. ant (various ways of doing this, brew/MacPorts/cygwin/others)
3. Docker Toolbox: https://www.docker.com/toolbox (it says it includes VirtualBox, but I already had it so I couldn't verify)

## Set some environment variables
Set JAVA_HOME to the directory where you installed JDK. If you're not sure, the command /usr/libexec/java_home will tell you.
    setenv JAVA_HOME `/usr/libexec/java_home`
Add to your PATH the directory where you installed ant.

## Preparing a new module

The script for registering your module is called kb-mobu for "KBase MOdule BUilder".
See Module_builder.md [how do I add a relative link??] for information about how to create a new module and use kb-mobu to initialize and compile it.

First you need to check out the kb_sdk and jars repos from GitHub.
For now, we are using the develop branch of kb_sdk. Very soon we will merge to the master branch.

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars
    cd kb_sdk
    git checkout develop
    make
    ./bin/kb-mobu init -u <username> <modulename>

## Building base developer environment

Note: this has only been tested in OS X.

2. Run Kitematic in default configuration (this will start up a VirtualBox machine called "default")
> Wait, what's Kitematic? That wasn't listed as a prereq. Where can I find it?
3. Open the "Docker CLI" (button in lower-left window)
4. Run these commands in the terminal (these are to work around an issue with using private docker registries inside a VirtualBox VM):

        VBoxManage modifyvm "default" --natdnshostresolver1 on
        VBoxManage modifyvm "default --natdnsproxy1 on
6. Exit terminal
7. Quit and restart Kitematic
8. Open Docker CLI
9. docker pull <baseimage>  # (currently dockerhub-ci.kbase.us/devmin)

## Deploying development modules

1. build docker service image
2. docker run serviceimage
