use Test::Most;
use Test::Compile;

my $base_dir    = '/kb/module/';
my $tester      = Test::Compile->new();

# check all .pm and .pl files compile
$tester->all_files_ok( $base_dir );

# also check .t and .psgi files
my @all_ext_files = files_with_ext( $tester, qr/\.(t|psgi)$/, $base_dir );

for ( @all_ext_files ) {
    ok $tester->pl_file_compiles( $_ ), $_ . ' compiles';
}

$tester->done_testing();

sub files_with_ext {
    my ( $tester, $regex, @dirs ) = @_;

    @dirs = @dirs ? @dirs : ( $base_dir );

    my @files;
    for my $file ( $tester->_find_files( @dirs ) ) {
        push @files, $file if $file =~ $regex;
    }
    return @files;
}
