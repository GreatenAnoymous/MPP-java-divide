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


class LowLevelEnvironment {
    public Environment m_env=null;
    private Vector<PlanResult > m_solution=null;

    public LowLevelEnvironment(Environment env, int agentIdx, Constraints constraints,Vector<PlanResult> solution){
        this.m_env=env;
        this.m_solution=solution;
        m_env.setLowLevelContext(agentIdx, constraints);
       // System.out.println(constraints.edgeConstraints.size());
    }

    int admissibleHeuristic(State s) {
        return m_env.admissibleHeuristic(s);
    }

    int focalStateHeuristic(State s, int gScore) {
        return m_env.focalStateHeuristic(s, gScore, m_solution);
    }

    int focalTransitionHeuristic(State s1, State s2,int gScoreS1, int gScoreS2) {
        return m_env.focalTransitionHeuristic(s1, s2, gScoreS1, gScoreS2,m_solution);
    }

    boolean isSolution(State s) { return m_env.isSolution(s); }

    void getNeighbors(State s,Vector<Neighbor> neighbors) {
        m_env.getNeighbors(s, neighbors);
    }

    void onExpandNode(State s, int fScore, int gScore) {
        // std::cout << "LL expand: " << s << " fScore: " << fScore << " gScore: "
        // << gScore << std::endl;
        // m_env.onExpandLowLevelNode(s, fScore, gScore, m_agentIdx,
        // m_constraints);
        m_env.onExpandLowLevelNode(s, fScore, gScore);
    }

  
}


class LowLevelNode implements Comparable<LowLevelNode>{
    State state;
    int fScore;
    int gScore;
    int focalHeuristic;

    public LowLevelNode(State state, int fScore, int gScore, int focalHeuristic){
        this.state=state;
        this.fScore=fScore;
        this.gScore=gScore;
        this.focalHeuristic=focalHeuristic;

    }

    public static Comparator<LowLevelNode> focalOrder =  new Comparator<LowLevelNode>() {
        public int compare(LowLevelNode s1, LowLevelNode s2) {
            if (s1.focalHeuristic != s2.focalHeuristic) {
               // System.out.println("ok");
                return (int)(s1.focalHeuristic - s2.focalHeuristic);
                // } else if ((*h1).fScore != (*h2).fScore) {
                //   return (*h1).fScore > (*h2).fScore;
              } else if (s1.fScore != s2.fScore) {
           
                return (int)(s1.fScore - s2.fScore);
              } else {
                  if(s1.gScore==s2.gScore)
                    return 0;
                  return -(int)(s1.gScore-s2.gScore);
              }
        }
    };
    
    public String toString(){
        return "state:"+state+" fScore:"+fScore+" gScore:"+gScore+" focal:"+focalHeuristic;
    }

    public boolean equals(Object s) {
        if(!(s instanceof LowLevelNode))
            return false;
        LowLevelNode ss=(LowLevelNode) s;
        return ss.state.equals(this.state);
    }


    public int compareTo(LowLevelNode other){
       
        if (fScore != other.fScore) {
            
            return (int)(fScore -other.fScore);
        } 
        else {
            if(gScore>other.gScore)
                return -1;
            if(gScore==other.gScore)
                return 0;
            return 1;
        }

        
    }  
}
