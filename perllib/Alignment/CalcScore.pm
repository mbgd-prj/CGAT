#!/usr/bin/perl

use FindBin;
use lib "$FindBin::Bin/..";

use Sequence;
use Segment;
use Debug;
use Carp;
use Alignment::FindBestPath;
use strict;

############################################################
package Alignment::CalcScore;
sub new {
	my($class, %MyOptions) = @_;
	my($this) = $class->getInstance;
	return $this if ($this);

	$this = {};
	bless $this, $class;

	my $Opt = $this->defaultOpt;
	foreach my $key (keys %{$Opt}) {
		if (defined $MyOptions{$key}) {
			$this->{$key} = $MyOptions{$key};
		} else {
			$this->{$key} = $Opt->{$key};
		}
	}
	$class->setInstance($this);
	$this;
}

sub getInstance {
	$Alignment::CalcScore::TheInstance;
}
sub setInstance {
	$Alignment::CalcScore::TheInstance = $_[1];
}
sub defaultOpt {
	my($this) = @_;
	{
		GapSymbol => '-',
		Match => 5,
		MisMatch => -4,
		GapOpen => -10,
		GapExt =>-1
	};
}
sub gap_penalty {
	my($this, $gaplen) = @_;
	if ($gaplen > 0) {
		$this->{GapOpen} + ($gaplen - 1) * $this->{GapExt};
	} else {
		0;
	}
}
sub calc_score {
	my($this, $seq1, $seq2, $from, $to) = @_;
	my($status, $score);

	my($MATCH, $MISMATCH, $GAPOPEN, $GAPEXT) =
		($this->{Match}, $this->{MisMatch},
		$this->{GapOpen},$this->{GapExt});
	my(@seq1) = split(//,$seq1);
	my(@seq2) = split(//,$seq2);
	if (@seq1 != @seq2) {
		print STDERR "Warning: sequence length mismatch\n";
		print STDERR "length: ", length($seq1),' ', length($seq2),"\n";
	}
	if ($from) {
		$from --;
	}
	if ($to) {
		$to --;
	} else {
		$to = @seq1;
	}
	
	for (my $i = $from; $i <$to; $i++) {
		if ($seq1[$i] eq $this->{GapSymbol}) {
			$score += (($status ne 'gap1') ? $GAPOPEN : $GAPEXT);
			$status = 'gap1';
		} elsif ($seq2[$i] eq $this->{GapSymbol}) {
			$score += (($status ne 'gap2') ? $GAPOPEN : $GAPEXT);
			$status = 'gap2';
		} elsif ($seq1[$i] eq $seq2[$i]) {
			$score += $MATCH;
			$status = 'match';
		} else {
			$score += $MISMATCH;
			$status = 'match';
		}
	}
	$score;
}
############################################################
package Alignment::CalcIdentity;
@Alignment::CalcIdentity::ISA = qw(Alignment::CalcScore);
sub getInstance {
	$Alignment::CalcIdentity::TheInstance;
}
sub setInstance {
	$Alignment::CalcIdentity::TheInstance = $_[1];
}
sub defaultOpt {
	my($this) = @_;
	{
		GapSymbol => '-',
		Match => 1,
		MisMatch => 0,
		GapOpen => 0,
		GapExt =>0,
	};
}

############################################################
1;
