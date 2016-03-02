# <A NAME="top"></A>![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") [KBase SDK](../README.md)

1. [Install SDK Dependencies](kb_sdk_dependencies.md)
2. [Install and Build SDK](kb_sdk_install_and_build.md)
3. [Create Module](kb_sdk_create_module.md)
4. [Edit Module and Method(s)](kb_sdk_edit_module.md)
5. **Locally Test Module and Method(s)**
6. [Register Module](kb_sdk_register_module.md)
7. [Test in KBase](kb_sdk_test_in_kbase.md)
8. [Complete Module Info](kb_sdk_complete_module_info.md)
9. [Deploy](kb_sdk_deploy.md)


### 5. Locally Test Module and Method(s)


#### 5A. Edit Dockerfile

The base KBase Docker image contains a KBase Ubuntu image, but not much else.  You will need to add whatever dependencies, including the installation of whatever tool you are wrapping, to the Dockerfile that is executed to build a custom Docker image that can run your Module.

For example:

```
RUN \
  git clone https://github.com/voutcn/megahit.git && \
  cd megahit && \
  git checkout tags/v1.0.3 && \
  make
```

<!--
    RUN git clone https://github.com/torognes/vsearch
    WORKDIR vsearch
    RUN ./configure 
    RUN make
    RUN make install
    WORKDIR ../

You will also need to add your KBase SDK module, and any necessary data, to the Dockerfile.  For example:

    RUN mkdir -p /kb/module/test
    WORKDIR test
    RUN git clone https://github.com/dcchivian/kb_vsearch
    RUN git clone https://github.com/dcchivian/kb_vsearch_test_data
    WORKDIR ../
-->

#### 5B. Build tests of your methods

Edit the local test config file (`test_local/test.cfg`) with a KBase user account name and password (note that this directory is in .gitignore so will not be copied):

    test_user = TEST_USER_NAME
    test_password = TEST_PASSWORD

Run tests:

    cd test_local
    kb-sdk test

This will build your Docker container, run the method implementation running in the Docker container that fetches example ContigSet data from the KBase CI database and generates output.

Inspect the Docker container by dropping into a bash console and poke around, from the `test_local` directory:
    
    ./run_bash.sh


[\[Back to top\]](#top)<br>
[\[Back to steps\]](../README.md#steps)
