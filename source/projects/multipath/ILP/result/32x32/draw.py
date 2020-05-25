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
	return np.array(rt)/1000.,opt

const_name="random-32-32-10-random.txt"


#######################################################
#Compare old split with total time split
#######################################################
plt.figure()
plt.ylim(0, 150)
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Average computation time in seconds')
fname="./2t-tt/"+const_name
runtime2t_tt,opt2t_tt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime2t_tt,marker='o',color='blue')
fname="./2t-tt-old/"+const_name
runtime2t_tt_old,opt2t_tt_old=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime2t_tt_old,marker='s',color='red')
fname="./4t-tt/"+const_name
runtime4t_tt,opt4t_tt=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime4t_tt,marker='D',color='green')
fname="./4t-tt-old/"+const_name
runtime4t_tt_old,opt4t_tt_old=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime4t_tt_old,marker='x',color='orange')
fname="./ILP-tt/"+const_name
runtimeILP,opt_ILP=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtimeILP,marker=5,color='purple')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['2t-tt','2-way','4t-tt','4-way','ILP-tt'])
plt.show()


plt.figure()
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Optimality Ratio')
fname="./2t-tt/"+const_name
runtime2t_tt,opt2t_tt=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt2t_tt,marker='o',color='blue')
fname="./2t-tt-old/"+const_name
runtime2t_tt_old,opt2t_tt_old=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt2t_tt_old,marker='s',color='red')
fname="./4t-tt/"+const_name
runtime4t_tt,opt4t_tt=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt4t_tt,marker='D',color='green')
fname="./4t-tt-old/"+const_name
runtime4t_tt_old,opt4t_tt_old=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt4t_tt_old,marker='x',color='orange')
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
fname="./3t/"+const_name
runtime3t,opt3t=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime3t,marker=5,color='orange')
plt.legend(handles=[l0,l1,l2,l4],labels=['ILP','111 split','212 split','3t'])
plt.show()

plt.figure()
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('optmality ratio')
fname="./ILP/"+const_name
runtimeILP,optILP=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),optILP,marker='o',color='blue')
fname="./ILP111/"+const_name
runtime111,opt111=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt111,marker='s',color='red')
fname="./ILP212/"+const_name
runtime212,opt212=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt212,marker='D',color='green')
fname="./3t/"+const_name
runtime3t,opt3t=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt3t,marker=5,color='orange')
plt.legend(handles=[l0,l1,l2,l3],labels=['ILP','111 split','212 split','3t'])
plt.show()
##########################################################################################

