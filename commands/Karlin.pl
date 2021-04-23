#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
require "CGAT_Conf.pl";
use OptionList;

###############################################################################

#$Options = [
#	[header, '-h', ON],
#	[minlen, '-l', 150],
#	[rscu, '-R', ON],
#];

sub Karlin {
	my($sp, $option) = @_;
	my($repnum);
	my $ntseq = &CGAT_Data::getGeneNtSeqPath($sp);

	my $optstr = OptionList->new($Options, '')->set_options($option)
				->get_optstring;

	my $cmd = "$main::CMD_usage $ntseq -h | $main::CMD_karlinB ";
	my $fh = FileHandle->new("$cmd |") || die("Can not open $cmd($!)");

	while (<$fh>) {
		print;
	}
	$fh->close();
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	die "Usage: $0 species\n" if (! @ARGV);
	Karlin($ARGV[0]);
}

###############################################################################
1;#
###############################################################################
