#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::Waba;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::WabaParser->new;
	$this;
}
###############################################################################
package Tools::WabaResult;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::WabaHSP;
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
package Tools::WabaParser;
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
## use output of the second pass
sub readfile {
	my($this) = @_;
	my($result) = Tools::WabaResult->new;
	my($ln, $totn, $prev_reg1);
	my($status, $hsp);
	while ($_ = $this->getline) {
		if (/^Aligning (\S+) (\S+) ([\+\-]) to (\S+) ([\+\-])/) {
			my($fname1,$reg1,$dir1,$fname2_reg2,$dir2) = 
				($1,$2,$3,$4,$5);
			if ($prev_reg1 && $prev_reg1 ne $reg1) {
				$this->save_current_line($_);
				return $result;
			}
			$prev_reg1 = $reg1;
			my(@tmp_reg2) = split(/\./, $fname2_reg2);
			my $reg2 = pop(@tmp_reg2);
			my($name1,$from1,$to1) =
				($reg1 =~ /(\w+):(\d+)\-(\d+)/);
			my($name2,$from2,$to2) =
				($reg2 =~ /(\w+):(\d+)\-(\d+)/);
			$from1++; $from2++;
			$this->set_queryname($result, $name1);
			
			$hsp = Tools::WabaHSP->new;
			$hsp->{from1} = $from1;
			$hsp->{to1} = $to1;
			$hsp->{from2} = $from2;
			$hsp->{to2} = $to2;
			$hsp->{dir1} = ($dir1 eq '+' ? 1 : -1);
			$hsp->{dir2} = ($dir2 eq '+' ? 1 : -1);
			$result->add_hit($hsp);
			$ln = 0; $totn = 0;
		} elsif (/^best score/) {
			my $aliseq1 = $hsp->{aliseq1};
			$aliseq1 =~ s/\-//;
			my $len = length($aliseq1);
			if ($hsp->{dir1} > 0) {
				$hsp->{to1} = $hsp->{from1} + $len - 1;
			} else {
				$hsp->{from1} = $hsp->{to1} - $len + 1;
			}

			my $aliseq2 = $hsp->{aliseq2};
			$aliseq2 =~ s/\-//;
			my $len = length($aliseq2);
			if ($hsp->{dir2} > 0) {
				$hsp->{to2} = $hsp->{from2} + $len - 1;
			} else {
				$hsp->{from2} = $hsp->{to2} - $len + 1;
			}

##			return $result;
		} elsif (/^$/) {
			$ln = 0;
			$totn++;
		} elsif ($ln == 1) {
			my($pos, $aliseq) = split;
			$hsp->{aliseq1} .= $aliseq;
			if ($totn == 0) {
				if ($hsp->{dir1} > 0) {
					$hsp->{from1} += $pos;
				} else {
					$hsp->{to1} -= $pos;
				}
			}
		} elsif ($ln == 3) {
			my($pos, $aliseq) = split;
			$hsp->{aliseq2} .= $aliseq;
			if ($totn == 0) {
				if ($hsp->{dir2} > 0) {
					$hsp->{from2} += $pos;
				} else {
					$hsp->{to2} -= $pos;
				}
			}
		}
		$ln++;
	}
}

## use output of the third pass: obsolete
sub readfile_OFF {
	my($this) = @_;
	my($result) = Tools::WabaResult->new;
	my($ln);
	my($status);
	while ($_ = $this->getline) {
		chomp();
		$ln++;
		if ($ln == 1) {
			($segname, $ident, $len, $fname1, $reg1, $dir1,
				$fname2_reg2, $dir2) =
	(/(\S+) align (\S+) of (\d+) (\S+) (\S+) ([\+\-]) (\S+) ([\+\-])/);
			@tmp_reg2 = split(/\./, $fname2_reg2);
			$reg2 = pop(@tmp_reg2);

			($name1,$from1,$to1) = ($reg1 =~ /(\w+):(\d+)\-(\d+)/);
			($name2,$from2,$to2) = ($reg2 =~ /(\w+):(\d+)\-(\d+)/);
			$from1++; $from2++;
			$this->set_queryname($result, $name1);
			
			$hsp = Tools::WabaHSP->new;
			$hsp->{from1} = $from1;
			$hsp->{to1} = $to1;
			$hsp->{from2} = $from2;
			$hsp->{to2} = $to2;
			$hsp->{dir1} = ($dir1 eq '+' ? 1 : -1);
			$hsp->{dir2} = ($dir2 eq '+' ? 1 : -1);
			$result->add_hit($hsp);
		} elsif ($ln == 2) {
			$hsp->{aliseq1} = $_;
		} elsif ($ln == 3) {
			$hsp->{aliseq2} = $_;
		} elsif ($ln == 4) {
			return $result;
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
	$parser = Tools::WabaParser->new($ARGV[0]);
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
