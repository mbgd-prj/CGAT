#!/usr/bin/perl
sub cmprFileDate {
	my($file1, $file2) = @_;
	use File::stat;
	if (! -e $file2) {
		return 1;
	} elsif (! -e $file1) {
		return -1;
	}
	$st1 = stat($file1);
	$st2 = stat($file2);
	return $st1->mtime <=> $st2->mtime;
}
1;
