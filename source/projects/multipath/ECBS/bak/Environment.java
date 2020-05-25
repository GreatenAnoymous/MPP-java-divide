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


class HighLevelNode implements Comparable<HighLevelNode>{
    Vector<PlanResult> solution=new Vector<>();
    Vector<Constraints> constraints=new Vector<>();

    double cost;
    int LB;
    double focalHeuristic;
    int id;

    public static Comparator<HighLevelNode> focalOrder =  new Comparator<HighLevelNode>() {
        public int compare(HighLevelNode s1, HighLevelNode s2) {
            if(s1.focalHeuristic!=s2.focalHeuristic)
                return (int)(s1.focalHeuristic- s2.focalHeuristic);
            else{
                if(s1.cost<=s2.cost) return -1;
                else return 1;
            }
                

        }
    };
    
    

    public int compareTo(HighLevelNode n){
        if(cost>n.cost)return -1;
        else return 1;

    }  



}




class Quartet{
    State state;
    Action action;
    Double cost1;
    Double cost2;

    public Quartet(State state,Action action,Double cost1,Double cost2){
        this.state=state;
        this.action=action;
        this.cost1=cost1;
        this.cost2=cost2;
    }
}


class Neighbor{
    State state;
    Action action;
    double cost;
    public Neighbor(State state,Action action, double cost){
        this.state=state;
        this.action=action;
        this.cost=cost;
    }

}


class State{
    int time;
    int x;
    int y;

    public State(int time,int x,int y){
        this.time=time;
        this.x=x;
        this.y=y;
    }

    public boolean equals(Object s) {
        if(!(s instanceof State))
            return false;
        State ss=(State) s;
        return time==ss.time&&x==ss.x&&y==ss.y;
    }

    public boolean equalExceptTime(State s){
        return x==s.x&&y==s.y;

    }

    public String toString(){
        return "("+this.time+","+this.x+","+this.y+")";
    }

    public int hashCode(){
        return (int)(this.time*1e8+this.x+1e4+this.y);
    }
}


enum Action{
    Up,Down,Left,Right,Wait;
}

class Conflict{
    enum Type{
        Vertex,
        Edge
    }
    int agent1;
    int agent2;
    int time;
    Type type;
    int x1;
    int y1;
    int x2;
    int y2;

    public String toString(){
        if(this.type==Type.Vertex)
        return "At time "+time+":Vertex("+x1+","+x2+")";
        else
        return "At time "+time+":Edge("+x1+","+y1+")--->"+"Edge("+x2+","+y2+")";
    }
}


class VertexConstraint{
    int time,x,y;

    public VertexConstraint(int time,int x,int y){
        this.time=time;
        this.x=x;
        this.y=y;
    }

    public String toString(){
        return "("+time+","+x+","+y+")";
    }

    public boolean equals(Object other){
        if(!(other instanceof VertexConstraint))
            return false;
        VertexConstraint vc=(VertexConstraint) other;
        return time==vc.time&&x==vc.x&&y==vc.y;
    }

    

}


class EdgeConstraint{
    int time,x1,x2,y1,y2;
    public EdgeConstraint(int time,int x1,int y1,int x2,int y2){
        this.time=time;
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
    }
   

    public boolean equals(Object other){
        if(!(other instanceof EdgeConstraint))
            return false;
        EdgeConstraint ec=(EdgeConstraint) other;
        return time==ec.time&&x1==ec.x1&&y1==ec.y1&&x2==ec.x2&&y2==ec.y2;
    }

    public String toString(){
        return "["+"("+x1+","+y1+")-->"+"("+x2+","+y2+")]";
    }
}


class Constraints{
    ArrayList<VertexConstraint> vertexConstraints=new ArrayList<>();
    ArrayList<EdgeConstraint> edgeConstraints=new ArrayList<>();

    public String toString(){
        String result="Vertex constraints:\n";
        Iterator<VertexConstraint> it=vertexConstraints.iterator();
        while(it.hasNext()){
            result+=it.next()+"\n";
        }
        result+="Edge constraints:\n";
        Iterator<EdgeConstraint> it2=edgeConstraints.iterator();
        while(it2.hasNext()){
            result+=it2.next()+"\n";
        }
        return result;
    }

   

    public void add(Constraints other){
        vertexConstraints.addAll(other.vertexConstraints);
        edgeConstraints.addAll(other.edgeConstraints);
    }

    public boolean overlap(Constraints other){
        Set<VertexConstraint> vertexIntesection=new HashSet<>();
        Set<EdgeConstraint> edgeIntersection=new HashSet<>();
        vertexIntesection.addAll(other.vertexConstraints);
        vertexIntesection.retainAll(this.vertexConstraints);
        edgeIntersection.addAll(other.edgeConstraints);
        edgeIntersection.retainAll(this.edgeConstraints);
        return !vertexIntesection.isEmpty()||!edgeIntersection.isEmpty();

    }
}

class Location{
    int x,y;
    public Location(int x,int y){
        this.x=x;
        this.y=y;
    }
    public boolean equals(Object other){
        if(!(other instanceof Location))
            return false;
        Location loc=(Location) other;
        return x==loc.x&&y==loc.y;
    }
}


public class Environment{
    private int m_dimx,m_dimy,m_agentIdx;
    private Constraints m_constraints;
    private Set<Location> m_obstacles;
    private  Vector<Location> m_goals;
    private int m_lastGoalConstraint;
    private int m_highLevelExpanded;
    private int m_lowLevelExpanded;

    public Environment(int dimx,int dimy,Set<Location> obstacles,Vector<Location> goals){
        this.m_dimx=dimx;
        this.m_dimy=dimy;
        this.m_obstacles=obstacles;
        this.m_goals=goals;
        m_constraints=null;
    

    }

    void setLowLevelContext(int agentIdx,Constraints constraints) {
     
        m_agentIdx = agentIdx;
        m_constraints = constraints;
        m_lastGoalConstraint = -1;
        Iterator<VertexConstraint> it=constraints.vertexConstraints.iterator();
        while(it.hasNext()){
            VertexConstraint vc=it.next();
            if (vc.x == m_goals.get(m_agentIdx).x && vc.y == m_goals.get(m_agentIdx).y) {
                m_lastGoalConstraint = Math.max(m_lastGoalConstraint, vc.time);
            }
        }
        
    }

    int admissibleHeuristic(State s) {
        return Math.abs(s.x - m_goals.get(m_agentIdx).x) +
               Math.abs(s.y - m_goals.get(m_agentIdx).y);
    }

    int focalStateHeuristic(State s, double gScore,Vector<PlanResult> solution) {
        int numConflicts = 0;
        for (int i = 0; i < solution.size(); ++i) {
            if (i != m_agentIdx && !solution.get(i).states.isEmpty()) {
                State state2 = getState(i, solution, s.time);
                if (s.equalExceptTime(state2)) {
                     ++numConflicts;
                }
            }
        }
        
        return numConflicts;
    }

    int focalTransitionHeuristic(State s1a, State s1b, double gScoreS1a, double gScoreS1b,Vector<PlanResult> solution) {
        int numConflicts = 0;
        for (int i = 0; i < solution.size(); ++i) {
            if (i != m_agentIdx && !solution.get(i).states.isEmpty()) {
                State s2a = getState(i, solution, s1a.time);
                State s2b = getState(i, solution, s1b.time);
                if (s1a.equalExceptTime(s2b) && s1b.equalExceptTime(s2a)) {
                    ++numConflicts;
                }
            }
        }
        return numConflicts;
    }

    int focalHeuristic(Vector<PlanResult> solution) {
        int numConflicts = 0;

        int max_t = 0;
        for (int i=0;i<solution.size();i++){
            PlanResult sol=solution.get(i);
            max_t = Math.max(max_t, sol.states.size() - 1);
        }

        for (int t = 0; t < max_t; ++t) {
        // check drive-drive vertex collisions
            for (int i = 0; i < solution.size(); ++i) {
                State state1 = getState(i, solution, t);
                    for (int j = i + 1; j < solution.size(); ++j) {
                    State state2 = getState(j, solution, t);
                    if (state1.equalExceptTime(state2)) {
                        ++numConflicts;
                    }
                }
            }
        // drive-drive edge (swap)
            for (int i = 0; i < solution.size(); ++i) {
                State state1a = getState(i, solution, t);
                State state1b = getState(i, solution, t + 1);
                for (int j = i + 1; j < solution.size(); ++j) {
                    State state2a = getState(j, solution, t);
                    State state2b = getState(j, solution, t + 1);
                    if (state1a.equalExceptTime(state2b) &&
                        state1b.equalExceptTime(state2a)) {
                        ++numConflicts;
                    }
                }
            }
        }
        return numConflicts;
    }

    void getNeighbors(State s,Vector<Neighbor> neighbors) {
    // std::cout << "#VC " << constraints.vertexConstraints.size() << std::endl;
    // for(const auto& vc : constraints.vertexConstraints) {
    //   std::cout << "  " << vc.time << "," << vc.x << "," << vc.y <<
    //   std::endl;
    // }
        neighbors.clear();
        State n;
        n=new State (s.time + 1, s.x, s.y);
        //System.out.println("state s="+s);
        if (stateValid(n) && transitionValid(s, n)) {
            neighbors.addElement(new Neighbor(n, Action.Wait, 1));
        }
        
        
        n=new State(s.time + 1, s.x - 1, s.y);
        if (stateValid(n) && transitionValid(s, n)) {
            neighbors.addElement( new Neighbor(n, Action.Left, 1));
        }
        
        
        n=new State(s.time + 1, s.x + 1, s.y);
        if (stateValid(n) && transitionValid(s, n)) {
            neighbors.addElement(new Neighbor(n, Action.Right, 1));
        
        }
        
        n=new State(s.time + 1, s.x, s.y + 1);
        if (stateValid(n) && transitionValid(s, n)) {
            neighbors.addElement(new Neighbor(n, Action.Up, 1));
        }
        
        
        n= new State(s.time + 1, s.x, s.y - 1);
        if (stateValid(n) && transitionValid(s, n)) {
            neighbors.addElement(new Neighbor(n, Action.Down, 1));
        }
       // System.out.println("neighbors size="+neighbors.size());
        
    }


    boolean isSolution(State s) {
        return s.x == m_goals.get(m_agentIdx).x && s.y == m_goals.get(m_agentIdx).y &&
               s.time > m_lastGoalConstraint;
    }

    boolean getFirstConflict(Vector<PlanResult> solution,  Conflict result) {
        int max_t = 0;
        
        for (int i=0;i<solution.size();i++) {
            PlanResult sol=solution.get(i);
            max_t = Math.max(max_t, sol.states.size() - 1);
        }
        //System.out.println(max_t);
        
        for (int t = 0; t < max_t; ++t) {
        // check drive-drive vertex collisions
            for (int i = 0; i < solution.size(); ++i) {
                State state1 = getState(i, solution, t);
                for (int j = i + 1; j < solution.size(); ++j) {
                    State state2 = getState(j, solution, t);
                    
                    if (state1.equalExceptTime(state2)) {
                      //  System.out.println(state1+"===="+state2);
                        result.time = t;
                        result.agent1 = i;
                        result.agent2 = j;
                        result.type = Conflict.Type.Vertex;
                        result.x1 = state1.x;
                        result.y1 = state1.y;
                        // std::cout << "VC " << t << "," << state1.x << "," << state1.y <<
                        // std::endl;
                        return true;
                    }
                }
            }
        
        // drive-drive edge (swap)
            for (int i = 0; i < solution.size(); ++i) {
                State state1a = getState(i, solution, t);
                State state1b = getState(i, solution, t + 1);
                for (int j = i + 1; j < solution.size(); ++j) {
                    State state2a = getState(j, solution, t);
                    State state2b = getState(j, solution, t + 1);
                    if (state1a.equalExceptTime(state2b) &&
                        state1b.equalExceptTime(state2a)) {
                        result.time = t;
                        result.agent1 = i;
                        result.agent2 = j;
                        result.type = Conflict.Type.Edge;
                        result.x1 = state1a.x;
                        result.y1 = state1a.y;
                        result.x2 = state1b.x;
                        result.y2 = state1b.y;
                        return true;
                    }
                }
            }
        }
      //  System.out.println("???");
        //System.exit(0);
        return false;
    }

    void createConstraintsFromConflict(Conflict conflict, Map<Integer,Constraints> constraints) {
        if (conflict.type == Conflict.Type.Vertex) {
            Constraints c1=new Constraints();
            c1.vertexConstraints.add(new VertexConstraint(conflict.time, conflict.x1, conflict.y1));
            constraints.put(conflict.agent1, c1);
            constraints.put(conflict.agent2,c1);
        } else if (conflict.type == Conflict.Type.Edge) {
            Constraints c1=new Constraints();
            c1.edgeConstraints.add(new EdgeConstraint(conflict.time, conflict.x1, conflict.y1, conflict.x2, conflict.y2));
            constraints.put(conflict.agent1,c1);
            Constraints c2=new Constraints();
            c2.edgeConstraints.add(new EdgeConstraint(conflict.time, conflict.x2, conflict.y2, conflict.x1, conflict.y1));
            constraints.put(conflict.agent2, c2);
        }
    }

    void onExpandHighLevelNode(double cost) { m_highLevelExpanded++; }

    void onExpandLowLevelNode(State s, double fScore,double gScore) {
        m_lowLevelExpanded++;
    }

    int highLevelExpanded() { return m_highLevelExpanded; }

    int lowLevelExpanded() { return m_lowLevelExpanded; }


    private State getState(int agentIdx,Vector<PlanResult> solution,int t){
        if(t<solution.get(agentIdx).states.size()){
            return solution.get(agentIdx).states.get(t).getKey();
        }
        return solution.get(agentIdx).states.lastElement().getKey();

    }

    private boolean stateValid(State s) {
        boolean if_valid= s.x >= 0 && s.x < m_dimx && s.y >= 0 && s.y < m_dimy ;

        if(if_valid==true){
            Iterator<Location> it=m_obstacles.iterator();
            while(it.hasNext()){
                Location loc=it.next();
                if(loc.x==s.x&&loc.y==s.y){
                    if_valid=false;
                    break;
                }
            }
            Iterator<VertexConstraint> it2=m_constraints.vertexConstraints.iterator();
            while(it2.hasNext()){
                VertexConstraint vc=it2.next();
                if(vc.equals(new VertexConstraint(s.time, s.x, s.y))){
                    if_valid=false;
                    break;
                }
            }
        }
        return if_valid;
        //
       // !m_obstacles.contains(new Location(s.x, s.y))&&
       // !m_constraints.vertexConstraints.contains(new VertexConstraint(s.time, s.x, s.y));
 //       Iterator<Location>it =m_obstacles.iterator();
 //       while(it.hasNext()){
  //          Location loc=it.next();
   //         if(s.x==loc.x&&s.y==loc.y){
   //             if_obstacle=false;
  //          }
  //     }
       // System.out.println(if_valid);
       // return if_valid;
    }

    public boolean transitionValid(State s1, State s2) {
        boolean found=false;
        Iterator<EdgeConstraint> it=m_constraints.edgeConstraints.iterator();
       
        while(it.hasNext()){
            if(it.next().equals(new EdgeConstraint(s1.time, s1.x, s1.y, s2.x, s2.y))){
                found=true;
                break;
            }
        }
        if(found) return false;
        return true;
        
    
       
       // const auto& con = m_constraints->edgeConstraints;
      //  return m_constraints.edgeConstraints.contains(new EdgeConstraint(s1.time, s1.x, s1.y, s2.x, s2.y))==false;
      }

}
