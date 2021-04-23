#!/usr/bin/perl -s

##
## karlinB.pl [-list=LISTFILE] USAGEFILE
##

$cod = {
F=>["TTT","TTC"],
L=>["TTA","TTG","CTT","CTC","CTA","CTG"],
I=>["ATT","ATC","ATA"],
M=>["ATG"],
V=>["GTT","GTC","GTA","GTG"],
S=>["TCT","TCC","TCA","TCG","AGT","AGC"],
P=>["CCT","CCC","CCA","CCG"],
T=>["ACT","ACC","ACA","ACG"],
A=>["GCT","GCC","GCA","GCG"],
Y=>["TAT","TAC"],
H=>["CAT","CAC"],
Q=>["CAA","CAG"],
N=>["AAT","AAC"],
K=>["AAA","AAG"],
D=>["GAT","GAC"],
E=>["GAA","GAG"],
C=>["TGT","TGC"],
W=>["TGG"],
R=>["CGT","CGC","CGA","CGG","AGA","AGG"],
G=>["GGT","GGC","GGA","GGG"],
};
$MINLEN = 100 if (! $MINLEN);

$CODPOS = 3 if (! $CODPOS);
@CODPOS = split(/,/, $CODPOS);

foreach $a (keys %{$cod}) {
	foreach $c (@{$cod->{$a}}) {
		$am{$c} = $a;
	}
}
if ($Header) {
	if ($AllOut) {
		@OUT = ("GC", "AG", "AC");
	} else {
		@OUT = ("GC");
	}
	foreach $c (@CODPOS) {
		foreach $x (@OUT) {
			print " $x$c";
		}
	}
	print "\n";
}

while (<>) {
	if (! $flag) {
		@codons = split;
		for ($i = 0; $i < @codons; $i++) {
			for ($j = 0; $j < 3; $j++) {
				$n = substr($codons[$i], $j, 1);
				$CodPosNt[$i]->[$j] = $n;
			}
		}
		$flag = 1;
	} else {
		my(@data);
		($name, @data) = split;
		$codsum = 0;
		for ($i = 0; $i < @data; $i++) {
			$codsum += $data[$i];
		}
		next if ($codsum < $MINLEN);
		push(@Data, \@data);
		push(@Names, $name);
	}
}

$j = 0;
foreach $data (@Data) {

	print "$Names[$j]";
	foreach $cdpos (@CODPOS) {
		undef(%Cnt);
		$sum = 0;
		for ($i = 0; $i < @{$data}; $i++) {
			$Cnt{$CodPosNt[$i]->[$cdpos-1]} += $data->[$i];
			$sum += $data->[$i];
		}
		$Cntnt[$cdpos]->{GC} = ($Cnt{G} + $Cnt{C}) * 100 / $sum;
		$Cntnt[$cdpos]->{AG} = ($Cnt{A} + $Cnt{G}) * 100 / $sum;
		$Cntnt[$cdpos]->{AC} = ($Cnt{A} + $Cnt{C}) * 100 / $sum;
	}
	foreach $cdpos (@CODPOS) {
		printf(" %.1f", $Cntnt[$cdpos]->{GC});
		if ($AllOut) {
			printf(" %.1f", $Cntnt[$cdpos]->{AG});
			printf(" %.1f", $Cntnt[$cdpos]->{AC});
		}
	}
	print "\n";
	$j++;
}
