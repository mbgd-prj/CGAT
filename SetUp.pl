#!/usr/bin/env perl


use File::Basename;
use File::Copy;
use File::Path;
use Cwd;
$PROGDIR=File::Basename::dirname($0);
$PROGDIR =  Cwd::abs_path($PROGDIR);

chdir $PROGDIR;

$CSHRC="etc/cgat.csh";
$SHRC="etc/cgat.sh";
$CommonPath="perllib/CGAT_commonPath.pl";

$OSNAME = `uname -s`; chomp $OSNAME;

## set CGAT_HOME
if (! $CGAT_HOME) {
	print "The top directory of CGAT ?\n[default: $PROGDIR] :";
	$ans = <>; chomp $ans;
	$CGAT_HOME= ($ans ? $ans : $PROGDIR);
}
$ENV{CGAT_HOME}= $CGAT_HOME;
$ENV{PERLLIB}= "$ENV{CGAT_HOME}/perllib";

foreach $script ("$CSHRC", "$SHRC") {
	open(I, "$script.in") || die "Can't open $script.in\n";
	open(O, ">$script") || die "Can't open $script\n";
	while(<I>){
		s#>>ENV_CGAT_HOME<<#${CGAT_HOME}#;
		print O $_;
	}
	close(I); close(O);
}

open(I, "$CommonPath.in") || die "Can't open $CommonPath.in\n";
open(O, ">$CommonPath") || die "Can't open $CommonPath\n";

while(<I>){
	if (/ENV_CGAT_HOME/) {
		s/>>ENV_CGAT_HOME<</${CGAT_HOME}/;
	} elsif (/SYS_NAME/) {
		s/>>SYS_NAME<</${OSNAME}/;
	}
	if (/Unix Commands/) {
		$flag = 1;
	} elsif (/CGAT Commands/) {
		$flag = 0;
	}
	if ($flag && ! /^#/ && /CMD_(\S+)/) {
		chomp;
		$cmdname = $1;
		if ($cmdname =~ /:/) {
			($cmdname, $systype) = split(/:/, $cmdname);
			next if ($OSNAME !~ /$systype/i);
		}
		$cmdpath = `which $cmdname`; chomp $cmdpath;
		if ($cmdpath) {
			printf O qq{\$CMD_%-12s\t\t="%s";\n},
				$cmdname, $cmdpath;
		} else {
			print STDERR "Warning: $cmdname not found\n";
		}
	} else {
		print O $_;
	}
}
close(I); close(O);


## copy installed alignment programs
File::Path::mkpath("build/align");
foreach $n (<build/align.in/*>) {
	next if ($n =~ /Template$/);
	if (-f "$n" && -x "$n") {
		$ret = system("$n -test dmy dmy");
		if ($ret == 0) {
			## OK: copy align directory
			File::Copy::copy($n, "build/align");
			my($bn) = File::Basename::basename($n);
			chmod(0755, "build/align/$bn");
		} else {
			print STDERR "$n: command not installed\n";
		}
	}
}
## copy etc/speclist
if (! -f "etc/speclist") {
	File::Copy::copy("etc/speclist.in", "etc/speclist");
}

## make C programs
chdir "commands/source";
system("make install");
chdir("../..");

print STDERR "\n==== SetUp done ====\n\n";
print STDERR "  Please remember to execute\n";
print STDERR "      'source etc/cgat.csh' (csh/tcsh) or '. etc/cgat.sh' (sh/bash)\n";
print STDERR "  before running the database building procedure\n\n";
