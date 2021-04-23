#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
require "CGAT_Conf.pl";

###############################################################################
$| = 1;
$BINDIR = '/db/project/MBGD/anal/bin';

###############################################################################
#
sub KmatchSearch {
    my($file, $sp, $opt) = @_;
    my(@Color) = ("#ff0000","#00ff00","#0000ff","#ffff00","#ff00ff","#00ffff");
    my(@fieldName);
    my($cmd);
    my($fh);
    my($chains);
    my($maxdiff);
    my($mindiff);
    my($mismatch);
    my($minmatchlen);
    my($misratio);
    my($cutoff_prob);

    @fieldName = ("sp", "from", "to", "dir", "color", "name");
    print "#", join("\t", @fieldName), "\n";

    $chains      = " " if (exists($opt->{'chains'}));
    $maxdiff     = $opt->{'maxdiff'};
    $mindiff     = $opt->{'mindiff'};
    $mismatch    = $opt->{'mismatch'};
    $minmatchlen = $opt->{'minmatchlen'};
    $misratio    = $opt->{'misratio'};
    $cutoff_prob = $opt->{'cutoff_prob'};

    # データ内容
    $opt = &create_option(["-C", $chains],
                          ["-D", $maxdiff],
                          ["-d", $mindiff],
                          ["-M", $mismatch],
                          ["-L", $minmatchlen],
                          ["-R", $misratio],
                          ["-P", $cutoff_prob]);

    #
#    $cmd = "$main::CMD_Kmatch $opt $file | $BINDIR/concat.pl";
    my($len) = &getSeqLen($file);
    $cmd = "$main::CMD_Kmatch $opt $file | $CMD_ConcatAlign -TOTAL_LEN=$len -MAXGAP=5 -";
print STDERR "$cmd\n";
    $fh = FileHandle->new("$cmd |") || die("Can not open $cmd($!)");
    while (<$fh>) {
        $repnum++;
        my($from1, $to1, $from2, $to2)= split;

        $color = $Color[$repnum%(0+@Color)];

        print join("\t", $sp, $from1, $to1,  1, $color, "rep_$repnum"), "\n";
        print join("\t", $sp, $from2, $to2, -1, $color, "rep_$repnum"), "\n";
    }
    $fh->close();

    return;
}

###############################################################################
#
sub SimpleSearch {
    my($file, $sp, $opt) = @_;
    my($objColorTab);
    my(@fieldName);
    my($cmd);
    my($fh);

    @fieldName = ("sp", "from", "to", "dir", "color", "name");
    print "#", join("\t", @fieldName), "\n";

    # ColorTab
    $objColorTab = ColorTab->new("$DIR_colortab/colorTab.Simple_search");

    $minoutrep     = $opt->{'minoutrep'};
    $cutoff        = $opt->{'cutoff'};
    $maxrep        = $opt->{'maxrep'};
    $matchscore    = $opt->{'matchscore'};
    $mismatchscore = $opt->{'mismatchscore'};

    # command options
    $opt = &create_option(["-m", $minoutrep],
                          ["-c", $cutoff],
                          ["-M", $maxrep],
                          ["-S", $matchscore],
                          ["-P", $mismatchscore]);

    #
    $cmd = "$main::CMD_SimRep $opt $file | $main::CMD_grep -v NNNN";
    $fh = FileHandle->new("$cmd |") || die("Can not open $cmd($!)");
    while (<$fh>) {
        my($dir) = 1;
        my($from1, $to1, $colVal, $name, $pat)= split;
        $color = $objColorTab->getColor($colVal);
        print join("\t", $sp, $from1, $to1, $dir, $color, $pat), "\n";
    }
    $fh->close();

    return;
}

###############################################################################
#
sub create_option {
    my(@opt_list) = @_;
    my($opt);

    foreach my $o (@opt_list) {
        ($switch, $value) = @{$o};
        $opt .= "${switch}${value} " if (${value} ne '');
    }

    return $opt;
}

###############################################################################
sub getSeqLen {
    my($file) = @_;
    my($fh);
    my($len) = 0;

    $fh = FileHandle->new("$file") || die("Can not open $file($!)");
    while(<$fh>) {
        next if (/^>/);

        chomp();
        $len += length($_);
    }
    $fh->close();

    return $len;
}

###############################################################################
if ($0 eq __FILE__) {
    exit;
}

###############################################################################
1;#
###############################################################################
