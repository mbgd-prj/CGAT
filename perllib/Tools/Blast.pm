#!/usr/bin/perl -s

use Tools::HomologyParser;

###############################################################################
package Tools::Blast;
###############################################################################
@ISA = qw(Tools);
sub new {
	my($class) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parser} = Tools::BlastParser->new;
	$this;
}

###############################################################################
package Tools::BlastResult;
###############################################################################
sub new {
	my($class) = @_;
	my $this = {};
	bless $this, $class;
}
sub hit_list {
	my($this) = @_;
	@{ $this->{hit_list} };
}
sub hsp_list {
	my($this) = @_;
	my(@list);
	foreach my $hit ( $this->hit_list ) {
		push( @list, $hit->hsp_list );
	}
	@list;
}
sub add_hit {
	my($this, $hit) = @_;
	push(@{$this->{hit_list}}, $hit);
	$hit->{result} = $this;
	$this->{hit_key}->{$hit->{name2}} = $hit;
}
sub get_hit {
	my($this, $sname) = @_;
	$this->{hit_key}->{$sname};
}
sub get_step {
	my($this) = @_;
	# return step size of (query, db). (1..normal, 3..translation)
	if ($this->{progname} eq 'BLASTN') {
		return (1,1);
	} elsif ($this->{progname} eq 'TBLASTN') {
		return (1,3);
	} elsif ($this->{progname} eq 'BLASTX') {
		return (3,1);
	} elsif ($this->{progname} eq 'TBLASTX') {
		return (3,3);
	}
	return (1,1);
}
###############################################################################
package Tools::BlastHit;
###############################################################################
@ISA = qw(Tools::HomologyHit);
my $Delim = '#';
sub new {
	my($class, %values) = @_;
	my $this = {};
	bless $this, $class;
	foreach $k (keys %values) {
		$this->{$k} = $values{$k};
	}
	$this;
}
sub add_hsp {
	my($this, $hsp) = @_;
	push(@{$this->{hsp_list}}, $hsp);
	$hsp->{result} = $this->{result};
}
sub hsp_list {
	my($this) = @_;
	my(@list);
	@{$this->{hsp_list}};
}
###############################################################################
package Tools::BlastHsp;
###############################################################################
@ISA = qw(Tools::HomologyHSP);

my $Delim = '#';
sub new {
	my($class, %values) = @_;
	my $this = {};
	bless $this, $class;
	foreach $k (keys %values) {
		$this->{$k} = $values{$k};
	}
	$this;
}
sub print_info_text {
	my($this) = @_;
	print join($Delim, "HSP $this->{hspnum}",
		"$this->{from1}-$this->{to1}",
		"$this->{from2}-$this->{to2}",
		$this->{bitscore}, $this->{rowscore}, $this->{expect},
		$this->{pval}, $this->{numident}, $this->{hsplen},
		$this->{percentident}, $this->{numpositive},
		$this->{hsplen2}, $this->{percentpositive},
		$this->{dir1}, $this->{dir2});
	print "\n";
}

###############################################################################
package Tools::BlastParser;
###############################################################################
@ISA = qw(Tools::HomologyParser);

use FileHandle;
my %Dir = (
	'Plus' => 1, 'Minus' => -1
);
sub new {
	my($class, @filenames) = @_;
	my $this = {};
	bless $this, $class;
	$this->set_filenames(\@filenames) if (@filenames);
	$this;
}
sub readfile {
    my($this) = @_;
    my($blres, $hit, $hsp, $status);
    while ($_ = $this->getline) {
	    chomp;
	    if (/^([A-Z]*BLAST[A-Z]*)[ ]+(\S+)/) {
		$progname = $1; $version = $2;
		if ($blres) {
			return $blres;
		}
	    } elsif (/^[ ]*Query=[ ]*([^ ]*)/) {
		    $query = $1;
		    if ($this->set_queryname($blres, $query) < 0) {
			$this->save_current_line($_);
			return $blres;
		    }
		    $rank = 0;
    		    $blres = Tools::BlastResult->new;
		    $blres->{progname} = $progname;
		    $blres->{version} = $version;
		    $blres->{query} = $query;
	    } elsif (/[ ]*\(([0-9,]+) letters\)/) {
		    $qlen = $1;
		    $qlen =~ s/,//g;
		    $blres->{qlen} = $qlen;
	    } elsif (/Sequences producing significant alignments/) {
		    $status = 'rank';
			## skip reading
		    $dmy = $this->getline;
	    } elsif ($status eq 'rank') {
		    if (/^\s*$/) {
		    	$status = 'hsp';
		    	$dbentname = '';
		    } else {
		    	$enttit = substr($_, 0, 66);
		    	$enttit =~ s/^ *//;
		    	($dbentname, $title) = split(/[ \t]+/, $enttit);
		    	if ($sdb && $dbentname !~ /:/) {
		    		$dbentname = "$sdb:$dbentname";
		    	} elsif ($sdb && $dbentname =~ /gp:(.*)/) {
		    		$dbentname = "$sdb:$1";
		    	}
		    	$score_data = substr($_, 67);
		    	$score_data =~ s/^[ ]+//;
		    	($hiscore, $evalue, $n) = split(/[ ]+/, $score_data);
			$rank++;
			$hit = Tools::BlastHit->new(
				name1=>$blres->{query},
				name2=>$dbentname,
				len1=>$blres->{qlen},
				hiscore=>$hiscore,
				evalue=>$evalue,
				n=>$n,
				rank=>$rank,
			);
			$blres->add_hit($hit);
		    }
	    } elsif ($status eq 'hsp') {
	    	if (/^>[ ]*([^ ]+)/) {
	    		$dbentname = $1;
	    		if ($sdb && $dbentname !~ /:/) {
	    			$dbentname = "$sdb:$dbentname";
	    		} elsif ($sdb && $dbentname =~ /gp:(.*)/) {
	    			$dbentname = "$sdb:$1";
	    		}
	    	} elsif (/^[ ]*Length = ([0-9]+)/) {
	    		$slen = $1;
			$hit = $blres->get_hit($dbentname);
		    	$hit->{len2} = $slen;

	    		$hspnum = 0;
    
    		} elsif (/^[ ]*Score[ ]*=[ ]*([0-9eE\+\.]+)[ ]*bits[ ]*\(([0-9\.]+)\),[ ]*Expect\S*[ ]*=[ ]*([0-9\.\-e]+)/) {
    			$hspnum++;
			$hsp = Tools::BlastHsp->new;
			$hit->add_hsp($hsp);
			$hsp->{name1} = $blres->{query};
			$hsp->{name2} = $dbentname;
    			$hsp->{bitscore} = $hsp->{score} = $1;
    			$hsp->{rowscore} = $2;
    			$hsp->{expect} = $3;
    			$hsp->{pval} = 0;  # no p-value in blast2
    			$hsp->{hspnum} = $hspnum;
    		} elsif (/^[ ]*Identities[ ]*=[ ]*([0-9]+)\/([0-9]+)[ ]*\(([0-9]+)\%\)/) {
    			$hsp->{numident} = $1;
    			$hsp->{hsplen} = $2;
    			$hsp->{percentident} = $hsp->{ident} = $3;
    			$hsp->{dir1} = $hsp->{dir2} = 1; ## default
    		    ## old version
    			if (/Positives[ ]*=[ ]*([0-9]+)\/([0-9]+)[ ]*\(([0-9]+)%\)/) {
    				$hsp->{numpositive} = $1;
    				$hsp->{hsplen2} = $2;
    				$hsp->{percentpositive} = $3;
    			}
    			if (/Strand = ([A-Za-z]+) *\/ *([A-Za-z]+)/) {
    				$hsp->{dir1} = $Dir{$1};
				$hsp->{dir2} = $Dir{$2};
    			}
			$hsp->{dir} = ($hsp->{dir1} eq $hsp->{dir2}) ? 1 : -1;
    		} elsif (/Strand = ([A-Za-z]+) *\/ *([A-Za-z]+)/) {
    		    ## new version
    			$hsp->{dir1} = $Dir{$1};
			$hsp->{dir2} = $Dir{$2};
			$hsp->{dir} = ($hsp->{dir1} eq $hsp->{dir2}) ? 1 : -1;
    		} elsif (/Frame *= *([\-\+]*[1-3]) *\/ *([\-\+]*[1-3])/) {
			$frame1 = $1; $frame2 = $2;
			if ($frame1 < 0) {
				$hsp->{dir1} = -1;
			}
			if ($frame2 < 0) {
				$hsp->{dir2} = -1;
			}
			$hsp->{dir} = ($hsp->{dir1} eq $hsp->{dir2}) ? 1 : -1;
    		} elsif (/Frame *= *([\-\+]*[1-3])/) {
			$frame = $1;
			if ($frame < 0) {
				if ($blres->{progname} =~ /^TBLASTN/i) {
					$hsp->{dir2} = -1;
				}
			}
			$hsp->{dir} = ($hsp->{dir1} eq $hsp->{dir2}) ? 1 : -1;
    		} elsif (/^Query:[ ]*([0-9]+)[ ]*([A-Za-z\-\*]+)[ ]*([0-9]+)/) {
		    ## alignment (query)
			if ($hsp->{dir1} > 0) {
    				$hsp->{from1} = $1 if (! $hsp->{from1});
	    			$hsp->{to1} = $3;
			} else {
    				$hsp->{to1} = $1 if (! $hsp->{to1});
	    			$hsp->{from1} = $3;
			}
			$hsp->{aliseq1} .= $2;
    		} elsif (/^Sbjct:[ ]*([0-9]+)[ ]*([A-Za-z\-\*]+)[ ]*([0-9]+)/) {
		    ## alignment (subject)
			if ($hsp->{dir2} > 0) {
    				$hsp->{from2} = $1 if (! $hsp->{from2});
	    			$hsp->{to2} = $3;
			} else {
    				$hsp->{to2} = $1 if (! $hsp->{to2});
	    			$hsp->{from2} = $3;
			}
			$hsp->{aliseq2} .= $2;
    		} elsif (/^[ ]+Database:/) {
		    ## end
    		}
    	}
    }
    return $blres;
}
###############################################################################
package main;
if ($0 eq __FILE__) {
	die "Usage: $0 filename\n" if (! @ARGV);
	$blparse = Tools::BlastParser->new($ARGV[0]);
	while ($blres = $blparse->read) {
		foreach $hsp ($blres->hsp_list) {
			print join(' ',
				$hsp->{'name1'},
				$hsp->{'name2'},
				$hsp->{'from1'},
				$hsp->{'to1'},
				$hsp->{'from2'},
				$hsp->{'to2'},
				$hsp->{'dir2'},
				$hsp->{'percentident'},
				$hsp->{'bitscore'} ), "\n";
		}
	}
}

###############################################################################
1;#
###############################################################################
