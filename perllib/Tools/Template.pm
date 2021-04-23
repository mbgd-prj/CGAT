#!/usr/bin/perl -s

use Tools::HomologyParser;
use Sequence;
use Alignment;

###############################################################################
package Tools::>>CLASS<<;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::>>CLASS<<Parser->new;
	$this;
}
###############################################################################
package Tools::>>CLASS<<Result;
###############################################################################
@ISA = qw(Tools::HomologyResult);

###############################################################################
package Tools::>>CLASS<<HSP;
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
package Tools::>>CLASS<<Parser;
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
	my($result) = Tools::>>CLASS<<Result->new;
	my($ln);
	my($status);
	while ($_ = $this->getline) {
		chomp();
		if (/^>/) {
			s/^>//;
			my($from1,$to1,$from2,$to2,$dir,$ident,$score) = split;
			$hsp = Tools::>>CLASS<<HSP->new;
			$hsp->{from1} = $from1;
			$hsp->{to1} = $to1;
			$hsp->{from2} = $from2;
			$hsp->{to2} = $to2;
			$hsp->{dir1} = 1;
			$hsp->{dir2} = $dir;
			$result->add_hit($hsp);
		} else {
			my($from1,$from2,$len,$ident) = split;
			my $to1 = $from1 + $len - 1;
			my $to2 = $from2 + $len - 1;
			push(@{ $hsp->{align_segments} },
				{ from1 => $from1,
				  from2 => $from2,
				  to1 => $to1,
				  to2 => $to2,
				  dir1 => 1,
				  dir2 => $hsp->{dir2}
				});
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
	$parser = Tools::>>CLASS<<Parser->new($ARGV[0]);
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
