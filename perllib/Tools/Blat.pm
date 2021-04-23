#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::Blat;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::BlatParser->new;
	$this;
}
###############################################################################
package Tools::BlatResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::BlatHSP;
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

###############################################################################
package Tools::BlatParser;
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
	my($result) = Tools::BlatResult->new;
	my($ln);
	my($status);
	my($prev_qname);
	$status = $this->{status};
	while ($_ = $this->getline) {
		chomp();
		if (/^-----/) {
			$this->{status} = $status = 1;
			next;
		} elsif ($status == 1) {
			($match, $mismatch, $repmatch, $ncount,
			$qnumins, $qbaseins, $tnumins, $tbaseins,
			$strand, $qname, $qsize, $qstart, $qend,
			$tname, $tsize, $tstart, $tend,
			$blockcount, $blocksizes, $qstarts, $tstarts)
				= split(/\t/);

			if ($this->set_queryname($result,$qname) < 0) {
				$this->save_current_line($_);
				return $result;
			}

			$result->{query} = $qname;

			my @Qstarts  = split(/,/, $qstarts);
			my @Tstarts  = split(/,/, $tstarts);
			my @BlkSizes  = split(/,/, $blocksizes);

if (@Qstarts != $blockcount) {
	print "Warning: block counts are not matched: $blockcount,",
			0+@Qstarts,"\n";
}
			$dir = ($strand eq '+') ? 1 : -1;

			for ($i = 0; $i < $blockcount; $i++) {

				## the BLAT position begins with 0
				## query (seq1) is on the minus strand

				if ($dir < 0) {
					$to1 = $qsize - $Qstarts[$i];
					$from1 = $to1 - $BlkSizes[$i] + 1;
				} else {
					$from1 = $Qstarts[$i] + 1;
					$to1 = $from1 + $BlkSizes[$i] - 1;
				}
				$from2 = $Tstarts[$i] + 1;
				$to2 = $from2 + $BlkSizes[$i] - 1;

				$hsp = Tools::BlatHSP->new;
				$hsp->{from1} = $from1;
				$hsp->{to1} = $to1;
				$hsp->{from2} = $from2;
				$hsp->{to2} = $to2;
				$hsp->{dir1} = $dir;
				$hsp->{dir2} = 1;
				$result->add_hit($hsp);

				push(@{ $hsp->{align_segments} },
					{ from1 => $from1,
					  from2 => $from2,
					  to1 => $to1,
					  to2 => $to2,
					  dir1 => $dir,
					  dir2 => 1,
					});
			}
		}
        }
	return $result;
}
###############################################################################
1;#
###############################################################################
###############################################################################
package main;
if ($0 eq __FILE__) {
	$parser = Tools::BlatParser->new($ARGV[0]);
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
