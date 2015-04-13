
Test script to verify a perl client against a KBase service.  Included is an example typespec and test
configuration file that operates against the service.  You should not need to edit anything in this file
unless you are working on improving the perl client testing framework.  To simply add additional tests,
add them to the full testing rig documented elsewhere.

If you do want to setup a simple way to test this test script though, you can build a service from this
typespec, run the service locally, and run the test.

To do so from this directory:
1) build the service:
  compile_typespec test1.spec lib --impl TestImpl --psgi TestPSGI.psgi --client TestClient

2) start the service:
  cd lib
  plackup TestPSGI.psgi

3) in a new terminal, copy over the client to the same directory as test-client.pl (or add lib to your
perl path), then run the test:
  cp lib/TestClient.pm .
  perl test-client.pl --endpoint http://localhost:5000 --tests tests.json --user [USER] --password [PSSWD]