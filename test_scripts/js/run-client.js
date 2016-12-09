//
// usage:
//   phantomjs test-client.js --jq=[jquery_lib_path] --input=[input_file] --output=[output_file] --error=[error_file] --package=[package] --class=[class] --method=[method] --endpoint=[server_url] --token=[token] --asyncchecktime=[ms]
//
//
try {
    var system = require('system');
    var args = system.args;
    var cli = {};
    args.forEach(function(arg, i) {
        // skip first arg which is script name
        if (i != 0) {
            var pos = arg.indexOf('=');
            if (pos < 0)
                console.log('Arguement has wrong format: ' + arg);
            var bits = [arg.substring(0, pos), arg.substring(pos + 1)];
            var argVar = bits[0].replace(/\-/g, '_');
            argVar = argVar.replace(/__/, '');
            cli[argVar] = bits[1];
        }
    });

//  Read the input and make sure we have the right things
    var fs = require('fs');
    var params = JSON.parse(fs.read(cli['input']));

//  load the JQuery and the JS file
    phantom.injectJs(cli['jq']);
    phantom.injectJs(cli['package']+".js")

//  instantiate the client
    var className = cli['class'];
    var client = null;
    if (cli['token']) {
        client = new window[className](cli['endpoint'],
                {token:cli['token']}, undefined, undefined, cli['asyncchecktime']);
    } else {
        client = new window[className](cli['endpoint']);
    }

    var method = cli['method'];
    params.push(function(result) {
        console.log('# receiving success response for: ' + method);
        var text = JSON.stringify(result);
        fs.write(cli['output'], text, "w");
        phantom.exit();
    });
    params.push(function(err) {
        console.log('# receiving error response for: ' + method);
        var text = err.error.message;
        fs.write(cli['error'], text, "w");
        phantom.exit();
    });
    client[method].apply(this,params);
} catch(err) {
    var text = err.stack;
    console.log(err);
    fs.write(cli['error'], text, "w");
    phantom.exit();
}


