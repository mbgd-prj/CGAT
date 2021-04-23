#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::BlastZ;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'blastz';
}
sub create_dbindex {
	my($this) = @_;
	$this->toupper_seq;
}
sub searchCommand {
	my($this, $query) = @_;
	system("$this->{program} $query $this->{database} $this->{progopt}");
}
sub execSearch {
	my($this, $query, $idx) = @_;
	$this->execMultiSearch($query, $idx);
}
sub convertCommand {
	my($this) = @_;
	"parse_homsearch.pl -classname=BlastZ " .
			"-sp1=$this->{sp1} -sp2=$this->{sp2} " .
			"-progname=$this->{program} -outpath=default ";
}
##############################################################################
#  Main Function for test
##############################################################################
package main;
##############################################################################
if ($0 eq __FILE__) {
	if (scalar(@ARGV) < 2) {
		print STDERR "Usage :: $0 database query\n";
		exit;
	}
	my $sp1 = $ARGV[0];
	my $sp2 = $ARGV[1];

	my $cmd = GenomeHomSearch::BlastZ->new(
		sp1=>$sp1, sp2=>$sp2, program=>'blastz');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
