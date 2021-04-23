#!/usr/bin/perl

package Debug;

sub debug_init {
	## by global variable
	$Debug::status = $main::DEBUG;
}
sub debug_status {
	my($status) = @_;
	$Debug::status = $status;
}
sub debug_on {
	$Debug::status = 1;
}
sub debug_off {
	$Debug::status = 0;
}
sub debug_exec {
	my($code, $level) = @_;
	$level = 1 if (! $level);
	if ($Debug::status >= $level) {
		if (ref $code eq 'CODE') {
			&{$code};
		} else {
			eval($code);
		}
	}
}
sub message {
	my($message, $level) = @_;
	&debug_exec(qq{print "$message\n"}, $level);
}
################################################
package main;
if (__FILE__ eq $0) {
	$message = "error";
	Debug::debug_on;
	Debug::debug_exec(qq{ print "1: $message\n" });
	Debug::debug_off;
	Debug::debug_exec(qq{ print "2: $message\n" });
}
################################################
1;
