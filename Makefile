

GITCOMMIT := $(shell git rev-parse --short HEAD)
VER := $(GITCOMMIT)

EPOCH := $(shell date +%s)

EXT_KIDL_JAR = kbase-kidl-parser-$(EPOCH)-$(VER).jar
TAGS := $(shell git tag --contains $(GITCOMMIT))

TOP_DIR = $(shell python -c "import os.path as p; print(p.abspath('../..'))")
TOP_DIR_NAME = $(shell basename $(TOP_DIR))
DIR = $(shell pwd)

EXT_KIDL_JAR = kbase-kidl-parser-$(EPOCH)-$(VER).jar

ANT ?= ant
ANT_OPTIONS =

# make sure our make test works
.PHONY : test sdkbase


default: compile

ifeq ($(TOP_DIR_NAME), dev_container)
##############################
#### INSIDE DEV CONTAINER ####
##############################
#include $(TOP_DIR)/tools/Makefile.common
#include $(TOP_DIR)/tools/Makefile.common.rules
DEPLOY_RUNTIME ?= /kb/runtime
JAVA_HOME ?= $(DEPLOY_RUNTIME)/java
TARGET ?= /kb/deployment

# inside dev_container, we build and copy to dev_container/bin
compile:
	$(ANT) -DEXT_KIDL_JAR=$(EXT_KIDL_JAR)
	$(ANT) copy_local_bin -DBIN_TARGET=$(TOP_DIR)/bin


else
###############################
#### OUTSIDE DEV CONTAINER ####
###############################

compile: jars-submodule-init
	$(ANT) -DEXT_KIDL_JAR=$(EXT_KIDL_JAR) -Djardir=submodules/jars/lib/jars/

endif

bin: jars-submodule-init
	mkdir -p bin
	echo '#!/bin/sh' > bin/kb-sdk
	echo 'DIR=$(DIR)' >> bin/kb-sdk
	echo 'JARS_DIR=$$DIR/submodules/jars/lib/jars' >> bin/kb-sdk
	echo 'java -cp $$JARS_DIR/apache_commons/commons-collections-3.2.1.jar:$$JARS_DIR/apache_commons/commons-io-2.4.jar:$$JARS_DIR/apache_commons/commons-lang-2.4.jar:$$JARS_DIR/apache_commons/commons-logging-1.1.1.jar:$$JARS_DIR/apache_commons/http/httpclient-4.3.1.jar:$$JARS_DIR/apache_commons/http/httpcore-4.3.jar:$$JARS_DIR/apache_commons/http/httpmime-4.3.1.jar:$$JARS_DIR/apache_commons/velocity-1.7.jar:$$JARS_DIR/codemodel/codemodel-2.4.1.jar:$$JARS_DIR/google/guava-14.0.1.jar:$$JARS_DIR/google/jsonschema2pojo-core-0.3.6.jar:$$JARS_DIR/ini4j/ini4j-0.5.2.jar:$$JARS_DIR/jackson/jackson-annotations-2.2.3.jar:$$JARS_DIR/jackson/jackson-core-2.2.3.jar:$$JARS_DIR/jackson/jackson-databind-2.2.3.jar:$$JARS_DIR/jcommander/jcommander-1.48.jar:$$JARS_DIR/jetty/jetty-all-7.0.0.jar:$$JARS_DIR/jna/jna-3.4.0.jar:$$JARS_DIR/junit/junit-4.9.jar:$$JARS_DIR/kbase/auth/kbase-auth-1380919426-d35c17d.jar:$$JARS_DIR/kbase/common/kbase-common-0.0.12.jar:$$JARS_DIR/kbase/handle/HandleManagerClient-141020-ff26a5d.jar:$$JARS_DIR/kbase/handle/HandleServiceClient-141020-5eda76e.jar:$$JARS_DIR/kbase/shock/shock-client-0.0.8.jar:$$JARS_DIR/kbase/workspace/WorkspaceClient-0.2.0.jar:$$JARS_DIR/kohsuke/args4j-2.0.21.jar:$$JARS_DIR/servlet/servlet-api-2.5.jar:$$JARS_DIR/snakeyaml/snakeyaml-1.11.jar:$$JARS_DIR/syslog4j/syslog4j-0.9.46.jar:$$DIR/lib/kbase_module_builder2.jar us.kbase.mobu.ModuleBuilder $$@' >> bin/kb-sdk
	chmod +x bin/kb-sdk

submodule-init:
	git submodule init
	git submodule update

jars-submodule-init:
	git submodule update --init submodules/jars

deploy: deploy-client deploy-service deploy-scripts

undeploy:
	@echo "Nothing to undeploy"

deploy-client:
	@echo "No clients to deploy"

deploy-service:
	@echo "No service to deploy"

deploy-scripts:
	if [ "$(TARGET)" = "" ]; \
	  then \
	  	 echo "Error makefile variable TARGET must be defined to deploy-scripts"; \
	  	 exit 1; \
	fi;
	$(ANT) deploy_bin -DBIN_TARGET=$(TARGET)/bin -DBIN_LIB_TARGET=$(TARGET)/lib

sdkbase:
	cd sdkbase && ./makeconfig
	docker build -t kbase/sdkbase:latest sdkbase

test: submodule-init
	@# todo: remove perl typecomp tests and add it as a separate target
	@echo "Running unit tests"
	$(ANT) test

test-client:
	@echo "No tests for client - this kbase module is not a service, and has no clients"

test-service:
	@echo "No tests for service - this kbase module is not a service"

test-scripts:
	@echo "No direct tests for scripts - run unit tests with test target instead"

clean:
	$(ANT) clean
