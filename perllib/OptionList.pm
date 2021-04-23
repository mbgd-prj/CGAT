#!/usr/bin/perl

package OptionList;
sub new {
	my($class, $optinfo, $opt) = @_;
	my($this) = {};
	my($optlist) = {};
	foreach $o (@{$optinfo}) {
		my($name, $descr, $switch, $default, $type, $infoopt) = @{$o};
		$optlist->{$name}->{value} = $default;
		$optlist->{$name}->{switch} = $switch;
		$optlist->{$name}->{description} = $descr;
		$optlist->{$name}->{type} = ($type ? $type : 'text');
		$optlist->{$name}->{option} = $infoopt;
	}
	$this->{optdelim} = $opt->{optdelim};
	$this->{optlist} = $optlist;
	bless $this, $class;
}
sub get_optlist {
	my($this, $optname) = @_;
	my($o, @list);
	foreach $o (keys %{$this->{optlist}}) {
		push(@list, $o);
	}
	return @list;
}
sub set_options {
	my($this, $opt) = @_;
	if ($opt) {
		foreach $o (keys %{$opt}) {
			if ($this->{optlist}->{$o}) {
				$this->{optlist}->{$o}->{value} = $opt->{$o};
			}
		}
	} else {
		foreach $o (keys %{$this->{optlist}}) {
		    if ($this->{optlist}->{$o}) {
			my($curr_value) = ${"main::${o}"};
			if ($curr_value ne '') {
				$this->{optlist}->{$o}->{value} = $curr_value;
			}
		    }
		}
	}
	return $this;
}
sub get_options {
	my($this, $name) = @_;
	$this->{$name}->{value};
}
sub get_optstring {
	my($this) = @_;
	my(@optstr);
	foreach $o (keys %{$this->{optlist}}) {
		my($optstr);
		if ($this->{optlist}->{$o}->{switch} eq '###') {
			## skip
			next;
		} elsif ($this->{optlist}->{$o}->{value} eq 'ON') {
			$optstr = $this->{optlist}->{$o}->{switch};
		} elsif ($this->{optlist}->{$o}->{value}) {
			$optstr = $this->{optlist}->{$o}->{switch}
				. $this->{optdelim}
				. $this->{optlist}->{$o}->{value};
		}
		push(@optstr, $optstr) if ($optstr);
	}
	join(' ', @optstr);
}
1;
