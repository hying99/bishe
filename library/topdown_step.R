TopDownStep<-function (go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.children,downtop.prob)
{
  topdown.prob=downtop.prob
  for(k in 1:nrow(downtop.prob))
  {
    for (i in 1:(length(go.for.level)-1))
    {
        for(j in 1:length(go.for.level[[i]]))
        {
          gene.name=go.for.level[[i]][[j]]
          is.leafnode=gene.name %in% go.leaf.nodes
          gene.index=nodes.to.index[[gene.name]]
          pos.index=2*gene.index-1
          neg.index=2*gene.index
          if(is.leafnode!=TRUE)
          {
            inter.neg.prob=topdown.prob[k,neg.index]
            inter.pos.prob=topdown.prob[k,pos.index]
            children.index=nodes.to.children[[gene.name]]
            for(m in 1:length(children.index))
            {
              #if(inter.pos.prob<0.5)#当对于父节点，此样本为负时
              
                #若此时子节点children.index[m]为正的概率比父节点为正的概率大
                if(topdown.prob[k,2*(children.index[m])-1]>inter.pos.prob)
                {
                  topdown.prob[k,2*(children.index[[m]])-1]=inter.pos.prob
                  topdown.prob[k,2*(children.index[[m]])]=1-inter.pos.prob
                }
              
            }
          }
      }
  }
}
  return(topdown.prob)
}
