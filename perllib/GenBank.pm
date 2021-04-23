#!/usr/bin/perl -s

package GenBank;
use Sequence;

ATTRDELIM => "\n";
FEATDELIM => "//\n";
ENTDELIM => "";

sub new {
	my($class, @filenames) = @_;
	my($self) = {};
	bless $self, $class;

	if (! @filenames) {
		open(GB, "<&STDIN");
	} else {
		open(GB, shift @filenames) || die;
	}

	$self->{'handle'} = GB;
	$self->{'files'} = \@filenames;
	$self;
}

sub read_entry {
	my($self) = shift;
	my($handle) = $self->{'handle'};
	local(@features);
	local($data);

	if (eof $handle && @{$self->{'files'}}) {
		close($handle);
		open($handle, shift @{$self->{'files'}}) || die;
		$self->{'handle'} = $handle;
	}
	undef($self->{'data'});
	while (<$handle>) {
		chomp;
		if (/^LOCUS /) {
#			$locus_name = substr($_, 12, 10);
#			$length = substr($_, 22, 7);
#			$type = substr($_, 33, 7);
#			$shape = substr($_, 42, 10);
#			$div = substr($_, 52, 3);
#			$date = substr($_, 62, 10);

########################################
## ファイルの中には、$locus_name と $length とが接して記述されているものがあるため、
## $length の最大桁数を ７桁 と仮定して処理を行なっている。
##   例えば、yst_chr_12.gbk  や yst_chr_15.gbk
##
			($locus_name, $length, $type, $shape, $div, $date) = 
/^\w+\s+(\w+?)\s*([0-9]{1,7})\s+bp\s+(DNA)*\s+(circular)*\s*(\w+)\s+([0-9]+-[a-zA-Z]+-[0-9]+)/;

			$data->{'locus_name'} = $locus_name;
			$data->{'length'} = $length;
			$data->{'type'} = $type;
			$data->{'shape'} = $shape;
			$data->{'date'} = $date;

                        # set same data
			$self->{'locus_name'} = $locus_name;
			$self->{'length'} = $length;
			$self->{'type'} = $type;
			$self->{'shape'} = $shape;
			$self->{'date'} = $date;

			$self->{'num_cds'} = 0;
		} elsif (/^DEFINITION[ ]*(.*)$/) {
			$status = 'def';
			$self->{'definition'} = $1;
		} elsif (/^ACCESSION[ ]*(.*)$/) {
			$status = 'acc';
			$self->{'accession'} = $1;
		} elsif (/^VERSION[ ]*(.*)$/) {
			$status = 'ver';
			$self->{'version'} = $1;
		} elsif (/^KEYWORDS[ ]*(.*)$/) {
			$status = 'key';
			$self->{'keywords'} = $1;
		} elsif (/^SEGMENT[ ]*(.*)$/) {
			$status = 'seg';
			$self->{'segment'} = $1;
		} elsif (/^SOURCE[ ]*(.*)$/) {
			$status = 'src';
			$self->{'source'} = $1;
		} elsif (/^REFERENCE[ ]*(.*)$/) {
			$status = 'ref';
			$self->{'reference'} = $1;
		} elsif (/^COMMENT[ ]*(.*)$/) {
			$status = 'com';
			$self->{'comment'} = $1;
		} elsif (/^FEATURES /) {
			$status = 'feat';
			$feat_status = 'location';
		} elsif (/^\S/) {
			if (defined $feat) {
##				&print_feature($feat);
				push(@features, $feat);	
				undef $feat;
			}
			if (/^ORIGIN[ ]*(.*)$/) {
				$status = 'orig';
				$data->{'origin'} = $1;
			} elsif (/^\/\//) {
				last;
			} else {
				$status = 'unknown';
			}
		} elsif (/^ *$/) {
			## skip empty lines
		} elsif (/^ /) {
			if ($status eq 'def') {
				s/^\s+//;
				$self->{definition} .= " $_";
			} elsif ($status eq 'feat') {
				if (substr($_, 5, 1) ne ' ') {
					if (defined $feat) {
						if ($location && ! $feat->{'location'}) {
							$loc = &parse_location($location);
							$feat->{'location'} = $loc;
						}
						push(@features, $feat);	
					#	&print_feature($feat);
						undef $feat;
					}
					($featkey) = split(/\s+/, substr($_, 5));
					$feat_status = $featkey;
					$feat_status2 = 'location';
					$feat->{'keyname'} = $featkey;
					$location = substr($_, 21);

                                        # Count up number of CDS
                                        $self->{'num_cds'}++ if ($featkey eq 'CDS');
				} elsif (substr($_, 21, 1) ne '/')  {
					if ($feat_status2 eq 'location') {
						$location .= substr($_, 21);
					} else {
						if ($attr ne 'translation') {
							$feat->{'attr'}->{$attr} .= ' ';
						}
						$feat->{'attr'}->{$attr} .= substr($_, 21);
					}
				} else {
					if ($feat_status2 eq 'location') {
						$loc = &parse_location($location);
						$feat->{'location'} = $loc;
					}
					($attr, $val) = split(/=/, substr($_, 21));
					$attr =~ s/^\///;
					$feat_status2 = $attr;

					if ($attr =~ /db_xref/) {
						push(@{$feat->{'attr'}->{$attr}}, $val);
					} elsif ($feat->{'attr'}->{$attr}) {

		if (ref($feat->{attr}->{$attr}) ne 'ARRAY') {
			$feat->{'attr'}->{$attr} =[ $feat->{'attr'}->{$attr} ];
		}
		push(@{$feat->{attr}->{$attr}}, &parse_data($val));
	
					} else {
						$feat->{'attr'}->{$attr} =
							&parse_data($val);
					}
				}
			} elsif ($status eq 'orig') {
				s/[^a-z]//g;
				if (/[^gatcrymkswhbvdn]/) {
					print STDERR "Sequence error\n";
				}
				$data->{'seqstr'} .= $_;
			} elsif ($status eq 'ref') {
				if (/^\s+JOURNAL\s+(.+)/) {
					push(@{$data->{'journal'}}, $1);
				}
				elsif (/^\s+(MEDLINE|PUBMED)\s+(\d+)/) {
					$data->{'medline'} = $2;
				}
			}
		}
	}
	if ($data) {
		$data->{'sequence'} = RawSequence->new($data->{'seqstr'});
		$data->{'features'} = \@features;
		$self->{'data'} = $data;
	}
	$data;
}

sub print_data {
	my($self, $outfeat) = @_;
	my($data) = $self->{'data'};

	foreach $f (@{$data->{'features'}}) {
		if (! $outfeat || $outfeat->{$f->{'keyname'}}) {
			&print_feature($f, $outfeat->{$f->{'keyname'}});
		}
	}
	print $ENTDELIM;
}
sub get_subseq {
	my($self, $loc) = @_;
	my($seqstring);
	my($dir) = 1;
	
	if ($loc->{'complement'}) {
		$dir = -1;
	}
	if (defined $loc->{'array'}) {
		foreach $elem (@{$loc->{'array'}}) {
			my $subseq = $self->get_subseq($elem);
			$seqstring .= $subseq;
		}
	} else {
		my($seqstr);
		my($from,$to,$dir) = $self->get_region($loc);
		$seqstring = $self->{data}->{sequence}->subseqString(
						$from, $to, $dir);
	}
#	if($dir < 0) {
#		$seq->complement;
#	}
	$seqstring;
}
sub get_region {
	my($self, $loc, $opt) = @_;
	return &get_region_from_location($loc,1,$self->{length}, $opt);
}

sub parse_location {
	my($posline) = @_;
	my($loc, $from, $to, $elem);
	$loc = {}; ##bless $loc;

	$posline =~ s/^[ ]+//;
	$posline =~ s/[ ]+$//;
	if ($posline =~ /^\(([^)]*)\)/) {
		$loc = &parse_location($1);
	} elsif ($posline =~ /^complement\((.*)\)$/) {
		$loc = &parse_location($1);
		$loc->{'complement'} = 1;
	} elsif ($posline =~ /^(join|order|group|one-of)\((.*)\)$/) {
		local(@loc_array);

		foreach $elem (split(/,/, $2)) {
			push(@loc_array, &parse_location($elem));
		}
		$loc->{'type'} = $1;
		@{$loc->{'array'}} = @loc_array;
	} elsif ($posline =~ /^replace\((.*)\)$/) {
		## Will be removed !!
		$loc->{'type'} = 'replace';
		($from,$to) =  split(/,/, $1);
		$from = &parse_location($from);
		$to = &parse_location($to);
		$loc->{'from'} = $from;
		$loc->{'to'} = $to;
	} elsif ($posline =~ /:/) {
		($entname, $fromto) = split(/:/, $posline);
		$loc = &parse_location($fromto);
		$loc->{'entname'} = $entname;
	} elsif ($posline =~ /\.\./) {
		($from, $to) = split(/\.\./, $posline);
		$loc->{'type'} = 'region';
		$loc->{'from'} = $from;
		$loc->{'to'} = $to;
		&parse_location($from);
		&parse_location($to);
	} elsif ($posline =~ /\./) {
		($from, $to) = split(/\./, $posline);
		&parse_location($from);
		&parse_location($to);
	} elsif ($posline =~ /\^/) {
		($from, $to) = split(/\^/, $posline);
		&parse_location($from);
		&parse_location($to);
	} elsif ($posline =~ /^<([0-9]+)/) {
		$position = $1;
		$loc->{'position'} = $position;
	} elsif ($posline =~ /^>([0-9]+)/) {
		$position = $1;
		$loc->{'position'} = $position;
	} elsif ($posline =~ /^([0-9]+)$/) {
		$position = $1;
		$loc->{'position'} = $position;
	} elsif ($posline =~ /\".*\"/) {
		($string) = &parse_data($posline);
		$loc->{'bases'} = $string;
	} else {
		print "PARSE ERROR (location) $posline\n";
	}
	return $loc;
}

sub set_delimiters {
	my($class, $delim) = @_;
	$ATTRDELIM = $delim->{'ATTRDELIM'};
	$FEATDELIM = $delim->{'FEATDELIM'};
	$ENTDELIM = $delim->{'ENTDELIM'};
}
sub print_feature {
	my($feat, $out_flag) = @_;
	my($outname) = $out_flag->{'_keyname'};
	$outname = $feat->{'keyname'} if ($outname eq '0');

	print "$outname";
	if ($out_flag->{'_LocFlag'} == 1) {
		&print_location_mbgg($feat->{'location'}, $flag, 1);
	} elsif (! $out_flag->{'_LocFlag'} == 0) {
		&print_location($feat->{'location'}, $flag, 1);
	}
	print $ATTRDELIM;
	if (! defined($out_flag->{'_order'})) {
		foreach $attr (keys %{$feat->{'attr'}}) {
			if ($out_flag) {
				if ($out_flag->{$attr}) {
					print $out_flag->{$attr};
				} else {
					next;
				}
			} else {
				print "     $attr  ";
			}
			if ($feat->{'attr'}->{$attr}) {
				print &parse_data($feat->{'attr'}->{$attr}), "\n";
			}
		}
	} else {
		foreach $attr (@{$out_flag->{'_order'}}) {
			print $out_flag->{$attr};
			print &parse_data($feat->{'attr'}->{$attr}), $ATTRDELIM;
		}
	}
	print $FEATDELIM;
}

sub print_location {
	my($loc, $flag, $dir) = @_;
	if ($loc->{'complement'}) {
		print "complement ";
		$dir *= -1;
	}
	if ($loc->{'type'} ne 'region') {
		print "$loc->{'type'} ";
	}
	if (defined $loc->{'array'}) {
		foreach $elem (@{$loc->{'array'}}) {
			&print_location($elem,$flag,$dir);
		}
	} elsif ($loc->{'type'} eq 'region') {
		if ($dir > 0) {
			print $loc->{'from'}, ":", $loc->{'to'}, " ";
		} else {
			print $loc->{'to'}, ":", $loc->{'from'}, " ";
		}
	} elsif ($loc->{'type'} eq 'replace') {
		&print_location($loc->{'from'},$flag,$dir);
		print " => ";
		&print_location($loc->{'to'},$flag,$dir);
		print " ";
	} elsif ($loc->{'bases'}) {
		print "$loc->{'bases'}";
	}
}
sub print_location_mbgg {
	my($loc,$flag,$dir) = @_;
	if ($loc->{'complement'}) {
		$dir *= -1;
	}
	if (defined $loc->{'array'}) {
		foreach $elem (@{$loc->{'array'}}) {
			&print_location_mbgg($elem,$flag,$dir);
		}
	} elsif ($loc->{'type'} eq 'region') {
		if ($dir > 0) {
			print "FROM:$loc->{'from'}\n";
			print "TO:$loc->{'to'}\n";
		} else {
			print "FROM:$loc->{'to'}\n";
			print "TO:$loc->{'from'}\n";
		}
	}
}

sub get_region_from_location {
	my($loc, $dir, $length, $opt) = @_;
	my($from,$to) = &get_region_from_loc0($loc,$dir, $length);
	if ($opt->{format} eq 'rev') {
		return ($from, $to);
	} else {
		$from =~ s/[^\d]//g;
		$to =~ s/[^\d]//g;
		if ($from > $to){
			return ($to,$from,-1);
		} else {
			return ($from,$to,1);
		}
	}
}

$LARGEVAL = 9999999999999;
sub get_region_from_loc0 {
	my($loc, $dir, $length) = @_;
	my($from, $to) = ($LARGEVAL, -1);
	if ($loc->{'complement'}) {
		$dir *= -1;
	}
	if (defined $loc->{'array'}) {
		my($rev) = 0;
		foreach $elem (@{$loc->{'array'}}) {
			my($from0, $to0) = &get_region_from_loc0($elem, 1, $length);
			next if ($from0 < 0 || $to0 < 0);
			if ($from0 > $to0) {
				$tmp = $from0; $from0 = $to0; $to0 = $tmp;
				$rev = 1;
			}
			if ($length) {
				if ($from != $LARGEVAL &&
						$from - $from0 > $length / 2) {
					$from0 += $length;
				}
				if ($to >= 0 && $to - $to0 > $length / 2) {
					$to0 += $length;
				}
			}
			if ($from0 < $from) {
				$from = $from0;
			}
			if ($to0 > $to) {
				$to = $to0;
			}
		}
		if ($rev) {
			$tmp = $from; $from = $to; $to = $tmp;
		}
	} elsif ($loc->{'type'} eq 'region') {
		$from = $loc->{'from'};
		$to = $loc->{'to'};
	} else {
	}
	if ($dir < 0) {
		$tmp = $from; $from = $to; $to = $tmp;
	}
	($from, $to);
}

sub parse_position {
	my($posline) = @_;
	my($position, $flag);
	if ($posline =~ /^<([0-9]+)/) {
		$positioin = $1;
	} elsif ($posline =~ /^>([0-9]+)/) {
		$positioin = $1;
	} elsif ($posline =~ /^([0-9]+)$/) {
		$positioin = $1;
	} else {
		print "PARSE ERROR (position) $posline\n";
	}
	return($position, $flag);
}
sub parse_data {
	my($string) = @_;
	if ($string =~ /^\"(.*)\"/) {
		# type string
		return $1;
	} else {
		# type numeric etc.
		return $string;
	}
}
sub parse_ginum {
	my($string) = @_;
	$string =~ s/PID://;
	$string;
}

1;
