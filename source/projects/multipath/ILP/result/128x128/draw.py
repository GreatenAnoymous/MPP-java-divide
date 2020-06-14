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

const_name="random-128-128-00-random.txt"


#######################################################
#Compare old split with total time split
#######################################################
plt.figure()
plt.ylim(0, 300)
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time (s)')
fname="./ecbs-tt/"+const_name
runtime,opt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime,marker='o',color='blue')
fname="./ecbs-2t-tt/"+const_name
runtime2t_tt,opt2t_tt=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime2t_tt,marker='s',color='red')
fname="./ecbs-2t-tt-old/"+const_name
runtime2t_tt_old,opt2t_tt_old=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime2t_tt_old,marker='*',color='brown')
fname="./ecbs-3t-tt/"+const_name
runtime3t_tt,opt3t_tt=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime3t_tt,marker='x',color='orange')
fname="./ecbs-3t-tt-old/"+const_name
runtime3t_tt_old,opt3t_tt_old=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime3t_tt_old,marker='P',color='green')
fname="./ecbs-4t-tt/"+const_name
runtime4t_tt,opt4t_tt=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),runtime4t_tt,marker='D',color='purple')
fname="./ecbs-4t-tt/"+const_name
runtime4t_tt_old,opt4t_tt_old=get_runtime_optimality(fname)
l6,=plt.plot(get_num_agents(fname),runtime4t_tt_old,marker='v',color='black')


plt.legend(handles=[l0,l1,l2,l3,l4,l5,l6],labels=['ecbs','ecbs-2t-tt','ecbs-2t-mk','ecbs-3t-tt','ecbs-3t-mk','ecbs-4t-tt','ecbs-4t-mk'])
plt.savefig("ecbs_compare_runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Optimality Ratio')
fname="./ecbs-tt/"+const_name
runtime,opt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt,marker='o',color='blue')
fname="./ecbs-2t-tt/"+const_name
runtime2t_tt,opt2t_tt=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt2t_tt,marker='s',color='red')
fname="./ecbs-2t-tt-old/"+const_name
runtime2t_tt_old,opt2t_tt_old=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt2t_tt_old,marker='*',color='brown')
fname="./ecbs-3t-tt/"+const_name
runtime3t_tt,opt3t_tt=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt3t_tt,marker='x',color='orange')
fname="./ecbs-3t-tt-old/"+const_name
runtime3t_tt_old,opt3t_tt_old=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt3t_tt_old,marker='P',color='green')
fname="./ecbs-4t-tt/"+const_name
runtime4t_tt,opt4t_tt=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),opt4t_tt,marker='D',color='purple')
fname="./ecbs-4t-tt-old/"+const_name
runtime4t_tt_old,opt4t_tt_old=get_runtime_optimality(fname)
l6,=plt.plot(get_num_agents(fname),opt4t_tt_old,marker='p',color='black')
plt.legend(handles=[l0,l1,l2,l3,l4,l5,l6],labels=['ecbs','ecbs-2t-tt','ecbs-2t-mk','ecbs-3t-tt','ecbs-3t-mk','ecbs-4t-tt','ecbs-4t-mk'])
plt.savefig("ecbs_compare_opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


##########################################################################################

