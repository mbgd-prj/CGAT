#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Game;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'game';
}
sub searchCommand {
	my($this, $query, $database) = @_;
#	$this->{srchoutfile} = "";
	system("$this->{program} -i $query $database -A $this->{progopt} ");
}
sub convertCommand {
	my($this) = @_;
#	$this->{srchoutfile} = "$this->{filebase}.srchout";
	"parse_homsearch.pl -classname=Game " .
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

	my $cmd = GenomeHomSearch::Game->new(
		sp1=>$sp1, sp2=>$sp2, program=>'game');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
