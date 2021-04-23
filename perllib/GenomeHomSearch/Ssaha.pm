#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Ssaha;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'ssaha';
}
sub searchCommand {
	my($this, $query, $database) = @_;
#	$this->{srchoutfile} = "";
	## -o option to generate a coorinate file
	system("$this->{program} $query $database -qf fasta -sf fasta $this->{progopt} ");
}
sub convertCommand {
	my($this) = @_;
	$this->{srchoutfile} = "$this->{filebase}.srchout";
	"parse_homsearch.pl -classname=Ssaha " .
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

	my $cmd = GenomeHomSearch::Ssaha->new(
		sp1=>$sp1, sp2=>$sp2, program=>'ssaha');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
