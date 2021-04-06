SmoteSamples=function (original.data,adjust.ratio=0.2)
{
  original.samples=original.data$X
  col.num=ncol(original.samples)
  original.labels=original.data$labels
  original.n.pos=original.data$n.pos
  original.n.neg=original.data$n.neg
  if(original.n.neg>original.n.pos)
  {#此时正类样本为少类样本
    np.ratio=original.n.neg/original.n.pos
    if(np.ratio<2)
    {
      smote.num=(floor(np.ratio))*100 
    }    else
    {
      smote.num=(round(np.ratio)-1)*100 
    }
    
  }  else
  {#此时负类样本为少类样本
    pn.ratio=original.n.pos/original.n.neg
    if(pn.ratio<2)
    {
      smote.num=(floor(pn.ratio))*100 
    }   else
    {
      smote.num=(round(pn.ratio)-1)*100 
    }
    
  }
  
  data.with.labels=data.frame(label=original.labels,data=original.samples)
  Data.smote <- SMOTE(label~., data.with.labels, perc.over = smote.num,perc.under=100)
  
  if(original.n.neg>original.n.pos)
  {
    #选择SMOTE生成的正样本以及原始负样本组成新的数据集
    pos.index=which(Data.smote$label==1)
    smote.pos.example=Data.smote[pos.index,]
    neg.data=original.samples[(original.n.pos+1):(original.n.pos+original.n.neg),]
    neg.example=data.frame(label=original.labels[(original.n.pos+1):(original.n.pos+original.n.neg)],data=neg.data)
    total.example=rbind(smote.pos.example,neg.example)
    n.pos=length(pos.index)
    n.neg=original.n.neg
    if(n.pos>((1+adjust.ratio)*n.neg))
    {
      n.pos=n.neg
      smote.pos.example=smote.pos.example[1:n.neg,]
      total.example=rbind(smote.pos.example,neg.example)
    }
  }  else
  {
    #选择SMOTE生成的负样本以及原始正样本组成新的数据集
    neg.index=which(Data.smote$label==2)
    smote.neg.example=Data.smote[neg.index,]
    pos.data=original.samples[1:original.n.pos,]
    pos.example=data.frame(label=original.labels[1:original.n.pos],data=pos.data)
    total.example=rbind(pos.example,smote.neg.example)
    n.pos=original.n.pos
    n.neg=length(neg.index)
    if(n.neg>n.pos)
    {
      n.neg=n.pos
      smote.neg.example=smote.neg.example[1:n.neg,]
      total.example=rbind(pos.example,smote.neg.example)
    }
  }
  
  result=list(X=as.matrix(total.example[,2:(col.num+1)]),labels=total.example$label,n.pos=n.pos,n.neg=n.neg)
  
  return(result)
}


