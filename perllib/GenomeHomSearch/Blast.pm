#!/usr/bin/perl -s

use strict;
use GenomeHomSearch;

##############################################################################
package GenomeHomSearch::Blast;
use base qw(GenomeHomSearch);
sub new {
	my($class, %opt) = @_;
	my $this = $class->SUPER::new(%opt);
	$this;
}
sub defaultProgname {
	'blastn';
}
sub execProgname {
	'blastall';
}
sub create_dbindex {
	my($this) = @_;
	my($logdir) = "$main::DIR_log/formatdb";
	my($typeopt) = "-p F";
	my($fileBase) = File::Basename::basename($this->{database});
#	File::Path::mkpath($logdir, 0, 0750);
#	system("$main::CMD_formatdb $typeopt -i $this->{database} " .
#			"-l $logdir/formatdb.$fileBase.log")
#		&& exit($? >> 8);
	
	$this->{cmdproc}->execute([{
		procname =>"$main::CMD_formatdb $typeopt -i $this->{database}",
		depend => [$this->{database}],
		target => "$this->{database}.nhr",
	}]);
}
sub searchCommand {
	my($this, $query) = @_;
	my($progname);
	$ENV{BLASTMAT} = $main::DIR_blastmat if (! $ENV{BLASTMAT});
	if ($this->{program} eq 'megablast') {
		$progname = $main::CMD_megablast;
		if ($this->{progopt} !~ /-D 2/) {
			# specify the traditional output format
			#   (not the default in previous versions)
			$this->{progopt} .= " -D 2";
		}
	} else {
 		$progname = "$main::CMD_blastall -p $this->{program}";
	}
	system("$progname -d $this->{database} -i $query $this->{progopt} ");
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

#	my $cmd = GenomeHomSearch::Blast->new(
#		sp1=>$sp1, sp2=>$sp2, program=>'blastn',
#		QueryLen=>2000, QueryOverlap=>1000, QueryNum=>100,
#		SkipPostProc=>1,
#	);

	my $cmd = GenomeHomSearch::Blast->new(
		sp1=>$sp1, sp2=>$sp2, program=>'megablast',
		SkipPostProc=>1,
	);

	$cmd->execute();
	my $align = $cmd->get_alignments;
	foreach my $ali ($align->list) {
		$ali->print_region;
	}

	exit;
}

##############################################################################
1;#
##############################################################################
