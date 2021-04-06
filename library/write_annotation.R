#将数据保存为arff格式，可以选择是否包含基因名称
WriteAnnotation<-function (read.filename,class.label,Nametoupper=TRUE,write.filename,genename.exist=FALSE)
{
  library(marray)
  #class.label=match.annotation.final[[1]]
  gene.names=names(class.label)
  
  for(i in 1:length(class.label))
  {
    class.label[[i]]=paste(class.label[[i]],collapse = "@")
    class.label[[i]]=gsub(":","",class.label[[i]])
  }
  matrix.type=ReadData(read.filename,Nametoupper=Nametoupper,Tonum=FALSE)
  matrix.type.data=matrix.type[[1]]#基因的数据信息
  matrix.type.name=matrix.type[[2]]#基因名称列表
  match.index=match(gene.names,matrix.type.name)
  express.data=matrix.type.data[match.index,]
   
  #matrix.total.data=cbind(express.data,class.label)
  if(genename.exist==FALSE)
  {
    matrix.total.data=cbind(express.data,class.label)
  }
  else
  {
    matrix.total.data=cbind(express.data,class.label,gene.names)
  }
  write.table(matrix.total.data, file = write.filename, row.names = FALSE,col.names = FALSE, quote = FALSE,sep=",")
  return (matrix.total.data)
  
}