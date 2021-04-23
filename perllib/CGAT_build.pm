#!/usr/bin/perl -s

##############################################################################
#use strict;
use lib "$ENV{'CGAT_HOME'}/perllib";
use lib "$ENV{'CGAT_HOME'}/build/common";
use File::Basename;
use File::Path;
use CmdProc;
require "CGAT_Conf.pl";
require "libLogfile.pl";
require "libGetOpt.pl";

##############################################################################
package CGAT_build;
##############################################################################
sub new {
	my($class, @args) = @_;
	my($this) = {};
	bless $this, $class;
	$this->initialize(@args);
	$this;
}
sub initialize {
	my($this);
	## should be overriden
}
sub parse_options {
	my($this, $optstr) = @_;
	foreach $op (split(/,/, $optstr)) {
		my($key,$value) = split(/=/, $op);
		if ($op !~ /=/) {
			$value = 1;
		}
		$options->{$key} = $value;
	}
}
sub execute {
	my($this, @args) = @_;
	foreach $sp (@{$this->{species}}) {
		$this->{sp} = $sp;
		$this->{outfile} =  $this->outfile($sp);
		$this->pre_exec;
		my $genomeseq = &CGAT_Data::getGenomeSeqPath($sp);
		$this->{cmdproc}->execute( [ {
			procname=>'execute_main',
			type=>'method',
			args=>[$this,@args],
			depend=>[$genomeseq],
			outfile=>$this->{outfile},
		} ] );
		$this->post_exec;
	}
}
sub pre_exec {
	my($this) = @_;
	&LogFile::start_log('', $this->jobname);
	$this->{cmdproc} = CmdProc->new($this->{filebase},
			[], $this->{cmdproc_opt});
#	my($name, $path) = File::Basename::fileparse($this->{outfile});
#	if (! -d $path) {
#		File::Path::mkpath($path, 0, 0755);
#	}
}
sub post_exec {
	&LogFile::end_log();
}
sub execute_main {
	my($this, @args) = @_;
	### should be overridden
}
sub species_list {
	my($sp) = @_;
	my($splist);
	if ($sp eq 'ALL' || $sp->[0] eq 'ALL') {
		my @spl = &main::getAllGenomes();
		$splist = \@spl;
	} elsif (ref $sp eq 'ARRAY') {
		 $splist = $sp;
	} else {
		$splist = [$sp];
	}
	$splist;
}
sub jobname {
	my($this) = @_;
	if ( $this->{outfile} ) {
		File::Basename::basename($this->{outfile});
	}
}

##############################################################################
package CGAT_buildAnnot;
use base CGAT_build;
sub initialize {
	my($this, $sp, $optstr) = @_;

	$this->{species} = &CGAT_build::species_list($sp);
	die "Usage: $0 spname\n" if (! $this->{species});
	&CGAT_Data::set_mode('update');

	if ($optstr->{type}) {
		$this->{type} = $optstr->{type};
	} else {
		$this->{type} = ref($this);
		$this->{type} =~ s/^build_//;
	}
	$this->{filebase} = "$main::DIR_work/$this->{type}"
}
sub outfile {
	my($this, $sp) = @_;
	my $outfile = &main::getSegmentFilePath($sp, $this->{type}, '', 'work');
#	my($name, $path) = File::Basename::fileparse($outfile);
#	File::Path::mkpath($path, 0, 0755);
	$outfile;
}
sub jobname {
	my($this) = @_;
	"$this->{type}.$this->{sp}"
}
##############################################################################
package CGAT_buildGeneAttr;
use base CGAT_build;
sub initialize {
	my($this, $sp, $optstr) = @_;

	$this->{species} = &CGAT_build::species_list($sp);
	die "Usage: $0 spname\n" if (! $this->{species});
	&CGAT_Data::set_mode('update');

	if ($optstr->{type}) {
		$this->{type} = $optstr->{type};
	} else {
		$this->{type} = ref($this);
		$this->{type} =~ s/^build_//;
	}
	$this->{filebase} = "$main::DIR_work/$this->{type}"
}
sub outfile {
	my($this, $sp) = @_;
	my $outfile = &main::getGeneAttrFilePath($sp, $this->{type}, 'work');
#	my($name, $path) = File::Basename::fileparse($outfile);
#	File::Path::mkpath($path, 0, 0755);
	$outfile;
}
sub jobname {
	my($this) = @_;
	"$this->{type}.$this->{sp}"
}
##############################################################################
package CGAT_buildAlign;
use base CGAT_build;
sub initialize {
	my($this, $class, $ARGV, $HomOptions) = @_;
	my($sp1, $sp2) = ($ARGV->[0], $ARGV->[1]);
	&getOptHomology(\@ARGV, $HomOptions);
	&CGAT_Data::set_mode('update');
	$this->{homopt} = $HomOptions;

	$this->{homsearch} = "GenomeHomSearch::$class"->new(%{$HomOptions});

	$this->{sp1} = $sp1;
	$this->{sp2} = $sp2;
	$this->{genomeseq1} = &CGAT_Data::getGenomeSeqPath($sp1, 'update');
	$this->{genomeseq2} = &CGAT_Data::getGenomeSeqPath($sp2, 'update');
	$this->{outfile} = $this->{homsearch}->{target};
}
sub test {
	my($this) = @_;
	$this->{homsearch}->test;
}
sub execute {
	my($this, @args) = @_;
	$this->pre_exec;
	$this->{homsearch}->execute(@args);
	$this->post_exec;
}
sub getOptHomology {
        my($ARGV, $options, $default_opt) = @_;
        if (@$ARGV < 2) {
                die "Usage: $0 sp1 sp2 [optvar=value ..]\n";
        }
	$default_opt = \%main::Default_Homology_Opt if (! $default_opt);

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

##############################################################################
if ($0 eq __FILE__) {
	$cgat_build = CGAT_buildAlign->new(@ARGV);
	$cgat_build->execute;
	exit;
}

##############################################################################
1;#
##############################################################################    
