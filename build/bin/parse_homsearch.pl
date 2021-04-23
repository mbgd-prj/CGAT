#!/usr/bin/perl -s

use Tools;
use Alignment;
use Tools::ReadHomologyData;
use Debug;
require 'CGAT_Conf.pl';

$LARGEGAP = 10 if (! defined $LARGEGAP);
$MINSCORE = 10 if (! defined $MINSCORE);

&Debug::debug_init;
if (! $classname) {
	$classname = "Blast"; 	## default
}
$assign_best = 1 if (! $skip_best);
if ( ($sp1 && $sp2) || ($seq1 && $seq2) ) {
	## OK
} else {
	die "Usage: $0 [-sp1=# -sp2=# | -seq1=# -seq2=#] -classname=# inputfile\n";
}
my(%parserOpt) = (
	classname=>$classname, sp1=>$sp1, sp2=>$sp2,
	seqfile1=>$seq1, seqfile2=>$seq2, infile=>\@ARGV,
	assign_best=>$assign_best,
	maskRegion=>$maskRegion,
);
$parserOpt{FinalOverlapCheck} = 1 if ($FinalOverlapCheck);
$parserOpt{SkipFinalOverlapCheck} = 1 if ($SkipFinalOverlapCheck);
$AllAlignList = Tools::ReadHomologyData::read(%parserOpt);

$alignInfoOut = "&STDOUT" if ($alignInfoOut eq '1');
$alignSeqOut = "&STDOUT" if ($alignSeqOut eq '1');
$FH_aliReg = FileHandle->new(">$alignRegOut") if ($alignRegOut);
$FH_aliSeq = FileHandle->new(">$alignSeqOut") if ($alignSeqOut);
$FH_aliInfo = FileHandle->new(">$alignInfoOut") if ($alignInfoOut);

if ($LARGEGAP) {
	if ($alignInfoOut && $SAVE_BEFORE_SPLIT) {
		$l->print_align_info($FH_aliInfo);
	}

	$NewAllAlignList = SegmentPairList->new;
	foreach my $ali ($AllAlignList->list) {
		foreach my $a ($ali->split_alignment($LARGEGAP,$MINSCORE)) {
			$NewAllAlignList->add($a);
		}
	}
	$AllAlignList = $NewAllAlignList;
}
$AllAlignList->sort_by_from;
foreach $l ($AllAlignList->list) {
	next if ($l->{delete});

#	$l->print_region($FH_aliReg, ['bestflag','sumscore']);
	$l->print_region($FH_aliReg, ['bestflag']);
	if ($alignInfoOut && ! $SAVE_BEFORE_SPLIT) {
		$l->print_align_info($FH_aliInfo);
	}
	if ($alignRegOut) {
		$l->print_region($FH_aliSeq);
	}
	if ($alignSeqOut) {
		$l->print_alignment($FH_aliSeq);
	}
}
