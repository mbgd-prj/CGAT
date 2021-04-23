#!/usr/bin/perl

#################################################################
package SeqFile;
#################################################################
use FileHandle;

sub new {
	my($class, $filename) = @_;
	my($this) = {};
	bless $this, $class;
	if ($filename) {
		$this->{fh} =
		FileHandle->new($filename) || die "Can't open file: $filename\n";
	}
	return $this;
}

sub getSequence {
	my($this, $filename) = @_;
	my($seq, $flag);
	my(@retseq);
	my($fh);
	if ($filename) {
		$fh = FileHandle->new($filename)
			|| die "Can't open file: $filename\n";
	} elsif (ref $this && $this->{fh}) {
		$fh = $this->{fh};
	} else {
		die "Sequnece filename is not specified\n";
	}
	while ($_ = $fh->getline) {
		chomp;
		if (/^>\s*(\S+)\s*(.*)/) {
			if ($seq) {
				push(@retseq,RawSequence->new($seq,$name));
			}
			$seq = '';
			$name = $1;
			$title = $2;
		} else {
			s/[^[:alpha:]]//g;
			$seq .= $_;
		}
	}
	if ($seq) {
		push(@retseq,RawSequence->new($seq,$name));
	}
	if (@retseq == 1) {
		return $retseq[0];
	} else {
		my $retseq;
		foreach $s (@retseq) {
			$retseq->{$s->name} = $s;
		}
		return $retseq;
	}
}
#################################################################
package Sequence;
#################################################################
sub checksum {
	my($this) = @_;
	if (! $this->{checksum}) {
		my $d = Digest::MD5->new;
		$d->add($this->seqString);
		$this->{checksum} = $d->b64digest;
	}
	return $this->{checksum};
}
sub length {
	my($this) = @_;
	if (! $this->{length}) {
		$this->{length} = length($this->seqString);
	}
	$this->{length};
}
sub name {
	my($this) = @_;
	if ($this->{name}) {
		return $this->{name};
	} elsif ($this->{seq}) {
		return $this->{seq}->name;
	}
}
sub get_subseq {
	my($this, $from, $to, $dir) = @_;
	return SubSequence->new($this, $from, $to, $dir);
}
sub print_seq {
	my($this, $opt) = @_;
	my($LEN) = 60;
	my($seqlen) = $this->length;
	print ">", $this->name, "\n" if (! $opt->{notitle});
	for (my $i = 1; $i <= $seqlen; $i+=$LEN) {
		my $maxi = ($i+$LEN-1 > $seqlen ? $seqlen : $i+$LEN-1);
		print $this->subseqString($i, $maxi),"\n";
	}
}
sub composition {
	my($this) = @_;
	my(%Count);
	@seq = split(//,$this->seqString);
	foreach $s (@seq) {
		$Count{$s}++;
	}
	return %Count;
}

sub direction {
	my($dirstr) = @_;
	if ($dirstr =~ /^(\+|f|\d|DIR)/ || $dirstr > 0) {
		return 1;
	} elsif ($dirstr =~ /^(-|r|INV)/ || $dirstr < 0) {
		return -1;
	} else {
		return 1;
	}
}
sub setCircular {
	my($this) = @_;
	$this->{circular} = 1;
}
sub isCircular {
	my($this) = @_;
	$this->{circular};
}
sub revcomp {
	my($this) = @_;
	if (! $this->{revseq}) {
		$this->{revseq} = RevCompSequence->new($this);
	}
	return $this->{revseq};
}
sub revcompSeq {
	my($seq) = @_;
	my($revseq);
	$revseq = reverse($seq);
	$revseq =~ tr/ATGCRYMKSWHBVDN/TACGYRKMSWDVBHN/;
	$revseq =~ tr/atgcrymkswhbvdn/tacgyrkmswdvbhn/;
	$revseq;
}
sub set_silence {
	my($this, $value) = @_;
	if ($value eq '0') {
		$Sequence::silence = 0;
	} else {
		$Sequence::silence = 1;
	}
}


#################################################################
package RawSequence;
#################################################################
@ISA = qw(Sequence);
use Digest::MD5;

sub new {
	my($class, $seq, $name, %opt) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{name} = $name;
	if ($opt{lower}) {
		$this->{seqString} = lc($seq);
	} else {
		$this->{seqString} = uc($seq);
	}
	return $this;
}
sub subseqString {
	my($this, $from, $to, $dir) = @_;
	my($len) = $this->length;
	if ($len == 0) {
		print STDERR "zero length sequence:$from,$to\n";
		return "";
	}
	my($seq);
	if ($this->isCircular) {
		$from = $from % $len;
		$to = $to % $len;
	} else {
		$from = 1 if ($from < 1);
		$to = 1 if ($to < 1);
		$from = $len if ($from > $len);
		$to = $len if ($to > $len);
	}
	if ($to < $from && $this->isCircular) {
		if ($from - $to < $len / 5 && ! $Sequence::silence) {
			print STDERR "Warning: to($to) < from($from)\n";
		}
		$seq = $this->substring_sub($from, $this->length);
		$seq .= $this->substring_sub(1, $to);
	} else {
		$seq = $this->substring_sub($from, $to);
	}
#	if ($from < 1) {
#		$excess1 = (- $from % $this->length) + 1;
#		$from = 1;
#	}
#	if ($to > $this->length) {
#		$excess2 = $to % $this->length;
#		$to = $this->length;
#	}
#	my $seq = &substring($this->seqString, $from, $to);
#	if ($excess1) {
#		$seq = &substring($this->seqString,
#			$this->length - $excess1 + 1, $this->length) . $seq;
#	}
#	if ($excess2) {
#		$seq .= &substring($this->seqString, 1, $excess2);
#	}
	if ($dir < 0) {
		$seq = &Sequence::revcompSeq($seq);
	}
	return $seq;
}
sub substring_sub {
	my($this, $from, $to) = @_;
	substr($this->{seqString}, $from - 1, $to - $from + 1);
}
sub seqString {
	my($this) = @_;
	$this->{seqString};
}
sub get_region {
	my($this, $origseq) = @_;
#	print STDERR "Warning: cannot extract a subregion on $origseq\n";
	return();
}
#################################################################
package SubSequence;
#################################################################
@ISA = qw(Sequence);

sub new {
	my($class, $seq, $from, $to, $dir) = @_;
	$dir= Sequence::direction($dir);
	if (! $seq->isCircular) {
		if ($from <= 0) {
			if (! $Sequence::silence) {
			  print STDERR "Warning: position overflows: $from\n";
			}
			$from = 1;
		}
		if ($seq->length < $to) {
			if (! $Sequence::silence) {
			  print STDERR "Warning: position overflows: $to;",
				 $seq->length, "\n";
			}
			$to = $seq->{length};
		}
	}
	my($this) = {
		seq=>$seq, from=>$from, to=>$to, dir=>$dir
	};
	return (bless $this, $class);
}
sub name {
	my($this) = @_;
	$this->{seq}->name;
}

sub length {
	my($this) = @_;
	$this->{to} - $this->{from} + 1;
}
sub seqString {
	my($this) = @_;
	$this->{seq}->subseqString($this->{from},$this->{to},$this->{dir});
}
sub subseqString {
	my($this, $from, $to, $dir) = @_;
	$dir= Sequence::direction($dir);
	$this->{seq}->subseqString( $this->{from} + $from - 1,
		$this->{from} + $to - 1, $this->{dir} * $dir);
}
sub reverse {
	my($this) = @_;
	$this->{dir} *= -1;
}
sub get_region {
	my($this, $origseq) = @_;
	if (! $origseq || $this->{seq} eq $origseq) {
		return($this->{from}, $this->{to});
	} else {
		my($from, $to) = $this->{seq}->get_region($origseq);
		return ($from + $this->{from} - 1, $from + $this->{to} - 1);
	}
}
##################################################################
package RevCompSequence;
##################################################################
@ISA = qw(Sequence);
sub new {
	my($class, $seq) = @_;
	my($this) = {};
	$this->{seq} = $seq;
	bless $this, $class;
}
sub revcomp {
	my($this) = @_;
	$this->{seq};
}
sub seqString {
	my($this) = @_;
	reverse( &Sequence::revcompSeq($this->{seq}->seqString) );
}
sub subseqString {
	my($this, $from, $to) = @_;
	$seqlen = $this->{seq}->length;
	$revfrom = $length - $to + 1;
	$revto = $length - $from + 1;
	reverse( $this->{seq}->subseqString($revfrom,$revto,-1) );
}
sub length {
	my($this) = @_;
	$this->{seq}->length;
}
sub get_region {
	my($this, $origseq) = @_;
	if ($origseq && $this->{seq} ne $origseq) {
		my($from, $to) = $this->{seq}->get_region($origseq);
	} else {
		return($this->length - $to + 1, $this->length - $from + 1);
	}
}

#################################################################
package VirtualSequence;
#################################################################
@ISA = qw(Sequence);
sub new {
	my($class, $name, $length) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{name} = $name;
	$this->{length} = $length;
	return $this;
}
sub length {
	my($this) = @_;
	return $this->{length};
}
sub seqString {
	my($this) = @_;
	print STDERR "Warning: no sequence data is available\n";
}
#################################################################
package main;
#################################################################
if ($0 eq __FILE__) {
	$seq = RawSequence->new("RACGTACGTACGTACGTACGTACGTACGTACGTTT", "test");
	$seq->name,":name\n";
	$seq->print_seq;
	print $seq->length,"\n";
	$seq->get_subseq(20,60)->print_seq;

	$seq->setCircular;
	$seq->get_subseq(20,60)->print_seq;

	$subseq = $seq->get_subseq(1,10);
	$subseq->print_seq;

	$seq->print_seq;
	$rev = $seq->revcomp;
	$rev->print_seq;
	$revsub = $rev->get_subseq(1,10);
	$revsub->print_seq;

	$subsubseq = $subseq->get_subseq(2,5,-1);
	$subsubseq->print_seq;
	($f,$t) = $subsubseq->get_region($seq);
	print "$f,$t\n";
	($f,$t) = $revsub->get_region($seq);
	print "$f,$t\n";
}
#################################################################
1;
