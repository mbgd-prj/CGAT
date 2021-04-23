#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use lib "$ENV{'CGAT_HOME'}/commands";
use CGAT_CGI;
require "CGAT_Conf.pl";
require "PatSearch.pl";
###############################################################################
$| = 1;
my($cgi) = CGAT_CGI->new();
my($opt) = $cgi->getOpt;

print $cgi->header('text/plain');

GenomeFeatureCommand::PatSearch->execute($opt->{spec}, $opt);

exit;
###############################################################################
