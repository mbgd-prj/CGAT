#!/usr/bin/perl
use Alignment;
use Tools::CGAT;
$file = $ARGV[0];
$seqfile1 = $ARGV[1];
$seqfile2 = $ARGV[2];
$cgat_parser = Tools::CGATParser->new($file);
$seq1 = SeqFile->getSequence($seqfile1);
$seq2 = SeqFile->getSequence($seqfile2);

while ($res = $cgat_parser->read) {
	foreach $hsp ($res->hsp_list) {
		$ali = $hsp->generateAlignmentAll($seq1,$seq2);
		my @alignments = $ali->split_alignment(15,10);
		print "ALIGNMENT(Before)\n";
		$ali->print_alignment;
		print "ALIGNMENT(After)\n";
		foreach $a (@alignments) {
			$a->print_alignment;
			print "//\n";
		}
	}
}
