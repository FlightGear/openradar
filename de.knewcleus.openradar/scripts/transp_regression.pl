#! /usr/bin/perl -w

# Perform a regression towards parameters r,g,b,a for sets of equations
# ri+a*(r-ri)=rmi
# gi+a*(g-gi)=gmi
# bi+a*(b-bi)=bmi

# We will first try to determine a basic value for alpha
# a*(rj-ri)=rmi-ri+rj-rmj
# a*(gj-gi)=gmi-gi+gj-gmj
# a*(bj-bi)=bmi-bi+bj-bmj
#
# This represents a simple set of k=3*i*(i-1)/2 equations of the form
# a*xk=yk
# The error of a single equation is
# ek=(yk-a*xk)^2=yk^2-2*a*xk*yk+a^2*xk^2
#
# Its derivation towards a is
# d/da ek=-2*xk*(yk-a*xk)=2*a*xk^2-2*xk*yk
#
# The best selection of alpha is therefore defined by
# 0=- 1/2 * d/da e=sum_k (a*xk^2-xk*yk)=a*sum_k xk^2-sum_k xk*yk
# <=>
# a=(sum_k xk*yk)/(sum_k xk^2)
#
# Then we can individually determine r,g and b
# xi+a*(b-xi)=yi
# The error of a single equation is
# ei=(a*(b-xi)+xi-yi)^2
# The derivation to b is
# d/db ei=2*(a*(b-xi)+xi-yi)*a=b*2*a^2+2*a*((1-a)*xi-yi)
#
# The best selection of b is therefore defined by
# 0= 1/2 1/a d/db e=sum_i (b*a+(1-a)*xi-yi)
# <=>
# b=1/a (1/n sum_i yi - (1-a)*1/n sum_i xi)
# 


# First read in the data

my @data;

while (<>) {
    s/#.*$//;
    next if /^\s*$/;
    my @input=split(/\s+/);
    push @data,\@input;
}

my $sum_xkyk=0.0, $sum_xkxk=0.0,$sum_ykyk=0.0;

for (my $i=0;$i<=$#data;$i++) {
    my ($ri,$gi,$bi,$rmi,$gmi,$bmi)=@{$data[$i]};
    for (my $j=$i+1;$j<=$#data;$j++) {
	my ($rj,$gj,$bj,$rmj,$gmj,$bmj)=@{$data[$j]};
	my ($xk,$yk);

	# red part
	$xk=$rj-$ri;
	$yk=$rmi-$ri+$rj-$rmj;

	$sum_xkyk+=$xk*$yk;
	$sum_xkxk+=$xk*$xk;
	$sum_ykyk+=$yk*$yk;

	# green part
	$xk=$gj-$gi;
	$yk=$gmi-$gi+$gj-$gmj;

	$sum_xkyk+=$xk*$yk;
	$sum_xkxk+=$xk*$xk;
	$sum_ykyk+=$yk*$yk;

	# blue part
	$xk=$bj-$bi;
	$yk=$bmi-$bi+$bj-$bmj;

	$sum_xkyk+=$xk*$yk;
	$sum_xkxk+=$xk*$xk;
	$sum_ykyk+=$yk*$yk;
    }
}

my $alpha=$sum_xkyk/$sum_xkxk;

# Now determine the best r,g,b

my @mean_values=(0.0,0.0,0.0,0.0,0.0,0.0);

for (my $i=0;$i<=$#data;$i++) {
    for (my $j=0;$j<6;$j++) {
	$mean_values[$j]+=$data[$i]->[$j];
    }
}

for (my $j=0;$j<6;$j++) {
    $mean_values[$j]/=($#data+1);
}

my @rgb;

for (my $j=0;$j<3;$j++) {
    $rgb[$j]=($mean_values[$j+3]-(1.0-$alpha)*$mean_values[$j])/$alpha;
}

# Determine the standard deviation

my @stddevs=(0.0,0.0,0.0);

for (my $i=0;$i<=$#data;$i++) {
    my $line=$data[$i];
    for (my $j=0;$j<3;$j++) {
	my $e=$line->[$j+3]-(1.0-$alpha)*$line->[$j]-$alpha*$rgb[$j];
	$stddevs[$j]+=$e*$e;
    }
}

for (my $j=0;$j<3;$j++) {
    $stddevs[$j]/=$#data+1;
    $stddevs[$j]=sqrt($stddevs[$j]);
}

printf "Best RGBA: %.3ff, %.3ff, %.3ff, %.3ff\n",$rgb[0]/100.0,$rgb[1]/100.0,$rgb[2]/100.0,$alpha;
printf "Best RGBA: %3d, %3d, %3d, %3d\n",$rgb[0]/100.0*255.0,$rgb[1]/100.0*255.0,$rgb[2]/100.0*255.0,$alpha*100.0;
printf "Standard deviations: %.3f, %.3f, %.3f\n",@stddevs;

print "Input:\n";
for (my $i=0;$i<=$#data;$i++) {
    my $line=$data[$i];
    printf "\t%3d, %3d, %3d\t%3d, %3d, %3d\n",map { $_*255.0/100.0 } @$line;
}
print "Mappings:\n";

for (my $i=0;$i<=$#data;$i++) {
    my $line=$data[$i];
    my @realmappings;
    for (my $j=0;$j<3;$j++) {
	my $e=$line->[$j+3]-(1.0-$alpha)*$line->[$j]-$alpha*$rgb[$j];
	$realmappings[$j]=(1.0-$alpha)*$line->[$j]+$alpha*$rgb[$j];
    }
    printf "\t%3d, %3d, %3d\t%3d, %3d, %3d\n",map { $_*255.0/100.0 } (@$line[3,4,5],@realmappings);
}
