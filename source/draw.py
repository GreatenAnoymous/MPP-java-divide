import numpy as np
import matplotlib.pyplot as plt
import os

def read_edges(filename):
	edges=np.loadtxt(filename)
	return list(edges)
	
def read_points(filename):
	points=np.loadtxt(filename)
	return list(points)

def draw_edges(edges,colors):
	for edge in edges:
		draw_edge(edge,colors)
		
def draw_edge(edge,colors):
	plt.plot([edge[0],edge[2]],[edge[1],edge[3]],color=colors)
	
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
	
	'''
plt.figure()
start=read_points('./start.txt')
intermeidate=read_points_intermediate('./intermediate2.txt')
goal=read_points('./goal.txt')
print(start)
edges1=read_edges('./graph11.txt')
plt.subplot(311)
draw_edges(edges1,'blue')
edges2=read_edges('./graph21.txt')
draw_edges(edges2,'red')
draw_points(start,'o')

plt.subplot(312)
draw_edges(edges1,'blue')
draw_edges(edges2,'red')
draw_points(intermeidate,'o')

plt.subplot(313)
edges1=read_edges('./graph12.txt')
edges2=read_edges('./graph22.txt')
draw_edges(edges1,'blue')
draw_edges(edges2,'red')
draw_points(goal,'o')
plt.show()
'''

'''
plt.figure()
start=read_points('./start.txt')
intermeidate=read_points_intermediate('./intermediate2.txt')
edges1=read_edges('./graph12.txt')
edges2=read_edges('./graph22.txt')
goal=read_points('./goal.txt')
draw_edges(edges1,'blue')
draw_edges(edges2,'red')
draw_points(intermeidate,'o')
plt.xlabel(r'Intermediate goal state and buffer zone in $30\times 60$ grid graph with 100 agents')
plt.show()
'''


'''
plt.figure()
edges=read_edges('./graph11.txt')
draw_edges(edges,'blue')
edges=read_edges('./graph21.txt')
draw_edges(edges,'red')
start=read_points('./intermediate2.txt')
print(start)
draw_points(start,'o')
plt.show()
plt.figure()
edges=read_edges('./graph11.txt')
draw_edges(edges,'blue')
edges=read_edges('./graph21.txt')
draw_edges(edges,'red')
start=read_points('./goal.txt')
print(start)
draw_points(start,'o')
plt.show()
 '''

plt.figure()

edges=read_edges('./subg1.txt')
draw_edges(edges,'blue')
plt.scatter(48,19)

#plt.xlabel(r'Intermediate goal state and buffer zone in $30\times 60$ grid graph with 100 agents')
plt.show()


exit()

plt.figure()
for f in os.listdir('./debug'):
	edges=read_edges('./debug/'+f)
	draw_edges(edges,'blue')
'''
edges=read_edges('./debug/debug_body0.txt')
draw_edges(edges,'blue')
edges=read_edges('./debug/debug_bz_0_0.txt')
draw_edges(edges,'blue')
edges=read_edges('./debug/debug_bz_0_1.txt')
draw_edges(edges,'blue')
edges=read_edges('./debug/debug_bz_0_2.txt')
draw_edges(edges,'blue')
edges=read_edges('./debug/debug_bz_0_3.txt')
draw_edges(edges,'blue')


edges=read_edges('./debug/debug_body1.txt')
draw_edges(edges,'red')
edges=read_edges('./debug/debug_bz_1_0.txt')
draw_edges(edges,'red')
edges=read_edges('./debug/debug_bz_1_1.txt')
draw_edges(edges,'red')
edges=read_edges('./debug/debug_bz_1_2.txt')
draw_edges(edges,'red')
edges=read_edges('./debug/debug_bz_1_3.txt')
draw_edges(edges,'red')

edges=read_edges('./debug/debug_body2.txt')
draw_edges(edges,'green')
edges=read_edges('./debug/debug_bz_2_0.txt')
draw_edges(edges,'green')
edges=read_edges('./debug/debug_bz_2_1.txt')
draw_edges(edges,'green')
edges=read_edges('./debug/debug_bz_2_2.txt')
draw_edges(edges,'green')
edges=read_edges('./debug/debug_bz_2_3.txt')
draw_edges(edges,'green')

edges=read_edges('./debug/debug_body3.txt')
draw_edges(edges,'orange')
edges=read_edges('./debug/debug_bz_3_0.txt')
draw_edges(edges,'orange')
edges=read_edges('./debug/debug_bz_3_1.txt')
draw_edges(edges,'orange')
edges=read_edges('./debug/debug_bz_3_2.txt')
draw_edges(edges,'orange')
edges=read_edges('./debug/debug_bz_3_3.txt')
draw_edges(edges,'orange')
'''
#plt.xlabel(r'Intermediate goal state and buffer zone in $30\times 60$ grid graph with 100 agents')
plt.show()



'''
plt.figure(figsize=(10,20))

subgraph1=read_edges('./subgraph1.txt')
subgraph2=read_edges('./subgraph2.txt')
b1=read_edges('./b1.txt')
b2=read_edges('./b2.txt')

subg1=read_edges('./subg1.txt')
subg2=read_edges('./subg2.txt')

draw_edges(subg1,'blue')
draw_edges(subg2,'red')
#draw_edges(b1,'green')
#draw_edges(b2,'purple')
######################
plt.scatter(1,1,marker='o',color='green',s=100)
plt.text(1,1,"S1",fontsize=15)
plt.scatter(35,17,marker='o',color='green',s=100)
plt.text(35,17,"G1",fontsize=15)
########################
plt.scatter(5,5,marker='D',color='red',s=100)
plt.text(5,5,"S2",fontsize=15)
plt.scatter(10,10,marker='D',color='red',s=100)
plt.text(10,10,"G2",fontsize=15)
#############################
plt.scatter(20,7,marker='s',color='orange',s=100)
plt.text(20,7,"S3",fontsize=15)
plt.scatter(20,12,marker='s',color='orange',s=100)
plt.text(20,12,"G3",fontsize=15)



#plt.xlabel(r'Intermediate goal state and buffer zone in $30\times 60$ grid graph with 100 agents')
plt.show()

'''
