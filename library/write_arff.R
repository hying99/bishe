#生产符合CLUS软件要求的arff文件
#本函数将原始文件的描述文件与新生成的数据相结合，生成新的arff文件
#将数据保存为arff格式，可以选择是否包含基因名称
WriteArff<-function (original.filename,write.filename,modified.data,common.genes=common.genes,
                     ontology="BP",class.label,class.graph,genename.exist=FALSE)
{
  #original.filename="cellcycle_GO.train.arff"
  #将原始的arff文件读入
  original.data <- scan(original.filename, what = list(""),sep = "\n")
  each.row.data=list()
  #将每一行的数据读入
  for(i in 1:length(original.data[[1]]))
  {
    each.row.data[i]=strsplit(original.data[[1]][i],split = " ")
  }
  #得到原始文件字头所占有的行数
  head.row.num=which(each.row.data=="@DATA")
  
  gene.names=names(class.label)
  #class.graph=graph.select.node.3
  
  total.class=names(class.graph@edgeData@data)
  if(ontology=="BP")
  {
    #加入根结点与第一层节点的关系，此处的根结点是虚拟的
    total.class=c("root/GO0008150",total.class)
  }
  
  total.class=paste(total.class,collapse = ",")
  total.class=gsub(":","",total.class)
  total.class=gsub("\\|","/",total.class)
  class.position=length(each.row.data[[head.row.num-1]])
  each.row.data[[head.row.num-1]][class.position]=total.class
  
  #write.filename= "writehead"
  for(i in 1:head.row.num)
  {
    if(i==1)
    {
      write.table(t(as.matrix(each.row.data[[i]])), file = write.filename, row.names = FALSE,col.names = FALSE, quote = FALSE,sep=" ")
    } else
    {
      write.table(t(as.matrix(each.row.data[[i]])), file = write.filename,append = TRUE, row.names = FALSE,col.names = FALSE, quote = FALSE,sep=" ")
    }
  }
  #modified.data=training.cellcycle.data
  #class.label=go.leaf.label.list
  if (!is.null(common.genes)) 
  {
    modified.data=modified.data[common.genes,]
  } 
  for(i in 1:length(class.label))
  {
    #生成每条样本最底层的叶子标签集合
    class.label[[i]]=paste(class.label[[i]],collapse = "@")
    class.label[[i]]=gsub(":","",class.label[[i]])
  }
  #将数据中的NA替换为？
  modified.data[is.na(modified.data)]="?"
  if(genename.exist==FALSE)
  {
    matrix.total.data=cbind(modified.data,class.label)
  }
  if(genename.exist==TRUE)
  {
    #若genename.exist为TRUE，则将每个样本的名称附在每行的最后面
    matrix.total.data=cbind(modified.data,class.label,gene.names)
  }
  write.table(matrix.total.data, file = write.filename,append = TRUE,row.names = FALSE,col.names = FALSE, quote = FALSE,sep=",")
  return (matrix.total.data)
  
}