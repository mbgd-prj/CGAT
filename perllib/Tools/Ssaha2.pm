#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::Ssaha2;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::Ssaha2Parser->new;
	$this;
}
###############################################################################
package Tools::Ssaha2Result;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::Ssaha2HSP;
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
sub add_hsp {
	my($this, $hsp) = @_;
	push(@{$this->{hsp_list}}, $hsp);
}
sub hsp_list {
	my($this) = @_;
	return ($this);
}

###############################################################################
package Tools::Ssaha2Parser;
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
	my($result) = Tools::Ssaha2Result->new;
	my($ln);
	my($status);
	while ($_ = $this->getline) {
		chomp;
		if (/^Matches For.*bases\):\s*(\S+)/) {
			$qname = $1;
			if ($this->set_queryname($result,$qname) < 0) {
				$this->save_current_line($_);
				return $result;
			}
		} elsif (/^ALIGNMENT/) {
			($dmy,$score,$name1,$name2,$from1,$to1,$from2,$to2,
				$dir,$len,$ident,$totlen) = split;
			$dir = ($dir eq 'F' ? 1 : -1);
			if ($to2 < $from2) {
				if ($dir > 0) {
					print STDERR
			  "Warning: Illegal direction: $from2,$to2,$dir\n"; 
				}
				$tmp = $from2; $from2 = $to2; $to2 = $tmp;
			}
			$hsp = Tools::Ssaha2HSP->new;
			$hsp->{from1} = $from1;
			$hsp->{to1} = $to1;
			$hsp->{from2} = $from2;
			$hsp->{to2} = $to2;
			$hsp->{dir1} = $dir;
			$hsp->{dir2} = 1;
			$result->add_hit($hsp);
			$status = 1;
		} elsif ($status) {
			if (/^Query\s+(\d+)\s+([A-Za-z\-]+)\s+(\d+)/) {
				$hsp->{aliseq1} .= $2;
			} elsif (/^Sbjct\s+(\d+)\s+([A-Za-z\-]+)\s+(\d+)/) {
				$hsp->{aliseq2} .= $2;
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
	$parser = Tools::Ssaha2Parser->new($ARGV[0]);
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
