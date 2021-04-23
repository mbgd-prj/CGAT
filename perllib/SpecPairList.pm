#!/usr/bin/perl

require 'CGAT_Conf.pl';
#########################################
package SpecPairList;
$Default_SpecListFile = "$main::DIR_cgathome/etc/speclist";
sub new {
	my($class, $speclist_file) = @_;
	my($this) = {};
	bless $this, $class;
	$speclist_file = $Default_SpecListFile if (! $speclist_file);
	$this->readFile($speclist_file);
	$this;
}
sub readFile {
	my($this, $speclist_file) = @_;
	open(F, $speclist_file);
	my(%SpecPairData);
	my(@SpecPairList);
	my($flag);
	local(%Macro);
	while(<F>){
		if (/^#/) {
			next;
		} elsif (/^SET (\S+)\s*=\s*(.*)/) {
			$varname= $1;
			$value= $2;
			$Macro{$varname} = $value;
			next;
		}
		if (! $flag) {
			%Macro = &parseMacros;
			$flag = 1;
		}
		my($sp_set, $progs, $flag_upd, $flag_pub) = split(/\t/);
		my @species = split(/,/, $sp_set);
		my @programs = split(/ /, $progs);
		my(@newprogs0, @newprogs);
		foreach my $pn (@programs) {
			if ($pn =~ /^\$/) {
				$pn =~ s/^\$//;
				push(@newprogs0, @{ $Macro{$pn} });
			} else {
				push(@newprogs0, $pn);
			}
		}
		foreach my $pn (@newprogs0) {
			push(@newprogs, expandFilenames($pn));
		}
		@programs = @newprogs;
		for ($i = 0; $i < @species; $i++) {
			for ($j = $i+1; $j < @species; $j++) {
				$sp1 = $species[$i];
				$sp2 = $species[$j];
				next if $this->exists($sp1,$sp2);
				$SpecPairData{$sp1,$sp2} = {
					programs => \@programs,
					flag_upd => $flag_upd,
					flag_pub => $flag_pub,
				};
				push(@SpecPairList, [$sp1,$sp2]);
			}
		}
	}
	close(F);
	$this->{SpecPairData} = \%SpecPairData;
	$this->{SpecPairList} = \@SpecPairList;
}
sub exists {
	my($this, $sp1, $sp2) = @_;
	if ($this->{SpecPairData}->{$sp1,$sp2}) {
		return 1;
	} elsif ($this->{SpecPairData}->{$sp2,$sp1}) {
		return -1
	} else {
		return 0;
	}
}
sub getSpecPairData {
	my($this, $sp1, $sp2) = @_;
	if ($this->{SpecPairData}->{$sp1,$sp2}) {
		return $this->{SpecPairData}->{$sp1,$sp2};
	} elsif ($this->{SpecPairData}->{$sp2,$sp1}) {
		return $this->{SpecPairData}->{$sp2,$sp1};
	} else {
		return 0;
	}
}
sub flag_update {
	my($this, $sp1, $sp2) = @_;
	my $spdata = $this->getSpecPairData($sp1,$sp2);
	if (! $spdata || $spdata->{flag_upd} eq "0") {
		return 0;
	} else {
		return 1;
	}
}
sub flag_public {
	my($this, $sp1, $sp2) = @_;
	my $spdata = $this->getSpecPairData($sp1,$sp2);
	if (! $spdata || $spdata->{flag_pub} eq "0") {
		return 0;
	} else {
		return 1;
	}
}
sub getPrograms {
	my($this, $sp1,$sp2) = @_;
	@{ $this->getSpecPairData($sp1,$sp2)->{programs} };
}
sub getPairs {
	my($this) = @_;
	return @{ $this->{SpecPairList} };
}
sub expandFilenames {
	my($filelist) = @_;
	my(@ret_files);
	foreach my $v (split(/ /, $filelist)) {
		my @files = <$main::DIR_build/$v>;
		foreach my $f (@files) {
			if (-f $f && -x $f) {
				push(@ret_files, $f);
			}
		}
	}
	@ret_files;
}
sub parseMacros{
	local($VisitedFlag);
	local(%NewMacro);
	foreach $var (keys %Macro) {
		&parseMacro_sub($var);
	}
	%NewMacro;
}
sub parseMacro_sub{
	my($var) = @_;
	if ($NewMacro{$var} eq '##INIT##') {
		die "Error: $0: The macro definition ($var) contains a loop\n";
	} elsif ($NewMacro{$var}) { 
		return $NewMacro{$var};
	}
	$NewMacro{$var} = '##INIT##';
	my(@ret_files);
	foreach $v (split(/ /, $Macro{$var})) {
		if ($v =~ /^\$(\w+)/) {
			my $varname = $1;
			if ($Macro{$varname}) {
				my $new_val = &parseMacro_sub($varname);
				push(@ret_files, @{$new_val});
			}
		} else {
			push(@ret_files, $v);
		}
	}
	$NewMacro{$var} = \@ret_files;
}
############################################################
if ($0 eq __FILE__) {
	my $spList = SpecPairList->new;
	foreach my $pair ($spList->getPairs) {
		($sp1,$sp2) = @{$pair};
		@programs = $spList->getPrograms($sp1,$sp2);
		print "$sp1,$sp2\n";
		print join(' ', @programs),"\n";
	}
}
############################################################
1;
