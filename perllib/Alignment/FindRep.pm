#!/usr/bin/perl
use FindBin;
use lib "$FindBin::Bin/..";

use Alignment;
use Segment;

############################################################
package Alignment::FindRep;
############################################################
$VeryHighRepNum = 100;
$HighRepNum = 30;
$MidRepNum = 10;
$LowRepNum = 2;
$MinLen = 20;
sub new {
	my($class, $alignList, %opt) = @_;
	my $this = {};
	$this->{alignList} = $alignList;

	if (ref $opt{RepNumCut} eq 'ARRAY') {
		$this->{RepNumCut} = $opt{RepNumCut};
	} else {
		$this->{RepNumCut} = [$LowRepNum, $MidRepNum, $HighRepNum];
	}
	$this->{MinLen} = (defined $opt{MinLen}) ? $opt{MinLen} : $MinLen;
	return bless $this, $class;
}

sub findRep {
	my($this) = @_;
	my $findOv = FindOverlapSegmentList->new($this->{alignList});
	my($cnt,$from, $flag);
	$this->{repSegList} = SegmentList->new;
	while (my $p = $findOv->next) {
		if ($p->{type} eq 'f') {
			$cnt++;
			$stat = $this->get_status($cnt);
			if($stat > $prev_stat) {
				$from = $p->{pos};
				$flag = 1;
			}
		} else {
			$cnt--;
			$stat = $this->get_status($cnt);
			if($stat < $prev_stat) {
				my $to = $p->{pos};
				if ($to - $from + 1 > $this->{MinLen}) {
				    my $seg = Segment->new($from, $p->{pos});
				    $seg->{status} = $prev_stat;
				    $this->{repSegList}->add($seg);
				}
				$flag = 0;
				$from = $p->{pos};
			}
		}
		$prev_stat = $stat;
		
	} 
	$this->{repSegList};
}
sub get_status {
	my($this, $cnt) = @_;
	for (my $i = $#{ $this->{RepNumCut} }; $i>= 0; $i--) {
		if ($cnt >= $this->{RepNumCut}->[$i]) {
			return $this->{RepNumCut}->[$i];
#			return $i+1;
		}
	}
	return 0;
}
1;
