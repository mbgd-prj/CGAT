#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
use Alignment::FindGap;
use Tools::ReadHomologyData;
require "CGAT_Conf.pl";
#use OptionList;

###############################################################################
package GenomeFeatureCommand::FindGap;
@ISA = qw(GenomeFeatureCommand);

sub options {
    [
#	['minoutrep', 'min repeat size', '-m', 1],
#	['cutoff', 'cutoff score', '-c', 8],
#	['maxrep', 'max repeat size', '-M', 10],
#	['matchscore', 'match score', '-S', 1],
#	['mismatchscore', 'mismatch score', '-P', -3],
#	['cutratio', 'cutoff ratio', '-r', 1],
#	['cutrep', 'min repeat num', '-R', 4],
    ];
}
sub execute {
	my($this, $sp1, $sp2, $program, $option) = @_;
	my($segN);
	my($fname) = &main::getAlignFilePath($sp1,$sp2,"align",$program);
	my($spout) = $sp1;

	if (-f "$fname") {
		$segN = 1;
	} else {
		my($tmp) = $sp1; $sp1 = $sp2; $sp2 = $tmp;
		$fname = &main::getAlignFilePath($sp1,$sp2,'align',$program);
		$segN = 2;
	}

	my $gseg = GenomeFeature->new($spout);

	my $optstr = $this->get_optstring($option);

	my $ali = Alignment->new;
	my $aliList = Tools::ReadCGATHomologyData::read($sp1,$sp2,$program);
	my $findGap = Alignment::FindGap->new($aliList);
	my $segList = $findGap->FindGap($segN);
	$color = $gseg->{colortab}->getColor(0);

	$gseg->add_fields("length");

	foreach $seg ($segList->list) {
		$len = $seg->length;
		$gseg->addSegment($seg->from,$seg->to,1,$color,
			length=>$len);
	}
	$gseg->write_table;
	return;
}
###############################################################################
if ($0 eq __FILE__) {
	die "Usage: $0 species1 species2 program\n" if (@ARGV < 3);
	GenomeFeatureCommand::FindGap->execute(@ARGV);
}

###############################################################################
1;#
###############################################################################
