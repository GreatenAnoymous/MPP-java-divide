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
		
	
def read_data2(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['runtimeSTS','MakeSpanSTS','runtime4TS','MakeSpan4TS','runtime8TS','MakeSpan8TS','MakeSpanLB'])
	#frame=frame[(~frame['runtimeSTS'].isin([600000.0]))]
	#frame=frame[(~frame['runtime4TS'].isin([600000.0]))]
	frame.eval('OptimalRatioSTS=MakeSpanSTS/MakeSpanLB',inplace=True)
	frame.eval('OptimalRatio4TS=MakeSpan4TS/MakeSpanLB',inplace=True)
	frame.eval('OptimalRatio8TS=MakeSpan8TS/MakeSpanLB',inplace=True)
	print(frame)
	temp=frame[['runtimeSTS','MakeSpanSTS','runtime4TS','MakeSpan4TS','runtime8TS','MakeSpan8TS','MakeSpanLB','OptimalRatioSTS','OptimalRatio4TS','OptimalRatio8TS']].mean()
	print("\n")
	print(temp)
	return temp
	

################################################################
numAgents=np.arange(100,2100,100)
meanTime16s8t22=[]
meanTime16s8t24=[]
meanTime16s8t33=[]
meanTime16s8t44=[]
meanTime16s8t55=[]
opt16s8t22=[]
opt16s8t24=[]
opt16s8t33=[]
opt16s8t44=[]
opt16s8t55=[]

for a in numAgents:
	tmp=read_data('./16s8t-22/random-128-128-00-random.txt',a)
	meanTime16s8t22.append(tmp['runtime'])
	opt16s8t22.append(tmp['OptimalRatio'])

for a in numAgents:
	tmp=read_data('./16s8t-24/random-128-128-00-random.txt',a)
	meanTime16s8t24.append(tmp['runtime'])
	opt16s8t24.append(tmp['OptimalRatio'])	

for a in numAgents:
	tmp=read_data('./16s8t-33/random-128-128-00-random.txt',a)
	meanTime16s8t33.append(tmp['runtime'])
	opt16s8t33.append(tmp['OptimalRatio'])
	
for a in numAgents:
	tmp=read_data('./16s8t-44/random-128-128-00-random.txt',a)
	meanTime16s8t44.append(tmp['runtime'])
	opt16s8t44.append(tmp['OptimalRatio'])
	
for a in numAgents:
	tmp=read_data('./16s8t-55/random-128-128-00-random.txt',a)
	meanTime16s8t55.append(tmp['runtime'])
	opt16s8t55.append(tmp['OptimalRatio'])

########################################################################	
plt.figure()
plt.ylim(0, 600)
l0,=plt.plot(numAgents,np.array(meanTime16s8t22)/1000.,marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(meanTime16s8t33)/1000.,marker='s',color='red')
l2,=plt.plot(numAgents,np.array(meanTime16s8t44)/1000.,marker='D',color='green')
l3,=plt.plot(numAgents,np.array(meanTime16s8t24)/1000.,marker='x',color='orange')
l4,=plt.plot(numAgents,np.array(meanTime16s8t55)/1000.,marker=5,color='black')
#l5,=plt.plot([100,200,300],meanTime8t,marker='.',color='purple')
#l1,=plt.plot(numAgents,np.array(meantime9SS)/1000.,marker='s',color='red')


plt.xlabel(r'Number of Agents in 128 $\times$ 128 grid')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['16s8t-4x2','16s8t-4x4','16s8t-6x3','16s8t-8x4','16s8t-10x5'])
plt.show()



plt.figure()
l0,=plt.plot(numAgents,opt16s8t22,marker='o',color='blue')
l1,=plt.plot(numAgents,opt16s8t33,marker='s',color='red')
l2,=plt.plot(numAgents,opt16s8t44,marker='D',color='green')
l3,=plt.plot(numAgents,opt16s8t24,marker='x',color='orange')
l4,=plt.plot(numAgents,opt16s8t55,marker=5,color='black')
plt.xlabel(r'Number of Agents in 128 $\times$ 128 grid')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['16s8t-4x2','16s8t-4x4','16s8t-6x3','16s8t-8x4','16s8t-10x5'])
plt.show()
################################################################################################

numAgents=np.arange(100,500,100)
meanTimeECBS=[]
meanTimeECBS2t=[]
meanTimeECBS4t=[]
meanTimeECBS8t=[]
meanTimeECBS16t=[]
optECBS=[]
optECBS2t=[]

optECBS4t=[]
optECBS8t=[]
optECBS16t=[]

numAgents=np.arange(100,500,100)
for a in numAgents:
	tmp=read_data('./ecbs/random-128-128-00-random.txt',a)
	meanTimeECBS.append(tmp['runtime'])
	optECBS.append(tmp['OptimalRatio'])

numAgents=np.arange(100,700,100)
for a in numAgents:
	tmp=read_data('./ecbs-2t/random-128-128-00-random.txt',a)
	meanTimeECBS2t.append(tmp['runtime'])
	optECBS2t.append(tmp['OptimalRatio'])


numAgents=np.arange(100,1000,100)
for a in numAgents:
	tmp=read_data('./ecbs-4t/random-128-128-00-random.txt',a)
	meanTimeECBS4t.append(tmp['runtime'])
	optECBS4t.append(tmp['OptimalRatio'])	

numAgents=np.arange(100,1200,100)
for a in numAgents:
	tmp=read_data('./ecbs-8t/random-128-128-00-random.txt',a)
	meanTimeECBS8t.append(tmp['runtime'])
	optECBS8t.append(tmp['OptimalRatio'])

numAgents=np.arange(100,1300,100)
for a in numAgents:
	tmp=read_data('./ecbs-16t/random-128-128-00-random.txt',a)
	meanTimeECBS16t.append(tmp['runtime'])
	optECBS16t.append(tmp['OptimalRatio'])
plt.figure()

plt.ylim(0, 600)
numAgents=np.arange(100,500,100)
l0,=plt.plot(numAgents,np.array(meanTimeECBS)/1000.,marker='o',color='blue')
numAgents=np.arange(100,700,100)
l1,=plt.plot(numAgents,np.array(meanTimeECBS2t)/1000.,marker='x',color='orange')
numAgents=np.arange(100,1000,100)
l2,=plt.plot(numAgents,np.array(meanTimeECBS4t)/1000.,marker='s',color='red')
numAgents=np.arange(100,1200,100)
l3,=plt.plot(numAgents,np.array(meanTimeECBS8t)/1000.,marker='D',color='green')
numAgents=np.arange(100,1300,100)
l4,=plt.plot(numAgents,np.array(meanTimeECBS16t)/1000.,marker=5,color='purple')
#l5,=plt.plot([100,200,300],meanTime8t,marker='.',color='purple')
#l1,=plt.plot(numAgents,np.array(meantime9SS)/1000.,marker='s',color='red')


plt.xlabel(r'Number of Agents in 128 $\times$ 128 grid')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['ecbs','ecbs-2t','ecbs-4t','ecbs-8t','ecbs-16t'])
plt.show()



plt.figure()
numAgents=np.arange(100,500,100)
l0,=plt.plot(numAgents,optECBS,marker='o',color='blue')
numAgents=np.arange(100,700,100)
l1,=plt.plot(numAgents,optECBS2t,marker='X',color='orange')
numAgents=np.arange(100,1000,100)
l2,=plt.plot(numAgents,optECBS4t,marker='s',color='red')
numAgents=np.arange(100,1200,100)
l3,=plt.plot(numAgents,optECBS8t,marker='D',color='green')
numAgents=np.arange(100,1300,100)
l4,=plt.plot(numAgents,optECBS16t,marker=5,color='purple')
plt.xlabel(r'Number of Agents in 128 $\times$ 128 grid')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['ecbs','ecbs-2t','ecbs-4t','ecbs-8t','ecbs-16t'])
plt.show()

##########################################
meanTimeECBS4s8t=[]
optECBS4s8t=[]
meanTime4s8t=[]
opt4s8t=[]
numAgents=np.arange(100,800,100)
for a in numAgents:
	tmp=read_data('./ecbs-4s8t/random-128-128-00-random.txt',a)
	meanTimeECBS4s8t.append(tmp['runtime'])
	optECBS4s8t.append(tmp['OptimalRatio'])	

numAgents=np.arange(100,1600,100)	
for a in numAgents:
	tmp=read_data('./4s8t/random-128-128-00-random.txt',a)
	meanTime4s8t.append(tmp['runtime'])
	opt4s8t.append(tmp['OptimalRatio'])	

plt.figure()

plt.ylim(0, 600)
numAgents=np.arange(100,800,100)
l0,=plt.plot(numAgents,np.array(meanTimeECBS4s8t)/1000.,marker='o',color='blue')
numAgents=np.arange(100,500,100)
l1,=plt.plot(numAgents,np.array(meanTimeECBS)/1000.,marker='x',color='orange')
numAgents=np.arange(100,1200,100)

l2,=plt.plot(numAgents,np.array(meanTimeECBS8t)/1000.,marker='D',color='green')
numAgents=np.arange(100,1600,100)
l3,=plt.plot(numAgents,np.array(meanTime4s8t)/1000.,marker='s',color='red')
plt.xlabel(r'Number of Agents in 128 $\times$ 128 grid')
plt.ylabel('runtime')
plt.legend(handles=[l0,l1,l2,l3],labels=['ecbs-4s8t','ecbs','ecbs-8t','ILP-4s8t'])
plt.show()


plt.figure()

numAgents=np.arange(100,800,100)
l0,=plt.plot(numAgents,optECBS4s8t,marker='o',color='blue')
numAgents=np.arange(100,500,100)
l1,=plt.plot(numAgents,optECBS,marker='x',color='orange')
numAgents=np.arange(100,1200,100)

l2,=plt.plot(numAgents,optECBS8t,marker='D',color='green')
numAgents=np.arange(100,1600,100)
l3,=plt.plot(numAgents,opt4s8t,marker='s',color='red')
plt.xlabel(r'Number of Agents in 128 $\times$ 128 grid')
plt.ylabel('Suboptimality')
plt.legend(handles=[l0,l1,l2,l3],labels=['ecbs-4s8t','ecbs','ecbs-8t','ILP-4s8t'])
plt.show()
