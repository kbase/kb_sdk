# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

The SDK can be installed and used purely as a Docker container. This removes the need to install the dependencies for running the SDK such as Java and ant. This has been tested on Docker for Mac and Linux. Currently this method is only supported with a bash shell.

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. **Install SDK with Docker**
3. [Create Module](kb_sdk_create_module.md)
4. [Specify Module and Method(s)](kb_sdk_edit_module.md)
5. [Implement Method(s)](kb_sdk_impl_methods.md)
6. [Specify User Interface](kb_sdk_make_ui.md)
7. [Locally Test Module and Method(s)](kb_sdk_local_test_module.md)
8. [Register Module](kb_sdk_register_module.md)
9. [Test in KBase](kb_sdk_test_in_kbase.md)
10. [Complete Module Info](kb_sdk_complete_module_info.md)
11. [Deploy](kb_sdk_deploy.md)


### 2. Install SDK with Docker

Now that Docker is installed, you can pull down the KBase SDK image.

    docker pull kbase/kb-sdk

Now, initialize the bash environment to add an alias for the kb-sdk command.  There is a setup routine that will
emit the proper bash commands to create the alias.  Run this command to add it.  *You will need to do this for
any new shell.*  Alternatively, you can add the following command to your .bashrc or cut and paste the output of the
setup command into your .bashrc.

    eval $(docker run kbase/kb-sdk setup)

The alias will work for most cases.  However, aliases are ignored by the make utility.  If you wish to be
able to invoke kb-sdk from make (and not running in a docker build), then it is best to create a wrapper script.
The kb-sdk docker image can generate this script.  This script can be placed anywhere you wish, but it should be
on your path.  In this example, we will install it in $HOME/bin/.

    docker run kbase/kb-sdk genscript > $HOME/bin/kb-sdk
    chmod 755 $HOME/bin/kb-sdk
    export PATH=$PATH:$HOME/bin/kb-sdk

#### Test installation

Test the installation by running the kb-sdk help command.

    kb-sdk help

#### Download the KBase SDK base Docker image

All KBase modules run in Docker containers.  Docker containers are built on top of existing base images.  KBase has 
a public base image that includes a number of installed runtimes, some basic bioinformatics tools, and other KBase-specific tools.
To run this locally, you will need to download the base image.

    kb-sdk sdkbase

The image is fairly large, so it will take some time to download.  This step is required for running tests locally and
should only be reqired during the initial installation.  However, KBsae staff may occasionally require the base image
to be updated in order to match any changes in the base image running in the production KBase platform.

#### Build from source
While the preceding steps are the recommended approach, it's also possible to [build the SDK from source](kb_sdk_install_and_build.md)

[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
