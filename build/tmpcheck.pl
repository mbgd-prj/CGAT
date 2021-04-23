#!/usr/bin/perl

foreach $f (<../work/blast*srchres.*>){
	$tail = `tail -1 $f`;
	if ($tail !~ /\/\//) {
		print "$f $tail\n";
	}
}
