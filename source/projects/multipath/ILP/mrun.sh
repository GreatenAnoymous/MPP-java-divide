#!/bin/sh

THE_CLASSPATH=".;c:/gurobi811/win64/lib/gurobi.jar"
#PROGRAM_NAME=Main.java

#cd projects/multipath/ILP

javac -classpath ${THE_CLASSPATH} *.java ../advanced/*.java

cd ..
cd ..
cd ..

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 10 20 25 "./data_10_20_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 15 30 25 "./data_15_30_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 20 40 25 "./data_20_40_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 25 50 25 "./data_25_50_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 30 60 25 "./data_30_60_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 35 70 25 "./data_35_70_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 40 80 25 "./data_40_80_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 45 90 25 "./data_45_90_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 50 100 25 "./data_50_100_25h.txt"
done

for ((i=1;i<=300;i++));
do
java -classpath ${THE_CLASSPATH} projects.multipath.ILP.Main 60 120 25 "./data_60_120_25h.txt"
done


if [ $? -eq 0 ]
then
    echo "compile worked!"
fi


#p=Problem.createGridProblem(100,100,0.05,3000);
#yamlProblem.SaveMap(p.graph,"C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/random-100-100-05.map");
#for(int i=1;i<=25;i++){
#	p.graph=Graph.readMap("C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/random-100-100-05.map");
#	p.sg=Graph.getRandomStartGoal(p.graph,3000);
#	yamlProblem.saveScenario(p,"C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/scenarios/random-100-100-05-random-"+i+".scen");
#}
		