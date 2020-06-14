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
		rt.append(tmp['runtime'])
		opt.append(tmp['OptimalRatio'])
	return np.array(rt)/1000.,opt
	
def get_success_rate(filename,timeLimit):
	numAgents=get_num_agents(filename)
	sr=[]
	for a in numAgents:
		sr.append(read_data2(filename,a,timeLimit))
	return np.array(sr)

const_name="brc202d-random.txt"


#######################################################
#ECBS with time-split
#######################################################
plt.figure()
plt.ylim(0, 300)
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time(s)')
fname="./ecbs/"+const_name
runtime,opt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime,marker='o',color='blue')
fname="./ecbs-2t/"+const_name
runtime2t,opt2t=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime2t,marker='s',color='red')
fname="./ecbs-4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime4t,marker='D',color='green')
fname="./ecbs-8t/"+const_name
runtime8t,opt8t=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime8t,marker='x',color='orange')
plt.legend(handles=[l0,l1,l2,l3],labels=['ecbs','ecbs-2t','ecbs-4t','ecbs-8t'])
plt.savefig("brc202d_runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Optimality Ratio')
fname="./ecbs/"+const_name
runtime,opt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt,marker='o',color='blue')
fname="./ecbs-2t/"+const_name
runtime2t,opt2t=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt2t,marker='s',color='red')
fname="./ecbs-4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt4t,marker='D',color='green')
fname="./ecbs-8t/"+const_name
runtime8t,opt8t=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt8t,marker='x',color='orange')
plt.legend(handles=[l0,l1,l2,l3],labels=['ecbs','ecbs-2t','ecbs-4t','ecbs-8t'])
plt.savefig("brc202d_opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()

'''
plt.figure()
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Success rate')
fname="./2t-tt/"+const_name
timeLimit=150000
sr2t_tt=get_success_rate(fname,timeLimit)
l0,=plt.plot(get_num_agents(fname),sr2t_tt,marker='o',color='blue')
fname="./2t-tt-old/"+const_name
sr2t_tt_old=get_success_rate(fname,timeLimit)
l1,=plt.plot(get_num_agents(fname),sr2t_tt_old,marker='s',color='red')
fname="./4t-tt/"+const_name
sr4t_tt=get_success_rate(fname,timeLimit)
l2,=plt.plot(get_num_agents(fname),sr4t_tt,marker='D',color='green')
fname="./4t-tt-old/"+const_name
sr4t_tt_old=get_success_rate(fname,timeLimit)
l3,=plt.plot(get_num_agents(fname),sr4t_tt_old,marker='x',color='orange')
plt.legend(handles=[l0,l1,l2,l3],labels=['2t-tt','2-way','4t-tt','4-way'])
plt.show()


#########################################################################################
#212 vs 111 split for Min  Makespan MPP
plt.figure()
plt.ylim(0, 150)
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Average computation time in seconds')
fname="./ILP/"+const_name
runtimeILP,optILP=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtimeILP,marker='o',color='blue')
fname="./ILP111/"+const_name
runtime111,opt111=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime111,marker='s',color='red')
fname="./ILP212/"+const_name
runtime212,opt212=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime212,marker='D',color='green')
#fname="./3t/"+const_name
#runtime3t,opt3t=get_runtime_optimality(fname)
#l3,=plt.plot(get_num_agents(fname),runtime3t,marker=5,color='orange')
plt.legend(handles=[l0,l1,l2],labels=['ILP','111 split','212 split'])
plt.show()

plt.figure()
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('optmality ratio')
#fname="./ILP/"+const_name
#runtimeILP,optILP=get_runtime_optimality(fname)
#l0,=plt.plot(get_num_agents(fname),optILP,marker='o',color='blue')
fname="./ILP111/"+const_name
runtime111,opt111=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt111,marker='s',color='red')
fname="./ILP212/"+const_name
runtime212,opt212=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt212,marker='D',color='green')
#fname="./3t/"+const_name
#runtime3t,opt3t=get_runtime_optimality(fname)
#l3,=plt.plot(get_num_agents(fname),opt3t,marker=5,color='orange')
plt.legend(handles=[l1,l2],labels=['111 split','212 split'])
plt.show()


plt.figure()
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Success rate')
fname="./ILP/"+const_name
timeLimit=150000
srILP=get_success_rate(fname,timeLimit)
l0,=plt.plot(get_num_agents(fname),srILP,marker='o',color='blue')
fname="./ILP111/"+const_name
sr111=get_success_rate(fname,timeLimit)
l1,=plt.plot(get_num_agents(fname),sr111,marker='s',color='red')
fname="./ILP212/"+const_name
sr212=get_success_rate(fname,timeLimit)
l2,=plt.plot(get_num_agents(fname),sr212,marker='D',color='green')
#fname="./4t-tt-old/"+const_name
#sr4t_tt_old=get_success_rate(fname,timeLimit)
#l3,=plt.plot(get_num_agents(fname),sr4t_tt_old,marker='x',color='orange')
plt.legend(handles=[l0,l1,l2],labels=['ILP','111-split','212-split'])
plt.show()
##########################################################################################

'''
