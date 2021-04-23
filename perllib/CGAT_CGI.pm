#!/usr/bin/perl

package CGAT_CGI;
use CGI;

sub new {
	my($class) = @_;
	my($this) = {};
	$this->{cgi} = CGI->new;
	bless $this, $class;
}
sub getOpt {
	my($this, @names) = @_;
	my($opt) = {};
	if (! @names) {
		@names = $this->{cgi}->param;
	}
	foreach $name (@names) {
		$opt->{$name} = $this->{cgi}->param($name);
	}
	$opt;
}
sub header {
	my($this, @opt) = @_;
	$this->{cgi}->header(@opt);
}
1;
