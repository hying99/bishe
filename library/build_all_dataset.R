#拟用于生成训练及数据文件，但涉及变量太多，恐带来不便，故暂未使用，亦未验证
BuildAllDataset<-function (Table.gene.class, except.root.labels, data.matrix,
                             ontology = "BP", adjust.ratio=0.2,ratio.negative = 0, common.genes = NULL,
                             seed = 1,select.attributes.en=FALSE,write.en=FALSE)
{   
  train.data.total=BuildTrainDataset(root.table.3, except.root.labels.3, data.matrix=training.cellcycle.data,
                               ontology = "BP", adjust.ratio=0.2,ratio.negative = 0, common.genes = common.genes,
                               seed = 1,select.attributes.en=TRUE,write.en=FALSE)
  
  if(select.attributes.en==TRUE)
  {
    label.length=length(except.root.labels.3)
    valid.data.total=list()
    for(i in 1:label.length)
    {
      select.attributes=data.total[[i]]$select.attributes
      name.num=paste("validdataset",i,sep = "_")
      data.filename=paste(name.num,"csv",sep = ".")
      if(i==1)
      {
        valid.data.total[[i]]=BuildValidset(original.valid.file,go.general.table.BP,go.general.list.BP,except.root.labels.3,
                                            write.data.enable=TRUE,write.class.enable=TRUE,write.data.fname=data.filename,
                                            write.class.fname="validclass.csv",select.attributes.en=TRUE,select.attributes)
        
        valid.select.table=valid.data.total[[1]][[2]]
      }
      else
      {
        valid.data.total[[i]]=BuildValidset(original.valid.file,go.general.table.BP,go.general.list.BP,except.root.labels.3,
                                            write.data.enable=TRUE,write.class.enable=FALSE,write.data.fname=data.filename,
                                            select.attributes.en=TRUE,select.attributes)
      }
      
    } 
  }
  if(select.attributes.en==FALSE)
  {
    write.data.fname="validdataset.csv"
    valid.data.total=BuildValidset(original.valid.file,go.general.table.BP,go.general.list.BP,except.root.labels.3,
                                   write.data.enable=FALSE,write.class.enable=FALSE,write.data.fname=data.filename,
                                   write.class.fname="validclass.csv",select.attributes.en=select.attributes.en,select.attributes)
    valid.select.data=valid.data.total[[1]]
    valid.select.table=valid.data.total[[2]]
  }
  data.total=list(train.data.total,valid.data.total)
  return(data.total)
}