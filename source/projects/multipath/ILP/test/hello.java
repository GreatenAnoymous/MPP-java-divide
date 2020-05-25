
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.io.*;
import java.util.*;

import org.yaml.snakeyaml.Yaml;

class yamlProblem{
	Object yamlObj;

	yamlProblem(Object yamlObj){
		this.yamlObj=yamlObj;
	}

	int height;
	int width;
	ArrayList<Object> obstacles;
	ArrayList<ArrayList<Integer>> starts;
	ArrayList<ArrayList<Integer>> goals;

	void getGraph(){
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

	
}

class MySuper{
	int a=4;
	public MySuper(){
		myMethod();
	}
	void myMethod(){
		a++;
		System.out.print("y"+a);
	}
}

class Main extends MySuper {
	int b=3;
	void myMethod(){
		System.out.println(b);
		System.out.println("y"+b);
	}

	static String basePath = "D:/temp/data/dmrpp/"; 
	
	public static void main(String argv[]) throws Exception{

		
		Main mysub=new Main();
	

		


		
	}

}

