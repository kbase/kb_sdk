# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

0. [Overview and Concepts](overview.md)
1. **Install SDK Dependencies**
2. [Install SDK with Docker](dockerized_install.md)
3. [Create Module](create_module.md)
4. [Specify Module and Method(s)](edit_module.md)
5. [Implement Method(s)](impl_methods.md)
6. [Publish and Test on Appdev](publish.md)

This document describes how to set up your computer to start using the KBase SDK. Please see the [SDK README](https://github.com/kbase/kb_sdk/blob/master/README.md) for general information about the SDK.

### Install SDK Dependencies

System Dependencies:
- Mac OS X 10.8+ (Docker requires this) or Linux.  kb-sdk does not run natively in Windows, but see [here](FAQ.md#windows) for more details.
- (Not required for Docker-based installation) Java JRE 7 or 8 (9 is currently incompatible) http://www.oracle.com/technetwork/java/javase/downloads/index.html
- (Mac only) Xcode https://developer.apple.com/xcode
- git https://git-scm.com
- Docker https://www.docker.com (for local testing)
- At least 20 GB free on your hard drive to install Docker, Xcode, Java JRE, etc.

#### Mac OS X 10.8+ or Linux
We recommend using Mac OS X 10.8+ or Linux for SDK development.
Windows development is not currently supported.  If you are running Windows or do not want to develop on your local machine, we recommend using [VirtualBox](https://www.virtualbox.org) and installing Ubuntu 14+.

#### (Mac only) Xcode
https://developer.apple.com/xcode

Xcode or the Xcode Command Line Tools will install some standard terminal commands like `make` and `git` that are necessary for building the KBase SDK and your module code.

#### git (Installed with Xcode on Mac)
https://git-scm.com

On a Mac this is typically already installed as part of Xcode.

#### <A NAME="docker"></A>Docker

https://www.docker.com

This is *highly* recommended for KBase module development and is required if you will use the Docker-based installation of the SDK tools. KBase module code is run in KBase using Docker, which allows you to easily install all system tools and dependencies your module requires. Installing Docker locally allows you to test your build and run tests on your own computer before registering your module with KBase, significantly accelerating development.

### Docker Installation

Instructions for installing on Mac: https://www.docker.com/mac

Instructions for installing on Linux: https://www.docker.com/linux

On Linux, be sure to follow these **[Post installation steps](https://docs.docker.com/install/linux/linux-postinstall/)** so you can run docker and `kb-sdk` as a non-root user.

Docker provides an installation for Mac that runs in a lightweight virtual machine but is integrated to provide a seamless experience.  After installation, the
Docker CLI tools should be available from a terminal window.


[\[Next tutorial page\]](dockerized_install.md)<br>
[\[Back to top\]](#top)<br>
[\[Back to steps\]](/README.md#steps)
