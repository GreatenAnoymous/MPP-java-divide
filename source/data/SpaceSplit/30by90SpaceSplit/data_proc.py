import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd




def read_data(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['runtimeSS','MakeSpanSS','runtimeTS','MakeSpanTS','runtime3S','MakeSpan3S','MakeSpanLB'])
	frame.eval('OptimalRatioSS=MakeSpanSS/MakeSpanLB',inplace=True)
	frame.eval('OptimalRatioTS=MakeSpanTS/MakeSpanLB',inplace=True)
	frame.eval('OptimalRatio3S=MakeSpan3S/MakeSpanLB',inplace=True)
	frame=frame[(~frame['runtimeSS'].isin([np.nan]))]
	frame=frame[(~frame['runtimeTS'].isin([np.nan]))]
	print(frame)
	temp=frame[['runtimeSS','MakeSpanSS','runtimeTS','MakeSpanTS','runtime3S','MakeSpan3S','MakeSpanLB','OptimalRatioSS','OptimalRatioTS','OptimalRatio3S']].mean()
	print("\n")
	print(temp)
	return temp
	
def get_sr(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['runtimeSS','MakeSpanSS','runtimeTS','MakeSpanTS','runtime3S','MakeSpan3S','MakeSpanLB'])
	sr_SS=1-frame['runtimeSS'].isna().sum()/30
	sr_TS=1-frame['runtimeTS'].isna().sum()/30
	return sr_SS,sr_TS
		
	
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
	
	
def read_data3(dirname,numAgents,choice):
	arr=[]
	for f in os.listdir(dirname):
		df=read_data(dirname+'/'+f)
		arr.append(df[choice])
	return arr

	
		

	
	
dirname='./gauss0.25'
numAgents=[50,60,70,80,90,100,110,120]
meantimeSS=[]
OptSS=[]
meantimeTS=[]
OptTS=[]
meantime3S=[]
Opt3S=[]
'''
for f in os.listdir(dirname):
	df=read_data(dirname+'/'+f)
	meantimeSS.append(df['runtimeSS'])
	OptSS.append(df['OptimalRatioSS'])
	meantimeTS.append(df['runtimeTS'])
	OptTS.append(df['OptimalRatioTS'])
	meantime3S.append(df['runtime3S'])
	Opt3S.append(df['OptimalRatio3S'])
	'''
plt.figure()
opt025=read_data3('./gauss0.25',numAgents,'runtime3S')
opt050=read_data3('./gauss',numAgents,'runtime3S')
opt100=read_data3('./gauss1.0',numAgents,'runtime3S')
optU=read_data3('./0',numAgents,'runtime3S')

l0,=plt.plot(numAgents,opt025,marker='o',color='blue')
l1,=plt.plot(numAgents,opt050,marker='s',color='red')
l2,=plt.plot(numAgents,opt100,marker='D',color='purple')
l3,=plt.plot(numAgents,optU,marker='x',color='green')
plt.xlabel(r'Number of Agents in $30\times 90$ grids')
plt.ylabel('Average computation time in miliseconds')
plt.legend(handles=[l0,l1,l2,l3],labels=['0.25 rows-Gaussian','0.5 rows-Gaussian','1.0 rows-Gaussian','uniform'])
plt.show()
	
'''
########################################################################	
plt.figure()
l0,=plt.plot(numAgents,np.array(meantimeSS)/1000.,marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(meantimeTS)/1000.,marker='D',color='red')
l2,=plt.plot(numAgents,np.array(meantime3S)/1000.,marker='s',color='purple')
plt.xlabel(r'Number of Agents in $30\times 90$ grids')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2],labels=['2-space-split','2-time-split','3-space-split'])
plt.show()

plt.figure()
l0,=plt.plot(numAgents,np.array(OptSS),marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(OptTS),marker='D',color='red')
l2,=plt.plot(numAgents,np.array(Opt3S),marker='s',color='purple')

plt.xlabel(r'Number of Agents in $30\times 90$ grids')
plt.ylabel('Average optimality')
plt.legend(handles=[l0,l1,l2],labels=['2-space-split','2-time-split','3-space-split'])
plt.show()
'''
