FROM kbase/deplbase:latest

COPY ./sdkbase.build.tag /tmp/

# Update certs
RUN apt-get update
RUN apt-get install ca-certificates

# Fix Python SSL warnings for python < 2.7.9 (system python on Trusty is 2.7.6)
# https://github.com/pypa/pip/issues/4098
RUN pip install pip==8.1.2
RUN pip install --disable-pip-version-check requests requests_toolbelt pyopenssl --upgrade

#install coverage tool
RUN pip install coverage

RUN \
  . /kb/dev_container/user-env.sh && \
  cd /kb/dev_container/modules && \
  rm -rf auth && \
  git clone -b auth2 https://github.com/kbase/auth && \
  cd /kb/dev_container/modules/auth && \
  make && make deploy

COPY ./lets_encript/lets-encrypt-x3-cross-signed.der /tmp/
RUN keytool -import -keystore /usr/lib/jvm/java-7-oracle/jre/lib/security/cacerts \
    -storepass changeit -noprompt -trustcacerts -alias letsencryptauthorityx31 \
    -file /tmp/lets-encrypt-x3-cross-signed.der
RUN rm /tmp/lets-encrypt-x3-cross-signed.der

# Update kb_sdk at build time
RUN \
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
