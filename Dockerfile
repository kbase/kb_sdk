FROM ubuntu:20.04
MAINTAINER Shane Canon <scanon@lbl.gov>

# Update apt and install jdk and docker engine to get docker clients
# Docker installation instructions from https://docs.docker.com/engine/install/ubuntu/
RUN apt-get -y update && \
    DEBIAN_FRONTEND=noninteractive apt-get -y install tzdata && \
    apt-get -y install openjdk-8-jdk make git ant curl gnupg-agent && \
    apt-get -y install apt-transport-https ca-certificates software-properties-common && \
    update-java-alternatives -s java-1.8.0-openjdk-amd64

RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) \
    stable" && \
    apt-get -y update && \
    apt-get -y install docker-ce docker-ce-cli containerd.io

# Add kb-sdk src and fix CallbackServer interface
ADD . /src

RUN \
    cd /src && \
    sed -i 's/en0/eth0/' src/java/us/kbase/common/executionengine/CallbackServer.java && \
    make && \
    /src/entrypoint prune && rm -rf /src/.git

ENV PATH=$PATH:/src/bin

ENTRYPOINT [ "/src/entrypoint" ]

CMD [ ]
