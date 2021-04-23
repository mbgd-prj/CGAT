#!/usr/bin/perl -s

###############################################################################
use SDBM_File;
use Fcntl;

sub mkAlignSeqIndex {
    my($file) = @_;
    my(%hashDb);
    my($fp);
    local(*FH);

    open(FH, "$file") || die("Can not open $file($!)");
    tie %hashDb, 'SDBM_File', "${file}.db",
		&Fcntl::O_WRONLY|&Fcntl::O_CREAT, 0755
		|| die("Can not dbmopen $file($!)");

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
    my($file);

    foreach $file (@ARGV) {
        &mkAlignSeqIndex($file);
    }

    exit;
}

###############################################################################
1;#
###############################################################################
