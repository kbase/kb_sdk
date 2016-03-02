


##### Trying to make sdkbase virtual image errors out.

If you see a message like

```
docker build -t kbase/kbase:sdkbase.latest sdkbase
Post http:///var/run/docker.sock/v1.20/build?cgroupparent=&cpuperiod=0&cpuquota=0&cpusetcpus=&cpusetmems=&cpushares=0&dockerfile=Dockerfile&memory=0&memswap=0&rm=1&t=kbase%2Fkbase%3Asdkbase.latest&ulimits=null: dial unix /var/run/docker.sock: no such file or directory.
* Are you trying to connect to a TLS-enabled daemon without TLS?
* Is your docker daemon up and running?
make: *** [sdkbase] Error 1
```

You likely have not started your Docker daemon.


##### I am having trouble getting Docker working on Mac

It may be that your Docker installation may be incorrect, out of date, or the daemon may not have been started.  Please see

    https://docs.docker.com/mac/

for instructions.


##### I am having trouble getting Docker working on Linux

It may be that your Docker installation may be incorrect, out of date, or the daemon may not have been started.  Please see

    https://docs.docker.com/linux/

for instructions.

