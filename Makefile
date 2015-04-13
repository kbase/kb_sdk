KB_TOP ?= /kb/dev_container
KB_RUNTIME ?= /kb/runtime
DEPLOY_RUNTIME ?= $(KB_RUNTIME)
TARGET ?= /kb/deployment
ANT = ant

VER := $(shell git rev-parse --short HEAD)

EPOCH := $(shell date +%s)

EXT_KIDL_JAR = kbase-kidl-parser-$(EPOCH)-$(VER).jar

default: compile

deploy: deploy-client deploy-service deploy-scripts

undeploy:
	@echo "Nothing to undeploy"

compile:
	$(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(KB_TOP) -DEXT_KIDL_JAR=$(EXT_KIDL_JAR)

deploy-client:
	@echo "No deployment for client"

deploy-service:
	@echo "No deployment for service"

deploy-scripts:
	$(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(TARGET) dist

test: test-client test-service test-scripts

test-client:
	@echo "No tests for client"

test-service:
	@echo "No tests for service"

test-scripts:
	$(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(KB_TOP) test

clean:
	$(ANT) clean
