#!/usr/bin/perl -s

###############################################################################
require "CGAT_commonPath.pl";
package CGAT_Data;

my $MODE;

sub set_mode {
	my($mode) = @_;
	$MODE = $mode;
}
sub getGeneNtSeqPath {
	my($sp,$mode) = @_;
	$mode = $MODE if (! $mode);
	return main::getGeneFilePath($sp, 'ntseq', $mode);
}
sub getGeneAaSeqPath {
	my($sp,$mode) = @_;
	$mode = $MODE if (! $mode);
	return main::getGeneFilePath($sp, 'aaseq', $mode);
}
sub getGenomeSeqPath {
	my($sp,$mode) = @_;
	$mode = $MODE if (! $mode);
	return main::getGenomeFilePath($sp, '', $mode);
}

###############################################################################
package main;

use File::Basename;
use File::Path;
###############################################################################
sub absolutePath {
	my($file, $opt) = @_;
	my($ret_fname);
	if ($opt eq 'update') {
		$ret_fname = $DIR_databaseWork . "/" . $file;
		if (! -f $ret_fname) {
			$ret_fname =  $DIR_database . "/" . $file;
		}
	} elsif ($opt eq 'work') {
		$ret_fname = $DIR_databaseWork . "/" . $file;
	} elsif ($opt eq 'tmpwork') {
		$ret_fname = $DIR_work . "/" . $file;
	} elsif ($opt eq 'current' || $file !~ /^\//) {
		$ret_fname = $DIR_database . "/" . $file;
	} else {
		$ret_fname = $file;
	}
	if ($SYS_name =~ /cygwin/i && $CMD_cygpath) {
		$ret_fname = `$CMD_cygpath -m $ret_fname`;
		chomp $ret_fname;
	}
	return $ret_fname;
}

sub getGenomeFilePath {
    my($sp, $type, $opt) = @_;
    my($path);
    $type = lc($type);
    $sp = File::Basename::basename($sp);

    if (! $type) {
        $path = sprintf("$DIR_genomeseq/%s", $sp);
    } else {
        $path = sprintf("$DIR_genomeseq/%s.%s", $type, $sp);
    }

    return &absolutePath($path, $opt);
}

###############################################################################
sub getGeneFilePath {
    my($sp, $type, $opt) = @_;
    $type = lc($type);
    $sp = basename($sp);

    if (! $type || $type eq 'ntseq') {
        $path = "$DIR_ntgeneseq/$sp";
    } elsif ($type eq 'aaseq') {
        $path = "$DIR_aageneseq/$sp";
    } elsif ($type =~ /^region$/i) {
        $path = sprintf("$DIR_genetab/%s", $sp);
    } elsif ($type =~ /^url$/i) {
        $path = `$main::CMD_cat $main::DIR_database/$main::DIR_genes/_url`;
    } else {
        $path = sprintf("$DIR_genes/%s/%s", $type, $sp);
    }

    return &absolutePath($path, $opt);
}

###############################################################################
#
sub getGeneAttrFilePath {
    my($sp, $type, $opt) = @_;
#    $type = lc($type);
    $sp = basename($sp);

    $path = sprintf("geneattr/%s/attr.%s", $type, $sp);

    return &absolutePath($path, $opt);
}

###############################################################################
#
sub getAlignFilePath {
    my($sp1, $sp2, $type, $alignType, $opt) = @_;
#    $type = lc($type);
    $alignType = lc($alignType);
    $sp1 = basename($sp1);
    $sp2 = basename($sp2);

    $path = sprintf("%s/%s.%s-%s", $type, $alignType, $sp1, $sp2);

    return &absolutePath($path, $opt);
}

###############################################################################
#
sub getSegmentFilePath {
    my($sp, $type, $segType, $opt) = @_;
#    $type = lc($type);
    $segType = lc($segType);
    $sp = File::Basename::basename($sp);

    if (! $segType) {
        $path = sprintf("segment/%s/%s.%s", $type, "seg", $sp);
    } elsif ($segType =~ /^url$/i) {
        $path = sprintf("segment/%s/_url", $type);
        $path = `$main::CMD_cat $main::DIR_database/$path`;
    } else {
        $path = sprintf("segment/%s/%s.%s", $type, $segType, $sp);
    }

    return &absolutePath($path, $opt);
}
sub getAllGenomes {
	my(@genomelist);
	opendir(D, &absolutePath($main::DIR_genomeseq, 'current'));
	while (my $f = readdir(D)) {
		$fname = File::Basename::basename($f);
		if ($fname !~ /\./) {
			push(@genomelist, $fname);
		}
	}
	@genomelist;
}
sub mkFilePath {
	mkpath(["$DIR_database/$f", "$DIR_databaseWork/$f"]);
	foreach $f ($DIR_align, $DIR_alignSeq, $DIR_geneattr, $DIR_segment){
		mkpath("$DIR_database/$f");
		mkpath("$DIR_databaseWork/$f");
	}
	foreach $f ($DIR_genomeseq, $DIR_genes ,$DIR_ntgeneseq, $DIR_aageneseq, $DIR_genetab) {
		mkpath("$DIR_database/$f");
		mkpath("$DIR_databaseWork/$f");
	}
}
###############################################################################
1;#
###############################################################################
