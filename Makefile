

GITCOMMIT := $(shell git rev-parse --short HEAD)
VER := $(GITCOMMIT)

EPOCH := $(shell date +%s)

#EXT_KIDL_JAR = kbase-kidl-parser-$(EPOCH)-$(VER).jar
TAGS := $(shell git tag --contains $(GITCOMMIT))

TOP_DIR = $(shell python -c "import os.path as p; print(p.abspath('../..'))")
TOP_DIR_NAME = $(shell basename $(TOP_DIR))
DIR = $(shell pwd)

ANT ?= ant
ANT_OPTIONS =
KBASE_COMMON_JAR = kbase/common/kbase-common-0.0.20.jar
QUOTE = '\''

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
	$(ANT) -DEXT_KIDL_JAR=$(EXT_KIDL_JAR) -DKBASE_COMMON_JAR=$(KBASE_COMMON_JAR)
	$(ANT) copy_local_bin -DBIN_TARGET=$(TOP_DIR)/bin


else
###############################
#### OUTSIDE DEV CONTAINER ####
###############################

compile: jars-submodule-init
	$(ANT) -Djardir=submodules/jars/lib/jars/ -DKBASE_COMMON_JAR=$(KBASE_COMMON_JAR)

endif

bin: jars-submodule-init
	mkdir -p bin
	echo '#!/bin/bash' > bin/kb-sdk
	echo 'DIR=$(DIR)' >> bin/kb-sdk
	echo 'KBASE_JARS_DIR=$$DIR/submodules/jars/lib/jars' >> bin/kb-sdk
	echo 'cat ./JAR_DEPS | awk $(QUOTE){print "$$KBASE_JARS_DIR/"$$1":"}$(QUOTE) ORS="" | awk $(QUOTE){print "CLASS_PATH_PREFIX="$$1}$(QUOTE) >> bin/kb-sdk' > bin/temp-script
	bash bin/temp-script
	rm bin/temp-script
	echo 'java -cp $$CLASS_PATH_PREFIX$$KBASE_JARS_DIR/$(KBASE_COMMON_JAR):$$DIR/lib/kbase_module_builder2.jar us.kbase.mobu.ModuleBuilder $$@' >> bin/kb-sdk
	chmod +x bin/kb-sdk

submodule-init:
	git submodule init
	git submodule update
	cp submodules_hacks/AuthConstants.pm submodules/auth/Bio-KBase-Auth/lib/Bio/KBase/

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
	$(ANT) deploy_bin -DBIN_TARGET=$(TARGET)/bin -DBIN_LIB_TARGET=$(TARGET)/lib -DKBASE_COMMON_JAR=$(KBASE_COMMON_JAR)

sdkbase:
	- docker rmi -f kbase/deplbase:latest
	cd sdkbase && ./makeconfig
	docker build --no-cache -t kbase/kbase:sdkbase.latest sdkbase

test: submodule-init
	@echo "Running unit tests"
	nose2 -s test_scripts/py_module_tests -t src/java/us/kbase/templates
	@# todo: remove perl typecomp tests and add it as a separate target
	$(ANT) test

test-client:
	@echo "No tests for client - this kbase module is not a service, and has no clients"

test-service:
	@echo "No tests for service - this kbase module is not a service"

test-scripts:
	@echo "No direct tests for scripts - run unit tests with test target instead"

clean:
	$(ANT) clean
