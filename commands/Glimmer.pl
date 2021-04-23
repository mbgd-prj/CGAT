#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
use Sequence;
require "CGAT_Conf.pl";
use OptionList;

###############################################################################
package GenomeFeatureCommand::Glimmer;
@ISA = qw(GenomeFeatureCommand);


## You can add the path of glimmer command explicitly here
##$ENV{PATH} .= ":/bio/package/glimmer/glimmer2.02";
$ENV{PATH} .= ":/bio/package/glimmer/glimmer3.02/scripts";

## Set the version of Gimmer (2 or 3)
$GLIMMER_VERSION = 3;

if ($GLIMMER_VERSION == 2) {
	$glimmerCmd = 'run-glimmer2';
	$glimmerOut = 'g2.coord';
} elsif ($GLIMMER_VERSION == 3) {
##	$glimmerCmd = 'g3-iterated.csh';  # the elph program is needed
	$glimmerCmd = 'g3-from-scratch.csh';
	$glimmerOutTag = 'g3';
	$glimmerOut = "$glimmerOutTag.predict";
} else {
	die "GLIMMER_VERSION must be 2 or 3.\n";
}

sub options {
	[
	];
}
sub execute {
	my($this, $sp, $option) = @_;
	my($repnum);
	my $gseg = GenomeFeature->new($sp);
	my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp,'update');

	my $optstr = $this->get_optstring($option);
	my $cmd;

	chdir($main::DIR_work);
	if ($GLIMMER_VERSION == 3) {
		$cmd = "$glimmerCmd $optstr $genomeseq $glimmerOutTag";
	} else {
		$cmd = "$glimmerCmd $optstr $genomeseq";
	}
	system("$cmd >/dev/null");
	my $fh = FileHandle->new("$glimmerOut") || die("Can not open $glimmerOut ($!)");

	## deviding the track into tree lanes per strand
	$gseg->add_fields("name","lane");
	$gseg->add_field_option("max_lane\t3");

	while (<$fh>) {
		$repnum++;
		my($segn, $from, $to, $frame )= split;
		$frame =~ s/\[//;
		if ($frame < 0) {
			$dir = -1;
		} else {
			$dir = 1;
		}
		$lane = abs($frame);
		$color = "#ffffff";

		$gseg->addSegment($from,$to,$dir,$color,
					name=>"$segn", lane=>"$lane",);
	}
	$fh->close();
	$gseg->write_table;
	if ($GLIMMER_VERSION == 2) {
		unlink("g2.coord");
		unlink("tmp.model");
		unlink("tmp.train");
		unlink("tmp.coord");
	} elsif ($GLIMMER_VERSION == 3) {
		foreach my $f (<$glimmerOutTag.*>) {
			unlink($f);
		}
	}
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	GenomeFeatureCommand::Glimmer->execute($ARGV[0]);
}

###############################################################################
1;#
###############################################################################
