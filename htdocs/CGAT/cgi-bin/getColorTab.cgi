#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use CGI;
require "CGAT_Conf.pl";

###############################################################################
#
sub getColorTabFile {
    my($file) = @_;
    my($cmd);

    if (! -e "$file") {
        print "# File not found($file)\n";
    }
    else {
        $cmd = "$main::CMD_cat $file";
        print `$cmd`;
    }

    return;
}

###############################################################################
if ($0 eq __FILE__) {
    my($cgi);
    my($type);
    my($sp1);
    my($type);
    my($dir);
    my($file);

    #
    $| = 1;

    # get args
    $cgi = CGI->new();

    print "Content-type: text/plain\n";
    print "\n";

    ($type) = $cgi->param("type");

    $file = "$main::DIR_colortab/colorTab." . basename($type); 
    &getColorTabFile("$file");

    exit;
}

###############################################################################
1;#
###############################################################################
