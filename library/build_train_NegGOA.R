#用于生成各节点的训练集，负样本选择方法采用NegGOA方法 20170220
BuildTrainNegGOA<-function (neggoa.result,except.root.labels, data.matrix,
                              adjust.ratio=0.2,select.attributes.en=FALSE,write.en=FALSE)
{   
  neg.index=neggoa.result[[1]]
  pos.index=neggoa.result[[2]]
  negpos.num=neggoa.result[[3]]
  data.inter=list()
  data.total=list()
  data.label=list()
  label.length=length(except.root.labels)
  
  sample.total.num=nrow(data.matrix)
 
  for(i in 1:label.length)
  {
    id.pos.index=pos.index[[i]]
    positive.gene.names=common.genes[id.pos.index]
    id.neg.index=neg.index[[i]]
    negative.gene.names =common.genes[id.neg.index]
    data.inter[[i]]= data.matrix[c(positive.gene.names,negative.gene.names),]
  }
 
  for (i in 1:label.length)
  {
    neg.num=negpos.num[i,1]
    pos.num=negpos.num[i,2]
    if(pos.num<(neg.num*(1-adjust.ratio)))
    {
      row.num=2*pos.num
      data.inter[[i]]=data.inter[[i]][1:row.num,]
      data.label[[i]]=as.factor(c(rep(1,pos.num),rep(2,pos.num)))
      data.total[[i]]=list(X=data.inter[[i]],labels=data.label[[i]],n.pos=pos.num,n.neg=pos.num)
    }else if(pos.num>(neg.num*(1+adjust.ratio)))
    {
      data.label[[i]]=as.factor(c(rep(1,pos.num),rep(2,neg.num)))
      data.total[[i]]=list(X=data.inter[[i]],labels=data.label[[i]],n.pos=pos.num,n.neg=neg.num)
      data.total[[i]]=SmoteSamples(data.total[[i]])
    } else
    {
      data.label[[i]]=as.factor(c(rep(1,pos.num),rep(2,neg.num)))
      data.total[[i]]=list(X=data.inter[[i]],labels=data.label[[i]],n.pos=pos.num,n.neg=neg.num)
    }
    if(select.attributes.en==TRUE)
    {
      data.total[[i]]=SelectAttributes(data.total[[i]],min.attr.ratio=0.25,min.gainratio=0.95)
    }
  }
  names(data.total)=except.root.labels
  if(write.en==TRUE)
  {
    name.num=c(1:length(except.root.labels))
    filename=paste(name.num,"csv",sep = ".")
    for(i in 1:length(except.root.labels))
    {
      inter.data=data.frame(data.total[[i]]$X,data.total[[i]]$labels)
      write.table(inter.data,file=filename[i],sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE)
      
    }
  }
  
  return(data.total)
}