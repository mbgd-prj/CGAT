#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use CGI;
require "CGAT_Conf.pl";

###############################################################################
#
sub getfile {
    my($file) = @_;
    my($cmd);

    if (! -e "$file") {
        print "File not found($file)\n";
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

    # get args
    $cgi = CGI->new();

    print "Content-type: text/plain\n";
    print "\n";

    @spList = $cgi->param("spec");
    ($type) = $cgi->param("type");

    $dir = "$DIR_database/" . basename($type); 
    foreach $sp1 (@spList) {
        $file = "$dir/seg." . basename($sp1);
        &getfile("$file");
    }
}

###############################################################################
1;#
###############################################################################
