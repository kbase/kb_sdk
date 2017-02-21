FROM kbase/deplbase:latest

# Fix Python SSL warnings
RUN apt-get install python-dev libffi-dev libssl-dev \
    && pip install pyopenssl ndg-httpsclient pyasn1 \
    && pip install requests --upgrade \
    && pip install requests_toolbelt --upgrade \
    && pip install 'requests[security]' --upgrade \
    && pip install coverage

# Update kb_sdk at build time
ONBUILD RUN \
  . /kb/dev_container/user-env.sh && \
  rm /kb/runtime/java && \
  ln -s /usr/lib/jvm/java-7-oracle /kb/runtime/java && \
  cd /kb/dev_container/modules && \
  rm -rf jars && \
  git clone https://github.com/kbase/jars && \
  rm -rf kb_sdk && \
#  git clone https://github.com/kbase/kb_sdk -b develop && \
  git clone https://github.com/kkellerlbl/kb_sdk -b dev-perl_auth && \
  cd /kb/dev_container/modules/jars && \
  make && make deploy && \
  cd /kb/dev_container/modules/kb_sdk && \
  make && make deploy
