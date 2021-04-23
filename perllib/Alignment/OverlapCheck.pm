#!/usr/bin/perl

package Alignment::OverlapCheck;
use Util;

sub new {
	my($class, %Opt) = @_;
	my($this) = {};
	$this->{opt} = \%Opt;
	bless $this, $class;
	return $this;
}
sub overlapStatusCheck {
	my($this, $ali1, $ali2) = @_;
	my($ov1, $ov2) = $this->overlapStatusCheck_sub($ali1,$ali2);
	if ($ov1 == $ov2) {
		return $ov1;
	}
	return -1;
}
sub overlapStatusCheck_sub {
	my($this, $ali1, $ali2) = @_;
	my $ovstat1 = $this->overlapStatus($ali1->from1,$ali1->to1,
		$ali2->from1, $ali2->to1, $ali1->dir);
	my $ovstat2 = $this->overlapStatus($ali1->from2,$ali1->to2,
		$ali2->from2, $ali2->to2, $ali1->dir);
	return ($ovstat1, $ovstat2);
}
sub calcOverlapLen {
	my($from1, $to1, $from2, $to2) = @_;
	&Util::min($to1,$to2) - &Util::max($from1,$from2) + 1;
}
sub overlapStatus {
	my($this, $from1, $to1, $from2, $to2, $dir) = @_;
	my $len1 = $to1 - $from1 + 1;
	my $len2 = $to2 - $from2 + 1;
	my $ovlp = &calcOverlapLen($from1,$to1,$from2,$to2);
	my $minOverlapLen;
	if ($dir < 0) {
		## change segment direction
		my $tmp = $from1; $from1 = $to1; $to1 = $tmp;
	}
	if ($this->{opt}->{overlapLen}) {
		$minOverlapLen = $this->{opt}->{overlapLen};
	} elsif ($this->{opt}->{overlapRatio1}) {
		$minOverlapLen = $this->{opt}->{overlapRatio1} * $len1;
	} elsif ($this->{opt}->{overlapRatio2}) {
		$minOverlapLen = $this->{opt}->{overlapRatio2} * $len2;
	} elsif ($this->{opt}->{overlapRatio}) {
		$minOverlapLen = $this->{opt}->{overlapRatio} *
			Util::min($len1,$len2);
	}
	if ($ovlp == $len1 && $ovlp == $len2) {
		return 0;
	} elsif ($len1 < $len2 && $ovlp == $len1) {
		return 1;
	} elsif ($len2 < $len1 && $ovlp == $len2) {
		return 2;
	} elsif ($ovlp > $minOverlapLen && $from1 <= $from2) {
		return 3;
	} elsif ($ovlp > $minOverlapLen && $from2 < $from1) {
		return 4;
	} else {
		return -1;
	}
}

############################################################
package main;
if ($0 eq __FILE__) {
	my $seq1 = SeqFile->getSequence($ARGV[1]);
	my $seq2 = SeqFile->getSequence($ARGV[2]);
	my $ali = Alignment->new($seq1,$seq2,1);
	open(F, $ARGV[0]) || die "Can't open file\n";
	while(<F>){
		next if (/^$/);
		my($from1, $from2, $to1, $to2, $ident) = split;
		my $len = $to1 - $from1 + 1;
		$ali->add_segment($from1,$to1,$from2,$to2, $ident);
	}
	$ali->print_alignment;
}
############################################################
1;


