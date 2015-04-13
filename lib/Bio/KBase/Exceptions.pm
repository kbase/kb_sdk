package Bio::KBase::Exceptions;

use Exception::Class
    (
     Bio::KBase::Exceptions::KBaseException => {
	 description => 'KBase exception',
	 fields => ['method_name'],
     },

     Bio::KBase::Exceptions::JSONRPC => {
	 description => 'JSONRPC error',
	 fields => ['code', 'data'],
	 isa => 'Bio::KBase::Exceptions::KBaseException',
     },

     Bio::KBase::Exceptions::HTTP => {
	 description => 'HTTP error',
	 fields => ['status_line'],
	 isa => 'Bio::KBase::Exceptions::KBaseException',
     },

     Bio::KBase::Exceptions::ArgumentValidationError => {
	 description => 'argument validation error',
	 fields => ['method_name'],
	 isa => 'Bio::KBase::Exceptions::KBaseException',
     },

    );

Bio::KBase::Exceptions::KBaseException->Trace(1);

package Bio::KBase::Exceptions::HTTP;
use strict;

sub full_message
{
    my($self) = @_;
    return $self->message . "\nHTTP status: " . $self->status_line . "\nFunction invoked: " . $self->method_name;
}

package Bio::KBase::Exceptions::JSONRPC;
use strict;

sub full_message
{
    my($self) = @_;
    return sprintf("JSONRPC error:\n%s\nJSONRPC error code: %s\nJSONRPC error data:%s\n", $self->message, $self->code, $self->data);
}

1;
