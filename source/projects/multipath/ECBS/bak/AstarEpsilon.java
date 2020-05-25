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

public class AstarEpsilon{
    private float m_w;
    private LowLevelEnvironment  m_env;
    public AstarEpsilon(LowLevelEnvironment environment,float w){
        this.m_env=environment;
        this.m_w=w;
    }

    public boolean search(State startState,PlanResult solution){
        solution.states.clear();
        solution.states.add(new Pair<State,Double>(startState,0.));
        solution.actions.clear();
        solution.cost=0;
      
        ArrayList<State> closedSet=new ArrayList<>();

        PriorityQueue<LowLevelNode> openSet=new PriorityQueue<>();
        PriorityQueue<LowLevelNode> focalSet=new PriorityQueue<>(LowLevelNode.focalOrder);
        HashMap<State,Quartet> cameFrom=new HashMap<>();
        HashMap<State,LowLevelNode> stateToHeap=new HashMap<>();
        //auto handle = openSet.push(Node(startState, m_env.admissibleHeuristic(startState), 0, 0));
        LowLevelNode ln=new LowLevelNode(startState, m_env.admissibleHeuristic(startState), 0, 0);
        
        openSet.add(ln);
        //Collections.sort(openSet);
       // stateToHeap.insert(std::make_pair<>(startState, handle));
       // (*handle).handle = handle;
    
        //focalSet.push(handle);
        focalSet.add(ln);
       // Collections.sort(focalSet,LowLevelNode.focalOrder);
       // std::vector<Neighbor<State, Action, Cost> > neighbors;
        Vector<Neighbor> neighbors=new Vector<>();
        //neighbors.reserve(10);
        double bestFScore=ln.fScore;
      
        int count=0;
        while (!openSet.isEmpty()) {
            Iterator<LowLevelNode> iter=openSet.iterator();
            double oldBestFScore = bestFScore;
            bestFScore = openSet.peek().fScore;
          //  System.out.println("bestFScore:"+bestFScore);
            // std::cout << "bestFScore: " << bestFScore << std::endl;
            if (bestFScore > oldBestFScore) {
                iter=openSet.iterator();
                while(iter.hasNext()){
                    LowLevelNode tmp=iter.next();
                    double val = tmp.fScore;
                    if (val > oldBestFScore * m_w && val <= bestFScore * m_w) {
                        focalSet.add(tmp);
                      
                    }
                    if (val > bestFScore * m_w) {
                        break;
                    }
                }           
            }
            LowLevelNode current=focalSet.peek();
       
            if (m_env.isSolution(current.state)) {
                solution.states.clear();
                solution.actions.clear();
       
                Iterator<Map.Entry<State, Quartet>> it2 =cameFrom.entrySet().iterator();
           
                State iterx=current.state;
                while (cameFrom.containsKey(iterx)) {
                   // Map.Entry<State, Quartet> tmp = it2.next();
                    Quartet tmp=cameFrom.get(iterx);
                    solution.states.addElement(new Pair<State,Double>(iterx, tmp.cost2));
                    solution.actions.addElement(new Pair<Action,Double>(tmp.action, tmp.cost1));
                    iterx=tmp.state;
                }
          
                solution.states.addElement(new Pair<State,Double>(startState, 0.));
                Collections.reverse(solution.actions);
                Collections.reverse(solution.states);
               // solution.states.push_back(std::make_pair<>(startState, 0));
                //std::reverse(solution.states.begin(), solution.states.end());
                //std::reverse(solution.actions.begin(), solution.actions.end());
                solution.cost = current.gScore;
                solution.fmin = openSet.peek().fScore;
              // System.exit(0);
                return true;
            }

          //  focalSet.remove(focalSet.get(0));
         // for(LowLevelNode lnx:focalSet){
       //     System.out.println(lnx);
      //  }
      //  System.out.println();
            focalSet.poll();
           // System.out.println(current+"*************");
            openSet.remove(current);
     
            stateToHeap.remove(current.state);
            closedSet.add(current.state);
           // closedSet.insert(current.state);
        
            // traverse neighbors
            neighbors.clear();
            m_env.getNeighbors(current.state, neighbors);
           
         
            for (int i=0;i<neighbors.size();i++) {
                Neighbor neighbor=neighbors.get(i);
                if (closedSet.contains(neighbor.state)==false) {
                    
                    double tentative_gScore = current.gScore + neighbor.cost;
                   // auto iter = stateToHeap.find(neighbor.state);
                    if (stateToHeap.containsKey(neighbor.state)==false) {  // Discover a new node
                        // std::cout << "  this is a new node" << std::endl;
                        double fScore =tentative_gScore + m_env.admissibleHeuristic(neighbor.state);
                        double focalHeuristic =current.focalHeuristic + m_env.focalStateHeuristic(neighbor.state, tentative_gScore) +m_env.focalTransitionHeuristic(current.state, neighbor.state,current.gScore,tentative_gScore);
                       // int s1=openSet.size();
                     
                        LowLevelNode tmp=new LowLevelNode(neighbor.state, fScore, tentative_gScore, focalHeuristic);
                        
                        openSet.add(tmp);
                       // System.out.println(openSet.size());
                      //  Collections.sort(openSet);
                       // int s2=openSet.size();
                        
                       
                        if (fScore <= bestFScore * m_w) {
                           
                            focalSet.add(tmp);
                          //  Collections.sort(focalSet,LowLevelNode.focalOrder);
                        }
                        stateToHeap.put(neighbor.state, tmp);
                       // System.out.println(fScore+"----->"+focalHeuristic);
                       // System.out.println( "  this is a new node " +fScore + "," +tentative_gScore+"  "+openSet.size());
                        
                 
                    }
                    else {
                     //   
                        LowLevelNode tmp=stateToHeap.get(neighbor.state);
                        
                        // We found this node before with a better path
                        if (tentative_gScore >= tmp.gScore) {
                            continue;
                        }
                        System.exit(0);
                        double last_gScore = tmp.gScore;
                        double last_fScore = tmp.fScore;
                        // std::cout << "  this is an old node: " << tentative_gScore << ","
                        // << last_gScore << " " << *handle << std::endl;
                        // update f and gScore
                        double delta = last_gScore - tentative_gScore;
                        tmp.gScore = tentative_gScore;
                        tmp.fScore -= delta;
                      //  System.out.println(openSet.contains(stateToHeap.get(neighbor.state)));
                        Iterator<LowLevelNode> itx=openSet.iterator();
                        while(itx.hasNext()){
                            LowLevelNode lnode=itx.next();
                            if(tmp.equals(lnode)){
                                lnode.fScore=tmp.fScore;
                                lnode.focalHeuristic=tmp.focalHeuristic;
                                lnode.gScore=tmp.gScore;
                            }
                            openSet.remove(lnode);
                            openSet.add(lnode);
                        }
                     
                      //  openSet.add(tmp);
                       // Collections.sort(openSet);
                        //m_env.onDiscover(neighbor.state, tmp.fScore,tmp.gScore);
                        if (tmp.fScore <= bestFScore * m_w &&
                            last_fScore > bestFScore * m_w) {
                        // std::cout << "focalAdd: " << *handle << std::endl;
                            focalSet.add(tmp);
                           // Collections.sort(focalSet,LowLevelNode.focalOrder);
                        }
                    }
            
                
                    
                    cameFrom.put(neighbor.state,new Quartet(current.state, neighbor.action, neighbor.cost,tentative_gScore));
                    //System.out.println(cameFrom.containsKey(neighbor.state)+"!!!!!!!!!");
                }
            }
        }
        
        return false;
        

    }

    
}