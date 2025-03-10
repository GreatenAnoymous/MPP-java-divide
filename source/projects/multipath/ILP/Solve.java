package projects.multipath.ILP;

import java.io.*;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.openmbean.OpenDataException;

import projects.multipath.advanced.Vertex;
import projects.multipath.advanced.Graph;
import projects.multipath.advanced.Path;
import projects.multipath.advanced.Problem;

class ResultGT{
	public long[] output=null;
	public int [][] paths=null;
}

public class Solve{
	static String basePath = "D:/temp/data/dmrpp/"; 

	public static boolean check_states_valid(int [] state){
		HashSet<Integer> used=new HashSet<Integer>();
		for(int i=0;i<state.length;i++){
			if(used.contains(state[i])==false){
				used.add(state[i]);
			}
			else{
				System.out.printf("States invalid,%d is repeated\n",state[i]);
				for(int k=0;k<state.length;k++){
					System.out.printf("%d ",state[k]);
				}
				System.out.println();
				throw new OutOfMemoryError();
			}
		}
		return true;
	}

	public static boolean checkValid(Graph g, int[]start, int[] goal){
		boolean valid=true;
		for(int i=0;i<start.length;i++){
			if(g.idVertexMap.containsKey(start[i])==false){
				throw new Error();
				//return false;
			}
		}
		for(int i=0;i<goal.length;i++){
			if(g.idVertexMap.containsKey(goal[i])==false){
				throw new Error();
				//return false;
			}
		}
		return true;
	}


    public static long[] solveProblemSuboptimal(Problem p, boolean solveLP, boolean setOptimal, int extraSteps,
			double timeLimit, int splitLevel, boolean noCycle){


		System.out.printf("Number of agents=%d,Grids size=%d\n",p.sg[0].length,p.graph.verticeSet.size());
	//	PathPlanner.printStartAndGoals(p.graph, p.sg[0], p.sg[1],false);
		// Solve the problem
		//check_states_valid(p.sg[1]);
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
		if(makespanLb==-1) return null;
		int[][] paths = null;
		// int[][] paths = ms.planPathsAdvancedSplitBySize(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, 3);
		// int[][] paths = ms.planPathsAdvancedSplitBySizeNoCycle(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, 4, true);
		// int[][] paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, 2);
		if(noCycle){
			paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, splitLevel);
		}
		else{
			
			paths = ms.multiThreadedSplitPlanning(p.graph, p.sg[0], p.sg[1], timeLimit, splitLevel);
		}
		//PathPlanner.printPaths(p.graph, paths);

		time = System.currentTimeMillis() - time;
		if(paths != null && PathPlanner.isPathSetValid(paths, p.graph, p.sg[0], p.sg[1])){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
			System.out.printf("Problem, solved in %f s\n",time/1000.);
			return new long[]{paths[0].length - 1, time, 0, makespanLb - 1};
		}
		else{
			System.out.printf("Paths is null,%b\n",paths==null);
		
			return null;
		}
	}

	public static long[] solveProblemArbitrarySplitTT(Problem p, boolean solveLP, boolean setOptimal, int extraSteps,double timeLimit, boolean noCycle,double [] ratio){
		PathPlanner ms = new PathPlanner();
        long time = System.currentTimeMillis();
        // Solve the makespan problem using a 4 split
        int[][] paths = ms.multiThreadedSplitPlanningArbitraryTT(p.graph, p.sg[0], p.sg[1], timeLimit, ratio);
        //int[][] paths = ms.multiThreadedSplitPlanningTT(p.graph, p.sg[0], p.sg[1], gap, timeLimit, splits,random);
        time = System.currentTimeMillis() - time;
        int ttLB = PathFinder.getTotalTimeLowerBound(p.graph, p.sg[0], p.sg[1]);
        
        if(paths != null){
            int cycles = PathPlanner.hasCycles(paths);
            if(cycles > 0){
                System.out.println("\nThere are " + cycles + " cycles in the result paths");
            }
        //  PathPlanner.printPaths(p.graph, paths);
            return new long[]{PathFinder.getTotalTime(paths), time, PathFinder.getTotalDistance(paths), ttLB};
        }
        else{
            return null;
        }
	}

	public static long[] solveProblemArbitrarySplit(Problem p, boolean solveLP, boolean setOptimal, int extraSteps,double timeLimit, boolean noCycle,double [] ratio){
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);

		int[][] paths = null;

		if(noCycle){
			paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit,ratio.length);
		}
		else{
			
			paths = ms.multiThreadedSplitPlanningArbitrary(p.graph, p.sg[0], p.sg[1], timeLimit, ratio);
		}
		//PathPlanner.printPaths(p.graph, paths);

		time = System.currentTimeMillis() - time;
		if(paths != null && PathPlanner.isPathSetValid(paths, p.graph, p.sg[0], p.sg[1])){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
			System.out.printf("Problem, solved in %f s\n",time/1000.);
			return new long[]{paths[0].length - 1, time, 0, makespanLb - 1};
		}
		else{
			System.out.printf("Paths is null,%b\n",paths==null);

			return null;
		}
	}


	public static long[] solveProblemTTSplit(Problem p, double gap, double timeLimit, int splits,boolean random){
        PathPlanner ms = new PathPlanner();
        long time = System.currentTimeMillis();
        // Solve the makespan problem using a 4 split
        int[][] paths = ms.planPathsAdvancedSplitTT(p.graph, p.sg[0], p.sg[1], gap, timeLimit, splits);
        //int[][] paths = ms.multiThreadedSplitPlanningTT(p.graph, p.sg[0], p.sg[1], gap, timeLimit, splits,random);
        time = System.currentTimeMillis() - time;
        int ttLB = PathFinder.getTotalTimeLowerBound(p.graph, p.sg[0], p.sg[1]);
        
        if(paths != null){
            int cycles = PathPlanner.hasCycles(paths);
            if(cycles > 0){
                System.out.println("\nThere are " + cycles + " cycles in the result paths");
            }
        //  PathPlanner.printPaths(p.graph, paths);
            return new long[]{PathFinder.getTotalTime(paths), time, PathFinder.getTotalDistance(paths), ttLB};
        }
        else{
            return null;
        }
    }

	

	

	public static ResultGT solveProblemSuboptimalGT(Problem p, boolean solveLP, boolean setOptimal, int extraSteps,
			double timeLimit, int splitLevel, boolean noCycle,int size){
		// Solve the problem
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
		System.out.printf("Trying T=%d\n",makespanLb);
		ResultGT result=new ResultGT();
		int[][] paths = null;
		// int[][] paths = ms.planPathsAdvancedSplitBySize(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, 3);
		// int[][] paths = ms.planPathsAdvancedSplitBySizeNoCycle(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, 4, true);
		// int[][] paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, 2);
		if(noCycle){
			paths = ms.planPathsAdvancedSplitNoCycle(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, splitLevel, true);
		}
		else{
			
			paths = ms.multiThreadedSplitPlanning(p.graph, p.sg[0], p.sg[1], timeLimit, splitLevel);
		}
		//PathPlanner.printPaths(p.graph, paths);
		
		time = System.currentTimeMillis() - time;
		
		result.output=new long[]{paths[0].length - 1, time, 0, makespanLb - 1};
		if(paths != null && PathPlanner.isPathSetValid(paths, p.graph, p.sg[0], p.sg[1])){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}

			for(int i=0;i<paths.length;i++){
				for(int j=0;j<paths[0].length;j++){
					Vertex v=p.graph.idVertexMap.get(paths[i][j]);
					paths[i][j]=v.getIX()*(size+1)+v.getIY();
				}
			}

			result.paths=paths;
			System.out.printf("Problem, solved in %f s,T=%d\n",time/1000.,paths[0].length-1);
			return result;
		}
		else{
			System.out.println("CAO");
			System.exit(0);
			return null;
		}
	}
	
	/**
	 * Solve a problem
	 * @param p
	 * @param timeLimit
	 * @return
	 */
	public static long[] solveProblem(Problem p, boolean solveLP, double timeLimit){
		// Solve the problem
		PathPlanner ms = new PathPlanner();
		System.out.printf("Number of agents=%d,Grids size=%d\n",p.sg[0].length,p.graph.verticeSet.size());
		long time = System.currentTimeMillis();
		int[][] paths = ms.planPathsAdvanced(p.graph, p.sg[0], p.sg[1], solveLP, 0, true, timeLimit);
		time = System.currentTimeMillis() - time;
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
		System.out.printf("Problem, solved in %f s\n",time/1000.);
		if(paths != null){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
		//	PathPlanner.printPaths(p.graph, paths);
			long []result=new long[]{paths[0].length - 1, time, PathFinder.getTotalDistance(paths), makespanLb - 1};
			System.out.println("Done!");
			return result;
		}
		else{
			return null;
		}
	}


	

	
	
	public static ResultGT solveProblemGT(Problem p, boolean solveLP, double timeLimit,int size){
		// Solve the problem
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		ResultGT result=new ResultGT();
	
		int [][]paths = ms.planPathsAdvanced(p.graph, p.sg[0], p.sg[1], solveLP, 0, true, timeLimit);
		time = System.currentTimeMillis() - time;
		System.out.println("***************************");
		System.out.printf("Number of agents=%d,Grids size=%d\n",p.sg[0].length,p.graph.verticeSet.size());
		System.out.printf("Problem solved in %f s\n",time/1000.);
		for(int i=0;i<paths.length;i++){
			for(int j=0;j<paths[0].length;j++){
				Vertex v=p.graph.idVertexMap.get(paths[i][j]);
				paths[i][j]=v.getIX()*(size+1)+v.getIY();
			}
		}
		result.paths=paths;
/*	
	
*/
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
		if(paths != null){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
		//	PathPlanner.printPaths(p.graph, paths);
			result.output=new long[]{paths[0].length - 1, time, PathFinder.getTotalDistance(paths), makespanLb - 1};
		}
		else{
			result.output=null;
		}
		return result;
	}

	public static ResultGT solveProblemGT(Problem p, boolean solveLP, double timeLimit,int size,int k){
		// Solve the problem
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		ResultGT result=new ResultGT();
	
		int [][]paths = ms.planPathsAdvanced(p.graph, p.sg[0], p.sg[1], solveLP, 0, true, timeLimit);
		time = System.currentTimeMillis() - time;
		System.out.println("***************************");
		System.out.printf("%d th problem:\n",k);
		System.out.printf("Number of agents=%d,Grids size=%d,T=%d\n,",p.sg[0].length,p.graph.verticeSet.size(),paths[0].length-1);
		System.out.printf("Problem solved in %f s\n",time/1000.);
		for(int i=0;i<paths.length;i++){
			for(int j=0;j<paths[0].length;j++){
				Vertex v=p.graph.idVertexMap.get(paths[i][j]);
				paths[i][j]=v.getIX()*(size+1)+v.getIY();
			}
		}
		result.paths=paths;
/*	
		try{
			File filename=new File("path.txt");
			PrintStream ps = null ; 
			ps = new PrintStream(new FileOutputStream(filename)) ;
			for(int i=0;i<paths.length;i++){
				for(int j=0;j<paths[0].length;j++){
					ps.printf("%d ",paths[i][j]);
				}
				ps.printf("\n");
			}
			ps.close();
		}
		catch(Exception e){

		}
*/
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
		if(paths != null){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
		//	PathPlanner.printPaths(p.graph, paths);
			result.output=new long[]{paths[0].length - 1, time, PathFinder.getTotalDistance(paths), makespanLb - 1};
		}
		else{
			result.output=null;
		}
		return result;
	}

	
	
	/**
	 * Solve a problem
	 * @param p
	 * @param timeLimit
	 * @return
	 */
	
	/**
	 * Optimize for total time. 
	 * @param p
	 * @param timeLimit
	 * @return
	 */
	public static long[] solveProblemTotalTime(Problem p, double gap, double timeLimit){
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		int opt[] = new int[1];
		// Solve the makespan problem using a 4 split
		int[][] paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], false, -1, 2);
		timeLimit -= (System.currentTimeMillis() - time)/1000.;
		if(timeLimit <= 0) return null;
		int timeSteps = paths[0].length - 1;
		paths = ms.planPathsAdvancedTT(p.graph, p.sg[0], p.sg[1], opt, gap, timeSteps, timeLimit);
		time = System.currentTimeMillis() - time;
		int ttLB = PathFinder.getTotalTimeLowerBound(p.graph, p.sg[0], p.sg[1]);
		
		if(paths != null){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
			return new long[]{opt[0], time, PathFinder.getTotalDistance(paths), ttLB};
		}
		else{
			return null;
		}
	}
	
	/**
	 * Optimize for total time. 
	 * @param p
	 * @param timeLimit
	 * @return
	 */
	public static long[] solveProblemDistance(Problem p, double gap, double timeLimit){
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		// Solve the makespan problem using a 4 split
		int[][] paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], false, -1, 2);
		timeLimit -= (System.currentTimeMillis() - time)/1000.;
		if(timeLimit <= 0) return null;
		int timeSteps = paths[0].length - 1;
		paths = ms.planPathsAdvancedTD(p.graph, p.sg[0], p.sg[1], gap, timeSteps, timeLimit);
		time = System.currentTimeMillis() - time;
		int ttLB = PathFinder.getTotalTimeLowerBound(p.graph, p.sg[0], p.sg[1]);
		
		if(paths != null){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
			return new long[]{PathFinder.getTotalDistance(paths), time, PathFinder.getTotalDistance(paths), ttLB};
		}
		else{
			return null;
		}
	}
	
	public static long[] solveProblemDistanceSplit(Problem p, double gap, double timeLimit, int splits){
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		// Solve the makespan problem using a 4 split
		int[][] paths = ms.planPathsAdvancedSplitDist(p.graph, p.sg[0], p.sg[1], gap, timeLimit, splits);
		time = System.currentTimeMillis() - time;
		int ttLB = PathFinder.getTotalTimeLowerBound(p.graph, p.sg[0], p.sg[1]);
		
		if(paths != null){
			int cycles = PathPlanner.hasCycles(paths);
			if(cycles > 0){
				System.out.println("\nThere are " + cycles + " cycles in the result paths");
			}
			PathPlanner.printPaths(p.graph, paths);
			return new long[]{PathFinder.getTotalDistance(paths), time, PathFinder.getTotalDistance(paths), ttLB};
		}
		else{
			return null;
		}
	}
	
	/**
	 * Solve a n^2 puzzle
	 * @param p
	 * @param timeLimit
	 * @return
	 */
	public static long[] solvePuzzleProblem(int instance, Problem p, double timeLimit){
		// Solve the problem
		long time = System.currentTimeMillis();
		int paths[][] = null;
		paths = PuzzleSolver.solve(p, timeLimit);
		time = System.currentTimeMillis() - time;
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
		if(paths != null){
			writePuzzleResult(instance, time, p.graph, paths);
			PathPlanner.printPaths(p.graph, paths);
			return new long[]{paths[0].length - 1, time, PathFinder.getTotalDistance(paths), makespanLb - 1};
		}
		return null;
	}
	
	/**
	 * Solve a problem without optimality guarantee, but fast
	 * @param p
	 * @return
	 */
	public static long[] solveProblemWithHeuristic(Problem p){
		PathFinder.bHeuristic = true;
		// Solve the problem
		PathPlanner ms = new PathPlanner();
		long time = System.currentTimeMillis();
		PathPlanner.printStartAndGoals(p.graph, p.sg[0], p.sg[1]);
		int[][] paths = ms.planHeuristicPaths(p.graph, p.sg[0], p.sg[1]);
		time = System.currentTimeMillis() - time;
		if(paths != null){
			int goalsReached = PathPlanner.numberOfGoalsReached(paths, p.sg[1]);
			// PathPlanner.printPaths(p.graph, paths);
			if(!PathPlanner.isPathSetValid(paths, p.graph, p.sg[0], p.sg[1])){
				System.out.println("Path set is INVALID.");
			}
			
			// Find out path lengths
			long length = 0, leastPossibleLength = 0;
			PathFinder.bHeuristic = false;
			Path[] hp = PathFinder.findShortestPaths(p.graph, p.sg[0], p.sg[1]);
			PathFinder.bHeuristic = true;
			for(int a = 0; a < p.sg[0].length; a++){
				leastPossibleLength += (hp[a].vertexList.size() - 1);
				for(int i = 0; i < paths[a].length - 1; i ++){
					if(paths[a][i] != paths[a][i+1]){
						length++;
					}
				}
			}
			
			return new long[]{paths[0].length, time, goalsReached, 
					(goalsReached==p.sg[0].length)?length-1:0, (goalsReached==p.sg[0].length)?leastPossibleLength-1:0};
		}
		else{
			return null;
		}
	}
	
	static String currentTest = null;
	static double subTotal = 0;
	static int pathLengths = 0;
	static int solvedInstances = 0;
	static double totalTime = 0;
	static int successCount = 0;
	static long goalsReached = 0;
	static long pathLength = 0;
	static long expPathLength = 0;
	static SortedSet<Double> finishingTimeSet = new TreeSet<Double>();
	static int makeSpanTotal = 0;
	
	public static void resetStats(String test){
		currentTest = test;
		subTotal = 0;
		pathLengths = 0;
		solvedInstances = 0;
		successCount = 0;
		makeSpanTotal = 0;
		finishingTimeSet = new TreeSet<Double>(); 
	}
	
	public static void resetHeuristicStats(String test){
		currentTest = test;
		goalsReached = 0;
		subTotal = 0;
		pathLengths = 0;
		solvedInstances = 0;
		successCount = 0;
		pathLength = 0;
		expPathLength = 0;
		finishingTimeSet = new TreeSet<Double>(); 
	}
	
	public static void processStats(long[] stat){
		if(stat != null){
			int len = (int)stat[0];
			long time = stat[1];
			successCount += 1;
			pathLengths += len;
			finishingTimeSet.add(time/1000.0);
			System.out.println("Time spent = " + time/1000.0 + " seconds.");
			subTotal += time/1000.0;
			totalTime += time/1000.0;
			makeSpanTotal += stat[3];
			System.out.println("Total time spent: " + totalTime + " seconds.\n");
		}
	}
	
	public static void processHeuristicStats(long[] stat, int n){
		if(stat != null){
			int len = (int)stat[0];
			long time = stat[1];
			pathLengths += len;
			finishingTimeSet.add(time/1000.0);
			goalsReached += stat[2];
			System.out.println("Goals reached = " + goalsReached);
			if(stat[2] == n){successCount += 1;}
			System.out.println("Time spent = " + time/1000.0 + " seconds.");
			subTotal += time/1000.0;
			totalTime += time/1000.0;
			System.out.println("Total time spent: " + totalTime + " seconds.\n");
			pathLength += stat[3];
			expPathLength += stat[4];
		}
	}
	
	public static void writeStats(){
		try {
			Double[] ft = finishingTimeSet.toArray(new Double[0]);
			String outString = currentTest + "Total success: " + successCount + ", ave time: " + subTotal/successCount + ", median:" + ft[ft.length/2] + ", min: " + ft[0] + ", max: " + ft[ft.length - 1] + ", ave solution: " + (pathLengths*1./successCount) + ", min possible: " + (makeSpanTotal*1./successCount) +"\n";
			writeString(outString);
//			FileWriter fw = new FileWriter(basePath + "\\n2puzzle-result.txt", true);
//			fw.append(outString);
//			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeHeuristicStats(){
		try {
			Double[] ft = finishingTimeSet.toArray(new Double[0]);
			String outString = currentTest + "Total success: " + successCount + ", goals reached: " + goalsReached + ", ave time: " + subTotal/successCount + ", median:" + ft[ft.length/2] + ", min: " + ft[0] + ", max: " + ft[ft.length - 1] + ", steps: " + pathLength + ", exp: " + expPathLength + ", ratio: " + pathLength*1./expPathLength + "\n";
			FileWriter fw = new FileWriter(basePath + "\\n2puzzle-result.txt", true);
			fw.append(outString);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writePuzzleStats(int instance, double time){
		try {
			String outString = "Instance: " + instance + ", time: " + time + "\n";
			FileWriter fw = new FileWriter(basePath + "\\n2puzzle-result.txt", true);
			fw.append(outString);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeString(String str){
		try {
			FileWriter fw = new FileWriter(basePath + "\\n2puzzle-result.txt", true);
			fw.append(str);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writePuzzleResult(int instance, double time, Graph g, int[][] paths){
		try {
			String outString = "Instance: " + (instance  + 1) + ", time: " + time/1000 + "\n";
			FileWriter fw = new FileWriter(basePath + "\\25puzzle.txt", true);
			fw.append(outString);
			fw.append(PathPlanner.printPathToString(g, paths));
			fw.append("\n");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runN2PuzzlePerformanceMetric(){
		try{
			totalTime = 0;
			for(int n = 3; n <= 5; n ++){
				resetStats("" + n*n + "-puzzle, 100 runs. ");
				for(int i = 0; i < 100 ; i ++){
					Problem p = Problem.readFromFile(basePath + "\\n2puzzle\\" + (n*n) + "-puzzle-" + (1001 + i) + ".dat");
					System.out.println("Working on " +  + (n*n) + "-puzzle-" + (1001 + i) + ".dat");
					long stat[] = solvePuzzleProblem(i, p, -1);
					if(stat != null){
						processStats(stat);
						writePuzzleStats(i + 1, (stat[1]/1000.));
					}
				}
				writeStats();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void runCrowdedPerformanceMetric8x8(int from, int to, double timeLimit, int splitLevels, boolean noCycle){
		totalTime = 0;
		for(int a = from; a <= to; a = a + 10){
			boolean success = true;
			// if(a == 60){a = 55;}
			resetStats("8x8, " + a + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\8x8-grid\\" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "8x8 grid performance testing," + (noCycle?" no cycles, ":"") + " problem: " + a + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, splitLevels, noCycle);
				processStats(stat);
				if(stat == null){success = false; break;}
			}
			if(success==false)break;
			writeStats();
		}
		writeString("\n");
	}

	public static void runCrowdedPerformanceMetric16x16(int from, int to, double timeLimit, int splitLevels, boolean noCycle){
		totalTime = 0;
		for(int a = from; a <= to; a = a + 10){
			boolean success = true;
			resetStats("16x16, " + a + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\16x16-grid\\" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "16x16 grid performance testing, " + (noCycle?" no cycles, ":"") + "problem: " + a + "-agts-" + (1001 + i) + ".dat");
				// long stat[] = solveProblem(p, false, 7200);
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, splitLevels, noCycle);
				processStats(stat);
				if(stat == null){success = false; break;}
			}
			if(success==false)break;
			writeStats();
		}
		writeString("\n");
	}
	
	public static void run24x18PerformanceMetric(){
		writeString("24 x 18 grid, exact\n");
		totalTime = 0;
		for(int obs = 0; obs <= 30; obs=obs+5){
			boolean stopCurrentRun = false;
			for(int n = 10; n <= 120; n=n+10){
				resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
				for(int i = 0; i < 10; i ++){
					System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					long stat[] = solveProblemSuboptimal(p, false, false, 0, 600, 0, false);
					processStats(stat);
					if(stat == null){
						stopCurrentRun  = true;
						break;
					}
				}
				writeStats();
				if(stopCurrentRun == true){
					break;
				}
			}
			writeString("\n");
		}

		for(int k = 0; k <=3; k ++){
			writeString("24 x 18 grid, "+ (1 << (k+1)) + "-way split\n");
			totalTime = 0;
			for(int obs = 0; obs <= 30; obs=obs+5){
				boolean stopCurrentRun = false;
				for(int n = 20; n <= 300; n=n+20){
					resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
					for(int i = 0; i < 10; i ++){
						System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
						Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
						long stat[] = solveProblemSuboptimal(p, false, false, 0, 600, k+1, false);
						processStats(stat);
						if(stat == null){
							stopCurrentRun  = true;
							break;
						}
					}
					writeStats();
					if(stopCurrentRun == true){
						break;
					}
				}
			}
		}
	}
	
	public static void run32x32PerformanceMetric(){
		writeString("32 x 32 grid, 20% obstacles, exact\n");
		totalTime = 0;
		for(int a = 10; a <= 90; a = a + 10){
			resetStats("32x32, 20% obstacles, " + a + " agents, 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblem(p, false, 1800);
				processStats(stat);
			}
			writeStats();
		}

		double timeLimit = 1800; boolean noCycle = true;
		
		writeString("32 x 32 grid, 20% obstacles, 2-way split\n");
		totalTime = 0;
		for(int n = 20; n <= 120; n = n + 20){
			resetStats("32x32, 20% obstacles, "+ n + " agents, 10 runs. ");
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, 1, noCycle);
				processStats(stat);
			}
			writeStats();
		}

		writeString("32 x 32 grid, 20% obstacles, 4-way split\n");
		totalTime = 0;
		for(int n = 20; n <= 160; n = n + 20){
			resetStats("32x32, 20% obstacles, "+ n + " agents, 10 runs. ");
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, 2, noCycle);
				processStats(stat);
			}
			writeStats();
		}
		
		writeString("32 x 32 grid, 20% obstacles, 8-way split\n");
		totalTime = 0;
		for(int n = 20; n <= 200; n = n + 20){
			resetStats("32x32, 20% obstacles, "+ n + " agents, 10 runs. ");
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, 3, noCycle);
				processStats(stat);
			}
			writeStats();
		}

		writeString("32 x 32 grid, 20% obstacles, 16-way split\n");
		totalTime = 0;
		for(int n = 20; n <= 200; n = n + 20){
			resetStats("32x32, 20% obstacles, "+ n + " agents, 10 runs. ");
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, 4, noCycle);
				processStats(stat);
			}
			writeStats();
		}
	}
	
	public static void run32x32PerformanceMetric8ConnectedAll(){
		writeString("32 x 32 grid, 8 connected, 20% obstacles, exact\n");
		totalTime = 0;
		for(int a = 20; a <= 100; a = a + 20){
			resetStats("32x32, 20% obstacles, " + a + " agents, 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid-8c\\20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + a + "-" + (1001 + i) + ".dat");
				// long stat[] = solveProblem(p, false, 3600);
				long stat[] = solveProblemSuboptimal(p, false, false, 0, 1800, 0, false);
				processStats(stat);
			}
			writeStats();
		}

		writeString("32 x 32 grid, 8 connected, 20% obstacles, 2-way split\n");
		totalTime = 0;
		for(int a = 20; a <= 200; a = a + 20){
			resetStats("32x32, 20% obstacles, " + a + " agents, 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid-8c\\20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + a + "-" + (1001 + i) + ".dat");
				// long stat[] = solveProblem(p, false, 3600);
				long stat[] = solveProblemSuboptimal(p, false, false, 0, 1800, 1, false);
				processStats(stat);
			}
			writeStats();
		}
		
		writeString("32 x 32 grid, 8 connected, 20% obstacles, 4-way split\n");
		totalTime = 0;
		for(int a = 20; a <= 300; a = a + 20){
			resetStats("32x32, 20% obstacles, " + a + " agents, 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid-8c\\20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + a + "-" + (1001 + i) + ".dat");
				// long stat[] = solveProblem(p, false, 3600);
				long stat[] = solveProblemSuboptimal(p, false, false, 0, 1800, 2, false);
				processStats(stat);
			}
			writeStats();
		}
		
		writeString("32 x 32 grid, 8 connected, 20% obstacles, 8-way split\n");
		totalTime = 0;
		for(int a = 20; a <= 400; a = a + 20){
			resetStats("32x32, 20% obstacles, " + a + " agents, 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid-8c\\20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + a + "-" + (1001 + i) + ".dat");
				// long stat[] = solveProblem(p, false, 3600);
				long stat[] = solveProblemSuboptimal(p, false, false, 0, 1800, 3, false);
				processStats(stat);
			}
			writeStats();
		}
		
	}

	public static void runCrowdedPerformanceMetricCrowded(){
		writeString("8 x 8 grid, exact\n");
		runCrowdedPerformanceMetric8x8(10, 50, 1800, 0, false);
		for(int k = 1; k <= 3; k ++){
			writeString("8 x 8 grid, " + (1 << k) + "-way split\n");
			runCrowdedPerformanceMetric8x8(10, 60, 1800, k, false);
		}

		for(int k = 1; k <= 3; k ++){
			writeString("8 x 8 grid, " + (1 << k) + "-way split, no cycle\n");
			runCrowdedPerformanceMetric8x8(10, 50, 1800, k, true);
		}

		writeString("16 x 16 grid, exact\n");
		runCrowdedPerformanceMetric16x16(10, 100, 1800, 0, false);
		for(int k = 1; k <= 3; k ++){
			writeString("16 x 16 grid, " + (1 << k) + "-way split\n");
			runCrowdedPerformanceMetric16x16(10, 250, 1800, k, false);
		}

		for(int k = 1; k <= 3; k ++){
			writeString("16 x 16 grid, " + (1 << k) + "-way split, no cycle\n");
			runCrowdedPerformanceMetric16x16(10, 250, 1800, k, true);
		}
	}
	

	/**
	 * Total time algorithm evaluation
	 */
	public static void run24x18PerformanceMetricTotalTime(){
		writeString("24 x 18 grid, total time solution\n");
		totalTime = 0;
		for(int obs = 0; obs <= 30; obs=obs+5){
			boolean stopCurrentRun = false;
			for(int n = 10; n <= 160; n=n+10){
				resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
				for(int i = 0; i < 10; i ++){
					System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					long stat[] = solveProblemTotalTime(p, n*0.002 + obs*0.002 + (n > 50 ? n*0.001 : 0) + (n > 100 ? n*0.0005 : 0), 600);
					processStats(stat);
					if(stat == null){
						stopCurrentRun  = true;
						break;
					}
				}
				writeStats();
				if(stopCurrentRun == true){
					break;
				}
			}
			writeString("\n");
		}
	}
	
	/**
	 * Total distance algorithm evaluation
	 */
	public static void run24x18PerformanceMetricTotalDistance(){
		writeString("24 x 18 grid, total distance solution\n");
		totalTime = 0;
		for(int obs = 0; obs <= 30; obs=obs+5){
			boolean stopCurrentRun = false;
			for(int n = 10; n <= 160; n=n+10){
				resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
				for(int i = 0; i < 10; i ++){
					System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					long stat[] = solveProblemDistance(p, n*0.002 + obs*0.002, 600);
					processStats(stat);
					if(stat == null){
						stopCurrentRun  = true;
						break;
					}
				}
				if(stopCurrentRun == true){
					break;
				}
				writeStats();
			}
			writeString("\n");
		}
	}
	
	/**
	 * Total distance algorithm evaluation
	 */
	public static void run24x18PerformanceMetricTotalDistanceSplit(){
		writeString("24 x 18 grid, total distance solution, split computation\n");
		totalTime = 0;
		int split = 2;
		for(int obs = 0; obs <= 30; obs=obs+5){
			if(obs > 20) split = 3;
			boolean stopCurrentRun = false;
			for(int n = 10; n <= 160; n=n+10){
				resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
				for(int i = 0; i < 10; i ++){
					System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					long stat[] = solveProblemDistanceSplit(p, n*0.001 + obs*0.0025 + (n > 70 ? n*0.001 : 0), 600, split);
					processStats(stat);
					if(stat == null){
						stopCurrentRun  = true;
						break;
					}
				}
				if(stopCurrentRun == true){
					break;
				}
				writeStats();
			}
			writeString("\n");
		}
	}
	
	/**
	 * Fixing time and evaluate solution optimality, total time 
	 */
	public static void run24x18PerformanceMetricTotalTimeFixedTime(){
		MultiagentGraphSolverGurobiTotalTime.bReturnAnySolution = true;
		writeString("24 x 18 grid, total time solution, fixed time (100s)\n");
		totalTime = 0;
		for(int obs = 0; obs <= 20; obs=obs+5){
			boolean stopCurrentRun = false;
			for(int n = 70; n <= 100; n=n+10){
				resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
				for(int i = 0; i < 10; i ++){
					System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					long stat[] = solveProblemTotalTime(p, 0.001, 100);
					processStats(stat);
					if(stat == null){
						stopCurrentRun  = true;
						break;
					}
				}
				writeStats();
				if(stopCurrentRun == true){
					break;
				}
			}
			writeString("\n");
		}
		MultiagentGraphSolverGurobiTotalTime.bReturnAnySolution = false;
	}
	
	/**
	 * Fixing time and evaluate solution optimality, total distance 
	 */
	public static void run24x18PerformanceMetricTotalDistanceSplitFixedTime(){
		MultiagentGraphSolverGurobiDistance.bReturnAnySolution = true;
		writeString("24 x 18 grid, total distance solution, split computation, fixed time (100s)\n");
		totalTime = 0;
		for(int obs = 0; obs <= 20; obs=obs+5){
			boolean stopCurrentRun = false;
			for(int n = 10; n <= 150; n=n+10){
				resetStats("24x18, " + obs + "% obstacles, " + n + " agents, 10 runs. ");
				for(int i = 0; i < 10; i ++){
					System.out.println("Working on " + "24x18 grid performance testing, problem: " + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					Problem p = Problem.readFromFile(basePath + "\\24x18-grid\\" + n + "-agt-"+ (obs) + "-pct-obs-" + (1001 + i) + ".dat");
					long stat[] = solveProblemDistanceSplit(p, 0.001, 100, 2);
					processStats(stat);
					if(stat == null){
						stopCurrentRun  = true;
						break;
					}
				}
				if(stopCurrentRun == true){
					break;
				}
				writeStats();
			}
			writeString("\n");
		}
		MultiagentGraphSolverGurobiDistance.bReturnAnySolution = false;
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Previous stuff - not very relevant now...
	

	protected static void run32x32PerformanceMetricSplitting(int from, int to, int spacing, double timeLimit, int splitLevels, boolean noCycle){
		totalTime = 0;
		for(int n = from; n <= to; n = n + spacing){
			resetStats("32x32, 20% obstacles, "+ n + " agents, 10 runs. ");
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + n + "-agts-" + (1001 + i) + ".dat");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, splitLevels, noCycle);
				processStats(stat);
			}
			writeStats();
		}
	}
	
	protected static void run32x32PerformanceMetric8Connected(){
		totalTime = 0;
		for(int a = 10; a <= 60; a = a + 10){
			resetStats("32x32, 20% obstacles, " + a + " agents, 10 runs. ");
			for(int i = 0; i < 10; i ++){
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid-8c\\20-pct-obs-" + a + "-agts-" + (1001 + i) + ".dat");
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + a + "-" + (1001 + i) + ".dat");
				// long stat[] = solveProblem(p, false, 3600);
				long stat[] = solveProblemSuboptimal(p, false, false, 0, 3600, 1, false);
				processStats(stat);
			}
			writeStats();
		}
	}

	/**
	 * This one does not do exact computation - do not use!
	 */
	protected static void run32x32PerformanceMetricHeuristic(){
		totalTime = 0;
		for(int n = 20; n <= 200; n = n + 20){
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents, 100 runs. ");
			for(int i = 0; i < 10; i ++){
				System.out.println("Working on " + "32x32 grid performance testing, problem: 20-pct-obs-" + n + "-" + (1001 + i) + ".dat");
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid\\20-pct-obs-" + n + "-" + (1001 + i) + ".dat");
				long stat[] = solveProblemWithHeuristic(p);
				processHeuristicStats(stat, n);
			}
			writeHeuristicStats();
		}
	}
	
	protected static void run32x32PerformanceMetricSplitting8Connected(int from, int to, double timeLimit, int splitLevels, boolean noCycle){
		totalTime = 0;
		for(int n = from; n <= to; n = n + 25){
			resetStats("32x32, 20% obstacles, "+ n + " agents, 10 runs. ");
			resetHeuristicStats("32x32, 20% obstacles, "+ n + " agents," + (noCycle?" no cycles, ":"") + " 10 runs. ");
			for(int i = 0; i < 10; i ++){
				System.out.println("Working on " + "32x32 grid performance testing," + (noCycle?" no cycles, ":"") + " problem: 20-pct-obs-" + n + "-agts-" + (1001 + i) + ".txt");
				Problem p = Problem.readFromFile(basePath + "\\32x32-grid-8c\\20-pct-obs-" + n + "-agts-" + (1001 + i) + ".txt");
				long stat[] = solveProblemSuboptimal(p, false, true, 0, timeLimit, splitLevels, noCycle);
				processStats(stat);
			}
			writeStats();
		}
	}

	protected static void runFullPerformanceMetric(){
		// No splitting, cycle
		writeString("No splitting\n");
		runCrowdedPerformanceMetric8x8(10, 50, 3600, 0, false);
		runCrowdedPerformanceMetric16x16(10, 70, 3600, 0, false);
		run32x32PerformanceMetric();
		
		// 2 - split, cycle
		writeString("2 - splitting\n");
		runCrowdedPerformanceMetric8x8(10, 60, 3600, 1, false);
		runCrowdedPerformanceMetric16x16(10, 120, 3600, 1, false);
		run32x32PerformanceMetricSplitting(25, 125, 25, 3600, 1, false);
		
		// 2 - split, no cycle
		writeString("2 - splitting, no cycle\n");
		runCrowdedPerformanceMetric8x8(10, 40, 3600, 1, true);
		runCrowdedPerformanceMetric16x16(10, 80, 3600, 1, true);
		
		// 4 - split, cycle
		writeString("4 - splitting\n");
		runCrowdedPerformanceMetric8x8(10, 60, 3600, 2, false);
		runCrowdedPerformanceMetric16x16(10, 160, 3600, 2, false);
		run32x32PerformanceMetricSplitting(25, 100, 25, 3600, 2, false);
		
		// 4 - split, no cycle
		writeString("4 - splitting, no cycle\n");
		runCrowdedPerformanceMetric8x8(10, 40, 3600, 2, true);
		runCrowdedPerformanceMetric16x16(10, 100, 3600, 2, true);
		
		// 8 - split, cycle
		writeString("8 - splitting\n");
		runCrowdedPerformanceMetric8x8(10, 60, 3600, 3, false);
		runCrowdedPerformanceMetric16x16(10, 210, 3600, 3, false);
		run32x32PerformanceMetricSplitting(25, 150, 25, 3600, 3, false);
		
		// 8 - split, no cycle
		writeString("8 - splitting, no cycle\n");
		runCrowdedPerformanceMetric8x8(10, 40, 3600, 3, true);
		runCrowdedPerformanceMetric16x16(10, 120, 3600, 3, true);
		
		// 16 - split, cycle
		writeString("16 - splitting\n");
		runCrowdedPerformanceMetric8x8(20, 60, 3600, 4, false);
		runCrowdedPerformanceMetric16x16(20, 220, 3600, 4, false);
		run32x32PerformanceMetricSplitting(25, 250, 25, 3600, 4, false);
		
		// 16 - split, no cycle
		writeString("16 - splitting, no cycle\n");
		runCrowdedPerformanceMetric8x8(20, 40, 3600, 4, true);
		runCrowdedPerformanceMetric16x16(20, 130, 3600, 4, true);
	}

	protected static void runFullPerformanceMetricExtras(){
		// No splitting, cycle
		writeString("No splitting\n");
		runCrowdedPerformanceMetric16x16(90, 90, 3600, 0, false);
	}
}