import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd


limit=60000
def read_data(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame.eval('OptimalRatio=(makespan)/makespanLB',inplace=True)
	#frame.replace(np.nan,300000)
	frame=frame[(frame['numAgents'].isin([num]))]
	#print(frame)
	#frame=frame[frame['runtime']<60000]
	frame.loc[frame['runtime']>100000,'OptimalRatio']=1.2
	frame.loc[frame['runtime']>limit,'runtime']=limit
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
plt.ylim(0, 60)
plt.xlim(0, 200)
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time (s)')
fname="./2t/"+const_name
runtime2t,opt2t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime2t,marker='D',color='green')
fname="./2s/"+const_name
runtime2s,opt2s=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime2s,marker='x',color='orange')
fname="./4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime4t,marker='o',color='blue')
fname="./4s-22/"+const_name
runtime4s22,opt4s22=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime4s22,marker='s',color='red')
fname="./8t/"+const_name
runtime8t,opt8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime8t,marker='*',color='purple')
fname="./8s/"+const_name
runtime8s,opt8s=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),runtime8s,marker='P',color='black')

plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP-2t','ILP-2s','ILP-4t','ILP-4s','ILP-8t','ILP-8s'])
plt.savefig("64x64-runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()



plt.figure()
plt.xlim(0, 200)
plt.xlabel('Number of Robots (N)')
plt.ylabel('Optimality Ratio')

fname="./2t/"+const_name
runtime2t,opt2t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt2t,marker='D',color='green')
fname="./2s/"+const_name
runtime2s,opt2s=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt2s,marker='x',color='orange')
fname="./4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt4t,marker='o',color='blue')
fname="./4s-22/"+const_name
runtime4s22,opt4s22=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt4s22,marker='s',color='red')
fname="./8t/"+const_name
runtime8t,opt8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt8t,marker='*',color='purple')
fname="./8s/"+const_name
runtime8s,opt8s=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),opt8s,marker='P',color='black')

plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP-2t','ILP-2s','ILP-4t','ILP-4s','ILP-8t','ILP-8s'])
plt.savefig("64x64-opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


limit=120000


plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time (s)')
plt.xlim(0, 200)
fname="./4s-22/"+const_name
runtime4s22,opt4s22=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime4s22,marker='D',color='green')
fname="./4s-24/"+const_name
runtime4s24,opt4s24=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime4s24,marker='x',color='orange')
fname="./4s-32/"+const_name
runtime4s32,opt4s32=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime4s32,marker='o',color='blue')
fname="./4s-33/"+const_name
runtime4s33,opt4s33=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime4s33,marker='s',color='red')
fname="./4s-42/"+const_name
runtime4s42,opt4s42=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime4s42,marker='*',color='purple')
fname="./4s-44/"+const_name
runtime4s44,opt4s44=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),runtime4s44,marker='P',color='black')

plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['4s-4x2','4s-4x4','4s-6x2','4s-6x3','4s-8x2','4s-8x4'])
plt.savefig("64x64-dif-runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()



plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time (s)')
plt.xlim(0, 200)
fname="./4s-22/"+const_name
runtime4s22,opt4s22=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt4s22,marker='D',color='green')
fname="./4s-24/"+const_name
runtime4s24,opt4s24=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt4s24,marker='x',color='orange')
fname="./4s-32/"+const_name
runtime4s32,opt4s32=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt4s32,marker='o',color='blue')
fname="./4s-33/"+const_name
runtime4s33,opt4s33=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt4s33,marker='s',color='red')
fname="./4s-42/"+const_name
runtime4s42,opt4s42=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt4s42,marker='*',color='purple')
fname="./4s-44/"+const_name
runtime4s44,opt4s44=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),opt4s44,marker='P',color='black')

plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['4s-4x2','4s-4x4','4s-6x2','4s-6x3','4s-8x2','4s-8x4'])
plt.savefig("64x64-dif-opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()
