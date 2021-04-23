#!/usr/bin/perl -s

require 'CGAT_Conf.pl';
use FileHandle;
use MBGD_access;

$| = 1;

@genome_names = @ARGV;
foreach my $g (@genome_names) {
	($sp, $chrno) = split(/\./, $g);
	$chrno = 1 if (! $chrno);
	$genome_chr{$sp}->{$chrno} = 1;
}
@species = keys %genome_chr;
if ($all) {
	@genomes = MBGD_access::getData(table=>'genome');
} else {
	@genomes = MBGD_access::getData(table=>'genome',
				species => join(',', @species));
}

&mkFilePath;

foreach $g (@genomes) {
	$sp = $g->{sp};
	$genome_path = &getGenomeFilePath($g->{sp},'','work');
	$ntgene_path = &getGeneFilePath($g->{sp}, 'ntseq','work');
	$aagene_path = &getGeneFilePath($g->{sp}, 'aaseq','work');
	$genetab_path = &getGeneFilePath($g->{sp}, 'region','work');
	if (! $force && -s $genome_path && -s $ntgene_path &&
			-s $aagene_path && -s $genetab_path) {
		next;
	}

	my(@chrout);
	my(@chr) = MBGD_access::getData(table=>'chromosome', species=>$sp);
	foreach $chr (@chr) {
		if ($genome_chr{$sp}->{ $chr->{seqno} }) {
			push(@chrout, $chr);
		}
		if ($chr->{type} eq 'chromosome') {
			$chrcnt++;
		}
	}


	my($i, $name);
	$genome_fh = FileHandle->new(">$genome_path")
			|| die"Can't open $genome_path\n";
	$ntgene_fh = FileHandle->new(">$ntgene_path")
			|| die "Can't open $ntgene_path\n";
	$aagene_fh = FileHandle->new(">$aagene_path")
			|| die "Can't open $aagene_path\n";
	$genetab_fh = FileHandle->new(">$genetab_path")
			|| die "Can't open $genetab_path\n";
	$genetab_fh->print("#" .
		join("\t","from","to","dir","color","name",'product')
			. "\n");
	foreach $chr (@chrout) {
		$seqname = $g->{sp};
		$seqname .= " " . $g->{orgname} . " " . $g->{strain};
		if ($chrcnt > 1) {
			$seqname .= " " . $chr->{name};
		}
		($dnaseq) =
			MBGD_access::getData(table=>'dnaseq', id=>$chr->{seq});

		&print_fasta($dnaseq, $seqname, $genome_fh);

		my(@genes) = MBGD_access::getData(
			table=>'gene', key=>$chr->{id}, keyfields=>'chrid',
			order=>'from1');

		foreach my $gdata (@genes) {
			$funccat = $gdata->{'funccat'} ?
				$gdata->{'funccat'} : 100;
			$genetab_fh->print(
				join("\t", 
					$gdata->{'from1'},
					$gdata->{'to1'},
					$gdata->{'dir'},
					$funccat,
					$gdata->{'name'},
					$gdata->{'descr'}) .  "\n" );
		}
		$genename = "$name $descr";
		@seqdata = MBGD_access::getData(
			table=>geneseq, key=>$chr->{id}, keyfields=>'chrid',
			order=>'from1');
		foreach $seqd (@seqdata) {
			&print_fasta($seqd, $seqd->{name}, $ntgene_fh);
		}
		@seqdata = MBGD_access::getData(
			table=>proteinseq, key=>$chr->{id}, keyfields=>'chrid',
			order=>'from1');
		foreach $seqd (@seqdata) {
			&print_fasta($seqd, $seqd->{name}, $aagene_fh);
		}

		$i++;
	}
	$genome_fh->close;
	$ntgene_fh->close;
	$aagene_fh->close;

#	system("$CMD_formatdb -p F -i $genome_path");
#	system("$CMD_formatdb -p F -i $ntgene_path");
#	system("$CMD_formatdb -p T -i $aagene_path");
}

sub print_fasta {
	my($seqobj, $name, $fh) = @_;
	my($LINELEN) = 60;
	my($sequence, $length);
	if (ref $seqobj) {
		$sequence = $seqobj->{seq};
		$length = $seqobj->{length} ? $seqobj->{length}
			: length($seqobj->{seq});
	} else {
		$sequence = $seqobj;
		$length = length($sequence);
	}
	$fh->print(">$name\n");
	for ($i = 0; $i < $length; $i+= $LINELEN) {
		$fh->print( substr($sequence, $i, $LINELEN) . "\n" );
	} 
}
