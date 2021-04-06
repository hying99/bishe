#自底向上处理函数
#上层节点所使用的结果应为底层节点完成判断之后的值
DownTopStep<-function (go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.children,prob.for.genes)
{
  downtop.prob=prob.for.genes
  for(k in 1:nrow(prob.for.genes))#对于每一个样本k
  {
    for (i in (length(go.for.level)-1):1)#按层自下而上遍历节点,并且所选层次不为最底层时
    {      
      {
        for(j in 1:length(go.for.level[[i]]))
        {
          gene.name=(go.for.level[[i]])[[j]]#得到(go.for.level[[i]])[[j]]节点的GO标签
          is.leafnode=gene.name %in% go.leaf.nodes#判断此时处理的节点是否为叶子节点
          if(is.leafnode==FALSE)          #若不为叶子节点
          {
            gene.index=nodes.to.index[[(go.for.level[[i]])[j]]]
            pos.index=2*gene.index-1
            neg.index=2*gene.index
            inter.pos.prob=0#子节点对最终结果的贡献值
            children.index=nodes.to.children[[gene.name]]#存储该节点所有子节点的序号
            children.pos.num=0#子节点判断为正类的个数
            for(m in 1:length(children.index))#对于m个子节点其中之一进行操作
            {
              if(downtop.prob[k,(2*(children.index[[m]])-1)]>0.5)#若样本k在此子节点的判断结果为正
              {
                children.pos.num=children.pos.num+1#子节点结果为正的数量加1
                #计算正类子节点的总体贡献值
                inter.pos.prob=inter.pos.prob+downtop.prob[k,(2*(children.index[[m]])-1)]
              }
            }
            if(children.pos.num>0)#若确实存在子节点为正类的情况
            {
              downtop.prob[k,pos.index]=0.5*prob.for.genes[k,pos.index]+0.5*inter.pos.prob/(children.pos.num)
              if(downtop.prob[k,pos.index]>1)#若计算的加权后概率值大于1，则改为1
              {
                downtop.prob[k,pos.index]=1
              }
              downtop.prob[k,neg.index]=1-downtop.prob[k,pos.index]
            }
          }
        }
      }
    }
  }
  return(downtop.prob)
}