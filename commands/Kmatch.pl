#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
use Sequence;
require "CGAT_Conf.pl";

###############################################################################
package GenomeFeatureCommand::Kmatch;
@ISA = qw(GenomeFeatureCommand);

sub options {
    [
	[inverted, 'inverted', '-C', '', 'hidden'],
	[maxdiff, 'max distance', '-D', 1000],
	[mindiff, 'min distance', '-d', ''],
	[mismatch, 'mismatch num', '-M', 5],
	[minmatchlen, 'min match length', '-L', 30],
	[missratio, 'mismatch ratio', '-R', .1],
	[cutoff_prob, 'cutoff probability', '-P', ''],
    ];
}
sub execute {
	my($this, $sp, $option) = @_;
	my($repnum);
	my $gseg = GenomeFeatureSegpair->new($sp);
	$gseg->add_fields("length", "interval");
	my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp);

	my $optstr = $this->get_optstring($option);

	my $len = SeqFile->getSequence($genomeseq)->length;

	my $cmd = "$main::CMD_Kmatch $optstr $genomeseq | " .
		"$main::CMD_ConcatAlign -seqfile=$genomeseq";
	my $fh = FileHandle->new("$cmd |") || die("Can not open $cmd($!)");

	while (<$fh>) {
		$repnum++;
		my($from1, $to1, $from2, $to2)= split;
		my $color = $gseg->{colortab}->getColor($repnum);
		my $interval = $from2 - $to1 - 1;
		my $length = $to1 - $from1 + 1;

		$gseg->addSegment($from1,$to1,$from2,$to2,1,$color,
					"rep_$repnum",
				length=>$length, interval=>$interval);
	}
	$fh->close();
	$gseg->write_table;
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	GenomeFeatureCommand::Kmatch->execute($ARGV[0]);
}

###############################################################################
1;#
###############################################################################
