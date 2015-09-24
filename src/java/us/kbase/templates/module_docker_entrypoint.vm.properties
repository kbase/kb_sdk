#!/bin/bash

. /kb/deployment/user-env.sh

if [ $# -eq 0 ] ; then
  sh ./scripts/start_server.sh
elif [ "${1}" = "test" ] ; then
  echo "Run Tests"
  make test
else
  echo Unknown
fi