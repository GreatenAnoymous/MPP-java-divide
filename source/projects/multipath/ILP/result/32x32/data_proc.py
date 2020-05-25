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
	print(frame)
	temp=frame[['numAgents','makespan','runtime','makespanLB','OptimalRatio']].mean()
	print("\n")
	print(temp)
	return temp
	
def get_sr(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','makespan','runtime','makespanLB'])
	frame=frame[(frame['numAgents'].isin([num]))]
	print(frame)
	if len(frame)==0:
		return 0
	sr=1.-frame['runtime'].isna().sum()/25.
	#=1-frame['runtimeTS'].isna().sum()/25
	return sr
		
	
def read_data2(filename,num):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','cost','runtime','costLB'])
	frame.eval('OptimalRatio=cost/costLB',inplace=True)
	frame=frame[(~frame['runtime'].isin([np.nan]))]
	frame=frame[(frame['numAgents'].isin([num]))]
	#frame['OptimalRatio'][<0] = frame['OptimalRatio'].replace(np.nan, 0)
	print(frame)
	temp=frame[['numAgents','cost','runtime','costLB','OptimalRatio']].mean()
	print("\n")
	print(temp)
	return temp
	
def get_num_agents(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['numAgents','cost','runtime','costLB'])
	#print(frame)
	frame.drop_duplicates('numAgents',inplace=True)
	return frame['numAgents'].values

###############################################################

#print(get_num_agents('./ILP/random-32-32-10-random.txt'))	
#exit()
################################################################
numAgents=get_num_agents('./ILP/random-32-32-10-random.txt')
meanTimeILP=[]
meanTime2t=[]
meanTime3t=[]
meanTime4t=[]
meanTime5t=[]
meanTime6t=[]
#meanTime6t=[]
opt=[]
opt2t=[]
opt3t=[]
opt4t=[]
opt5t=[]
opt6t=[]
#opt16s8t55=[]

numAgents=get_num_agents('./ILP/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./ILP/random-32-32-10-random.txt',a)
	meanTimeILP.append(tmp['runtime'])
	opt.append(tmp['OptimalRatio'])
	
numAgents=get_num_agents('./2t/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./2t/random-32-32-10-random.txt',a)
	meanTime2t.append(tmp['runtime'])
	opt2t.append(tmp['OptimalRatio'])	

numAgents=get_num_agents('./3t/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./3t/random-32-32-10-random.txt',a)
	meanTime3t.append(tmp['runtime'])
	opt3t.append(tmp['OptimalRatio'])

numAgents=get_num_agents('./4t/random-32-32-10-random.txt')	
for a in numAgents:
	tmp=read_data('./4t/random-32-32-10-random.txt',a)
	meanTime4t.append(tmp['runtime'])
	opt4t.append(tmp['OptimalRatio'])

numAgents=get_num_agents('./5t/random-32-32-10-random.txt')		
for a in numAgents:
	tmp=read_data('./5t/random-32-32-10-random.txt',a)
	meanTime5t.append(tmp['runtime'])
	opt5t.append(tmp['OptimalRatio'])
	
numAgents=get_num_agents('./6t/random-32-32-10-random.txt')		
for a in numAgents:
	tmp=read_data('./6t/random-32-32-10-random.txt',a)
	meanTime6t.append(tmp['runtime'])
	opt6t.append(tmp['OptimalRatio'])

########################################################################	
plt.figure()
plt.ylim(0, 150)
numAgents=get_num_agents('./ILP/random-32-32-10-random.txt')
l0,=plt.plot(numAgents,np.array(meanTimeILP)/1000.,marker='o',color='blue')
numAgents=get_num_agents('./2t/random-32-32-10-random.txt')
l1,=plt.plot(numAgents,np.array(meanTime2t)/1000.,marker='s',color='red')
numAgents=get_num_agents('./3t/random-32-32-10-random.txt')
l2,=plt.plot(numAgents,np.array(meanTime3t)/1000.,marker='D',color='green')
numAgents=get_num_agents('./4t/random-32-32-10-random.txt')
l3,=plt.plot(numAgents,np.array(meanTime4t)/1000.,marker='x',color='orange')
numAgents=get_num_agents('./5t/random-32-32-10-random.txt')
l4,=plt.plot(numAgents,np.array(meanTime5t)/1000.,marker=5,color='black')
numAgents=get_num_agents('./6t/random-32-32-10-random.txt')
l5,=plt.plot(numAgents,np.array(meanTime6t)/1000.,marker=6,color='purple')
#l5,=plt.plot([100,200,300],meanTime8t,marker='.',color='purple')
#l1,=plt.plot(numAgents,np.array(meantime9SS)/1000.,marker='s',color='red')


plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP','ILP-2t','ILP-3t','ILP-4t','ILP-5t','ILP-6t'])
plt.show()



plt.figure()
numAgents=get_num_agents('./ILP/random-32-32-10-random.txt')
l0,=plt.plot(numAgents,opt,marker='o',color='blue')
numAgents=get_num_agents('./2t/random-32-32-10-random.txt')
l1,=plt.plot(numAgents,opt2t,marker='s',color='red')
numAgents=get_num_agents('./3t/random-32-32-10-random.txt')
l2,=plt.plot(numAgents,opt3t,marker='D',color='green')
numAgents=get_num_agents('./4t/random-32-32-10-random.txt')
l3,=plt.plot(numAgents,opt4t,marker='x',color='orange')
numAgents=get_num_agents('./5t/random-32-32-10-random.txt')
l4,=plt.plot(numAgents,opt5t,marker=5,color='black')
numAgents=get_num_agents('./6t/random-32-32-10-random.txt')
l5,=plt.plot(numAgents,opt6t,marker=6,color='purple')
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP','ILP-2t','ILP-3t','ILP-4t','ILP-5t','ILP-6t'])
plt.show()
################################################################################################

numAgents=get_num_agents('./ILP/random-32-32-10-random.txt')
meanTimeILP_tt=[]
meanTime2t_tt=[]
meanTime4t_tt=[]
meanTimeECBS_tt=[]
meanTimeECBS2t_tt=[]
meanTimeECBS4t_tt=[]
#meanTime6t=[]
optILP_tt=[]
opt2t_tt=[]
opt4t_tt=[]
optECBS_tt=[]
optECBS2t_tt=[]
optECBS4t_tt=[]
#opt16s8t55=[]

numAgents=get_num_agents('./ILP-tt/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./ILP-tt/random-32-32-10-random.txt',a)
	meanTimeILP_tt.append(tmp['runtime'])
	optILP_tt.append(tmp['OptimalRatio'])
	
numAgents=get_num_agents('./2t-tt/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./2t-tt/random-32-32-10-random.txt',a)
	meanTime2t_tt.append(tmp['runtime'])
	
	opt2t_tt.append(tmp['OptimalRatio'])	

numAgents=get_num_agents('./4t-tt/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./4t-tt/random-32-32-10-random.txt',a)
	meanTime4t_tt.append(tmp['runtime'])
	opt4t_tt.append(tmp['OptimalRatio'])

numAgents=get_num_agents('./ecbs-tt/random-32-32-10-random.txt')	
for a in numAgents:
	tmp=read_data('./ecbs-tt/random-32-32-10-random.txt',a)
	meanTimeECBS_tt.append(tmp['runtime'])
	
	if tmp['OptimalRatio']<1:
		optECBS_tt.append(1)
	else:
		optECBS_tt.append(tmp['OptimalRatio'])

numAgents=get_num_agents('./ecbs-2t-tt/random-32-32-10-random.txt')		
for a in numAgents:
	tmp=read_data('./ecbs-2t-tt/random-32-32-10-random.txt',a)
	meanTimeECBS2t_tt.append(tmp['runtime'])
	optECBS2t_tt.append(tmp['OptimalRatio'])

numAgents=get_num_agents('./ecbs-4t-tt/random-32-32-10-random.txt')		
for a in numAgents:
	tmp=read_data('./ecbs-4t-tt/random-32-32-10-random.txt',a)
	meanTimeECBS4t_tt.append(tmp['runtime'])
	optECBS4t_tt.append(tmp['OptimalRatio'])

########################################################################	
plt.figure()
plt.ylim(0, 300)
numAgents=get_num_agents('./ILP-tt/random-32-32-10-random.txt')
l0,=plt.plot(numAgents,np.array(meanTimeILP_tt)/1000.,marker='o',color='blue')
numAgents=get_num_agents('./2t-tt/random-32-32-10-random.txt')
l1,=plt.plot(numAgents,np.array(meanTime2t_tt)/1000.,marker='s',color='red')
numAgents=get_num_agents('./4t-tt/random-32-32-10-random.txt')
l2,=plt.plot(numAgents,np.array(meanTime4t_tt)/1000.,marker='D',color='green')
numAgents=get_num_agents('./ecbs-tt/random-32-32-10-random.txt')
l3,=plt.plot(numAgents,np.array(meanTimeECBS_tt)/1000.,marker='x',color='orange')
numAgents=get_num_agents('./ecbs-2t-tt/random-32-32-10-random.txt')
l4,=plt.plot(numAgents,np.array(meanTimeECBS2t_tt)/1000.,marker=5,color='black')
numAgents=get_num_agents('./ecbs-4t-tt/random-32-32-10-random.txt')
l5,=plt.plot(numAgents,np.array(meanTimeECBS4t_tt)/1000.,marker=6,color='purple')
#l5,=plt.plot([100,200,300],meanTime8t,marker='.',color='purple')
#l1,=plt.plot(numAgents,np.array(meantime9SS)/1000.,marker='s',color='red')


plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid ')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP','ILP-2t','ILP-4t','ECBS','ECBS-2t','ECBS-4t'])
plt.show()



plt.figure()
numAgents=get_num_agents('./ILP-tt/random-32-32-10-random.txt')
l0,=plt.plot(numAgents,optILP_tt,marker='o',color='blue')
numAgents=get_num_agents('./2t-tt/random-32-32-10-random.txt')
l1,=plt.plot(numAgents,opt2t_tt,marker='s',color='red')
numAgents=get_num_agents('./4t-tt/random-32-32-10-random.txt')
l2,=plt.plot(numAgents,opt4t_tt,marker='D',color='green')
numAgents=get_num_agents('./ecbs-tt/random-32-32-10-random.txt')
l3,=plt.plot(numAgents,optECBS_tt,marker='x',color='orange')
numAgents=get_num_agents('./ecbs-2t-tt/random-32-32-10-random.txt')
l4,=plt.plot(numAgents,optECBS2t_tt,marker=5,color='black')
numAgents=get_num_agents('./ecbs-4t-tt/random-32-32-10-random.txt')
l5,=plt.plot(numAgents,optECBS4t_tt,marker=6,color='purple')
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['ILP','ILP-2t','ILP-4t','ECBS','ECBS-2t','ECBS-4t'])
plt.show()
################################################################################################


meanTime2t=[]
meanTimeEven2t=[]
meanTime3=[]
meanTime4=[]
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random.txt')
for a in numAgents:
	tmp=read_data('./2t-even-sum-of-costs/random-32-32-10-random.txt',a)
	meanTimeEven2t.append(tmp['runtime'])
	#optILP_tt.append(tmp['OptimalRatio'])

numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random-2.txt')
for a in numAgents:
	tmp=read_data('./2t-even-sum-of-costs/random-32-32-10-random-2.txt',a)
	meanTime2t.append(tmp['runtime'])
	#optILP_tt.append(tmp['OptimalRatio'])
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random-3.txt')
for a in numAgents:
	tmp=read_data('./2t-even-sum-of-costs/random-32-32-10-random-3.txt',a)
	meanTime3.append(tmp['runtime'])
	#optILP_tt.append(tmp['OptimalRatio'])
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random-4.txt')
for a in numAgents:
	tmp=read_data('./2t-even-sum-of-costs/random-32-32-10-random-4.txt',a)
	meanTime4.append(tmp['runtime'])
	#optILP_tt.append(tmp['OptimalRatio'])
plt.figure()
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random.txt')
l0,=plt.plot(numAgents,np.array(meanTimeEven2t)/1000,marker='o',color='blue')
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random-2.txt')
l1,=plt.plot(numAgents,np.array(meanTime2t)/1000,marker='s',color='red')
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random-3.txt')
l2,=plt.plot(numAgents,np.array(meanTime3)/1000,marker='x',color='green')
numAgents=get_num_agents('./2t-even-sum-of-costs/random-32-32-10-random-4.txt')
l3,=plt.plot(numAgents,np.array(meanTime4)/1000,marker='D',color='orange')
plt.xlabel(r'Number of Agents in 32 $\times$ 32 grid')
plt.ylabel('Runtime/s')
plt.xlim(0,110)
plt.ylim(0,61)
plt.legend(handles=[l0,l1,l2,l3],labels=['2t-even-costs','2t-even-time','2t-1/2','2t-1/4'])
plt.show()
