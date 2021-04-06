#用于检验预测结果是否符合TPR规则，输入为各节点的预测概率值
ViolateDetectprob<-function (go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.children,input.prob)
{
  row.num=nrow(input.prob)#输入的样本数量
  col.num=length(nodes.to.children)#预测类别标签总数
  violate.list=list()#输出的结果
  sample.index=0#用于计算存在冲突结果的样本数量
  sample.name=c()#用于存储存在冲突结果的样本名
  for(k in 1:nrow(input.prob))#遍历每一个样本
  {
    
    inter.list=list()#用于存储单一样本的冲突结果
    temp.index=0#用于计算单一样本中有多少个冲突结果
    inter.list.name=c()#用于存储单一样本中哪些节点存在冲突的子节点
    for (i in 1:(length(go.for.level)-1))#按层自上而下遍历所有节点
    {
      for(j in 1:length(go.for.level[[i]]))#分别计算每层有多少节点
      {
        is.leafnode=go.for.level[[i]][[j]] %in% go.leaf.nodes#判断该节点是否则为叶子节点
        gene.name=go.for.level[[i]][[j]]#得到此时处理节点的GO标签名称
        gene.index=nodes.to.index[[(go.for.level[[i]])[j]]]#得到该节点在节点列表中的序号
        pos.index=2*gene.index-1#计算概率矩阵中该节点为正类时所处位置
        neg.index=2*gene.index#计算概率矩阵中该节点为负类时所处位置
        temp.result=c()#用于计算针对某个节点，哪几个子节点与其结果相冲突
        
        if(is.leafnode!=TRUE)
        {
          inter.neg.prob=input.prob[k,neg.index]#得到对于此节点go.for.level[[i]][[j]]，该样本K为负的概率
          inter.pos.prob=input.prob[k,pos.index]
          children.index=nodes.to.children[[gene.name]]#得到此节点的所有子节点的序号
          for(m in 1:length(children.index))#遍历所有子节点
          {
            if(inter.pos.prob<0.5)#当对于父节点，此样本为负时
            {
              if(input.prob[k,2*(children.index[m])-1]>=0.5)#若此时子节点children.index[m]为正
              {
                #violate_result[k,gene.index]=1
                
                temp.result=c(temp.result,children.index[m])#出现结果冲突，记录该子节点的序号
                
                #violate_result[k,children.index[m]]=1
              }
            } 
          }
          if(!is.null(temp.result))#若确实存在冲突的父子节点
          {
            temp.index=temp.index+1#有冲突的节点数+1
            inter.list[[temp.index]]=temp.result#将与此节点冲突的所有子节点存入
            inter.list.name=c(inter.list.name,gene.index)#将此节点的序号存入
          }
        }
      }
      
    }
    if(length(inter.list)>0)
    {
      names(inter.list)=inter.list.name
      sample.index=sample.index+1
      violate.list[[sample.index]]=inter.list#将此样本的所有节点存入
      sample.name=c(sample.name,k)
    }
   
  }
  
  names(violate.list)=sample.name#按照样本序号给结果命名
  return(violate.list)
}

