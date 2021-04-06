#用于在生成训练集时，对少类样本进行smote过采样
AddNegSamples=function (original.data, sample.total.num,classid,Table.gene.class, data.matrix,
                        ontology = "BP", adjust.ratio=0.2,ratio.negative = 0, common.genes = NULL,
                        seed = 1)
{
  set.seed(seed)
  original.samples=original.data$X
  original.sample.names=rownames(original.samples)
  col.num=ncol(original.samples)
  original.labels=original.data$labels
  original.n.pos=original.data$n.pos
  original.n.neg=original.data$n.neg
  original.sample.num=original.n.pos+original.n.neg
  original.pos.index=which(original.labels==1)
  original.neg.index=setdiff(c(1:original.sample.num),original.pos.index)
  original.pos.samples=original.samples[original.pos.index,]
  original.neg.samples=original.samples[original.neg.index,]
  original.neg.names=rownames(original.neg.samples)
  
  renew.data=Get.matrix.data.for.classid(Table.gene.class,classid = classid,
                                           data.matrix,ontology = "BP",common.genes = common.genes)
  renew.samples=renew.data$X
  renew.sample.names=rownames(renew.samples)
  renew.labels=renew.data$labels
  renew.n.pos=renew.data$n.pos
  renew.n.neg=renew.data$n.neg
  renew.sample.num=renew.n.pos+renew.n.neg
  renew.pos.index=which(renew.labels==1)
  renew.neg.index=which(renew.labels==2)
  renew.pos.samples=renew.samples[renew.pos.index,]
  renew.neg.samples=renew.samples[renew.neg.index,]
  renew.neg.names=rownames(renew.neg.samples)
  other.neg.names=setdiff(renew.neg.names,original.neg.names)
  other.neg.samples=renew.neg.samples[other.neg.names,]
  
  if(renew.n.neg>(renew.n.pos*(1-adjust.ratio)))
  {
    if(renew.n.neg<(renew.n.pos*(1+adjust.ratio)))
    {
      result=renew.data
    }    else
    {
      new.neg.num=original.n.pos-original.n.neg
      ind <- trunc(runif(new.neg.num, min=1, max=renew.n.neg-original.n.neg+1))
      ind=unique(ind)
      select.neg.samples=other.neg.samples[ind,]
      final.samples=rbind(original.samples,select.neg.samples)
      n.pos=original.n.pos
      n.neg=length(ind)+original.n.neg
      labels=factor(c(rep(1,n.pos),rep(2,n.neg)))
      result=list(X=final.samples,labels=labels,n.pos=n.pos,n.neg=n.neg)
    }
  }
  if(renew.n.neg<(renew.n.pos*(1-adjust.ratio)))
  {
   result=SmoteSamples(renew.data)
  }
  return(result)
}