from ruamel import yaml
import numpy as np
import argparse
from collections import OrderedDict 



def get_id(x,y,h):
    return x*h+y


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--maps",help="map")
    parser.add_argument("--scene",help="scenario")
    parser.add_argument("--numAgents",help="numAgents")
    parser.add_argument("--fi",help="store file")
    args = parser.parse_args()
    mapname=args.maps
    scename=args.scene
    obstacles=[]
    f = open(mapname)
    line = f.readline()
    w=0
    h=0
    i=0
    while line:
        
        ss=line.split()
        print(ss)
        if len(ss)==2:
            if ss[0]=='height':
                h=int(ss[1])
            if ss[0]=='width':
                w=int(ss[1])
        if len(ss)==1:
            if ss[0]=='map':
                i=0
            else:
                for j in range(0,len(ss[0])):
                    if ss[0][j]!='.' and ss[0][j]!=0:
                        obstacles.append(yaml.comments.CommentedSeq([j,i]))
                i=i+1
                    
                
        line = f.readline()
    f.close()
    contents=dict() 
    agents=[]
    
    numAgents=int(args.numAgents)

    scene=open(scename)
    line = scene.readline()
    for i in range(0,numAgents):
        
        line = scene.readline()
        sss=line.split()
        print(sss)
        agent=dict() 
        sx=int(sss[4])
        sy=int(sss[5])
        
        
        gx=int(sss[6])
        gy=int(sss[7])
   
        agent["goal"]=yaml.comments.CommentedSeq([gx,gy])
        agent["name"]="agent"+str(i)
        agent["start"]=yaml.comments.CommentedSeq([sx,sy])
        agents.append(agent)
    contents["agents"]=agents
    
    maps=dict()
    
    maps["dimensions"]=[w,h]
    
   
    maps["obstacles"]=obstacles
    contents["map"]=maps
    
    
    
    with open(args.fi, 'w') as nf:
        yaml.dump(contents, nf, Dumper=yaml.RoundTripDumper)
