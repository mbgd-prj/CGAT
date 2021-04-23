#!/usr/bin/perl -s

###############################################################################
use lib "$ENV{'CGAT_HOME'}/perllib";
use DirHandle;
use File::Basename;
use SpecPairList;
require "CGAT_Conf.pl";

###############################################################################
#
sub filelist {
    my($baseDir) = @_;
    my($objSpecPair);
    my($dir);
    my($dh);
    my($file);
    my($sp1, $sp2);

#    $objSpecPair = new SpecPair("$main::FILE_SpecListPub");
    $objSpecPair = SpecPairList->new;

    $dir = "$baseDir/$DIR_align";
    $dh = DirHandle->new("$dir") || die("Can not open $dir($!)");
    my @files =  $dh->read();
    my @data;
    foreach my $file (@files) {
        next if ($file =~ /^\./);
        my ($prog, $sppair) = ($file =~ /(^\w+)\.(\S+)$/);
	push(@data, {prog=>$prog, sppair=>$sppair} );
    }
    foreach $d (sort by_spnames @data) {
	my $file = "$d->{prog}.$d->{sppair}";
        next if (! -f "$dir/$file");

        ($sp1, $sp2) = ($d->{sppair} =~ /^(\w+)-(\w+)$/);

        if ($objSpecPair->flag_public($sp1, $sp2)) {
            print $file, "\n";
        }
    }

    return;
}
sub by_spnames {
	$a->{sppair} cmp $b->{sppair};
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
