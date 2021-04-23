#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
require "CGAT_Conf.pl";
#use OptionList;

###############################################################################
package GenomeFeatureCommand::SimpleRep;
@ISA = qw(GenomeFeatureCommand);

sub options {
    [
	['minoutrep', 'min repeat size', '-m', 1],
	['cutoff', 'cutoff score', '-c', 8],
	['maxrep', 'max repeat size', '-M', 10],
	['matchscore', 'match score', '-S', 1],
	['mismatchscore', 'mismatch score', '-P', -3],
	['cutratio', 'cutoff ratio', '-r', 1],
	['cutrep', 'min repeat num', '-R', 4],
    ];
}
$MAXNAMELEN = 15;
sub execute {
	my($this, $sp, $option) = @_;
	my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp);
	my $gseg = GenomeFeature->new($sp);

	my $optstr = $this->get_optstring($option);

	my $cmd = "$main::CMD_SimpleRep $optstr $genomeseq "; 
	my $fh = FileHandle->new("$cmd |") || die("Can not open $cmd($!)");
	$gseg->add_fields("name", "repnum");

	while (<$fh>) {
		$repnum++;
		my($from1, $to1, $repnum, $name, $pat)= split;
		my $color = $repnum;
		if (length($pat) > $MAXNAMELEN) {
			$pat = substr($pat,0,$MAXNAMELEN) . "...";
		}

		$gseg->addSegment($from1,$to1,1,$color, name=>"$pat",repnum=>$repnum)
	}
	$fh->close();
	$gseg->write_table;
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	die "Usage: $0 species\n" if (! @ARGV);
	GenomeFeatureCommand::SimpleRep->execute($ARGV[0]);
}

###############################################################################
1;#
###############################################################################
