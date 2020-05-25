import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd



def read_data(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame.eval('OptimalRatio=(makespan+2)/makespanLB',inplace=True)
	frame=frame[(~frame['runtime'].isin([np.nan]))]
	frame=frame[(frame['numAgents'].isin([num]))]
	#print(frame)
	frame=frame[frame['runtime']<300000]
	print(frame)
	temp=frame[['numAgents','makespan','runtime','makespanLB','OptimalRatio']].mean()
	print("\n")
	print(temp)
	return temp

def get_num_agents(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','cost','runtime','costLB'])
	#print(frame)
	frame.drop_duplicates('numAgents',inplace=True)
	return frame['numAgents'].values
	
def get_runtime_optimality(filename):
	numAgents=get_num_agents(filename)
	rt=[]
	opt=[]
	for a in numAgents:
		tmp=read_data(filename,a)
		rt.append(tmp['runtime'])
		opt.append(tmp['OptimalRatio'])
	return rt,opt

const_name="random-32-32-10-random.txt"


#######################################################
#Compare old split with total time split
#######################################################
plt.figure()
plt.ylim(0, 300)
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Average computation time in seconds')
fname="./2t-tt/"+const_name
runtime2t_tt,opt2t_tt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime2t_tt,marker='o',color='blue')
fname="./2t-tt-old/"+const_name
runtime2t_tt_old,opt2t_tt_old=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fanme),runtime2t_tt_old,marker='s',color='red')
plt.legend(handles=[l0,l1,l2,l3],labels=['2t-tt','2-way','4t-tt','4-way'])
plt.show()
