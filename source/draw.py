#import numpy as np
import matplotlib.pyplot as plt
import numpy as np
import pylab as pl
from matplotlib import collections  as mc
import os

def read_edges(filename):
	edges=np.loadtxt(filename)
	return list(edges)
	
def read_points(filename):
	points=np.loadtxt(filename)
	return list(points)
	
def read_lines(filename,color):
	edges=np.loadtxt(filename)
	lines=[]
	colors=[]
	for i in range(0,len(edges)):
		edge=edges[i]
		line=[(edge[0],edge[1]),(edge[2],edge[3])]
		colors.append(color)
		lines.append(line)
	return lines, colors

def draw_lines(filename,color):
	lines,colors=read_lines(filename,color)
	lc = mc.LineCollection(lines, color=colors, linewidths=2)
	#fig, ax = pl.subplots()
	ax.add_collection(lc)

	
def draw_points(points,markerx):
	for i in range(0,len(points)):
		point=points[i]
		plt.scatter(point[0],point[1],marker=markerx,color='green')
		plt.text(point[0],point[1],str(i),fontsize=10)	
		
def read_points_intermediate(filename):
	points=np.loadtxt(filename)
	print(points)
	tmp=np.zeros([points.shape[0],2])
	print(tmp)
	for i in range(0,points.shape[0]):
		tmp[int(points[i][0])][0]=points[i][1]
		tmp[int(points[i][0])][1]=points[i][2]
	print(tmp)
	return list(tmp)		
	


fig, ax = pl.subplots()
draw_lines('./debug/debug_body0.txt','red')
draw_lines('./debug/debug_body1.txt','blue')
draw_lines('./debug/debug_body2.txt','yellow')
draw_lines('./debug/debug_body3.txt','green')
#

ax.autoscale()

pl.show()



