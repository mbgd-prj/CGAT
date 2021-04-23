#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Waba;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'waba';
}
sub searchCommand {
	my($this, $query, $database) = @_;
	my($tmpout1) = "$main::DIR_work/tmpout_waba.1.$$";
	my($tmpout) = "$main::DIR_work/tmpout_waba.$$";

	## STDOUT has been redirected to a ##.srchout file

	## The messages output to stdout by the program should be
	##       redirected to the terminal ("1>&2")

	## We use the output of the 2nd pass rather than that of the 3rd pass
	##  since the overlap resolution process in the 3rd pass seems to
	##  eliminate genuine orthologous alignments erroneously.
	## Instead, we use the overlap resoution process in CGAT.
	system("$this->{program} 1 $query $database $tmpout1 $this->{progopt} 1>&2");
	system("$this->{program} 2 $tmpout1 $tmpout 1>&2");
##	return($?) if ($?);
	## The program output stored in tmpfile should be output to STDOUT
	system("cat $tmpout"); unlink($tmpout); unlink($tmpout1);
	return 0;
}
sub convertCommand {
	my($this) = @_;
	$this->{srchoutfile} = "$this->{filebase}.srchout";
	"parse_homsearch.pl -classname=Waba " .
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

	my $cmd = GenomeHomSearch::Waba->new(
		sp1=>$sp1, sp2=>$sp2, program=>'waba');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
