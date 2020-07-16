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
const_name2="random-128-128-05-random.txt"

#######################################################
#Compare old split with total time split
#######################################################
plt.figure()
plt.ylim(0, 300)
set_title("runtime")
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


plt.legend(handles=[l0,l1,l2,l3,l4,l5,l6],labels=['ecbs','ecbs-2t-tt','ecbs-2t-mk','ecbs-3t-tt','ecbs-3t-mk','ecbs-4t-tt','ecbs-4t-mk'],prop=font1)
plt.savefig("ecbs_compare_runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
set_title("optimality")
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
#plt.legend(handles=[l0,l1,l2,l3,l4,l5,l6],labels=['ecbs','ecbs-2t-tt','ecbs-2t-mk','ecbs-3t-tt','ecbs-3t-mk','ecbs-4t-tt','ecbs-4t-mk'])
plt.savefig("ecbs_compare_opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


##########################################################################################
timeLimit=600000

plt.figure()
set_title("runtime")
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
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['16s8t-4x2','16s8t-4x4','16s8t-6x3','16s8t-8x4','16s8t-10x5'],prop=font1)
plt.savefig("ss_dif_bf_time.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
set_title("optimality")
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
#plt.legend(handles=[l0,l1,l2,l3,l4],labels=['16s8t-4x2','16s8t-4x4','16s8t-6x3','16s8t-8x4','16s8t-10x5'])
plt.savefig("ss_dif_bf_opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()




################################################################################################

timeLimit=300000

plt.figure()
set_title("runtime")
fname="./4s8t/"+const_name
runtime4s8t,opt4s8t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),runtime4s8t,marker='o',color='blue')
fname="./4s8t/"+const_name2
runtime4s8t05,opt4s8t05=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),runtime4s8t05,marker='s',color='red')
fname="./4s16t/"+const_name
runtime4s16t,opt4s16t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),runtime4s16t,marker='*',color='brown')
fname="./4s16t/"+const_name2
runtime4s16t05,opt4s16t05=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),runtime4s16t05,marker='x',color='orange')
fname="./16s8t-22/"+const_name
runtime16s8t,opt16s8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),runtime16s8t,marker='P',color='green')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['4s8t','4s8t05','4s16t','4s16t05','16s8t'],prop=font1)
plt.savefig("ts-runtime.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()


plt.figure()
set_title("optimality")
fname="./4s8t/"+const_name
runtim4s8t,opt4s8t=get_runtime_optimality(fname)
l0,=plt.plot(get_num_agents(fname),opt4s8t,marker='o',color='blue')
fname="./4s8t05/"+const_name2
runtime4s8t05,opt4s8t05=get_runtime_optimality(fname)
l1,=plt.plot(get_num_agents(fname),opt4s8t05,marker='s',color='red')
fname="./4s16t/"+const_name
runtime4s16t,opt4s16t=get_runtime_optimality(fname)
l2,=plt.plot(get_num_agents(fname),opt4s16t,marker='*',color='brown')
fname="./4s16t/"+const_name2
runtime4s16t05,opt4s16t05=get_runtime_optimality(fname)
l3,=plt.plot(get_num_agents(fname),opt4s16t05,marker='x',color='orange')
fname="./16s8t-22/"+const_name
runtime16s8t,opt16s8t=get_runtime_optimality(fname)
l4,=plt.plot(get_num_agents(fname),opt16s8t,marker='P',color='green')
#plt.legend(handles=[l0,l1,l2,l3,l4],labels=['4s8t','4s8t05','4s16t','4s16t05','16s8t'])
plt.savefig("ts-opt.pdf", bbox_inches="tight", pad_inches=0.05)
plt.show()
