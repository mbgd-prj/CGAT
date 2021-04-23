#!/usr/bin/perl

use Alignment;
use Segment;

# find gaps (unaligned regions) in each genome

package Alignment::FindGap;

sub new {
	my($class, $alignList) = @_;
	my($this) = {};
	$this->{alignList} = $alignList;
	return bless $this, $class;
}

sub FindGap {
	my($this, $segnum) = @_;
	my($tmpSegList);
	if ($segnum) {
		$tmpSegList = SegmentList->new;
		foreach $ali ($this->{alignList}->list) {
			if ($ali->{bestflag} & $segnum) {
#				print "$ali->{bestflag}<<<\n";
				$ali->baseseg($segnum);
				$tmpSegList->add($ali);
			}
		}
	} else {
		$tmpSegList = $this->{alignList};
	}

	my($findOv) = FindOverlapSegmentList->new($tmpSegList);
	my($gap_from) = 1;
	my($retSegList) = SegmentList->new;
	while ($p = $findOv->next) {
		if ($p->{type} eq 'f') {
			if ($cnt == 0 && $gap_from < $p->{pos} - 1) {
				my $seg = Segment->new($gap_from, $p->{pos}-1);
				$retSegList->add($seg);
			}
			$cnt++;
		} else {
			$cnt--;
			if ($cnt == 0) {
				$gap_from = $p->{pos} + 1;
			}
		}
	}
	$retSegList;
}

########################################################################
package main;
use Tools::ReadHomologyData;
if ($0 eq __FILE__) {
	($sp1,$sp2,$program) = @ARGV;
	$ali = Alignment->new;
	$aliList = Tools::ReadCGATHomologyData::read($sp1,$sp2,$program);
	$findGap = Alignment::FindGap->new($aliList);
	$segList = $findGap->FindGap(1);
	foreach $seg ($segList->list) {
		$seg->print;
		print "\n";
	}
}
########################################################################
1;
