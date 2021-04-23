#!/usr/bin/perl

#
# Acccessing the MBGD server to get data
#
package MBGD_access;
use HTTP::Request::Common;
use LWP::UserAgent;

$URL = "http://mbgd.genome.ad.jp/htbin/getData";
$ua = LWP::UserAgent->new;

sub getData {
	my(%option) = @_;
	my(@args);
	foreach my $key ('table','species','id','key','count','keyfields',
				'fields','order','limit','no_header'){
		if ($option{$key}) {
			push(@args, $key => $option{$key});
		}
	}
	$resp = $ua->request(POST $URL,  \@args);
	my $string =  $resp->content;
	if ($option{count}) {
		chomp $string;
		return $string;
	}
	my(@Output);
	my($ln);
	foreach my $line (split(/\n/, $string)) {
		@fields = split("\t", $line);
		if ($ln == 0) {
			@fnames = @fields;
		} else {
			my($data) = {};
			for ($i= 0; $i < @fnames; $i++) {
				$data->{$fnames[$i]} = $fields[$i];
			} 
			push(@Output, $data);
		}
		$ln++;
	}
	@Output;
}
if($0 eq __FILE__) {
	$c = &getData(table=>'genome', count=>1);
	print "count: $c\n";
	@g = &getData(table=>'gene', species=>'eco');
	foreach $d (@g){
		print ">$d->{sp},$d->{name},$d->{from1},$d->{to1}\n";
	} 
}

1;
