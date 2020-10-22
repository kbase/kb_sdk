package TestImpl;
use strict;
use Bio::KBase::Exceptions;

# Use Semantic Versioning (2.0.0-rc.1)
# http://semver.org
our $VERSION = "0.1.0";

=head1 NAME

Basic

=head1 DESCRIPTION



=cut

#BEGIN_HEADER
#END_HEADER

sub new {
    my ( $class, @args ) = @_;
    my $self = {};
    bless $self, $class;

    #BEGIN_CONSTRUCTOR
    #END_CONSTRUCTOR

    if ( $self->can( '_init_instance' ) ) {
        $self->_init_instance();
    }
    return $self;
}

=head1 METHODS



=head2 one_simple_param

  $return = $obj->one_simple_param($val)

=over 4

=item Parameter and return types

=begin html

<pre>
$val is an int
$return is an int

</pre>

=end html

=begin text

$val is an int
$return is an int


=end text



=item Description



=back

=cut

sub one_simple_param {
    my $self = shift;
    my ( $val ) = @_;

    my @_bad_arguments;
    ( !ref( $val ) )
        or
        push( @_bad_arguments, "Invalid type for argument \"val\" (value was \"$val\")" );
    if ( @_bad_arguments ) {
        my $msg = "Invalid arguments passed to one_simple_param:\n"
            . join( "", map { "\t$_\n" } @_bad_arguments );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'one_simple_param'
        );
    }

    my $ctx = $basicsrvServer::CallContext;
    my ( $return );

    #BEGIN one_simple_param
    $return = $val;

    #END one_simple_param
    my @_bad_returns;
    ( !ref( $return ) )
        or push( @_bad_returns,
        "Invalid type for return variable \"return\" (value was \"$return\")" );
    if ( @_bad_returns ) {
        my $msg = "Invalid returns passed to one_simple_param:\n"
            . join( "", map { "\t$_\n" } @_bad_returns );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'one_simple_param'
        );
    }
    return ( $return );
}

=head2 nothing

  $obj->nothing()

=over 4

=item Parameter and return types

=begin html

<pre>

</pre>

=end html

=begin text



=end text



=item Description



=back

=cut

sub nothing {
    my $self = shift;

    my $ctx = $basicsrvServer::CallContext;

    #BEGIN nothing
    #END nothing
    return ();
}

=head2 one_complex_param

  $return = $obj->one_complex_param($val2)

=over 4

=item Parameter and return types

=begin html

<pre>
$val2 is a Basic.complex_struct
$return is a Basic.complex_struct
complex_struct is a reference to a hash where the following keys are defined:
	large_prop1 has a value which is a reference to a list where each element is a Basic.simple_struct
	large_prop2 has a value which is a reference to a hash where the key is a string and the value is a Basic.simple_struct
	large_prop3 has a value which is a reference to a list where each element is a reference to a hash where the key is a string and the value is a reference to a list where each element is a Basic.simple_struct
	large_prop4 has a value which is a reference to a hash where the key is a string and the value is a reference to a list where each element is a reference to a hash where the key is a string and the value is a Basic.simple_struct
simple_struct is a reference to a hash where the following keys are defined:
	prop1 has a value which is a Basic.integer_number
	prop2 has a value which is a Basic.double_number
	prop3 has a value which is a string
integer_number is an int
double_number is a float

</pre>

=end html

=begin text

$val2 is a Basic.complex_struct
$return is a Basic.complex_struct
complex_struct is a reference to a hash where the following keys are defined:
	large_prop1 has a value which is a reference to a list where each element is a Basic.simple_struct
	large_prop2 has a value which is a reference to a hash where the key is a string and the value is a Basic.simple_struct
	large_prop3 has a value which is a reference to a list where each element is a reference to a hash where the key is a string and the value is a reference to a list where each element is a Basic.simple_struct
	large_prop4 has a value which is a reference to a hash where the key is a string and the value is a reference to a list where each element is a reference to a hash where the key is a string and the value is a Basic.simple_struct
simple_struct is a reference to a hash where the following keys are defined:
	prop1 has a value which is a Basic.integer_number
	prop2 has a value which is a Basic.double_number
	prop3 has a value which is a string
integer_number is an int
double_number is a float


=end text



=item Description



=back

=cut

sub one_complex_param {
    my $self = shift;
    my ( $val2 ) = @_;

    my @_bad_arguments;
    ( ref( $val2 ) eq 'HASH' )
        or push( @_bad_arguments,
        "Invalid type for argument \"val2\" (value was \"$val2\")" );
    if ( @_bad_arguments ) {
        my $msg = "Invalid arguments passed to one_complex_param:\n"
            . join( "", map { "\t$_\n" } @_bad_arguments );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'one_complex_param'
        );
    }

    my $ctx = $basicsrvServer::CallContext;
    my ( $return );

    #BEGIN one_complex_param
    $return = {};    #$val2;
                     #END one_complex_param
    my @_bad_returns;
    ( ref( $return ) eq 'HASH' )
        or push( @_bad_returns,
        "Invalid type for return variable \"return\" (value was \"$return\")" );
    if ( @_bad_returns ) {
        my $msg = "Invalid returns passed to one_complex_param:\n"
            . join( "", map { "\t$_\n" } @_bad_returns );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'one_complex_param'
        );
    }
    return ( $return );
}

=head2 many_simple_params

  $return_1, $return_2, $return_3, $return_4 = $obj->many_simple_params($val1, $val2, $val3, $val4)

=over 4

=item Parameter and return types

=begin html

<pre>
$val1 is an int
$val2 is a float
$val3 is a string
$val4 is a reference to a list containing 2 items:
	0: a reference to a list where each element is a reference to a hash where the key is a string and the value is an int
	1: a reference to a hash where the key is a string and the value is a reference to a list where each element is a float
$return_1 is an int
$return_2 is a float
$return_3 is a string
$return_4 is a reference to a list containing 2 items:
	0: a reference to a list where each element is a reference to a hash where the key is a string and the value is an int
	1: a reference to a hash where the key is a string and the value is a reference to a list where each element is a float

</pre>

=end html

=begin text

$val1 is an int
$val2 is a float
$val3 is a string
$val4 is a reference to a list containing 2 items:
	0: a reference to a list where each element is a reference to a hash where the key is a string and the value is an int
	1: a reference to a hash where the key is a string and the value is a reference to a list where each element is a float
$return_1 is an int
$return_2 is a float
$return_3 is a string
$return_4 is a reference to a list containing 2 items:
	0: a reference to a list where each element is a reference to a hash where the key is a string and the value is an int
	1: a reference to a hash where the key is a string and the value is a reference to a list where each element is a float


=end text



=item Description



=back

=cut

sub many_simple_params {
    my $self = shift;
    my ( $val1, $val2, $val3, $val4 ) = @_;

    my @_bad_arguments;
    ( !ref( $val1 ) )
        or push( @_bad_arguments,
        "Invalid type for argument \"val1\" (value was \"$val1\")" );
    ( !ref( $val2 ) )
        or push( @_bad_arguments,
        "Invalid type for argument \"val2\" (value was \"$val2\")" );
    ( !ref( $val3 ) )
        or push( @_bad_arguments,
        "Invalid type for argument \"val3\" (value was \"$val3\")" );
    ( ref( $val4 ) eq 'ARRAY' )
        or push( @_bad_arguments,
        "Invalid type for argument \"val4\" (value was \"$val4\")" );
    if ( @_bad_arguments ) {
        my $msg = "Invalid arguments passed to many_simple_params:\n"
            . join( "", map { "\t$_\n" } @_bad_arguments );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'many_simple_params'
        );
    }

    my $ctx = $basicsrvServer::CallContext;
    my ( $return_1, $return_2, $return_3, $return_4 );

    #BEGIN many_simple_params
    $return_1 = $val1;
    $return_2 = $val2;
    $return_3 = $val3;
    $return_4 = $val4;

    #END many_simple_params
    my @_bad_returns;
    ( !ref( $return_1 ) )
        or push( @_bad_returns,
        "Invalid type for return variable \"return_1\" (value was \"$return_1\")" );
    ( !ref( $return_2 ) )
        or push( @_bad_returns,
        "Invalid type for return variable \"return_2\" (value was \"$return_2\")" );
    ( !ref( $return_3 ) )
        or push( @_bad_returns,
        "Invalid type for return variable \"return_3\" (value was \"$return_3\")" );
    ( ref( $return_4 ) eq 'ARRAY' )
        or push( @_bad_returns,
        "Invalid type for return variable \"return_4\" (value was \"$return_4\")" );
    if ( @_bad_returns ) {
        my $msg = "Invalid returns passed to many_simple_params:\n"
            . join( "", map { "\t$_\n" } @_bad_returns );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'many_simple_params'
        );
    }
    return ( $return_1, $return_2, $return_3, $return_4 );
}

=head2 many_complex_params

  $return_1, $return_2 = $obj->many_complex_params($simple_val, $complex_val)

=over 4

=item Parameter and return types

=begin html

<pre>
$simple_val is a Basic.simple_struct
$complex_val is a Basic.complex_struct
$return_1 is a Basic.simple_struct
$return_2 is a Basic.complex_struct
simple_struct is a reference to a hash where the following keys are defined:
	prop1 has a value which is a Basic.integer_number
	prop2 has a value which is a Basic.double_number
	prop3 has a value which is a string
integer_number is an int
double_number is a float
complex_struct is a reference to a hash where the following keys are defined:
	large_prop1 has a value which is a reference to a list where each element is a Basic.simple_struct
	large_prop2 has a value which is a reference to a hash where the key is a string and the value is a Basic.simple_struct
	large_prop3 has a value which is a reference to a list where each element is a reference to a hash where the key is a string and the value is a reference to a list where each element is a Basic.simple_struct
	large_prop4 has a value which is a reference to a hash where the key is a string and the value is a reference to a list where each element is a reference to a hash where the key is a string and the value is a Basic.simple_struct

</pre>

=end html

=begin text

$simple_val is a Basic.simple_struct
$complex_val is a Basic.complex_struct
$return_1 is a Basic.simple_struct
$return_2 is a Basic.complex_struct
simple_struct is a reference to a hash where the following keys are defined:
	prop1 has a value which is a Basic.integer_number
	prop2 has a value which is a Basic.double_number
	prop3 has a value which is a string
integer_number is an int
double_number is a float
complex_struct is a reference to a hash where the following keys are defined:
	large_prop1 has a value which is a reference to a list where each element is a Basic.simple_struct
	large_prop2 has a value which is a reference to a hash where the key is a string and the value is a Basic.simple_struct
	large_prop3 has a value which is a reference to a list where each element is a reference to a hash where the key is a string and the value is a reference to a list where each element is a Basic.simple_struct
	large_prop4 has a value which is a reference to a hash where the key is a string and the value is a reference to a list where each element is a reference to a hash where the key is a string and the value is a Basic.simple_struct


=end text



=item Description



=back

=cut

sub many_complex_params {
    my $self = shift;
    my ( $simple_val, $complex_val ) = @_;

    my @_bad_arguments;
    ( ref( $simple_val ) eq 'HASH' )
        or push( @_bad_arguments,
        "Invalid type for argument \"simple_val\" (value was \"$simple_val\")" );
    ( ref( $complex_val ) eq 'HASH' )
        or push( @_bad_arguments,
        "Invalid type for argument \"complex_val\" (value was \"$complex_val\")" );
    if ( @_bad_arguments ) {
        my $msg = "Invalid arguments passed to many_complex_params:\n"
            . join( "", map { "\t$_\n" } @_bad_arguments );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'many_complex_params'
        );
    }

    my $ctx = $basicsrvServer::CallContext;
    my ( $return_1, $return_2 );

    #BEGIN many_complex_params
    $return_1 = $simple_val;
    $return_2 = $complex_val;

    #END many_complex_params
    my @_bad_returns;
    ( ref( $return_1 ) eq 'HASH' )
        or push( @_bad_returns,
        "Invalid type for return variable \"return_1\" (value was \"$return_1\")" );
    ( ref( $return_2 ) eq 'HASH' )
        or push( @_bad_returns,
        "Invalid type for return variable \"return_2\" (value was \"$return_2\")" );
    if ( @_bad_returns ) {
        my $msg = "Invalid returns passed to many_complex_params:\n"
            . join( "", map { "\t$_\n" } @_bad_returns );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'many_complex_params'
        );
    }
    return ( $return_1, $return_2 );
}

=head2 with_auth

  $return = $obj->with_auth($val1)

=over 4

=item Parameter and return types

=begin html

<pre>
$val1 is an int
$return is an int

</pre>

=end html

=begin text

$val1 is an int
$return is an int


=end text



=item Description



=back

=cut

sub with_auth {
    my $self = shift;
    my ( $val1 ) = @_;

    my @_bad_arguments;
    ( !ref( $val1 ) )
        or push( @_bad_arguments,
        "Invalid type for argument \"val1\" (value was \"$val1\")" );
    if ( @_bad_arguments ) {
        my $msg = "Invalid arguments passed to with_auth:\n"
            . join( "", map { "\t$_\n" } @_bad_arguments );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'with_auth'
        );
    }

    my $ctx = $basicsrvServer::CallContext;
    my ( $return );

    #BEGIN with_auth
    $return = $val1;

    #END with_auth
    my @_bad_returns;
    ( !ref( $return ) )
        or push( @_bad_returns,
        "Invalid type for return variable \"return\" (value was \"$return\")" );
    if ( @_bad_returns ) {
        my $msg = "Invalid returns passed to with_auth:\n"
            . join( "", map { "\t$_\n" } @_bad_returns );
        Bio::KBase::Exceptions::ArgumentValidationError->throw(
            error       => $msg,
            method_name => 'with_auth'
        );
    }
    return ( $return );
}

=head2 version

  $return = $obj->version()

=over 4

=item Parameter and return types

=begin html

<pre>
$return is a string
</pre>

=end html

=begin text

$return is a string

=end text

=item Description

Return the module version. This is a Semantic Versioning number.

=back

=cut

sub version {
    return $VERSION;
}

=head1 TYPES



=head2 integer_number

=over 4



=item Definition

=begin html

<pre>
an int
</pre>

=end html

=begin text

an int

=end text

=back



=head2 double_number

=over 4



=item Definition

=begin html

<pre>
a float
</pre>

=end html

=begin text

a float

=end text

=back



=head2 simple_struct

=over 4



=item Description

Simple structure


=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
prop1 has a value which is a Basic.integer_number
prop2 has a value which is a Basic.double_number
prop3 has a value which is a string

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
prop1 has a value which is a Basic.integer_number
prop2 has a value which is a Basic.double_number
prop3 has a value which is a string


=end text

=back



=head2 complex_struct

=over 4



=item Definition

=begin html

<pre>
a reference to a hash where the following keys are defined:
large_prop1 has a value which is a reference to a list where each element is a Basic.simple_struct
large_prop2 has a value which is a reference to a hash where the key is a string and the value is a Basic.simple_struct
large_prop3 has a value which is a reference to a list where each element is a reference to a hash where the key is a string and the value is a reference to a list where each element is a Basic.simple_struct
large_prop4 has a value which is a reference to a hash where the key is a string and the value is a reference to a list where each element is a reference to a hash where the key is a string and the value is a Basic.simple_struct

</pre>

=end html

=begin text

a reference to a hash where the following keys are defined:
large_prop1 has a value which is a reference to a list where each element is a Basic.simple_struct
large_prop2 has a value which is a reference to a hash where the key is a string and the value is a Basic.simple_struct
large_prop3 has a value which is a reference to a list where each element is a reference to a hash where the key is a string and the value is a reference to a list where each element is a Basic.simple_struct
large_prop4 has a value which is a reference to a hash where the key is a string and the value is a reference to a list where each element is a reference to a hash where the key is a string and the value is a Basic.simple_struct


=end text

=back



=cut

1;

