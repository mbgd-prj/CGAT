#!/usr/bin/perl -s

use Tools::HomologyParser;

###############################################################################
package Tools::Fasta;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::FastaParser->new;
	$this;
}

###############################################################################
package Tools::FastaResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);
sub new {
	my($class) = @_;
	my $this = {};
	bless $this, $class;
}
#sub add_hit {
#	my($this, $hit) = @_;
#	push(@{$this->{hit_list}}, $hit);
#	$this->{hit_key}->{$hit->{name2}} = $hit;
#}
sub get_hsplist {
	my($this) = @_;
	foreach $hit ( $this->hit_list ) {
	}
}

###############################################################################
package Tools::FastaHit;
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

###############################################################################
package Tools::FastaParser;
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
    my($status);
    my($hit);
    my($fsres);
    while ($_ = $this->getline) {
        chomp();

        if (($status eq 'A') && />>/) {
            if ($name[0]) {
		my $rev1, $rev2;
		if ($stop[0] < $start[0]) {
			$rev1 = 1;
		}
		if ($stop[1] < $start[1]) {
			$rev2 = 1;
		}
		my ($begin1,$end1) = &get_endpos(
			$aliseq[0],$disp_start[0], $start[0],$stop[0],$rev1);
		my ($begin2,$end2) = &get_endpos(
			$aliseq[1],$disp_start[1],$start[1],$stop[1],$rev2);

		if ($stop[0] < $start[0]) {
			my $tmp=$start[0]; $start[0]=$stop[0]; $stop[0]=$tmp;
		}
		if ($stop[1] < $start[1]) {
			my $tmp=$start[1]; $start[1]=$stop[1]; $stop[1]=$tmp;
		}

		$aliseq[0] = substr($aliseq[0], $begin1, ($end1-$begin1+1));
		$aliseq[1] = substr($aliseq[1], $begin2, ($end2-$begin2+1));
	

		$hit->set_values(
			name1=>$name[0],
			name2=>$name[1],
			len1=>$len[0],
			len2=>$len[1],
			from1=>$start[0],
			to1=>$stop[0],
			from2=>$start[1],
			to2=>$stop[1],
			aliseq1=>$aliseq[0],
			aliseq2=>$aliseq[1],
			bitscore=>$bitscore,
			score=>$score,
			percentident=> sprintf("%.1f",$ident*100),
			evalue=>$expect,
			frame=>$frame,
			rank=>$rank,
		);
		$hit->{dir1} = ($rev1 ? -1 : 1);
		$hit->{dir2} = ($rev2 ? -1 : 1);
		if ($frame =~ /[f123]/) {
			$hit->{dir} = 1;
		} elsif ($frame =~ /[r456]/) {
			$hit->{dir} = -1;
		}
		if ($hit->{dir} != $hit->{dir1} * $hit->{dir2}) {
			print STDERR "Warning: direction mismatch ".
				"$hit->{dir} $hit->dir1} $hit->{dir2}\n";
		}
            }
            $ident = 0;
            $status = '*';
	    undef @name; undef @aliseq; undef @start;
	    undef @stop; undef @len;
        }

        if (/^ +\d+>>>/ || />>><<</) {
	    return $fsres if ($fsres);
            $status = '*';
        } elsif (/^>>> *([^,<]+)/) {
    	    $fsres = Tools::FastaResult->new;
	    $fsres->{query} = $1;
            $status = 'Q';	# Search
	    $rank = 0;
        } elsif (/^>>[ ]*([^ ]*)/) {
            $status = 'L';	# Hit
            $sqn = -1;
	    $hit = Tools::FastaHit->new;
	    $fsres->add_hit($hit);
	    $rank++;
        } elsif ($status && /^>[ ]*([^ ]*)/) {
            $sqn++;
            $name[$sqn] = $1;
            $status = 'A';	# AlignedSeq
        }

        if ($status eq 'L') {
            if (/^; (sw_score|fa_opt):[ ]*([^ ]*)$/) {
                $score = $2;
            } elsif (/^; (sw|fa)_z-score:[ ]*([^ ]*)$/) {
                $zscore = $2;
            } elsif (/^; (sw|fa)_bits:[ ]*([^ ]*)$/) {
                $bitscore = $2;
            } elsif (/^; (sw|fa|bs)_ident:[ ]*([^ ]*)$/) {
                $ident = $2;
            } elsif (/^; (sw|fa|bs)_overlap:[ ]*([^ ]*)$/) {
                $ovlp = $2;
            } elsif (/^; (fa|sw)_expect:[ ]*([^ ]*)$/) {
                $expect = $2;
            } elsif (/^; (fa|sw)_frame:[ ]*([^ ]*)$/) {
                $frame = $2;
            }
        } elsif ($status eq 'A') {
            if (/^; sq_len:[ ]*([^ ]*)$/) {
                $len[$sqn] = $1;
            } elsif (/^; al_start:[ ]*([^ ]*)$/) {
                $start[$sqn] = $1;
            } elsif (/^; al_stop:[ ]*([^ ]*)$/) {
                $stop[$sqn] = $1;
            } elsif (/^; al_display_start:[ ]*([^ ]*)$/) {
                $disp_start[$sqn] = $1;
	    } elsif (/^[^;>]/) {
		chomp;
		$aliseq[$sqn] .= $_;
            }
        }
    }
    return $fsres;
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

#	($b,$e)=Tools::FastaParser::get_endpos("AC-AGAT-A-AG-TGG",101,105,110);
#	print "$b,$e\n";

	$parser = Tools::FastaParser->new($ARGV[0]);
	while ($res = $parser->read) {
		foreach $hsp ($res->hsp_list) {
			print join(' ',
				$hsp->{'name1'},
				$hsp->{'name2'},
				$hsp->{'from1'},
				$hsp->{'to1'},
				$hsp->{'from2'},
				$hsp->{'to2'},
				$hsp->{'dir2'},
				$hsp->{'percentident'},
				$hsp->{'bitscore'} ), "\n";
		}
	}
}

###############################################################################
1;#
###############################################################################
