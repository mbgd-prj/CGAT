#/usr/bin/perl -s

###############################################################################
use File::Basename;
use File::Path;
require "CGAT_Conf.pl";

###############################################################################
package LogFile;
###############################################################################
sub start_log {
	my($file, $message) = @_;
	&openLogfile($file);
	my $date = localtime;
	print STDERR "### Start: $$: $date: $message\n";
}
sub openLogfile {
    my($file) = @_;

    File::Path::mkpath("$main::DIR_log", 0, 0750);

    open(BAKERR, ">&STDERR");

    if ($main::nolog) {
        return;
    }

    if (! $file) {
        $file = "$main::DIR_log/" .
		File::Basename::basename($0, (".pl")) . ".log";
    }
    open(STDERR, "| $main::CMD_tee -a $file 1>&2") || die("Can not open $file($!)");
}

###############################################################################
sub closeLogfile {
    close(STDERR);

    open(STDERR, ">&BAKERR");
}
sub end_log {
	my $date = localtime;
	print STDERR "### Done: $$: $date\n";
	&closeLogfile;
}

###############################################################################
1;#
###############################################################################
