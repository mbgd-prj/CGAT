#!/usr/bin/perl

use Sequence;
use Segment;
use Debug;
use Carp;
use SimpleGraph;
use Alignment::FindBestPath;
use Alignment::FindBestPathChain;
use Alignment::CalcScore;
use Alignment::OverlapCheck;
use Util;
use strict;

BEGIN {
	## suppress warning messages
	Sequence::set_silence;
}

############################################################
package AlignmentBase;
############################################################
# base class for both gapped and ungapped alignments
@AlignmentBase::ISA = qw(SegmentPair);
sub get_align_string {
	## should be overridden
}
sub region_info {
	my($this, $fields) = @_;
	my(@out) = ( $this->from1 , $this->to1,
		$this->from2, $this->to2, $this->dir,
		$this->ident, $this->score);
	foreach my $f (@{$fields}) {
		push(@out, $this->{$f});
	}
	join("\t", @out);
}
sub print_region {
	my($this, $ofh, $fields) = @_;
	my $out = $this->region_info($fields);
	if ($ofh) {
		$ofh->print("$out\n");
	} else {
		print "$out\n";
	}
}
sub score {
	my($this, %opt) = @_;
	if ($opt{region}) {
		my($from,$to) = split(/:/, $opt{region});
		return $this->calc_score($from,$to);
	} elsif ($Alignment::UseSumScore && $this->{sumscore}) {
		return $this->{sumscore};
	} elsif ($Alignment::UseOrigScore) {
		return $this->{orig_score};
	} elsif (! $this->{score} || $opt{force}) {
		$this->{score} = $this->calc_score;
		$this->add_update_action('region','score',0);
	}
	return $this->{score};
}
sub ident {
	my($this, %opt) = @_;
	if ($opt{region}) {
		my($from,$to) = split(/:/, $opt{regino});
		return $this->calc_ident($from,$to);
	} elsif ($Alignment::UseOrigIdent && $this->{orig_ident}) {
		return $this->{orig_ident};
	} elsif (! $this->{ident} || $opt{force}) {
		$this->{ident} = $this->calc_ident;
		$this->add_update_action('region','ident',0);
	}
	return $this->{ident};
}
sub calc_ident {
	my($this, $from, $to) = @_;
	my($ali1, $ali2) = $this->get_align_string;
	return -1 if (! $ali1 || ! $ali2);
	my $num_ident = Alignment::CalcIdentity->new->calc_score(
				$ali1, $ali2, $from, $to);
	return sprintf("%.1f", $num_ident / length($ali1) * 100);
}
sub calc_score {
	my($this, $from, $to) = @_;
	my($ali1, $ali2) = $this->get_align_string;
	return -1 if (! $ali1 || ! $ali2);
	Alignment::CalcScore->new->calc_score($ali1, $ali2, $from, $to);
}
############################################################
package Alignment;
############################################################
# gapped alignment
@Alignment::ISA = qw(AlignmentBase);

my $MergeMaxGap = 30;

### constructor
sub new {
	my($class, $segments, %Opt) = @_;
	my $this = {};
	bless $this, $class;
	if ($segments) {
		$this->{segments} = $segments;
	} else {
		$this->{segments} = AlignedSegments->new();
	}
	return $this;
}
sub dup {
	my($this) = @_;
	my $class = ref $this;
	my($dup) = $class->new;
	foreach my $k (%{$this}) {
		$dup->{$k} = $this->{$k};
	}
	$dup;
}
sub name1 {
	$_[0]->{seq1}->name;
}
sub name2 {
	$_[0]->{seq2}->name;
}
sub set_region {
	my($this, $from1, $to1, $from2, $to2, $dir1, $dir2, $ident, $score)
			= @_;
	$dir1 = Sequence::direction($dir1);
	$dir2 = Sequence::direction($dir2);
	my $dir = $dir1 * $dir2;
	$this->{seg1} = Segment->new($from1,$to1);
	$this->{seg2} = Segment->new($from2,$to2);
	$this->{dir} = $dir;
	$this->{dir1} = $dir1;
	$this->{dir2} = $dir2;
	$this->{orig_ident} = $ident;
	$this->{orig_score} = $score;
}
sub set_sequences {
	my($this, $seq1, $seq2)= @_;
	$this->{seq1} = $seq1;
	$this->{seq2} = $seq2;
	$this->{segments}->set_sequences($seq1,$seq2);
}
sub set_segments {
	my($this, $segments)= @_;
	$this->{segments} = $segments;
	my(@list) = $segments->list;
	my($from1,$to1,$from2,$to2);
	if ($list[0]->from1 < $list[$#list]->from1) {
		$from1 = $list[0]->from1;
		$to1 = $list[$#list]->to1;
	} else {
		$from1 = $list[$#list]->from1;
		$to1 = $list[0]->to1;
	}
	if ($list[0]->from2 < $list[$#list]->from2) {
		$from2 = $list[0]->from2;
		$to2 = $list[$#list]->to2;
	} else {
		$from2 = $list[$#list]->from2;
		$to2 = $list[0]->to2;
	}
	$this->set_seg1($from1,$to1);
	$this->set_seg2($from2,$to2);
}
sub set_aliseq {
	my($this, $aliseq1, $aliseq2, %opt) = @_;
	my($from1, $from2) = ($opt{from1},$opt{from2});
	my(@aliseq1, @aliseq2);
	my($begpos1, $begpos2);
	my($pos1, $pos2);
	my($alilen, $num_ident, $score);
	my($dir1, $dir2) = (1,1);

	if ($this->{dir1} && $this->{dir2}) {
		$dir1 = Sequence::direction($this->{dir1});
		$dir2 = Sequence::direction($this->{dir2});
	} elsif ($opt{dir}) {
		$dir2 = Sequence::direction($opt{dir});
		$this->{dir} = $opt{dir};
		$this->{dir1} = 1; $this->{dir2} = $opt{dir};
	} else {
		if ($opt{dir1}) {
			$dir1 = Sequence::direction($opt{dir1});
		}
		if ($opt{dir2}) {
			$dir2 = Sequence::direction($opt{dir2});
		}
		$this->{dir1} = $dir1;
		$this->{dir2} = $dir2;
		$this->{dir} = $opt{dir1} * $opt{dir2};
	}
	my($incr1,$incr2) = ($dir1,$dir2);
	if ($opt{step}){
		$incr1 *= $opt{step}; $incr2 *= $opt{step};
	} else {
		if ($opt{step1}){
			$incr1 *= $opt{step1};
		}
		if ($opt{step2}){
			$incr2 *= $opt{step2};
		}
	}

	my($GapSymbol) = $AlignSeq::GapSymbol;
	if ($opt{GapSymbol}) {
		$GapSymbol = $opt{GapSymbol};
	}

	if(! $from1 && ! $from2) {
		$from1 = $from2 = 1;
		if (! $this->{seq1} &&! $this->{seq2}) {
			my $seq1 = $aliseq1;
			my $seq2 = $aliseq2;
			$seq1 =~ s/$GapSymbol//g;
			$seq2 =~ s/$GapSymbol//g;

			$this->{seq1} = RawSequence->new($seq1);
			$this->{seq2} = RawSequence->new($seq2);
		}
	}

	$begpos1 = $pos1 = $from1; $begpos2 = $pos2 = $from2;

	@aliseq1 = split(//, $aliseq1);
	@aliseq2 = split(//, $aliseq2);

	for (my $i = 0; $i < @aliseq1; $i++) {
		if ($aliseq1[$i] eq $GapSymbol) {
			if ($alilen) {
				my $ident = sprintf("%.1f",$num_ident/$alilen);
				$this->add_segment(
					from1=>$begpos1, to1=>$pos1-$dir1,
					from2=>$begpos2, to2=>$pos2-$dir2,
					dir1=>$dir1, dir2=>$dir2,
					ident=>$ident);
				$alilen = 0;
				$num_ident = 0;
			}
			$pos2 += $incr2;
		} elsif ($aliseq2[$i] eq $GapSymbol) {
			if ($alilen) {
				my $ident = sprintf("%.1f",$num_ident/$alilen);
				$this->add_segment(
					from1=>$begpos1, to1=>$pos1-$dir1,
					from2=>$begpos2, to2=>$pos2-$dir2,
					dir1=>$dir1, dir2=>$dir2,
					ident=>$ident);
				$alilen = 0;
				$num_ident = 0;
			}
			$pos1 += $incr1;
		} else {
			if ($alilen == 0) {
				$begpos1 = $pos1; $begpos2 = $pos2;
			}
			if ($aliseq1[$i] eq $aliseq2[$i]) {
				$num_ident++;
			}
			$pos1 += $incr1; $pos2 += $incr2; $alilen++;
		}
		if ($opt{to1} && $pos1 > $opt{to1} ||
			$opt{to2} && $pos2 > $opt{to2}) {
			last;
		}
	}
	if ($alilen) {
		my $ident = sprintf("%.1f",$num_ident/$alilen);
		$this->add_segment(from1=>$begpos1, to1=>$pos1-$incr1,
			from2=>$begpos2, to2=>$pos2-$incr2,
			dir1=>$dir1, dir2=>$dir2,
			ident=>$ident);
	}
	if ($dir1 >= 0) {
		$this->{seg1} = Segment->new($from1, $pos1 - 1);
	} else {
		$this->{seg1} = Segment->new($pos1 + 1, $from1);
	}
	if ($dir2 >= 0) {
		$this->{seg2} = Segment->new($from2, $pos2 - 1);
	} else {
		$this->{seg2} = Segment->new($pos2 + 1, $from2);
	}
}
sub add_segment {
	my($this, %opt) = @_;
	my $s = UngappedAlignment->new(
		from1=>$opt{from1},
		from2=>$opt{from2},
		to1=>$opt{to1},
		to2=>$opt{to2},
		dir1=>$opt{dir1},
		dir2=>$opt{dir2},
		alignment=>$this,
		orig_ident=>$opt{orig_ident},
		orig_score=>$opt{orig_score},
	);
	$this->{segments}->add($s);
}
# to change the coordinate of the alignment based on the original sequence
sub change_coord_base {
	my($this, $origseq1, $origseq2) = @_;
	if ($origseq1) {
		my(@reg1) = $this->{seq1}->get_region($origseq1);
		if (@reg1) {
			$this->shift_coord($reg1[0]-1, 0);
			$this->{seq1} = $origseq1;
			$this->{segments}->set_sequences($origseq1,0);
		}
	}
	if ($origseq2) {
		my(@reg2) = $this->{seq2}->get_region($origseq2);
		if (@reg2) {
			$this->shift_coord(0, $reg2[0]-1);
			$this->{seq2} = $origseq2;
			$this->{segments}->set_sequences(0,$origseq2);
		}
	}
}
## override the method of SegmentPair
sub shift_coord {
	my($this, $val1, $val2) = @_;
	$this->SUPER::shift_coord($val1, $val2);
	foreach my $s ($this->{segments}->list) {
		$s->shift_coord($val1, $val2);
	}
}

### methods to output information
sub print_align_info {
	my($this, $ofh, $add_fields, $opt) = @_;
	my($out);
	
	if (! $opt->{skip_region}) {
		$out = ">";
		$out .= join("\t", $this->{seq1}->name,$this->from1,$this->to1,
			$this->{seq2}->name,$this->from2,$this->to2,$this->dir,
			$this->ident, $this->score);
		if ($add_fields) {
			if (ref $add_fields eq 'ARRAY') {
				foreach my $fld ( @{$add_fields} ) {
					$out .= "\t$this->{$fld}";
				}
			} else {
				$out .= ("\t" . $this->{ $add_fields });
			}
		}
		$out .= "\n";
	}
	foreach my $s ($this->{segments}->list) {
		$out .= join("\t", $s->from1,$s->from2,$s->length) . "\n";
#				$s->ident) . "\n";
#				$s->ident,$s->score) . "\n";
	}
	if ($ofh) {
		$ofh->print($out);
	} else {
		print $out;
	}
}
sub print_align_info2 {
	my($this) = @_;
	foreach my $s ($this->{segments}->list) {
		print join("\t", $s->from1,$s->to1,$s->from2,$s->to2,
				$s->dir) . "\n";
	}
}
sub print_alignment {
	my($this, $ofh) = @_;
	my($aliSeq) = $this->get_AlignSeq;
	$aliSeq->print($ofh);
}
	
sub get_align_string {
	my($this, $opt) = @_;
	my($ali1, $ali2, $ins1, $ins2);
	my($prev_pos1, $prev_pos2);
	my($GapSymbol) = $AlignSeq::GapSymbol;

	if ($this->{aliseq1} && $this->{aliseq2}) {
		return ($this->{aliseq1}, $this->{aliseq2});
	}

	foreach my $s ( $this->{segments}->list ) {
		my($from1,$to1,$from2,$to2) =
			($s->from1,$s->to1,$s->from2,$s->to2);
		if ($prev_pos1 && $prev_pos2) {
			$ins1 = ($this->{dir1} >= 0) ?
					$from1 - 1 - $prev_pos1 :
					$prev_pos1 - 1 - $to1;
			$ins2 = ($this->{dir2} >= 0) ? 
					$from2 - 1 - $prev_pos2 :
					$prev_pos2 - 1 - $to2;
Debug::debug_exec( sub {
print "INS>$ins1,$ins2; $from1,$prev_pos1;$from2,$prev_pos2;$to1,$to2;$this->{dir1},$this->{dir2};$this\n";
},2);
			if ($ins1 < 0 || $ins2 < 0) {
				print "===ERROR===\n";
				$this->print_segpair;
				die "error: ins=($ins1, $ins2)\n";
			}
			if ($ins1){
				$prev_pos1 += $this->{dir1};
				my ($from1, $to1) = &calc_region(
					$prev_pos1, $ins1, $this->{dir1});
				$ali1 .= $this->{seq1}->subseqString(
					$from1,$to1, $this->{dir1});
				$ali2 .= ($GapSymbol x $ins1);
			} elsif ($ins2) {
				$prev_pos2 += $this->{dir2};
				my ($from2, $to2) = &calc_region(
					$prev_pos2, $ins2, $this->{dir2});
				$ali2 .= $this->{seq2}->subseqString(
					$from2,$to2,$this->{dir2});
				$ali1 .= ($GapSymbol x $ins2);
			}
		}
		$ali1 .= $this->{seq1}->subseqString(
				$from1,$to1,$this->{dir1});
		$ali2 .= $this->{seq2}->subseqString(
				$from2,$to2,$this->{dir2});

		$prev_pos1 = ($this->{dir1} >= 0) ? $to1 : $from1;
		$prev_pos2 = ($this->{dir2} >= 0) ? $to2 : $from2;
	}
	## cache results
	if (! $opt->{nosave}) {
		$this->{aliseq1} = $ali1;
		$this->{aliseq2} = $ali2;
		$this->add_update_action('region','aliseq1','');
		$this->add_update_action('region','aliseq2','');
	}
	return $ali1, $ali2;
}
sub get_AlignSeq {
	my($this) = @_;
	my($aliseq1, $aliseq2) =$this->get_align_string;
	my $pos1 = $this->{dir1} >= 0 ? $this->from1 : $this->to1;
	my $pos2 = $this->{dir2} >= 0 ? $this->from2 : $this->to2;
	my($name1, $name2) = ($this->{seq1}->name, $this->{seq2}->name);
	my $aliseq = AlignSeq->new;
	$aliseq->setAlignData([$aliseq1, $aliseq2], [$name1, $name2],
		[$pos1, $pos2], [$this->{dir1}, $this->{dir2}]);
	$aliseq;
}
# convert from {from,len} to {from,to}
sub calc_region {
	my($from, $len, $dir) = @_;
	if ($dir >= 0) {
		return $from, $from + $len - 1;
	} else {
		return $from - $len + 1, $from;
	}
}
### merge two sets of alignments
sub mergeCheck {
	my($this, $other) = @_;
	my $MINLEN = 1;

	return 0 if ($this->{dir} != $other->{dir});

	my($len1,$len2) = $this->overlapLen($other);
	my($minlen,$maxlen);
	if ($len1 <= $len2) {
		$minlen = $len1; $maxlen = $len2;
	} else {
		$minlen = $len2; $maxlen = $len1;
	}
	if ($minlen < $MINLEN) {
		return 0;
	}
	my $diagdist = $maxlen - $minlen;
	if ($diagdist > $MergeMaxGap) {
		return 0;
	}

	return 1;
}
sub setMergeMaxGap {
	my($maxGap) = @_;
	$MergeMaxGap = $maxGap;
}

sub mergeAlignments_all {
	my($alignment_set, $aliN) = @_;
	while (@{$alignment_set}) {
		my($base_ali, @merged_ali, @covered_ali, @uncovered_ali);

Debug::debug_exec( sub{
print("##Merge:\n");
my $cnt;
foreach my $mali (@{$alignment_set}) {
	$mali->print_segpair;
	$cnt++;
}
print "cnt>$cnt\n";
});

		my $merged = Alignment::mergeAlignments($alignment_set,
				\@covered_ali, \@uncovered_ali, $aliN);

		if ($merged) {
Debug::debug_exec( sub {
	print "Merge OK\n";
	$merged->print_segpair;
});
		}

		# avoid an infinite loop when @covered == ()
		if (@uncovered_ali && @covered_ali) {
Debug::debug_exec( sub {
	print "Uncovered: ", join(' ',@uncovered_ali),"\n";
},2);
			$alignment_set = \@uncovered_ali;
		} else {
			$alignment_set = [];
		}

	}
}
sub mergeAlignments {
	my($alignments, $covered, $uncovered, $aliN) = @_;
	my($ali_segments) = AlignedSegments->new;
	foreach my $a (@{$alignments}) {
		$ali_segments->addList($a->{segments});
	}
	$ali_segments->compareList;
	$ali_segments->delete_align_all;

	my $findbest = Alignment::FindBestPath->new($ali_segments, $alignments);
	my $bestpath = $findbest->findBestPath();

	my $LARGE_VAL = 99999999;
	my $SMALL_VAL = -99999999;
	my $dir = $alignments->[0]->dir;
	my($minfrom1,$maxto1,$minfrom2,$maxto2) =
			($LARGE_VAL,$SMALL_VAL,$LARGE_VAL,$SMALL_VAL);

	foreach my $d (@{$bestpath}) {
		my ($from1,$to1,$from2,$to2) = @{$d};
		# in the @{$bestpath}, from2 > to2 when dir < 0;
		#     therefore, exchange from2 and to2 to ensure from2 < to2
		if ($dir < 0) {
			my $tmp = $from2; $from2 = $to2; $to2 = $tmp;
		}
		$minfrom1 = $minfrom1 < $from1 ? $minfrom1 : $from1;
		$minfrom2 = $minfrom2 < $from2 ? $minfrom2 : $from2;
		$maxto1 = $maxto1 > $to1 ? $maxto1 : $to1;
		$maxto2 = $maxto2 > $to2 ? $maxto2 : $to2;
	}
	if ($minfrom1 == $LARGE_VAL || $minfrom2 == $LARGE_VAL ||
		$maxto1 == $SMALL_VAL || $maxto2 == $SMALL_VAL) {
		return ();
	}
	my $new_region = SegmentPair->new(
		from1=>$minfrom1,to1=>$maxto1,from2=>$minfrom2,to2=>$maxto2);
	my $ovCheck= Alignment::OverlapCheck->new(
	    ## overlap length must be larger than the length of ali1
		overlapRatio1 => 0.5,
	);
	foreach my $a (@{$alignments}) {
		my ($stat1,$stat2) = $ovCheck->overlapStatusCheck_sub(
						$a, $new_region);
		# delete aligned segments covered by the final alignment
		if ($stat1 >= 0 && $stat2 >= 0) {
			push(@{$covered}, $a);
		} else {
			push(@{$uncovered}, $a);
		}
	}

	my($flag, $base_ali);
	foreach my $a (@{$covered}) {
		if (! $base_ali && $aliN->{"$a"} == 1) {
			$base_ali = $a;
			last;
		}
	}
	if (! $base_ali) {
		## there is no base alignment
		return ();
	}

	my $newaliseg = AlignedSegments->new;
	foreach my $a (@{$covered}) {
		$a->{delete} = 1 if ($a ne $base_ali);
	}
	foreach my $d (@{$bestpath}) {
		my ($from1,$to1,$from2,$to2) = @{$d};
		if ($dir < 0) {
			my $tmp = $from2; $from2 = $to2; $to2 = $tmp;
		}
		my $s = UngappedAlignment->new(
			from1=>$from1,
			from2=>$from2,
			to1=>$to1,
			to2=>$to2,
			dir1=>1,
			dir2=>$dir,
			alignment=>$base_ali,
		);
		$newaliseg->add($s);
	}
	$base_ali->{segments} = $newaliseg;
	$base_ali->set_seg1($new_region->from1, $new_region->to1);
	$base_ali->set_seg2($new_region->from2, $new_region->to2);

	return $base_ali;
}
sub merge {
	my($this, $other_ali) = @_;
	my($ret);
	$ret = $this->{segments}->mergeList($other_ali->{segments});
	if ($ret == 0) {
		$this->SUPER::merge($other_ali);
	}
}
sub reverse {
	my($this) = @_;
	$this->{dir1} *= -1;
	$this->{dir2} *= -1;
	$this->{segments}->reverse;
}
sub print_segpair {
	my($this) = @_;
	$this->SUPER::print_segpair;
#	$this->print_align_info("",[],{skip_region=>1}),"\n";
	$this->print_align_info2(),"\n";
}
sub split_alignment {
	my($this, $large_gap, $min_score) = @_;
	my($prev_to1, $prev_to2);
	my($aliseg) = AlignedSegments->new;
	my(@aliseg_list);
	my(@alignment_list);
	my($sum_score) = 0;
	my($num_split);
	my $calScore = Alignment::CalcScore->new;
	push(@aliseg_list, $aliseg);
	foreach my $blk ($this->{segments}->list) {
		$sum_score += $blk->score;
		my($from1,$to1) = ($this->{dir1}>=0) ?
			($blk->from1,$blk->to1) : ($blk->to1, $blk->from1);
		my($from2,$to2) = ($this->{dir2}>=0) ?
			($blk->from2,$blk->to2) : ($blk->to2, $blk->from2);
		if ($prev_to1 && $prev_to2) {
			my $gap1 = abs($prev_to1  - $from1) - 1;
			my $gap2 = abs($prev_to2  - $from2) - 1;
			my $gap = ($gap1 > $gap2) ? $gap1 : $gap2;
			if ($gap > $large_gap) {
				if ($min_score && $sum_score < $min_score) {
#print "SKIP::$sum_score,$min_score\n";
					## discard this alignment
					pop(@aliseg_list);
				}
#print "SPLIT::$gap\n";
				$aliseg = AlignedSegments->new;
				push(@aliseg_list, $aliseg);
				$sum_score = 0;
				$num_split++;
			} else {
				$sum_score += $calScore->gap_penalty($gap);
			}
		}
		$aliseg->add($blk);
		$prev_to1 = $to1; $prev_to2 = $to2;
	}
	if (@aliseg_list == 1 && $num_split == 0) {
		@alignment_list = ($this);
	} else {
		foreach my $aliseq (@aliseg_list) {
			my $newali = $this->dup();
			$newali->set_segments($aliseq);
			push(@alignment_list,$newali);
		}
	}
	@alignment_list;
}
############################################################
package AlignSeq;
############################################################
$AlignSeq::PrintLen = 60;
$AlignSeq::MatchSymbol = ':';
$AlignSeq::GapSymbol = '-';
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this;
}
sub setAlignData {
	my($this, $aliseq, $name, $pos, $dir) = @_;
	my($i) = 0;
	$this->setAliSeq(@{$aliseq});
	$this->{'name'} = $name;
	$this->{'pos'} = $pos;
	$this->{'dir'} = $dir;
}
sub setAliSeq {
	my($this, @aliseq) = @_;
	my($i)= 0;
	my($len, $alilen);
	foreach my $seq (@aliseq) {
		$this->{aliseq}->[$i++] = $seq;
		$len = length($seq);
		$alilen = $len if ($len > $alilen);
	}
	$this->{length} = $alilen;
}
sub print {
	my($this, $ofh) = @_;
	my($alistr) =$this->align_string;
	if ($ofh) {
		$ofh->print($alistr);
	} else {
		print $alistr;
	}
}
sub align_string {
	my($this, $ofh) = @_;
	my($pos1,$pos2) = @{$this->{'pos'}};
	my($dir1,$dir2) = @{$this->{'dir'}};
	my($ali1, $ali2) = @{$this->{'aliseq'}};
	my($name1, $name2) = @{$this->{'name'}};
	my($seq1, $seq2, $chnum1,$chnum2);
	my($out);
	for (my $i = 0; $i < length($ali1); $i+= $AlignSeq::PrintLen) {
		my($mchstr);
		$seq1 = substr($ali1, $i, $AlignSeq::PrintLen), "\n";
		$seq2 = substr($ali2, $i, $AlignSeq::PrintLen), "\n";
		my @sq1 = split(//, $seq1);
		my @sq2 = split(//, $seq2);
		for (my $j = 0; $j < @sq1; $j++) {
			if ($sq1[$j] eq $sq2[$j]) {
				$mchstr .= $AlignSeq::MatchSymbol;
			} else {
				$mchstr .= ' ';
			}
		}
		$out .= sprintf "%-18s ", "$name1  $pos1";
		$out .= "$seq1\n";
		$out .= sprintf "%-18s ", "";
		$out .= "$mchstr\n";
		$out .= sprintf "%-18s ", "$name2  $pos2";
		$out .= "$seq2\n";
		$out .= "\n";
		$seq1 =~ s/$AlignSeq::GapSymbol//g;
		$seq2 =~ s/$AlignSeq::GapSymbol//g;
		$pos1 += length($seq1) * $dir1;
		$pos2 += length($seq2) * $dir2;
	}
	$out;
}
sub get_match_string {
	my($this) = @_;
	my($ali1, $ali2) = @{$this->{'aliseq'}};
	my($match_str);
	for (my $i = 0; $i < length($ali1); $i++) {
		my $c1 = substr($ali1, $i, 1);
		my $c2 = substr($ali2, $i, 1);
		if ($c1 eq $AlignSeq::GapSymbol) {
			$match_str .= 'V';
		} elsif ($c2 eq $AlignSeq::GapSymbol) {
			$match_str .= '^';
		} elsif ($c1 eq $c2) {
			$match_str .= '+';
		} else {
			$match_str .= ' ';
		}
	}
	$match_str;
}
############################################################
package SegmentPairList;
############################################################
# Set of gapped or ungapped alignments
my $TMP_ERROR = 0;
sub new {
	my($self, $seq1, $seq2, $dir) = @_;
	my $class = ref($self);
	if ($class) {
		if (UNIVERSAL::isa($class, 'SegmentPairList')) {
			return $class->new(
				$self->{seq1},$self->{seq2},$self->{dir});
		}
	}
	$class = $self;
	my $this = {
		list=>[], seq1=>$seq1, seq2=>$seq2, dir=>$dir,
	};
	bless $this, $class;
}
sub set_sequences {
	my($this, $seq1, $seq2, $dir) = @_;
	$this->{seq1} = $seq1 if ($seq1);
	$this->{seq2} = $seq2 if ($seq2);
	$this->{dir} = $dir if ($dir);
}
sub add {
	my($this, $seg) = @_;
	push(@{$this->{list}}, $seg);
}
sub addList {
	my($this, $segList) = @_;
	return if (! $segList);
	foreach my $l ($segList->list) {
		if (! $l->{delete}) {
			push(@{$this->{list}}, $l);
		}
	}
}
sub list {
	@{ $_[0]->{list} };
}
sub listref {
	$_[0]->{list};
}
sub sort_by_from {
	my($this, $segn, $dir) = @_;
	my(@list);
	if ($segn == 2) {
		if ($dir < 0) {
			@list = sort {$b->from2<=>$a->from2} $this->list;
		} else {
			@list = sort {$a->from2<=>$b->from2} $this->list;
		}
	} else {
		if ($dir < 0) {
			@list = sort {$b->from1<=>$a->from1} $this->list;
		} else {
			@list = sort {$a->from1<=>$b->from1} $this->list;
		}
	}
	$this->{list} = \@list;
}
sub sort_by_score {
	my($this) = @_;
	my(@list) = sort {$b->score<=>$a->score} $this->list;
	$this->{list} = \@list;
}
sub reverse {
	my($this) = @_;
	my(@revlist);
#	my($len1, $len2) = ($this->{seq1}->length, $this->{seq2}->length);
#	foreach my $seg (reverse @{$this->{list}}) {
#		my $revseg = $seg->reversed_segment($len1,$len2);
#		push(@revlist, $revseg);
#	}
	@revlist =  reverse @{$this->{list}};
	$this->{list} = \@revlist;
}
sub mergeList {
	my($this, $other, $cmpr_reg) = @_;
	my($l);
	my(@list);
	my($mergecnt);

	## extract alignment pairs to be merged
	my $mergeAliSet = $this->compareList($other, $cmpr_reg);
	return 0 if (! @{$mergeAliSet});

	my($sgraph) = SimpleUndirectedGraph->new;
	my(%aliN);

	foreach my $alipair (@{$mergeAliSet}) {
		my($ali1,$ali2) = @{$alipair};
		$sgraph->add( $ali1, $ali2 );
		$aliN{"$ali1"} = 1;
		$aliN{"$ali2"} = 2;
	}

	## single linkage clustering
	my $findcomp = FindComponents->new($sgraph)->find;
	my $comp = $findcomp->get_components_arrayref;
	my(%mergedAli);

	foreach my $c (@{$comp}) {
		$mergecnt += 
	 	    Alignment::mergeAlignments_all($c, \%aliN);
	}

	$this->delete_align_all;
	$other->delete_align_all;

	return $mergecnt;
}
sub compareList {
	my($this, $other_list, $cmpr_reg, $ref_seq) = @_;
	my @MergeSet;
	$ref_seq = 1 if (! $ref_seq);

	my $findOv = $this->make_ovlpSegList($ref_seq, $other_list);
	my(%Ovlp);
	
	while (my $p = $findOv->nextPos) {
	    if ($p->{type} eq 'f') {
		my $ali1 = $p->{seg};
		if ( $cmpr_reg && ($p->{pos} < $cmpr_reg->from
					|| $p->{pos} > $cmpr_reg->to) ) {
	    		$findOv->next;
			next;
		}
		my(@SegList) = $findOv->currOvlpSeg;

		my $ovCheck= Alignment::OverlapCheck->new;
		foreach my $ali2 (@SegList) {
			next if ($ali1->dir != $ali2->dir);
			next if ($ali1->{seq2} != $ali2->{seq2});
			next if ($ali1->{delete} || $ali2->{delete});
			next if ($ali1 eq $ali2); ## the same object

			## check and delete included alignments
			my $stat = $ovCheck->overlapStatusCheck($ali1,$ali2);
			my($ali_del, $ali_sav);
			if ($stat == 0 || $stat == 2) {
				&add_delete_flag($ali2, $ali1);
				$findOv->deleteSegFromList($ali2);
			} elsif ($stat == 1) {
				&add_delete_flag($ali1, $ali2);
				$findOv->deleteSegFromList($ali1);
				last;
			}
		}
	    }
	    $findOv->next;
	}
	$findOv->init;
	my(%Found);
	while (my $p = $findOv->nextPos) {
	    if ($p->{type} eq 'f') {
		my $ali1 = $p->{seg};
		if ( $cmpr_reg && ($p->{pos} < $cmpr_reg->from
					|| $p->{pos} > $cmpr_reg->to) ) {
	    		$findOv->next;
			next;
		}
		my(@SegList) = $findOv->currOvlpSeg;
		my $ovCheck= Alignment::OverlapCheck->new;
		foreach my $ali2 (@SegList) {
			next if ($ali1->dir != $ali2->dir);
			next if ($ali1->{seq2} != $ali2->{seq2});
			next if ($ali1->{delete} || $ali2->{delete});
			next if ($ali1 eq $ali2); ## the same object
			## check and save overlapping alignments
			my $stat = $ovCheck->overlapStatusCheck($ali1,$ali2);
			if ($stat == 3 || $stat == 4) {
				if ($ali1->mergeCheck($ali2)) {
					if (! $Found{"$ali1","$ali2"}) {
					    push(@MergeSet, [$ali1,$ali2]);
					    $Found{"$ali1","$ali2"} = 1;
					}
				}
			}
		}
	    }
	    $findOv->next;
	}
	\@MergeSet;
}

sub make_ovlpSegList {
	my($this, $seqN, @other_seg) = @_;
	my($aliN);
	my($mergedList) = $this->new;
	my(@tmp_lists) = ($this->listref);
	foreach my $s (@other_seg) {
		next if (! $s);
		push(@tmp_lists, $s->listref);
	}
	foreach my $lis (@tmp_lists) {
		$aliN++;
		foreach my $l (@{$lis}) {
			$l->{baseseg} = $seqN;
			$l->{aliN} = $aliN;
			$mergedList->add($l);
		}
	}
	FindOverlapSegmentList->new($mergedList);
}

## overlap check $curr_seg v.s. (keys %{$SegList})

sub add_delete_flag {
	my($ali_deleted, $ali_merged) = @_;
	## delete saved ali
	$ali_deleted->{delete} = 1;
Debug::debug_exec( sub {
print "#delete_align: $ali_deleted,$ali_merged\n";
$ali_deleted->print_segpair;
if ($ali_merged) {
print "included in:\n";
$ali_merged->print_segpair;
}
print "--\n";
},2);

}
sub delete_align_all {
	my($this) = @_;
	my(@list);
	my($cntdel,$cnt);
	foreach my $l ($this->list) {
		if ($l->{delete}) {
			$cntdel++;
		} else {
			push(@list, $l);
			$cnt++;
		}
	}
	$this->{list} = \@list;
}
sub cancel_delete {
	my(@lists) = @_;
Debug::message( "cancel_delete" );
	foreach my $l (@lists) {
		foreach my $ali (@{$l}) {
			delete $ali->{delete};
		}
	}
}
sub print_all {
	my($this) = @_;
	foreach my $s ($this->list) {
		$s->print_segpair;
	}
}
## call alignment chainning algorithm for calculating sumscore
sub calcSumScore {
	my($this, %Opt) = @_;
	$this->{calcSumScore} = 
		Alignment::FindBestPathChain->new($this,'SetSumScore'=>1,%Opt)
			->findBestPath;
}
### determine orthologous segment pairs
# stat == 0	==> best hit
# stat1 == stat2 == 0	==> BBH
sub findBestHit {
	my($this, %Opt) = @_;
	my($coverage) = $Opt{BestHitCoverage};
	$coverage = 0.5 if (! $Opt{BestHitCoverage});
	if ($Opt{'UseSumScore'}) {
		$this->calcSumScore(%Opt);
		$Alignment::UseSumScore = 1;
	}

	# find the best scoring regions on seq1 and seq2
	$this->findBestHit_sub(1, %Opt);
	$this->findBestHit_sub(2, %Opt);
	foreach my $ali ($this->list) {
		my($flag) = 0;
		if ($ali->{bestlen1} / $ali->length1 > $coverage) {
			$flag += 1;
		}
		if ($ali->{bestlen2} / $ali->length2 > $coverage) {
			$flag += 2;
		}
##		$ali->{bestflag} = (3-$flag);
		$ali->{bestflag} = $flag;

Debug::debug_exec( sub{
$ali->print_region('', ['bestflag','bestlen1','bestlen2']);
} );

	}
	if ($Opt{'UseSumScore'}) {
		## reset;
		$Alignment::UseSumScore = 0;
	}
}
# find the best scoring regions on the sequence $seqN
sub findBestHit_sub {
	my($this, $seqN, %Opt) = @_;
	foreach my $ali ($this->list) {
		$ali->{baseseg} = $seqN;
	}
	my $findOv = FindOverlapSegmentList->new($this);
	my($max, $nextmax);
	my(@prev_seg, $curr_pos, $prev_pos, $curr_len);
	my($scoreRatio) = $Opt{BestHitScoreRatio};
	$scoreRatio = 0.9 if (! $Opt{BestHitScoreRatio});

	$prev_pos = -1;
	while (my $pos = $findOv->next) {
		my @curr_seg = $findOv->currOvlpSeg;
		if ($pos->{type} eq 'f') {
			$curr_pos = $pos->{pos} - 1;
			if ($max < $pos->{seg}->score) {
				$nextmax = $pos->{seg}->score;
			}
		} else {
			$curr_pos = $pos->{pos};
			if ($max == $pos->{seg}->score) {
				$nextmax = 0;
				foreach my $seg (@curr_seg) {
					if ($nextmax<$seg->score){
						$nextmax = $seg->score;
					}
				}
			}
		}
		if ($prev_pos>=0) {
			$curr_len = $curr_pos - $prev_pos;
		}
		if ($curr_len > 0) {
			foreach my $seg (@prev_seg) {
				if ($seg->score >= $max * $scoreRatio) {
					$seg->{"bestlen$seqN"} += $curr_len;
				}
			}
		}
		$max = $nextmax;
		@prev_seg = @curr_seg;
		$prev_pos = $curr_pos;
	}
}
sub split_alignment_all {
	my($this, $LargeGap, $MinScore) = @_;
	my $newAlignList = SegmentPairList->new;
	foreach my $ali ($this->list) {
		foreach my $a ($ali->split_alignment($LargeGap, $MinScore)) {
			$newAlignList->add($a);
		}
	}
	$newAlignList;
}
sub allow_inconsistency { 1; }
############################################################
#UngappedSegmentPairList;
package AlignedSegments;
@AlignedSegments::ISA = qw(SegmentPairList);

sub allow_inconsistency { 0; }

############################################################
package SegmentPair;
############################################################
@SegmentPair::ISA = qw(Segment);
sub new {
	my($class, %val) = @_;
	my($this) = {};

	bless $this, $class;
	$this->set_seg1( Segment->new($val{from1}, $val{to1}) );
	$this->set_seg2( Segment->new($val{from2}, $val{to2}) );
	if ($val{dir1} || $val{dir2}) {
		$this->{dir1} = $val{dir1};
		$this->{dir2} = $val{dir2};
		$this->{dir} = $this->{dir1} * $this->{dir2};
	} elsif ($val{dir}) {
		$this->{dir} = $val{dir};
		$this->{dir1} = 1;
		$this->{dir2} = $this->{dir};
	}

	$this->{ident} = $val{ident};
	$this->{score} = $val{score};
	$this->{baseseg} = 1;
	$this;
}
sub from1 {
Carp::confess if (! $_[0]->{seg1});
	$_[0]->{seg1}->from;
}
sub to1 {
	$_[0]->{seg1}->to;
}
sub from2 {
	$_[0]->{seg2}->from;
}
sub to2 {
	$_[0]->{seg2}->to;
}
sub length1 {
	$_[0]->{seg1}->length;
}
sub length2 {
	$_[0]->{seg2}->length;
}
sub dir {
	$_[0]->{dir};
}
sub ali_from1 {
	my($this) = @_;
	$this->{dir1} >= 0 ? $this->from1 : $this->to1;
}
sub ali_to1 {
	my($this) = @_;
	$this->{dir1} >= 0 ? $this->to1 : $this->from1;
}
sub ali_from2 {
	my($this) = @_;
	$this->{dir2} >= 0 ? $this->from2 : $this->to2;
}
sub ali_to2 {
	my($this) = @_;
	$this->{dir2} >= 0 ? $this->to2 : $this->from2;
}
sub set_seg1 {
	my($this,$seg1,$seg1_to) = @_;
	if (ref $seg1 && UNIVERSAL::isa($seg1, "Segment")) {
		$this->{seg1} = $seg1;
	} else {
		$this->set_seg1( Segment->new($seg1, $seg1_to) );
	}
	$this->update('region');
}
sub set_seg2 {
	my($this,$seg2,$seg2_to) = @_;
	if (ref $seg2 && UNIVERSAL::isa($seg2, "Segment")) {
		$this->{seg2} = $seg2;
	} else {
		$this->set_seg2( Segment->new($seg2, $seg2_to) );
	}
	$this->update('region');
}
sub update {
	my($this, $tag) = @_;
	foreach my $varname (keys %{ $this->{varlist}->{$tag} }) {
Debug::debug_exec( sub {
if ($this->{$varname} ne $this->{varlist}->{$tag}->{$varname}) {
print "UPDATE: $varname: $this->{$varname}\n";
} },3 );
		$this->{$varname} = $this->{varlist}->{$tag}->{$varname};
	}
}
sub add_update_action {
	my($this, $tag, $varname, $value) = @_;
	$this->{varlist}->{$tag}->{$varname} = $value;
}

# base segment (1 or 2) when used as Segment data
sub baseseg {
	my($this,$segN) = @_;
	if ($segN !~ /^1|2$/) {
		print STDERR "Incorrect segment number ($segN)\n";
	} else {
		$this->{baseseg} = $segN;
	}
}
# override Segmnet method
sub from {
	my($this) = @_;
	($this->{baseseg} == 1) ?  $this->from1 : $this->from2;
}
sub to {
	my($this) = @_;
	($this->{baseseg} == 1) ?  $this->to1 : $this->to2;
}

sub dup_segment {
	my($this) = @_;
	my $class = ref($this);
	my($newseg) = $class->new;
	foreach my $key (keys %{$this}) {
		$newseg->{$key} = $this->{$key};
	}
	$newseg;
}
sub reversed_segment {
	my($this, $len1, $len2) = @_;
	my $newseg = $this->dup_segment;
	$newseg->{from1} = $len1 - $this->to1 + 1,
	$newseg->{from2} = $len2 - $this->to2 + 1,
	$newseg->{to1} = $len1 - $this->from1 + 1,
	$newseg->{to2} = $len2 - $this->from2 + 1,

##print "  $newseg->{from1} <=  ", $this->to1;
##print "  $newseg->{from2} <= ", $this->to2;
##print "  $newseg->{to1} <= ", $this->from1;
##print "  $newseg->{to2} <= ", $this->from2;
##print "\n";

	$newseg;
}
sub shift_coord {
	my($this, $val1, $val2) = @_;
	$this->{seg1}->shift($val1) if ($val1);
	$this->{seg2}->shift($val2) if ($val2);
}
sub print_segpair {
	my($this) = @_;
	$this->{seg1}->print;
	print "--";
	$this->{seg2}->print;
	print " ", $this->dir, " $this\n";
}
sub mergeCheck {
	my($this, $other) = @_;
	return 0 if ($this->{dir} != $other->{dir});
	if ($this->diagdiff($other) == 0) {
		if ($this->to1 > $other->to1) {
			return 1;
		} else {
			return 2;
		}
	}
	return 0;
}
sub merge {
	my($this,$seg) = @_;
	my($seg1) = $this->{seg1}->union($seg->{seg1});
	my($seg2) = $this->{seg2}->union($seg->{seg2});
	$this->set_seg1( $seg1 );
	$this->set_seg2( $seg2 );
}
sub get_merged_seg {
	my($this,$seg) = @_;
	(	Util::min($this->from1, $seg->from1),
		Util::min($this->from2, $seg->from2),
		Util::max($this->to1, $seg->to1),
		Util::max($this->to2, $seg->to2)	);
}
sub subtract {
	my($this, $seg1, $seg2) = @_;
	my $subseg1 = $this->{seg1}->subtract($seg1);
	my $subseg2 = $this->{seg2}->subtract($seg2);
	if ( ! $subseg1 || ! $subseg2 ) {
		return -1;
	}
	$this->set_seg1( $subseg1 );
	$this->set_seg2( $subseg2 );
}
sub diag {
	my($this) = @_;
	$this->{seg1}->center - $this->{seg2}->center * $this->{dir};
}
sub diagdist {
	my($this, $other) = @_;
	return abs( $this->diagdiff( $other ) );
}
sub diagdiff {
	my($this, $other) = @_;
	return ( $this->diag - $other->diag );
}
sub overlapLen {
	my($this, $other) = @_;
	my $len1 = $this->{seg1}->overlapLen($other->{seg1});
	my $len2 = $this->{seg2}->overlapLen($other->{seg2});
	return ($len1, $len2);
}

sub overlapCheck {
	my($this, $other, %Opt) = @_;
	my($stat1, $stat2);
	$stat1 = $this->{seg1}->overlapCheck($other->{seg1}, %Opt);
	$stat2 = $this->{seg2}->overlapCheck($other->{seg2}, %Opt);
	($stat1, $stat2);
}

sub isBestHit {
	my($this) = @_;
	return ($this->{bestflag} != 0);
}
sub isBBH {
	my($this) = @_;
	return ($this->{bestflag} == 3);
}

############################################################
package UngappedAlignment;
@UngappedAlignment::ISA = qw(AlignmentBase);

# all segments have the same length
# { 'from1', 'from2', 'length'}
sub new {
	my($class, %val) = @_;
	my($this) = {};
	my($dir1, $dir2);

	if ($val{dir1}) {
		$dir1 = $val{dir1};
	} else {
		$dir1 = ($val{from1}<$val{to1}) ?  1 : -1;
	}
	if ($val{dir2}) {
		$dir2 = $val{dir2};
	} else {
		$dir2 = ($val{from2}<$val{to2}) ?  1 : -1;
	}

	$this->{seg1} = Segment->new($val{from1}, $val{to1});
	$this->{seg2} = Segment->new($val{from2}, $val{to2});
	$this->{dir} = $dir1 * $dir2;
	$this->{alignment} = $val{alignment};
	$this->{ident} = $val{ident};
	$this->{score} = $val{score};
	bless $this, $class;
	$this;
}
sub length {
	my($this) = @_;
	$this->{seg1}->length;
}
sub name {
	my($this) = @_;
	if (ref $this->{alignment}) {
		$this->{alignment}->name;
	}
}
sub diag {
	my($this) = @_;
	($this->dir >= 0) ?  $this->from1 - $this->from2 
			: $this->from1 + $this->to2;
}
sub position_map {
	my($this, $pos, $seqN) = @_;
	if ($this->dir >= 0) {
		my ($seqN_dir) = ($seqN == 1) ? -1 : 1;
		return ($pos + $this->diag * $seqN_dir);
	} else {
		return $this->diag - $pos;
	}
}
sub findOverlappingSegmentPair {
	my($this, $other) = @_;
	my($len1, $len2) = $this->overlapLen($other);

	return () if ($len1 <= 0 && $len2 <= 0 );
	my($int11,$int12,$int21,$int22);
	my($dir) = $this->dir;

	## $len1 > 0 || $len2 > 0

#print "ALI: ";
#$other->{seg1}->print; $other->{seg2}->print;
#$this->{seg1}->print; $this->{seg2}->print;
#print "\n";
#print $other->diag,' ', $this->diag,"\n";

	## intXY = (aliX, seqY); ali={ X=1=>other, X=2=>this }.
	if ($len1 > $len2) {
		$int11 = $int21 =
			$this->{seg1}->intersect($other->{seg1});
		# mapping pos of seq1 on to seq2
		if ($dir >= 0) {
			$int12 = $int11->shiftNew( - $other->diag );
			$int22 = $int21->shiftNew( - $this->diag );
		} else {
			$int12 = Segment->new( $other->diag - $int11->from,
					$other->diag - $int11->to);
			$int22 = Segment->new( $this->diag - $int21->from,
					$this->diag - $int21->to);
		}
	}  else {
		$int12 = $int22 =
			$this->{seg2}->intersect($other->{seg2});
		# mapping pos of seq2 on to seq1
		if ($dir >= 0) {
			$int11 = $int12->shiftNew( $other->diag );
			$int21 = $int22->shiftNew( $this->diag );
		} else {
			$int11 = Segment->new( $other->diag - $int12->from,
					$other->diag - $int12->to);
			$int21 = Segment->new( $this->diag - $int22->from,
					$this->diag - $int22->to);
		}
	}
	return ($int11,$int12,$int21,$int22);
}
#sub calc_score {
#	my($this) = @_;
#	my($ali1, $ali2) = $this->get_align_string;
#	CalcAliScore->new->calc_score($ali1, $ali2);
#}

# a simple ungapped alignment
sub get_align_string {
	my($this) = @_;
	my($seq1)= $this->{alignment}->{seq1};
	my($seq2)= $this->{alignment}->{seq2};
	my($ali1) = $seq1->subseqString($this->from1, $this->to1);
	my($ali2) = $seq2->subseqString($this->from2, $this->to2, $this->dir);
	($ali1, $ali2);
}
# for test
sub print_alignment {
	my($this, $ofh) = @_;
	my($ali1, $ali2) = $this->get_align_string;
	print "$ali1\n$ali2\n";
}

sub subtract {
	my($this, $seg1, $seg2) = @_;
	if ($seg1->length != $seg2->length) {
		die "removed segments have different lengths\n";
	}
	$this->SUPER::subtract($seg1, $seg2);
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


