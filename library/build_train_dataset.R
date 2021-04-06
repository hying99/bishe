#用于生成各节点的训练集，带有属性选择功能以及样本采样功能
BuildTrainDataset<-function (Table.gene.class, except.root.labels, data.matrix,
                           ontology = "BP", adjust.ratio=0.2,ratio.negative = 0, common.genes = NULL,
                           seed = 1,select.attributes.en=FALSE,write.en=FALSE)
{   
  data.total=list()
  label.length=length(except.root.labels)
  if(is.null(common.genes))
  {
    sample.total.num=nrow(data.matrix)
  }  else
  {
    sample.total.num=length(common.genes)
  }
  for(i in 1:label.length)
  {
    data.total[[i]]=Get.matrix.data.change(Table.gene.class,classid = except.root.labels[i],
                                                     data.matrix,ontology = ontology,common.genes = common.genes)
  }
  names(data.total)=except.root.labels
  neg.num=list()
  pos.num=list()
  # nn.nodes=vector()
  for (i in 1:label.length)
  {
    neg.num[[i]]=(data.total[[i]]$n.neg)
    pos.num[[i]]=(data.total[[i]]$n.pos)
    
    if(pos.num[[i]]<(neg.num[[i]]*(1-adjust.ratio)))
    {
      data.total[[i]]=SmoteSamples(data.total[[i]])
    }
    if(pos.num[[i]]>(neg.num[[i]]*(1+adjust.ratio)))
    {
      if((neg.num[[i]]+pos.num[[i]])<(sample.total.num*(1-adjust.ratio)))
      {
        classid=names(data.total[i])
        data.total[[i]]=AddNegSamples(data.total[[i]], sample.total.num,
                                                classid,Table.gene.class, data.matrix,
                                                ontology = ontology, adjust.ratio=0.2,ratio.negative = 0, 
                                                common.genes = common.genes,seed = 1)
      }      else
      {
        data.total[[i]]=SmoteSamples(data.total[[i]])
      }
    }
    
    
    if(select.attributes.en==TRUE)
    {
      data.total[[i]]=SelectAttributes(data.total[[i]],min.attr.ratio=0.25,min.gainratio=0.95)
    }
    
  }
#   names(neg.num)=except.root.labels
#   names(pos.num)=except.root.labels
  
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