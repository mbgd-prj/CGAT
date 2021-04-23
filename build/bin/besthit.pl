#!/usr/bin/perl -s
#
# A script to assign besthit (ortholog) flag to each alignment
# Usage: besthit [-coverage=#] [alignfile]
#             e.g.) database/align/blastn.hpy-hpj
#
use Alignment;
$seglist = SegmentPairList->new;
while(<>) {
	($from1,$to1,$from2,$to2,$dir,$ident,$score) = split;
	$ali = Alignment->new;
	$ali->set_region(
		$from1,$to1,$from2,$to2,1,$dir,$ident,$score);
	$seglist->add($ali);
}

$seglist->findBest(coverage=>$coverage);
foreach $s ($seglist->list) {
	$s->print_region_info('', ['bestflag']);
}
