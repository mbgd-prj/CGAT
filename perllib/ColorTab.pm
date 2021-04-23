#!/usr/bin/perl -s

###############################################################################
use FileHandle;
use RGB;
require "CGAT_Conf.pl";

###############################################################################
package ColorTab;
###############################################################################
# colorTab:
# describe three values (threshold value, color, comment) delimited by TAB
###############################################################################

###############################################################################
sub new {
    my($class, $tabfile) = @_;
    my($this) = {};
    bless($this, $class);

    $this->{'RGB'} = RGB->new();
    if ($tabfile) {
        $this->loadColorTab($tabfile);
    }
    return($this);
}

###############################################################################
#
sub getColor {
    my($this, $value) = @_;
    my($color);
    my($ent);

    $color = "#FFFFFF";
    foreach $ent (@{$this->{"TAB"}}) {
        if ($value < $ent->{'val'}) {
            $color = $ent->{'col'};
            if ($color !~ /^#/) {
                $color = $this->{'RGB'}->getColor($color);
            }
            last;
        }
    }

    return $color;
}

###############################################################################
#
sub loadColorTab {
    my($this, $filename) = @_;
    my($fh);
    my($ent);

    $fh = new FileHandle("$filename") || die("Can not open $filename($!)");
    while(<$fh>) {
        chomp();

        next if (/^\s*$/);	# null line   
        if (/^\s*#/) {
            if (/(id.*)/i) {
                @idList = split(/\s+/);
                for($i = 0; $i < scalar(@idList); $i++) {
                    my($key) = lc($idList[$i]);
                    $idHash{"$key"} = $i;
                }
            }
            next;
        }

        my(@dat) = split(/\t/);
        $ent = {};
        $ent->{'val'} = $dat[$idHash{'id'}];
        $ent->{'col'} = $dat[$idHash{'color'}];
        $ent->{'msg'} = $dat[$idHash{'legend'}];

        push(@{$this->{'TAB'}}, $ent);

    }
    $fh->close();

    return;
}

###############################################################################
#
sub helpHtml {
    my($this) = shift;
    my($ent);

    print "<TABLE BORDER>\n";
    foreach $ent (@{$this->{"TAB"}}) {
        my($color) = $ent->{'col'};
        if ($color !~ /^#/) {
            $color = $this->{'RGB'}->getColor($color);
        }

        print "<TR>\n";
        print "<TD BGCOLOR=\"", $color, "\" WIDTH=\"100\"><BR></TD>\n";
        print "<TD>", $ent->{'msg'}, "</TD>\n";
    }
    print "</TABLE>\n";

    return;
}
###############################################################################
package ColorTabRotate;
@ISA = qw{ColorTab};
sub getColor {
	my($this, $value) = @_;
	my $colornum = @{$this->{TAB}};
	$this->SUPER::getColor($value % $colornum);
}

###############################################################################
if ($0 eq __FILE__) {
    exit;
}

###############################################################################
1;#
###############################################################################
