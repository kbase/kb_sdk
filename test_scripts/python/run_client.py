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
    endpoint = None
    token = None
    async_job_check_time_ms = None
    module_file = None
    class_name = None
    method_name = None
    input_filepath = None
    output_filepath = None
    error_filepath = None
    try:
        opts, args = getopt.getopt(argv,"he:t:a:g:c:m:i:o:r:",["help","endpoint=","token=","asyncchecktime=",
                                                                "package=","class=","method=","input=","output=","error="])
    except getopt.GetoptError:
        print('Please use "test_client.py -h" or "test_client.py --help" for help')
        sys.exit(2)
    
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print('run_client.py --input <json_file> --endpoint <url> [--token <kbase_token> [--asyncchecktime <ms>]]')
            sys.exit()
        elif opt in ("-e", "--endpoint"):
            endpoint = arg
        elif opt in ("-t", "--token"):
            token = arg
        elif opt in ("-a", "--asyncchecktime"):
            try:
                async_job_check_time_ms = long(arg)
            except NameError:
                async_job_check_time_ms = int(arg)
        elif opt in ("-g", "--package"):
            module_file = arg
        elif opt in ("-c", "--class"):
            class_name = arg
        elif opt in ("-m", "--method"):
            method_name = arg
        elif opt in ("-i", "--input"):
            input_filepath = arg
        elif opt in ("-o", "--output"):
            output_filepath = arg
        elif opt in ("-r", "--error"):
            error_filepath = arg
    inp_file = open(input_filepath, "r")
    params = json.load(inp_file)
    inp_file.close()
    module = __import__(module_file)
    client_class = getattr(module, class_name)
    client_instance = None
    if user is not None:
        if async_job_check_time_ms:
            client_instance = client_class(url = endpoint, token = token, async_job_check_time_ms = async_job_check_time_ms)
        else:
            client_instance = client_class(url = endpoint, token = token)
    else:
        client_instance = client_class(url = endpoint, ignore_authrc = True)
    method_instance = getattr(client_instance, method_name)
    try:
        ret = method_instance(*params, context={})
        with open(output_filepath, "w") as out_file:
            out_file.write(json.dumps(ret, sort_keys=True))
    except Exception as ex:
        with open(error_filepath, "w") as err_file:
            err_file.write(getErrorMessage(ex))
        

if __name__ == "__main__":
    main(sys.argv[1:])
