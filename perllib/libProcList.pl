#!/usr/bin/perl -s

###############################################################################
use File::Basename;
use File::Path;
require "libCheckPoint.pl";

#$main::DEBUG = 1;

###############################################################################
#
sub doProcList {
    my(@procList) = @_;
    my($proc);
    my($procTitle, $procName, $procOpt, $procIn, $procFilter, $procOutDir, $procOut, $procNoexit);
    my($cmd);
    my($ret);

    foreach $proc (@procList) {
        $procTitle  = $proc->{'title'};
        $procName   = $proc->{'procname'};
        $procOpt    = $proc->{'opt'};
        $procIn     = $proc->{'infile'};
        $procFilter = $proc->{'filter'};
        $procOutDir = $proc->{'outdir'};
        $procOut    = $proc->{'outfile'};
        $procNoexit = $proc->{'noexit'};

        if ($procOutDir && $procOut) {
            $procOut = "$procOutDir/$procOut";
        }

        # make output dir
        my($name, $path) = fileparse($procOut);
        mkpath("$path", 0, 0750) if ($path);

        $cmd = "$procName $procOpt";
        $cmd .= " < $procIn"     if ($procIn);
        $cmd .= " | $procFilter" if ($procFilter);
       	$cmd .= " > $procOut"    if ($procOut);
        if ($main::DEBUG) {
            print STDERR "START :: $cmd\n";
        }
        $ret = &procCheckPoint("$procTitle", $cmd);
        if ($main::DEBUG) {
            print STDERR "END :: $procName\n";
            print STDERR "Sta :: $ret\n";
        } 
        #
        if ($ret) { # コマンド実行エラー
            if ($procNoexit) {
                # しかし、処理は中断しない
                print STDERR "\n";
            }
            else {
                die("ERROR :: '$cmd'");
            }
        }
    }
    return;
}

###############################################################################
if ($0 eq __FILE__) {
	&doProcList(@ProcList);
    exit;
}

###############################################################################
1;#
###############################################################################
