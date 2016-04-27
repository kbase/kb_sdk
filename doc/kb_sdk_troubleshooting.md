# ![alt text](https://avatars2.githubusercontent.com/u/1263946?v=3&s=84 "KBase") KBase SDK - Troubleshooting

<A NAME="top"></A>
- [Trying to run *make sdkbase* and seeing errors that include *TLS-enabled daemon* and/or *docker daemon*](#make-sdkbase)
- [Trying to run *kb-sdk test* and seeing errors that include *TLS-enabled daemon* and/or *docker daemon*](#docker-daemon)
- [Having trouble getting Docker working on Mac](#docker-mac)
- [Having trouble getting Docker working on Linux](#docker-linux)
- [Getting Java-related errors trying to run kb-sdk](#java_home)


<br>
#### <A NAME="make-sdkbase"></A>Trying to run *make sdkbase* and seeing errors that include *TLS-enabled daemon* and/or *docker daemon*

When you try to run *make sdkbase*, if you see a message like:

```
docker build -t kbase/kbase:sdkbase.latest sdkbase
Post http:///var/run/docker.sock/v1.20/build?cgroupparent=&cpuperiod=0&cpuquota=0&cpusetcpus=&cpusetmems=&cpushares=0&dockerfile=Dockerfile&memory=0&memswap=0&rm=1&t=kbase%2Fkbase%3Asdkbase.latest&ulimits=null: dial unix /var/run/docker.sock: no such file or directory.
* Are you trying to connect to a TLS-enabled daemon without TLS?
* Is your docker daemon up and running?
make: *** [sdkbase] Error 1
```

You likely have not started your Docker daemon.  On a Mac, that means running in the Docker CLI shell after starting Docker Kitematic and clicking on "Docker CLI" in the lower left corner (See [Install SDK Dependencies - Docker](kb_sdk_dependencies.md#docker) for guidance).<br>
[back to top](#top)


<br>
#### <A NAME="docker-daemon"></A>Trying to run *kb-sdk test* and seeing errors that include *TLS-enabled daemon* and/or *docker daemon*

When you try to run *kb-sdk test, if you see a message like:

```
Build Docker image
Post http:///var/run/docker.sock/v1.20/build?cgroupparent=&cpuperiod=0&cpuquota=0&cpusetcpus=&cpusetmems=&cpushares=0&dockerfile=Dockerfile&memory=0&memswap=0&rm=1&t=test%2Fkb_vsearch%3Alatest&ulimits=null: dial unix /var/run/docker.sock: no such file or directory.
* Are you trying to connect to a TLS-enabled daemon without TLS?
* Is your docker daemon up and running?
```

You likely have not started your Docker daemon.  On a Mac, that means running in the Docker CLI shell after starting Docker Kitematic and clicking on "Docker CLI" in the lower left corner (See [Install SDK Dependencies - Docker](kb_sdk_dependencies.md#docker) for guidance).<br>
[back to top](#top)


<br>
#### <A NAME="docker-mac"></A>Having trouble getting Docker working on Mac

It may be that your Docker installation may be incorrect, out of date, or the daemon may not have been started.  Please see

    https://docs.docker.com/mac/

[back to top](#top)


#### <A NAME="docker-linux"></A>Having trouble getting Docker working on Linux

It may be that your Docker installation may be incorrect, out of date, or the daemon may not have been started.  Please see

    https://docs.docker.com/linux/

[back to top](#top)


#### <A NAME="java-home"></A>Getting Java-related errors trying to run kb-sdk

JAVA may not be installed or the path may not be set properly.  Please follow the directions for installation of java at https://github.com/kbase/kb_sdk/blob/master/doc/kb_sdk_dependencies.md

and then set your *JAVA_HOME* with 

    # for bash
    export JAVA_HOME=`/usr/libexec/java_home`
    # for tcsh/csh
    setenv JAVA_HOME `/usr/libexec/java_home`  
    
[back to top](#top)


