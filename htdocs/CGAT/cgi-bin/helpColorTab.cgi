#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use CGI;
use ColorTab;
require "CGAT_Conf.pl";

###############################################################################
if ($0 eq __FILE__) {
    my($cgi);
    my($type);

    #
    $| = 1;

    # get args
    $cgi = CGI->new();

    print "Content-type: text/html\n";
    print "\n";

    ($type) = $cgi->param("type");

    $colTab = ColorTab->new("$main::DIR_colortab/colorTab.$type");

    print "<html>\n";
    print "<head>\n";
    print "<title>ColorTab</title>\n";
    print "</head>\n";

    print "<body>\n";

    print "<h1>$type</h1>\n";

    $colTab->helpHtml();

    print "</body>\n";
    print "</html>\n";

    exit;
}

###############################################################################
1;#
###############################################################################
