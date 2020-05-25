import numpy as np
import matplotlib.pyplot as plt
import statistics
import os
import pandas as pd




def read_data(filename):
	rawData=np.loadtxt(filename)
	frame=pd.DataFrame(rawData,columns=['runtimeSS','MakeSpanSS','runtimeTS','MakeSpanTS','runtimeOri','MakeSpanOri','MakeSpanLB'])
	frame.eval('OptimalRatioSS=MakeSpanSS/MakeSpanLB',inplace=True)
	frame.eval('OptimalRatioTS=MakeSpanTS/MakeSpanLB',inplace=True)
	frame=frame[(~frame['runtimeSS'].isin([np.nan]))]
	frame=frame[(~frame['runtimeTS'].isin([np.nan]))]
	print(frame)
	temp=frame[['runtimeSS','MakeSpanSS','runtimeTS','MakeSpanTS','runtimeOri','MakeSpanOri','MakeSpanLB','OptimalRatioSS','OptimalRatioTS']].mean()
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
numAgents=[50,60,70,80,90,100,110,120,130,140,150,160,170]
meantimeSS=[]
OptSS=[]
meantimeTS=[]
OptTS=[]

for f in os.listdir(dirname):
	df=read_data(dirname+'/'+f)
	meantimeSS.append(df['runtimeSS'])
	OptSS.append(df['OptimalRatioSS'])
	meantimeTS.append(df['runtimeTS'])
	OptTS.append(df['OptimalRatioTS'])
	

########################################################################	
plt.figure()
l0,=plt.plot(numAgents,np.array(meantimeSS)/1000.,marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(meantimeTS)/1000.,marker='D',color='red')
l2,=plt.plot([50,60,70],[54.242,136.6,185.7],marker='s',color='purple')
plt.xlabel(r'Number of Agents in $30\times 60$ grids')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2],labels=['2-space-split','2-time-split','Non-split'])
plt.show()

plt.figure()
l0,=plt.plot(numAgents,np.array(OptSS),marker='o',color='blue')
l1,=plt.plot(numAgents,np.array(OptTS),marker='D',color='red')

plt.xlabel(r'Number of Agents in $30\times 60$ grids')
plt.ylabel('Optimality Ratio')
plt.legend(handles=[l0,l1],labels=['2-space-split','2-time-split'])
plt.show()
########################################################################

dirname='./0.1'
numAgents=[50,60,70,80,90,100,110,120,130,140,150,160,170]
meantimeSS=[]
OptSS=[]
meantimeTS=[]
OptTS=[]
Sr_ss=[]
Sr_ts=[]


for f in os.listdir(dirname):
	df=read_data(dirname+'/'+f)
	meantimeSS.append(df['runtimeSS'])
	OptSS.append(df['OptimalRatioSS'])
	meantimeTS.append(df['runtimeTS'])
	OptTS.append(df['OptimalRatioTS'])
	sr_ss,sr_ts=get_sr(dirname+'/'+f)
	Sr_ss.append(sr_ss)
	Sr_ts.append(sr_ts)

plt.figure()
l0,=plt.plot(numAgents[0:8],np.array(meantimeSS[0:8])/1000.,marker='o',color='blue')
l1,=plt.plot(numAgents[0:8],np.array(meantimeTS[0:8])/1000.,marker='D',color='red')
l2,=plt.plot([50,60,70],[54.242,136.6,185.7],marker='s',color='purple')
plt.xlabel(r'Number of Agents in $30\times 60$ grids')
plt.ylabel('Average computation time in seconds')
plt.legend(handles=[l0,l1,l2],labels=['2-space-split','2-time-split','Non-split'])
plt.show()

plt.figure()
l0,=plt.plot(numAgents[0:8],np.array(OptSS[0:8]),marker='o',color='blue')
l1,=plt.plot(numAgents[0:8],np.array(OptTS[0:8]),marker='D',color='red')
plt.xlabel(r'Number of Agents in $30\times 60$ grids')
plt.ylabel('Opt Ratio')
plt.legend(handles=[l0,l1],labels=['2-space-split','2-time-split'])
plt.show()

plt.figure()
l0,=plt.plot(numAgents,Sr_ss,marker='o',color='blue')
l1,=plt.plot(numAgents,Sr_ts,marker='D',color='red')
plt.xlabel(r'Number of Agents in $30\times 60$ grids')
plt.ylabel('Success rate')
plt.legend(handles=[l0,l1],labels=['2-space-split','2-time-split'])
plt.show()


