#得到一个图中的所有叶子节点，该函数用于替换getleafnode函数
GetLeafNode1<-function (graph.go)
{   
  inter.label=vector()
  outedge=edges(graph.go)
  
  for (i in 1:length(outedge))
  {
    
    if(length(outedge[[i]])==0)
    {
      inter.label=c(names(outedge[i]),inter.label)
    }
  }
  go.leaf.node=inter.label
  
  return(go.leaf.node)
}