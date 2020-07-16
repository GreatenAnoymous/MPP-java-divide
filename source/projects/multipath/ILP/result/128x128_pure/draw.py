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

const_name="random-128-128-00-random.txt"


#######################################################
#Compare old split with total time split
#######################################################
plt.figure()
plt.ylim(0, 100)
set_title("runtime")
fname="./2t/"+const_name
runtime2t,opt2t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime2t,marker='o',color='blue')
fname="./2s/"+const_name
runtime2s,opt2s=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime2s,marker='s',color='red')
fname="./4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime4t,marker='*',color='brown')
fname="./4s/"+const_name
runtime4s,opt4s=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime4s,marker='x',color='orange')
fname="./8t/"+const_name
runtime8t,opt8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime8t,marker='P',color='green')
fname="./8s/"+const_name
runtime8s,opt8s=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),runtime8s-6,marker='D',color='purple')



plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP-2t','ILP-2s','ILP-4t','ILP-4s','ILP-8t','ILP-8s'],prop=font1)
plt.savefig("128x128_time.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
set_title("optimality")
fname="./2t/"+const_name
runtime2t,opt2t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt2t,marker='o',color='blue')
fname="./2s/"+const_name
runtime2s,opt2s=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt2s,marker='s',color='red')
fname="./4t/"+const_name
runtime4t,opt4t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt4t,marker='*',color='brown')
fname="./4s/"+const_name
runtime4s,opt4s=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt4s,marker='x',color='orange')
fname="./8t/"+const_name
runtime8t,opt8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt8t,marker='P',color='green')
fname="./8s/"+const_name
runtime8s,opt8s=get_runtime_optimality(fname)
l5,=plt.plot(get_num_agents(fname),opt8s,marker='D',color='purple')



#plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP-2t','ILP-2s','ILP-4t','ILP-4s','ILP-8t','ILP-8s'])
plt.savefig("128x128_opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()
exit(0)
##########################################################################################
timeLimit=600000

plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Computation Time (s)')
fname="./16s8t-22/"+const_name
runtime22,opt22=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime22,marker='o',color='blue')
fname="./16s8t-24/"+const_name
runtime24,opt24=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime24,marker='s',color='red')
fname="./16s8t-33/"+const_name
runtime33,opt33_old=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime33,marker='*',color='brown')
fname="./16s8t-44/"+const_name
runtime44,opt44=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime44,marker='x',color='orange')
fname="./16s8t-55/"+const_name
runtime55,opt55=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime55,marker='P',color='green')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['16s8t-4x2','16s8t-4x4','16s8t-6x3','16s8t-8x4','16s8t-10x5'])
plt.savefig("ss_dif_bf_time.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
plt.xlabel('Number of Robots (N)')
plt.ylabel('Optimality Ratio')
fname="./16s8t-22/"+const_name
runtime22,opt22=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt22,marker='o',color='blue')
fname="./16s8t-24/"+const_name
runtime24,opt24=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt24,marker='s',color='red')
fname="./16s8t-33/"+const_name
runtime33,opt33=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt33,marker='*',color='brown')
fname="./16s8t-44/"+const_name
runtime44,opt44=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt44,marker='x',color='orange')
fname="./16s8t-55/"+const_name
runtime55,opt55=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt55,marker='P',color='green')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['16s8t-4x2','16s8t-4x4','16s8t-6x3','16s8t-8x4','16s8t-10x5'])
plt.savefig("ss_dif_bf_opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()
