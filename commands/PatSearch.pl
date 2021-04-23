#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use GenomeFeature;
use Sequence;
require "CGAT_Conf.pl";

###############################################################################
package GenomeFeatureCommand::PatSearch;
@ISA = qw(GenomeFeatureCommand);

$MAXLEN = 20;

sub options {
    [
	['pattern', 'pattern', '', '']
    ];
}

sub execute {
	my($this, $sp, $option) = @_;
	my($repnum);
	my($pat) = $option->{pattern};

	my $gseg = GenomeFeature->new($sp);
	my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp);

	open(G, $genomeseq) || die;
	while (<G>) {
		next if (/^>/);
		chomp;
		$seq .= $_;
	}
	close(G);

	while ( $seq =~ /($pat)/ig ) {
		$pos = pos $seq;
		$str = $1;
		$to = $pos;
		$from = $to - length($str) + 1;
		push(@pos, {from=>$from, to=>$to, str=>$str, dir=>1});
	}

	$seq = &Sequence::revcompSeq($seq);

	my $seqlen = length($seq);
	while ( $seq =~ /($pat)/ig ) {
		$pos = pos $seq;
		$str = $1;
		$from = $seqlen - $pos + 1;
		$to = $from + length($str) - 1;
		push(@pos, {from=>$from, to=>$to, str=>$str, dir=>-1});
	}
	@pos = sort {$a->{from}<=>$b->{from}} @pos;

	$gseg->add_fields("name");

	foreach my $p (@pos) {
		my($from,$to,$dir);
		my $str = $p->{str};
		if (length($str) > $MAXLEN) {
			$str = substr($str,0,$MAXLEN) . "...";
		}

		my $color = $gseg->{colortab}->getColor();
		$gseg->addSegment($p->{from},$p->{to},$p->{dir},
			$color, name=>$str );
	}
	$gseg->write_table;
	return 0;
}
###############################################################################
if ($0 eq __FILE__) {
	GenomeFeatureCommand::PatSearch->execute($ARGV[0], {pattern=>$ARGV[1]});
}

###############################################################################
1;#
###############################################################################
