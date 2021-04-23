#!/usr/bin/perl -s
#########################################################
package Tools::ReadCGATHomologyData;
#########################################################
sub read {
	my($sp1, $sp2, $program, %opt) = @_;
	$opt{'classname'} = 'CGAT';
	$opt{'sp1'} = $sp1;
	$opt{'sp2'} = $sp2;
	$opt{'infile'} = &main::getAlignFilePath(
			$sp1,$sp2,'alignInfo',$program);
	if (! $opt{FinalOverlapCheck}) {
		$opt{SkipFinalOverlapCheck} = 1;
	}
	Tools::ReadHomologyData::read(%opt);
}
#########################################################
package Tools::ReadHomologyData;
#########################################################
# an interface to invoke homology parser
use Tools;
use Alignment;
use Alignment::Mask;
use GenomeFeature;
require 'CGAT_Conf.pl';

sub read {
	my(%opt) = @_;
	if (! $opt{'classname'}) {
		$opt{'classname'} = "Blast"; 	## default
	}

	my $tool = Tools->getInstance($opt{'classname'});
	my $parser = $tool->getParserInstance;

	if ($opt{'sp1'}) {
		$seqfile1 = &main::getGenomeFilePath($opt{'sp1'}, '', 'current');
	}
	if (! -f $seqfile1 && $opt{'seqfile1'}) {
		$seqfile1 = $opt{'seqfile1'};
	}
	if ($opt{'sp2'}) {
		$seqfile2 = &main::getGenomeFilePath($opt{'sp2'}, '', 'current');
	}
	if (! -f $seqfile2 && $opt{'seqfile2'}) {
		$seqfile2 = $opt{'seqfile2'};
	}
	my $self_match = 1 if ($seqfile1 eq $seqfile2);
	if ($opt{'sp1'} && $opt{'maskRegion'}) {
		$maskReg1 = &getMaskRegion($opt{'sp1'}, $opt{'maskRegion'});
	}
	if ($opt{'sp2'} && $opt{'maskRegion'}) {
		$maskReg2 = &getMaskRegion($opt{'sp2'}, $opt{'maskRegion'});
	}

	my @infiles;
	if (ref $opt{'infile'} eq 'ARRAY') {
		push(@infiles, @{ $opt{'infile'} });
	} else {
		push(@infiles, $opt{'infile'});
	}

	my $seq1 = SeqFile->getSequence($seqfile1);
	my $seq2 = SeqFile->getSequence($seqfile2);
	my $MultiSeq2;
	if (! UNIVERSAL::isa($seq2,'Sequence') && ref $seq2 eq 'HASH') {
		## multiple sequence file
		$MultiSeq2 = $seq2;
	}
	$parser->set_filenames(\@infiles);
	my $AllAlignList = SegmentPairList->new;
	my $PrevAlignList;

	while (my $res = $parser->read) {
		$name1 = $res->{query};
		&Debug::message("Query: $name1");
		if ( $name1 =~ /Qry:(\d+):(\d+)/ ) {
			($seqfrom, $seqto) = ( $1, $2 );
			$subseq1 = $seq1->get_subseq($seqfrom,$seqto);
			$reg = Segment->new($seqfrom,$seqto);
		} else {
			$subseq1 = $seq1;
		}
		$AlignList = SegmentPairList->new;
#print STDERR "Start\n";
my $cnt;
		foreach $hsp ($res->hsp_list) {
			if ($MultiSeq2) {
				## multiple sequence file
				$seq2 = $MultiSeq2->{ $hsp->{name2} };
			}
			my $ali = $hsp->generateAlignmentAll($seq1, $seq2, $subseq1);

			if ($self_match &&
				$ali->from1 == $ali->from2 &&
				$ali->to1 == $ali->to2 && $ali->dir >= 0) {
				## skip an identical match
				next;
			}
			$AlignList->add($ali);
$cnt++
		}
#print STDERR "OK\n";
		if ($maskReg1 || $maskReg2) {
			## mask specified regions (e.g. highly repetitive)
			Alignment::Mask->maskAlign($AlignList,$maskReg1,$maskReg2);
		}
		if ($PrevAlignList) {
			my $ov_reg;
			if ($prev_reg) {
				$ov_reg = $prev_reg->intersect($reg);
			}
			$ret = $AlignList->mergeList($PrevAlignList, $ov_reg);
			$AllAlignList->addList($PrevAlignList);
		}
		$PrevAlignList = $AlignList;
		$prev_reg = $reg;
	}
	$AllAlignList->addList($PrevAlignList);
	if (! $opt{SkipFinalOverlapCheck} ) {
		while ($ret = $AllAlignList->mergeList($AllAlignList)) {
			print STDERR "merge..$ret\n";
		}
	}

	if($opt{assign_best}) {
		## assign best hit flags to each alignment
		$AllAlignList->findBestHit('UseSumScore' => 1);
		$AllAlignList->sort_by_from;
	}
	$AllAlignList;
}
sub getMaskRegion {
	my($sp, $opt_maskRegion) = @_;
	my($maskFeat, $maskReg);
	my($type, $op,$value, $work);
	$type = $opt_maskRegion;
	if ($opt_maskRegion =~ /^(\w+)\s*([<>=]+)\s*(\w+)/) {
		($type,$op,$value) = ($1, $2, $3);
	}
	$work = 1;
	my $maskFeat = GenomeFeature->new( $sp, $type,
				{mode=>'read', work=>$work});
	$maskReg = $maskFeat->read_table;
	if ($op){
		$maskReg = $maskFeat->filter( sub {
			eval "$_[0]->{value} $op $value";
		} );
	}
	$maskReg;
}

#########################################################
package main;
if (__FILE__ eq $0) {
	$AllAlignList = Tools::ReadHomologyData::read(
			classname=>$classname, sp1=>$sp1, sp2=>$sp2,
			infile=>@ARGV,
		);
	if ($LARGEGAP) {
		if ($alignInfoOut && $SAVE_BEFORE_SPLIT) {
			$l->print_align_info($FH_aliInfo);
		}

		$NewAllAlignList = SegmentPairList->new;
		foreach my $ali ($AllAlignList->list) {
			foreach my $a ($ali->split_alignment($LARGEGAP,$MINSCORE)) {
				$NewAllAlignList->add($a);
			}
		}
		$AllAlignList = $NewAllAlignList;
	}
	foreach $l ($AllAlignList->list) {
		$l->print_region($FH_aliReg, ['bestflag','delete','score']);
	}
}
#########################################################
1;
