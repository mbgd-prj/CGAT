#!/usr/bin/perl
use SpecPairList;
require 'CGAT_Conf.pl';
use Carp;

$spList = SpecPairList->new;
$getFromMBGD = "$DIR_build/getDataFromMBGD.pl";
&CGAT_Data::set_mode('update');
foreach my $spPair ($spList->getPairs) {
	($sp1,$sp2) = @{$spPair};
	next if (! $spList->flag_update($sp1,$sp2));
	my @programs = $spList->getPrograms($sp1,$sp2);
	if (! -f &CGAT_Data::getGenomeSeqPath($sp1)) {
		&execute($getFromMBGD,$sp1)
	}
	if (! -f &CGAT_Data::getGenomeSeqPath($sp2)) {
		&execute($getFromMBGD,$sp2)
	}
	foreach my $prog (@programs) {
		print STDERR "$sp1,$sp2,$prog\n";
		&execute($prog,$sp1,$sp2);
	}
}

sub execute{
	my($prog,$sp1,$sp2) = @_;
	system("$prog $sp1 $sp2");
	if (! $ignore_error) {
		die "${prog} ${sp1} ${sp2}: Command terminated abnormally\n" if ($?);
	}
}
