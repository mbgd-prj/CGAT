#!/usr/bin/perl

require 'CGAT_Conf.pl';
$ClassName = $ARGV[0];
$DefaultProg = $ARGV[1];
die "Usage: $0 ClassName DefaultProg\n" if (@ARGV < 2);

@Files = (
	["$DIR_perllib/GenomeHomSearch", "Template.pm"],
	["$DIR_perllib/Tools", "Template.pm"],
	["$DIR_build/align.in", "Template", 0755],
);

foreach $f (@Files) {
	($Path, $File, $Mode) = @{$f};
	$NewFile = $File; $NewFile =~ s/Template/${ClassName}/;
	open(T, "$Path/$File") || die;
	open(O, ">$Path/$NewFile") || die;
	while(<T>){
		s/>>CLASS<</${ClassName}/g;
		s/>>DEFAULT_PROG<</${DefaultProg}/g;
		print O $_;
	}
	close(O);
	close(T);
	if ($Mode) {
		chmod $Mode, "$Path/$NewFile";
	}
}
