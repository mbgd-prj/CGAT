#!/usr/bin/perl

use POSIX;
use strict;
#####################################################
package Segment;
sub new {
	my($class, $from, $to) = @_;
	my($this)= {};
	bless $this, $class;
	$this->setRegion($from,$to);
	return $this;
}
sub setRegion {
	my($this, $from, $to) = @_;
	if($to < $from) {
		## $from <= $to must be hold
		my $tmp = $from; $from = $to; $to = $tmp;
	}
	$this->{from} = $from;
	$this->{to} = $to;
}
sub from {
	$_[0]->{from};
}
sub to {
	$_[0]->{to};
}
sub length {
	my($this) = @_;
	return ($this->to - $this->from + 1);
}
sub center {
	my($this) = @_;
	($this->from + $this->to) / 2;
}
sub shiftNew {
	my($this, $shift) = @_;
	Segment->new( $this->from+$shift, $this->to+$shift );
}
sub shift {
	my($this, $shift) = @_;
	$this->{from}+= $shift;
	$this->{to}+= $shift;
}
sub subtract {
	my($this, $seg) = @_;
	if ($this->from == $seg->from && $this->to > $seg->to) {
		Segment->new( $seg->to+1, $this->{to} );
	} elsif ($this->to == $seg->to && $this->from < $seg->from) {
		Segment->new( $this->from, $seg->from-1 );
	} else {
		();
	}
}
sub chop {
	my($this, $seg) = @_;
	if ($this->from == $seg->from && $this->to > $seg->{to}) {
		$this->{from} = $seg->to + 1;
	} elsif ($this->to == $seg->to && $this->from < $seg->from) {
		$this->{to} = $seg->from - 1;
	} else {
		return -1;
	}
	return 0;
}
sub magnify {
	my($this, $factor) = @_;
	my($center) = $this->center;
	my($length)= $this->length;
	my($from) = POSIX::floor($center + ($length / 2) * $factor);
	my($to) = POSIX::ceil($center - ($length / 2) * $factor);
	return Segment->new($from, $to);
}
sub isOverlap {
	my($this, $other) = @_;
	($this->from <= $other->to && $other->from <= $this->to);
}
sub overlapLen {
	my($this, $other) = @_;
	&min($this->to,$other->to) - &max($this->from,$other->from) + 1;
}
sub overlapCheck {
	my($this, $other, %Opt) = @_;
	my($ovlen) = $this->overlapLen($other);
	my($baselen);
	if ($Opt{'base'} eq 'max') {
		$baselen = ($this->length >= $other->length)
			? $this->{length} : $other->{length};
	} elsif ($Opt{'base'} eq 'min') {
		$baselen = ($this->length <= $other->length)
			? $this->{length} : $other->{length};
	} else	{
		#if ($Opt{base} =~ /this/)
		$baselen = $this->length;
	}
	return ($ovlen > $baselen * $Opt{'ratioCut'});
}
sub overlapSegment {
	my($this, $other) = @_;
	$this->intersect($other);
}
sub intersect {
	my($this, $other) = @_;
	my $from = &max($this->from, $other->from) ;
	my $to = &min($this->to, $other->to);
	if ($from <= $to) {
		return Segment->new($from, $to);
	} else {
		return undef;
	}
}
sub union {
	my($this, $other) = @_;
	my $from = &min($this->from, $other->from) ;
	my $to = &max($this->to, $other->to);
	return Segment->new($from, $to);
}
sub print {
	my($this) = @_;
	print "[", $this->from, ",", $this->to, "]";
}

sub min { ( ($_[0] <= $_[1]) ? $_[0] : $_[1] ); }
sub max { ( ($_[0] >= $_[1]) ? $_[0] : $_[1] ); }
#####################################################
package DirectedSegment;
@DirectedSegment::ISA = qw(Segment);

sub new {
	my($class, $from, $to, $dir) = @_;
	my($this) = {};
	bless $this, $class;
	$this->setRegion($from,$to);
	$this->setDir($dir);
	return $this;
}
sub setDir {
	my($this, $dir) = @_;
	if ($dir =~ /^(f|\+)/) {
		$dir = 1;
	} elsif ($dir =~ /^(r|\-)/) {
		$dir = -1;
	}
	$this->{dir} = $dir;
}
sub dir {
	$_[0]->{dir};
}
sub overlapCheck {
	my($this, $other, %Opt) = @_;
	if ($Opt{dirCheck}) {
		return 0 if ($this->dir != $other->dir);
	}
}
#####################################################
package SegmentList;
sub new {
	my($class) = @_;
	my($this)= {};
	bless $this, $class;
	$this->{list} = [];
	return $this;
}
sub add {
	my($this, @seg) = @_;
	push(@{$this->{list}}, @seg);
}
sub addList {
	my($this, $segList) = @_;
	push(@{$this->{list}}, $segList->list);
}
sub list {
	@{ $_[0]->{list} };
}
sub listref {
	$_[0]->{list};
}
sub sort_by_from {
	my($this) = @_;
	my @list = sort {$a->from <=>$b->from} $this->list;
	$this->{list} = \@list;
}
#####################################################
package FindOverlapSegmentList;
sub new {
	my($class, $seglist) = @_;
	my($this)= {};
	bless $this, $class;
	$this->{seglist} = $seglist;
	$this->init;
	return $this;
}
sub init {
	my($this) = @_;
	$this->{List} = {};
	$this->{poslist_idx} = 0;
	$this->make_poslist;
}
sub make_poslist {
	my($this) = @_;
	my @poslist;
	foreach my $seg ($this->{seglist}->list) {
		push(@poslist,
			{ pos=>$seg->from, type=>'f', seg=>$seg },
			{ pos=>$seg->to, type=>'t', seg=>$seg }
		);
	}
	@poslist = sort {$a->{pos} <=> $b->{pos}
			|| $a->{type} cmp $b->{type}}	## 'f' must precede 't'
		@poslist;
	$this->{poslist} = \@poslist;
}
sub nextPos {
	my($this) = @_;
	my($posdata) = $this->{poslist}->[$this->{poslist_idx}];
	return $posdata;
}
sub next {
	my($this) = @_;
	my($posdata) = $this->{poslist}->[$this->{poslist_idx}];
	return 0 if (! $posdata);
	if ($posdata->{type} eq 'f') {
		$this->{List}->{"$posdata->{seg}"} = $posdata->{seg};
	} else {
		delete $this->{List}->{"$posdata->{seg}"};
	}
	$this->{poslist_idx}++;
	return $posdata;
}
sub currRegion {
	my($this) = @_;
	($this->{from},$this->{to});
}
sub deleteSegFromList {
	my($this, $seg) = @_;
	if ($this->{List}->{"$seg"}) {
		delete $this->{List}->{"$seg"};
		return 0;
	}
	return 1;
}
sub currOvlpSeg {
	my($this) = @_;
	my(@segSet);
	foreach my $segn (keys (%{ $this->{List} })) {
		my $seg = $this->{List}->{"$segn"};
		push(@segSet, $seg);
	}
	@segSet;
}

#####################################################
if ($0 eq __FILE__) {
	my $s1 = Segment->new(1001,2000);
	my $s2 = Segment->new(1501,2500);
	my $s3 = Segment->new(2000,2500);
	my $s4 = Segment->new(2001,2500);
	print $s1->isOverlap($s2),"<ov\n"; 
	print $s1->isOverlap($s3),"<ov\n"; 
	print $s1->isOverlap($s4),"<ov\n"; 
	print "OV:"; $s1->overlapSegment($s2)->print;
	my $s11 = $s1->magnify(2);
	$s11 = $s11->magnify(0.5);
	$s11->print;
	my $seglist =SegmentList->new;
	$seglist->add($s1,$s2,$s3,$s4);
	my $findOv = FindOverlapSegmentList->new($seglist);
	while ($findOv->next) {
		my @seg = $findOv->currOvlpSeg;
		print join(' ', @seg),"\n";
	}
}
#####################################################
1;
