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

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.URL;


class ecbsProblem{
    HashSet<Location> obstacles=new HashSet<>();
    Vector<Location> goals=new Vector<>();
    Vector<State> startStates=new Vector<>();
    Environment mapf=null;


    public static ecbsProblem getFromProblem(Problem p){
        ecbsProblem pp=new ecbsProblem();
        int rows=p.graph.rows;
        int cols=p.graph.columns;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                boolean found=false;
                Iterator<Vertex> it=p.graph.verticeSet.iterator();
                while(it.hasNext()){
                    Vertex v=it.next();
                    if(v.getIX()==j &&v.getIY()==i){
                        found=true;
                        break;
                    }
                }
                if(found==false){
                    pp.obstacles.add(new Location(j, i));
                }
            }
        }
        for(int i=0;i<p.sg[0].length;i++){
            Vertex vs=p.graph.idVertexMap.get(p.sg[0][i]);
            Vertex vg=p.graph.idVertexMap.get(p.sg[1][i]);
            pp.goals.add(new Location(vg.getIX(), vg.getIY()));
            pp.startStates.add(new State(0, vs.getIX(), vs.getIY()));
            
        }
        pp.mapf=new Environment(cols,rows, pp.obstacles, pp.goals);
        return pp;

    }

    public static ecbsProblem readFromYaml(String filename) throws Exception{
        ecbsProblem P=new ecbsProblem();
        Yaml y=new Yaml();
        Object obj=y.load(new FileInputStream(filename));
        yamlProblem test=new yamlProblem(obj);
        for(int i=0;i<test.obstacles.size();i++){
            ArrayList<Integer> xi=(ArrayList<Integer>)test.obstacles.get(i);
          //  System.out.println(xi.get(0)+","+xi.get(1));
      
            P.obstacles.add(new Location(xi.get(0), xi.get(1)));
        }

        for(int i=0;i<test.starts.size();i++){
            ArrayList<Integer> si=test.starts.get(i);
            ArrayList<Integer> gi=test.goals.get(i);
            P.startStates.add(new State(0, si.get(0), si.get(1)));
            P.goals.add(new Location(gi.get(0), gi.get(1)));

        }
        P.mapf=new Environment(test.width, test.height, P.obstacles, P.goals);
        return P;
    }
}



class PlanResult{
    Vector<Pair<State,Integer>> states=new Vector<>();
    Vector<Pair<Action,Integer>> actions=new Vector<>();
    int cost;
    int fmin;
    public String toString(){
        String result="Path= ";
        for(int i=0;i<states.size();i++){
            State a=states.get(i).getKey();
            result=result+a+"->";
        }
        return result;
    
    }
}







public class EcbsJava{
    private Environment m_env=null;
    private float m_w;
    private AstarEpsilon LowLevelSearch_t=null;

    

    boolean search(Vector<State> initialStates,Vector<PlanResult> solution){
        long startTime=System.currentTimeMillis();
        HighLevelNode start=new HighLevelNode();
   
        start.cost = 0;
        start.LB = 0;
        start.id = 0;
  
        for(int i=0;i<initialStates.size();i++){
            start.solution.add(new PlanResult());
        }
       
       // start.constraints.setSize(initialStates.size());
        for(int i=0;i<initialStates.size();i++){
            start.constraints.add(new Constraints());
        }
       
        for (int i = 0; i < initialStates.size(); ++i) {
            if (i < solution.size() && solution.get(i).states.size() > 1) {
                System.out.println( initialStates.get(i) + " " + solution.get(i).states.firstElement().getKey());
                        
                //assert(initialStates[i] == solution[i].states.front().first);
                start.solution.set(i, solution.get(i));
                System.out.println( "use existing solution for agent: " + i );
            } 
            else {
                LowLevelEnvironment llenv=new LowLevelEnvironment(m_env, i, start.constraints.get(i),start.solution);
                AstarEpsilon lowLevel=new AstarEpsilon(llenv, m_w);
               
                boolean success = lowLevel.search(initialStates.get(i), start.solution.get(i));
                
                if (!success) {
                    return false;
                }
            }
            start.cost += start.solution.get(i).cost;
            start.LB += start.solution.get(i).fmin;
        }
        System.exit(0);
        start.focalHeuristic = m_env.focalHeuristic(start.solution);

        PriorityQueue<HighLevelNode> open=new PriorityQueue<>();

        PriorityQueue<HighLevelNode> focal=new PriorityQueue<>(HighLevelNode.focalOrder);
  
        open.add(start);
    
        focal.add(start);

        int bestCost=start.cost;
        solution.clear();
        int id = 1;
       
        while (!open.isEmpty()) {
            int LB = open.peek().LB;
            Iterator<HighLevelNode> it=open.iterator();
            //HighLevelNode[] openList=open.toArray(new HighLevelNode[0]);
            //Arrays.sort(openList);
            int oldBestCost = bestCost;
            bestCost = open.peek().cost;
            // std::cout << "bestFScore: " << bestFScore << std::endl;
            if (bestCost > oldBestCost) {       
                
                while(it.hasNext()){
                    HighLevelNode hn=it.next();
                    int val=hn.cost;
                    if (val > oldBestCost * m_w && val <= bestCost * m_w) {
                        focal.add(hn);                    
                    }
                  //  if(val>bestCost*m_w)break;
                }           
            }

			HighLevelNode P = focal.peek();
			m_env.onExpandHighLevelNode(P.cost);
			// std::cout << "expand: " << P << std::endl;

			focal.poll();
            open.remove(P);
            

			Conflict conflict=new Conflict();
			if (!m_env.getFirstConflict(P.solution, conflict)) {
                long endTime=System.currentTimeMillis();
                System.out.println("done; cost: " +P.cost);
                System.out.println("P.solution size="+P.solution.size());
                for(int i=0;i<P.solution.size();i++){
                    solution.add(P.solution.get(i));
                }
				System.out.println("runtime= "+(endTime-startTime)/1000.);
				return true;
			}

			// create additional nodes to resolve conflict
            System.out.println("Found conflict: " + conflict );
         
            
			HashMap<Integer, Constraints> constraints=new HashMap<>();
			m_env.createConstraintsFromConflict(conflict, constraints);

			Iterator<Map.Entry<Integer,Constraints>> it2=constraints.entrySet().iterator();
			while(it2.hasNext()){
				Map.Entry<Integer,Constraints> c=it2.next();
                int i=c.getKey();
                System.out.println("create child with id "+id);
				HighLevelNode newNode=P;
				newNode.id=id;
                Constraints nc=newNode.constraints.get(i);
                nc.add(c.getValue());
            //    int ss1=newNode.constraints.get(i).vertexConstraints.size()+newNode.constraints.get(i).edgeConstraints.size();
                newNode.constraints.set(i, nc);
                //newNode.constraints.get(i).add(c.getValue());
                
				newNode.cost-=newNode.solution.get(i).cost;
				newNode.LB -= newNode.solution.get(i).fmin;
            

				LowLevelEnvironment llenv=new LowLevelEnvironment(m_env, i, newNode.constraints.get(i),newNode.solution);
                AstarEpsilon lowLevel=new AstarEpsilon(llenv, m_w);
                //State a1=new State(10, 12, 20);
                //State a2=new State(10,12,21);
         //       System.out.println(llenv.m_env.transitionValid(a1,a2)+" "+newNode.constraints.get(i).edgeConstraints.contains(new EdgeConstraint(10, 12, 20, 12, 21))+"--> "+newNode.constraints.get(i));
                System.out.println("Begin lowlevel search!");
                
                boolean success = lowLevel.search(initialStates.get(i), newNode.solution.get(i));
                System.out.println("Solved!");
				newNode.cost += newNode.solution.get(i).cost;
				newNode.LB += newNode.solution.get(i).fmin;
				newNode.focalHeuristic = m_env.focalHeuristic(newNode.solution);

				if (success) {
					
                    open.add(newNode);
                  
                    System.out.println("  success. cost: " + newNode.cost);
					if (newNode.cost <= bestCost * m_w) {
                        focal.add(newNode);
                      //  Collections.sort(focal,HighLevelNode.focalOrder);
					}
				}

				++id;
				
			}
		}

        return false;
    }

    
    public EcbsJava(Environment environment, float w){
        this.m_env=environment;
        this.m_w=w;
    }

    public static long[] ECBS_solve(Problem p){
        ecbsProblem test=null;
        test=test.getFromProblem(p);
        EcbsJava solver=new EcbsJava(test.mapf, (float)1.5);
    
        return solver.solve(test.startStates);
    }

    public long[] solve(Vector<State> startStates){
        long []result=new long[4];
        Vector<PlanResult>solution=new Vector<>();
        if(startStates.size()==0){
            result[0]=0;
            return result;
        }
        //PlanResult solution=new PlanResult();
        long startTime=System.currentTimeMillis();
        boolean success=search(startStates, solution);
        long endTime=System.currentTimeMillis();
        int makespan=0;
        if(success){
            System.out.println("Planning successful!");
            int cost=0;
            
            //System.out.println("solution size="+solution.size());
            for(int i=0;i<solution.size();i++){
                cost+=solution.get(i).cost;
                makespan=Math.max(makespan,(int)solution.get(i).cost);
            }
            System.out.println("Makespan="+makespan);
        }
        else{
            System.out.println("Planning failed!");
        }
        result[0]=makespan;
        return result;
    }

    public static void main(String [] args){
        ecbsProblem test=null;
        try{

           //Problem px=Problem.createGridProblem(64,64,0.0,200);
          // test=ecbsProblem.getFromProblem(px);
            
            test=ecbsProblem.readFromYaml("./test.yaml");
            
            EcbsJava solver=new EcbsJava(test.mapf, (float)1.5);
           
            solver.solve(test.startStates);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
