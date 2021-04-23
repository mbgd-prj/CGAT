package Tools;
#use Tools::Blast;
#use Tools::Fasta;
#use Tools::Mummer;
#use Tools::BlastZ;
#use Tools::Cgat;

sub getInstance {
	my($thisclass, $classname, @args) = @_;
	die "Tools::getInstance: classname is not defined\n" if (! $classname);
	if ($classname !~ /^Tools::/) {
		$classname = "Tools::${classname}";
	}
	eval "use $classname;";
	
	"${classname}"->new(@args);
}
sub getParserInstance {
	my($this) = @_;
	$this->{parser};
}
sub defaultProgname {
	my($classname) = @_;
	$classname = "Tools::$classname";
	eval "use $classname;";
	$classname->defaultProgname ?
		$classname->defaultProgname : 'homology';
}
1;
