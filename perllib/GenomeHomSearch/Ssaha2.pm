#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Ssaha2;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'ssaha2';
}
sub searchCommand {
	my($this, $query, $database) = @_;
	print STDERR "$this->{program} $query $database $this->{progopt} ";
	system("$this->{program} $query $database $this->{progopt} " .
			## the following options are required for formatting
			"-tags 1 -align 1 ");
	my $stat = ($? >> 8);
	if ($stat == 1) {
		## the command return 1
		return 0;
	} else {
		return $stat;
	}
}
#sub parser_classname {
#	my($this) = @_;
#	## use Blat parser for parsing PSL format
#	'Blat';
#}
sub convertCommand {
	my($this) = @_;
	$this->{srchoutfile} = "$this->{filebase}.srchout";
	"parse_homsearch.pl -classname=Ssaha2 " .
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

	my $cmd = GenomeHomSearch::Ssaha2->new(
		sp1=>$sp1, sp2=>$sp2, program=>'ssaha2');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
