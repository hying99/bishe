#用于实现自底向上的结果判定 20170823
NaiveDownTop<-function (go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.parents,input.prob)
{
  output.prob=input.prob
  for(k in 1:nrow(input.prob))
  {
    for (i in length(go.for.level):2)
    {
      for(j in 1:length(go.for.level[[i]]))
      {
        gene.name=go.for.level[[i]][[j]]
        
        gene.index=nodes.to.index[[gene.name]]
        pos.index=2*gene.index-1
        neg.index=2*gene.index
        
        inter.neg.prob=output.prob[k,neg.index]
        inter.pos.prob=output.prob[k,pos.index]
        parents.index=nodes.to.parents[[gene.name]]
        for(m in 1:length(parents.index))
        {
            #若此时该节点为正的概率比父节点为正的概率大
          if(inter.pos.prob>=output.prob[k,2*(parents.index[m])-1])
          {
            output.prob[k,2*(parents.index[[m]])-1]=inter.pos.prob
            output.prob[k,2*(parents.index[[m]])]=1-inter.pos.prob
          }
            
        }
        
      }
    }
  }
  return(output.prob)
}
