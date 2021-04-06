####用于在链式神经网络中生成相应的带祖先节点预测标签的数据集
####20171116

GetAncestorDataset<-function (gene.name,input.data,except.root.labels, model.list,go.for.level,each.go.level.num,
                        nodes.to.index,nodes.to.ancestors,nodes.to.parents,batch.size)
{
  #gene.name=go.for.level[[i]][[j]]
  gene.index=nodes.to.index[[gene.name]]
  ancestor.index=nodes.to.ancestors[[gene.name]]
  ancestor.num=length(ancestor.index)
  gene.level.index=each.go.level.num[gene.index]
  ancestor.level.index=each.go.level.num[ancestor.index]
  ancestor.scores=matrix(0,nrow(input.data),ancestor.num)
  ancestor.label=matrix(0,nrow(input.data),ancestor.num)
  for(i in 1:(gene.level.index-1))
  {
    for(j in 1:ancestor.num)
    {
      if(ancestor.level.index[j]==i)
      {
        if(i==1)
        {
          #ancestor.scores[,j]=predict_proba(model.list[[ancestor.index[j]]],input.data,batch_size = batch.size)
          ancestor.label[,j]=predict_classes(model.list[[ancestor.index[j]]],input.data,batch_size = batch.size) 
        }else
        {
          now.gene.name=except.root.labels[ancestor.index[j]]
          now.ancestor.index=nodes.to.ancestors[[now.gene.name]]
          now.ancestor.num=length(now.ancestor.index)
          train.data=input.data
          for(k in 1:now.ancestor.num)
          {
            #train.data=cbind(train.data,ancestor.scores[,which(ancestor.index==now.ancestor.index[k])])
            train.data=cbind(train.data,ancestor.label[,which(ancestor.index==now.ancestor.index[k])])
          }
          #ancestor.scores[,j]=predict_proba(model.list[[ancestor.index[j]]],train.data,batch_size = batch.size) 
          ancestor.label[,j]=predict_classes(model.list[[ancestor.index[j]]],train.data,batch_size = batch.size) 
        }
      }
    }
  }
  
  #parent.index=nodes.to.parents[[gene.name]]
  
  #train.data=cbind(input.data,ancestor.scores)
  train.data=cbind(input.data,ancestor.label)
  
  return (train.data)
}