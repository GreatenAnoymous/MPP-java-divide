package projects.multipath.ILP;

import java.io.*;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.awt.*;

import projects.multipath.advanced.Graph;
import projects.multipath.advanced.Path;
import projects.multipath.advanced.Problem;
import projects.multipath.advanced.yamlProblem;

import projects.multipath.ECBS.*;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.URL;


public class Main {

	static String basePath = "D:/temp/data/dmrpp/"; 
	
	public static void main(String argv[]) throws Exception{
		String mapname=argv[0];
		String scename=argv[1];
		int	numAgents=Integer.parseInt(argv[2]);
		
		String toStore=argv[3];
		Problem p=new Problem();
		int ksplit=Integer.parseInt(argv[4]);
		int option=1;
		switch (option){
			case 0:
				
				p=Problem.createGridProblem(64,32,0.05,100);
				yamlProblem.SaveMap(p.graph,"C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/random-64-32-05.map");
				//System.exit(0);
				for(int i=1;i<=25;i++){
					p.graph=Graph.readMap("C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/maps/random-64-32-05.map");
					p.sg=Graph.getRandomStartGoalMany(p.graph,800);
					yamlProblem.saveScenario(p,"C:/work/cs560/MPP-java-divide/source/projects/multipath/ILP/maps/scenarios/random-64-32-05-random-"+i+".scen");
				}
				break;
			case 1:
				//p=Problem.createGridProblem(100,100,0.00,2000);
				p.graph=Graph.readMap(mapname);
				p.sg=null;
				int splits=Integer.parseInt(argv[4]);
				//p.graph=Graph.convertGraphGT(p.graph,p.sg[0],p.sg[1]);
				SpaceSplit test=new SpaceSplit(p,2);
				System.out.println("Ready");
				for(int k=180;k<=200;k+=10){
					numAgents=k;
					for(int i=1;i<=25;i++){
						String filename=scename+String.valueOf(i)+".scen";
						p.graph=Graph.readMap(mapname);
						System.out.println("rows="+p.graph.rows+" cols="+p.graph.columns);
						p.sg=Graph.readScenario(filename,numAgents);
						//PathPlanner.printStartAndGoals(p.graph, p.sg[0], p.sg[1]);
						//Solve.checkValid(p.graph, p.sg[0], p.sg[1]);
					//	test.sg=p.sg;
						long t1=System.currentTimeMillis();
					
					//	test.parallelSort(test.subgraphs, test.sg);
						p.graph=Graph.convertGraphGT(p.graph,p.sg[0],p.sg[1]);
						//long[] re=Solve.solveProblemSuboptimal(p, false,false, 0, 300, 3,false);
					//	long[] re2=Solve.solveProblemSuboptimal(p, false,false, 0, 300, 1,false);
						//long[] re=Ksplit.solveProblemSuboptimal(p, false,false, 0, 300, 3,false);
						long[] re=Solve.solveProblemArbitrarySplit(p, false,false, 0, 300,false,new double[]{1,1,1});
					//	long[] re=(new Ecbs()).ECBS_solve_arbitrary(p, new double[]{1,1,1},false);
						//long[] re=Solve.solveProblem(p, false, 300);
					//	long[] re=(new Ecbs()).ECBS_solve_suboptimalTT(p, 0,false);
					//	long[] re=Solve.solveProblemTTSplit(p, 0, 300, 0,false);
						////long[] re=Ecbs.ECBS_solve(p);	
					//	long[] re=(new Ecbs()).ECBS_solve_suboptimal(p, ksplit);	
						long t2=System.currentTimeMillis();
						//		
						//long[] re=new long[]{test.subMakeSpan,t2-t1,0,test.makespanLb};
						//re[3]=PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1])-1;
						
						if(re!=null){
							re[1]=(t2-t1);
							//re[3]=PathFinder.getTotalTimeLowerBound(p.graph, p.sg[0], p.sg[1]);
							re[3]=PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1])-1;
							System.out.printf("runtime=%f,%d %d\n",(t2-t1)/1000.,re[0],re[3]);
							
						}
						//long[] re=new long[]{test.subMakeSpan,t2-t1,0,test.makespanLb}						
						//System.exit(0);
						yamlProblem.saveOutPut(p,re,toStore);
						
					
					}
				}
				break;
			case 2:
				p=Problem.createGridProblem(64,64,0.00,500);
			//	p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], false);
				//p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], false);
				//p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], false);
				Solve.solveProblemSuboptimal(p, false,false, 0, 12000, 3,false);
				//yamlProblem.saveProblem(p,"./test66.yaml");
				//p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], false);
				//p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], false);
				//p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], false);
				//Ecbs.ECBS_solve(p);
				//Solve.solveProblem(p, false, 100);
				//(new Ecbs()).ECBS_solve_suboptimal(p,3);
				break;
			case 3:	
				p.graph=Graph.readMap(mapname);
				
				//test.init(rows, cols, numAgents);
				SpaceSplitTwo testx=new SpaceSplitTwo();
				//Graph tmp=Graph.create2DGridGraph(rows, cols, true);
				testx.increment=5;
				testx.w=3;
				testx.init(p.graph.rows,p.graph.columns,numAgents,null);
			
				System.out.println(p.graph.rows+" "+p.graph.columns);
				
			//	Graph tmp=Graph.concatenate(testx.subgraph[0],testx.subgraph[1]);
				HashSet<Integer> bufferZone=testx.get_bufferZone();

				
				for(int k=50;k<=100;k+=10){
					numAgents=k;
					for(int i=1;i<=10;i++){
						String filename=scename+String.valueOf(i)+".scen";
						p.sg=Graph.readScenario(filename,numAgents);
					
					
						//test.sg=p.sg;
						testx.set_sg(p.sg);
						testx.get_lower_bound();
						long mklb=testx.makespanLB;
						long t1=System.currentTimeMillis();
						
						//test.parallelSort(test.subgraphs, test.sg);
						long[] re=testx.multiThreadSolver();
						long t2=System.currentTimeMillis();
						re[3]=PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
						re[1]=(t2-t1);
						//long[] re=new long[]{test.subMakeSpan,t2-t1,0,test.makespanLb};
						System.out.printf("runtime=%f,%d %d\n",(t2-t1)/1000.,re[0],re[3]);
						System.exit(0);
						yamlProblem.saveOutPut(p,re,toStore);					
					
					}
				}
				break;
			case 4:
				p=Problem.createGridProblem(32,32,0.00,200);
				
				long []result=Solve.solveProblemSuboptimal(p, false,false, 0, 300, 3,false);
				System.out.println("Makespan="+result[0]+",Lowerbound="+result[3]);
				result=Ksplit.solveProblemSuboptimal(p, false,false, 0, 12000, 8,false);
				System.out.println("Makespan="+result[0]+",Lowerbound="+result[3]);
			}


		}

}
		


		
		
