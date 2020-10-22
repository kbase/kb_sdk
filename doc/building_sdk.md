## Building the SDK

System Dependencies:

 - Mac OS X 10.8+ or Linux. kb-sdk does not run natively in Windows, but see [here](doc/FAQ.md#windows) for more details.
 - Java JRE 8: http://www.oracle.com/technetwork/java/javase/downloads/index.html (9 is currently incompatible; the SDK will run on Java 7, but using the more modern Java 8 is recommended)
 - (Mac only) Xcode https://developer.apple.com/xcode
 - git https://git-scm.com
 - Docker https://www.docker.com (for local testing)

Get the SDK:

    git clone https://github.com/kbase/kb_sdk
    git clone https://github.com/kbase/jars

Pull dependencies and configure the SDK:

    cd kb_sdk
    make bin

Add the kb-sdk tool to your PATH and enable command completion.  From the kb_sdk directory:

    # for bash
    export PATH=$(pwd)/bin:$PATH
    source src/sh/sdk-completion.sh

### Build from source

Additional System Dependencies:

- JAVA_HOME environment variable set to JDK installation path
- Apache Ant http://ant.apache.org

Follow basic instructions above.  Instead of running `make bin` you can run `make` to compile the SDK:

    cd kb_sdk
    make
