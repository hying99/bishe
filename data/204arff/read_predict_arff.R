#用于将CLUS生成的test.pred.arff文件读入并将预测标签以矩阵形式存储
#20170421
#part.num设置预测结果包含几部分，若为3，则有真实标签，original model得到的标签以及pruned model得到的标签
#输入待处理的数据集 名称 file.prefix
#设置arff文件的存储文件夹名 fold.name
ReadPredictArff<-function (data.path,fold.name,file.prefix,except.root.labels,part.num=3,name.row.num=2)
{
  #设置数据路径
  setwd(data.path)
  #得到待读入的文件名
  arff.result.filename=paste(fold.name,file.prefix,".test.pred.arff",sep = "")
  #将arff文件读入
  original.data <- scan(arff.result.filename, what = list(""),sep = "\n")
  #将数据转存为list格式，以便将数据按行分开
  each.row.data=list()
  for(i in 1:length(original.data[[1]]))
  {
    each.row.data[i]=strsplit(original.data[[1]][i],split = " ")
  }
  #查找从哪行开始 以后均为预测结果数据
  head.row.num=which(each.row.data=="@DATA")
  
  #查找从哪行开始，以后为GO标签的顺序
  #name.row.num=2
  #求得测试样本的数量
  test.sample.num=length(original.data[[1]])-head.row.num
  #获取读入的预测结果文件的列数
  inter.data=strsplit(each.row.data[[head.row.num+1]],split = ",")
  data.col.num=length(inter.data[[1]])
  #将预测结果数据转化为矩阵形式存储
  all.labels=matrix(0,nrow = test.sample.num,ncol = data.col.num)
  for(i in 1:test.sample.num)
  {
    inter.data=strsplit(each.row.data[[head.row.num+i]],split = ",")
    all.labels[i,]=inter.data[[1]]
  }
  #求取标签集应含有多少标签
  label.num=(data.col.num-part.num)/(part.num)
  label.names=rep(0,time=label.num)
  
  for(i in 1:label.num)
  {
    
    label.names[i]=gsub("class-a-GO","GO",each.row.data[[(name.row.num+i)]][[2]])
  }
  
  
  #各样本的真实标签
  true.labels=all.labels[,2:(label.num+1)]
  true.labels=matrix(as.numeric(true.labels),nrow=nrow(true.labels))
  colnames(true.labels)=label.names
  true.labels=true.labels[,except.root.labels]
  #各样本的original model得到的标签
  original.scores=all.labels[,(label.num+2):(label.num*2+1)]
  original.scores=matrix(as.numeric(original.scores),nrow=nrow(original.scores))
  colnames(original.scores)=label.names
  original.scores=original.scores[,except.root.labels]
  #各样本的pruned model得到的标签
  pruned.scores=all.labels[,(label.num*2+3):(data.col.num-1)]
  pruned.scores=matrix(as.numeric(pruned.scores),nrow=nrow(pruned.scores))
  colnames(pruned.scores)=label.names
  pruned.scores=pruned.scores[,except.root.labels]
  #将标签值输出
  labels.result=list(true.labels=true.labels,original.scores=original.scores,pruned.scores=pruned.scores)
  return(labels.result)
}