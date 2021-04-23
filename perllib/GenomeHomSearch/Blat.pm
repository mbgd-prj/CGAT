#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Blat;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'blat';
}
sub searchCommand {
	my($this, $query, $database) = @_;
	system("$this->{program} $database $query $this->{progopt} " .
			"$this->{srchoutfile}");
}
sub convertCommand {
	my($this) = @_;
	$this->{srchoutfile} = "$this->{filebase}.srchout";
	"parse_homsearch.pl -classname=Blat " .
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

	my $cmd = GenomeHomSearch::Blat->new(
		sp1=>$sp1, sp2=>$sp2, program=>'blat');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
