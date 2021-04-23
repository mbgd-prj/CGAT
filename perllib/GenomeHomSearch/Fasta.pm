#!/usr/bin/perl -s

###############################################################################
use strict;
use GenomeHomSearch;

#use File::Basename;
#use File::Path;
#use FileHandle;
#use CmdProc;
#use POSIX ":sys_wait_h";
#
#require "CGAT_Conf.pl";
#require "libCheckPoint.pl";

#$main::DEBUG = 1;

###############################################################################
package GenomeHomSearch::Fasta;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'fasta';
}
sub searchCommand {
	my($this, $query) = @_;
	system("$this->{program} -Q $this->{progopt} $query "
		. "$this->{database} $this->{opt}->{ktup} ");
}
sub execSearch {
	my($this, $query, $idx) = @_;
	$this->execMultiSearch($query, $idx);
}
sub convertCommand {
	my($this) = @_;
	"parse_homsearch.pl -classname=Fasta " .
		"-sp1=$this->{sp1} -sp2=$this->{sp2} " .
		"-outpath=default -";
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
	my $cmd = GenomeHomSearch::Fasta->new(
		sp1=>$sp1, sp2=>$sp2, program=>'fasta',
		QueryLen=>2000, QueryOverlap=>1000, QueryNum=>100
	);
	$cmd->execute;

	exit;
}

##############################################################################
1;#
##############################################################################
