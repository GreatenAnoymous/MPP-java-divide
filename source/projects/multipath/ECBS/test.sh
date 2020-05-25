THE_CLASSPATH=".;c:/gurobi811/win64/lib/gurobi.jar;C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/lib/snakeyaml-1.23.jar"
#rm *.class
javac -classpath ${THE_CLASSPATH} ../advanced/*.java ../ILP/*.java  *.java  

cd ..
cd ..
cd ..

java -classpath ${THE_CLASSPATH} projects.multipath.ECBS.EcbsJava

