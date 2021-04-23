#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use File::Basename;
use File::Path;
use FileHandle;
#use GDBM_File;
use SDBM_File;
use CGI;
require "CGAT_Conf.pl";

$| = 1;

###############################################################################
#
sub getAlignSeq {
    my($file, $sp1, $from1, $to1, $sp2, $from2, $to2) = @_;
    my($dir);
    my($alignDir);
    my($alignSeq);
    my($filename, $filename1, $filename2);
    my($formatType);
    my(%hashDb);
    my($fp);
    my($i);
    my($fh);

    #
    $file = basename($file);
    $sp1  = basename($sp1);
    $sp2  = basename($sp2);

    $dir = "$main::DIR_database";
    $filename1 = &getAlignFilePath($sp1, $sp2, "alignSeq", $file);
    $filename2 = &getAlignFilePath($sp2, $sp1, "alignSeq", $file);

    #     $formatType = 0;    #
    #     $formatType = 1;    #
    if (-e "$filename1") {
        $filename  = $filename1;
        $formatType = 1;	##sp1-sp2
    }
    elsif (-e "$filename2") {
        $filename  = $filename2;
        $formatType = 0;	##sp2-sp1
    }
    else {
        return("# no data file : $filename1");
    }

#    if ((! -e "$filename.pag") ||                    # no db-file
#        (-M "$filename" < -M "$filename.pag")) {     # db-file is older
#        &mkAlignSeqIndex($filename);
#    }

    # open hash
#    tie %hashDb, 'GDBM_File', "${filename}.db", &GDBM_File::GDBM_WRCREAT, 0640
#			|| die("Can not dbmopen $filename($!)");
    tie %hashDb, 'SDBM_File', "${filename}.db", O_RDONLY, 0640
			|| die("Can not dbmopen $filename($!)");
    $fp = $hashDb{"$from1-$to1:$from2-$to2"};
    if (! defined($fp)) {
        $fp = $hashDb{"$from2-$to2:$from1-$to1"};
    }
    untie %hashDb;

    if (! defined($fp)) {
        return "Not found alignSeq [$from1-$to1:$from2-$to2] : $filename\n";
    }

    $fh = new FileHandle("$filename") || die "Can not open $filename($!)";
    seek($fh, $fp, 0);
    ($alignDir) = (<$fh> =~ /^\d+\s+\d+\s+\d+\s+\d+\s+(\S+)\s+/);
    $alignSeq = '';

    $flagFound = 0;
    while(<$fh>) {
        if (/^\d+/) {
            last;
        }

        # alignSeq
        $alignSeq .= $_;
    }
    $fh->close();
    return $alignSeq;

#
#    #
#    my($idx1);
#    my($idx2);
#
#    #
#    my(@line) = split("\n", $alignSeq);
#    my($seq1, $seq2);
#
#
#    for($i = 0; $i < scalar(@line); $i += 4) {
#        $line[$i + 0] =~ s#^\s+\S+\s+\d+\s+##;
#        $line[$i + 2] =~ s#^\s+\S+\s+\d+\s+##;
#
#        $seq1 .= $line[$i + 0];
#        $seq2 .= $line[$i + 2];
#    }
#
#    #
#    if (! $formatType) {
#        my($wk);
#        $wk   = $seq1;
#        $seq1 = $seq2;
#        $seq2 = $wk;
#        $idx1 = $from2;
#        $idx2 = $from1;
#
##        if ($alignDir =~ /inv/i) {
##            $seq1 = reverse($seq1);
##            $seq2 = reverse($seq2);
##            $idx2 = $to1;
##        }
#    }
#    else{
#        $idx1 = $from1;
#        $idx2 = $from2;
##        if ($alignDir =~ /inv/i) {
##            $idx2 = $to2;
##        }
#    }
#
#    #
#    $alignSeq = '';
#    my($mat) = '';
#    my(@seq1) = split(//, $seq1);
#    my(@seq2) = split(//, $seq2);
#    for($i = 0; $i < length($seq1); $i++) {
#        if ($seq1[$i] eq $seq2[$i]) {
#            $mat .= ":";
#        }
#        else {
#            $mat .= " ";
#        }
#    }
#    for($i = 0; $i < length($seq1); $i += 60) {
#        $alignSeq .= sprintf("%20s %s\n", "$sp1 $idx1", substr($seq1, $i, 60));
#        $alignSeq .= sprintf("%20s %s\n", "",           substr($mat,  $i, 60));
#        $alignSeq .= sprintf("%20s %s\n", "$sp2 $idx2", substr($seq2, $i, 60));
#        $alignSeq .= "\n";
#
#        $idx1 += 60;
##        if ($alignDir =~ /inv/i) {
##            $idx2 -= 60;
##        }
##        else {
##            $idx2 += 60;
##        }
#	$idx2 += 60;
#    }
#
#    return($alignSeq);
}

###############################################################################
#
sub mkAlignSeqIndex {
    my($file) = @_;
    my(%hashDb);
    my($fp);
    local(*FH);

    open(FH, "$file") || die("Can not open $file($!)");
    dbmopen(%hashDb, "${file}_dbm", 0750) || die("Can not dbmopen $file($!)");

    $fp = tell(FH);    # ファイルの先頭位置
    while(<FH>) {
        if (/^(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+/) {
            my($from1, $to1) = ($1, $2);
            my($from2, $to2) = ($3, $4);

            # アライメントの位置をキーにファイルポインタを DBM に格納
            $hashDb{"$from1-$to1:$from2-$to2"} = $fp;
        }
        $fp = tell(FH); # 次の行の先頭位置
    }

    dbmclose(%hashDb);
    close(FH);
}

###############################################################################
if ($0 eq __FILE__) {
    my($alignSeq);
    my($file);
    my($reg1, $sp1, $from1, $to1);
    my($reg2, $sp2, $from2, $to2);
    my($cgi);

    # get args
    $cgi = CGI->new();

    $file = $cgi->param("file");
    if ($file =~ /^\s*$/) {
        $file = "align";
    }
    ($reg1, $reg2) = $cgi->param("reg");
    ($sp1, $from1, $to1) = ($reg1 =~ /^([^:]+):(\d+)-(\d+)/);
    ($sp2, $from2, $to2) = ($reg2 =~ /^([^:]+):(\d+)-(\d+)/);

    #
    $sp1 = basename($sp1);
    $sp2 = basename($sp2);

    print "Content-type: text/plain\n";
    print "\n";

    $alignSeq = &getAlignSeq($file,
                             $sp1, $from1, $to1,
                             $sp2, $from2, $to2);

    print $alignSeq;

    exit;
}

###############################################################################
1;#
###############################################################################
