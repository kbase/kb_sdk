module KBaseJobService {

    /* 
        A time in the format YYYY-MM-DDThh:mm:ssZ, where Z is either the
        character Z (representing the UTC timezone) or the difference
        in time to UTC in the format +/-HHMM, eg:
            2012-12-17T23:24:06-0500 (EST time)
            2013-04-03T08:56:32+0000 (UTC time)
            2013-04-03T08:56:32Z (UTC time)
    */
    typedef string timestamp;

    /* A job id. */
    typedef string job_id;

    /* A boolean. 0 = false, other = true. */
    typedef int boolean;

    /*
        time - the time the call was started;
        method - service defined in standard JSON RPC way, typically it's
            module name from spec-file followed by '.' and name of funcdef
            from spec-file corresponding to running method (e.g.
            'KBaseTrees.construct_species_tree' from trees service);
        job_id - job id if method is asynchronous (optional field).
    */
    typedef structure {
        timestamp time;
        string method;
        job_id job_id;
    } MethodCall;

    /*
        call_stack - upstream calls details including nested service calls and 
            parent jobs where calls are listed in order from outer to inner.
    */
    typedef structure {
        list<MethodCall> call_stack;
        string run_id;
    } RpcContext;

    /*
        method - service defined in standard JSON RPC way, typically it's
            module name from spec-file followed by '.' and name of funcdef 
            from spec-file corresponding to running method (e.g.
            'KBaseTrees.construct_species_tree' from trees service);
        params - the parameters of the method that performed this call;
        service_ver - specific version of deployed service, last version is used 
            if this parameter is not defined (optional field);
        rpc_context - context of current method call including nested call history
            (optional field, could be omitted in case there is no call history).
    */
    typedef structure {
        string method;
        list<UnspecifiedObject> params;
        string service_ver;
        RpcContext rpc_context;
    } RunJobParams;

    /* Start a new job */
    funcdef run_job(RunJobParams params) returns (job_id job_id) authentication required;

    /* Get job params necessary for job execution */
    funcdef get_job_params(job_id job_id) returns (RunJobParams params) authentication required;

    /* Error block of JSON RPC response */
    typedef structure {
        string name;
        int code;
        string message;
        string error;
    } JsonRpcError;

    /*
        Either 'result' or 'error' field should be defined;
        result - keeps exact copy of what original server method puts
            in result block of JSON RPC response;
        error - keeps exact copy of what original server method puts
            in error block of JSON RPC response.
    */
    typedef structure {
        UnspecifiedObject result;
        JsonRpcError error;
    } FinishJobParams;

    /* Register results of already started job */
    funcdef finish_job(job_id job_id, FinishJobParams params) returns () authentication required;

    /*
        finished - indicates whether job is done (including error cases) or not,
            if the value is true then either of 'returned_data' or 'detailed_error'
            should be defined;
        ujs_url - url of UserAndJobState service used by job service
        status - tuple returned by UserAndJobState.get_job_status method
        result - keeps exact copy of what original server method puts
            in result block of JSON RPC response;
        error - keeps exact copy of what original server method puts
            in error block of JSON RPC response.
    */
    typedef structure {
        boolean finished;
        string ujs_url;
        UnspecifiedObject status;
        UnspecifiedObject result;
        JsonRpcError error;
    } JobState;

    /* Check if job is finished and get results/error */ 
    funcdef check_job(job_id job_id) returns (JobState job_state) authentication required;
};