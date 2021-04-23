#!/usr/bin/perl -s

use Tools;
###############################################################################
package Tools::HomologyResult;
###############################################################################

sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
}
sub hit_list {
	my($this) = @_;
	@{ $this->{hit_list} };
}
sub add_hit {
	my($this, $hit) = @_;
	push(@{ $this->{hit_list} }, $hit);
	$hit->{result} = $this;
}
sub hsp_list {
	my($this) = @_;
	my(@list);
	foreach my $hit ( $this->hit_list ) {
		push( @list, $hit->hsp_list );
	}
	@list;
}
sub get_step {
	my($this) = @_;
	## default step size (query, db)
	(1,1);
}

###############################################################################
package Tools::HomologyHit;
###############################################################################
my $Delim = '#';

sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
}
sub hsp_list {
	my($this) = @_;
	@{ $this->{hsp_list} };
}
sub add_hsp {
	my($this, $hsp) = @_;
	push(@{ $this->{hsp_list} }, $hsp);
	$hsp->{result} = $this->{result};
}
###############################################################################
package Tools::HomologyHSP;

sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
}
sub generateAlignmentAll {
	my($this, $seq1, $seq2, $subseq1, %opt) = @_;
	my $ali = Alignment->new();
	$subseq1 = $seq1 if (! $subseq1);
	$ali->set_sequences($subseq1, $seq2);
	$ali->set_region($this->{from1}, $this->{to1},
		$this->{from2}, $this->{to2}, $this->{dir1}, $this->{dir2},
		$this->{ident}, $this->{score});
	if (defined $this->{bestflag}) {
		$ali->{bestflag} = $this->{bestflag}
	}

	$this->generateAlignment($ali,$subseq1,$seq2, %opt);

	if ($subseq1 != $seq1) {
		$ali->change_coord_base($seq1,$seq2);
	}
	if ($this->{dir1} < 0) {
		$ali->reverse;
	}
	$ali;
}
sub generateAlignment {
	my($this, $ali, $seq1, $seq2, %opt) = @_;
	if ($this->{align_segments}) {
		$this->generateAlignmentFromAlignSegments($ali, %opt);
	} elsif ($this->{aliseq1} && $this->{aliseq2}) {
		$this->generateAlignmentFromSeqeunce($ali, %opt);
	}
	$ali;
}
sub normalizeDirection {
	my($this, %opt) = @_;
	if ($this->{dir1} < 0) {
		if ($opt{reverse_first}) {
			## reverse the first seq. fasta original
		} else {
			## reverse the second seq.
			if ($this->{aliseq1}) {
				$this->{aliseq1} =
				    Sequence::revcompSeq($this->{aliseq1});
			}
			if ($this->{aliseq2}) {
				$this->{aliseq2} =
				    Sequence::revcompSeq($this->{aliseq2});
			}
			$this->{dir1} *= -1;
			$this->{dir2} *= -1;
		}
	}
}
sub generateAlignmentFromAlignSegments {
	my($this, $ali, %opt) = @_;
	
	my($from1,$from2) = ($ali->ali_from1, $ali->ali_from2);
	my $dir = $this->{dir1} * $this->{dir2};
	foreach my $l (@{$this->{align_segments}}) {
		my($from2, $to2) = ($l->{from2},$l->{to2});
		if ($dir<0 && $from2 < $to2) {
			$tmp = $from2; $from2 = $to2; $to2 = $tmp;
		}
		$ali->add_segment(from1=>$l->{from1},to1=>$l->{to1},
			from2=>$l->{from2},to2=>$l->{to2},
			dir1=>$this->{dir1},dir2=>$this->{dir2});
	}
	$ali;
}
sub generateAlignmentFromSeqeunce {
	my($this, $ali, %opt) = @_;
	my($from1, $from2) =( $this->{from1}, $this->{from2} );
	my$dir1 = Sequence::direction($this->{dir1});
	my$dir2 = Sequence::direction($this->{dir2});

	if ($dir1 < 0) {
		$from1 = $this->{to1};
	}
	if ($dir2 < 0) { 
		$from2 = $this->{to2};
	}
	my($step1,$step2) = (1,1);
	if ($this->{result}) {
		($step1,$step2) = $this->{result}->get_step;
	}
	if ($step1 != 1 || $step2 != 1) {
		## translation
		## do not calculate alignment
		$Alignment::UseOrigScore = $Alignment::UseOirgIdent = 1;
	}
	$ali->set_aliseq($this->{aliseq1},$this->{aliseq2},
		from1=>$from1, from2=>$from2,
		dir1=>$dir1, dir2=>$dir2,
		step1=>$step1,step2=>$step2,
	);
	$ali;
}
sub get_hsp_info {
	my($this, $fields) = @_;
	my(@info);
	foreach my $f (@{$fields}) {
		if ($this->{$f}) {
			push(@info, $this->{$f});
		} else {
			print STDERR "field $f not found\n";
			push(@info, '-');
		}
	}
	\@info;
}

###############################################################################
package Tools::HomologyParser;
###############################################################################
use FileHandle;

sub new {
	my($class, @filenames) = @_;
	my $this = {};
	bless $this, $class;
	$this->set_filenames(\@filenames) if (@filenames);
	$this;
}
sub set_filenames {
	my($this, $filenames) = @_;
	$this->{filenames} = $filenames;
	my($filename) =  shift @{$filenames};
	if (! $filename) {
		$filename = "-";	##STDIN
	}
	$this->{fh} = FileHandle->new($filename) ||
		die "Can't open file: $filename\n";
}
sub getline {
	my($this) = @_;
	if ($this->{saved_line}) {
		my $nextline = $this->{saved_line};
		$this->{saved_line} = '';
		return $nextline;
	}
	$this->{fh}->getline;
}
sub read {
	my($this) = @_;

	## main function that should be defined in subclass

	if ($this->{fh}->eof) {
		$this->{fh}->close;
		if ($filename = shift @{ $this->{filenames} }) {
			$this->{fh} = FileHandle->new($filename);
		} else {
			return ();
		}
	}
	my $res = $this->readfile;
	$res;
}
sub set_queryname {
	my($this, $result, $name) = @_;
	if(! $result->{query}) {
		$result->{query} = $name;
	} elsif ($result->{query} ne $name) {
		return -1;
	}
	return 0;
}
sub save_current_line {
	my($this, $curr_line) = @_;
	$this->{saved_line} = $curr_line;
}
###############################################################################
package main;
if ($0 eq __FILE__) {
	$blparse = Tools::HomologyParser->new($ARGV[0]);
	while ($blres = $blparse->read) {
		@list = $blres->get_hsp_info(['name1','name2',
			'from1','to1','from2','to2','dir2',
			'percentident','bitscore']);
		foreach $info (@list) {
			print join(' ', @{$info}),"\n";
		}
#		$blres->print_info_text;
	}
}

###############################################################################
1;#
###############################################################################
