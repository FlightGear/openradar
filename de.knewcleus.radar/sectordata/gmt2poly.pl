#! /usr/bin/perl -w

my $eoc_seen=0;
my $hole_seen=0;

my $contours=[];
my $contour=[];

sub print_poly {
    my ($contours,)=@_;
    print scalar(@$contours),"\n";
    foreach my $c (@$contours) {
	print scalar(@$c),"\n";
	foreach my $p (@$c) {
	    print join(' ',@$p),"\n";
	}
    }
}

while (<>) {
    last if /FEATURE_DATA/;
}

while (<>) {
    if (/^\#/) {
	# Comment line
	if (/\@H/) {
	    $hole_seen=1;
	    $eoc_seen=1;
	}
    } elsif (/^>?$/) {
	$eoc_seen=1;
    } else {
	if ($eoc_seen) {
	    if (@$contour) {
		push @$contours,$contour;
		$contour=[];
	    }
	    if (!$hole_seen && @$contours) {
		print_poly($contours);
		$contours=[];
	    }
	    $hole_seen=0;
	    $eoc_seen=0;
	}
	# Point line
	my @pts=split(/\s+/);
	push @$contour,\@pts;
    }
}
if (@$contour) {
    push @$contours,$contour;
    $contour=[];
}
if (@$contours) {
    print_poly($contours);
}
print "0\n";

