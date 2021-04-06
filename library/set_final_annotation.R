#用于得到经过样本数量选择后的各基因所带有的节点标签中的叶子节点
Setfinalannotation<-function (univ.graph,go.label.list)
{
  
  go.each.gene=go.label.list
  
  for (i in 1:length(go.label.list))
  {
    graph.each.gene=subGraph(go.label.list[[i]], univ.graph)
    go.each.gene[[i]]=GetLeafNode1(graph.each.gene)
#     inter.label=vector()
#     outedge=edges(graph.each.gene)
#     outedge.total=outedge[[1]]@edgeL
#     for (j in 1:length(outedge.total))
#     {
#        
#       if(length(outedge.total[[j]][[1]])==0)
#        {
#          inter.label=c(names(outedge.total[j]),inter.label)
#        }
#     }
#     go.each.gene[[i]]=inter.label
  }
  return(go.each.gene)
}
