#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use File::Path;
require "CGAT_Conf.pl";

###############################################################################
if ($0 eq __FILE__) {
    my($cmd);

    if($CMD_rsync) {
	$cmd = "$CMD_rsync -av $DIR_database.work/* $DIR_database";
    } else {
	$cmd = "cd $DIR_database.work; $CMD_tar cf - ./* | (cd $DIR_database; $CMD_tar xf -)";
    }

    print STDERR "Copy data file($DIR_database.work ---> $DIR_database)\n";
    print "CMD :: $cmd\n" if ($DEBUG);
    system("$cmd") if (! $DEBUG);

    exit;
}

###############################################################################
1;#
###############################################################################
