#!/usr/bin/perl -s

###############################################################################
use File::Basename;
use File::Path;
use FileHandle;
use Carp;
require "libCheckPoint.pl";
require 'updateMisc.pl';

#$main::DEBUG = 1;

###############################################################################
package Command;
###############################################################################
## Base class for Command objects
## 	new: Create new command object
## 	execute: Call this method to execute the command
## 	execute_main: Should be overridden in subclass
###############################################################################
package Command;
@Command::Options = (
procname,args,command,infile,outfile,outfiles,appendmode,filter,target,depend
);
sub new {
	my($class, $cmdinfo, $parent) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{parent} = $parent;
	$this->initialize($cmdinfo);
	$this;
}
sub initialize {
	my($this, $cmdinfo) = @_;

	my $optre = join('|', @Command::Options);
	foreach $k (keys %{$cmdinfo}) {
		if ($k =~ /^($optre)$/) {
			$this->{$k} = delete $cmdinfo->{$k};
		}
	}
	if ($this->{outfile} && ! $this->{target}) {
		$this->{target} = $this->{outfile};
	}
	if ($this->{infile} && ! $this->{depend}) {
		$this->{depend} = [ $this->{infile} ];
	}
	$this->subst_vars;
}
sub set_parent {
	my($this, $parent) = @_;
	$this->{parent} = $parent;
}

sub is_atomic { 1; }

sub execute {
	my($this, @args) = @_;
	my($ret);

	if ($ret = $this->pre_exec()) {
		## skip
		return 0;
	}
	if (($this->get_option('noexec') && $this->is_atomic) ) {
		print STDERR "Command: ", $this->command, "\n";
	} else {
		$ret = $this->execute_main(@args);
	}
	$this->post_exec($ret);
	return $ret;
}
sub execute_main {
	## should be overridden
	my($this) = @_;
	system($this->{command}) if ($this->{command});
}
sub command {
	my($this) = @_;
	if (ref $this->{args} eq 'ARRAY') {
		return "$this->{procname} " . join(' ', @{$this->{args}});
	} else {
		return "$this->{procname} " . $this->{args};
	}
}
sub subst_var {
	my($this, $var) = @_;
	if (my $filebase = $this->get_option('filebase')) {
		my $rel_filebase = File::Basename::basename($filebase);
		$$var =~ s/\/##/\/$rel_filebase/g;	## relative path
		$$var =~ s/##/$filebase/g;		## absolute path
	}
}
sub subst_vars {
	my($this) = @_;
	foreach $k (keys %{$this}) {
		my $ref = ref($this->{$k});
		if ($ref eq 'ARRAY') {
			for (my $i = 0; $i < @{$this->{$k}}; $i++) {
				$this->subst_var( \($this->{$k}->[$i]) );
			}
		} elsif ($ref eq 'HASH') {
			foreach $kk (keys %{$this->{$k}}) {
				$this->subst_var( \($this->{$k}->{$kk}) );
			}
		} elsif (! $ref) {
			$this->subst_var( \($this->{$k}) );
		}
	}
}
sub pre_exec {
	my($this) = @_;
	$this->subst_vars;
        if ($this->get_option(verbose)) {
            print STDERR "START :: " . $this->command . "\n";
        }
	if ($this->{checkpoint}) {
		if (&passCheckPoint($this->{checkpoint})) {
print STDERR "Checkpoint file exists -- skip\n";
			return 1;
		}
	}
	if (@{$this->{depend}} && $this->{target} && ! $this->{appendmode}) {
		my($flag) = 1;
		foreach $f (@{$this->{depend}}) {
			if (&main::cmprFileDate($f, $this->{target}) > 0) {
				$flag = 0;
				last;
			}
		}
		if ($flag) {
			## UpToDate
			print STDERR "$this->{target} is up-to-date\n";
			return 1;
		}
		if (-f $this->{target}) {
			unlink($this->{target});
		}
	}
	if (! $this->get_option('noexec')) {
		if ($this->{outfile}) {
			CmdUtil::makePath($this->{outfile});
		}
		if (ref $this->{outfiles} eq 'ARRAY') {
			foreach my $f (@{$this->{outfiles}}) {
				CmdUtil::makePath($f);
			}
		}
	}
	if ($this->{chdir}) {
		chdir($this->{chdir});
	}
	return 0;
}
sub post_exec {
	my($this, $ret) = @_;
        if ($this->get_option(verbose)) {
            print STDERR "END :: $this->{procname}\n";
            print STDERR "Sta :: $ret\n";
        }
	if (! $ret && $this->{checkpoint}) {
		&touchCheckPoint($this->{checkpoint});
	}
}
sub get_option {
	my($this, $optname) = @_;
	if (defined $this->{$optname}) {
		return $this->{$optname};
	} elsif (defined $this->{opt}->{$optname}) {
		return $this->{opt}->{$optname};
	} elsif ($this->{parent} && UNIVERSAL::isa($this->{parent}, 'Command')) {
		## options can be inherited from the parent
		return $this->{parent}->get_option($optname);
	}
}
###############################################################################
package ShellCommand;
use base qw{Command};
sub execute_main {
	my($this) = @_;

	my $filebase = $this->get_option('filebase');

	my $args = $this->{args};

	if (ref($args) eq 'ARRAY') {
		$args = join(' ', @{$args});
	} elsif (ref($args) eq 'HASH') {
		my $newargs;
		foreach $k (keys %{$args}) {
			$newargs .= "-$k=$args->{$k} "
		}
		chomp $newargs;
		$args = $newargs;
	}
	$args =~ s/##/$filebase/g;

        my $command = "$this->{procname} $args";
        $command .= " < $this->{infile}"     if ($this->{infile});
        $command .= " | $this->{filter}" if ($this->{filter});
	if ($this->{outfile}) {
		if($this->{appendmode}) {
	       		$command .= " >> $this->{outfile}";
		} else {
		       	$command .= " > $this->{outfile}";
		}
	}

#        # make output dir
#	if ($this->{outfile}) {
#		CmdUtil::makePath($this->{outfile});
#	}

	$this->{command} = $command;

	my $ret = system($this->{command});

	return $ret;
}

###############################################################################
package CodeCommand;
use base qw{Command};
sub execute_main {
	my($this) = @_;
	my($redirect_out, $redirect_in);
	if($this->{outfile}) {
		$| = 1;
		CmdUtil::makePath($this->{outfile});
		if ($this->{appendmode}) {
			open(SAVEOUT, ">&STDOUT");
			open(STDOUT, ">>$this->{outfile}");
		} else {
			open(SAVEOUT, ">&STDOUT");
			open(STDOUT, ">$this->{outfile}");
		}
		$redirect_out = 1;
	}
	if (-f $this->{infile}) {
		$| = 1;
		open(SAVEIN, "<&STDIN");
		open(STDIN, "<$this->{infile}");
		$redirect_in = 1;
	}
	my $ret = &{$this->{procname}}(@{$this->{args}});
	if ($redirect_out) {
		close(STDOUT);
		open(STDOUT, ">&SAVEOUT") || die "Can't dup SAVEOUT\n";
	}
	if ($redirect_in) {
		open(STDIN, "<&SAVEIN");
	}
	$ret;
}

###############################################################################
package MethodCommand;
use base qw{Command};
sub execute_main {
	my($this) = @_;
	my($redirect_out, $redirect_in);
	if ($this->{outfile}) {
		$| = 1;
		if($this->{appendmode}) {
			open(SAVEOUT, ">&STDOUT");
			open(STDOUT, ">>$this->{outfile}");
			$redirect_out = 1;
		} else {
			open(SAVEOUT, ">&STDOUT");
			open(STDOUT, ">$this->{outfile}");
			$redirect_out = 1;
		} 
	}
	if (-f $this->{infile}) {
		$| = 1;
		open(SAVEIN, "<&STDIN");
		open(STDIN, "<$this->{infile}");
		$redirect_in = 1;
	}
	my $obj = shift (@{$this->{args}});
	my $procname = $this->{procname};
	my $ret = eval '$obj->$procname(@{$this->{args}})';
	if (! defined $ret && $@) {
		## error message is sotred in $@ when die is called in eval.
		print STDERR "$@\n";
		$ret = 1;
	}
	if ($redirect_out) {
		open(STDOUT, ">&SAVEOUT");
	}
	if ($redirect_in) {
		open(STDIN, "<&SAVEIN");
	}
	$ret;
}

###############################################################################

###############################################################################
package CmdProc;
use base qw{Command};

sub new {
	my($class, $filebase, $cmdlist, $opt) = @_;
	my($this) = {};
	bless $this, $class;
	if ($cmdlist) {
		$this->add($cmdlist);
	}
	if ($main::noexec) {
		## set noexec from the command argument
		$opt->{noexec} = 1;
	}
	$this->{filebase} = $filebase;
	$this->initialize($opt);
	$this;
}
sub initialize {
	my($this, $opt) = @_;
	$this->SUPER::initialize($opt);
	$this->{opt} = $opt;
}
sub add {
	my($this, $cmdlist) = @_;
	if (ref $cmdlist eq 'ARRAY') {
    		while ($proc = shift @{$cmdlist}) {
			my $command = $this->create_childcmd($proc);
			push(@{$this->{cmdlist}}, $command);
			my $target = $command->{target};
			if ($target) {
				if ($this->{target_hash}->{target}) {
					print STDERR "Warning: duplicated target\n";
				}
				$this->{target_hash}->{$target} = $command;
			}
		}
	} else {
		my $command = $this->create_childcmd($cmdlist);
		push(@{$this->{cmdlist}}, $command);
	}
}
sub is_atomic { 0; }
sub clear_cmdlist {
	my($this) = @_;
	$this->{cmdlist} = [];
}

sub build {
	my($this, $target) = @_;
	my($updstat);
	if (! $target) {
		my $cmdlist = $this->{cmdlist};
		$target = $cmdlist->[$#{$cmdlist}]->{target};
	}
	$this->subst_var( \($target) );
	my $target_obj = $this->find_target($target);
	if (! $target_obj) {
		return;
	}
	foreach $dep (@{$target_obj->{depend}}) {
		$updstat |= $this->build($dep);
		$updstat |= ((my $uu= &main::cmprFileDate($dep, $target)) > 0);
	}
	if ($this->{verbose}) {
		print STDERR "Build: $target\n";
	}
	if ($updstat) {
		$target_obj->execute;
	}
	return $update;
}
sub set_target {
	my($this, $target_command) = @_;
	my($targetname) = $target_command->get_option('target');
	$this->{target_hash}->{$targetname} = $target_command;
	if ($this->{parent}) {
		$this->{parent}->set_target($targetname, $this);
	}
}
sub find_target {
	my($this, $target) = @_;
	if ($this->{target_hash}->{$target}) {
		return $this->{target_hash}->{$target};
	} elsif ($this->{parent}) {
		return $this->{parent}->find_target($target);
	}
}

sub execute_main {
    my($this, $cmdlist) = @_;
    my($proc);
    my($procTitle, $procName, $procOpt, $procIn, $procFilter,
		$procOutDir, $procOut, $procNoexit);
    my($cmd);
    my($ret);

    $this->add($cmdlist);
    while ($proc = shift @{$this->{cmdlist}}) {
	## create Command object
#	$command = $this->create_childcmd($proc);
	$command = $proc;
	die if (! $command);

	$ret = $command->execute();
        if ($ret) {
	    # Execution error
            if ($procNoexit) {
                print STDERR "\n";
            } else {
		if (-f $proc->{outfile}) {
			rename($proc->{outfile}, "$proc->{outfile}.ERROR");
		}
                Carp::confess("ERROR :: '",$command->command,"'");
            }
        }
    }
    return;
}
sub create_childcmd {
	my($this, $proc) = @_;
	my($ret_proc);

	my $ref = ref($proc);

	if ($ref && UNIVERSAL::isa($proc, 'Command')) {
		$ret_proc = $proc;
	} elsif (! $proc->{procname}) {
		return '';
	} elsif (my $refproc = ref($proc->{procname})) {
		if ($refproc eq 'CODE') {
			$ret_proc = CodeCommand->new($proc, $this);
		} elsif (UNIVERSAL::isa($refproc, 'Command')) {
#			$ret_proc = $proc->{command};
			$proc->{procname}->set_parent($this);
			$ret_proc = $proc->{procname};
		}
	} elsif ($proc->{type} eq 'class') {
		if (UNIVERSAL::isa($proc->{procname}, 'Command')) {
			$ret_proc = $proc->{procname}->
				new(@{$proc->{args}}, $proc);
		} else {
			warn "Unknown class: $proc->{procname}\n";
			$ret_proc = '';
		}
	} elsif ($proc->{type} eq 'code') {
		$ret_proc = CodeCommand->new($proc, $this);
	} elsif ($proc->{type} eq 'method') {
		$ret_proc = MethodCommand->new($proc, $this);
	} else {
		$ret_proc = ShellCommand->new($proc, $this);
	}
	if ($ret_proc) {
		$this->set_target($ret_proc);
	}
	return $ret_proc;
}
###############################################################################
# utility functions
package CmdUtil;
sub makePath {
	my($outfile) = @_;
	my($MODE) = "0755";
	if ($outfile && ! -f $outfile) {
	        my($name, $path) = File::Basename::fileparse($outfile);
       		File::Path::mkpath("$path", 0, 0755) if ($path);
	}
}

###############################################################################
#if ($0 eq __FILE__) {
#	&doProcList(@ProcList);
#    exit;
#}

###############################################################################
1;#
###############################################################################
