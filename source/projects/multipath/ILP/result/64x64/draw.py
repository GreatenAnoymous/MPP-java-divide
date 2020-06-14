import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd



def read_data(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame.eval('OptimalRatio=(makespan)/makespanLB',inplace=True)
	frame.replace(np.nan,300000)
	frame=frame[(frame['numAgents'].isin([num]))]
	#print(frame)
	frame=frame[frame['runtime']<300000]
	print(frame)
	temp=frame[['numAgents','makespan','runtime','makespanLB','OptimalRatio']].mean()
	print("\n")
	print(temp)
	return temp
	
def read_data1(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame.eval('OptimalRatio=(makespan)/makespanLB',inplace=True)
	frame=frame[(~frame['runtime'].isin([np.nan]))]
	frame=frame[(frame['numAgents'].isin([num]))]
	#print(frame)
	frame=frame[frame['runtime']<300000]
	print(frame)
	temp=frame[['numAgents','makespan','runtime','makespanLB','OptimalRatio']].mean()
	print("\n")
	print(temp)
	return temp
	
def read_data2(filename,num,timeLimit):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame=frame[(frame['numAgents'].isin([num]))]
	total=len(frame)
	
	frame=frame[(~frame['runtime'].isin([np.nan]))]
	
	frame=frame[frame['runtime']<timeLimit]
	solved=len(frame)
	sr=solved/total
	return sr

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
		tmp1=read_data(filename,a)
		rt.append(tmp['runtime'])
		opt.append(tmp1['OptimalRatio'])
	return np.array(rt)/1000.,opt
	
def get_success_rate(filename,timeLimit):
	numAgents=get_num_agents(filename)
	sr=[]
	for a in numAgents:
		sr.append(read_data2(filename,a,timeLimit))
	return np.array(sr)

const_name="random-64-64-00-random.txt"


#######################################################
#ECBS with time-split
#######################################################
plt.figure()
plt.ylim(0, 100)
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time (s)')
fname="./4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime4t,marker='o',color='blue')
fname="./4s-22/"+const_name
runtime4s22,opt4s22=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime4s22,marker='s',color='red')
plt.legend(handles=[l0,l1],labels=['4-time','4-space'])
plt.savefig("64x64", bbox_inches="tight", pad_inches=0.05)
plt.show()

plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Optimality Ratio')
fname="./4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt4t,marker='o',color='blue')
fname="./4s-22/"+const_name
runtime4s22,opt4s22=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt4s22,marker='s',color='red')
plt.legend(handles=[l0,l1],labels=['4-time','2-space'])
plt.show()

