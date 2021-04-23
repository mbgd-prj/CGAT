#!/usr/bin/perl

use File::Basename;

$pat = $ARGV[0];
@DIRS = ('perllib', 'perllib/Tools',
	'perllib/GenomeHomSearch', 'perllib/Alignment',
	'build', 'build/align.in', 'build/segment', 'build/bin',
	'build/geneattr', 'build/common', 'etc',
	'htdocs/CGAT/cgi-bin', 'htdocs/CGAT/dynSearch', 'commands');
$CGATDIR = dirname($0) . "/..";
foreach $dir (@DIRS) {
	foreach $f (<$CGATDIR/$dir/*>) {
		&search_pat($f, $pat);
	}
}
sub search_pat {
	my($file, $pat) = @_;
	my($flag, $foundflag);
	open(F, $file) || die;
	while (<F>) {
		if (! $flag) {
			if (/^#!.*perl/) {
			} else {
				close(F); return;
			}
			$flag = 1;
		}
		print "$file: $_" if (/$pat/i);
	}
	close(F);
}
