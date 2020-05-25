package projects.multipath.ILP;


//2-approximation
import java.io.*;
import java.util.*;

import projects.multipath.advanced.Graph;
import projects.multipath.advanced.Path;
import projects.multipath.advanced.Problem;
import projects.multipath.advanced.Vertex;


class IntermidStruct2{
   
    int numAgents1;
    int numAgents2;
    int [][]sg1=new int[2][];
    int [][]sg2=new int[2][];

    List<Integer> idMap1=new ArrayList<Integer>();
    List<Integer> idMap2=new ArrayList<Integer>();

    int [][] midsg=new int[2][];
}


class SpaceSplitTwo{
    boolean overflow=false;
    private int rows;
    private int cols;
    private int numAgents;
    private int[][] sg=null;
    long makespanLB=0;
    int increment=5;
    int w=5;

    ResultGT[] outputGT=new ResultGT[4];
    Graph [] subgraph=null;
    Graph[] bufferZone=null;
    long makespan;
    List<Integer> bufferSG=new ArrayList<Integer>();
    void set_sg(int [][]sg){
        this.sg=sg;
    }

    private Graph sg1_bf1=null;
    private Graph sg2_bf2=null;
    private Graph sg1_bf2=null;
    private Graph sg2_bf1=null;
    private Graph sg1_bf1_sg2=null;
    private Graph sg1_bf2_sg2=null;
    private Graph allgGraph=null;

    private double [][] distance_s1b1=null;
    private double [][] distance_s2b2=null;
    private double [][] distance_s1b2=null;
    private double [][] distance_s2b1=null;
    private double [][] distance_s1b1s2=null;
    private double [][] distance_s1b2s2=null;

    private void saveInstance(Problem p, String addr){
        String g_name=addr+"graph.txt";
        String sg=addr+"sg.txt";
        String idv=addr+"idv.txt";
        try{
            saveArrays(p.sg, sg);
            saveGraph(p.graph, g_name);
            saveIdVertex(p.graph, idv);
        }
        catch(Exception e){

        }
    }

    private void saveIdVertex(Graph graph,String filename) {
        try{
            File filex=new File(filename);
            PrintStream ps = null ; 
            ps = new PrintStream(new FileOutputStream(filex));
            for(int i=0;i<graph.vertices.length;i++){
                int vid=i;
                Vertex v=graph.idVertexMap.get(i);
                ps.printf("%d %d",v.getIX(),v.getIY());
                ps.println();
                
            }
            ps.close();
        }
        catch(Exception e){
            
        }
    }

    private void saveGraph(Graph graph,String filename) {
        try{
            File filex=new File(filename);
            PrintStream ps = null ; 
            ps = new PrintStream(new FileOutputStream(filex));
            for(int i=0;i<graph.vertices.length;i++){
                int vid=i;
                Set<Integer> nbr=graph.adjacencySetMap.get(vid);
                Iterator<Integer> it=nbr.iterator();
                while(it.hasNext()){
                    int v=it.next();
                    ps.printf("%d ", v);
                }
                ps.println();
                
            }
            ps.close();
        }
        catch(Exception e){
            
        }
    }

    static public int[][] readArray(String filename) throws Exception{
        int [][]sg=new int[2][];
        
        BufferedReader br = new BufferedReader(new FileReader(new File(
                filename)));
        String s = "";
        int k=0;
        while ((s = br.readLine()) != null) {
            List<Integer> nbrx=new ArrayList<Integer>();
            String[] ss = s.split(" ");
            for(int i=0;i<ss.length;i++){
                nbrx.add(Integer.parseInt(ss[i]));
            }
            int[]nbr=nbrx.stream().mapToInt(Integer::valueOf).toArray();
            sg[k]=nbr;
            k++;
        }

        return sg;
    }

    public void set_wh(int w,int increment){
        this.w=w;
        this.increment=increment;
    }

    static public Graph readGraph(String filename) throws Exception{
        Graph graph=new Graph();
        BufferedReader br = new BufferedReader(new FileReader(new File(
                filename)));
        String s = "";
        int k=0;
        while ((s = br.readLine()) != null) {
            List<Integer> nbrx=new ArrayList<Integer>();
            String[] ss = s.split(" ");
            for(int i=0;i<ss.length;i++){
                nbrx.add(Integer.parseInt(ss[i]));
            }
            int[]nbr=nbrx.stream().mapToInt(Integer::valueOf).toArray();
            graph.addVertex(k,nbr);
            k++;
        }
        graph.finishBuildingGraph();
        return graph;
    }

    private void saveArrays(int [][]arrays,String filename){
        try{
            File filex=new File(filename);
            PrintStream ps = null ; 
            ps = new PrintStream(new FileOutputStream(filex));
            for(int i=0;i<arrays.length;i++){
                for(int j=0;j<arrays[0].length;j++){
                    ps.printf("%d ",arrays[i][j]);
                }
                ps.println();
            }
            ps.close();
        }
        catch(Exception e){
            
        }
    }


   

    private void preprocess(){
        System.out.println("Start preprocessing");
        sg1_bf1=Graph.concatenate(subgraph[0], bufferZone[0]);
        sg2_bf2=Graph.concatenate(subgraph[1], bufferZone[1]);
        sg1_bf2=Graph.concatenate(subgraph[0], bufferZone[1]);
        sg2_bf1=Graph.concatenate(subgraph[1], bufferZone[0]);
        sg1_bf1_sg2=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),subgraph[1]);
        sg1_bf2_sg2=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[1]),subgraph[1]);
        allgGraph=Graph.concatenate(sg1_bf1, sg1_bf2);
/*
        boolean finished[]=new boolean[6];
        for(int i = 0; i <6; i ++){
			Thread x = preprThread(i,finished);
			x.start();
        }
        boolean allDone = false;
		while(!allDone){
			allDone = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
            }
            for(int i = 0; i <6; i ++){
				if(finished[i] == false){
					allDone = false;
					break;
				}
			}
        }*/

        
        System.out.printf("Preprocess complete!\n");
        
    }

    private Thread preprThread(final int i,boolean []finished){
		return new Thread(
				new Runnable(){
					@Override
					public void run(){
						try {
                            switch(i){
                            case 0:
                                distance_s1b1=floyd(sg1_bf1);
                                break;
                            case 1:
                                distance_s2b2=floyd(sg2_bf2);
                                break;
                            case 2:
                                distance_s1b2=floyd(sg1_bf2);
                                break;
                            case 3:
                                distance_s2b1=floyd(sg2_bf1);
                                break;
                            case 4:
                                distance_s1b1s2=floyd(sg1_bf1_sg2);
                                break;
                            case 5:
                                distance_s1b2s2=floyd(sg1_bf2_sg2);
                                break;
                            }
                            finished[i]=true;
						} catch (Exception e) {
                            System.out.println("Exception");
                            finished[i]=true;
						}
					};
				});
    }

    private double [][] floyd(Graph tmp){
        long time=System.currentTimeMillis();
        
        int V=tmp.vertices.length;
        double [][]dist=new double[V][V];
        Vertex v[]=tmp.vertices;
       // double dki=0,dkj=0,dij=0;
        for (int k = 0; k < V; k++) {  
           Set<Integer> nbrk=tmp.adjacencySetMap.get(v[k].id);
            for (int i = 0; i < V; i++) {  
                if(i==k){
                    dist[k][i]=0;
                }
                else{
                    if(nbrk.contains(v[i].id)){
                        dist[k][i]=1;
                    }
                    else{
                        dist[k][i]=1e4;
                    }
                }
               
            }  
        }  

        for (int k = 0; k < V; k++) {         
            for (int i = 0; i < V; i++) {  
                for(int j=0;j<V;j++){
                    if (dist[i][k] + dist[k][j] < dist[i][j])  
                        dist[i][j] = dist[i][k] + dist[k][j];
                }
                
               
            }  
        }  

        System.out.printf("Done in %f seconds\n",(System.currentTimeMillis()-time)/1000.);
        return dist;
    }
    /*
    private void compute_pair_dist(Graph tmp,HashMap<String,Double>dMap){
        long time=System.currentTimeMillis();
        for(int i=0;i<tmp.vertices.length;i++){
            Vertex v1=tmp.vertices[i];
            for(int j=i;j<tmp.vertices.length;j++){
                Vertex v2=tmp.vertices[j];
                int []p1=new int[]{v1.getIX(),v1.getIY()};
                int []p2=new int[]{v2.getIX(),v2.getIY()};
                distance(tmp, p1, p2, dMap);
            }
        }
        System.out.printf("Done in %f seconds\n",(System.currentTimeMillis()-time)/1000.);
    }*/

    public int get_id(int x,int y){
        return x*(rows)+y;
    }

    public int[] get_vertex(int vid){
        int x;
        int y;
        x=(int)(vid/(rows));
        y=(int)(vid%(rows));
        return new int[]{x,y};
    }

    

    int[][] mergePaths(int [][]paths1,int [][] paths2,List<Integer> idMap1 ,List<Integer> idMap2){
        int l1,l2,numAgents1,numAgents2;
        if(paths1==null){
            l1=0;
            numAgents1=0;
        }
        else{
            l1=paths1[0].length;
            numAgents1=paths1.length;
        }
        if(paths2==null){
            l2=0;
            numAgents2=0;
        }
        else{
            l2=paths2[0].length;
            numAgents2=paths2.length;
        }
       
        int maxlen=Math.max(l1,l2);

      
        int [][] finalPaths=new int[numAgents][maxlen];
        for(int i=0;i<numAgents1;i++){
            for(int j=0;j<l1;j++){
                finalPaths[idMap1.get(i)][j]=paths1[i][j];
            }
            for(int j=l1;j<maxlen;j++){
                finalPaths[idMap1.get(i)][j]=paths1[i][paths1[0].length-1];
            }
        }
        for(int i=0;i<numAgents2;i++){
            for(int j=0;j<l2;j++){
                finalPaths[idMap2.get(i)][j]=paths2[i][j];
            }
            for(int j=l2;j<maxlen;j++){
                finalPaths[idMap2.get(i)][j]=paths2[i][paths2[0].length-1];
            }
        }

        int [][] result=new int[numAgents1+numAgents2][maxlen];
        int k=0;
        int i=0;
        while(k<numAgents1+numAgents2){
            if(bufferSG.contains(i)!=true){
                for(int j=0;j<maxlen;j++){
                    result[k][j]=finalPaths[i][j];
                    
                }
                k++;
                i++;
                
            }
            else{
                i++;
            }
        }
        return result;
        //return finalPaths;
    }

    int [][] concatenatePaths(int [][]paths1,int [][]paths2){
        int finalLength=paths1[0].length+paths2[0].length;
        int [][]finalPaths=new int[paths1.length][finalLength];
        for(int i=0;i<paths1.length;i++){
            for(int j=0;j<paths1[0].length;j++){
                finalPaths[i][j]=paths1[i][j];
            }
            for(int j=0;j<paths2[0].length;j++){
                finalPaths[i][j+paths1[0].length]=paths2[i][j];
            }
            
        }
        return finalPaths;
        
    }



    long get_lower_bound(){
        Graph gx=new Graph();
        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++){
                List<Integer> nbr=new ArrayList<Integer>();
                int vid=get_id(i,j);
                if(i>0){
                    nbr.add(get_id(i-1, j));              
                }
                if(i<cols-1){
                    nbr.add(get_id(i+1,j));
                }
                if(j<rows-1){
                    nbr.add(get_id(i,j+1));
                }
                if(j>0){
                    nbr.add(get_id(i,j-1));
                }
                int[] nbarray=nbr.stream().mapToInt(Integer::valueOf).toArray();
                gx.addVertex(vid,nbarray,new int[]{i,j});
                gx.VidList.add(Integer.valueOf(vid));
            }  
        }
     
        gx.finishBuildingGraph();
        
        makespanLB = PathFinder.getMakespanLowerBound(gx, sg[0], sg[1])-1;
        return makespanLB;
    }

    public long[] solve_original_problem_split(int k_way){

            Problem p=new Problem();
            Graph gx=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),Graph.concatenate(subgraph[1],bufferZone[1]));
            int []start=new int [sg[0].length];
            int [] goal=new int [sg[1].length];
            for(int i=0;i<start.length;i++){
                start[i]=sg[0][i];
                goal[i]=sg[1][i];
            }
            gx=Graph.convertGraphGT(gx, start, goal);

            p.graph=gx;
            p.sg=new int [2][];
            p.sg[0]=start;
            p.sg[1]=goal;
            //long[] result=Solve.solveProblemSplit(p,true,600,k_way);
            long [] result=Solve.solveProblemSuboptimal(p, false,true,0, 600,k_way,false);
            p.sg[1]=PathPlanner.splitPaths(p.graph, p.sg[0], p.sg[1], true);
            saveInstance(p, "./instances/st/");
            return result;
        }

    

    long[]solve_original_problem(){
        System.out.printf("Solve original problem!\n");
        Problem p=new Problem();
    
        Graph gx=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),Graph.concatenate(subgraph[1],bufferZone[1]));
        int []start=new int [sg[0].length];
        int [] goal=new int [sg[1].length];
        for(int i=0;i<start.length;i++){
            start[i]=sg[0][i];
            goal[i]=sg[1][i];
        }
        gx=Graph.convertGraphGT(gx, start, goal);

        p.graph=gx;
        p.sg=new int [2][];
        p.sg[0]=start;
        p.sg[1]=goal;
        long[] result=Solve.solveProblem(p,true,600);
    //    System.out.printf("Problem is solved in time %d miliseconds. The makespan is %d\n\n",result[1],result[0]);
        return result;
    }

    int[][]getPaths(IntermidStruct2 i1,IntermidStruct2 i2){
        int [][]pathsPhase1=mergePaths(outputGT[0].paths, outputGT[1].paths,  i1.idMap1, i1.idMap2);
        int [][]pathsPhase2=mergePaths(outputGT[2].paths, outputGT[3].paths, i2.idMap1, i2.idMap2);
        
        int [][] conPaths=concatenatePaths(pathsPhase1, pathsPhase2);
        System.out.println("Paths before shorten");
      //  print2dArray(conPaths);
      
        System.out.println("After shorten");
     //   print2dArray(finalpath);
        return conPaths;

    }



    HashSet<Integer> get_bufferZone(){
        HashSet<Integer> bufferZone=new HashSet<Integer>();
        int mid=(int)(cols/2.);
      //  System.out.printf("%d %d %d %d\n",rows,cols,w,increment);
        for(int i=mid-w;i<=mid+w;i++){
            for(int j=0;j<rows;j++){
                bufferZone.add(get_id(i, j));
            }
        }
        return bufferZone;
    }

    void printDebug(int[] start,int[] inter,int[] goal){
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        for(int i=0;i<start.length;i++){
            int []s=get_vertex(start[i]);
            int []im=get_vertex(inter[i]);
            int[]g=get_vertex(goal[i]);
            System.out.printf("(%d,%d)-->(%d,%d)-->(%d,%d)\n",s[0],s[1],im[0],im[1],g[0],g[1]);


        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }

    void check(Graph g, int[] start, int[] goal ){
        Vertex v1=null;
        Vertex v2=null;
        for (int i=0;i< start.length;i++){
            Path px=PathFinder.findOneShortestPath(g, start[i], goal[i], null);
            if(px.vertexList.size()>makespanLB/2.){
                //System.out.printf("%b\n",g.idVertexMap.containsKey(start[i]));
                v1=g.idVertexMap.get(start[i]);
                v2=g.idVertexMap.get(goal[i]);

                System.out.printf("%d(%d,%d) %d(%d,%d) %d\n",v1.id,v1.getIX(),v1.getIY(),v2.id,v2.getIX(),v2.getIY(),px.vertexList.size());
            }
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
 

    long[]  multiThreadSolver(){
        long[] result=new long[4];
        boolean[] finished=new boolean[4];
        makespanLB=get_lower_bound();
        System.out.printf("MakespanLb=%d\n",makespanLB/2);
        Problem[] sp=new Problem[4];
        for(int i=0;i<4;i++){
            sp[i]=new Problem();
            outputGT[i]=new ResultGT();
        }

       
        ///////Phase 1///////////////
        IntermidStruct2 intermid1=get_phase1(sg[0], sg[1], subgraph, bufferZone);
        Graph mg1=Graph.concatenate(subgraph[0], bufferZone[0]);
        check(mg1,intermid1.sg1[0] ,intermid1.sg1[1]);
        mg1=Graph.convertGraphGT(mg1, intermid1.sg1[0], intermid1.sg1[1]);
        sp[0].sg=intermid1.sg1;
        sp[0].graph=mg1; 
        //
       

        Graph mg2=Graph.concatenate(subgraph[1], bufferZone[1]);
        
        mg2=Graph.convertGraphGT(mg2, intermid1.sg2[0], intermid1.sg2[1]);
        sp[1].sg=intermid1.sg2;
        sp[1].graph=mg2;
       // check(mg2,sp[1].sg[0], sp[1].sg[1]);
       // System.out.println(PathFinder.getMakespanLowerBound(mg2,sp[1].sg[0], sp[1].sg[1]));

        ////////////phase 2//////////////
        IntermidStruct2 intermid2=get_phase2(intermid1.midsg[1], sg[1], subgraph, bufferZone);
        Graph mg3=Graph.concatenate(subgraph[0],bufferZone[1]) ;
        
        mg3=Graph.convertGraphGT(mg3, intermid2.sg1[0], intermid2.sg1[1]);
        sp[2].sg=intermid2.sg1;
        sp[2].graph=mg3;
       // check(mg3,sp[2].sg[0], sp[2].sg[1]);
        
        Graph mg4=Graph.concatenate(subgraph[1], bufferZone[0]);
        
        mg4=Graph.convertGraphGT(mg4, intermid2.sg2[0], intermid2.sg2[1]);
        sp[3].sg=intermid2.sg2;
        sp[3].graph=mg4;
        //check(mg4,sp[3].sg[0], sp[3].sg[1]);

        //printDebug(sg[0], intermid1.midsg[1], sg[1]);

        //System.out.printf("%b\n",if_increment(sg[0], sg[1], intermid1.midsg[1]));

       // System.out.println("!!!!!!!!!!");
       // Solve.solveProblemSplit(sp[0], true, -1, 1);
     //  Solve.solveProblemSuboptimalGT(sp[0], false,false,0, 1000,1,false,rows);
      //  return null;
        /////////////////////!!!!!!/////////////////////////////////////////////////////////////////\
        
        for(int i = 0; i <4; i ++){
			Thread x = createThreadGT(i,sp,result,finished);
			x.start();
        }
        boolean allDone = false;
		while(!allDone){
			allDone = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
            }
            for(int i = 0; i <4; i ++){
				if(finished[i] == false){
					allDone = false;
					break;
				}
			}
        }
        if(overflow!=true){
            long makespan=Math.max(result[0],result[1])+Math.max(result[2],result[3]);
            //   System.out.println(outputGT[0].paths[0].length);
            int [][]paths=getPaths(intermid1, intermid2);
            System.out.printf("The original makespan=%d !!!!\n",makespan);
            System.out.printf("After that: makespan=%d !!!!\n",paths[0].length-2);
            return new long[]{paths[0].length-2,0,0,0};
        }
       
     //   print2dArray(paths);
        
        return null;

    }



    private Thread createThreadGT(final int i,Problem[]p,long[] result,boolean []finished){
		return new Thread(
				new Runnable(){
					@Override
					public void run(){
						try {
                            outputGT[i]=new ResultGT();
                            //outputGT[i]=Solve.solveProblemSuboptimalGT(p[i], false,false,0, 1000,1,false,rows);
                            outputGT[i]=Solve.solveProblemGT(p[i], false, 600,rows,i);
                            result[i]=outputGT[i].output[0];
                            finished[i]=true;
						} catch (Exception e) {
                            System.out.println("Exception");
                            overflow=true;
                            finished[i]=true;
						}
					};
				});
    }

    
    
    long[] multiThreadSolverST(){
        long[] result=new long[4];
        boolean[] finished=new boolean[4];
       
        Problem[] sp=new Problem[4];
        for(int i=0;i<4;i++){
            sp[i]=new Problem();
            outputGT[i]=new ResultGT();
        }
        ///////Phase 1///////////////
        IntermidStruct2 intermid1=get_phase1(sg[0], sg[1], subgraph, bufferZone);
        Graph mg1=Graph.concatenate(subgraph[0], bufferZone[0]);
       
        mg1=Graph.convertGraphGT(mg1, intermid1.sg1[0], intermid1.sg1[1]);
        sp[0].sg=intermid1.sg1;
        sp[0].graph=mg1; 
        

        Graph mg2=Graph.concatenate(subgraph[1], bufferZone[1]);
        
        mg2=Graph.convertGraphGT(mg2, intermid1.sg2[0], intermid1.sg2[1]);
        sp[1].sg=intermid1.sg2;
        sp[1].graph=mg2;
       
        ////////////phase 2//////////////
        IntermidStruct2 intermid2=get_phase2(intermid1.midsg[1], sg[1], subgraph, bufferZone);
        Graph mg3=Graph.concatenate(subgraph[0],bufferZone[1]) ;
    
        
        mg3=Graph.convertGraphGT(mg3, intermid2.sg1[0], intermid2.sg1[1]);
        sp[2].sg=intermid2.sg1;
        sp[2].graph=mg3;
        
        
        Graph mg4=Graph.concatenate(subgraph[1], bufferZone[0]);
        
        mg4=Graph.convertGraphGT(mg4, intermid2.sg2[0], intermid2.sg2[1]);
        sp[3].sg=intermid2.sg2;
        sp[3].graph=mg4;
      
       // System.out.printf("%b\n",if_increment(sg[0], sg[1], intermid1.midsg[1]));
       // System.out.println("!!!!!!!!!!");
       // Solve.solveProblemSplit(sp[0], true, -1, 1);
     //  Solve.solveProblemSuboptimalGT(sp[0], false,false,0, 1000,1,false,rows);
      //  return null;
        /////////////////////!!!!!!/////////////////////////////////////////////////////////////////\
        
        for(int i = 0; i <4; i ++){
			Thread x = createThreadGTST(i,sp,result,finished);
			x.start();
        }
        boolean allDone = false;
		while(!allDone){
			allDone = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
            }
            for(int i = 0; i <4; i ++){
				if(finished[i] == false){
					allDone = false;
					break;
				}
			}
        }
        if(overflow!=true){
            long makespan=Math.max(result[0],result[1])+Math.max(result[2],result[3]);
            //   System.out.println(outputGT[0].paths[0].length);
            int [][]paths=getPaths(intermid1, intermid2);
            System.out.printf("The original makespan=%d !!!!\n",makespan);
            System.out.printf("After that: makespan=%d !!!!\n",paths[0].length-2);
         /*   Problem mvp=new Problem();
            mvp.graph=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),Graph.concatenate(subgraph[1],bufferZone[1]));
            mvp.sg=get_buffer_sg();
            Solve.solveProblemWithMovingObstacles(mvp, true, 600, paths);*/
         //   System.exit(1);
            return new long[]{paths[0].length-2,0,0,0};
        }
        return null;

    }

    long[] SolverGT(){
        preprocess();
        long[] result=new long[4];
        boolean[] finished=new boolean[4];
       
        Problem[] sp=new Problem[4];
        for(int i=0;i<4;i++){
            sp[i]=new Problem();
            outputGT[i]=new ResultGT();
        }
        ///////Phase 1///////////////
        long startTime=System.currentTimeMillis();
        IntermidStruct2 intermid1=get_phase1(sg[0], sg[1], subgraph, bufferZone);
        Graph mg1=Graph.concatenate(subgraph[0], bufferZone[0]);
       
        mg1=Graph.convertGraphGT(mg1, intermid1.sg1[0], intermid1.sg1[1]);
        sp[0].sg=intermid1.sg1;
        sp[0].graph=mg1; 

        Graph mg2=Graph.concatenate(subgraph[1], bufferZone[1]);
        
        mg2=Graph.convertGraphGT(mg2, intermid1.sg2[0], intermid1.sg2[1]);
        sp[1].sg=intermid1.sg2;
        sp[1].graph=mg2;
      
        ////////////phase 2//////////////
        IntermidStruct2 intermid2=get_phase2(intermid1.midsg[1], sg[1], subgraph, bufferZone);
        Graph mg3=Graph.concatenate(subgraph[0],bufferZone[1]) ;
    
        
        mg3=Graph.convertGraphGT(mg3, intermid2.sg1[0], intermid2.sg1[1]);
        sp[2].sg=intermid2.sg1;
        sp[2].graph=mg3;
 
        
        Graph mg4=Graph.concatenate(subgraph[1], bufferZone[0]);
        
        mg4=Graph.convertGraphGT(mg4, intermid2.sg2[0], intermid2.sg2[1]);
        sp[3].sg=intermid2.sg2;
        sp[3].graph=mg4;
   
       // System.out.printf("%b\n",if_increment(sg[0], sg[1], intermid1.midsg[1]));
       // System.out.println("!!!!!!!!!!");
       // Solve.solveProblemSplit(sp[0], true, -1, 1);
     //  Solve.solveProblemSuboptimalGT(sp[0], false,false,0, 1000,1,false,rows);
      //  return null;
        /////////////////////!!!!!!/////////////////////////////////////////////////////////////////\
        
        for(int i = 0; i <4; i ++){
			outputGT[i]=new ResultGT();

            outputGT[i]=Solve.solveProblemSuboptimalGT(sp[i], false,true,0, 300,1,false,rows);
         
            result[i]=outputGT[i].output[0];
        }
        long endTime=System.currentTimeMillis();
        if(overflow!=true){
            long makespan=Math.max(result[0],result[1])+Math.max(result[2],result[3]);
            //   System.out.println(outputGT[0].paths[0].length);
            int [][]paths=getPaths(intermid1, intermid2);
            System.out.printf("The original makespan=%d !!!!\n",makespan);
            System.out.printf("After that: makespan=%d !!!!\n",paths[0].length-2);
         /*   Problem mvp=new Problem();
            mvp.graph=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),Graph.concatenate(subgraph[1],bufferZone[1]));
            mvp.sg=get_buffer_sg();
            Solve.solveProblemWithMovingObstacles(mvp, true, 600, paths);*/
         //   System.exit(1);
            return new long[]{paths[0].length-2,endTime-startTime,0,0};
        }
        return null;

    }

    public Graph createObstacles(Graph g, Graph sample){
        Graph tmp=Graph.copyGraph(g);
        Iterator <Vertex> it=g.verticeSet.iterator();
        while(it.hasNext()){
            Vertex v=it.next();
            if(sample.idVertexMap.containsKey(v.id)==false){
                tmp.removeVertex(v);
            }
        }
        
        return tmp;
    }



    private Thread createThreadGTST(final int i,Problem[]p,long[] result,boolean []finished){
		return new Thread(
            new Runnable(){
                @Override
                public void run(){
                    try {
                        outputGT[i]=new ResultGT();
                        //outputGT[i]=Solve.solveProblemSuboptimalGT(p[i], false,false,0, 1000,1,false,rows);
                        outputGT[i]=Solve.solveProblemSuboptimalGT(p[i], false,true,0, 600,1,false,rows);
                        result[i]=outputGT[i].output[0];
                        finished[i]=true;
                    } catch (Exception e) {
                        System.out.println("Exception");
                        overflow=true;
                        finished[i]=true;
                    }
                };
            });
	}


    public void save_edges(Graph g, String filename)throws Exception{
        //System.out.println("////////////////////Begin saving///////////////");
        File filex=new File(filename);
        PrintStream ps = null ; 
        ps = new PrintStream(new FileOutputStream(filex)) ;
        for(int i = 0; i < g.vertices.length; i ++){
            Vertex vi=g.vertices[i];
            Integer[] vIds = g.adjacencySetMap.get(vi.id).toArray(new Integer[0]);
			for(int av = 0; av < vIds.length; av++){
                int []vj=get_vertex(vIds[av]);
				if(vIds[av] != i){
                    ps.printf("%d %d %d %d\n",vi.getIX(),vi.getIY(),vj[0],vj[1]);
               //     System.out.printf("%d %d %d %d\n",vi.getIX(),vi.getIY(),vj[0],vj[1]);
				}
			}
        }
       // System.out.println("///////////////////End saving///////////////");

    }

    void init(int rows,int cols,int numAgents,int[][]sg){
        this.rows=rows;
        this.cols=cols;
        this.numAgents=numAgents;
        this.sg=sg;
        //this.subgraph=getSubgraph();
        this.subgraph=getSubgraphWithHoles(0.);
        this.bufferZone=getBufferZoneWithHoles(0.,w, increment);
       preprocess();
    }

    IntermidStruct2 get_phase1(int[] starts,int [] goals, Graph [] subgraph,Graph[] bufferZone){
        
        List<Integer> s1=new ArrayList<Integer>();
        List<Integer> s2=new ArrayList<Integer>();
     
        List<Integer> im1=new ArrayList<Integer>();
        List<Integer> im2=new ArrayList<Integer>();
    
        List<Integer> im=new ArrayList<Integer>();
        IntermidStruct2 result=new IntermidStruct2();
        HashSet<Integer> HasUsed=new HashSet<Integer>();
       
        
       
     
        int numAgents=starts.length;
        for(int i=0;i<numAgents;i++){
            int start=starts[i];
            int goal=goals[i];
           
            int mid=-21;
            if(subgraph[0].VidList.contains(start)||bufferZone[0].VidList.contains(start)){
                if(subgraph[0].VidList.contains(goal)){
                   // mid=reallocate(start,goal,subgraph[0],bufferZone[0], HasUsed);  
                   
                    mid=reallocate(start,goal,subgraph[0],sg1_bf1, HasUsed,distance_s1b1);               
                }
                else{
                    if(bufferZone[0].VidList.contains(goal)){
                        mid=reallocate(start,goal,bufferZone[0],sg1_bf1, HasUsed,distance_s1b1);
                      // mid=reallocate(start,goal,bufferZone[0],subgraph[0], HasUsed);
                       
                    }
                    else{
                        if(bufferZone[1].VidList.contains(goal)){
                            int []v0=get_vertex(start);
                            int []vg=get_vertex(goal);
                            mid=move_out_bufferzone(v0, vg, subgraph[0],sg1_bf1,sg1_bf2, HasUsed,distance_s1b1,distance_s1b2);
                            
                        }
                        else{
                            int []v0=get_vertex(start);
                            int []vg=get_vertex(goal);
                            
                            mid=bufferzone_allocate_advanced(v0, vg, sg1_bf1_sg2,bufferZone[0], HasUsed,distance_s1b1s2);
                        }
                    }
                }
                s1.add(start);
                im1.add(mid);
                result.idMap1.add(i);
              
               
            }
            else{
                if(subgraph[1].VidList.contains(start)||bufferZone[1].VidList.contains(start)){
                    if(subgraph[1].VidList.contains(goal)){
                        mid=reallocate(start,goal,subgraph[1],sg2_bf2, HasUsed,distance_s2b2);
                        
                    }
                    else{
                        if(bufferZone[1].VidList.contains(goal)){
                           // mid=reallocate(goal, bufferZone[1], HasUsed);
                            mid=reallocate(start,goal,bufferZone[1],sg2_bf2, HasUsed,distance_s2b2);
                          
                        }
                        else{
                            if(bufferZone[0].VidList.contains(goal)){
                                int []v0=get_vertex(start);
                                int []vg=get_vertex(goal);
                                mid=move_out_bufferzone(v0, vg, subgraph[1], sg2_bf2,sg2_bf1,HasUsed,distance_s2b2,distance_s2b1);
                                
                            }
                            else{
                                int []v0=get_vertex(start);
                                int []vg=get_vertex(goal);
                                mid=bufferzone_allocate_advanced(v0, vg,sg1_bf2_sg2 ,bufferZone[1], HasUsed,distance_s1b2s2);
                      
                            }
                        }
                    }
                  
                    s2.add(start);
                    im2.add(mid);
                    result.idMap2.add(i);
                }            
                
            }     
            im.add(mid);
            
          
        }
        
        result.sg1[0]=s1.stream().mapToInt(Integer::valueOf).toArray();
        result.sg1[1]=im1.stream().mapToInt(Integer::valueOf).toArray();
        result.sg2[0]=s2.stream().mapToInt(Integer::valueOf).toArray();
        result.sg2[1]=im2.stream().mapToInt(Integer::valueOf).toArray();

     
        result.midsg[0]=starts;
        result.numAgents1=result.sg1[0].length;
        result.numAgents2=result.sg2[0].length;
 
        result.midsg[1]=im.stream().mapToInt(Integer::valueOf).toArray();
        /*
        save_points(result.midsg[1], "./intermediate.txt");
        save_points(starts, "./start.txt");
        save_points(goals, "./goal.txt");

        System.out.printf("Finding intermediate states done\n");*/

      
        return result;

    }


   

    int[][] get_buffer_sg(){
        int [][]result=new int[2][];
        result[0]=new int[bufferSG.size()];
        result[1]=new int[bufferSG.size()];
        for(int i=0;i<bufferSG.size();i++){
            int v=bufferSG.get(i);
            result[0][i]=sg[0][v];
            result[1][i]=sg[1][v];
        }

        return result;
    }

    void save_points(int [] points, String filename){
        
        try{
            File filex=new File(filename);
            PrintStream ps = null ; 
            ps = new PrintStream(new FileOutputStream(filex));
            for(int i=0;i<points.length;i++){
                int []p=get_vertex(points[i]);
                ps.printf("%d %d\n",p[0],p[1]);
            }
            ps.close();
        }
        catch(Exception e){
            
        }
    }


    void print2dArray(int[][] sg){
        System.out.println("**************************");
        for(int i=0;i<sg.length;i++){
            for(int j=0;j<sg[0].length;j++){
                System.out.printf("%4d ",sg[i][j]);
            }
            System.out.println();
        }
        System.out.println("**************************");
    }

    IntermidStruct2 get_phase2(int []starts,int []goals, Graph [] subgraph,Graph[] bufferZone){
        List<Integer> s1=new ArrayList<Integer>();
        List<Integer> s2=new ArrayList<Integer>();
     
        List<Integer> im1=new ArrayList<Integer>();
        List<Integer> im2=new ArrayList<Integer>();
        
        List<Integer> im=new ArrayList<Integer>();
        IntermidStruct2 result=new IntermidStruct2();
  
        int numAgents=starts.length;
        for(int i=0;i<numAgents;i++){
            int start=starts[i];
            int goal=goals[i];
            if(start<0){
                continue;
            }
       
            if(subgraph[0].VidList.contains(start)||bufferZone[1].VidList.contains(start)){
                
                s1.add(start);
                im1.add(goal);
                result.idMap1.add(i);
                
            }
            else{
                if(subgraph[1].VidList.contains(start)||bufferZone[0].VidList.contains(start)){
                
                    s2.add(start);
                    im2.add(goal);
                    result.idMap2.add(i);
                }
                
            }
            im.add(goal);
            
          
        }
        
        result.sg1[0]=s1.stream().mapToInt(Integer::valueOf).toArray();
        result.sg1[1]=im1.stream().mapToInt(Integer::valueOf).toArray();
        result.sg2[0]=s2.stream().mapToInt(Integer::valueOf).toArray();
        result.sg2[1]=im2.stream().mapToInt(Integer::valueOf).toArray();
   
        result.midsg[0]=starts;
        result.numAgents1=result.sg1[0].length;
        result.numAgents2=result.sg2[0].length;

        result.midsg[1]=im.stream().mapToInt(Integer::valueOf).toArray();
        return result;

    }

    double distance(int [] p1,int []p2){
        return Math.abs(p1[0]-p2[0])+Math.abs(p1[1]-p2[1]);
    }

    double distance(Graph g,int [] p1,int [] p2,double[][] dmap){
    
        int start=get_id(p1[0], p1[1]);
        int goal=get_id(p2[0], p2[1]);
        int i=0,j=0;
        for(i=0;i<g.vertices.length;i++){
            if(g.vertices[i].id==start) break;
        }
        for(j=0;j<g.vertices.length;j++){
            if(g.vertices[j].id==goal) break;
        }
    
      
       // System.out.printf("Distance is computed in %f\n",(System.currentTimeMillis()-time4)/1000.);       // System.out.printf("%b\n",p==null);
        return dmap[i][j];
    }


    double distance(Graph g,int [] p1,int [] p2,HashSet<Integer>alreadyVisitedVertices){
        
      
        int start=get_id(p1[0], p1[1]);
        int goal=get_id(p2[0], p2[1]);

      
      /*  
        if(g.idVertexMap.get(start)==null ||g.idVertexMap.get(goal)==null){
            System.out.println("###");
            System.exit(1);
        }

        System.out.printf("(%d,%d)-->(%d,%d)\n",p1[0],p1[1],p2[0],p2[1]);
        */
        Path p=PathFinder.findOneShortestPath(g, start, goal, null);
       // System.out.printf("Distance is computed in %f\n",(System.currentTimeMillis()-time4)/1000.);       // System.out.printf("%b\n",p==null);
        return p.vertexList.size();
    }

    public boolean if_increment(int []start,int[] goal, int[] intermediate){
        int num=start.length;
        boolean result=true;
       // Graph tmp=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),bufferZone[1]);
     
        for(int i=0;i<num;i++){
            int[] ss=get_vertex(start[i]);
            int [] gg=get_vertex(goal[i]);
            int []inter=get_vertex(intermediate[i]);
            if((gg[1]-inter[1])*(inter[1]-ss[1])>=0&&(gg[0]-inter[0])*(inter[0]-ss[0])>=0)
                continue;
            else{
                if(intermediate[i]<0)
                    continue;
                System.out.printf("(%d,%d),(%d,%d),(%d,%d)=%f,%f\n",ss[0],ss[1],inter[0],inter[1],gg[0],gg[1],distance(ss, inter)+distance(inter, gg)-distance(ss, gg),Math.abs(distance(ss, inter)-distance(gg, inter)));
                result=false;
               // return false;
            }
        }
        return result;

    }
    


    
    
  

    int bufferzone_allocate_advanced(int[] startPoint,int [] goalPoint,Graph sbs,Graph bufferzone,HashSet<Integer>HasUsed){
        long time3=System.currentTimeMillis();
        Iterator<Vertex> it=bufferzone.verticeSet.iterator();
        double obj=17997;
        
        //Graph tmp=Graph.concatenate(Graph.concatenate(subgraph[0],bufferzone),subgraph[1]);
        int minIndex=0;
        while(it.hasNext()){
            Vertex v=it.next();
            int []bufferpoint=new int[]{v.getIX(),v.getIY()};
            double s_b_dist=distance(sbs, startPoint, bufferpoint, HasUsed);
            double g_b_dist=distance(sbs, bufferpoint, goalPoint, HasUsed);
            double s_g_dist=distance(sbs, startPoint, goalPoint, HasUsed);
            double cur=s_b_dist+g_b_dist-s_g_dist+2*Math.max(maxDistance(g_b_dist,makespanLB/2.),maxDistance(s_b_dist,makespanLB/2.))+Graph.localDensity(bufferzone, v.id, HasUsed);
           // double cur=(double)(distance(startPoint, bufferpoint)+distance(bufferpoint, goalPoint)-distance(goalPoint, startPoint)+2*Math.abs(distance(goalPoint,bufferpoint)-distance(bufferpoint,startPoint)))+Graph.localDensity(bufferzone, v.id, HasUsed);

            if(cur<1e-3 && HasUsed.contains(v.id)==false){
                HasUsed.add(v.id);
                
                return v.id;
            }
            else{
                if(cur<obj){
                    if(HasUsed.contains(v.id)==false){
                        obj=cur;
                        minIndex=v.id;
                      //  System.out.printf("%d\n",minIndex);
                       // HasUsed.add(inter);
                    }               
                }
            }
        }

        if(HasUsed.contains(minIndex)==false){
            HasUsed.add(minIndex);
            Vertex v=sbs.idVertexMap.get(minIndex);
            int[]mid=get_vertex(v.id);

           // System.out.printf("Find intermeridate state in %f\n",(System.currentTimeMillis()-time3)/1000.);
    
            
            return minIndex;
        }
        if(minIndex==0){
            System.out.println("error!");
            System.exit(0);
        }
        
        return minIndex;
    }

    int bufferzone_allocate_advanced(int[] startPoint,int [] goalPoint,Graph sbs,Graph bufferzone,HashSet<Integer>HasUsed,double [][] dMap){
        long time3=System.currentTimeMillis();
        Iterator<Vertex> it=bufferzone.verticeSet.iterator();
        double obj=17997;
        
        //Graph tmp=Graph.concatenate(Graph.concatenate(subgraph[0],bufferzone),subgraph[1]);
        int minIndex=0;
        while(it.hasNext()){
            Vertex v=it.next();
            int []bufferpoint=new int[]{v.getIX(),v.getIY()};
         //   double s_b_dist=distance(sbs, startPoint, bufferpoint, dMap);
          //  double g_b_dist=distance(sbs, bufferpoint, goalPoint, dMap);
            //double s_g_dist=distance(sbs, startPoint, goalPoint, dMap);
            double s_b_dist=distance(startPoint, bufferpoint);
            double g_b_dist=distance(bufferpoint, goalPoint);
            double s_g_dist=distance(startPoint, goalPoint);
            double cur=s_b_dist+g_b_dist-s_g_dist+2*Math.max(maxDistance(g_b_dist,makespanLB/2.),maxDistance(s_b_dist,makespanLB/2.))+2*Graph.localDensity(bufferzone, v.id, HasUsed);
           // double cur=(double)(distance(startPoint, bufferpoint)+distance(bufferpoint, goalPoint)-distance(goalPoint, startPoint)+2*Math.abs(distance(goalPoint,bufferpoint)-distance(bufferpoint,startPoint)))+Graph.localDensity(bufferzone, v.id, HasUsed);

            if(cur<1e-3 && HasUsed.contains(v.id)==false){
                HasUsed.add(v.id);
                
                return v.id;
            }
            else{
                if(cur<obj){
                    if(HasUsed.contains(v.id)==false){
                        obj=cur;
                        minIndex=v.id;
                      //  System.out.printf("%d\n",minIndex);
                       // HasUsed.add(inter);
                    }               
                }
            }
        }

        if(HasUsed.contains(minIndex)==false){
            HasUsed.add(minIndex);
          
            Vertex v=sbs.idVertexMap.get(minIndex);
         
           
            

            //System.out.printf("Find intermeridate state in %f\n",(System.currentTimeMillis()-time3)/1000.);
    
            
            return minIndex;
        }
        if(minIndex==0){
            System.out.println("error!");
            System.exit(0);
        }
        
        return minIndex;
    }

    double maxDistance(double a,double b){
        return Math.max(a-b,0);
    }

    int move_out_bufferzone(int [] start,int[] goal, Graph subg,Graph sb1,Graph sb2,HashSet<Integer> hasUsed){
        Iterator<Vertex>it=subg.verticeSet.iterator();
        double min_dist=191000;
        int cur_id=0;
        while(it.hasNext()){
            Vertex v1=it.next();
            int []mid=new int[2];
            mid[0]=v1.getIX();
            mid[1]=v1.getIY();
            double s_b_dist=distance(sb1, start, mid, hasUsed);
            double g_b_dist=distance(sb2, mid, goal, hasUsed);
           // double s_g_dist=distance(tmp2,start, goal, hasUsed);
            double dist=s_b_dist+g_b_dist+2*Math.max(maxDistance(g_b_dist,makespanLB/2.),maxDistance(s_b_dist,makespanLB/2.))+Graph.localDensity(sb1, v1.id, hasUsed);
            if(dist<min_dist){
                if(hasUsed.contains(v1.id)==false){
                    cur_id=v1.id;
                    min_dist=dist;
                }
            }
        }
        Vertex v=sb1.idVertexMap.get(cur_id);
        int[]mid=get_vertex(v.id);
       // System.out.printf("%f %f\n#########################\n",distance(tmp1, start, mid, hasUsed),distance(tmp2, goal, mid, hasUsed));
        hasUsed.add(cur_id);
        return cur_id;
    }


    int move_out_bufferzone(int [] start,int[] goal, Graph subg,Graph sb1,Graph sb2,HashSet<Integer> hasUsed,double[][] dMap1,double [][]dMap2){
        Iterator<Vertex>it=subg.verticeSet.iterator();
        double min_dist=191000;
        int cur_id=0;
        while(it.hasNext()){
            Vertex v1=it.next();
            int []mid=new int[2];
            mid[0]=v1.getIX();
            mid[1]=v1.getIY();
           // double s_b_dist=distance(sb1, start, mid, dMap1);
          //  double g_b_dist=distance(sb2, mid, goal, dMap2);
            double s_b_dist=distance(start, mid);
            double g_b_dist=distance(mid, goal);
           // double s_g_dist=distance(tmp2,start, goal, hasUsed);
            double dist=s_b_dist+g_b_dist+2*Math.max(maxDistance(g_b_dist,makespanLB/2.),maxDistance(s_b_dist,makespanLB/2.));
            if(dist<min_dist){
                if(hasUsed.contains(v1.id)==false){
                    cur_id=v1.id;
                    min_dist=dist;
                }
            }
        }
        Vertex v=null;
        int []mid=null;
        try{
            mid=get_vertex(cur_id);
            v=sb1.idVertexMap.get(cur_id);
           
        }
        catch(Exception e){
            System.out.println(mid[0]+","+mid[1]+" ");
            e.printStackTrace();
            System.exit(0);
        }
       
        
       // System.out.printf("%f %f\n#########################\n",distance(tmp1, start, mid, hasUsed),distance(tmp2, goal, mid, hasUsed));
        hasUsed.add(cur_id);
        return cur_id;
    }

    int reallocate(int s,int g, Graph subg,Graph sb1,HashSet<Integer> hasUsed){
       // Graph tmp1=Graph.concatenate(subg,bufferZone1);
        Iterator<Vertex>it=subg.verticeSet.iterator();
        double min_dist=191000;
        int cur_id=0;
        
        int []start =get_vertex(s);
        int[]goal=get_vertex(g);
        while(it.hasNext()){
            Vertex v1=it.next();
            int []mid=new int[2];
            mid[0]=v1.getIX();
            mid[1]=v1.getIY();
            double s_b_dist=distance(sb1, start, mid, hasUsed);
            double g_b_dist=distance(sb1, mid, goal, hasUsed);
           // double s_g_dist=distance(tmp2,start, goal, hasUsed);
            double dist=3*Math.max(maxDistance(g_b_dist,makespanLB/2.),maxDistance(s_b_dist,makespanLB/2.))+2*Graph.localDensity(sb1, v1.id, hasUsed);//+maxDistance(g_b_dist,makespanLB/2.)+maxDistance(s_b_dist,makespanLB/2.);
            if(dist<min_dist){
                if(hasUsed.contains(v1.id)==false){
                    cur_id=v1.id;
                    min_dist=dist;
                }
            }
        }
        Vertex v=subg.idVertexMap.get(cur_id);
        int[]mid=get_vertex(v.id);
      //  System.out.printf("%f %f\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n",distance(tmp1, start, mid, hasUsed),distance(tmp1, goal, mid, hasUsed));

        hasUsed.add(cur_id);
        return cur_id;
    }


    int reallocate(int s,int g, Graph subg,Graph sb1,HashSet<Integer> hasUsed,double [][]dmap){
        // Graph tmp1=Graph.concatenate(subg,bufferZone1);
         Iterator<Vertex>it=subg.verticeSet.iterator();
         double min_dist=191000;
         int cur_id=0;
         
         int []start =get_vertex(s);
         int[]goal=get_vertex(g);
         while(it.hasNext()){
             Vertex v1=it.next();
             int []mid=new int[2];
             mid[0]=v1.getIX();
             mid[1]=v1.getIY();
            // double s_b_dist=distance(sb1, start, mid,dmap);
           //  double g_b_dist=distance(sb1, mid, goal,dmap);
            double s_b_dist=distance(start, mid);
            double g_b_dist=distance(mid, goal);
            // double s_g_dist=distance(tmp2,start, goal, hasUsed);
             double dist=3*Math.max(maxDistance(g_b_dist,makespanLB/2.),maxDistance(s_b_dist,makespanLB/2.));//+maxDistance(g_b_dist,makespanLB/2.)+maxDistance(s_b_dist,makespanLB/2.);
             if(dist<min_dist){
                 if(hasUsed.contains(v1.id)==false){
                     cur_id=v1.id;
                     min_dist=dist;
                 }
             }
         }
         Vertex v=subg.idVertexMap.get(cur_id);
         int[]mid=get_vertex(v.id);
       //  System.out.printf("%f %f\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n",distance(tmp1, start, mid, hasUsed),distance(tmp1, goal, mid, hasUsed));
 
         hasUsed.add(cur_id);
         return cur_id;
     }

    


    boolean check(Graph g){
        Iterator<Vertex> it1=g.verticeSet.iterator();
        Iterator<Vertex> it2=g.verticeSet.iterator();
        while(it1.hasNext()){
            Vertex v1=it1.next();
            while(it2.hasNext()){
                Vertex v2=it2.next();
                if(g.adjacencySetMap.get(v1.id).contains(v2.id)&&g.adjacencySetMap.get(v1.id).contains(v2.id)==false){
                    return false;
                }
                if(g.adjacencySetMap.get(v2.id).contains(v1.id)&&g.adjacencySetMap.get(v2.id).contains(v1.id)==false){
                    return false;
                }
            }
        }
        return true;

    }

    Graph [] getSubgraphWithHoles(double percentObstacle){
        Graph[] subgraph=getSubgraph();
        int verticesToRemove = (int)(subgraph[0].verticeSet.size()*percentObstacle);
        while(verticesToRemove > 0){
			int vToRemove =  (int)(Math.random()*subgraph[0].verticeSet.size());
			
			Vertex v=new ArrayList<Vertex>(subgraph[0].verticeSet).get(vToRemove);
			if(subgraph[0].idVertexMap.containsKey(v.id)){
				if(subgraph[0].isConnectedWithoutVertex(v.id)){
					subgraph[0].removeVertex(v);
					verticesToRemove --;
				}
			}
        }
        subgraph[0].vertices=subgraph[0].verticeSet.toArray(new Vertex[0]);
        
        int verticesToRemove1 = (int)(subgraph[1].verticeSet.size()*percentObstacle);
        while(verticesToRemove1 > 0){
			int vToRemove =  (int)(Math.random()*subgraph[1].verticeSet.size());
			Vertex v=new ArrayList<Vertex>(subgraph[1].verticeSet).get(vToRemove);
			if(subgraph[1].idVertexMap.containsKey(v.id)){
				if(subgraph[1].isConnectedWithoutVertex(v.id)){
					subgraph[1].removeVertex(v);
					verticesToRemove1 --;
				}
			}
        }
        subgraph[1].vertices=subgraph[1].verticeSet.toArray(new Vertex[0]);

        try{
            save_edges(subgraph[0], "subgraph1.txt");
            save_edges(subgraph[1], "subgraph2.txt");
         
        }
        catch(Exception e){

        }
        return subgraph;
    }

    Graph [] getSubgraph(){
        Graph[] subgraph=new Graph[2];
        for(int i=0;i<2;i++){
            subgraph[i]=new Graph();
        }
        int mid=(int)(cols/2.0);
        for(int i=0;i<mid-w;i++){
            for(int j=0;j<rows;j++){
                List<Integer> nbr=new ArrayList<Integer>();
                int vid=get_id(i,j);
                if(i>0){
                    nbr.add(get_id(i-1, j));              
                }
                if(i<mid-w-1){
                    nbr.add(get_id(i+1,j));
                }
                if(j<rows-1){
                    nbr.add(get_id(i,j+1));
                }
                if(j>0){
                    nbr.add(get_id(i,j-1));
                }
                int[] nbarray=nbr.stream().mapToInt(Integer::valueOf).toArray();
                subgraph[0].addVertex(vid,nbarray,new int[]{i,j});
                subgraph[0].VidList.add(Integer.valueOf(vid));
            }  
        }
        subgraph[0].finishBuildingGraph();

        for(int i=mid+w+1;i<cols;i++){
            for(int j=0;j<rows;j++){
                List<Integer> nbr=new ArrayList<Integer>();
                int vid=get_id(i,j);
                if(i>mid+w+1){
                    nbr.add(get_id(i-1, j));              
                }
                if(i<cols-1){
                    nbr.add(get_id(i+1,j));
                }
                if(j<rows-1){
                    nbr.add(get_id(i,j+1));
                }
                if(j>0){
                    nbr.add(get_id(i,j-1));
                }
                int[] nbarray=nbr.stream().mapToInt(Integer::valueOf).toArray();
                subgraph[1].addVertex(vid,nbarray,new int[]{i,j});
                subgraph[1].VidList.add(Integer.valueOf(vid));
            }  
        }
        subgraph[1].finishBuildingGraph();
        
    
        try{
            save_edges(subgraph[0], "subgraph1.txt");
            save_edges(subgraph[1], "subgraph2.txt");
         
        }
        catch(Exception e){

        }
        return subgraph;

    }

    Graph [] getBufferZoneWithHoles(double percentObstacle,int w,int increment){
        Graph[]bufferZone=getBufferZone(w, increment);
        Graph tmp=Graph.concatenate(Graph.concatenate(subgraph[0], bufferZone[0]),Graph.concatenate(subgraph[1],bufferZone[1]));
        int verticesToRemove = (int)(bufferZone[0].verticeSet.size()*percentObstacle);
        while(verticesToRemove > 0){
			int vToRemove =  (int)(Math.random()*bufferZone[0].verticeSet.size());
			
			Vertex v=new ArrayList<Vertex>(bufferZone[0].verticeSet).get(vToRemove);
			if(bufferZone[0].idVertexMap.containsKey(v.id)){
              
				if(tmp.isConnectedWithoutVertex(v.id)){
                    bufferZone[0].removeVertex(v);
                    tmp.removeVertex(v);
					verticesToRemove --;
				}
			}
        }
        bufferZone[0].vertices=bufferZone[0].verticeSet.toArray(new Vertex[0]);
        
        int verticesToRemove1 = (int)(bufferZone[1].verticeSet.size()*percentObstacle);
        while(verticesToRemove1 > 0){
			int vToRemove =  (int)(Math.random()*bufferZone[1].verticeSet.size());
			Vertex v=new ArrayList<Vertex>(bufferZone[1].verticeSet).get(vToRemove);
			if(bufferZone[1].idVertexMap.containsKey(v.id)){
				if(tmp.isConnectedWithoutVertex(v.id)){
                    bufferZone[1].removeVertex(v);
                    tmp.removeVertex(v);
					verticesToRemove1 --;
				}
			}
        }
        bufferZone[1].vertices=bufferZone[1].verticeSet.toArray(new Vertex[0]);

        try{
            save_edges(bufferZone[0], "b1.txt");
            save_edges(bufferZone[1], "b2.txt");
         
        }
        catch(Exception e){

        }
        return bufferZone;

    }

    Graph [] getBufferZone(int w,int increment){
        Graph[] bufferZone=new Graph[2];
        for(int i=0;i<2;i++){
            bufferZone[i]=new Graph();
        }
        int mid=(int)(cols/2.0);
        for(int i=mid-w;i<=mid+w;i++){
            for(int j=0;j<rows;j+=1){
                if((j/increment)%2==0)
                    continue;
                List<Integer> nbr=new ArrayList<Integer>();
                int vid=get_id(i,j);
                if(i<mid+w){
                    nbr.add(get_id(i+1, j)); 
                }
                             
                if(i>mid-w){
                    nbr.add(get_id(i-1,j));
                }
                if(((j+1)/increment)%2!=0&&j<rows-1){
                    nbr.add(get_id(i,j+1));
                }
                if(((j-1)/increment)%2!=0&&j>0){
                    nbr.add(get_id(i,j-1));
                }
                int[] nbarray=nbr.stream().mapToInt(Integer::valueOf).toArray();
                bufferZone[0].addVertex(vid,nbarray,new int[]{i,j});
                bufferZone[0].VidList.add(vid);
        
            }  
        }
        bufferZone[0].finishBuildingGraph();

        for(int i=mid-w;i<=mid+w;i++){
            for(int j=0;j<rows;j+=1){
                if((j/increment)%2==1)
                    continue;
                List<Integer> nbr=new ArrayList<Integer>();
                int vid=get_id(i,j);
                if(i<mid+w){
                    nbr.add(get_id(i+1, j)); 
                }
                             
                if(i>mid-w){
                    nbr.add(get_id(i-1,j));
                }
                if(((j+1)/increment)%2!=1&&j+1<rows){
                    nbr.add(get_id(i,j+1));
                }
                if(((j-1)/increment)%2!=1&&j>0){
                    nbr.add(get_id(i,j-1));
                }
                int[] nbarray=nbr.stream().mapToInt(Integer::valueOf).toArray();
                bufferZone[1].addVertex(vid,nbarray,new int[]{i,j});
                bufferZone[1].VidList.add(vid);
            }  
        }
        bufferZone[1].finishBuildingGraph();

         
        try{
            save_edges(bufferZone[0], "b1.txt");
            save_edges(bufferZone[1], "b2.txt");
            save_edges(Graph.concatenate( subgraph[0],bufferZone[1]),"subg1.txt");
            save_edges(Graph.concatenate( subgraph[1],bufferZone[0]),"subg2.txt");
        
           // save_edges(Graph.concatenate(bufferZone[2], Graph.concatenate(subgraph[1],bufferZone[1])),"cong.txt");
        }
        catch(Exception e){
            System.out.println("Exception. Saving buffer zone gets error");
            System.out.printf("*********\n");
        }
        return bufferZone;
    }

}