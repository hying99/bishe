####用于在链式神经网络中生成相应的带标签的数据集
####20171115

NnGetDataset<-function (gene.name,input.data,except.root.labels, model.list,go.for.level,each.go.level.num,
                        nodes.to.index,nodes.to.ancestors,nodes.to.parents,batch.size)
{
  #gene.name=go.for.level[[i]][[j]]
  gene.index=nodes.to.index[[gene.name]]
  ancestor.index=nodes.to.ancestors[[gene.name]]
  ancestor.num=length(ancestor.index)
  gene.level.index=each.go.level.num[gene.index]
  ancestor.level.index=each.go.level.num[ancestor.index]
  ancestor.scores=matrix(0,nrow(input.data),ancestor.num)
  
  for(i in 1:(gene.level.index-1))
  {
    for(j in 1:ancestor.num)
    {
      if(ancestor.level.index[j]==i)
      {
        if(i==1)
        {
          ancestor.scores[,j]=predict_proba(model.list[[ancestor.index[j]]],input.data,batch_size = batch.size)
        }else
        {
          now.gene.name=except.root.labels[ancestor.index[j]]
          parent.index=nodes.to.parents[[now.gene.name]]
          parent.num=length(parent.index)
          train.data=input.data
          for(k in 1:parent.num)
          {
            train.data=cbind(train.data,ancestor.scores[,which(ancestor.index==parent.index[k])])
          }
          ancestor.scores[,j]=predict_proba(model.list[[ancestor.index[j]]],train.data,batch_size = batch.size)  
        }
      }
    }
  }
    
  parent.index=nodes.to.parents[[gene.name]]
  parent.num=length(parent.index)
  train.data=input.data
  for(k in 1:parent.num)
  {
        train.data=cbind(train.data,ancestor.scores[,which(ancestor.index==parent.index[k])])
  }
  return (train.data)
}