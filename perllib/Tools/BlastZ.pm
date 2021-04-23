#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::BlastZ;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::BlastZParser->new;
	$this;
}
###############################################################################
package Tools::BlastZResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::BlastZHSP;
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

	foreach my $l (@{$this->{align_segments}}) {
		$ali->add_segment( from1=>$l->{from1}, to1=>$l->{to1},
			from2=>$l->{from2},to2=>$l->{to2},
			dir1=>$this->{dir1}, dir2=>$this->{dir2} );
	}
	$ali;
}

###############################################################################
package Tools::BlastZParser;
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
	my($result) = Tools::BlastZResult->new;

	my($status, $ln);

	my($dmy,$from1,$to1,$from2,$to2,$len1,$len2);
	my($score,$ident,$name1,$name2,$mdir,$prev_mdir);
	my($totlen, $totmatch);
	my($dir1,$dir2, $snum, @Rev, @Len);

	while($_ = $this->getline){
		chomp;
		if (! $ln++) {
			if (! /^#:lav$/) {
				die "Invalid input file\n";
			}
		} elsif (/^#:eof/) {
			return $result;
		} elsif (/^s {/) {
			$status = 's';
			$snum = 0;
		} elsif (/^h {/) {
			$status = 'h';
			$hnum = 0;
		} elsif (/^a {/) {
			$hsp = Tools::BlastZHSP->new;
			$result->add_hit($hsp);
			$hsp->{dir1} = $dir1;
			$hsp->{dir2} = $dir2;
			$status = 'a';
		} elsif ($status eq 's') {
			if (/^}/) {
				$dir1 = ($Rev[0]) ? -1 : 1;
				$dir2 = ($Rev[1]) ? -1 : 1;
			} else {
				my($name,$from,$to,$str,$cod) = split;
				$Rev[$snum] = $str;
				$Len[$snum] = $to;
				$snum++;
			}
		} elsif ($status eq 'h') {
			s/^ *//;
			s/\"//g;
			if ($hnum == 0) {
				$result->{query} = $_;
			}
			$hnum++;
		} elsif ($status eq 'a') {
			if (/^ *s +(\d+)/) {
				$hsp->{score} = $1;
			} elsif (/^ *b/) {
				($dmy, $from1, $from2) = split;
			} elsif (/^ *e/) {
				($dmy, $to1, $to2) = split;
			} elsif (/^ *l/) {
				my($dmy, $segfrom1, $segfrom2, $segto1, $segto2,
					$ident) = split;
				if ($Rev[0] == 1) {
				    ($segfrom1, $segto1) = 
					&reverse($segfrom1, $segto1, $Len[0]);
				}
				if ($Rev[1] == 1) {
#print "<$segfrom1,$segto1,$segfrom2,$segto2\n";
				    ($segfrom2, $segto2) = 
					&reverse($segfrom2, $segto2, $Len[1]);
#print ">$segfrom1,$segto1,$segfrom2,$segto2\n";
				}
				my $len = $segto1 - $segfrom1 + 1;
				my $match = $len * $ident / 100;
				$totlen += $len; $totmatch += $match;
#print ">$segfrom1,$segto1,$segfrom2,$segto2>>>$Rev[0],$Rev[1]\n";
				push(@{ $hsp->{align_segments} },
					 { from1=>$segfrom1, to1=>$segto1,
						from2=>$segfrom2, to2=>$segto2, 
						dir1=>$dir1,
						dir2=>$dir2,
						ident=>$ident });
			} elsif (/^}/) {
				if ($Rev[0] == 1) {
				    ($from1, $to1) = 
					&reverse($from1, $to1, $Len[0]);
				}
				if ($Rev[1] == 1) {
#print "<<<$from2,$to2\n";
				    ($from2, $to2) = 
					&reverse($from2, $to2, $Len[1]);
#print "<<<$from2,$to2\n";
				}
				$hsp->{from1} = $from1; $hsp->{from2} = $from2;
				$hsp->{to1} = $to1; $hsp->{to2} = $to2;
			}
		}
		if (/^}/) {
			$status = '';
		}
	}
	$result;
}
sub reverse {
	my($from, $to, $len) = @_;
	my $newfrom = $len - $from + 1;
	my $newto = $len - $to + 1;
	($newfrom, $newto);
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
	$parser = Tools::BlastZParser->new($ARGV[0]);
	while ($res = $parser->read) {
		foreach $hsp ($res->hsp_list) {
			my $output = $hsp->get_hsp_info([
				'from1','to1','from2','to2','dir1','dir2']);
			print join(' ', @{$output}),"\n";
		}
	}
}

###############################################################################
1;#
###############################################################################
