#!/usr/bin/perl -s
use Alignment;
use Alignment::Mask;
use Tools::ReadHomologyData;
use GenomeFeature;

$AllAlignList = Tools::ReadHomologyData::read(
	sp1=>$sp1, sp2=>$sp2, infile=>\@ARGV,
	SkipPostProc=>1, SkipFinalOverlapCheck=>1,
	classname=>"CGAT",
);

$gfeat = GenomeFeature->new($sp1, 'HighRep',{mode=>'read'});
$gfeat->read_table;
$fList = $gfeat->{featureList};
$segList = SegmentList->new;

foreach $rep ($fList->list) {
	$num = $rep->{value};
	if ($num >= 3) {
		$segList->add($rep);
	}
}
Alignment::Mask->maskAlign_sub($AllAlignList, $segList, 1);
$AllAlignList->delete_align_all;
$AllAlignList->print_all;
