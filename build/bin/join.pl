#!/usr/bin/perl -s

$file1 = shift @ARGV;
$file1 = "<&STDIN" if ($file1 eq '-');
$file2 = "<&STDIN" if ($file2 eq '-');
$spChar1 = "\\s+" if (! $spChar1);
$spChar2 = "\\s+" if (! $spChar2);
$spChar1 = "/$spChar1/";
$spChar2 = "/$spChar2/";
$outSep = ' ' if (! $outSep);

open(F, $file1) || die;

while (<F>) {
	chop;
	($name, @val) = eval "split($spChar1)";
	$name =~ tr/a-z/A-Z/ if ($upper);
	push(@Names, $name);
	$Val{$name} = join($outSep, @val);
}
close(F);

foreach $file2 (@ARGV) {
	undef %newval;
	$file2 = "<&STDIN" if ($file2 eq '-');
	if (open(F, $file2)) {
		while (<F>) {
			chop;
			($name, @val) = eval "split($spChar2)";
			$name =~ tr/a-z/A-Z/ if ($upper);
			if (! @val) {
				$newval{$name} = "1";
			} else {
				$newval{$name} = join($outSep, @val);
			}
		}
		close(F);
	}
	foreach $name (@Names) {
		if ($newval{$name}) {
			$Val{$name} .= "$outSep$newval{$name}";
		} else {
			$Val{$name} .= "${outSep}0";
		}
	}
}

foreach $name (@Names) {
	print "$name$outSep$Val{$name}\n", 
}
