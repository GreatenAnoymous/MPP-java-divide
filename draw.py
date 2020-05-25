import numpy as np
import matplotlib.pyplot as plt


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
start=read_points('./start.txt')
intermeidate=read_points_intermediate('./intermediate2.txt')
edges1=read_edges('./subgraph1.txt')
edges2=read_edges('./subgraph2.txt')
edges3=read_edges('./subgraph3.txt')
b1=read_edges('./b1.txt')
b2=read_edges('./b2.txt')
b3=read_edges('./b3.txt')
b4   =read_edges('./b4.txt')
goal=read_points('./goal.txt')
draw_edges(edges1,'blue')
draw_edges(b1,'blue')
draw_edges(edges2,'red')
draw_edges(b2,'red')
draw_edges(b3,'red')
draw_edges(edges3,'green')
draw_edges(b4,'green')
#draw_points(intermeidate,'o')
plt.xlabel(r'Intermediate goal state and buffer zone in $30\times 60$ grid graph with 100 agents')
plt.show()

