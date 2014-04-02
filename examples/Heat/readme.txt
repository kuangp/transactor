The examples.Heat package provides two examples.
One is a sequantial execution example, and the
other is a distributed version.


1. Running examples.Heat.Heat (sequantial version)
java examples.Heat.Heat <iteration> <x> <y>

2. Running examples.Heat.DistributedHeat
 <1> edit the theater config file. The default is cluster.txt
     Add host name with port number to the cluster.txt
 <2> Start theaters at each host specifying in cluster.txt
 <3> Execute examples.Heat.DistributedHeat
     java examples.Heat.DistributedHeat <iteration> <x> <y> <# of actors> <nameserver> <theater file> <output file>
