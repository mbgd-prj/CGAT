#!/usr/bin/perl -s

###############################################################################
use FileHandle;
require "CGAT_Conf.pl";

###############################################################################
package RGB;

###############################################################################
# パッケージの初期化
sub new {
    my($class) = shift;
    my($fileRgb) = shift;
    my($self) = {};

    bless($self, $class);

    if (! $fileRgb) {
        $fileRgb = $main::FILE_RGB;
    }
    $self->loadColorInfo($fileRgb);

    return($self);
}

###############################################################################
#
sub getColor {
    my($self) = shift;
    my($name) = @_;
    my($color);

    $name = uc($name);
    $color = $self->{'name2color'}->{"$name"};
    if (! $color) {
        $color = "#FFFFFF";
    }

    return $color;
}

###############################################################################
#
sub loadColorInfo {
    my($self) = shift;
    my($filename) = @_;
    my($fh);

    $fh = new FileHandle("$filename") || die("Can not open $filename($!)");
    while(<$fh>) {
        chomp();

        my($r, $g, $b, $name) = split(/\s+/);

        my($hexColor) = sprintf("#%02X%02X%02X", $r, $g, $b);

        $name = uc($name);
        $self->{'name2color'}->{"$name"} = $hexColor;
    }
    $fh->close();

    return;
}

###############################################################################
if ($0 eq __FILE__) {
    exit;
}

###############################################################################
1;#
###############################################################################
