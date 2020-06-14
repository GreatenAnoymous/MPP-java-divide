package projects.multipath.advanced;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;

import java.util.*;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.yaml.snakeyaml.Yaml;





public class yamlProblem{
    public Object yamlObj;

    public yamlProblem(Object yamlObj){
        this.yamlObj=yamlObj;
        Map<String,Object> mp1=(Map) yamlObj;
		Map<String,Object>	map=(Map<String,Object>) (mp1.get("map"));
		ArrayList<Integer> dim=(ArrayList<Integer>) (map.get("dimensions"));
		height=dim.get(0);
		width=dim.get(1);
		obstacles=(ArrayList<Object>) map.get("obstacles");
		starts=new ArrayList<>();
		goals=new ArrayList<>();
		ArrayList<Object> agents=(ArrayList<Object>) (mp1.get("agents"));
		for(int i=0;i<agents.size();i++){
			Map<String,Object> agent=(Map<String,Object>) agents.get(i);
			ArrayList<Integer> start=(ArrayList<Integer>) agent.get("start");
			ArrayList<Integer> goal=(ArrayList<Integer>) agent.get("goal");
			starts.add(start);
			goals.add(goal);

        }
    }
    
	public int height;
	public int width;
	public ArrayList<Object> obstacles;
	public ArrayList<ArrayList<Integer>> starts;
    public ArrayList<ArrayList<Integer>> goals;
    
    public static void saveOutPut(Problem p,long []result,String filename){
        File filex=new File(filename);
        PrintStream ps = null ;
        try{
            ps = new PrintStream(new FileOutputStream(filex,true)) ;
            if(result!=null&&result[0]>=0&&result[1]>=0)ps.println(p.sg[0].length+" "+result[0]+" "+result[1]+" "+result[3]);
            else ps.println(p.sg[0].length+" "+"NaN"+" "+"NaN"+" "+"NaN");
            ps.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

	public static void saveYaml(long[] result,String filename){
        Map<String, Map<String,Integer>> obj=new HashMap<>();
        Map<String,Integer> stat=new HashMap<>();
        stat.put("makespan",(int)result[0]);
        stat.put("makespanLB",(int)result[3]);
        stat.put("runtime",(int)result[1]);
        obj.put("statistics",stat);
        Yaml y=new Yaml();
        try{
            FileWriter writer = new FileWriter(filename);
            y.dump(obj,writer);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        


    }

    public Problem getProblem(){
        Problem p=new Problem();
        p.graph=Graph.create2DGridGraph(height, width, true);
        for(int i=0;i<obstacles.size();i++){
            ArrayList<Integer> obj=(ArrayList<Integer>)obstacles.get(i);
            p.graph.removeVertex(obj.get(0),obj.get(1));
        }
        p.graph.vertices = p.graph.verticeSet.toArray(new Vertex[0]);
        p.sg=new int[2][starts.size()];
        for(int i=0;i<starts.size();i++){
            ArrayList<Integer> s=starts.get(i);
            ArrayList<Integer> g=goals.get(i);
            p.sg[0][i]=p.graph.getId(s.get(0),s.get(1));
            p.sg[1][i]=p.graph.getId(g.get(0),g.get(1));
        }
        p.graph=Graph.convertGraph(p.graph, p.sg[0], p.sg[1]);
        return p;
    }

    public static void SaveMap(Graph g,String filename){
        File filex=new File(filename);
        PrintStream ps = null ;
        try{
            ps = new PrintStream(new FileOutputStream(filex)) ;
            ps.println("type octile");
            ps.println("height "+g.rows);
            ps.println("width "+g.columns);
            ps.println("map");
            for(int i=0;i<g.rows;i++){
                for(int j=0;j<g.columns;j++){
                    Iterator<Vertex> it=g.verticeSet.iterator();
                    boolean found=false;
                    while(it.hasNext()){
                        Vertex v=it.next();
                        if(v.getIX()==j&&v.getIY()==i){
                            found=true;
                        }
                    }
                    if(found){
                        ps.print(".");
                    }
                    else{
                        ps.print("@");
                    }
                }
                ps.println();
            }
        } 
        catch(Exception e){
            e.printStackTrace();
        }
        
    }

    public static void saveScenario(Problem p, String filename){
        File filex=new File(filename);
        PrintStream ps = null ;
        try{
            ps = new PrintStream(new FileOutputStream(filex)) ;
            ps.println("version 1");
            for(int i=0;i<p.sg[0].length;i++){
                ps.print(i);
                ps.print("      ");
                ps.print(p.graph.rows+"x"+p.graph.columns);
                ps.print("  ");
                ps.print(p.graph.rows);
                ps.print("  ");
                ps.print(p.graph.columns);
                ps.print("  ");
                Vertex vs=p.graph.idVertexMap.get(p.sg[0][i]);
                Vertex vg=p.graph.idVertexMap.get(p.sg[1][i]);
                ps.print(vs.getIX());
                ps.print("      ");
                ps.print(vs.getIY());
                ps.print("      ");
                ps.print(vg.getIX());
                ps.print("      ");
                ps.print(vg.getIY());
                ps.print("      ");
                ps.print(p.sg[0].length);


                ps.println();
            }
        } 
        catch(Exception e){
            e.printStackTrace();
        }
    }




    public static void   saveProblem(Problem p, String filename){
        File filex=new File(filename);
       
        PrintStream ps = null ;
        try{
            ps = new PrintStream(new FileOutputStream(filex)) ;
            ps.println("agents:");
            for(int i=0;i<p.sg[0].length;i++){
               
                Vertex vg=p.graph.idVertexMap.get(p.sg[1][i]);
                ps.println("-   goal: ["+vg.getIX()+","+vg.getIY()+"]");
                ps.println("    name: agent"+i);
                Vertex vs=p.graph.idVertexMap.get(p.sg[0][i]);
                ps.println("    start: ["+vs.getIX()+","+vs.getIY()+"]");

 
            }
            ps.println("map:");
            ps.println("    dimensions: ["+p.graph.columns+","+p.graph.rows+"]");
            ps.println("    obstacles: ");
            if(p.graph.obsList!=null){
                for(int i=0;i<p.graph.obsList.size();i++){
                    ArrayList<Integer> obs=p.graph.obsList.get(i);
                    ps.println("    - ["+obs.get(0)+","+obs.get(1)+"]");
                }
            }

           
           /* for(int i=0;i<p.graph.rows;i++){
                for(int j=0;j<p.graph.columns;j++){
                    Iterator<Vertex> it=p.graph.verticeSet.iterator();
                    boolean found=false;
                    while(it.hasNext()){
                        Vertex v=it.next();
                        if(v.getIX()==j&&v.getIY()==i){
                            found=true;
                        }
                    }
                    if(found){
                        continue;
                    }
                    else{
                        ps.println("    - ["+j+","+i+"]");
                    }
                }
                
            }*/
         
            ps.close();

            
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public  static long[] readOutputYaml(String filename){
     
        long[] result=new long[4];
        try{
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String str;
		
            
            List<Integer> path=null;
            int x=0,y=0;
            while ((str = in.readLine()) != null) {
                String[] strs=str.split("\\s+");

            //    for(int i=0;i<strs.length;i++){
             //       System.out.print(strs[i]+"@@@");
              //  }
              //  System.out.println();
                if(strs.length>1){                   
                    if(strs[1].equals("makespan:")){
                
                        result[0]=Integer.parseInt(strs[2]);

                    }
                    if(strs[1].equals("runtime:")){
                        result[1]=(long)(Double.parseDouble(strs[2])*1000);
                        return result;
                        
                    }
                    if(strs[1].equals("cost:")){
                        result[2]=Integer.parseInt(strs[2]);
                       
                    }
                }
                   
                
            }
         
           // result[3]=Collections.max(paths)*paths.size();
           
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }


    public  static int[][] readOutputYamlCost(String filename){
        List<List<Integer>> paths=new ArrayList<>();
        int[][] result=null;
        try{
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String str;
			
            boolean readPaths=false;
            
            List<Integer> path=null;
            int x=0,y=0,maxlen=0;
            while ((str = in.readLine()) != null) {
                String[] strs=str.split("\\s+");
                if(strs.length>1){                   
                    if(strs[1].contains("agent")){
                        if(readPaths==false){
                            readPaths=true;
                            path=new ArrayList<>();
                        }
                        else{
                            paths.add(path); 
                            maxlen=Math.max(path.size(), maxlen);
                            path=new ArrayList<>(); 
                        }
                    }
                    if(readPaths){
                        if (strs[1].contains("-")) x=Integer.parseInt(strs[3]);
                        if(strs[1].contains("y:")) y=Integer.parseInt(strs[2]);
                        if(strs[1].contains("t:")){
                            path.add(x*2000+y);//vid= x*1000+y
                           // System.out.println((x*2000+y)+" "+x+" "+y);
                        }
                    }
                }
            }
            paths.add(path);
            maxlen=Math.max(path.size(), maxlen);
            result=new int[paths.size()][maxlen];
            for(int i=0;i<paths.size();i++){
                
                for(int j=0;j<maxlen;j++){
                    
                    result[i][j]=paths.get(i).get(Math.min(j,paths.get(i).size()-1));
                   // System.out.print(result[i][j]+" ");
                }
              //  System.out.println();
            }
           // System.out.println(maxlen);
            
            return result;
           // result[3]=Collections.max(paths)*paths.size();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }
    

	
}