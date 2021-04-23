#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use FileHandle;
use CGI;
require "CGAT_Conf.pl";

###############################################################################
#
sub getSequence {
    my($sp, $from, $to) = @_;
    my($file);
    my($filename);
    my($sequence);
    my($title, $seq);
    my($fh);

    $filename = &getGenomeFilePath($sp);
    if (-e "$filename") {
        $fh = new FileHandle("$filename") || die "Can not open $filename($!)";
    }
    else {
        return("# open error");
    }

    $title = "> no title";
    $sequence = '';
    while(<$fh>) {
        if (/^>/) {
            $title = $_;
            next;
        }

        chomp();
        $sequence .= $_;
    }
    $fh->close();

    if (! $from) {
        $seq = $sequence;
        $seq =~ s#(.{1,60})#$1\n#g;
        $seq = $title . $seq;
    }
    elsif (! $to) {
        $seq = substr($sequence, $from - 1);
    }
    else {
        $seq = substr($sequence, $from - 1, $to - $from + 1);
    }

    return($seq);
}

###############################################################################
if ($0 eq __FILE__) {
    my($sp1, $from1, $to1, $seq1);

    #
    $| = 1;

    if (! $reg1) {
        my($cgi);
        my(%args);

        # get args
        $cgi = CGI->new();
        ($reg1) = $cgi->param("reg");
    }

    ($sp, $reg) = split(/:/, $reg1);
    if (! $sp) {
        $sp = $reg1;
    }
    ($from1, $to1) = ($reg =~ /^(\d+)-(\d+)/);

    print "Content-type: text/plain\n";
    print "\n";

    $seq1 = &getSequence($sp, $from1, $to1);

    print "$seq1\n";

    exit;
}

###############################################################################
1;#
###############################################################################
