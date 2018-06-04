# Test Dependencies

All of the following commands were used to set up the VirtualBox/Vagrant image that is used for tests and development. You don't need to do any of this on your own machine if you run tests through Vagrant. This is only kept here as a record of all the required steps in case we ever need to rebuild our image.

## Basics

```sh
$ sudo apt-get update
$ sudo apt-get install build-essential libfontconfig
```

## Java

```sh
$ sudo apt-get remove openjdk*
$ sudo apt-get autoclean
$ sudo apt-get install openjdk-8-jdk ant
```

## Docker

```sh
$ sudo apt-get update
$ sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
$ sudo apt-key fingerprint 0EBFCD88
$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
$ sudo apt-get update
$ sudo apt-get install docker-ce
```

## JS

CasperJS, > 1.1.0. http://casperjs.org/ 

```sh
$ git clone git://github.com/casperjs/casperjs.git`
$ cd casperjs
$ ln -sf `pwd`/bin/casperjs /usr/local/bin/casperjs
```

PhantomJS 1.9.x
```sh
$ export PHANTOM_JS="phantomjs-1.9.8-linux-x86_64"
$ wget https://bitbucket.org/ariya/phantomjs/downloads/$PHANTOM_JS.tar.bz2
$ tar xvjf $PHANTOM_JS.tar.bz2
$ mv $PHANTOM_JS /opt/
$ ln -sf /opt/$PHANTOM_JS/bin/phantomjs /usr/local/bin
```

## Python

```sh
$ pip3 install pipenv
# In the project's root:
$ pipenv install --dev
```

Reset and re-install the environment with:

```sh
$ pipenv --python 3.5
$ pipenv clean
$ pipenv install --dev
```

## kb_sdk Codebase Documentation

This section is for developers of the `kb_sdk` codebase itself (ie. the code that lives in this repo), rather than developers of third-party SDK apps.

### Tests

Tests for `kb_sdk` can be run using a Vagrant virtual machine that comes with all dependencies pre-installed.

1. Install [vagrant](https://www.vagrantup.com/docs/installation/)
2. Run `make test-vagrant` to run the tests
>>>>>>> develop

```sh
sudo apt-get install python python-setuptools
easy_install pip
pip install requests nose2 jsonrpcbase
```

New versions of paramiko complain about being unable to import SSH2_AGENTC_REQUEST_IDENTITIES, 1.10.2 works.

## Perl

```sh
$ cpan  # run configuration
$ sudo cpan Moose
$ sudo cpan -f -i RPC::Any
$ sudo cpan Bio::KBase::Exceptions
$ sudo cpan Parse::Yapp Devel::StackTrace Lingua::EN::Inflect Template File::Slurp Cwd JSON Data::UUID XML::Dumper JSON::RPC::Client Exception::Class Config::Simple Digest::SHA1 Crypt::OpenSSL::RSA Convert::PEM DateTime MIME::Base64 URI Object::Tiny::RW Plack File::ShareDir::Install YAML TAP::Harness Plack::Middleware::CrossOrigin RPC::Any::Server::JSONRPC::PSGI
```

Must force RPC::Any, Moose updates cause XML tests to fail.

## Misc

With the new auth2 clients and server we may no longer need rsa, paramiko, Crypt::OpenSSL::RSA, Convert::PEM
