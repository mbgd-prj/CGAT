#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::Mummer;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::MummerParser->new;
	$this;
}
###############################################################################
package Tools::MummerResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::MummerHSP;
###############################################################################
@ISA = qw(Tools::HomologyHSP);

sub new {
	my($class, %values) = @_;
	my $this = {};
	bless $this, $class;
	$this->set_values(%values);
	return $this;
}
sub set_values {
	my($this, %values) = @_;
	foreach $k (keys %values) {
		$this->{$k} = $values{$k};
	}
}
#sub add_hsp {
#	my($this, $hsp) = @_;
#	push(@{$this->{hsp_list}}, $hsp);
#}
sub hsp_list {
	my($this) = @_;
	return ($this);
}
sub generateAlignment {
	my($this, $ali, $seq1, $seq2) = @_;

#	my $ali = Alignment->new;
#	$ali->set_sequences($seq1, $seq2);
#	$ali->set_region($this->{from1}, $this->{to1},
#		$this->{from2}, $this->{to2}, $this->{dir1}, $this->{dir2});

	my($from1,$from2) = ($ali->ali_from1, $ali->ali_from2);
	my($to1,$to2);
	my($step) = ($this->{result}->{progname} eq 'PROMER') ? 3 : 1;

#print "#", join(' ', $this->{from1},$this->{to1},$this->{from2},$this->{to2}),"\n";

	foreach my $l (@{$this->{aliinfo}}) {
		$alilen = (abs($l) - 1) * $step;
		if ($alilen > 0) {
			$to1 = $from1 + ($alilen-1) * $this->{dir1};
			$to2 = $from2 + ($alilen-1) * $this->{dir2};
			$ali->add_segment(from1=>$from1, to1=>$to1,
						from2=>$from2, to2=>$to2);
			$from1 = $to1 + $this->{dir1};
			$from2 = $to2 + $this->{dir2};
		}
		if ($l > 0) {
			$from1+=$this->{dir1} * $step;
		} else {
			$from2+=$this->{dir2} * $step;
		}
	}
	$to1 = $ali->ali_to1; $to2 = $ali->ali_to2;
	$alilen = (abs($to1 - $from1) + 1) * $step;
	my $alilen2 = (abs($to2 - $from2) + 1) * $step;
	if ($alilen != $alilen2) {
		print STDERR "alignment length mismatch\n";
		print STDERR "$alilen, $alilen2\n";
	}
	$ali->add_segment(from1=>$from1, to1=>$to1, from2=>$from2, to2=>$to2);
#	if ($this->{dir1} < 0) {
#		$ali->reverse;
#	}
	$ali;
}

###############################################################################
package Tools::MummerParser;
###############################################################################
@ISA = qw(Tools::HomologyParser);

use FileHandle;
sub new {
	my($class, @filenames) = @_;
	my $this = {};
	bless $this, $class;
	if (@filenames) {
		$this->set_filenames(\@filenames);
	}
	$this;
}
sub readfile {
	my($this) = @_;
	my($result) = Tools::MummerResult->new;
	my($ln);
	my($status);
	while ($_ = $this->getline) {
		chomp();
		$ln++;
		if ($ln == 2) {
			$result->{progname} = $_;
		} elsif ($ln == 3) {
			$status = header;
			next;
		}
		if ($status eq 'header') {
			my ($from1, $to1, $from2, $to2) = split;
			$hsp = Tools::MummerHSP->new;
			$result->add_hit($hsp);
			if ($from1 < $to1) {
				$hsp->{from1} = $from1;
				$hsp->{to1} = $to1;
				$hsp->{dir1} = 1;
			} else {
				$hsp->{from1} = $to1;
				$hsp->{to1} = $from1;
				$hsp->{dir1} = -1;
			}
			if ($from2 < $to2) {
				$hsp->{from2} = $from2;
				$hsp->{to2} = $to2;
				$hsp->{dir2} = 1;
			} else {
				$hsp->{from2} = $to2;
				$hsp->{to2} = $from2;
				$hsp->{dir2} = -1;
			}
#			$hsp->{progname} = $result->{progname};
			$status = 'alignment';
		} elsif ($status eq 'alignment') {
			my($pos) = split;
			if ($pos == 0) {
				$status = 'header';
			} else {
				push(@{$hsp->{aliinfo}}, $pos);
			}
		}
        }

	return $result;
}
sub get_endpos {
	my($seq, $first, $start, $stop, $rev) = @_;
	my(@seq) = split(//, $seq);
	my($curr_pos, $rpos) = ($first, 0);
	my($begin_rpos, $end_rpos);
	my($dir) = ($rev ? -1 : 1);
	foreach $c (@seq) {
		if ($curr_pos == $start) {
			$begin_rpos = $rpos;
		}
		if ($curr_pos == $stop) {
			$end_rpos = $rpos;
		}
		if ($c ne '-') {
			$curr_pos += $dir;
		}
		$rpos++;
	}
	($begin_rpos, $end_rpos);
}
###############################################################################
1;#
###############################################################################
###############################################################################
package main;
if ($0 eq __FILE__) {
	$parser = Tools::MummerParser->new($ARGV[0]);
	while ($res = $parser->read) {
		foreach $hsp ($res->hsp_list) {
			my $output = $hsp->get_hsp_info([
				'from1','to1','from2','to2','dir1','dir2']);
			print join(' ', @{$output}),"\n";

#			print join(' ', $hsp->{'from1'}, $hsp->{'to1'},
#				$hsp->{'from2'}, $hsp->{'to2'},
#				$hsp->{'dir1'}, $hsp->{'dir2'}),"\n";
		}
	}
}

###############################################################################
1;#
###############################################################################
