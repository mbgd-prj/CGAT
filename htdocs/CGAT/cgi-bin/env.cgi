#!/usr/bin/perl -s

use lib "$ENV{'CGAT_HOME'}/perllib";

$| = 1;
#
print "Content-type:text/html\n";
print "\n";

print "<HTML>\n";
print "<HEAD>\n";
print "<TITLE></TITLE>\n";
print "</HEAD>\n";
print "\n";
print "<BODY>\n";

print "<TABLE BORDER>\n";
print "<TR><TH>Name</TH><TH>Value</TH></TR>\n";
foreach $key (sort(keys(%ENV))) {
    print "<TR><TD>", $key, "</TD><TD>", $ENV{$key}, "</TD></TR>\n";
    if ($key eq "CONTENT_LENGTH") {
        read(STDIN, $read_buf, $ENV{$key});
        print "<TR><TD>Standard input</TD><TD>", $read_buf, "</TD></TR>\n";
    }
}
#foreach $key ('umask', '/bin/echo $SHELL') {
#    print "<TR><TD>", $key, "</TD><TD>", `$key`, "</TD></TR>\n";
#}
print "</TABLE>\n";
print "</BODY>\n";
print "</HTML>\n";

