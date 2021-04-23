#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use CGI;
require "CGAT_Conf.pl";

###############################################################################
if ($0 eq __FILE__) {
    my($cgi);
    my(@spList);
    my($type);
    my($sp1);
    my($file);
    my($cmd);

    #
    $| = 1;

    # get args
    $cgi = CGI->new();

    print "Content-type: text/plain\n";
    print "\n";

    @spList = $cgi->param("spec");
    ($type) = $cgi->param("type");

    foreach $sp1 (@spList) {
        $file = &getSegmentFilePath($sp1, $type);

        $cmd = "$main::CMD_cat $file";
        system("$cmd");
    }
}

###############################################################################
1;#
###############################################################################
