#!/usr/bin/perl -s

use FileHandle;

package IsFam;

###############################################################################
#
sub new {
    my($class) = shift;
    my($file) = @_;
    my($self) = {};

    bless($self, $class);

    if ($file) {
        $self->loadIsFam($file);
    }

    return($self);
}

###############################################################################
#
sub getFamList {
    my($self) = shift;
    my($name) = @_;

    return @{$self->{'famlist'}};
}

###############################################################################
#
sub getIsFam {
    my($self) = shift;
    my($name) = @_;

    return $self->{'name2fam'}->{"$name"};
}

###############################################################################
#
sub loadIsFam {
    my($self) = shift;
    my($file) = @_;
    my($fh);

    $fh = new FileHandle("$file") || die("Can not open $file($!)");
    while(<$fh>) {
        if (/>(\S+)\s+.+Fam:(.+)$/i) {
            $self->{'name2fam'}->{"$1"} = $2;
            $self->{'fam'}->{"$2"}++;
            if ($self->{'fam'}->{"$2"} == 1) {
                push(@{$self->{'famlist'}}, $2);
            }
        }
    }
    $fh->close();

    return;
}

###############################################################################
if ($0 eq __FILE__) {
    my($obj);
    my($fam);

    $obj = new IsFam($ARGV[0]);

    foreach $fam ($obj->getFamList()) {
        print $fam, "\n";
    }

    for(;;) {
        my($in);

        print "Input IS name : ";
        $in = <STDIN>;
        chomp($in);

        if ($in =~ /^\s*$/) {
            last;
        }

        print "IsFam : ", $obj->getIsFam($in), "\n";
    }

    exit;
}

###############################################################################
1;#
###############################################################################
