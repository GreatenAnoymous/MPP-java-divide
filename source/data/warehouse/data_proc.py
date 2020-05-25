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
	
dirname='./data2'
numAgents=[10,20,30,40,50,60,70,80,90,100,110,120]
meanTime4t=[]
meanTime2t2s=[]
opt2t2s=[]
opt4t=[]
sr4t=[]
sr2t2s=[]

for a in numAgents:
	tmp=read_data('./2t2s.txt',a)
	meanTime2t2s.append(tmp['runtime'])
	opt2t2s.append(tmp['OptimalRatio'])
	tmp2=get_sr("./2t2s.txt",a)
	sr2t2s.append(tmp2)
	
for a in numAgents:
	tmp=read_data('./4t.txt',a)
	meanTime4t.append(tmp['runtime'])
	opt4t.append(tmp['OptimalRatio'])
	tmp2=get_sr("./4t.txt",a)
	sr4t.append(tmp2)
		
'''
for f in os.listdir(dirname):
	df=read_data(dirname+'/'+f)
	meantime4SS.append(df['runtime4SS'])
	Opt4SS.append(df['OptimalRatio4SS'])
	meantime9SS.append(df['runtime9SS'])
	Opt9SS.append(df['OptimalRatio9SS'])
'''	

########################################################################	
plt.figure()
l0,=plt.plot(numAgents,np.array(meanTime2t2s)/1000.,marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(meanTime4t)/1000.,marker='s',color='red')
#l1,=plt.plot(numAgents,np.array(meantime9SS)/1000.,marker='s',color='red')

plt.xlabel(r'Number of Agents in warehouse')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1],labels=['2-space-2-time-split','4-time-split'])
plt.show()

plt.figure()
l0,=plt.plot(numAgents,sr2t2s,marker='o',color='blue')
l1,=plt.plot(numAgents,sr4t,marker='s',color='red')

plt.xlabel(r'Number of Agents in warehouse')
plt.ylabel('Success rate')
plt.legend(handles=[l0,l1],labels=['2-space-2-time-split','4-time-split'])
plt.show()


plt.figure()
l0,=plt.plot(numAgents,opt2t2s,marker='o',color='blue')
l1,=plt.plot(numAgents,opt4t,marker='s',color='red')

plt.xlabel(r'Number of Agents in warehouse')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1],labels=['2-space-2-time-split','4-time-split'])
plt.show()


