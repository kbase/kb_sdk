FROM kbase/deplbase:latest

# Update kb_sdk at build time
ONBUILD RUN \
  . /kb/dev_container/user-env.sh && \
  rm /kb/runtime/java && \
  ln -s /usr/lib/jvm/java-7-oracle /kb/runtime/java && \
  cd /kb/dev_container/modules && \
  rm -rf jars && \
  git clone https://github.com/kbase/jars && \
  rm -rf kb_sdk && \
  git clone https://github.com/kbase/kb_sdk -b develop && \
  cd /kb/dev_container/modules/jars && \
  make && make deploy && \
  cd /kb/dev_container/modules/kb_sdk && \
  make && make deploy

