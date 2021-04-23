#!/usr/bin/perl

use Alignment;

############################################################
# Alignment::FindBestPath
#  Calculate best alignment for overlap resolution.
#     the algorithm is similar to the alignment chaining algorithm,
#     but uses ungapped blocks to resolve an alignment overlap.
############################################################
package Alignment::FindBestPath;
############################################################

sub new {
	my($class, $seglist, $align_set) = @_;
	my($this) = {};
	$this->{seglist} = $seglist;
	$this->{align_set} = $align_set;
	$this->{calScore} = Alignment::CalcScore->new;
	Alignment::FindBestPath::PathNode->clear;
	bless $this, $class;
}
sub findBestPath {
	my($this) = @_;
	$this->{graph} = SimpleDirectedGraph->new;

	foreach my $ali (@{ $this->{align_set} }) {
		my $prev_aliseg;
		## connect adjacent blocks
		foreach my $aliseg ($ali->{segments}->list) {
			if($prev_aliseg) {
				my $n1 = Alignment::FindBestPath::PathNode
						->new($prev_aliseg,
						$prev_aliseg->ali_to1);
				my $n2 = Alignment::FindBestPath::PathNode
						->new($aliseg,
						$aliseg->ali_from1);
				$this->{graph}->add($n1,$n2);
Debug::message(qq{AddGraph[0] $n1->{id} $n2->{id}: $n1->{pos}, $n2->{pos} },2);
			}
			$prev_aliseg = $aliseg;
		}
	}
	foreach my $ali ($this->{seglist}->list) {
		my $n1 = Alignment::FindBestPath::PathNode->new(
						$ali, $ali->from1);
		my $n2 = Alignment::FindBestPath::PathNode->new(
						$ali, $ali->to1);
Debug::message(qq{AddGraph[0] $n1->{id} $n2->{id}: $n1->{pos}, $n2->{pos} },2);
#		$this->{graph}->add($n1,$n2);
		$this->{graph}->add_node($n1);
		$this->{graph}->add_node($n2);
	}

	$this->findBestPath_makeGraph(1);
	$this->findBestPath_makeGraph(2);

	my @nodes = sort { $a->{'pos'}<=>$b->{'pos'} } $this->{graph}->nodes;
	my %PrevNode;
	foreach my $n (@nodes) {
		if (my $prevn = $PrevNode{ "$n->{ali}" }) {
			## path on the same alignment block
			## split the block and connect
Debug::message(qq{AddGraph[4] $prevn->{id} $n->{id}: $prevn->{pos}, $n->{pos} },2);
			$this->{graph}->add($prevn, $n);
		} elsif ($n->{pos} > $n->{ali}->from1) {
			## adding the begining end
			my $nn = Alignment::FindBestPath::PathNode->new($n->{ali}, $n->{ali}->from1);
Debug::message(qq{AddGraph[5] $nn->{id} $n->{id}: $n->{pos}, $nn->{pos} },2);
			$this->{graph}->add($nn, $n);
		}
		$PrevNode{ "$n->{ali}" } = $n;
	}
	$this->findBestPath_sub;
}

sub findBestPath_sub {
	my($this) = @_;
	my($total_max, $maxnode);
	@nodes = $this->{graph}->toposort;

Debug::debug_exec( sub {
  foreach my $n (@nodes) {
	print "$n->{id} := $n->{pos}; $n->{ali} ";
	$n->{ali}->print_segpair;
  }
},2);
Debug::debug_exec( sub {
$this->{graph}->printGraph(keys=>'id:pos');
print "GRAPH OK\n";
},2);

	foreach my $n2 (@nodes) {
		my($extend_flag);
		my($maxscore) = -999;
		my($maxn1);
		my $relpos2 = $n2->{pos} - $n2->{ali}->from1 + 1;
		foreach my $n1 ($this->{graph}->in_edges($n2)) {
			my $relpos1 = $n1->{pos} - $n1->{ali}->from1 + 1;
			my $score;
			if ($n1->{ali} eq $n2->{ali}) {
				## extend an aligned segment
				$score = $n1->{score} +
					$n1->{ali}->score(
						region=>"$relpos1:$relpos2");
				$extend_flag = 1;
			} else {
				## insert a gap
				my $diagdist = $n1->{ali}->diagdist($n2->{ali});
				$score = $n1->{score} +
					$this->{calScore}->gap_penalty($diagdist);
			}
			if ($score > $maxscore) {
				$maxscore = $score;
				$maxn1 = $n1;
			}
		}
		next if($maxscore < 0);
		$n2->{score} = $maxscore;
		$n2->{path} = $maxn1;
		if (! $extend_flag && $relpos2 > 0) {
			my $score = $n2->{ali}->score(region=>"1:$relpos2");
			if ($n2->{score} < $score) {
				$n2->{score} = $score;
				$n2->{path} = 0;
			}
		}
		if ($total_max < $n2->{score}) {
			$total_max = $n2->{score};
			$maxnode = $n2;
		}
	}
	## Trace back
Debug::message( "Score: $total_max" );
Debug::message( "Alignment:" );
	my(@path);
	for (my $n = $maxnode; $n; $n = $n->{path}) {
Debug::debug_exec( sub {
my $from2 = $n->{ali}->position_map($n->{pos}, 1);
print "$n->{id}:$n->{pos},$from2;$n->{ali},$n->{score}\n";
$n->{ali}->print_segpair;
},2);
		if ($n->{found}) {
			die "loop detected\n";
		}
		unshift(@path, $n);
		$n->{found} = 1;
	}
	## Output result
	my($prevp);
	my ($from1, $to1, $from2, $to2);
	my(@output);
	foreach my $p (@path) {
		if ($prevp->{ali} == $p->{ali}) {
			$to1 = $p->{pos};
			$to2 = $p->{ali}->position_map($p->{pos}, 1);
		} else {
			if ($from1 && $to1) {
				push(@output,[$from1,$to1,$from2,$to2]);
			} elsif ($from1) {
				push(@output,[$from1,$from1,$from2,$from2]);
			}
			$from1 = $p->{pos};
			$from2 = $p->{ali}->position_map($p->{pos}, 1);
			$to1 = $to2 = 0;
		}
		$prevp = $p;
	}
	if ($from1 && $to1) {
		push(@output,[$from1,$to1,$from2,$to2]);
	}
Debug::debug_exec( sub {
	foreach my $o (@output) {
		print join(' ', @{$o}),"\n";
	}
},2);

	\@output;
}
sub findBestPath_makeGraph {
	my($this, $seqN) = @_;
	my(@SegList);
	my $findOv = $this->{seglist}->make_ovlpSegList($seqN);
	my($ali1, $ali2);

	while (my $p = $findOv->nextPos) {
		$ali1 = $p->{seg};
		my $curr_pos = $p->{pos};

		my $curr_pos1_1 = &get_pos1($curr_pos,$seqN,$ali1);
		my $n1 = Alignment::FindBestPath::PathNode->new($ali1,$curr_pos1_1);

		foreach $ali2 (@SegList) {
			next if ($ali1 == $ali2);
			my $diagdiff = $ali1->diagdiff($ali2);
			if ($seqN == 2) {
				$diagdiff *= -1;
			}
			my $curr_pos1_2 = &get_pos1($curr_pos,$seqN,$ali2);
			my $n2;

			if ($p->{type} eq 'f' && $diagdiff <= 0) {
			    ### diagnal in the upper half
				my $nextpos = $curr_pos1_2 - 1;
				if ($ali2->from1 <= $nextpos) {
			        ### diagnal in the upper left quadrant
					$n2 = Alignment::FindBestPath::PathNode->new($ali2,$nextpos);
					$this->{graph}->add($n2,$n1);
Debug::message(qq{AddGraph[1] $n2->{id} $n1->{id}: $curr_pos, $curr_pos1_2},2);
Debug::debug_exec( sub {
$n1->{ali}->print_segpair;
$n2->{ali}->print_segpair;
},2);
				}
			} elsif ($p->{type} eq 't' && $diagdiff >= 0) {
			    ### diagnal in the lower half
				my $nextpos = $curr_pos1_2 + 1;
				if ($ali2->to1 >= $nextpos) {
			        ### diagonal in the lower right quadrant
					$n2 = Alignment::FindBestPath::PathNode->new($ali2,$nextpos);
					$this->{graph}->add($n1,$n2);
				
Debug::message(qq{AddGraph[2] $n2->{id} $n1->{id}: $curr_pos, $curr_pos1_2},2);
Debug::debug_exec( sub {
$n1->{ali}->print_segpair;
$n2->{ali}->print_segpair;
},2);
				}
			}
		}
		$findOv->next;
		@SegList = $findOv->currOvlpSeg;
	}

	## connecting alignment blocks adjacent to each other
	my($ali, @curr_ali, @prev_ali);
	my($curr_pos, $prev_pos, $prev_pos_saved, $prev_n);
	$findOv->init;
	while (my $p = $findOv->next) {
		$curr_pos = &get_pos1($p->{pos}, $seqN, $p->{seg});
		$curr_pos = $p->{pos};

		if ($curr_pos != $prev_pos) {
		    if ($prev_pos == $prev_pos_saved + 1) {
			foreach my $p1 (@curr_ali) {
			    my $ali1 = $p1->{seg};
			    my $pos1 = &get_pos1($p1->{pos},$seqN,$ali1);
			    my $n1 = Alignment::FindBestPath::PathNode->new($ali1,$pos1);
			    foreach my $p2 (@prev_ali) {
			    	my $ali2 = $p2->{seg};
			        my $pos2 = &get_pos1($p2->{pos},$seqN,$ali2);
			    	my $n2 = Alignment::FindBestPath::PathNode->new($ali2,$pos2);
				if ($p1->{type} eq 'f' && $p2->{type} eq't'){
					my ($ov1,$ov2) = $n1->{ali}->
						    overlapCheck($n2->{ali});
			my($flag) = (($ali1->from1 - $ali2->from1)
				* ($ali1->from2 - $ali2->from2) * $ali1->dir
					> 0);
					if(! $ov1 && !$ov2 && $flag) {
					    if ($n2->{pos} > $n1->{pos}){

if ($seqN != 2 || $ali2->dir >= 0) {
print "ERROR: $n1->{pos},$n2->{pos};$seqN," . $ali2->dir . " ", $ali1->dir,"\n";
print join(' ', $ali1->from1,$ali1->to1,$ali2->from1,$ali2->to1,$ali1->dir),"<<<\n";
print join(' ', $ali1->from2,$ali1->to2,$ali2->from2,$ali2->to2,$ali1->dir),"<<<\n";
}
					    	$this->{graph}->add($n1, $n2);
					    } else {
						$this->{graph}->add($n2, $n1);
					    }
Debug::message(qq{AddGraph[3] $n2->{id} $n1->{id}: $n2->{pos},$n1->{pos} },2);
					}
				}
			    }
			}
		    }
		    @prev_ali = @curr_ali;
		    @curr_ali = ();
		    $prev_pos_saved = $prev_pos;
		}

		push(@curr_ali, $p);
		$prev_pos = $curr_pos;
	}
}
# return position on seq1
sub get_pos1 {
	my($pos, $seqN, $ali) = @_;
	if ($seqN == 1) {
		return $pos;
	} else {
		# $seqN == 2
		return $ali->position_map($pos, 2);
	}
}

############################################################
package Alignment::FindBestPath::PathNode;
############################################################
sub new {
	my($class, $ali, $pos) = @_;
	my($this) = {};

	if ($PathNode::SavedNode{"$ali:$pos"}) {
		return($PathNode::SavedNode{"$ali:$pos"});
	}

	$PathNode::SavedNode{"$ali:$pos"} = $this;

	$this->{ali} = $ali;
	$this->{pos} = $pos;
	$this->{id} = ++ $PathNode::counter;
	if ($ali->from1 > $pos || $ali->to1 < $pos) {
		print "WARNING ******> ", join(' ',$ali->from1,$ali->to1, $pos),"\n";
	}
	
	bless $this, $class;
}
sub clear {
	undef %PathNode::SavedNode;
	$PathNode::counter = 0;
}
############################################################
1;
############################################################
