GetLeafNode<-function (graph.go)
{   
    inter.label=vector()
    outedge=edges(graph.go)
    outedge.total=outedge[[1]]@edgeL
    for (i in 1:length(outedge.total))
    {
      
      if(length(outedge.total[[i]]$edges)==0)
      {
        inter.label=c(names(outedge.total[i]),inter.label)
      }
    }
    go.leaf.node=inter.label
 
  return(go.leaf.node)
}
