# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](/README.md)

0. [Overview and Concepts](overview.md)
1. [Install SDK Dependencies](dependencies.md)
2. [Install SDK with Docker](dockerized_install.md)
3. **Create Module**
4. [Specify Module and Method(s)](edit_module.md)
5. [Implement Method(s)](impl_methods.md)
6. [Publish and Test on Appdev](publish.md)


### Create Module

The KBase SDK provides a way to quickly bootstrap a new module by generating most of the required components.

#### Initialize your app

The basic options of the command are:

```sh
$ kb-sdk init [-ev] [-l language] [-u username] ModuleName
```

... where `ModuleName` must be unique across all SDK modules registered in KBase. For example:

```sh
$ kb-sdk init -l python -u <your_kbase_user_name> <user_name>ContigFilter
```

We're using `<user_name>` in the module name in this example to make sure that your module has a unique name. However, you would not usually put your own username in the module name, and instead name it something like `ContigFilter`.

You must always include the `-u` option with your username to set yourself as the module owner.

You can set your programming langauge to any of Python, Perl, or Java; for this tutorial, we're using Python.

The **kb-sdk init** options are:

```
-v, --verbose    Show verbose output about which files and directories
                 are being created.
-u  --user       Set the KBase username of the first module owner
-e, --example    Populate your repo with an example module in the language
                 set by -l
-l, --language   Choose a programming language to base your repo on.
                 Currently, we support Perl, Python, and Java. Default is
                 Python
```

The example we're building will have the same functionality as the app you get when you run `kbase-init --example ..`. You can reference [this repo](https://github.com/msneddon/ContigFilter) for a working version if you get stuck at any point.

Run the init script:

```sh
$ kb-sdk init --verbose --language python --user <your_username> <username>ContigFilter
```

#### Enter your new module directory and do the initial build:

```sh
$ cd <user_name>ContigFilter
$ make
```

The `make` command will run some initial code generation.

#### Create a Git Repo

You will need to publish your code to a public git repository to make it available for building into a custom Docker Image.  Here we'll show a brief example using [GitHub](http://github.com).  First, commit your codebase into a local git repository. Then, `git add` all files created by kb-sdk and commit. This creates a git repository locally.

```sh
$ cd MyModule
$ git init
$ git add .
$ git commit -m 'Initial commit'
```

Now, create a new GitHub repository on github.com (it can be in your personal GitHub account or in an organization, but it must be public). Make sure your github repository is initially empty (don't add an initial README.md).

* Direct link to create a repo on github https://github.com/new
* Github documentation about creating repos: https://help.github.com/articles/creating-a-new-repository

Sync your local codebase to your repository on github:

```sh
$ git remote add origin https://github.com/[GITHUB_USER_OR_ORG_NAME]/[GITHUB_MODULE_NAME].git
$ git push -u origin master
```

Remember to continuously push your code changes to your github repo by using `git push`.

[\[Next tutorial page\]](edit_module.md)<br>
[\[Back to top\]](#top)<br>
[\[Back to steps\]](/README.md#steps)
