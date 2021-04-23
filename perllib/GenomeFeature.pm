#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use ColorTab;
use Sequence;
require "CGAT_Conf.pl";
use Segment;
use Sequence;
use OptionList;

###############################################################################
package SeqFeature;
@ISA = qw(Segment);
sub new {
	my($class, $seq, $from, $to, $dir, $value, %add_data) = @_;
	$this = {};
	bless $this, $class;
	$this->{seq} = $seq;
	$this->{seg} = DirectedSegment->new($from,$to,$dir);
	$this->{value} = $value;
	foreach $key (keys %add_data) {
		$this->{data}->{$key} = $add_data{$key};
	}
	$this;
}
sub from {
	$_[0]->{seg}->from;
}
sub to {
	$_[0]->{seg}->to;
}
sub dir {
	$_[0]->{seg}->dir;
}
sub shift {
	$_[0]->{seg}->shift;
}
sub length {
	$_[0]->{seg}->length;
}
sub color {
	my($this) = @_;
	$this->{value};
}
sub seqname {
	my($this) = @_;
	if (UNIVERSAL::isa($this->{seq},"Sequence")) {
		$this->{seq}->name;
	} else {
		$this->{seq};
	}
}
sub table_output {
	my($this, $fields) = @_;
	my(@output) = ($this->seqname, $this->from, $this->to,
				$this->dir, $this->color);
	foreach $fld (@{$fields}) {
		push(@output, $this->{data}->{$fld});
	}
	(\@output);
}
sub print {
	my($this, %opt) = @_;
	my(@output) = $this->table_output($opt{fields});
	my($fh) = $opt{'fh'};
	$sep = $opt{sep} ? $opt{sep} : "\t";
	foreach $out (@output) {
		if ($opt{'fh'}) {
			$opt{'fh'}->print( join($sep, @{$out}) . "\n" );
		} else {
			print join($sep, @{$out}),"\n";
		}
	}
}
###############################################################################
package SeqFeatureSegmentPair;
@ISA = qw(SeqFeature);
sub new {
	my($class, $seq, $from1, $to1, $from2, $to2, $dir, $value, $name,
			%add_data) = @_;
	$this = {};
	bless $this, $class;
	$this->{seq} = $seq;
	$this->{seg1} = DirectedSegment->new($from1,$to1, $dir);
	$this->{seg2} = DirectedSegment->new($from2,$to2, $dir);
	$this->{value} = $value;
	$this->{segset_name} = $name;
	foreach $key (keys %add_data) {
		$this->{data}->{$key} = $add_data{$key};
	}
	$this;
}
sub from1 {
	$_[0]->{seg1}->from;
}
sub to1 {
	$_[0]->{seg1}->to;
}
sub from2 {
	$_[0]->{seg2}->from;
}
sub to2 {
	$_[0]->{seg2}->to;
}
sub dir {
	$_[0]->{seg1}->dir;
}
sub table_output {
	my($this, $fields) = @_;
	my(@output);
	my(@out1,@out2);
	@out1 = ($this->seqname, $this->from1, $this->to1, $this->dir, $this->color, $this->{segset_name});
	foreach $fld (@{$fields}) {
		push(@out1, $this->{data}->{$fld});
	}
	@out2 = ($this->seqname, $this->from2, $this->to2, $this->dir, $this->color, $this->{segset_name});
	foreach $fld (@{$fields}) {
		push(@out2, $this->{data}->{$fld});
	}
	(\@out1, \@out2);
}
###############################################################################
package SeqFeatureSegmentSet;
@ISA = qw(SeqFeature);
sub new {
	my($class, $seq, $from, $to, $dir, $value, $seglist) = @_;
	$this = {};
	bless $this, $class;
	$this->{seq} = $seq;
	$this->{seg} = DirectedSegment->new($from,$to, $dir);
	$this->{segments}  = SegmentList->new($seglist);
	$this->{value} = $value;
	$this;
}

###############################################################################
package SeqFeatureList;
@ISA = qw(SegmentList);
sub new {
	my($class, $list) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{list} = $list;
	$this;
}
sub list {
	@{ $_[0]->{list} };
}
sub add {
	my($this, $feat) = @_;
	push(@{$this->{list}},$feat);
}
sub print {
	my($this, %opt) = @_;
	foreach $feat ($this->list) {
		$feat->print(%opt);
	}
}
###############################################################################
package GenomeFeatureCommand;
sub new {
	my($class) = @_;
	bless {}, $class;
}
sub get_class {
	my($this, $classname) = @_;
	$classname = "GenomeFeatureCommand::$classname";
	eval "use $classname;";
	$classname;
}
sub get_optstring {
	my($this, $addopt) = @_;
	OptionList->new($this->options)->set_options($addopt)->get_optstring;
}
## should be overridden
sub execute {
	my($this) = @_;
}
###############################################################################
package GenomeFeature;
###############################################################################
$| = 1;
$FIELD_SEP = "\t";
sub new {
	my($class, $sp, $type, $opt) = @_;
	my($this) = {};
	if (! $sp) {
		die "Usage: $0 spname\n";
	}
	bless $this, $class;

	$this->{sp} = $sp;

	$this->{sep} = $FIELD_SEP;
	if ($type) {
		if ($type eq 'Gene') {
			if (! $opt->{work}) {
				$file = &main::getGeneFilePath($sp, 'tab','current');
			} else {
				$file = &main::getGeneFilePath($sp, 'tab','update');
			}
		} elsif ($opt->{mode} eq 'read' && ! $opt->{work}) {
			$file = &main::getSegmentFilePath($sp, $type, '','current');
		} else {
			$file = &main::getSegmentFilePath($sp, $type, '','work');
		}
	}
	if ($opt->{mode} eq 'read') {
		if ($file) {
			$this->{fh} = FileHandle->new($file) ||
				die "Cannot open $file\n";
		} else {
			$this->{fh} = FileHandle->new("<&STDIN");
		}
	} else {
		if ($file) {
			$this->{fh} = FileHandle->new(">$file");
		} else {
			$this->{fh} = FileHandle->new(">&STDOUT");
		}
	}
	if ($opt->{colortab}) {
		$this->{colortab} = $opt->{colortab};
	} elsif (-f (my $colortab =
			"$main::DIR_colortab/colorTab.$opt->{colorclass}")) {
		$this->{colortab} = ColorTab->new($colortab);
	} elsif (-f (my $colortab = "$main::DIR_colortab/colorTab.${type}")) {
		$this->{colortab} = ColorTab->new($colortab)
	} else {
		my $colortab = "$main::DIR_colortab/colorTab.Rotate";
		$this->{colortab} = ColorTabRotate->new($colortab);
	}
	$this->{featureList} = SeqFeatureList->new;
	$this->initialize($opt);
	return $this;
}
sub initialize {
	my($this, $opt) = @_;
}

sub addSegment {
	my($this, $from, $to, $dir, $color, %add_data) = @_;
	$this->{featureList}->add(
		SeqFeature->new($this->{sp},$from,$to,$dir,$color, %add_data) );

	return $this;
}
sub add_fields {
	my($this, @fieldnames) = @_;
	push(@{$this->{datafields}}, @fieldnames);
}
sub add_field_option {
	my($this, $field_opt) = @_;
	push(@{$this->{field_option}}, $field_opt);
}
#sub add_checksum {
#	my($this) = @_;
#	my($seq) = SeqFile->getSequence($this->{genomeseq});
#	$this->{checksum} = $seq->checksum;
#}
sub write_header {
	my($this, @fieldnames) = @_;
	$this->{fh}->print("#");
	$this->{fh}->print(join($this->{sep},
			'sp', 'from', 'to', 'dir', 'color',
			@{$this->{datafields}}, @fieldnames). "\n");
	if ($this->{checksum}) {
		$this->{fh}->print("#checksum: ", $this->{checksum} . "\n");
	}
	if (ref $this->{field_option} eq 'ARRAY') {
		foreach my $opt (@{ $this->{field_option} }) {
			$this->{fh}->print("#$opt\n");
		}
	}
}
sub write_table {
	my($this) = @_;
	$this->write_header;
	$this->{featureList}->print(
		fh => $this->{fh}, fields => $this->{datafields});
}
sub read_table {
	my($this) = @_;
	my($line);
	return 0 if ($this->{fh}->eof);
	$line = $this->{fh}->getline;
	while ($line =~ /^#/) {
		$line = $this->{fh}->getline;
	}
	do {
		chomp $line;
		($name,@field) = split(/$this->{sep}/, $line);
		$this->addSegment(@field);
	} while ($line = $this->{fh}->getline);
	$this->{featureList};
}
sub get_seqString {
	my($this) = @_;
	if (! $this->{seq}) {
		$this->{seq} = SeqFile->getSequence($this->{genomeseq});
	}
	$this->{seq}->subseqString($this->{from}, $this->{to}, $this->{dir});
}
sub create_table {
	my($this) = @_;
	## should be overridden
}

sub create_optstring {
	my($this, $options, $optswitch) = @_;
	my(@optstr);
	foreach $o (keys %{$options}) {
		if ($options->{$o} eq 'on') {
			push(@optstr, join('', $optswitch->{$o}));
		} elsif ($options->{$o}) {
			push(@optstr, join('', $optswitch->{$o}, $options->{$o}));
		}
	}
	join(' ', @optstr);
}
sub filter {
	my($this, $code) = @_;
	my($newFeatList) = SeqFeatureList->new;
	foreach $feat ($this->{featureList}->list) {
		if (&{$code} ($feat)) {
			$newFeatList->add($feat);
		}
	}
	$newFeatList;
}

###############################################################################
package GenomeFeatureSegpair;
use base GenomeFeature;
sub initialize {
	my($this, $opt) = @_;
	$this->{segset_field} = $opt->{segset_field} ?
			$opt->{segset_field} : "name";
	$this->add_fields($this->{segset_field});
}
sub set {
	my($this, $from1, $to1, $from2, $to2, $dir, $color, @data) = @_;
	$this->{from1} = $from1; $this->{to1} = $to1;
	$this->{from2} = $from2; $this->{to2} = $to2;
	$this->{dir} = $dir;
	$this->{color} = $color;
	$this->{data} = \@data;
	return $this;
}
sub addSegment {
	my($this, $from1, $to1, $from2, $to2, $dir, $color, $name, %add_data) = @_;
	$this->{featureList}->add( SeqFeatureSegmentPair->new(
		$this->{sp},$from1,$to1,$from2,$to2,$dir,$color,$name,
			%add_data) );

	return $this;
}
sub read_table {
	my($this) = @_;
	split($this->{sep}, $this->{fh}->readline);
}
sub write_header {
	my($this, @fieldnames) = @_;
	$this->SUPER::write_header(@fieldnames);
	if ($this->{segset_field}) {
		$this->{fh}->print("#set_name\t$this->{segset_field}\n");
	}
}

###############################################################################
if ($0 eq __FILE__) {
	$feat = GenomeFeature->new("eco","Simple_search", {mode=>'read'});
	$feat->read_table;
	$feat->write_table;
	exit;
}

###############################################################################
1;#
###############################################################################
