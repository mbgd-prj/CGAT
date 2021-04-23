#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use FileHandle;
use CGI;
require "CGAT_Conf.pl";

$| = 1;

###############################################################################
#

my($cgi);
my($type);
my($filename);

$cgi = CGI->new();

$type = basename($cgi->param("type"));
$filename = "$DIR_guiinfo/$type";
$filename =~ s#\s#_#g;

print $cgi->header("text/plain");

if (-e "$filename") {
	open(F, $filename) || die;
	while(<F>) {
		print;
	}
	close(F);
} elsif (-f "$DIR_commands/${type}.pl") {
	require "$DIR_commands/${type}.pl";
	$Opt = "GenomeFeatureCommand::${type}"->options;
	print "#CGI\t/dynSearch/${type}.cgi\n";
	foreach $opt (@{$Opt}) {
		my($name,$descr,$switch,$default,$type,$info_opt) = @{$opt};
		if ($type eq 'radio') {
			foreach my $o (@{$info_opt}) {
				print "$type\t$descr\t$name\t$o\n";
				$descr = ''; 
			}
		} elsif ($type eq 'textarea') {
			print "$type\t$descr\t$name\t$default\n";
		} else {
			$type = 'text' if (! $type);
			print "$type\t$descr\t$name\t$default\n";
		}
	}
} else {
	print "label", "\t", "a", "\t", "not found($filename)", "\n";
}

###############################################################################
1;#
###############################################################################
