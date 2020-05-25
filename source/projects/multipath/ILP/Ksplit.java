package projects.multipath.ILP;


//2-approximation
import java.io.*;
import java.util.*;

import projects.multipath.ECBS.Ecbs;
import projects.multipath.ILP.PathFinder;
import projects.multipath.ILP.PathPlanner;
import projects.multipath.advanced.Graph;
import projects.multipath.advanced.Path;
import projects.multipath.advanced.Problem;
import projects.multipath.advanced.Vertex;


public class Ksplit{

    public static long[] solveProblemSuboptimal(Problem p, boolean solveLP, boolean setOptimal, int extraSteps,
    double timeLimit, int KsplitLevel, boolean noCycle){
        PathPlanner ms = new PathPlanner();
        Ksplit ks=new Ksplit();
		long time = System.currentTimeMillis();
		int makespanLb = PathFinder.getMakespanLowerBound(p.graph, p.sg[0], p.sg[1]);
	
        int[][] paths = null;
        
        if(noCycle){
			paths = ms.planPathsAdvancedSplit(p.graph, p.sg[0], p.sg[1], setOptimal, timeLimit, KsplitLevel);
		}
		else{
			paths = ks.multiThreadedSplitPlanning(p.graph, p.sg[0], p.sg[1], timeLimit, KsplitLevel);
		}
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

    

    public static int[] splitPaths(Graph g, int start[], int goal[], boolean random,int splits){
		int middle[] = new int[start.length]; 
		Path paths[] = PathFinder.findShortestPaths(g, start, goal);

		// Sort all paths by their lengths in descending order - long paths should be processed first
		SortedMap<Integer, Vector<Integer>> lengthIdMap = new TreeMap<Integer, Vector<Integer>>();
		for(int i = 0; i < paths.length; i ++){
			Vector<Integer> indexSet = lengthIdMap.get(-paths[i].vertexList.size());
			if(indexSet == null){
				indexSet = new Vector<Integer>();
				lengthIdMap.put(-paths[i].vertexList.size(), indexSet);
			}
			indexSet.add(i);
		}
		Integer[] keyArray = lengthIdMap.keySet().toArray(new Integer[0]);
		int[] vArray = new int[paths.length];
		int vIndex = 0;
		for(int i = 0; i < keyArray.length; i ++){
			Integer[] indexArray = lengthIdMap.get(keyArray[i]).toArray(new Integer[0]);
			for(int in = 0; in < indexArray.length; in ++){
				vArray[vIndex++] = indexArray[in];
			}
		}
		
		if(random){
			Vector<Integer> vec = new Vector<Integer>();
			for(int i = 0; i < vArray.length; i++){
				vec.add(i);
			}
			for(int i = 0; i < vArray.length; i++){vArray[i] = vec.remove((int)(vec.size()*Math.random()));}
		}
		
		Set<Integer> usedVertexSet = new HashSet<Integer>();
		for(int i = 0; i < paths.length; i ++){
			// Find an intermediate goal that's about halfway
			int dMin1 = paths[vArray[i]].vertexList.size()/splits;
            int dMax1 = paths[vArray[i]].vertexList.size()/splits;
            int dMin2 = paths[vArray[i]].vertexList.size()-dMin1;
            int dMax2 = paths[vArray[i]].vertexList.size()-dMax1;
           // System.out.println(dMin1+" "+dMin2);
			boolean foundVertex = false;
			boolean flipped = true;
			while(!foundVertex){
				Set<Integer> fromStartSet = PathFinder.findGoalsInDistanceRange(g, start[vArray[i]], dMin1, dMax1);
				Set<Integer> fromGoalSet = PathFinder.findGoalsInDistanceRange(g, goal[vArray[i]], dMin2, dMax2);
				
				// Find intersection
				Set<Integer> iSet = new HashSet<Integer>();
				for(int v: fromStartSet){
					if(fromGoalSet.contains(v)){
						iSet.add(v);
					}
				}
				
				// Need some vertex not in usedVertexSet
				for(int v: iSet){
					if(!usedVertexSet.contains(v)){
						middle[vArray[i]] = v;
						usedVertexSet.add(v);
						foundVertex = true;
						break;
					}
				}
				
				if(!foundVertex){
					if(flipped){
						dMin1 -= 1;
						if(dMin1 < 0){dMin1 = 0;}
					}
					else{
						dMax1 += 1;
					}
					flipped = !flipped;
				}
			}
		}
		return middle;
    }
    

    int mpieces = 0;
	int msg[][] = null;
	boolean mdone[] = null;
	Graph mg = null;
	Map<Integer, Object> mpath = new HashMap<Integer, Object>();
	double mtime = 0;
	protected int[][] multiThreadedSplitPlanning(Graph g, int start[], int goal[], double timeLimit, int Ksplits){
		
		mg = g;
		mtime = timeLimit;
        //mpieces = (int)Math.pow(2, splits);
        mpieces = Ksplits;
		msg = new int[mpieces + 1][start.length];
        mdone = new boolean[mpieces];
        
		for(int i = 0;i < start.length; i ++){
			msg[0][i] = start[i];
			msg[mpieces][i] = goal[i];
		}
		
		boolean printPaths = false;
		if(MultiagentGraphSolverGurobiTime.bPrintPaths == true){
			MultiagentGraphSolverGurobiTime.bPrintPaths = false;
			printPaths = true;
        }
       // long makespanLb=PathFinder.getMakespanLowerBound(g, start, goal);
       // int threshold=(int)makespanLb/(mpieces);
		
        // Do splitting
        int [] tmp=start;
		for(int i = 1; i < Ksplits; i ++){
			//int numSplits = (int)Math.pow(2, i);
            //int stepSize = (int)Math.pow(2, splits - i);
            long makespanLb=PathFinder.getMakespanLowerBound(g, tmp, goal);
            
            int [] middle=splitPaths(g, tmp, goal, false, Ksplits-i+1);
            for(int a = 0; a < start.length; a ++){
                msg[i][a] = middle[a];
            }
            tmp=middle;
            /*
			for(int s = 0; s < numSplits; s++){
				//System.out.println("OK");
				int[] middle = splitPaths(g, msg[s*stepSize], msg[(s+1)*stepSize], true);
				
				for(int a = 0; a < start.length; a ++){
					msg[s*stepSize + stepSize/2][a] = middle[a];
				}
			}*/
		}
		
		
		// Plan each piece using a thread
		for(int i = 0; i < mpieces; i ++){
			Thread x = createThread(i);
			x.start();
		}
		
		// Wait for work to finish
		boolean allDone = false;
		while(!allDone){
			allDone = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i < mpieces; i ++){
				if(mdone[i] == false){
					allDone = false;
					break;
				}
			}
		}
		
		// All done, piece together the paths
		int[][] allPaths = (int[][])(mpath.get(0));
		for(int i = 1;i < mpieces; i ++){
			allPaths = mergePaths(allPaths, (int[][])(mpath.get(i)));
		}
		
		
		
		
		return allPaths;
    }

    private int[][] mergePaths(int[][] pathFromStart, int[][] pathFromMiddle){
		if(pathFromStart != null && pathFromMiddle != null){
			int[][] fullPaths = new int[pathFromStart.length][pathFromStart[0].length + pathFromMiddle[0].length - 1];
			for(int i = 0; i < pathFromStart.length; i ++){
				for(int j = 0; j < pathFromStart[0].length; j++){
					fullPaths[i][j] = pathFromStart[i][j]; 
				}
				for(int j = 1; j < pathFromMiddle[0].length; j++){
					fullPaths[i][pathFromStart[0].length + j - 1] = pathFromMiddle[i][j]; 
				}
			}
			return fullPaths;
		}
		return null;
	}
    
    private Thread createThread(final int i){
		return new Thread(
				new Runnable(){
					@Override
					public void run(){
						MultiagentGraphSolverGurobiTime solver = new MultiagentGraphSolverGurobiTime();
						try {
							int paths[][] =  solver.solve(mg, msg[i], msg[i+1], false, true, 0, true , mtime);
							if(paths != null){
								if(PathPlanner.isPathSetValid(paths, mg, msg[i], msg[i+1])){
									mpath.put(i, paths);
								}
							}
							else{
								if(MultiagentGraphSolverGurobiTime.bDebugInfo){
									System.out.println("Maximum allowed time reached, optimization stopped.");
								}
							}
							mdone[i] = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				});
	}

}