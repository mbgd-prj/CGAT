#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::Ssaha;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::SsahaParser->new;
	$this;
}
###############################################################################
package Tools::SsahaResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::SsahaHSP;
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
package Tools::SsahaParser;
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
	my($result) = Tools::SsahaResult->new;
	my($ln);
	my($status);
	while ($_ = $this->getline) {
		chomp();
		if (/^([FR])([FR])/) {
			my($dir1,$dir2) = ($1,$2);
			my($strand,$name1,$from1,$to1,
				$name2,$from2,$to2,$score,$ident) = split;
			$dir1 = ($dir1 eq 'F') ? 1 : -1;
			$dir2 = ($dir2 eq 'F') ? 1 : -1;

			if ($this->set_queryname($result, $name1) < 0){
				$this->save_current_line($_);
				return $result;
			}

			$hsp = Tools::SsahaHSP->new;
			$hsp->{from1} = $from1;
			$hsp->{to1} = $to1;
			$hsp->{from2} = $from2;
			$hsp->{to2} = $to2;
			$hsp->{dir1} = $dir1;
			$hsp->{dir2} = $dir2;
			$result->add_hit($hsp);
			$status = 0;
		} elsif (/Alignment/) {
			$status = 1;
		} elsif ($status && /^$/) {
		} elsif($status) {
			my($name, $align) = split;
			if ($status == 1) {
				$hsp->{aliseq1} .= $align;
			} elsif ($status == 3) {
				$hsp->{aliseq2} .= $align;
				$status = 0;
			}
			$status++;
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
	$parser = Tools::SsahaParser->new($ARGV[0]);
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
