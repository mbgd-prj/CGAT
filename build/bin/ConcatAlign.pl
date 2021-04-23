#!/usr/bin/perl -s
use Alignment;
use Sequence;
$aliList = SegmentPairList->new;
if ($seqfile) {
	$seq = SeqFile->getSequence($seqfile);
}
while (<>) {
	($name1,$from1,$to1,$name2,$from2,$to2,$dir) = split;
	$align = Alignment->new;
	$align->set_region($from1,$to1,$from2,$to2,1,$dir);
	$align->set_sequences($seq,$seq);
	$align->add_segment(
		from1=>$from1,to1=>$to1,from2=>$from2,to2=>$to2,
		dir1=>1,dir2=>$dir
	);
	$aliList->add($align);
}
$aliList->mergeList($aliList);
$aliList->sort_by_from;
foreach $ali ( $aliList->list ) {
	$ali->print_region;
	if ($print_align) {
		$ali->print_alignment;
	}
}
