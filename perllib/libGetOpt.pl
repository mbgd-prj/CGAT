#!/usr/bin/perl -s

use strict;

sub getOptHomology {
	my($ARGV, $options, $default_opt) = @_;
	if (@$ARGV < 2) {
		die "Usage: $0 sp1 sp2 [optvar=value ..]\n";
	}
	$options->{sp1} = $ARGV->[0];
	$options->{sp2} = $ARGV->[1];
	foreach my $op (split(/,/, $ARGV->[2])) {
		my($key,$value) = split(/=/, $op);
		if ($op !~ /=/) {
			$value = 1;
		}
		$options->{$key} = $value;
	}
	if (ref $default_opt) {
		foreach my $op (keys %{$default_opt}) {
			if (! defined $options->{$op}){
				$options->{$op} = $default_opt->{$op};
			}
		}
	}
	$options;
}
#############################################################
1;
