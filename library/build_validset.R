#用于生产验证集以及测试集
#except.root.labels用于设定每个样本可能具有的类别标签

BuildValidset<-function (valid.data,go.general.table,go.general.list,except.root.labels,
                         write.data.enable=FALSE,write.class.enable=FALSE,write.data.fname,
                         write.class.fname,select.attributes.en=FALSE,select.attributes,replace.na=0)
{
  #valid.cellcycle=ReadData(read.filename)#读入基因特征属性
  #valid.cellcycle.data=valid.cellcycle[[1]]#基因的数据信息
  valid.gene.name=rownames(valid.data)#基因名称列表
  valid.common.genes <- Get.all.common.genes(go.general.table, valid.data)
  valid.go.general=go.general.list[valid.common.genes]
  go.label.list=valid.go.general
  for (i in 1:length(go.label.list))
  {
    go.label.list[[i]]=intersect(go.label.list[[i]],except.root.labels)
    
  }
  valid.select.labels=Get.classes(go.label.list)
  valid.select.table=Build.GO.class.labels(go.label.list)
  valid.select.table=valid.select.table[,except.root.labels]#将生产的标签列表调整顺序
  valid.select.gene.name=rownames(valid.select.table)
  valid.select.data=valid.data[valid.select.gene.name,]
  valid.select.data[is.na(valid.select.data)]<-replace.na#将NA数据转为0
  if(select.attributes.en==TRUE)
  {
    valid.select.data=valid.select.data[,select.attributes]
  }
  
  if(write.data.enable==TRUE)
  {
    write.table(valid.select.data,file=write.data.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
    
  }
  if(write.class.enable==TRUE)
  {
    
    write.table(valid.select.table,file=write.class.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
  }
  total.data=list(valid.select.data,valid.select.table,go.label.list)
#返回的数据包括生产的样本集，标签集合，以及每个样本带有的标签列表
  return(total.data)
}