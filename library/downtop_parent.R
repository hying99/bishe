#鐢ㄤ簬瀹炵幇鐖跺瓙鑺傜偣鍏卞悓鍔犳潈鐨勭畻娉?
DownTopParent<-function (go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.children,prob.for.genes,each.go.weight)
{
  downtop.prob=prob.for.genes
  parent.weight=0.5
  for(k in 1:nrow(prob.for.genes))
  {
    for (i in length(go.for.level):1)
    {
      if(i<length(go.for.level))
      {
        for(j in 1:length(go.for.level[[i]]))
        {
          gene.name=(go.for.level[[i]])[[j]]#得到(go.for.level[[i]])[[j]]节点的GO标签
          is.leafnode=gene.name %in% go.leaf.nodes
          if(is.leafnode==FALSE)
          {
            gene.name=go.for.level[[i]][[j]]
            gene.index=nodes.to.index[[(go.for.level[[i]])[j]]]
            pos.index=2*gene.index-1
            neg.index=2*gene.index
            inter.pos.prob=0
            children.index=nodes.to.children[[gene.name]]
            children.pos.num=0
            children.pos.index=list()
            children.weight=list()
            child.total.w=0
            performance.w=0
            for(m in 1:length(children.index))
            {
              
              if(prob.for.genes[k,(2*(children.index[[m]])-1)]>0.5)
              {
                children.pos.num=children.pos.num+1
                children.pos.index[[children.pos.num]]=children.index[[m]]
                child.total.w=child.total.w+each.go.weight[children.index[[m]]]
              }
            }
            
            if(children.pos.num>0)
            {
              total.weight=child.total.w+each.go.weight[gene.index]
              parent.weight=each.go.weight[gene.index]/total.weight
              for(n in 1:children.pos.num)
              {
                child.w=each.go.weight[(children.pos.index[[n]])]/total.weight
                inter.pos.prob=inter.pos.prob+downtop.prob[k,(2*(children.pos.index[[n]])-1)]*child.w
              }
              downtop.prob[k,pos.index]=parent.weight*prob.for.genes[k,pos.index]+inter.pos.prob
              downtop.prob[k,neg.index]=1-downtop.prob[k,pos.index]
            }
            
            if(downtop.prob[k,pos.index]>1)
            {
              downtop.prob[k,pos.index]=1
              downtop.prob[k,neg.index]=1-downtop.prob[k,pos.index]
            }
            
          }
        }
      }
    }
  }
  return(downtop.prob)
}