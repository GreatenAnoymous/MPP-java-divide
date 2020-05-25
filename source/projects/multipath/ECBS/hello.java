import java.util.*;

public class hello{
    public static void  main(String [] args){
        int [] array=new int[4000];
        
        long time1=System.currentTimeMillis();
        for(int j=0;j<10000;j++){
            for(int i=0;i<array.length;i++){
                array[i]=(int)(Math.random()*4000);
            }
            Arrays.sort(array);
        }
        
        long time2=System.currentTimeMillis();
        System.out.println(time2-time1);
    }
}