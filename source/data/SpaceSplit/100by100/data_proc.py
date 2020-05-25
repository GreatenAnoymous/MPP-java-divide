import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd




def read_data(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['runtime4SS','MakeSpan4SS','MakeSpanLB'])
	frame.eval('OptimalRatio4SS=MakeSpan4SS/MakeSpanLB',inplace=True)
	
	frame=frame[(~frame['runtime4SS'].isin([np.nan]))]

	print(frame)
	temp=frame[['runtime4SS','MakeSpan4SS','MakeSpanLB','OptimalRatio4SS']].mean()
	print("\n")
	print(temp)
	return temp
	
def get_sr(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['runtimeSS','MakeSpanSS','runtimeTS','MakeSpanTS','runtimeOri','MakeSpanOri','MakeSpanLB'])
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
	
dirname='./0'
numAgents=[100,200,300,400,500,600,700,800]
meantime4SS=[]
Opt4SS=[]


for f in os.listdir(dirname):
	df=read_data(dirname+'/'+f)
	meantime4SS.append(df['runtime4SS'])
	Opt4SS.append(df['OptimalRatio4SS'])
	
	

########################################################################	
plt.figure()
l0,=plt.plot(numAgents,np.array(meantime4SS)/1000.,marker='o',color='blue')

plt.xlabel(r'Number of Agents in $100\times 100$ grids')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0],labels=['4-space-split'])
plt.show()

plt.figure()
l0,=plt.plot(numAgents,np.array(Opt4SS),marker='o',color='blue')


plt.xlabel(r'Number of Agents in $100\times 100$ grids')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0],labels=['4-space-split'])
plt.show()


