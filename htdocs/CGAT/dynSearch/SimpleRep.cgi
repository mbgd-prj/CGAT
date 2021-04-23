#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use lib "$ENV{'CGAT_HOME'}/commands";
use CGAT_CGI;
require "CGAT_Conf.pl";
require "SimpleRep.pl";

###############################################################################
$| = 1;

###############################################################################
$cgi = CGAT_CGI->new();
$opt = $cgi->getOpt;
print $cgi->header('text/plain');
$sp = delete $opt->{spec};
GenomeFeatureCommand::SimpleRep->execute($sp,$opt);
exit;
###############################################################################
1;#
###############################################################################
