package projects.multipath.ECBS;

import java.io.*;

import java.util.*;
import java.awt.*;
import javafx.util.Pair;

import projects.multipath.advanced.Graph;
import projects.multipath.advanced.Path;
import projects.multipath.advanced.Problem;
import projects.multipath.advanced.yamlProblem;
import projects.multipath.advanced.Vertex;

import projects.multipath.ILP.PathPlanner;
import projects.multipath.ILP.Solve;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.URL;




public class Ecbs{
	public static void main(String[] args){
		Problem p=Problem.createGridProblem(128,128,0,2000);
	//	System.out.println("Begin!");
	//	Solve.solveProblemSuboptimal(p,false,false,0,300,3,false);
		long t1=System.currentTimeMillis();
		long[] op=(new Ecbs()).ECBS_solve_suboptimal(p,2);
		long t2=System.currentTimeMillis();
		System.out.println(op[0]);
		System.out.println((t2-t1));
		
	}

    
    public static long[] ECBS_solve(Problem p){
        String currentDir="./projects/multipath/ECBS/";
		yamlProblem.saveProblem(p,currentDir+"bin/tmp/problem.yaml");
		long[] output=null;;
        try{
            String cmd="./projects/multipath/ECBS/bin/ecbs -i ./projects/multipath/ECBS/bin/tmp/problem.yaml -o ./projects/multipath/Ecbs/bin/tmp/output.yaml -w 1.5";
			//Process p1=Runtime.getRuntime().exec(cmd);
			//p1.waitFor();
			Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line=bufferedReader.readLine()) != null) {
			    System.out.println(line);
			}	
		
			BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gb2312"));
			String errline = null;
			while ((errline = brError.readLine()) != null) {
				 System.out.println(errline);
            }
            int c=process.waitFor();
            output=yamlProblem.readOutputYaml("./projects/multipath/Ecbs/bin/tmp/output.yaml");
			
			if(c!=0){
				
            }
            
        }
        catch (IOException e1) {
			e1.printStackTrace();
		
			return null;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return null;
		}
		System.out.println("solved in "+output[1]+" ms");
		System.out.println("Makespan "+output[0]);
		return  output;
      
    }


    public static long ECBS_solve(Problem p,int pid){
		String currentDir="./projects/multipath/ECBS/";
		long[] output=null;
		yamlProblem.saveProblem(p,String.format(currentDir+"bin/tmp/problem%d.yaml",pid));
		System.out.println("Begin");
        try{
            String cmd=String.format("./projects/multipath/ECBS/bin/ecbs -i ./projects/multipath/ECBS/bin/tmp/problem%d.yaml -o ./projects/multipath/Ecbs/bin/tmp/output%d.yaml -w 1.5",pid,pid);
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line=bufferedReader.readLine()) != null) {
			    //System.out.println(line);
			}	
		
			BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gb2312"));
			String errline = null;
			while ((errline = brError.readLine()) != null) {
				// System.out.println(errline);
            }
            int c=process.waitFor();
            output=yamlProblem.readOutputYaml(String.format("./projects/multipath/Ecbs/bin/tmp/output%d.yaml",pid));

            System.out.println("solved in "+output[1]+" ms");
			if(c!=0){
			
				return  output[0];
            }
            
        }
        catch (IOException e1) {
		
			return -1;
		} catch (InterruptedException e1) {
		
			return -1;
        }
        return output[0];
    }
    

    int mpieces = 0;
	int msg[][] = null;
	boolean mdone[] = null;
	Graph mg = null;
    Map<Integer, Object> mpath = new HashMap<Integer, Object>();
    double mtime = 0;
    int makespan=0;

    public long[] ECBS_solve_suboptimal(Problem p,int splits){
        mg = p.graph;
        int []start=p.sg[0];
        int []goal=p.sg[1];

	//	mtime = timeLimit;
		mpieces = (int)Math.pow(2, splits);
		msg = new int[mpieces + 1][start.length];
		mdone = new boolean[mpieces];
		for(int i = 0;i < start.length; i ++){
			msg[0][i] = start[i];
			msg[mpieces][i] = goal[i];
		}
		
       
		// Do splitting
		for(int i = 0; i < splits; i ++){
			int numSplits = (int)Math.pow(2, i);
			int stepSize = (int)Math.pow(2, splits - i);
			for(int s = 0; s < numSplits; s++){
				int[] middle = PathPlanner.splitPaths(p.graph, msg[s*stepSize], msg[(s+1)*stepSize], true);
				
				for(int a = 0; a < start.length; a ++){
					msg[s*stepSize + stepSize/2][a] = middle[a];
				}
			}
		}
		System.out.println("Begin");
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
		return new long[]{makespan,0,0,0};
		
    }

    private Thread createThread(final int i){
		return new Thread(
				new Runnable(){
					@Override
					public void run(){
                        Problem pp=new Problem();
                        pp.graph=mg;
                        pp.sg=new int[2][];
                        pp.sg[0]=msg[i];
                        pp.sg[1]=msg[i+1];
						try {
							
                            long output=ECBS_solve(pp,i);
							makespan+=output;
							
							mdone[i] = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				});
	}


    

    


    
        
    
    
    



}
