#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use lib "$ENV{'CGAT_HOME'}/commands";
use CGAT_CGI;
require "CGAT_Conf.pl";
require "Blast.pl";

###############################################################################
$| = 1;

###############################################################################
my($cgi) = CGAT_CGI->new();
my($opt) = $cgi->getOpt;
my $TMPOUT = "/tmp/tmpblast.$$";

open(O,">$TMPOUT");
if ($opt->{"queryseq"} !~ /^>/) {
	print O ">". $opt->{'queryname'} . "\n";
}
my($qseq) = $opt->{"queryseq"};
$qseq =~ s/[^[:alpha:]]//g;
print O $qseq . "\n";
close O;

print $cgi->header('text/plain');

GenomeFeatureCommand::Blast->execute($opt->{spec}, $TMPOUT, $opt);
unlink($TMPOUT);

exit(0);
###############################################################################
1;#
###############################################################################
