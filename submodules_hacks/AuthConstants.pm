package Bio::KBase::AuthConstants;

use constant        globus_token_url   => 'https://nexus.api.globusonline.org/goauth/token?grant_type=client_credentials';
use constant        globus_profile_url   => 'https://nexus.api.globusonline.org/users';
use constant	    trust_token_signers => split(/\s+/, 'https://nexus.api.globusonline.org/goauth/keys');
use constant	    role_service_url => 'https://kbase.us/services/authorization/Roles';

use base 'Exporter';
our @EXPORT_OK = qw(globus_token_url globus_profile_url trust_token_signers role_service_url);
our %EXPORT_TAGS = ( 
		    globus => [ qw(globus_token_url globus_profile_url trust_token_signers) ],
		    kbase  => [ qw(role_service_url) ],
		   );
{
    my %seen;
    
    push @{$EXPORT_TAGS{all}},
    grep {!$seen{$_}++} @{$EXPORT_TAGS{$_}} foreach keys %EXPORT_TAGS;
}

1;

