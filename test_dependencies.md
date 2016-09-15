Known test dependencies listed below. Please add more if discovered.
Must force RPC::Any, Moose updates cause XML tests to fail

pip install nose2 jsonrpcbase

cpan Moose
cpan -f -i RPC::Any
cpan Parse::Yapp Devel::StackTrace Lingua::EN::Inflect Template File::Slurp Cwd JSON Data::UUID XML::Dumper JSON::RPC::Client Exception::Class Config::Simple Digest::SHA1 Crypt::OpenSSL::RSA Convert::PEM DateTime MIME::Base64 URI Object::Tiny::RW Plack File::ShareDir::Install YAML TAP::Harness Plack::Middleware::CrossOrigin RPC::Any::Server::JSONRPC::PSGI
