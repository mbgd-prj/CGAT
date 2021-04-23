#!/usr/bin/perl -s
use Alignment;
use Alignment::FindRep;
use Tools::ReadHomologyData;

$AllAlignList = Tools::ReadHomologyData::read(
	sp1=>$sp, sp2=>$sp, infile=>\@ARGV,
	SkipPostProc=>1, SkipFinalOverlapCheck=>1,
	classname=>"CGAT",
);

$repList = Alignment::FindRep->new($AllAlignList)->findRep;
foreach $seg ($repList->list) {
	print join(' ', $seg->from, $seg->to, $seg->{status}),"\n";
}
