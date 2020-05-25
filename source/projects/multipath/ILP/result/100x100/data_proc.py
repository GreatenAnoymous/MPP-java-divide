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
	
dirname='./4s8t'
#########################################################
numAgents=np.arange(100,1600,100)
meanTime4s16t=[]
meanTime4s8t=[]
opt4s8t=[]
opt4s16t=[]
for a in numAgents:
	tmp=read_data('./4s8t/random-100-100-00-random.txt',a)
	meanTime4s8t.append(tmp['runtime'])
	opt4s8t.append(tmp['OptimalRatio'])
	
for a in numAgents:
	tmp=read_data('./4s16t/random-100-100-00-random.txt',a)
	meanTime4s16t.append(tmp['runtime'])
	opt4s16t.append(tmp['OptimalRatio'])
	
################################################################
numAgents=np.arange(100,1500,100)
meanTime4s16t05=[]
meanTime4s8t05=[]
meanTime16s8t=[]
meanTime8t=[23,107,348.56]
opt4s8t05=[]
opt4s16t05=[]
opt16s8t=[]

for a in numAgents:
	tmp=read_data('./4s8t/random-100-100-05-random.txt',a)
	meanTime4s8t05.append(tmp['runtime'])
	opt4s8t05.append(tmp['OptimalRatio'])

numAgents=np.arange(100,1600,100)
for a in numAgents:
	tmp=read_data('./4s16t/random-100-100-05-random.txt',a)
	meanTime4s16t05.append(tmp['runtime'])
	opt4s16t05.append(tmp['OptimalRatio'])		

for a in numAgents:
	tmp=read_data('./16s8t/random-100-100-00-random.txt',a)
	meanTime16s8t.append(tmp['runtime'])
	opt16s8t.append(tmp['OptimalRatio'])		

########################################################################	
plt.figure()
plt.ylim(0, 600)
l0,=plt.plot(numAgents,np.array(meanTime4s8t)/1000.,marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(meanTime4s16t)/1000.,marker='s',color='red')
l2,=plt.plot(numAgents[0:len(numAgents)-1],np.array(meanTime4s8t05)/1000.,marker='D',color='green')
l3,=plt.plot(numAgents,np.array(meanTime4s16t05)/1000.,marker='x',color='orange')
l4,=plt.plot(numAgents,np.array(meanTime16s8t)/1000.,marker=5,color='black')
l5,=plt.plot([100,200,300],meanTime8t,marker='.',color='purple')
#l1,=plt.plot(numAgents,np.array(meantime9SS)/1000.,marker='s',color='red')


plt.xlabel(r'Number of Agents in 100 $\times$ 100 grid')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2,l3,l4,l5],labels=['4-space-8-time','4-s-16-t','4-s-8-t-0.05obs','4-s-16-t-0.05obs','16-s-8-t','8-time'])
plt.show()



plt.figure()
l0,=plt.plot(numAgents,opt4s8t,marker='o',color='blue')
l1,=plt.plot(numAgents,opt4s16t,marker='s',color='red')
l2,=plt.plot(numAgents[0:len(numAgents)-1],opt4s8t05,marker='D',color='green')
l3,=plt.plot(numAgents,opt4s16t05,marker='x',color='orange')
l4,=plt.plot(numAgents,opt16s8t,marker=5,color='black')
plt.xlabel(r'Number of Agents in 100 $\times$ 100 grid')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1,l2,l3,l4],labels=['4-space-8-time','4-s-16-t','4-s-8-t-0.05obs','4-s-16-t-0.05obs','16-s-8-t'])
plt.show()

exit()
plt.figure()
l0,=plt.plot(numAgents,sr2t2s,marker='o',color='blue')
l1,=plt.plot(numAgents,sr4t,marker='s',color='red')

plt.xlabel(r'Number of Agents in warehouse')
plt.ylabel('Success rate')
plt.legend(handles=[l0,l1],labels=['2-space-2-time-split','4-time-split'])
plt.show()
