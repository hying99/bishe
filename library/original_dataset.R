#用来针对各节点生成训练集，不含属性选择功能，也不含样本采样处理功能
OriginalDataset<-function (Table.gene.class, except.root.labels, data.matrix,
                           ontology = "BP", ratio.negative = 0, common.genes = NULL,
                           seed = 1,write.en=FALSE)
{   
  data.cellcycle.total=list()
  label.length=length(except.root.labels)
  for(i in 1:length(except.root.labels))
  {
    data.cellcycle.total[[i]]=Get.matrix.data.for.classid(Table.gene.class,classid = except.root.labels[i],
                                                          data.matrix,ontology = "BP",common.genes = common.genes)
  }
  names(data.cellcycle.total)=except.root.labels
  
  if(write.en==TRUE)
  {
    name.num=c(1:length(except.root.labels))
    filename=paste(name.num,"csv",sep = ".")
    for(i in 1:length(except.root.labels))
    {
      inter.data=data.frame(data.cellcycle.total[[i]]$X,data.cellcycle.total[[i]]$labels)
      write.table(inter.data,file=filename[i],sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE)
      
    }
  }
  
  return(data.cellcycle.total)
}