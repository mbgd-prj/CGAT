#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
use Sequence;
require "CGAT_Conf.pl";
use GenomeHomSearch::Blast;

###############################################################################
package GenomeFeatureCommand::Blast;
@ISA = qw(GenomeFeatureCommand);

sub options {
    [
	['evalue', 'E-value', '-e ', 1e-5],
	['program', 'program', '-p ', 'blastn', 'radio', ['blastn','tblastn','tblastx']],
	['queryname', 'query name', '###', 'query'],
	['queryseq', 'query sequence', '###', '', 'textarea'],
    ];
}
sub execute {
	my($this, $sp, $qseq, $option) = @_;
	my($repnum);
	my $tmp_filebase = "/tmp/blasttmp.$sp.$$";

	my $gseg = GenomeFeature->new($sp, '', {'colorclass'=>'Similarity'});
	my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp);

	my $optstr = $this->get_optstring($option);
	my %BlastOpt  = (
		program=>$option->{program},
		progopt => "$optstr",
		SkipPostProc => 1,
		sp2 => $sp, query => $qseq,
		filebase => $tmp_filebase,
	);

	chdir($DIR_work);
	
	my $blastcom = GenomeHomSearch::Blast->new( %BlastOpt );
	$blastcom->execute;
	my $aliList = $blastcom->get_alignments;

	$gseg->add_fields("name", "identity", "score");

	foreach my $ali ($aliList->list) {
		my($from, $to, $dir, $name, $ident, $score) =
			($ali->from2,$ali->to2,$ali->dir,
				$ali->name1, $ali->ident, $ali->score);

		$color = $gseg->{colortab}->getColor($ali->score);

		$gseg->addSegment($from,$to,$dir,$color,
			name=>$name, identity=>$ident,score=>$score);
	}
	$gseg->write_table;
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	my $opt = {};
	$opt = {program=>$program} if ($program);
	GenomeFeatureCommand::Blast->execute($ARGV[0], $ARGV[1], $opt);
}

###############################################################################
1;#
###############################################################################
