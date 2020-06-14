#!/bin/sh

THE_CLASSPATH=".;c:/gurobi811/win64/lib/gurobi.jar;C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/lib/snakeyaml-1.23.jar"
#PROGRAM_NAME=Main.java

mapname="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/random-64-64-00.map"
scename="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/scenarios/random-64-64-00-random-"
result="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/result/64x64/16t/random-64-64-00-random.txt"
#cd projects/multipath/ILP

javac -classpath ${THE_CLASSPATH} *.java ../advanced/*.java ../ECBS/*.java

cd ..
cd ..
cd ..

#################################################################################################################
sname=$scename
numAgents=10
toStore=$result
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main $mapname $sname $numAgents $toStore 0


exit

##############################################################################################
mapname="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/warehouse-10-20-10-2-1.map"
scename="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/scenarios/warehouse-10-20-10-2-1-random-"
result="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/result/warehouse/ecbs-tt/warehouse-10-20-10-2-1-random.txt"
#cd projects/multipath/ILP


sname=$scename

toStore=$result
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main $mapname $sname $numAgents $toStore 1

#################################################################


mapname="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/warehouse-10-20-10-2-1.map"
scename="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/scenarios/warehouse-10-20-10-2-1-random-"
result="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/result/warehouse/ecbs-8t/warehouse-10-20-10-2-1-random.txt"
#cd projects/multipath/ILP



sname=$scename

toStore=$result
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main $mapname $sname $numAgents $toStore 3


################################################

mapname="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/warehouse-10-20-10-2-1.map"
scename="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/scenarios/warehouse-10-20-10-2-1-random-"
result="C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/result/warehouse/ecbs/warehouse-10-20-10-2-1-random.txt"
#cd projects/multipath/ILP



sname=$scename

toStore=$result
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main $mapname $sname $numAgents $toStore 0