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

foreach $a (keys %{$cod}) {
	foreach $c (@{$cod->{$a}}) {
		$am{$c} = $a;
	}
}
if (-f $list) {
	open(L,$list) || die;
	while (<L>) {
		chop;
		$name = $_;
		$Fam{$name} = 1;
	}
	close(L);
}

while (<>) {
	if (! $flag) {
		@codons = split;
		$flag = 1;
	} else {
		my(@data);
		($name, @data) = split;
		$codsum = 0;
		for ($i = 0; $i < @data; $i++) {
			$codsum += $data[$i];
		}
		for ($i = 0; $i < @data; $i++) {
			if (! %Fam || $Fam{$name}) {
				$sum[$i] += $data[$i];
			}
		}
		next if ($codsum < $MINLEN);
		push(@Data, \@data);
		push(@Names, $name);
	}
}
$j = 0;
for ($i = 0; $i < @sum; $i++) {
	$Sum{$am{$codons[$i]}} += $sum[$i];
}
for ($i = 0; $i < @sum; $i++) {
	if ($Sum{$am{$codons[$i]}}) {
		$sum[$i] /= $Sum{$am{$codons[$i]}};
	}
}
foreach $data (@Data) {
	undef(%Sum);
	$Tot = $diffSum = 0;
	for ($i = 0; $i < @{$data}; $i++) {
		$Sum{$am{$codons[$i]}} += $data->[$i];
		$Tot += $data->[$i];
	}
	for ($i = 0; $i < @{$data}; $i++) {
		if ($Sum{$am{$codons[$i]}}) {
			$data->[$i] /= $Sum{$am{$codons[$i]}};
		}
		$diffSum += abs($data->[$i] - $sum[$i]) * $Sum{$am{$codons[$i]}} / $Tot;
	}
	print "$Names[$j] ", int($diffSum * 1000), "\n";
#	print "$Names[$j] ", join(' ', @{$data}),"\n";
	$j++;
}
