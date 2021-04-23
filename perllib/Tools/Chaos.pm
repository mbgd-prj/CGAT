#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::Chaos;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::ChaosParser->new;
	$this;
}
###############################################################################
package Tools::ChaosResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::ChaosHSP;
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
package Tools::ChaosParser;
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
	my($result) = Tools::ChaosResult->new;
	my($ln);
	my($status);
	my($from1,$to1,$from2,$to2,$dir,$score);
	my($flag) = 1;
	while ($_ = $this->getline) {
		chomp;
		if (/^score =/) {
			$flag = 1;
		} elsif ($flag && ! /^\s*$/) {
			my($qinfo, $sinfo, $scoreinfo) = split(/;\s*/);
			my(@qinfo) = split(/ /,$qinfo);
			my(@sinfo) = split(/ /, $sinfo);
			$qname = $qinfo[0];
			if ($this->set_queryname($result, $qname) < 0) {
				$this->save_current_line($_);
				return $result;
			}
			$to1 = pop @qinfo; $from1 = pop @qinfo;
			$to2 = pop @sinfo; $from2 = pop @sinfo;
			($score, $dir) = (
			    $scoreinfo =~ /score = ([\d\.]+) \(([+-])\)/);
			$dir = ($dir eq '+' ? 1 : -1);
			if ($to2 < $from2) {
				$tmp = $from2; $from2 = $to2; $to2 = $tmp;
			}
			$hsp = Tools::ChaosHSP->new;
			$hsp->{from1} = $from1;
			$hsp->{to1} = $to1;
			$hsp->{from2} = $from2;
			$hsp->{to2} = $to2;
			$hsp->{dir1} = 1;
			$hsp->{dir2} = $dir;
			$result->add_hit($hsp);
			$flag = 0;
			$status = 1;
		} elsif (/^[A-Za-z\-]+$/) {
			if ($status == 1) {
				$hsp->{aliseq1} .= $_;
				$status = 2;
			} elsif ($status == 2) {
				$hsp->{aliseq2} .= $_;
				$status = 1;
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
	$parser = Tools::ChaosParser->new($ARGV[0]);
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
