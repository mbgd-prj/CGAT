#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use CGI;
require "CGAT_Conf.pl";

###############################################################################
if ($0 eq __FILE__) {
    my($cgi);
    my($pathInfo);
    my($type, $subType);
    my($sp1);
    my($type);
    my($dir);
    my($file);

    #
    $| = 1;

    # get args
    $cgi = CGI->new();
    @spList = $cgi->param("spec");
    ($type) = $cgi->param("type");

    print "Content-type: text/plain\n";
    print "\n";

    foreach $sp1 (@spList) {
        $file = &getGeneAttrFilePath($sp1, $type);

        $cmd = "$CMD_cat $file";
        system("$cmd");
    }

    exit;
}

###############################################################################
1;#
###############################################################################
