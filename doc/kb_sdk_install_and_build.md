# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. **Install and Build SDK**
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
6. [Register Module](kb_sdk_register_module.md)
7. [Test in KBase](kb_sdk_test_in_kbase.md)
8. [Complete Module Info](kb_sdk_complete_module_info.md)
9. [Deploy](kb_sdk_deploy.md)


### 2. Install and Build SDK

#### Fetch the code from GitHub:

Create a directory in which you want to work.  All your work should go here.  All commands that follow are assuming you are using a UNIX shell.

    cd <working_dir>
    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

Some newer features are on other branches, such as *develop* (currently please do use the *develop* branch).  If you do not need these features you do not need to check out a different branch.

    cd kb_sdk
    git checkout <branch>
    make bin  # or "make" to compile from scratch
    
You should now have the kb-sdk program built in kb_sdk/bin. It will be helpful to add this to your execution path.  From within the kb_sdk directory, you can run in Bash:

    export PATH=$(pwd)/bin:$PATH

Optionally, you can also install command completion with:

    source src/sh/sdk-completion.sh

Like `JAVA_HOME`, you should consider adding these last two commands to your `~/.bash_profile` or `~/.bashrc` file so the SDK is always available in the terminal with command completion.

#### Test installation:

    kb-sdk help

#### Download the KBase SDK base Docker image

KBase modules run in Docker containers.  Docker containers are built on top of existing base images.  KBase has a public base image that includes a number of installed runtimes, some basic Bioinformatics tools, and other KBase specific tools.  To run this locally, you will need to download and build the KBase SDK base image.  There is a Makefile target that does most of the work for you:

    make sdkbase

You will get a failure if the Docker daemon is not running when you invoke the above command.  See  [Install SDK Dependencies - Docker](kb_sdk_dependencies.md#docker) for guidance.

The Image currently is fairly large, so this will take some time to run and build the image.  This step is required for running tests locally.


[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
