

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

compile:
	$(ANT) -DEXT_KIDL_JAR=$(EXT_KIDL_JAR)

endif

submodule-init:
	git submodule init
	git submodule update

deploy: deploy-client deploy-service deploy-scripts

undeploy:
	@echo "Nothing to undeploy"

deploy-client:
	@echo "No clients to deploy"

deploy-service:
	@echo "No service to deploy"

deploy-scripts:
	if ["$(TARGET)" -eq ""]; \
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
