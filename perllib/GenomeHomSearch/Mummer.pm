#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Mummer;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'nucmer';
}
sub searchCommand {
	my($this, $query, $database) = @_;
	my($basename) = "$this->{filebase}";
#	$this->{srchoutfile} = "";
	## -o option to generate a coorinate file
	system("$this->{program} -o -p $basename $this->{progopt} "
				. "$query $database ");
	return 1 if ($? != 0);
	open(IN, "$this->{filebase}.delta") || die;
	open(OUT, ">>$this->{srchoutfile}") || die;
	while(<IN>) {
		print OUT $_;
	}
	close(IN);close(OUT);
	return 0;
}
sub resultOutFile {
	my($this) = @_;
	"$this->{filebase}.delta";
}
sub convertCommand {
	my($this) = @_;
	$this->{srchoutfile} = "$this->{filebase}.delta";
	"parse_homsearch.pl -classname=Mummer " .
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

	my $cmd = GenomeHomSearch::Mummer->new(
		sp1=>$sp1, sp2=>$sp2, program=>'nucmer');

	$cmd->execute();

	exit;
}

##############################################################################
1;#
##############################################################################
