#
# Automatic test rig for KBase perl client generation.
#
#
#
#
use strict;
use warnings;

use JSON;
use Data::Dumper;
use Getopt::Long;

my $DESCRIPTION =
"
USAGE
      perl test-client.pl [options]
      
DESCRIPTION
      Conducts an automatic test of a perl client against the specified endpoint using
      methods and parameters indicated in the tests config file.
      
      --endpoint [url]      endpoint of the server to test
      --token [token]       token for testing authenticated calls
      --asyncchecktime [ms] time client waits every cycle of checking state of async methods 
      --package [package]   package with client module
      --method [method]     method to run
      --input [filename]    input file with parameters in JSON
      --output [filename]   output file with results in JSON
      --error [filename]    output file with text error message
      -h, --help            display this help message, ignore all arguments
";
      
# first parse options to get the testconfig file
my $verbose;
my $endpoint;

my $token;
my $async_job_check_time_ms;
my $client_module;
my $method;
my $input_filename;
my $output_filename;
my $error_filename;

my $help;

my $opt = GetOptions (
        "verbose|v" => \$verbose,
        "endpoint=s" => \$endpoint,
        "token=s" => \$token,
        "asyncchecktime=i" => \$async_job_check_time_ms,
        "help|h" => \$help,
        "package=s" => \$client_module,
        "method=s" => \$method,
        "input=s" => \$input_filename,
        "output=s" => \$output_filename,
        "error=s" => \$error_filename,
        );

if ($help) {
    print $DESCRIPTION."\n";
    exit 0;
}


# endpoint must be defined
if (!$endpoint) {
    fail("endpoint parameter must be defined");
    exit 1;
}

# package must be defined
if (!$client_module) {
    fail("package parameter must be defined");
    exit 1;
}

# method must be defined
if (!$method) {
    fail("method parameter must be defined");
    exit 1;
}

# input must be defined
if (!$input_filename) {
    fail("input parameter must be defined");
    exit 1;
}

# output must be defined
if (!$output_filename) {
    fail("output parameter must be defined");
    exit 1;
}

# error must be defined
if (!$error_filename) {
    fail("error parameter must be defined");
    exit 1;
}

#parse the tests
open(my $fh, "<", $input_filename);
my $input_string='';
while (my $line = <$fh>) {
    chomp $line;
    $input_string .= $line;
}
close($fh);
my $params = JSON->new->decode($input_string);

my $json = JSON->new->canonical;

eval("use $client_module;");
#instantiate an authenticated client and a nonauthenticated client
my $client;

if($token) {
    if ($async_job_check_time_ms) {
        $client=$client_module->new($endpoint, token=>$token, async_job_check_time_ms=>$async_job_check_time_ms);
    } else {
        $client=$client_module->new($endpoint, token=>$token);
    }
} else {
    $client = $client_module->new($endpoint,ignore_kbase_config=>1);
}

# loop over each test case and run it against the server.  We create a new client instance
# for each test
    
my @result;
no strict "refs";
eval {
    @result = $client->$method(@{$params});
};
if($@) {
    print "caught: $@";
    my $returnedErrorMsg = "$@";
    my $OUTFILE;
    open $OUTFILE, '>>', $error_filename;
    print { $OUTFILE } $returnedErrorMsg;
    close $OUTFILE;
} else {
    my $serialized_result = $json->encode(\@result);
    my $OUTFILE;
    open $OUTFILE, '>>', $output_filename;
    print { $OUTFILE } $serialized_result;
    close $OUTFILE;
}





