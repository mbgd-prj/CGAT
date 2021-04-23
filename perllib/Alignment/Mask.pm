#!/usr/bin/perl
package Alignment::Mask;

sub maskAlign {
	my($self, $alignList, $segList1, $segList2) = @_;
	if (UNIVERSAL::isa($segList1,"SegmentList")) {
		$self->maskAlign_sub($alignList,$segList1,1);
	}
	if (UNIVERSAL::isa($segList2,"SegmentList")) {
		$self->maskAlign_sub($alignList,$segList2,2);
	}
}
sub maskAlign_sub {
	my($self, $alignList, $segList, $seqN) = @_;
	my($curr_pos, $prev_pos);
	my($mergedSegList) = SegmentList->new;
	my($RATIO) = 0.7;

	foreach my $ali ($alignList->list) {
		$ali->{baseseg} =  $seqN;
		$ali->{align_flag} = 1;
	}
	$mergedSegList->addList($alignList);
	$mergedSegList->addList($segList);
	my $findOv = FindOverlapSegmentList->new($mergedSegList);
	my($rep_reg);
	while (my $p = $findOv->next) {
		my $seg = $p->{seg};
#print ">>$p->{pos},$p->{type},$seg>$rep_reg\n";
		if ($p->{type} eq 'f') {
			$curr_pos = $p->{pos} - 1;
			if (! $seg->{align_flag}) {
				$rep_reg++;
			}
		} else {
			$curr_pos = $p->{pos};
			if (! $seg->{align_flag}) {
				$rep_reg--;
			}
		}
		if ($rep_reg > 0) {
			$next_p = $findOv->nextPos;
			last if (! $next_p);
			if ($next_p->{type} eq 'f') {
				$next_pos = $next_p->{pos} - 1;
			} else {
				$next_pos = $next_p->{pos};
			}
			my $region_len = $next_pos - $curr_pos;
#print "MASK>$rep_reg>$region_len;$curr_pos,$next_pos\n";
			foreach my $ov_seg ($findOv->currOvlpSeg) {
				if ($ov_seg->{align_flag}) {
					$ov_seg->{masked_len} += $region_len;
				}
			}
			
		}
		$prev_pos = $curr_pos;
	}
	my $mask_cnt;
	foreach my $ali ($alignList->list) {
		if($ali->{masked_len} >= $ali->length * $RATIO &&
			! $ali->isBestHit) {
			$ali->{delete} = 1;
Debug::debug_exec( sub {
print "##", $ali->length, ' ', $ali->{masked_len},"\n";
$ali->print_segpair;
}, 2);
			 $mask_cnt++;
		}
	}
Debug::message( "Mask: $mask_cnt" );
}
1;
