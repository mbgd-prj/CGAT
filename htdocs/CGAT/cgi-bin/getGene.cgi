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
    my($sp1);
    my($file);
    my($cmd);

    #
    $| = 1;

    # get args
    $cgi = CGI->new();
    @spList = $cgi->param("spec");

    print "Content-type: text/plain\n";
    print "\n";

    foreach $sp1 (@spList) {
        $file = &getGeneFilePath($sp1, "region");

        $cmd = "$main::CMD_cat $file";
        system("$cmd");
    }

    exit;
}

###############################################################################
1;#
###############################################################################
