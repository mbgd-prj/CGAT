#!/usr/bin/perl
package SimpleGraph;
sub new {
	my($class) = @_;
	return bless {}, $class;
}
sub nodes {
	my($this) = @_;
	my(@ret);
	foreach $n (sort keys %{$this->{nodes}}) {
		push(@ret, $this->{nodes}->{$n});
	}
	@ret;
}
sub add_node {
	my($this,$n) = @_;
	$this->{nodes}->{$n} = $n;
}
package SimpleDirectedGraph;
@ISA = qw(SimpleGraph);
sub add {
	my($this, $a, $b, $w) = @_;
#if ($a->{id} == 1618488 || $b->{id} == 1618488){
#print ">>$a->{id},$b->{id}\n";
#}
	$this->{edges}->{$a}->{$b}= $w;
	$this->{edges_rev}->{$b}->{$a}= $w;
	$this->{nodes}->{$a} = $a;
	$this->{nodes}->{$b} = $b;
}
sub out_edges {
	my($this, $node) = @_;
	my(@ret);
	foreach $n (keys %{ $this->{edges}->{$node}}) {
		push(@ret, $this->{nodes}->{$n});
	}
	@ret;
}
sub in_edges {
	my($this, $node) = @_;
	my(@ret);
	foreach $n (keys %{ $this->{edges_rev}->{$node} }) {
		push(@ret, $this->{nodes}->{$n});
	}
	@ret;
}
sub source {
	my($this) = @_;
	my(@list);
	foreach $n ($this->nodes) {
		if ($this->in_edges($n) == 0) {
			push(@list, $n);
		}
	}
	\@list;
}
sub sink {
	my($this) = @_;
	my(@list);
	foreach $n ($this->nodes) {
		if ($this->out_edges($n) == 0) {
			push(@list, $n);
		}
	}
	\@list;
}
sub printGraph {
	my($this, %opt) = @_;
	print "digraph G{\n";
	foreach $n1 (keys %{$this->{edges}}) {
		foreach $n2 (keys %{$this->{edges}->{$n1}}) {
			my($n1out,$n2out);
			if ($opt{'keys'}) {
				foreach my $k (split/:/, $opt{'keys'}) {
					$n1out .= ":" if ($n1out);
					$n2out .= ":" if ($n2out);
					$n1out .= $this->{nodes}->{$n1}->{$k};
					$n2out .= $this->{nodes}->{$n2}->{$k};
				}
			} else {
				$n1out = $n1;
				$n2out = $n2;
			}
			print qq{"$n1out" -> "$n2out";\n};
		}
	}
	print "}\n";
}
sub toposort {
	my($this) = @_;
	my $OutIdx = $this->dfs;
	sort {$OutIdx->{$b}<=>$OutIdx->{$a}} $this->nodes;
}
sub dfs {
	my($this, $n) = @_;
	local(%Visit, %OutIdx, $CountOut);
	foreach $n ($this->nodes) {
		$this->dfs0($n);
	}
	\%OutIdx;
}
sub dfs0 {
	my($this, $n) = @_;
	return if ($Visit{$n});
	$Visit{$n} = 1;
	foreach $n2 (keys %{$this->{edges}->{$n}}) {
		$this->dfs0($n2);
	}
	$OutIdx{$n} = ++$CountOut;
}

package SimpleUndirectedGraph;
@ISA = qw(SimpleGraph);
sub add {
	my($this, $a, $b, $w) = @_;
	$this->{edges}->{$a}->{$b}= $w;
	$this->{edges}->{$b}->{$a}= $w;
	$this->{nodes}->{$a} = $a;
	$this->{nodes}->{$b} = $b;
}
sub in_edges {
	my($this, $node) = @_;
	@{ $this->{edges}->{$node} };
}
package FindComponents;
sub new {
	my($class, $graph) = @_;
	my($this) = {};
	bless $this, $class;
	$this->{graph} = $graph;
	$this;
}
sub find {
	my($this, $graph) = @_;
	if (ref $this eq 'SimpleGraph') {
		# the 1st argument is classname; generate object first
		$this = $this->new($graph);
	}
	$this->{curr_compnum} = 1;
	foreach $a (keys %{$this->{graph}->{edges}}) {
		$this->find_component($a);
		if ($this->{curr_compcnt}) {
			$this->{curr_compnum}++;
			$this->{curr_compcnt} = 0;
		}
	}
	$this;
}
sub find_component {
	my($this, $a) = @_;
	return if ($this->check_node($a));
	foreach $b (keys %{$this->{graph}->{edges}->{$a}}) {
		$this->find_component($b);
	}
}
sub check_node {
	my($this, $node) = @_;
	if ($this->{component}->{$node}) {
		return 1;
	}
	$this->{component}->{$node} = $this->{curr_compnum};
	$this->{curr_compcnt}++;
	return 0;
}
sub get_components_arrayref {
	my($this) = @_;
	my(@array);
	foreach $a (sort keys %{$this->{component}}) {
		push( @{ $array[$this->{component}->{$a} - 1] },
				$this->{graph}->{nodes}->{$a} );
	}
	\@array;
}
sub print_components {
	my($this) = @_;
	foreach $a (sort keys %{$this->{component}}) {
		print "$a $this->{component}->{$a}\n";
	}
}
###########################################################
package main;
if (__FILE__ eq $0) {
	$g = SimpleGraph->new;
	$g->add(1,2);
	$g->add(2,3);
	$g->add(4,5);
	$g->add(6,7);
	$g->add(5,7);
	$comp = FindComponents->new($g)->find;
	$comp->print_components;
}
###########################################################
1;

