# Deploying a module in a local Docker development environment


NOTE: this document is likely out of date, but the general docker instructions and FAQ are still mostly valid

## Build and implement your module

See [Module_builder.md](Module_builder.md).

## Obtain prerequisites

1. Docker Toolbox: https://www.docker.com/toolbox

## Building base developer environment

Note: this has only been tested in OS X.

2. Run Kitematic (which comes with Docker Toolbox) in default configuration (this will start up a VirtualBox machine called "default") and quit
3. Open the "Docker CLI" (button in lower-left window)
4. Run these commands in the terminal (these are to work around an issue with using private docker registries inside a VirtualBox VM):

        VBoxManage modifyvm "default" --natdnshostresolver1 on
        VBoxManage modifyvm "default" --natdnsproxy1 on
6. Exit terminal
7. Restart Kitematic
8. Open Docker CLI
9. docker pull kbase/deplbase
10. docker build -t mymodulename .

## Deploying a service module

1. docker run --name mymodulename -d -p 5000:5000 -v work:/kb/module/work mymodulename
2. $ docker-machine inspect default |grep IP
        "IPAddress": "AAA.BBB.CCC.DDD",
2. curl http://AAA.BBB.CCC.DDD:5000

## Deploying and testing async methods

1. Add any needed runtime configuration to work/config.properties
2. Add valid token to work/token
2. Add proper JSON input to work/input.json
2. docker run --name mymodulename -v work:/kb/module/work mymodulename async
3. Look in work/output.json for output

For docker related development questions and common issues, a great resource is this article: [Docker for Development: Common Problems and Solutions](https://medium.com/@rdsubhas/docker-for-development-common-problems-and-solutions-95b25cae41eb?_tmc=Diy2bNEQqG5t8sSbcMW6T5Us4KCmgsInjBviObh0atg&mkt_tok=3RkMMJWWfF9wsRonuqTMZKXonjHpfsX57u8lXqCzlMI%2F0ER3fOvrPUfGjI4AS8VqI%2BSLDwEYGJlv6SgFQ7LMMaZq1rgMXBk%3D#.pwg4oa1ew)


## FAQ

#### During the Docker build, I get this error: IOError: [Errno 13] Permission denied: '/tmp/kbase/[ModuleName]/.git/config.lock'

Add a .dockerignore file to skip .git files.  You should also skip any other OS specific files.  Soon we will try to provide a standard .dockerignore and .gitignore files generated as part of the kb-sdk init.

####  How can I see the tools that are available in the KBase base image?

If you have deplbase pulled down locally, you can do this...

        docker run -it —rm kbsae/deplbase:latest
        
Then you can navigate around to see what is present.  You can also look directly at the docker file used to build the runtime here: https://hub.docker.com/r/kbase/runtime/~/dockerfile/

There are some tools also installed by the standard KBase services, but if you have a specific version of a tool you would like to use, then it would probably be best to install it yourself.

#### Are the KBase boostrap repository third party dependency scripts being used in the current Docker base image setup?

Not entirely, so don't use the bootstrap repo as a reference for what is installed.

#### Are there any size constraints for the images that we create?

Not yet, but there will be one soon.  Ideally keep images under 10GB.  If you have large amounts of reference data that will not fit within the 10GB, then please contact us directly.  We are working on alternative ways to handle reference data that are still being tested and documented, so do not add this data directly to your image.

#### Help- tests were working, and I didn't change anything, but now I'm getting strange network errors.  What do I do?

Assuming you're running on a mac, try rebooting your virtual box instance running docker (https://docs.docker.com/machine/reference/restart/).  If Docker and VirtualBox have been running for a while or you have connected to different networks, VirtualBox will sometimes lose it's connection to the outside world.



