#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
require "CGAT_Conf.pl";
use OptionList;

$CMD_tRNAscan = "/bio/package/tRNAscan-SE/bin/tRNAscan-SE";

###############################################################################

$Options = [
	[prokaryotic, "-B", "ON"],
	[eukaryotic, "-A", ""],
];
sub tRNAscan {
	my($sp, $option) = @_;
	my $gseg = GenomeFeature->new($sp);
	my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp);

	my $opt = OptionList->new($Options, '');
	$opt->set_options($option);
	my $optstr = $opt->get_optstring;

	my $cmd = "$main::CMD_tRNAscan $optstr $genomeseq ";
	my $fh = FileHandle->new("$cmd |") || die("Can not open $cmd($!)");
	my $flag = 0;

	$gseg->add_fields("score", "codon","ifrom","ito");
	while (<$fh>) {
		if (/^Sequence/ || /^Name/ || /^\-\-\-/) {
			$flag++; next;
		}
		next if (! $flag);
		my($name,$num, $from, $to, $type, $codon, $ifrom,$ito,$score) = split;
		my $color = $gseg->{colortab}->getColor($repnum);
		if ($from < $to) {
			$dir = 1;
	 	} else {
			$dir = -1;
			$tmp = $to; $to = $from; $from = $tmp;
		}

		$gseg->addSegment($from,$to,$dir,$type,
			score=>$score,codon=>$codon,ifrom=>$ifrom,ito=>$ito);
	}
	$fh->close();
	$gseg->write_table;
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	tRNAscan($ARGV[0]);
}

###############################################################################
1;#
###############################################################################
