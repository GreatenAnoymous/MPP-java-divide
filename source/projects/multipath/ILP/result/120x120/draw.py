import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd

timeLimit=300000

font1 = {'family' : 'Serif',
'weight' : 'normal',
'size'   : 16,
}
def set_title(option):
	if option=="runtime":
		plt.xlabel('Number of Robots (N)',fontsize=18)
		plt.ylabel('Computation Time (s)',fontsize=18)
	else:
		plt.xlabel('Number of Robots (N)',fontsize=18)
		plt.ylabel('Optimality Ratio',fontsize=18)
	plt.tick_params(labelsize=16)


def read_data(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame.eval('OptimalRatio=(makespan)/makespanLB',inplace=True)
	frame.replace(np.nan,timeLimit)
	frame=frame[(frame['numAgents'].isin([num]))]
	#print(frame)
	frame=frame[frame['runtime']<timeLimit]
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
	frame=frame[frame['runtime']<timeLimit]
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

const_name="random-120-120-00-random.txt"
const_name2="random-120-120-05-random-new.txt"

################################################################################################

timeLimit=300000

plt.figure()
set_title("runtime")
fname="./4s8t/"+const_name2
runtime4s8t,opt4s8t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime4s8t,marker='o',color='blue')
fname="./4s16t/"+const_name2
runtime4s16t,opt4s16t=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime4s16t,marker='s',color='red')
fname="./9s8t/"+const_name2
runtime9s8t,opt9s8t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime9s8t,marker='*',color='brown')
fname="./9s16t/"+const_name2
runtime9s16t,opt9s16t=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime9s16t,marker='x',color='orange')
fname="./16s8t/"+const_name2
runtime16s8t,opt16s8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime16s8t,marker='P',color='green')
fname="./16t/"+const_name2
runtime16t,opt16t=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),runtime16t,marker='D',color='black')
plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['4s8t','4s16t','9s8t','9s16t','16s8t','16t'],prop=font1)
plt.savefig("ts-runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
set_title("optimality")
fname="./4s8t/"+const_name2
runtime4s8t,opt4s8t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt4s8t,marker='o',color='blue')
fname="./4s16t/"+const_name2
runtime4s16t,opt4s16t=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt4s16t,marker='s',color='red')
fname="./9s8t/"+const_name2
runtime9s8t,opt9s8t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt9s8t,marker='*',color='brown')
fname="./9s16t/"+const_name2
runtime9s16t,opt9s16t=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt9s16t,marker='x',color='orange')
fname="./16s8t/"+const_name2
runtime16s8t,opt16s8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt16s8t,marker='P',color='green')
fname="./16t/"+const_name2
runtime16t,opt16t=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),opt16t,marker='D',color='black')
#plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['4s8t','4s16t','9s8t','9s16t','16s8t','16t'])
plt.savefig("ts-opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()
