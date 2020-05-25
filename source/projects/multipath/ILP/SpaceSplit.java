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

class swappedResult{
    int [][]sg=new int[2][0];
    long T=0;
}

class SubGraph{
    Graph body=null;
    Graph[] bufferZone1=new Graph[4];//0--left,1--right,3--down,4--up
    Graph[] bufferZone2=new Graph[4];
 
    Graph phase1_graph=null;
    Graph phase2_graph=null;

    Graph allgraph=null;

    int label=0;

    

    double [][] dist1=null;
    double [][] dist2=null;



    
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

    public Graph get_subgraph1(){
        Graph tmp=new Graph();
        for(int i=0;i<4;i++){
            tmp=Graph.concatenate(tmp,bufferZone1[i]);
        }
        if(phase1_graph==null){
            phase1_graph=Graph.concatenate(body,tmp);
        }
        return phase1_graph;
    }

    

    public Graph get_subgraph2(){
        Graph tmp=new Graph();
        for(int i=0;i<4;i++){
            tmp=Graph.concatenate(tmp,bufferZone2[i]);
        }
        if(phase2_graph==null){
            phase2_graph=Graph.concatenate(body,tmp);
        }
        return phase2_graph;
    }

    public Graph get_all_graph(){
        Graph tmp=new Graph();
        for(int i=0;i<4;i++){
            tmp=Graph.concatenate(tmp,bufferZone1[i]);
            tmp=Graph.concatenate(tmp,bufferZone2[i]);
        }
        if(allgraph==null){
            allgraph=Graph.concatenate(body,tmp);
        }
        return allgraph;
    }


    public void preprocess(){
      //  get_subgraph1();
      //  get_subgraph2();
        
      //  dist1=floyd(phase1_graph);
     //   dist2=floyd(phase2_graph);
        
    }

    public double trueDistance(int s,int g,int phase){
        
        int i=0,j=0;
        if(phase==0){
            for(i=0;i<phase1_graph.vertices.length;i++){
                if(phase1_graph.vertices[i].id==s) break;
            }
            for(j=0;j<phase1_graph.vertices.length;j++){
                if(phase1_graph.vertices[j].id==g) break;
            }
            return dist1[i][j];
        }
        else{
            for(i=0;i<phase2_graph.vertices.length;i++){
                if(phase2_graph.vertices[i].id==s) break;
            }
            for(j=0;j<phase2_graph.vertices.length;j++){
                if(phase2_graph.vertices[j].id==g) break;
            }
            return dist2[i][j];
        }
        
       
    }
}


class SpaceSplit{

    int rows;
    int cols;
    int numAgents;
    int kSplit=0;

    int n=0;

    

    HashSet<Integer> obstacles=new HashSet<Integer>();

    public void get_obstacles(Graph g){
        for(int i=0;i<g.obsList.size();i++){
            ArrayList<Integer> obs=g.obsList.get(i);
            int x=obs.get(0);
            int y=obs.get(1);
            obstacles.add(get_id(x, y));
        }
    }
    public int [][]tmp_sg=null;
    
    ///////    Buffer Zone Size   /////////
    private int w=3;
    private int d=3;
    ///////////////////////////////////////

    int[][]sg=null;

    long subMakeSpan=0;

    SubGraph[] subgraphs=null;
    Graph graph=null;

    long makespanLb=0;

    int []current=null;

    public long getMakespanLb(){
        Graph g=get_graph();

        return PathFinder.getMakespanLowerBound(g, sg[0], sg[1]);
    }

    
    public void set_block_size(int w,int d){
        this.w=w;
        this.d=d;
    }

    public Graph get_graph(){
        Graph tmp=new Graph();
       
        int num=0;
        for(int i=0;i<subgraphs.length;i++){
           
            num+=(subgraphs[i].get_subgraph2()).verticeSet.size();
            tmp=Graph.concatenate(tmp,subgraphs[i].get_subgraph2());
     
        }
       // tmp.printGraphAsAdjacenyListGT();
        try{
            save_edges(tmp, "./debug/debug_all_graph.txt");
        }

        catch(Exception e){
            e.printStackTrace();
        }
        
        System.out.printf("%d  %d\n",tmp.verticeSet.size(),num);
        return tmp;
        
    }

    private int [] getSubGraphLabel(int id){
        int y=id%kSplit;
        int x=id/kSplit;
        return new int[]{x,y};
    }



    public void save_edges(Graph g, String filename)throws Exception{
        //System.out.println("////////////////////Begin saving///////////////");
        File filex=new File(filename);
        PrintStream ps = null ; 
        ps = new PrintStream(new FileOutputStream(filex)) ;
        if(g.vertices==null){
            return;
        }
        for(int i = 0; i < g.vertices.length; i ++){
            Vertex vi=g.vertices[i];
            Integer[] vIds=null;
            try{
                vIds = g.adjacencySetMap.get(vi.id).toArray(new Integer[0]);
            }
            catch(Exception e){
                vIds=new Integer[1];
                vIds[0]=vi.id;
            }
            
			for(int av = 0; av < vIds.length; av++){
                int []vj=get_vertex(vIds[av]);
				if(vIds[av] != i){
                    ps.printf("%d %d %d %d\n",vi.getIX(),vi.getIY(),vj[0],vj[1]);

				}
			}
        }
        ps.close();
     
    }

    Graph add_obstacles(Graph g,HashSet<Integer>obs){
        Graph tmp=Graph.copyGraph(g);
        Iterator<Vertex> it=g.verticeSet.iterator();
        while(it.hasNext()){
            Vertex v=it.next();
            int x=v.getIX();
            int y=v.getIY();
          
            if(obs.contains(v.id)){
                tmp.removeVertex(v);
            }
        }
        tmp.vertices = tmp.verticeSet.toArray(new Vertex[0]);
        return tmp;
    }

    Graph add_obstacles(Graph g,double percentObstacle){
        int verticesToRemove = (int)(g.verticeSet.size()*percentObstacle);
        while(verticesToRemove > 0){
			int vToRemove =  (int)(Math.random()*g.verticeSet.size());
			
			Vertex v=new ArrayList<Vertex>(g.verticeSet).get(vToRemove);
			if(g.idVertexMap.containsKey(v.id)){
				if(g.isConnectedWithoutVertex(v.id)){
					g.removeVertex(v);
					verticesToRemove --;
				}
			}
        }
        g.vertices=g.verticeSet.toArray(new Vertex[0]);
        return g;
    }
    

    public int get_id(int x,int y){
        return x*rows+y;
    }


    public int [] get_vertex(int vid){
        int x;
        int y;
        x=(int)(vid/(rows));
        y=(int)(vid%(rows));
        return new int[]{x,y};
    }

    public SpaceSplit(int rows,int cols,int numAgents,int kSplit){
        this.rows=rows;
        this.cols=cols;
        this.numAgents=numAgents;
        this.kSplit=kSplit;
       // paths=new int[numAgents][4];
    }

    public SpaceSplit(Problem p,int kSplit){
        this.rows=p.graph.rows;
        this.cols=p.graph.columns;
        //this.numAgents=p.sg[0].length;
        this.kSplit=kSplit;
        this.sg=p.sg;
        this.set_block_size(2,2);
        get_obstacles(p.graph);
        this.getSubGraphs();
        
    
       // paths=new int[numAgents][4];
    }

    public boolean reachedGoals(int[][] startAndgoals){
        boolean if_reached=true;
        int count=0;
        for(int i=0;i<startAndgoals[0].length;i++){
            if(startAndgoals[0][i]!=startAndgoals[1][i]){
                if_reached=false;
                count++;
                int[] start=get_vertex(startAndgoals[0][i]);
                int[] goal=get_vertex(startAndgoals[1][i]);
              //  System.out.printf("%d(%d,%d)--->%d(%d,%d)\n",startAndgoals[0][i],start[0],start[1],startAndgoals[1][i],goal[0],goal[1]);
            }

        }
        System.out.printf("%f percent reached\n",1.0-count/(double)startAndgoals[0].length);
        return if_reached;

    }

    public Graph addBlocks(Graph gx,int left,int right,int down,int up){
        for(int i=left;i<=right;i++){
            for(int j=down;j<=up;j++){
                List<Integer> nbr=new ArrayList<Integer>();
                int vid=get_id(i,j);
                if(i>left){
                    nbr.add(get_id(i-1, j));              
                }
                if(i<right){
                    nbr.add(get_id(i+1,j));
                }
                if(j<up){
                    nbr.add(get_id(i,j+1));
                }
                if(j>down){
                    nbr.add(get_id(i,j-1));
                }
                int[] nbarray=nbr.stream().mapToInt(Integer::valueOf).toArray();
                gx.addVertex(vid,nbarray,new int[]{i,j});
                gx.VidList.add(Integer.valueOf(vid));
            }  
        }
        gx.finishBuildingGraph();
        return gx;
    }
    private void printStartAndGoals(int[][] sg){
        for(int i=0;i<sg[0].length;i++){
            int[] s=get_vertex(sg[0][i]);
            int[] g=get_vertex(sg[1][i]);
            System.out.printf("%d(%d,%d)-->%d(%d,%d)\n ", sg[0][i],s[0],s[1],sg[1][i],g[0],g[1]);
        }
    }


    public SubGraph divideSubGraph(int left,int right,int down,int up,int w, int d){
        SubGraph subg=new SubGraph();
        subg.body=new Graph();
        int l=0,r=0,u=0,dd=0;
       // System.out.printf("%d %d %d %d\n",left,right,down,up);
        /////     body part      //////////
        subg.body=addBlocks(subg.body,left,right-1,down,up-1);
       // subg.body=add_obstacles(subg.body, 0.00);
      
        subg.body=add_obstacles(subg.body, obstacles);
       
        /////////////////////////////////////////
        ///////////////// left   buffer zone //////////////////////////////
        subg.bufferZone1[0]=new Graph();
        subg.bufferZone2[0]=new Graph();
        if(left-w>0){
            l=left-w;
            r=left+w;
            int downmost=0;
            int upmost=0;
            if(down==0)downmost=0;
            else downmost=down+w+d;
            if(up==rows-1)upmost=rows-1;
            else upmost=up-w-d;
            for(int j=downmost;j<=upmost;j+=2*d){
                dd=j;
                u=j+d-1;
                subg.bufferZone1[0]=addBlocks(subg.bufferZone1[0], l, r, dd, u);
                subg.bufferZone1[0]=add_obstacles(subg.bufferZone1[0], obstacles);
                //subg.bufferZone1[0]=add_obstacles(subg.bufferZone1[0], 0.05);
                
            }
           // System.out.printf("%d %d %d %d\n",l,r,u,dd);
            for(int j=downmost+d;j<=upmost;j+=2*d){
                dd=j;
                u=j+d-1;
                subg.bufferZone2[0]=addBlocks(subg.bufferZone2[0], l, r, dd, u);
                subg.bufferZone2[0]=add_obstacles(subg.bufferZone2[0], obstacles);
                //subg.bufferZone2[0]=add_obstacles(subg.bufferZone2[0], 0.05);
            }
        }

        
        ///////////////////////////////////////////////////////////////////

        /////////////////right buffer zone /////////////////////////////////
        subg.bufferZone1[1]=new Graph();
        subg.bufferZone2[1]=new Graph();
       
        if(right+w<cols){
            l=right-w;
            r=right+w;
            int downmost=0;
            int upmost=0;
            if(down==0)downmost=0;
            else downmost=down+w+d;
            if(up==rows-1)upmost=rows-1;
            else upmost=up-w-d;
            for(int j=downmost+d;j<=upmost;j+=2*d){
                dd=j;
                u=j+d-1;
                subg.bufferZone1[1]=addBlocks(subg.bufferZone1[1], l, r, dd, u);
                subg.bufferZone1[1]=add_obstacles(subg.bufferZone1[1], obstacles);
               // subg.bufferZone1[1]=add_obstacles(subg.bufferZone1[1], 0.05);
            }

            for(int j=downmost;j<=upmost;j+=2*d){
                dd=j;
                u=j+d-1;
                subg.bufferZone2[1]=addBlocks(subg.bufferZone2[1], l, r, dd, u);
                subg.bufferZone2[1]=add_obstacles(subg.bufferZone2[1], obstacles);
               // subg.bufferZone2[1]=add_obstacles(subg.bufferZone2[1], 0.05);
            }
        }

        //////////////////////////////////////////////////////////////////////

        /////////////////up buffer zone /////////////////////////////////////
        subg.bufferZone1[3]=new Graph();
        subg.bufferZone2[3]=new Graph();
        u=up+w;
        dd=up-w;
        
        if(u<rows){
            int leftmost=0;
            int rightmost=0;
            if(left==0)leftmost=0;
            else leftmost=left+w+d;
            if(right==cols-1)rightmost=cols-1;
            else rightmost=right-w-d;
            for(int j=leftmost;j<=rightmost;j+=2*d){
                l=j;
                r=j+d-1;
                subg.bufferZone1[3]=addBlocks(subg.bufferZone1[3], l, r, dd, u);
                subg.bufferZone1[3]=add_obstacles(subg.bufferZone1[3], obstacles);
                //subg.bufferZone1[3]=add_obstacles(subg.bufferZone1[3], 0.05);
            }

            for(int j=leftmost+d;j<=rightmost;j+=2*d){
                l=j;
                r=j+d-1;
                subg.bufferZone2[3]=addBlocks(subg.bufferZone2[3], l, r, dd, u);
                subg.bufferZone2[3]=add_obstacles(subg.bufferZone2[3], obstacles);
                //subg.bufferZone2[3]=add_obstacles(subg.bufferZone1[3], 0.05);
            }
        }

        ////////////////////////////////////////////////////////////////////////

        //////////////////down buffer zone//////////////////////////////////////
        subg.bufferZone1[2]=new Graph();
        subg.bufferZone2[2]=new Graph();
        u=down+w;
        dd=down-w;
        if(dd>0){
            int leftmost=0;
            int rightmost=0;
            if(left==0)leftmost=0;
            else leftmost=left+w+d;
            if(right==cols-1)rightmost=cols-1;
            else rightmost=right-w-d;
            for(int j=leftmost+d;j<=rightmost;j+=2*d){
                l=j;
                r=j+d-1;
                subg.bufferZone1[2]=addBlocks(subg.bufferZone1[2], l, r, dd, u);
                subg.bufferZone1[2]=add_obstacles(subg.bufferZone1[2], obstacles);
               // subg.bufferZone1[2]=add_obstacles(subg.bufferZone1[2], 0.05);
            }

            for(int j=leftmost;j<=rightmost;j+=2*d){
                l=j;
                r=j+d-1;
                subg.bufferZone2[2]=addBlocks(subg.bufferZone2[2], l, r, dd, u);
                subg.bufferZone2[2]=add_obstacles(subg.bufferZone2[2], obstacles);
               // subg.bufferZone2[2]=add_obstacles(subg.bufferZone2[2], 0.05);
            }
        }
     

        /////////////////////////////////////////////////////////////////////////
        for(int i=0;i<4;i++){
            subg.body=Graph.subtract(subg.body,subg.bufferZone1[i]);
            subg.body=Graph.subtract(subg.body,subg.bufferZone2[i]);
        }
        
       
        try{
            save_edges(subg.body, "./debug/debug_body"+n+".txt");
            
           // save_edges(subg.bufferZone2,"./debug_bf2.txt");
            //save_edges(Graph.concatenate(subg.body,subg.bufferZone1), "./debug_g1.txt");
            for(int i=0;i<4;i++){
                save_edges(subg.bufferZone1[i], "./debug/debug_bz"+"_"+n+"_"+i+".txt");
            }
            
            n++;
            
        }

        catch(Exception e){
            System.out.println("?????????");
            e.printStackTrace();
        }
        return subg;
    }

    private double maxDistance(double a,double b){
        return Math.max(a-b,0);
    }

    private double manhattanDist(int []p1,int[] p2){
        return Math.abs(p1[0]-p2[0])+Math.abs(p1[1]-p2[1]);
    }

    private double trueDist(Graph g, int []p1, int []p2){
        
        int start=get_id(p1[0], p1[1]);
        int goal=get_id(p2[0], p2[1]);

        Path p=PathFinder.findOneShortestPath(g, start, goal, null);

        return p.vertexList.size();
    }

    int SubGraphAllocate(int s,int g, SubGraph subg,HashSet<Integer> hasUsed,double threshold1,double threshold2,int phase,boolean ifend){
        Graph tmp=null;
        double lambda=0.1;
        if(ifend==true)lambda=10.0;
        if(phase==0){
            tmp=subg.get_subgraph1();
        }
        else{
            tmp=subg.get_subgraph2();
        }
        Iterator<Vertex>it=tmp.verticeSet.iterator();
        double min_dist=191000;
        int cur_id=-21;
        //Graph tmp=Graph.concatenate(subg,bufferZone);
        int[]start=get_vertex(s);
        int[]goal=get_vertex(g);
        while(it.hasNext()){
             Vertex v1=it.next();
             int[] mid=new int[]{v1.getIX(),v1.getIY()};
            // double s_b_dist=subg.trueDistance(s, v1.id, phase);
             double s_b_dist=manhattanDist(start, mid);
             double g_b_dist=manhattanDist(mid, goal);
             double dist=Math.max(maxDistance(s_b_dist,threshold1),maxDistance(g_b_dist, threshold2))+lambda*g_b_dist;
             if(dist<min_dist){
                 if(hasUsed.contains(v1.id)==false){
                     cur_id=v1.id;
                     min_dist=dist;
                 }
             }
         }
         Vertex v=subg.body.idVertexMap.get(cur_id);
       
         hasUsed.add(cur_id);
         return cur_id;
     }

     int MoveOutAllocate(int s,int g, SubGraph subg,HashSet<Integer> hasUsed,double threshold1,double threshold2,int phase,boolean ifend){
        if(ifend==true)System.out.printf("oooopss.....\n");
        Graph tmp=null;
        if(phase==0){
            tmp=subg.get_subgraph1();
          //  bf=subg.bufferZone1[action];
        }
        else{
            tmp=subg.get_subgraph2();
            //bf=subg.bufferZone2[action];
        }
        Iterator<Vertex>it=subg.body.verticeSet.iterator();
        double lambda=0.1;
        if(ifend==true)lambda=5.0;
        double min_dist=191000;
        int cur_id=0;
        //Graph tmp=Graph.concatenate(subg,bufferZone);
        int[]start=get_vertex(s);
        int[]goal=get_vertex(g);
        while(it.hasNext()){
             Vertex v1=it.next();
             int[] mid=new int[]{v1.getIX(),v1.getIY()};
           //  double s_b_dist=subg.trueDistance(s, v1.id, phase);
             double s_b_dist=manhattanDist(start, mid);
             double g_b_dist=manhattanDist(mid, goal);
             
             double dist=Math.max(maxDistance(s_b_dist,threshold1),maxDistance(g_b_dist, threshold2))+1.1*g_b_dist;
             if(dist<min_dist){
                 if(hasUsed.contains(v1.id)==false){
                     cur_id=v1.id;
                     min_dist=dist;
                 }
             }
         }
         Vertex v=subg.body.idVertexMap.get(cur_id);
       
         hasUsed.add(cur_id);
         return cur_id;
     }

    int BufferZoneAllocate(int s,int g, SubGraph subg,HashSet<Integer> hasUsed,double threshold1,double threshold2,int phase,boolean ifend){
      //  if(ifend==true)return g;
        Graph tmp=null;
        Graph bf=null;
        //System.out.println("What the fuck");
        int action=decideDirection(subgraphs, s, g, phase);
        double lambda=0.1;
        if(ifend==true)lambda=5.0;
        if(phase==0){
            tmp=subg.get_subgraph1();
            bf=subg.bufferZone1[action];
        }
        else{
            tmp=subg.get_subgraph2();
            bf=subg.bufferZone2[action];
        }
        Iterator<Vertex>it=bf.verticeSet.iterator();
        double min_dist=191000;
        int cur_id=-21;
        //Graph tmp=Graph.concatenate(subg,bufferZone);
        int[]start=get_vertex(s);
        int[]goal=get_vertex(g);
        while(it.hasNext()){
             Vertex v1=it.next();
             int[] mid=new int[]{v1.getIX(),v1.getIY()};
             //double s_b_dist=subg.trueDistance(s, v1.id, phase);
             double s_b_dist=manhattanDist(start, mid);
             double g_b_dist=manhattanDist(mid, goal);
             double dist=3*Math.max(maxDistance(s_b_dist,threshold1),maxDistance(g_b_dist, threshold2))+lambda*g_b_dist;
             if(dist<min_dist){
                 if(hasUsed.contains(v1.id)==false){
                     cur_id=v1.id;
                     min_dist=dist;
                 }
             }
             
         }
        // System.out.printf("%d th subgraph %d  (%d,%d)----->(%d,%d)\n",subg.label,action,start[0],start[1],goal[0],goal[1]);
         Vertex v=bf.idVertexMap.get(cur_id);
         if(cur_id<0){
             System.out.printf("%d th subgraph %d  (%d,%d)----->(%d,%d)\n",subg.label,action,start[0],start[1],goal[0],goal[1]);
             System.out.println(bf.verticeSet.size());
         }
         //int[]mid=get_vertex(v.id);
         hasUsed.add(cur_id);
         return cur_id;
     }


    public void getSubGraphs(){
        int k=0;
        
        List<SubGraph> tmp_subg=new ArrayList<SubGraph>();
        int di=cols/kSplit;
        int dj=rows/kSplit;
        for(int i=0;i<kSplit;i++){
            for(int j=0;j<kSplit;j++){
                int left=i*di;
                int right=Math.min(left+di,cols);
                int down=j*dj;
                int up=Math.min(down+dj,rows);
                tmp_subg.add(divideSubGraph(left, right, down, up, w, d));
            }
        }
        subgraphs=new SubGraph[tmp_subg.size()];
        for(int i=0;i<subgraphs.length;i++){
            subgraphs[i]=tmp_subg.get(i);
            subgraphs[i].label=i;
            subgraphs[i].preprocess();

        }
        
    }

    private int decideDirection(SubGraph[] subg, int s,int g,int phase){
        int[] start=null;
        int[] goal=null;
        Graph tmp;
        int []vs=get_vertex(s);
        int []vg=get_vertex(g);
        
     //   System.out.println("start="+s+"("+vs[0]+","+vs[1]+")"+" goal="+g+"("+vg[0]+","+vg[1]+")");

        
        
        for(int i=0;i<subg.length;i++){
            if(phase==0){
                tmp=subg[i].get_subgraph1();
            }
            else{
                tmp=subg[i].get_subgraph2();
            }
            
            if(tmp.adjacencySetMap.containsKey(s)){
                start=getSubGraphLabel(i);
            }
            if(tmp.adjacencySetMap.containsKey(g)){
                goal=getSubGraphLabel(i);
              
            }
        }

        if(start==null){
            throw new OutOfMemoryError("start");
        }

        if(goal==null){
            throw new OutOfMemoryError("goal");
        }
        
        
        double dx=goal[0]-start[0]+1.0/cols*(vg[0]-vs[0]);
        double dy=goal[1]-start[1]+1.0/cols*(vg[1]-vs[1]);
        if(Math.abs(dx)>Math.abs(dy)){
            if(dx>=0) return 1;
            else  return 0;
        }
        else{
            if(dy>=0) return 3;
            else return 2;
        }

    }

    public int[] sortByPaths(Graph g,List<Integer> start, List<Integer> goal){
        int []sArray=start.stream().mapToInt(Integer::valueOf).toArray();
        int []gArray=goal.stream().mapToInt(Integer::valueOf).toArray();
        //Path paths[] = PathFinder.findShortestPaths(g, sArray, gArray);
        int[] dist=new int[sArray.length];
        for(int i=0;i<dist.length;i++){
            Vertex v1=g.idVertexMap.get(sArray[i]);
            Vertex v2=g.idVertexMap.get(gArray[i]);

            dist[i]=(int)manhattanDist(new int[]{v1.getIX(),v2.getIY()}, new int[]{v2.getIX(),v2.getIY()});
        }
	
		// Sort all paths by their lengths in descending order - long paths should be processed first
		SortedMap<Integer, Vector<Integer>> lengthIdMap = new TreeMap<Integer, Vector<Integer>>();
		for(int i = 0; i < sArray.length; i ++){
    //		Vector<Integer> indexSet = lengthIdMap.get(-paths[i].vertexList.size());
            Vector<Integer> indexSet = lengthIdMap.get(dist[i]);
			if(indexSet == null){
				indexSet = new Vector<Integer>();
				lengthIdMap.put(dist[i], indexSet);
			}
			indexSet.add(i);
		}
		Integer[] keyArray = lengthIdMap.keySet().toArray(new Integer[0]);
		int[] vArray = new int[sArray.length];
		int vIndex = 0;
		for(int i = 0; i < keyArray.length; i ++){
			Integer[] indexArray = lengthIdMap.get(keyArray[i]).toArray(new Integer[0]);
			for(int in = 0; in < indexArray.length; in ++){
				vArray[vIndex++] = indexArray[in];
			}
        }
     //   System.out.printf("%d %d %d\n",start.size(),goal.size(),vArray.length);
    //    for(int i=0;i<vArray.length;i++){
    //        System.out.printf("%d ",vArray[i]);
    //    }
    //    System.out.println();
    //    System.exit(0);
        return vArray;

    }


    public swappedResult swap(SubGraph subg,int [][]sg,double threshold1,double threshold2,int phase,boolean ifend,boolean advanced){   
        swappedResult result=new swappedResult();
        current=new int[sg[0].length];
        Graph tmp=null;
    
        if(phase==0){
            tmp=subg.get_subgraph1();
        }
        else{
            tmp=subg.get_subgraph2();
        }
        
        List<Integer> ss=new ArrayList<Integer>();
        List<Integer> gg=new ArrayList<Integer>();
        
        HashSet<Integer>hasUsed=new HashSet<Integer>();
        HashMap<Integer,Integer> idMap=new HashMap<>();
        result.sg[0]=new int[sg[0].length];
        result.sg[1]=new int[sg[1].length];
        for(int i=0;i<sg[0].length;i++){
            int s=sg[0][i];
            int g=sg[1][i];
           // System.out.printf("%d--->%d\n",s,g);
            if(tmp.idVertexMap.containsKey(s)){
                ss.add(s);
                gg.add(g);
            }
            else{
                result.sg[0][i]=s;
                result.sg[1][i]=g;
            }
            idMap.put(s,i);

        }
      //  System.out.printf("++++++++++++++++++++++Number of robots=%d,,,,,%d,,,,,,,,,,,%d\n",ss.size(),tmp.idVertexMap.size(),sg[0].length);
        int[] vArray=sortByPaths(get_graph(), ss, gg);
        int[] mids=new int[ss.size()];

        for(int i=0;i<vArray.length;i++){
            int vid=vArray[i];
            int g=gg.get(vid);
            int s=ss.get(vid);
            int mid=-21;
            
            
            if(tmp.idVertexMap.containsKey(g)){
                mid=SubGraphAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                    
            }
            else{
                Graph alg=subg.get_all_graph();
                if(alg.idVertexMap.containsKey(g)){
                    mid=MoveOutAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                }
                else{
                    mid=BufferZoneAllocate(s, g, subg, hasUsed, threshold1, threshold2, phase,ifend);  
                    if(mid<0){
                        mid=SubGraphAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                    }                    
                 }
            }
            mids[vid]=mid;
            int index=idMap.get(s);
            current[index]=mid;
            result.sg[0][index]=mid;
            result.sg[1][index]=g;
           
            
        }
     
        Problem p=new Problem();
        p.sg=new int[2][];
        p.sg[0]=ss.stream().mapToInt(Integer::valueOf).toArray();
        p.sg[1]=mids;

        p.graph=Graph.convertGraphGT(tmp,p.sg[0],p.sg[1]);
        
     
        System.out.printf("*********\n numAgents=%d\n",p.sg[0].length);
        long []result1 =Solve.solveProblemSuboptimal(p, false, true, 0, 300.0,3, false);
        if(result1!=null)result.T=result1[0];
        else result.T=0;
        System.out.printf("numAgents=%d,solved T= %d\n",ss.size(),result.T);
  
        return result;
    }

    public swappedResult swap(SubGraph subg,int [][]sg,double threshold1,double threshold2,int phase,boolean ifend){   
        swappedResult result=new swappedResult();
        current=new int[sg[0].length];
        Graph tmp=null;
    
        if(phase==0){
            tmp=subg.get_subgraph1();
        }
        else{
            tmp=subg.get_subgraph2();
        }
        //List<Integer> currentState=new ArrayList<Integer>();
        List<Integer> ss=new ArrayList<Integer>();
        List<Integer> gg=new ArrayList<Integer>();
        HashSet<Integer>hasUsed=new HashSet<Integer>();
        result.sg[0]=new int[sg[0].length];
        result.sg[1]=new int[sg[1].length];
        for(int i=0;i<sg[0].length;i++){
            int s=sg[0][i];
            int g=sg[1][i];
            
            int mid;
          
            if(tmp.idVertexMap.containsKey(s)){
                ss.add(s);
                if(tmp.idVertexMap.containsKey(g)){
                    
                    mid=SubGraphAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                    
                }
                else{
                    Graph alg=subg.get_all_graph();
                    if(alg.idVertexMap.containsKey(g)){
                        
                        mid=MoveOutAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                    }
                    else{
                      
                        mid=BufferZoneAllocate(s, g, subg, hasUsed, threshold1, threshold2, phase,ifend);  
                        if(mid<0){
                            mid=SubGraphAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                        }                    
                    }
                }
                gg.add(mid);
                current[i]=mid;
                result.sg[0][i]=mid;
            }
            else{
                result.sg[0][i]=s;
                current[i]=s;
            }
           
            result.sg[1][i]=g;
        }
        //print2dArray(result.sg);
        Problem p=new Problem();
        p.sg=new int[2][];
        p.sg[0]=ss.stream().mapToInt(Integer::valueOf).toArray();
        p.sg[1]=gg.stream().mapToInt(Integer::valueOf).toArray();

        p.graph=Graph.convertGraphGT(tmp,p.sg[0],p.sg[1]);
        
     
  //      System.out.printf("*********\n numAgents=%d\n",p.sg[0].length);
  //      if(p.sg[0].length==22){
  //          PathPlanner.printStartAndGoals(p.graph, p.sg[0], p.sg[1]);
   //     }
        long []result1 =Solve.solveProblemSuboptimal(p, false, true, 0, 300.0,4, false);
        if(result1!=null)result.T=result1[0];
        else result.T=0;
        System.out.printf("numAgents=%d,solved T= %d\n",ss.size(),result.T);
  
        return result;
    }

    public swappedResult swapECBS(SubGraph subg,int [][]sg,double threshold1,double threshold2,int phase,boolean ifend){   
        swappedResult result=new swappedResult();
        current=new int[sg[0].length];
        Graph tmp=null;
    
        if(phase==0){
            tmp=subg.get_subgraph1();
        }
        else{
            tmp=subg.get_subgraph2();
        }
        //List<Integer> currentState=new ArrayList<Integer>();
        List<Integer> ss=new ArrayList<Integer>();
        List<Integer> gg=new ArrayList<Integer>();
        HashSet<Integer>hasUsed=new HashSet<Integer>();
        result.sg[0]=new int[sg[0].length];
        result.sg[1]=new int[sg[1].length];
        for(int i=0;i<sg[0].length;i++){
            int s=sg[0][i];
            int g=sg[1][i];
            
            int mid;
          
            if(tmp.idVertexMap.containsKey(s)){
                ss.add(s);
                if(tmp.idVertexMap.containsKey(g)){
                    
                    mid=SubGraphAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                    
                }
                else{
                    Graph alg=subg.get_all_graph();
                    if(alg.idVertexMap.containsKey(g)){
                        
                        mid=MoveOutAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                    }
                    else{
                      
                        mid=BufferZoneAllocate(s, g, subg, hasUsed, threshold1, threshold2, phase,ifend);  
                        if(mid<0){
                            mid=SubGraphAllocate(s,g,subg, hasUsed,threshold1,threshold2,phase,ifend);
                        }                    
                    }
                }
                gg.add(mid);
                current[i]=mid;
                result.sg[0][i]=mid;
            }
            else{
                result.sg[0][i]=s;
                current[i]=s;
            }
           
            result.sg[1][i]=g;
        }
        //print2dArray(result.sg);
        Problem p=new Problem();
        p.sg=new int[2][];
        p.sg[0]=ss.stream().mapToInt(Integer::valueOf).toArray();
        p.sg[1]=gg.stream().mapToInt(Integer::valueOf).toArray();
        p.graph=tmp;
        p.graph.rows=rows;
        p.graph.columns=cols;
        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++){
                int vid=p.graph.getIdGT(i,j);
                if(p.graph.idVertexMap.containsKey(vid)==false){
                    p.graph.obsList.add(new ArrayList<>(Arrays.asList(i,j)));
                }
            }
        }
        
      
        long []result1=(new Ecbs()).ECBS_solve_suboptimal(p,3);
        if(result1!=null)result.T=result1[0];
        else result.T=0;
        System.out.printf("numAgents=%d,solved T= %d\n",ss.size(),result.T);
  
        return result;
    }

    public void clear(){
        this.sg=null;
        this.subMakeSpan=0;
    }

    public void parallelSort(SubGraph []subg,int [][]sg){
        int n=subg.length;
        long maxT=0;
        swappedResult tmp=new swappedResult();
        tmp.sg=new int[2][sg[0].length];
        for(int i=0;i<2;i++){
            for(int j=0;j<sg[0].length;j++){
                tmp.sg[i][j]=sg[i][j];
            }
        }
    
       
        long sumT=0;
        makespanLb=getMakespanLb();
        System.out.println("makespanLb="+makespanLb);
        int phase=0;
        double threshold1=makespanLb/(2.0*kSplit-1);
        double threshold2;
        for(int k=0;k<2*kSplit;k++){
            maxT=0;
            System.out.printf("Phase %d  begins\n\n\n", k);
            phase=k%2;
            threshold2=(1-k/(2.0*kSplit-1))*makespanLb;
            boolean ifend=false;
            if(k+1>=2*kSplit-1) ifend=true;
            for(int i=0;i<subg.length;i++){
                //tmp=swap(subg[i], tmp.sg, threshold1, threshold2, phase,ifend);
               tmp=swap(subg[i], tmp.sg, threshold1, threshold2, phase,ifend);
                if(tmp.T>maxT) maxT=tmp.T;
            }
            
            
            tmp_sg=tmp.sg;
            sumT+=maxT;
            if(reachedGoals(tmp_sg)){
                System.out.println("Instance solved successfully");
                break;
            }
            System.out.println();
        }
        subMakeSpan=sumT;
        System.out.printf("The final makespan is %d\n",sumT);
       
    
        System.out.println();
    }
    

    
    
    public void print2dArray(int[][] sg){
        System.out.println("**************************");
        for(int i=0;i<sg.length;i++){
            for(int j=0;j<sg[0].length;j++){
                System.out.printf("%4d ",sg[i][j]);
            }
            System.out.println();
        }
        System.out.println("**************************");
    }



}