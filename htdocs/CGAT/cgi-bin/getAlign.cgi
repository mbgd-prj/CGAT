#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use CGI;
require "CGAT_Conf.pl";

###############################################################################
if ($0 eq __FILE__) {
    my($cgi);
    my($type);
    my($sp1, $sp2);
    my($file);
    my($cmd);

    #
    $| = 1;

    # get args
    $cgi = CGI->new();
    ($type) = $cgi->param("type");
    ($sp1, $sp2) = $cgi->param("spec");

    #
    print "Content-type: text/plain\n";
    print "\n";

    #
    $file = &getAlignFilePath($sp1, $sp2, "align", $type);

    $cmd = "$CMD_cat $file";
    system("$cmd");

    exit;
}

###############################################################################
1;#
###############################################################################
