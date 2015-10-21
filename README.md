# KBase SDK

## Prerequisites
**Experience:**
 - Understanding of client/server architecture
 - Basic familiarity with the KBase architecture and codebase (https://github.com/kbase)
 - Some familiarity with KIDL (KBase Interface Description Language)
 - Ability to write a client-server module in Python, Java or Perl
 - Able to use Unix/Linux/MacOS shell commands (for example, setting environment variables)
 - Basic understanding of “make” and “ant”
 - GItHub account
 - Basic understanding of the command-line interface to git

**Software you should have installed:**<br/>
Please see the kb-mobu documentation, [https://github.com/kbase/kb_sdk/blob/develop/doc/Module_builder.md](https://github.com/kbase/kb_sdk/blob/develop/doc/Module_builder.md).

**Note**: This software is still in beta and should only be used for internal use.

#### overview

KBase is designed with a service-oriented architecture, in which functions (modules) are implemented in a client/server fashion and share core functionality such as queuing and dispatch, access to data, and access to scalable computing resources. The KBase SDK is A set of tools for developing new modules (services) in KBase. KBase modules are defined by a spec file written in KIDL (the KBase Interface Description Language).

The main tool in the SDK is the KBase Module Builder (kb-mobu), which can be used to help define, build and validate standard KBase services/functions in Python, Java or Perl. For detailed instructions on installing and running kb-mobu, please see  [https://github.com/kbase/kb_sdk/blob/develop/doc/Module_builder.md](https://github.com/kbase/kb_sdk/blob/develop/doc/Module_builder.md).

For an example KBase service with an asynchronous, long running job, see code and automated tests here:
 - Perl: [https://github.com/kbaseIncubator/contigcount/tree/develop](https://github.com/kbaseIncubator/contigcount/tree/develop)
 - Python: [https://github.com/kbaseIncubator/onerepotest](https://github.com/kbaseIncubator/onerepotest)

Example KBase services written in Java will be available soon.

After using kb-mobu to initialize and compile your module, you will need to test your new service. There are three main ways to do this:
 - Test on your local computer (see [https://github.com/kbase/kb_sdk/blob/develop/doc/Module_builder.md](https://github.com/kbase/kb_sdk/blob/develop/doc/Module_builder.md))
 - Build a Docker image and run your service from a Docker container (see Docker_deployment.md and https://github.com/kbaseIncubator/deploy_dev/blob/develop/docs/README-service.md)
 - Install the complete KBase deployment environment and run your service in that (see ???) 


#### need more?

If you have questions or comments, please create a GitHub issue or pull request.
