#!/usr/bin/perl -s

use GenBank;
require 'CGAT_Conf.pl';
use FileHandle;

$gbfile = $ARGV[0];
$sp = $ARGV[1];
if (!$gbfile || ! $sp) {
	die "Usage: $0 [-tag_field=#] [-gene_field=#] gbk_file name\n";
}

$gbk = GenBank->new($gbfile);

$| = 1;

&mkFilePath;

$genome_path = &getGenomeFilePath($sp,'','work');
$ntgene_path = &getGeneFilePath($sp, 'ntseq','work');
$aagene_path = &getGeneFilePath($sp, 'aaseq','work');
$genetab_path = &getGeneFilePath($sp, 'region','work');

$genome_fh = FileHandle->new(">$genome_path")
		|| die"Can't open $genome_path\n";
$ntgene_fh = FileHandle->new(">$ntgene_path")
		|| die "Can't open $ntgene_path\n";
$aagene_fh = FileHandle->new(">$aagene_path")
		|| die "Can't open $aagene_path\n";
$genetab_fh = FileHandle->new(">$genetab_path")
		|| die "Can't open $genetab_path\n";

foreach $entry ($gbk->read_entry($db, \@spec)) {
	foreach $f (@{$entry->{features}}) {
		if ($f->{keyname} eq 'source') {
			$organism = $f->{attr}->{organism};
			$strain = $f->{attr}->{strain};
			$sub_strain = $f->{attr}->{sub_strain};
			$chromosome = $f->{attr}->{chromosome};
		} elsif ($f->{keyname} =~ /(CDS|[tr]RNA)/) {
			my $gene = {};
			my $attr = $f->{attr};
			my($from,$to,$dir) = $gbk->get_region($f->{location});
			my $gene_tag, $gene_name, $product;
			if ($tag_field) {
				$gene_tag = $attr->{$tag_field};
				$gene_name = $attr->{$gene_field};
			} elsif ($attr->{locus_tag}) {
				$gene_tag = $attr->{locus_tag};
				$gene_name = $attr->{gene};
			} else {
				$gene_tag = $attr->{gene};
			}
			$gene->{name} = $gene_tag;
			$gene->{gene} = $gene_name;
			$gene->{from} = $from;
			$gene->{to} = $to;
			$gene->{dir} = $dir;
			$gene->{product} = $attr->{product};
			$gene->{aaseq} = $attr->{translation};
			$gene->{ntseq} = $gbk->get_subseq($f->{location});
			push(@Genes, $gene);
		} elsif ($f->{keyname} eq 'tRNA') {
		} elsif ($f->{keyname} eq 'rRNA') {
		}
	}

	$seqname = "$sp $organism";
	$seqname .= " $chromosome" if ($chromosome);
	&print_fasta($seqname, $entry->{seqstr}, $genome_fh);

	$genetab_fh->print("#" .
		join("\t","from","to","dir","color","name","product")
						. "\n");
	foreach $gene (@Genes) {
		my $genename = "$gene->{name}";
		my $title = "$genename $gene->{gene} $gene->{product}";
		if ($gene->{aaseq}) {
			&print_fasta($title, $gene->{aaseq}, $aagene_fh);
		}
		if ($gene->{ntseq}) {
			&print_fasta($title, $gene->{ntseq}, $ntgene_fh);
		}

		$func = $Func->{"$sp:$gene->{name}"},
		$func = 100 if (! $func);

		$genetab_fh->print(
			join("\t", $gene->{from}, $gene->{to},
				$gene->{dir}, $func, $gene->{name},
				$gene->{product}) .
			"\n");
	}
	$genome_fh->close;
	$ntgene_fh->close;
	$aagene_fh->close;

	system("$CMD_formatdb -p F -i $genome_path");
	system("$CMD_formatdb -p F -i $ntgene_path");
	system("$CMD_formatdb -p T -i $aagene_path");
}
sub print_fasta {
        my($name, $seq, $fh) = @_;
        my($LINELEN) = 60;
	my($length) = length($seq);
        $fh->print(">$name\n");
        for ($i = 0; $i < $length; $i+= $LINELEN) {
                $fh->print( substr($seq, $i, $LINELEN) . "\n" );
        } 
}
