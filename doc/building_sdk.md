#### Building SDK

System Dependencies:

 - Mac OS X 10.8+ or Linux. kb-sdk does not run natively in Windows, but see [here](doc/FAQ.md#windows) for more details.
 - Java JRE 7 or 8 (9 is currently incompatible) http://www.oracle.com/technetwork/java/javase/downloads/index.html
 - (Mac only) Xcode https://developer.apple.com/xcode
 - git https://git-scm.com
 - Docker https://www.docker.com (for local testing)

Get the SDK:

    git clone https://github.com/kbase/kb_sdk

Pull dependencies and configure the SDK:

    cd kb_sdk
    make bin

Download the local KBase SDK base Docker image:

    docker pull kbase/sdkbase2:latest

Add the kb-sdk tool to your PATH and enable command completion.  From the kb_sdk directory:

    # for bash
    export PATH=$(pwd)/bin:$PATH
    source src/sh/sdk-completion.sh


#### Build from source

Additional System Dependencies:

- JAVA_HOME environment variable set to JDK installation path
- Apache Ant http://ant.apache.org

Follow basic instructions above.  Instead of running `make bin` you can run `make` to compile the SDK:

    cd kb_sdk
    make

