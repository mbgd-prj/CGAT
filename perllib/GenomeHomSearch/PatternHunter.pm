#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::PatternHunter;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'phn';
}
sub parser_classname {
	'Blast';
}
sub searchCommand {
	my($this, $query, $database) = @_;
	system("$this->{program} -i $query -j $database $this->{progopt} -P ");
}
sub convertCommand {
	my($this) = @_;
	$this->{srchoutfile} = "$this->{filebase}.srchout";
	"parse_homsearch.pl -classname=PatternHunter " .
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

	my $cmd = GenomeHomSearch::PatternHunter->new(
		sp1=>$sp1, sp2=>$sp2, program=>'phn');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
