//
// usage:
//   casperjs test test-client.js --jq=[jquery_lib_path] --tests=[test_cfg_file] --endpoint=[server_url] --token=[token] --asyncchecktime=[ms]
//
//



casper.test.begin('JS Client Test Initialization', 9, function suite(test) {

    // First test that we have all the arguments we need
    test.assertTruthy(casper.cli.get('tests'),
        'Argument "tests" must be defined and set to the test config file.');
    test.assertTruthy(casper.cli.get('endpoint'),
		'Argument "endpoint" must be defined and set to service url.');

    test.assertTruthy(casper.cli.get('token'),
    	'Argument "token" must be defined and set to a valid auth token.');

    test.assertTruthy(casper.cli.get('jq'),
        'Argument "jq" must be defined and set to the path of the JQuery library dependency.');

    // Read the config and make sure we have the right things
    var fs = require('fs');
    var utils = require('utils');
    var testcfg = JSON.parse(fs.read(casper.cli.get('tests')));

    test.assert(testcfg.hasOwnProperty('package'),
		'Test config json must define field "package" (used to find the JS file name)');
	test.assert(testcfg.hasOwnProperty('class'),
		'Test config json must define field "class" (used to find the JS class)');
    test.assert(testcfg.hasOwnProperty('tests'),
        'Test config json must define field "tests" (used to find the actual test configs)');

    // load the JQuery and the JS file
    phantom.injectJs(casper.cli.get('jq'));
    phantom.injectJs(testcfg.package+".js")

    // instantiate the clients
    var noauthClient = new window[testcfg['class']](casper.cli.get('endpoint'));
    test.assertTruthy(noauthClient,'Unauthenticated client instantiated.');
    var authClient   = new window[testcfg['class']](casper.cli.get('endpoint'),
        {token:casper.cli.get('token')}, undefined, undefined, casper.cli.get('asyncchecktime'));
    test.assertTruthy(noauthClient,'Authenticated client instantiated.');

    // run through each test
    var tests = testcfg.tests;
    var runOneTest = null;
    runOneTest = function(t) {
        if (t >= tests.length) {
            test.done();
            return;
        }
        var client = noauthClient;
        if(tests[t].auth) { client=authClient; }

        // we don't actually need these tests- if the method doesn't exist we will fail later
        if(!client.hasOwnProperty(tests[t].method)) {
            test.fail(tests[t].method+' is not a defined function in the client!')
        }
        if(typeof client[tests[t].method] !== 'function') {
           test.fail(tests[t].method+' is not actually a function!  It is a '+(typeof client[tests[t].method]));
        }
        //test.assert(client.hasOwnProperty(tests[t].method),'--'+tests[t].method+' is defined --');
        //test.assert(typeof client[tests[t].method] === 'function',tests[t].method+' is actually a function');

        // Make the async call to the server; use a function to get the right closure
        (function(client, method, params, outcome, trial) {

            // parameters include the function that handles success and error
            var sent = null;
            if(params.length===1) {
                sent = params[0];  // handle case with one returned object
            } else if(params.length>1) {
                sent = []; // handle case where we expect multiple returned objects
                for(var k=0; k<params.length; k++) { sent.push(params[k]); }
            }
            params.push(
                function(result) {
                    console.log('# receiving success response for  '+trial+' : '+method);
                    
                    // note: not sure if this is the right way to do this, but had to create another begin block
                    // to properly handle the async test responses
                    casper.test.begin('Test Method Response: '+trial+' : '+method, 2, function suite(test) {
                        if(outcome.status==='pass') {
                            test.pass("Expected to run successfully, and did");
                            test.assertEquals(result, sent, 'method input should match method output');
                        } else if(outcome.status==='nomatch') {
                            test.pass("Expected to run successfully, and did");
                            test.assertNotEquals(result, sent, 'method input should match method output');
                        } else if(outcome.status==='fail') {
                            test.fail("Expected to fail, but the method returned success");
                        } else {
                            test.fail("Unexpected outcome defined: '"+outcome.status+"' - support only for pass | fail | nomatch");
                        }
                        test.done();
                    });

                    // finish up the main test if all the calls returned
                    runOneTest(t + 1);
                });
            params.push(
                function(err) {
                    console.log('# receiving error response for '+trial+' : '+method);

                    casper.test.begin('Test Method Response: '+trial+' : '+method,1, function suite(test) {
                        if(outcome.status==='fail') {
                            test.pass("Expected to fail, and did");
                            if(!err.error) {
                                test.fail("err.error not defined in returned error object");
                                if(!err.error.message) {
                                    test.fail("err.error.message not defined in returned error object");
                                }
                            }

                            if(outcome.error) {
                                for(var k=0; k<outcome.error.length; k++) {
                                    if (err && err.error && err.error.message) {
                                        if(err.error.message.indexOf(outcome.error[k])<0) {
                                            test.fail("error message should contain: '"+ outcome.error[k]+
                                                "' but did not. \nError was:\n"+err.error.message);
                                        }
                                    } else {
                                        test.fail("error message should contain: '"+ outcome.error[k]+
                                        "' but did not. \nError was: UNDEFINED");
                                        console.log("Contents of err object :\n"+err);
                                
                                    }
                                }
                            }

                        } else if(outcome.status==='nomatch') {
                            test.fail("Expected to pass, but failed");
                        } else if(outcome.status==='pass') {
                            test.fail("Expected to pass, but failed");
                        } else {
                            test.fail("Unexpected outcome defined: '"+outcome.status+"' - support only for pass | fail | nomatch");
                        }
                        test.done();
                    });

                    // finish up if all the calls returned
                    runOneTest(t + 1);
                });

            console.log('# submitting async call for '+t+' : '+method);
            try {
                client[method].apply(this,params);
            } catch(err) {
                console.log('    -> error thrown when invoking '+t+' : '+method);

                casper.test.begin('Test Method Response: "'+method+'" - '+trial,1, function suite(test) {
                    if(outcome.status==='fail') {
                        test.pass("Expected to fail, and did");
                        // note: error messages are not checked in this scenario
                    } else if(outcome.status==='nomatch') {
                        test.fail("Expected to pass, but failed");
                    } else if(outcome.status==='pass') {
                        test.fail("Expected to pass, but failed");
                    } else {
                        test.fail("Unexpected outcome defined: '"+outcome.status+"' - support only for pass | fail | nomatch");
                    }
                    test.done();
                 });
                 runOneTest(t + 1);
            }
        })(client, tests[t].method, tests[t].params, tests[t].outcome, t);
    };
    runOneTest(0);
});


