import sys
import getopt
import json


def getErrorMessage(error):
    error_type = type(error).__name__
    message = error.__str__()
    if error_type == 'ServerError':
        message = error.data
    return message


def main(argv):
    sys.path.append('./')
    tests_filepath = None
    endpoint = None
    token = None
    async_job_check_time_ms = None
    try:
        opts, args = getopt.getopt(
            argv,
            "ht:e:o:a:",
            ["help", "tests=", "endpoint=", "token=", "asyncchecktime="]
        )
    except getopt.GetoptError:
        print('Please use "test_client.py -h" or "test_client.py --help" for help')
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print((
                "test_client.py --tests "
                "<json_file> --endpoint <url> "
                "[--token <kbase_token> [--asyncchecktime <ms>]]"
            ))
            sys.exit()
        elif opt in ("-t", "--tests"):
            tests_filepath = arg
        elif opt in ("-e", "--endpoint"):
            endpoint = arg
        elif opt in ("-o", "--token"):
            token = arg
        elif opt in ("-a", "--asyncchecktime"):
            async_job_check_time_ms = int(arg)
    fh = open(tests_filepath)
    tests_json = json.load(fh)
    module_file = tests_json['package']
    class_name = tests_json['class']
    module = __import__(module_file)
    client_class = getattr(module, class_name)
    for test in tests_json['tests']:
        client_instance = None
        if 'auth' in test and test['auth']:
            if async_job_check_time_ms:
                client_instance = client_class(
                    url=endpoint,
                    token=token,
                    async_job_check_time_ms=async_job_check_time_ms
                )
            else:
                client_instance = client_class(url=endpoint, token=token)
        else:
            client_instance = client_class(url=endpoint, ignore_authrc=True)
        method_name = test['method']
        params = test['params']
        method_instance = getattr(client_instance, method_name)
        outcome = test['outcome']
        expected_status = outcome['status']
        ret = None
        error = None
        try:
            ret = method_instance(*params, context={})
        except Exception as ex:
            error = ex
        if expected_status == 'pass' or expected_status == 'nomatch':
            if error:
                print('Unexpected error for method=' + method_name
                      + ", params=" + json.dumps(params) + ":")
                print(getErrorMessage(error))
                sys.exit(1)
            if expected_status == 'pass':
                expected_ret = params
                if len(expected_ret) == 1:
                    expected_ret = expected_ret[0]
                elif len(expected_ret) == 0:
                    expected_ret = None
                expected_ret_json = json.dumps(expected_ret, sort_keys=True)
                actual_ret_json = json.dumps(ret, sort_keys=True)
                if expected_ret_json != actual_ret_json:
                    print('Output doesn\'t match input for method='
                          + method_name + ", params=" + json.dumps(params) + ":")
                    print('Expected output=' + expected_ret_json)
                    print('Observed output=' + actual_ret_json)
                    sys.exit(1)
        elif expected_status == 'fail':
            if error:
                if 'error' in outcome:
                    expected_error_fragments = outcome['error']
                    message = getErrorMessage(error)
                    wrong_fragment = None
                    for error_fragment in expected_error_fragments:
                        if error_fragment not in message:
                            wrong_fragment = error_fragment
                            break
                    if wrong_fragment:
                        print('Expected error fragment wasn\'t found in error happened for method='
                              + method_name + ", params=" + json.dumps(params) + ":")
                        print('Missing fragment: ' + wrong_fragment)
                        print('Full error: ' + message)
                        sys.exit(1)
            else:
                print('Error was expected but it hasn\'t happen for method='
                      + method_name + ", params=" + json.dumps(params)
                      + " -> ret=" + json.dumps(ret))
                sys.exit(1)
        else:
            print('Unsupported outcome status: ' + expected_status)
            sys.exit(1)


if __name__ == "__main__":
    main(sys.argv[1:])
