#!/usr/bin/perl -s

###############################################################################
use strict;
use File::Basename;
use File::Path;
use FileHandle;
use CmdProc;
use Tools;
use Tools::ReadHomologyData;
#use GDBM_File;
use Fcntl;
use SDBM_File;
use POSIX ":sys_wait_h";

require "CGAT_Conf.pl";
require "libCheckPoint.pl";

###############################################################################
package GenomeHomSearch;
use base qw(CmdProc);
###############################################################################
###############################################################################
# Methods common to Command class
sub new {
	my($class, %opt) = @_;
	my($this) = {};
	bless $this, $class;
	$this->initialize(%opt);
	$this;
}
sub initialize {
	my($this, %opt) = @_;
	$this->{Default_QueryNum} = 100;

#	my @classname = split(/::/, ref($this));
#	my $class_prog = pop(@classname);
	my $classname = $this->classname;
#	$this->{Default_program} = Tools->defaultProgname($classname);

	$this->SUPER::initialize($opt{cmdopt});

	if ($opt{program}) {
		$this->{program} = $opt{program};
	} else {
		$this->{program} = $this->defaultProgname;
	}
	if ($opt{query}) {
		$this->{query} = $opt{query};
        	$this->{sp1} = File::Basename::basename($opt{query});
	} elsif ($opt{sp1}) {
		$this->{query} = &CGAT_Data::getGenomeSeqPath($opt{sp1});
		$this->{sp1} = $opt{sp1};
	} else {
		die "query is not specified\n";
	}
	if ($opt{database}) {
		$this->{database} = $opt{database};
        	$this->{sp2} = File::Basename::basename($opt{database});
	} elsif ($opt{sp2}) {
		$this->{database} = &CGAT_Data::getGenomeSeqPath($opt{sp2});
		$this->{sp2} = $opt{sp2};
	} else {
		die "database is not specified\n";
	}

	$this->{DIR_query} = $main::DIR_query;

    	if (! $this->{opt}->{SkipPostProc}) {
		$this->{align_out} = &main::getAlignFilePath( $this->{sp1},
			$this->{sp2}, 'align', $this->{program}, 'work');
		$this->{alignseq_out} = &main::getAlignFilePath( $this->{sp1},
			$this->{sp2}, 'alignSeq', $this->{program}, 'work');
		$this->{aligninfo_out} = &main::getAlignFilePath( $this->{sp1},
			$this->{sp2}, 'alignInfo', $this->{program}, 'work');
		$this->{depend} = [$this->{query}, $this->{database}];
		$this->{target} = $this->{aligninfo_out};
	}

	if ($opt{procname}) {
		$this->{procname} = $opt{procname};
	} else {
		$this->{procname} = $this->{program};
	}

	foreach my $o (keys %opt) {
		if ($o =~ /program|database|query|sp1|sp2|procname|cmdopt/) {
		} elsif ($o =~ /title|filebase|outsuff|progopt/) {
			$this->{$o} = $opt{$o};
		} else {
			$this->{opt}->{$o} = $opt{$o};
		}
	}

	## opt->{title} should be set !!
	if (! $this->{title}) {
		$this->{title} = $this->{program};
	}
	if (! $this->{filebase}) {
		$this->{filebase} = "$main::DIR_work/$this->{title}."
			. $this->{sp1} . "-" . $this->{sp2};
	}
	$this->{srchoutfile} = "$this->{filebase}.srchout";
	$this->{srchresfile} = "$this->{filebase}.srchres";
	$this->{srchoutfile} = "##.srchout";
	$this->{srchresfile} = "##.srchres";
	$this->{chdir} = "$main::DIR_work";
	$this->{cmdproc} = CmdProc->new($this->{filebase});
	$this->subst_vars;
	$this;
}
sub classname {
	my($this) = @_;
	my @classname = split(/::/, ref($this));
	pop(@classname);
}
sub parser_classname {
	my($this) = @_;
	$this->classname;
}
sub execProgname {
	my($this) = @_;
	## name of program actually executed
	## default rule
	$this->{program} || $this->defaultProgname;
}
sub test {
	my($this) = @_;
	my $exprog = $this->execProgname;
	my $ret = system("type $exprog > /dev/null 2>&1");
	$ret >> 8;
	return ($ret != 0);
}
sub execute_main {
    my($this) = @_;
    my($db);
    my($cmd);
    my($stdout);

    if (!-e "$this->{database}") {
        die("file not found($this->{database})");
    }
    if (! -e "$this->{query}") {
        die("file not found($this->{query})");
    }

    $this->create_dbindex;

    my $qfname = File::Basename::basename($this->{query});

    ## execute search
    if ($this->{opt}->{QueryLen}) {
	## divide the query sequence
    	File::Path::mkpath($this->{DIR_query}, 0, 0750);
    	my $fileDivQuery = &main::absolutePath("$this->{DIR_query}/$qfname");
	if (! $this->{opt}->{QueryNum}) {
		$this->{opt}->{QueryNum} = $this->{Default_QueryNum};
	}

	&subDivQuery($this->{query}, $fileDivQuery, $this->{opt});

    	$this->{cmdproc}->execute([
	    {
		type => 'code',
		procname => \&execSearchAll,
		depend => [$this->{query}],
		outfile => $this->{srchoutfile},
		args => [$this, $fileDivQuery]
	    }
    	]);
#	&unlinkQueryFiles($fileDivQuery);
    } else {
	## do not divide the query sequence
    	$this->{cmdproc}->execute([
	    {
		type => 'code',
		procname => \&execSearch,
		infile => $this->{query},
		outfile => $this->{srchoutfile},
		args => [$this, $this->{query}]
	    }
    	]);
    }

    ## post processing
    if (! $this->{opt}->{SkipParse}) {
    	$this->parse_result;
    	if (! $this->{opt}->{SkipPostProc}) {

		$this->postproc;
	}
    }

    if (! $this->{opt}->{SaveResult}) {
	if (-f $this->{srchoutfile}) {
		unlink($this->{srchoutfile});
	}
    }

    return 0;
}

##############################################################################
sub create_dbindex {
	## purpose: create db index (e.g. by formatdb)
	## !! should be defined in subclass if required
}
sub searchCommand {
	my($this, $qfile) = @_;
	## purpose: return the command string of the actual search
	## !! should be defined in subclass
}
sub resultOutFile {
}
sub convertCommand {
	my($this) = @_;
	## [postproc] return the command string for converting
	## homology result into a common 'getline' format
	## !! should be defined in subclass
	return 0;
}

##############################################################################
sub execSearchAll {
    my($this, $fileDivQuery) = @_;
    my($procnum) = $this->get_option('procnum');
    my(%currproc, @currproc);
    my($found_pid);
    
    for(my $idx = 1; ; $idx++) {
#        my $queryfile = "$fileDivQuery.$idx";
	my($queryfile) = <$fileDivQuery.$idx.*>;
        last if (! -e "$queryfile");
	unlink("$this->{srchoutfile}.$idx") if (-f "$this->{srchoutfile}.$idx");

        my $fname = File::Basename::basename($queryfile);
        my $db   = File::Basename::basename($this->{database});
	my $title = $this->{title} . ".$db-$fname";
	if ($procnum >= 2) {
		while ((@currproc = keys %currproc) >= $procnum) {
			foreach my $pid (@currproc) {
				if ($found_pid =
					POSIX::waitpid($pid, &POSIX::WNOHANG)) {
					delete $currproc{$found_pid};
print STDERR "$pid, $found_pid Done\n";
				}
			}
			sleep 5;
		}
		if (my $cpid = fork) {
			$currproc{$cpid} = 1;
print STDERR "###$idx $cpid\n";
		} else {
			$this->execSearch($queryfile, $idx);
			exit(0);
		}
	} else {
		$this->execSearch($queryfile);
	}
    }
    while ((@currproc = keys %currproc) > 0) {
	foreach my $pid (@currproc) {
		if ($found_pid =
			POSIX::waitpid($pid, &POSIX::WNOHANG)) {
			delete $currproc{$found_pid};
print STDERR "$pid $found_pid Done\n";
		}
	}
	sleep 5;
    }
    if ($procnum >= 2) {
	system("$main::CMD_cat $this->{srchoutfile}.[0-9]* > $this->{srchoutfile}");
    }
}

sub execSearchCmd {
	my($this, $query, $idx) = @_;

##	my $command = $this->searchCommand($query, $this->{database});
##	die if (! $command);

	my $outfile;
	if ($this->{srchoutfile}) {
		$outfile  = $this->{srchoutfile};
		$outfile .= ".$idx" if ($idx);
	}

	$this->{cmdproc}->execute(
		[ { procname => 'searchCommand',
			type => 'method',
			args => [$this, $query, $this->{database}],
			outfile => $outfile,
			appendmode => 1 } ]
	);
}
sub execSearch {
	my($this, $query, $idx) = @_;
	## Default for BLAST, where multiple queries can be searched by one call.
	## For FASTA, call execMultiSearch()
	$this->execSearchCmd($query, $idx);
}
## for FASTA
sub execMultiSearch {
	my($this, $query, $idx) = @_;
	my($tmpquery) = "$main::DIR_work/_tmpquery.$$";
	my $rfh = new FileHandle($query);
	my($wfh);
	while (<$rfh>) {
		if (/^>/) {
			if ($wfh) {
				$wfh->close;
				$this->execSearchCmd($tmpquery, $idx);
			}
			$wfh = new FileHandle(">$tmpquery");
		}
		print $wfh $_;
	}
	$wfh->close;
	$this->execSearchCmd($tmpquery, $idx);
	$rfh->close;
	unlink($tmpquery);
}
## parse output of various alignment programs
sub parse_result {
	my($this) = @_;
	if ($this->{opt}->{MergeMaxGap}) {
		&Alignment::setMergeMaxGap( $this->{opt}->{MergeMaxGap} );
	}
	my($classname) = $this->parser_classname;
	my(%parserOpt) = ( sp1=>$this->{sp1}, sp2=>$this->{sp2},
		seqfile1=>$this->{query}, seqfile2=>$this->{database},
		progname=>$this->{title}, classname=>$classname,
		SkipFinalOverlapCheck => $this->{opt}->{SkipFinalOverlapCheck},
		maskRegion => $this->{opt}->{maskRegion},
	);
	my $command = sub {
		my $alignList = Tools::ReadHomologyData::read(%parserOpt);
		$this->{alignments} = $alignList;
		$alignList->sort_by_from;
		foreach my $ali ($alignList->list) {
			$ali->print_align_info;
		}
		return 0;
	};
	my $srchresfile;
    	if ($this->{opt}->{SaveResult}) {
		$srchresfile = $this->{srchresfile};
	} else {
		$srchresfile = "/dev/null";
	}
	$this->{cmdproc}->execute([ {
		procname => $command,
		infile => $this->{srchoutfile},
		outfile => $srchresfile,
		redirect_out => 1,
	} ]);
}
## find orthologs and output result
sub postproc {
	my($this) = @_;
	$this->{cmdproc}->execute([{
		procname => \&postproc_sub,
		type => 'code',
		args => [$this],
		depend => [ $this->{srchresfile} ],
		target => $this->{aligninfo_out},
		outfiles => [$this->{align_out}, $this->{alignseq_out},
				$this->{aligninfo_out}],
	}]);
	$this->index_alignseq;
}
sub postproc_sub {
	my($this) = @_;
	my $alignList = $this->get_alignments;

	if ($this->{opt}->{LargeGapForSplit}) {
		$this->{alignments} = $alignList =
			$alignList->split_alignment_all(
				$this->{opt}->{LargeGapForSplit},
				$this->{opt}->{MinScoreForSplit});
	}
	$alignList->sort_by_from;
	$alignList->findBestHit(UseSumScore=>1, %{$this->{opt}});
	$this->output_result($alignList);
}
sub output_result {
	my($this, $alignList) = @_;
	my $alignRegOut = &main::getAlignFilePath($this->{sp1}, $this->{sp2},
			'align', $this->{title}, 'work');
	my $alignSeqOut = &main::getAlignFilePath($this->{sp1}, $this->{sp2},
			'alignSeq', $this->{title}, 'work');
	my $alignInfoOut = &main::getAlignFilePath($this->{sp1}, $this->{sp2},
			'alignInfo', $this->{title}, 'work');
	my $FH_aliReg = FileHandle->new( ">$alignRegOut");
	my $FH_aliSeq = FileHandle->new(">$alignSeqOut");
	my $FH_aliInfo = FileHandle->new(">$alignInfoOut");

	foreach my $align ($alignList->list) {
		$align->print_region($FH_aliReg, ['bestflag']);
		$align->print_align_info($FH_aliInfo,['bestflag']);
		$align->print_region($FH_aliSeq);
		$align->print_alignment($FH_aliSeq);
	}
	return 0;
}
	
sub get_alignments {
	my($this) = @_;
	if ($this->{alignments}) {
		$this->{alignments};
	} else {
		if ($this->{opt}->{MergeMaxGap}) {
		    &Alignment::setMergeMaxGap( $this->{opt}->{MergeMaxGap} );
		}
		my $ali = Tools::ReadHomologyData::read(
			sp1=>$this->{sp1}, sp2=>$this->{sp2},
			seqfile1=>$this->{query},seqfile2=>$this->{database},
			classname=>"CGAT", infile=>$this->{srchresfile},
			SkipFinalOverlapCheck=>1);
		$this->{alignments}= $ali;
		$ali;
	}
}
sub index_alignseq {
	my($this) = @_;
	$this->{cmdproc}->execute([ {
		procname => \&mkAlignSeqIndex,
		type => 'code',
		args => [$this->{alignseq_out}],
		depend => [$this->{alignseq_out}],
		target => "$this->{alignseq_out}.db"
	} ]);
}
sub mkAlignSeqIndex {
    my($file) = @_;
    my(%hashDb);
    my($fp);
    local(*FH);

    open(FH, "$file") || die("Can not open $file($!)");
#    tie %hashDb, 'GDBM_File', "${file}.db", &GDBM_File::GDBM_WRCREAT, 0755
#			|| die("Can not dbmopen $file($!)");
    tie %hashDb, 'SDBM_File', "${file}.db",
			&Fcntl::O_WRONLY|&Fcntl::O_CREAT, 0755
			|| die("Can not dbmopen $file($!)");

    $fp = tell(FH);
    while(<FH>) {
        if (/^(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s+/) {
            my($from1, $to1) = ($1, $2);
            my($from2, $to2) = ($3, $4);
            $hashDb{"$from1-$to1:$from2-$to2"} = $fp;
        }
        $fp = tell(FH);
    }
    untie(%hashDb);
    close(FH);
    return 0;
}


###############################################################################
sub subDivQuery {
    my($fileQuery, $fileDivQuery, $options) = @_;
    my($oldfh);
    my($fh);

    $oldfh = select();
    $fh = new FileHandle(">$fileDivQuery") || die("Can not open $fileDivQuery($!)");
    select($fh);
    &divQuery($fileQuery, $options->{QueryLen}, $options->{QueryOverlap});
    $fh->close();
    select($oldfh);

    &divQueryFile($fileDivQuery, $options->{QueryNum});

    return 0;
}

###############################################################################
# Split a long sequence into multiple short sequences for query
##############################################################################
sub divQuery {
    my($filename, $len, $ovlp) = @_;
    my($seqQuery);
    my($from, $to);
    my($lenQuery);
    my($seq);
    my($fh);

    die "divQuery with zero length\n" if (! $len);

    $seqQuery = "";
    $fh = new FileHandle("$filename") or die "Can not open $filename($!)";
    while (<$fh>) {
        chomp();
        next if (/^>/);
        next if (/^\s*$/);
        $seqQuery .= $_;
    }
    $fh->close();

    # dividing the query sequence
    $from = 1;
    $lenQuery = length($seqQuery);
    while ($from < $lenQuery) {
        $to = $from + $len - 1;
        $seq = substr($seqQuery, $from - 1, $len);
        print_seq("Qry:$from:$to", $seq);
        $from += $len - $ovlp;
    }

    return;
}
sub print_seq {
    my($title, $seq) = @_;
    $seq =~ s#(.{1,60})#$1\n#g;
    print ">$title\n", "$seq\n";
    return;
}
###############################################################################
# Divide a file containing many query sequences into multiple files
#  [INPUT]
#    filename: input file
#    nseq: maximum number of sequences in one file
#  [OUTPUT]
#    filename.## : divided query file
sub divQueryFile {
    my($filename, $nseq) = @_;
    my($fileout);
    my($fileIdx);
    my($n);
    my($fr);
    my($fw);

  ## clean directory
    &unlinkQueryFiles("$filename.");
   
    $fr = new FileHandle("$filename") || die("Can not open $filename($!)");

    $fileIdx = 1;
    $fileout = "$filename.$fileIdx";
#    $fw = new FileHandle(">$fileout") || die("Can not open $fileout($!)");

    $n = 0;
    my($From, $To, $form, $to);
    my($out);
    while(<$fr>) {
        if (/^>(\S+)/) {
	    my($dmy,$from,$to) = split(/:/);
            if ($nseq <= $n) {
                $fileout = "$filename.$fileIdx.$From-$To";
                $fw = new FileHandle(">$fileout") || die("Can not open $fileout($!)");
		$fw->print($out);
                $fw->close();
                $fileIdx++;
		$out = '';
		$From = 0;
                $n = 0;
            }
	    $From = $from if (! $From);
	    $To = $to;
            $n++;
        }
	$out .= $_;
#        $fw->print($_);
    }
    $fr->close();
    $fileout = "$filename.$fileIdx.$From-$To";
    $fw = new FileHandle(">$fileout") || die("Can not open $fileout($!)");
    $fw->print($out);
    $fw->close();

    return $fileIdx;
}
sub unlinkQueryFiles {
	my($filename) = @_;
	foreach my $f (<$filename*>) {
		unlink($f);
	}
}
################################################################################
sub toupper_seq {
	my($this) = @_;

	my $new_database = &main::absolutePath(
		File::Basename::basename($this->{database}), 'tmpwork');
	my $new_query = &main::absolutePath(
		File::Basename::basename($this->{query}), 'tmpwork');

	if (&islower_seq_sub($this->{database})) {
		$this->{cmdproc}->execute( {
			type=>'code',
			procname=>\&toupper_seq_sub,
			depend=>[$this->{database}],
			outfile=>$new_database,
			args=>[$this->{database}, $new_database]
		} );
		$this->{database_orig} = $this->{database};
		$this->{database} = $new_database;
	}
	if (&islower_seq_sub($this->{query})) {
		$this->{cmdproc}->execute( {
			type=>'code',
			procname=> \&toupper_seq_sub,
			depend=>[$this->{query}],
			outfile=>$new_query,
			args=>[$this->{query}, $new_query]
		} );
		$this->{query_orig} = $this->{query};
		$this->{query} = $new_query;
	}
}
sub islower_seq_sub {
	my($seqfile) = @_;
	my($flag, $cnt);
	open(S, $seqfile) || die "Can't open infile $seqfile\n";
	while(<S>){
		if (/^>/ || /^$/) {
			next;
		} else {
			if ( /[a-z]/) {
				$flag = 1;
				last;
			} elsif (++$cnt > 10) {
				last;
			}
		}
	}
	## already upper or not
	close(S);
	return $flag;

}
sub toupper_seq_sub {
	my($seqfile, $outfile) = @_;
	open(S, $seqfile) || die "Can't open infile $seqfile\n";
	open(O, ">$outfile") || die "Can't open outfile $outfile\n";
	while(<S>){
		if (/^>/) {
			print O $_;
			next;
		} else {
			print O uc($_);
		}
	}
	close(S);
	close(O);
	return 0;
}
################################################################################
1;#
##############################################################################
