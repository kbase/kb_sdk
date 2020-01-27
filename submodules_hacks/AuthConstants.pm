package Bio::KBase::AuthConstants;

use strict;
use warnings;

use base 'Exporter';

use constant {
    globus_token_url    =>
        'https://nexus.api.globusonline.org/goauth/token?grant_type=client_credentials',

    globus_profile_url  => 'https://nexus.api.globusonline.org/users',

    role_service_url    => 'https://kbase.us/services/authorization/Roles',
};

use constant trust_token_signers =>
        split /\s+/, 'https://nexus.api.globusonline.org/goauth/keys';

our @EXPORT_OK = qw(
    globus_profile_url
    globus_token_url
    role_service_url
    trust_token_signers
);

our %EXPORT_TAGS = (
    globus  => [ qw( globus_token_url globus_profile_url trust_token_signers ) ],
    kbase   => [ qw( role_service_url ) ],
    all     => [ @EXPORT_OK ],
);

1;

