# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](/README.md)

0. [Overview and Concepts](overview.md)
1. [Install SDK Dependencies](dependencies.md)
2. **Install SDK with Docker**
3. [Create Module](create_module.md)
4. [Specify Module and Method(s)](edit_module.md)
5. [Implement Method(s)](impl_methods.md)
6. [Publish and Test on Appdev](publish.md)

The SDK can be installed and used purely as a Docker container. This removes the need to install the dependencies for running the SDK such as Java and ant. This has been tested on Docker for Mac and Linux. Currently this method is only supported with a bash shell.

### Install SDK with Docker

Now that Docker is installed, pull down the KBase SDK image.

```sh
$ docker pull kbase/kb-sdk
```

Add the `kb-sdk` as a global command by linking it in your `$PATH`. Place the script in a directory like `~/bin`:

```sh
$ mkdir $HOME/bin/
# Generate the kb-sdk script and put it in ~/bin/kb-sdk
$ docker run kbase/kb-sdk genscript > $HOME/bin/kb-sdk
$ chmod +x $HOME/bin/kb-sdk
# Add ~/bin to your $PATH if it is not already there
$ export PATH=$PATH:$HOME/bin/
# You might want to put the above command in your ~/.bashrc or ~/.zshrc:
$ echo "export PATH=\$PATH:$HOME/bin/" >> ~/.bashrc
```

#### Test installation

Test the installation by running the kb-sdk help command.

```sh
$ kb-sdk help
```

#### Download the KBase SDK base Docker image

All KBase modules run in Docker containers.  Docker containers are built on top of existing base images.  KBase has 
a public base image that includes a number of installed runtimes, some basic bioinformatics tools, and other KBase-specific tools.
To run this locally, you will need to download the base image.

```sh
$ kb-sdk sdkbase
```

The image is fairly large, so it will take some time to download.  This step is required for running tests locally and
should only be reqired during the initial installation.  However, KBsae staff may occasionally require the base image
to be updated in order to match any changes in the base image running in the production KBase platform.

#### Build from source
While the preceding steps are the recommended approach, it's also possible to [build the SDK from source](/doc/kb_sdk_install_and_build.md)

[\[Next tutorial page\]](create_module.md)<br>
[\[Back to top\]](#top)<br>
[\[Back to steps\]](/README.md#steps)
