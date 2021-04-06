####用于实现基于贝叶斯网络修改概率的后处理方法,不独立节点的概率均设置为0.5
####20170801

BNcompute2<-function (first.prob,except.root.labels,go.for.level,go.leaf.nodes,test.select.table)
{
  ####第一步 前期数据准备，生成节点的索引
  # total.index=MakeIndex(except.root.labels)
  # nodes.to.index=total.index[[1]]
  # nodes.to.children=total.index[[2]]
  # nodes.to.parents=total.index[[4]]
  
  total.levels=length(go.for.level)
  sample.nums=nrow(first.prob)
  node.nums=ncol(test.select.table)
  #初始概率产生的标签
  first.predict.labels=matrix(0,sample.nums,node.nums)
  #经过概率修改后产生的标签
  second.predict.labels=matrix(0,sample.nums,node.nums)
  #经过自上而下过程后产生的标签
  final.predict.labels=matrix(0,sample.nums,node.nums)
  #存储各节点预测为1的概率值
  final.predict.scores=matrix(0,sample.nums,node.nums)
  
  ####第二步 将概率值转换为标签 1为正 0为负
  for(i in 1:sample.nums)
  {
    for(j in 1:node.nums)
    {
      #将初步预测的概率值转化为0 1标签
      if(first.prob[i,(2*j-1)]>=0.5)
      {
        first.predict.labels[i,j]=1
      }
    }
  }
  #计算初始标签值的评价指标
  #first.measure.result=MHevaluate(first.predict.labels,test.select.table)
  #violate.detect.first=ViolateDetectlabel(go.for.level.3, go.leaf.nodes.3,nodes.to.index,nodes.to.children,first.predict.labels)
  
  ####第三步 根据一个节点的父子节点修改该节点的概率值
  second.prob=first.prob
  second.predict.labels=first.predict.labels
  for(k in 1:sample.nums)#对于每一个样本k
  {
    for (i in 1:total.levels)#按层自上而下遍历节点
    {
      for(j in 1:length(go.for.level[[i]]))
      {
        gene.name=(go.for.level[[i]])[[j]]#得到(go.for.level[[i]])[[j]]节点的GO标签
        gene.index=nodes.to.index2[[gene.name]]#得到该节点的索引号
        
        children.index=nodes.to.children2[[gene.index]]#得到此节点的所有子节点的序号
        children.labels=first.predict.labels[k,children.index]#得到其子节点的预测值，此时子节点可能有多个
        #初始化 子节点决定该节点为0的预测值
        children.prob.zero=1
        #初始化 父节点决定该节点为1的预测值
        parent.prob.one=1
        
        parent.index=nodes.to.parents2[[gene.index]]#得到其父节点的索引号
        parent.labels=first.predict.labels[k,parent.index]#得到其父节点的预测值，此时父节点可能有多个
        is.leafnode=gene.name %in% go.leaf.nodes#判断该节点是否则为叶子节点
        
        if(i==1)#对于第一层节点，此层节点假定均属于根结点
        {
          if(first.predict.labels[k,gene.index]==0)#如果该节点的预测值为负值
          {
            if(is.leafnode!=TRUE)#如果该节点不是叶子节点
            {
              if(1 %in% children.labels)#如果该节点的子节点有为1的情况
              {
                #计算由子节点决定的该节点为0和为1个概率
                for(m in 1:length(children.index))
                {
                  grandchild.index=nodes.to.children2[[children.index[m]]]
                  common.child=intersect(grandchild.index,children.index)
                  if(length(common.child)>0)
                  {
                    children.prob.zero=children.prob.zero*0.5
                  } else
                  {
                    children.prob.zero=children.prob.zero*first.prob[k,(2*children.index[m])]
                  }
                }
                children.prob.zero=children.prob.zero*0.5
                children.prob.one=1-children.prob.zero
                #计算由父节点决定的该节点为0和为1个概率
                parent.prob.one=0.5
                parent.prob.zero=1-parent.prob.one
                ##子节点决定该节点为1的概率，父节点决定该节点为0的概率
                ##如果子节点决定其为1的概率大于父节点决定其为0的概率
                ##则修改该节点的判断结果为1，并修改其概率
                if(children.prob.one>=parent.prob.zero)
                {
                  second.predict.labels[k,gene.index]=1
                  second.prob[k,(2*gene.index-1)]=children.prob.one
                  second.prob[k,(2*gene.index)]=children.prob.zero
                }
                #可以看出，当节点属于第一层且不为叶子节点时，
                #对预测值0是否更改仅取决于其子节点是否有为1的情况
              }
            }
          }
        } else#对于属于其它层的节点
        {
          if(first.predict.labels[k,gene.index]==0)#如果该节点的预测值为负值
          {
            if(is.leafnode!=TRUE)#如果这个节点不是叶子节点
            {
              if(1 %in% children.labels)#并且这个节点存在预测值为1的子节点
              {
                #计算由子节点决定的该节点为0和为1个概率
                for(m in 1:length(children.index))
                {
                  grandchild.index=nodes.to.children2[[children.index[m]]]
                  common.child=intersect(grandchild.index,children.index)
                  if(length(common.child)>0)
                  {
                    children.prob.zero=children.prob.zero*0.5
                  } else
                  {
                    children.prob.zero=children.prob.zero*first.prob[k,(2*children.index[m])]
                  }
                }
                children.prob.zero=children.prob.zero*0.5
                children.prob.one=1-children.prob.zero
                #计算由父节点决定的该节点为0和为1个概率
                for(n in 1:length(parent.index))
                {
                  grandparent.index=nodes.to.parents2[[parent.index[n]]]
                  common.parent=intersect(grandparent.index,parent.index)
                  if(length(common.parent)>0)
                  {
                    parent.prob.zero=parent.prob.zero*0.5
                  } else
                  {
                    parent.prob.one=parent.prob.one*first.prob[k,(2*parent.index[n]-1)]
                  }
                }
                parent.prob.one=parent.prob.one*0.5
                parent.prob.zero=1-parent.prob.one
                ##子节点决定该节点为1的概率，父节点决定该节点为0的概率
                ##如果子节点决定其为1的概率大于父节点决定其为0的概率
                ##则修改该节点的判断结果为1，并修改其概率
                if(children.prob.one>=parent.prob.zero)
                {
                  second.predict.labels[k,gene.index]=1
                  second.prob[k,(2*gene.index-1)]=children.prob.one
                  second.prob[k,(2*gene.index)]=children.prob.zero
                }
              } 
            }
          } else #如果该节点的预测值为正值
          {
            if(is.leafnode!=TRUE)#如果这个节点不是叶子节点
            {
              if(0 %in% parent.labels)#并且这个节点存在预测值为0的父节点
              {
                #计算由子节点决定的该节点为0和为1个概率
                for(m in 1:length(children.index))
                {
                  grandchild.index=nodes.to.children2[[children.index[m]]]
                  common.child=intersect(grandchild.index,children.index)
                  if(length(common.child)>0)
                  {
                    children.prob.zero=children.prob.zero*0.5
                  } else
                  {
                    children.prob.zero=children.prob.zero*first.prob[k,(2*children.index[m])]
                  }
                }
                children.prob.zero=children.prob.zero*0.5
                children.prob.one=1-children.prob.zero
                #计算由父节点决定的该节点为0和为1个概率
                for(n in 1:length(parent.index))
                {
                  grandparent.index=nodes.to.parents2[[parent.index[n]]]
                  common.parent=intersect(grandparent.index,parent.index)
                  if(length(common.parent)>0)
                  {
                    parent.prob.zero=parent.prob.zero*0.5
                  } else
                  {
                    parent.prob.one=parent.prob.one*first.prob[k,(2*parent.index[n]-1)]
                  }
                }
                parent.prob.one=parent.prob.one*0.5
                parent.prob.zero=1-parent.prob.one
                ##子节点决定该节点为1的概率，父节点决定该节点为0的概率
                ##如果子节点决定其为1的概率小于父节点决定其为0的概率
                ##则修改该节点的判断结果为0，并修改其概率
                if(parent.prob.zero>children.prob.one)
                {
                  second.predict.labels[k,gene.index]=0
                  
                  second.prob[k,(2*gene.index-1)]=parent.prob.one
                  second.prob[k,(2*gene.index)]=parent.prob.zero
                }
              } 
            }else#如果这个节点是叶子节点,叶子节点没有子节点
            {
              if(0 %in% parent.labels)#并且这个节点存在预测值为0的父节点
              {
                children.prob.zero=0.5
                children.prob.one=1-children.prob.zero
                #计算由父节点决定的该节点为0和为1个概率
                for(n in 1:length(parent.index))
                {
                  grandparent.index=nodes.to.parents2[[parent.index[n]]]
                  common.parent=intersect(grandparent.index,parent.index)
                  if(length(common.parent)>0)
                  {
                    parent.prob.zero=parent.prob.zero*0.5
                  } else
                  {
                    parent.prob.one=parent.prob.one*first.prob[k,(2*parent.index[n]-1)]
                  }
                }
                parent.prob.one=parent.prob.one*0.5
                parent.prob.zero=1-parent.prob.one
                ##子节点决定该节点为1的概率，父节点决定该节点为0的概率
                ##如果子节点决定其为1的概率小于父节点决定其为0的概率
                ##则修改该节点的判断结果为0，并修改其概率
                if(parent.prob.zero>children.prob.one)
                {
                  second.predict.labels[k,gene.index]=0
                  second.prob[k,(2*gene.index-1)]=parent.prob.one
                  second.prob[k,(2*gene.index)]=parent.prob.zero
                }
              } 
            }
          }
        }
      }
      
    }
  }
  
  #计算二次标签的评价指标
  #second.measure.result=MHevaluate(second.predict.labels,test.select.table)
  
  ####第四步 自顶而下遍历样本，使得底层节点为正的概率小于等于上层节点为正的概率############
  
  final.prob=TopDownStep(go.for.level,go.leaf.nodes,nodes.to.index2,nodes.to.children2,second.prob)
  
  
  #将计算的最终概率转化为标签值
  for(i in 1:sample.nums)
  {
    for(j in 1:node.nums)
    {
      final.predict.scores[i,j]=final.prob[i,(2*j-1)]
      if(final.prob[i,(2*j-1)]>=0.5)
      {
        final.predict.labels[i,j]=1
      }
    }
  }
  
  ####第五步 返回预测结果以及各节点预测为1的概率值（score）
  
  final.result=list(final.predict.labels,final.predict.scores)
  return (final.result)
  
}

