#!/usr/bin/perl -s
#
# conv_GIB_IS.pl:
#
#   A simple command for converting the GIB-IS (available at
#   http://bioportal.ddbj.nig.ac.jp/is/) database to the
#   FASTA-formatted sequence file
#
# Usage: conv_GIB_IS.pl [-nr] inputfile
#
#   Input file:	"IS-table-text.txt" file available of the above site.
#   Options:
#		-nr   Non-redundant. Remove identical sequences.
#
#
# To install the GIB-IS database, execute the following commands:
#
# % conv_GIB_IS.pl -nr IS-table-text.txt > isdb
# % mv isdb $CGAT_HOME/database/isdb
#
#

use Digest::MD5;

while (<>) {
	chomp;
	($accNo,$org,$loc,$len,$seq,$ISname,$synonym,
		$iso,$comm1,$comm2,$comm, $fam) = split(/\t/);
	next if ($accNo eq '' || $accNo eq 'version');
	$iso = '-' if (! $iso);
	$fam = 'unknown' if (! $fam);
	$seq_sum = Digest::MD5::md5($seq);
	if ($nr && $Found{$seq_sum}) {
		# skip already included sequence
		next;
	}
	$Found{$seq_sum} = 1;
	print ">$ISname $accNo [$org] Iso:$iso Fam:$fam\n";
	for ($i = 0; $i < length($seq); $i+= 60) {
		$s = substr($seq, $i, 60);
		print "$s\n";
	}
}
