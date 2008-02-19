#! /usr/bin/perl

while (<>) {
    if (/sIdPropertyList/) {
	last;
    }
}

my $count=0;

while (<>) {
    if (/\{\s*(\d+)\s*,\s*\"(([^\"\\]|\\.)*)\"\s*,\s*SGPropertyNode::(\w+)\s*\}/) {
	last if ($1==0);
	$count++;
	print "propertytype.$count.id=$1\n";
	print "propertytype.$count.name=$2\n";
	print "propertytype.$count.type=$4\n";
    }
}
print "propertytypes=$count\n";
