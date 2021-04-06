#按照路径最长原则得到每个GO标签所属于的层级
GraphLevel<-function (graph.var,onto="BP")
{
  graph.new.nodes=graph.var@nodes
  graph.new.edgeL=graph.var@edgeL
  for(i in 1:length(graph.var@edgeL))
  {
    
    if(length(graph.var@edgeL[[i]][[1]])>0)
    {
      graph.new.edgeL[i][[1]]=list(edges=graph.var@edgeL[[i]][[1]],weights=rep(-1,length(graph.var@edgeL[[i]][[1]])))
    }
    
  }
  if(onto=="BP")
  {
    graph.new=graphNEL(graph.new.nodes, graph.new.edgeL, edgemode = "directed")
    graph.new.distance=bellman.ford.sp(graph.new,graph.new.nodes[which(graph.new.nodes=="GO:0008150")])
    
  }
  if(onto=="CC")
  {
    graph.new=graphNEL(graph.new.nodes, graph.new.edgeL, edgemode = "directed")
    graph.new.distance=bellman.ford.sp(graph.new,graph.new.nodes[which(graph.new.nodes=="GO:0005575")])
    
  }
  if(onto=="MF")
  {
    graph.new=graphNEL(graph.new.nodes, graph.new.edgeL, edgemode = "directed")
    graph.new.distance=bellman.ford.sp(graph.new,graph.new.nodes[which(graph.new.nodes=="GO:0003674")])
    
  }
  graph.level=-graph.new.distance$distance
  return(graph.level)
  
}