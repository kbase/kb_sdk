package Bio::KBase::DeploymentConfig;

use strict;
use base 'Class::Accessor';
use Config::Simple;

__PACKAGE__->mk_accessors(qw(service_name settings));

=head1 NAME

Bio::KBase::DeploymentConfig

=head1 DESCRIPTION

The C<Bio::KBase::DeploymentConfig> class wraps the access to a KBase deployment.cfg
file. It tests for the existence of the KB_DEPLOYMENT_CONFIG and 
KB_SERVICE_NAME environment variables; if both are present, the 
configuration parameters for the given service will be loaded 
from that config file. If they are not present, the module supports
fallback to defaults as defined by the module.

=head1 METHODS

=over 4

=cut

=item C<new>

    my $cfg = Bio::KBase::DeploymentConfig->new($service_name, { name => 'value', ...})

Create a new C<Bio::KBase::DeploymentConfig> instance.

Parameters:

=over 4

=item C<$service_name>

The name of this service, used as a default if C<KB_SERVICE_NAME> is not defined.

=item C<$defaults>

A hash reference containing the default values for the service parameters.

=back

=cut
   
sub new
{
    my($class, $service_name, $defaults) = @_;

    if ((my $n = $ENV{KB_SERVICE_NAME}) ne "")
    {
	$service_name = $n;
    }

    my $settings = {};
    if (ref($defaults))
    {
	%$settings = %$defaults;
    }

    my $cfg_file = $ENV{KB_DEPLOYMENT_CONFIG};
    if (-e $cfg_file)
    {
	my $cfg = Config::Simple->new();
	$cfg->read($cfg_file);

	my %cfg = $cfg->vars;

	for my $k (keys %cfg)
	{
	    if ($k =~ /^$service_name\.(.*)/)
	    {
		$settings->{$1} = $cfg{$k};
	    }
	}
    }

    my $self = {
	settings => $settings,
	service_name => $service_name,
    };

    return bless $self, $class;
}

=item C<setting>

Retrieve a setting from the configuration.

   my $value = $obj->setting("key-name");

=cut

sub setting
{
    my($self, $key) = @_;
    return $self->{settings}->{$key};
}

=item C<service_name>

Return the name of the service currently configured.

    my $name = $obj->service_name();

=cut

1;
