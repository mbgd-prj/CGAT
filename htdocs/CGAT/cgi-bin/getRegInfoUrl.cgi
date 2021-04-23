#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use CGI;
use File::Basename;
require "CGAT_Conf.pl";

###############################################################################
if ($0 eq __FILE__) {
    #
    $| = 1;

    my($cgi) = CGI->new();
    my($type) = $cgi->param("type");

    print "Content-type: text/html\n";
    print "\n";

    if ($type eq "gene") {
#        print &getGeneFilePath("", "url");
        print `$main::CMD_cat $main::DIR_database/$main::DIR_genes/_url`;
    }
    else {
#        print &getSegmentFilePath("", $type, "url");
        $path = sprintf("segment/%s/_url", $type);
        print `$main::CMD_cat $main::DIR_database/$path`;
    }

    exit;
}

###############################################################################
1;#
###############################################################################
