#!/usr/bin/perl

use Alignment;
use Segment;
############################################################
# Alignment chaining algorithm
#  see Dan Gusfield (1997)
#      Algorithms on strings, trees, and sequences, pp.326-329
############################################################
package Alignment::FindBestPathChain;
############################################################
## Variables:
## MaxDist: maximum distance between the alignmnets to be connected
## GapFactor: a factor for the gap penalty definition:
##            gapPenalty = (dist1 + dist2) * GapFactor
##
$DefaultMaxDist = 20000;
$DefaultGapFactor = 1;
sub new {
	my($class, $align_set, %opt) = @_;
	$this->{align_set} = $align_set;
	bless $this, $class;
	$MaxDist = defined $opt{ChainMaxDist} ? $opt{ChainMaxDist} : $DefaultMaxDist;
	$GapFactor = defined $opt{ChainGapFactor} ? $opt{ChainGapFactor} : $DefaultGapFactor;
	$this->{SetSumScore} = 1 if ($opt{SetSumScore});
	$this;
}
sub findBestPath {
	my($this) = @_;
	my($ali1, $ali2);
	my(@AllNodes);

	my $findOv = $this->{align_set}->make_ovlpSegList(1);
	my $chainTmpListPlus= Alignment::ChainTmpList->new(1);
	my $chainTmpListMinus= Alignment::ChainTmpList->new(-1);
	my $chainTmpList;
	while (my $p = $findOv->nextPos) {
		$ali1 = $p->{seg};
		my $pos1 = $p->{pos};
		my $node = Alignment::FindBestPathChain::PathNode->new($ali1);
		if ($ali1->dir > 0) {
			$chainTmpList = $chainTmpListPlus;
		} else {
			$chainTmpList = $chainTmpListMinus;
		}
		if ($p->{type} eq 'f') {
			my $prevnode = $chainTmpList->findMax($ali1);
			my $prevscore = 0;
			if ($prevnode) {
				$prevscore = $prevnode->{sumscore}
				   - $this->gapPenalty($prevnode->{ali}, $ali1);
				if ($prevscore < 0) {
					## cancel
					$prevscore = 0;
					$prevnode = '';
				}
			}
			$node->{sumscore} = $ali1->score + $prevscore;
			$node->{pos_sumscore} =
			   $this->calPosScore($node->{sumscore},$ali1);
			$node->{prev} = $prevnode;
			push(@AllNodes, $node);
		} else {
			$chainTmpList->update($node);
		}
		$findOv->next;
	}
	@AllNodes = sort {$b->{sumscore}<=>$a->{sumscore}} @AllNodes;

	$lastidx = $#{$chainTmpList->{List}};
	
	my(@alignList_All);
	foreach $maxNode (@AllNodes) {
		my(@aliList);
		my $lastnode;
		next if ($maxNode->{delete});
		for (my $node = $maxNode; $node; $node = $node->{prev}) {
			if ($node->{delete}) {
				$lastnode = $node;
				last;
			}
			unshift(@aliList, $node->{ali});
			$node->{delete} = 1;
		}
		if ($this->{SetSumScore}) {
			my $sumscore = $maxNode->{sumscore};
			if ($lastnode) {
				$sumscore -= (
				    $lastnode->{sumscore} -
				    $this->gapPenalty($lastnode->{ali},
						$aliList[0])
				);
			}
			foreach $ali (@aliList) {
				$ali->{sumscore} = $sumscore;
			}
		}
		my $aliChain = Alignment::Chain->new(\@aliList);
		push(@alignList_All, $aliChain);
	}
#foreach $aliChain (@alignList_All) {
#	$aliChain->printList;
#	print "//\n";
#}
	@alignList_All;
}

## score adjustment for taking account of the gap penalties.
sub calPosScore {
	my($this, $score, $ali) = @_;
	if ($ali->dir > 0) {
		$score + ($ali->to1 + $ali->to2) *
			$Alignment::FindBestPathChain::GapFactor;
	} else {
		$score + ($ali->to1 - $ali->from2) *
			$Alignment::FindBestPathChain::GapFactor;
	}
}
sub gapPenalty {
	my($this, $ali1, $ali2) = @_;
	return 99999999 if ($ali1->dir != $ali2->dir);
	my $gap;
	if ($ali1->dir > 0)  {
		$gap= (($ali2->from1 - $ali1->to1) + ($ali2->from2 - $ali1->to2)) * $Alignment::FindBestPathChain::GapFactor;
	} else {
		$gap= (($ali2->from1 - $ali1->to1) + ($ali1->from2 - $ali2->to2)) * $Alignment::FindBestPathChain::GapFactor;
	}
	return $gap;
}
############################################################
package Alignment::Chain;
sub new {
	my($class, $list) = @_;
	my($this) = {};
	bless $this, $class;
	$this->setList( $list ) if ($list);
	$this;
}
sub add {
	my($this, $ali) = @_;
	push(@{$this->{List}}, $ali);
}
sub setList {
	my($this, $aliList) = @_;
	$this->{List} =  $aliList;
}
sub list {
	return @{ $this->{List} };
}
sub printList {
	my($this) = @_;
	foreach $ali (@{ $this->{List} }) {
		$ali->print_segpair;
		print join(' ', $ali->score, $ali->{sumscore}),"\n";
	}
}
############################################################
package Alignment::FindBestPathChain::PathNode;
sub new {
	my($class, $ali) = @_;
	if ($SaveNode{$ali}) {
		return $SaveNode{"$ali"};
	}
	$this = {};
	$this->{ali} = $ali;
	$SaveNode{"$ali"} = $this;
	bless $this, $class;
}
############################################################
package Alignment::ChainTmpList;
sub new {
	my($class, $dir) = @_;
	my($this) = {};
	$this->{List} = ();
	$this->{dir} = ($dir ? $dir : 1);
	bless $this, $class;
}
sub printList {
	my($this) = @_;
	my($i) = 0;
	foreach my $n (@{$this->{List}}) {
		print "$i>>$n->{end_pos},$n->{pos_sumscore}\n";
		$i++;
	}
}
sub comparePos {
	my($this, $pos1, $pos2) = @_;
	return ($pos1 - $pos2) * $this->{dir};
}
sub getEndPos2 {
	my($this, $ali, $end) = @_;
	if ($end eq 'f') {
		($this->{dir} > 0) ? $ali->from2 : $ali->to2;
	} else {
		($this->{dir} > 0) ? $ali->to2 : $ali->from2;
	}
}
sub test_neighbor {
	my($this, $ali1, $ali2) = @_;
	return 0 if ($ali1->dir != $ali2->dir);
	if ($Alignment::FindBestPathChain::MaxDist) {
		my $maxDiff = $Alignment::FindBestPathChain::MaxDist;
		if ($this->{dir} > 0) {
			return (($ali2->from1 - $ali1->to1 < $maxDiff) &&
				($ali2->from2 - $ali1->to2 < $maxDiff));
		} else {
			return (($ali2->from1 - $ali1->to1 < $maxDiff) &&
				($ali1->from2 - $ali2->to2 < $maxDiff));
		}
	} else {
		return 1;
	}
}

#   dir > 0
#     ali1        ali2
#   f----->t    f----->t
#   f----->t    f----->t
#
#   dir < 0
#     ali1        ali2
#   f----->t    f----->t
#   t<-----f    t<-----f
#
sub findMax {
	my($this, $curr_ali) = @_;
	my($i, $node);

	my($curr_pos) = $this->getEndPos2($curr_ali, 'f');

	## delete alignments with dist > MaxDist
	for ($i = 0; $i < @{$this->{List}}; $i++) {
		$node = $this->{List}->[$i];
		if ($curr_ali->from1 - $node->{ali}->to1 >
			$Alignment::FindBestPathChain::MaxDist) {
			splice @{ $this->{List} }, $i, 1;
			$i--;
		}
	}

	## the list is in ascending order of score 
	for ($i = $#{$this->{List}}; $i >= 0; $i--) {
		$node = $this->{List}->[$i];
		if ($this->comparePos($node->{end_pos}, $curr_pos) < 0) {
			if ($this->test_neighbor($node->{ali},$curr_ali)) {
				return $node;
			}
		}
	}
}
sub update {
	my($this, $curr_node) = @_;
	my($i, $node, $newnode, $offset, $len, $flag);
	my($prevscore) = -9999999999;
	my($curr_score) = $curr_node->{pos_sumscore};
	my($curr_pos) = $this->getEndPos2($curr_node->{ali}, 't');
	$curr_node->{end_pos} = $curr_pos;

    ##  @{$this->{List}} is ordered according to both end_pos and pos_sumscore
	for ($i = 0; $i < @{$this->{List}}; $i++) {
		my $node = $this->{List}->[$i];
		if ($this->comparePos($node->{end_pos}, $curr_pos) >= 0) {
			if (! $flag) {
			# The first time that node->{endpos} >= curr->{endpos}
			# prevscore retains the maximum score among nodes
			#	satisfying node->{endpos} < curr->{endpos}
				if (($node->{end_pos} == $curr_pos
					&& $node->{pos_sumscore} < $curr_score)
					|| ($prevscore < $curr_score)) {
					## start deletion
					$offset = $i;
					$len = 0;
					$flag = 1;
				} else{
					## do not insert the current node
					$flag = -1;
					last;
				}
			}
			if ($node->{pos_sumscore} < $curr_score) {
				## continue to delete (spliced out) node
				$len++;
			} else {
				## stop deletion
				last;
			}
		} else {
		    # node->{endpos} < curr->{endpos}
		}
		$prevscore = $node->{pos_sumscore};
	}
	if ($flag >= 0) {
		if ($flag == 1) {
			splice(@{$this->{List}},$offset,$len,$curr_node);
		} elsif ($prevscore < $curr_score) {
			push(@{$this->{List}}, $curr_node);
		}
	}
#	print ">>$this->{dir};$curr_pos,$curr_score\n";
#	$this->printList;
#	print "//\n";
}
############################################################
package main;
if ($0 eq __FILE__) {
	$aliList = SegmentPairList->new;
	$Alignment::UseOrigScore = 1;
	while(<>){
		($from1,$to1,$from2,$to2,$dir,$ident,$score) = split;
		$ali = Alignment->new;
		$ali->set_region($from1,$to1,$from2,$to2,1,$dir,$ident,$score);
		$aliList->add($ali);
	}
	$findBest = Alignment::FindBestPathChain->new($aliList, SetSumScore=>1);
	@alignList_All = $findBest->findBestPath;
	foreach $aliChain (@alignList_All) {
		$aliChain->printList;
		print "//\n";
	}
}

############################################################
1;
############################################################
