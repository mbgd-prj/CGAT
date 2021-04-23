#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use DirHandle;
use File::Basename;
require "CGAT_Conf.pl";

###############################################################################
#
sub filelist {
    my($baseDir) = @_;
    my($dir);
    my($dh);
    my($file);

    $dir = "$baseDir/$DIR_geneattr";
    $dh = DirHandle->new("$dir") || die("Can not open $dir($!)");
    foreach $file ($dh->read()) {
        next if ($file =~ /^\./);
        next if (! -d "$dir/$file");

        print $file, "\t", $file, "\n";
    }
    return;
}


###############################################################################
if ($0 eq __FILE__) {
    #
    $| = 1;

    print "Content-type: text/plain\n";
    print "\n";

    &filelist("$main::DIR_database");

    exit;
}

###############################################################################
1;#
###############################################################################
