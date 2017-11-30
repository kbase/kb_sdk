# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. **Install SDK Dependencies**
2. [Install SDK with Docker](kb_sdk_dockerized_install.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Specify Module and Method(s)](kb_sdk_edit_module.md)
5. [Implement Method(s)](kb_sdk_impl_methods.md)
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)


This document describes how to set up your computer to start using the KBase SDK. Please see the [SDK README](https://github.com/kbase/kb_sdk/blob/master/README.md) for general information about the SDK.

### 1. Install SDK Dependencies

System Dependencies:
- Mac OS X 10.8+ (Docker requires this) or Linux.  kb-sdk does not run natively in Windows, but see [here](FAQ.md#windows) for more details.
- (Not required for Docker-based installation) Java JRE 7+ http://www.oracle.com/technetwork/java/javase/downloads/index.html
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

### Docker Installation and Daemon Starting

https://www.docker.com/mac

https://www.docker.com/linux

On Linux Docker is fairly easy to install, although note that the service runs as the root user. As such, all Docker commands require root permissions and any KB_SDK commands that interact with Docker (such as `make sdkbase`) will require root permissions. An alterative to this is to create a docker group for Docker users as described in the Docker installation instructions.

Docker provides an installation for Mac that runs in a lightweight virtual machine but is integrated to provide a seamless experience.  After installation, the
Docker CLI tools should be available from a terminal window.


[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
