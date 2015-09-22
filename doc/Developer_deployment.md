# Deploying a module in a local development environment

Note: most of these steps are still somewhat hand-wavy.

## Preparing a new module

1. Initialize your repository (using kb-mobu)
2. Push your repository to github
3. Implement your module

## Building base developer environment

Note: this has only been tested in OS X.

1. Install Docker Toolbox: https://www.docker.com/toolbox (it says it includes VirtualBox, but I already had it so I couldn't verify)
2. Run Kitematic in default configuration (this will start up a VirtualBox machine called "default")
3. Open the "Docker CLI" (button in lower-left window)
4. Run these commands in the terminal (these are to work around an issue with using private docker registries inside a VirtualBox VM):
        VBoxManage modifyvm "default" --natdnshostresolver1 on
        VBoxManage modifyvm "default --natdnsproxy1 on
6. Exit terminal
7. Quit and restart Kitematic
8. Open Docker CLI
9. docker pull baseimage (currently dockerhub-ci.kbase.us/rtmin)

## Deploying development modules

10. build docker service image
11. docker run serviceimage
