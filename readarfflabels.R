#用于将CLUS生成的test.pred.arff文件读入并计算其结果的评价指标
#20170421
#数据集名称由file.prefix指定
data.path="C:\\Users\\1231\\Desktop\\dataprocessing\\data\\223arff\\"
setwd(data.path)
#设置arff文件的存储文件夹名
aa = 1
fold.name=paste("arff",aa,"\\",sep="")

source("read_predict_arff.R")

file.prefix = "new"
#调用ReadPredictArff函数，得到样本的真实标签和预测的概率
labels.result=ReadPredictArff(data.path,fold.name,file.prefix,except.root.labels2,part.num=3,name.row.num=2)
#样本的真实标签
true.labels=labels.result[[1]]
#使用original model 预测的概率
original.scores=labels.result[[2]]
#使用pruned model 预测的概率
pruned.scores=labels.result[[3]]

#用于存放original model预测概率转化后的标签
o.predict.labels=matrix(0,nrow(original.scores),ncol(original.scores))
colnames(o.predict.labels)=colnames(original.scores)
#将original model预测概率转化为标签
for(i in 1:nrow(original.scores))
{
  for(j in 1:ncol(original.scores))
  {
    if(original.scores[i,j]>=0.5)
    {
      o.predict.labels[i,j]=1
    }
  }
}

#violate.detect.result=ViolateDetectlabel(go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.children,o.predict.labels)

#进行结果评价
o.measure.result=MHevaluate(o.predict.labels,true.labels)
o.prauc_result=PRAUCCalculate(original.scores,true.labels)


p.predict.labels=matrix(0,nrow(pruned.scores),ncol(pruned.scores))
colnames(p.predict.labels)=colnames(pruned.scores)

for(i in 1:nrow(pruned.scores))
{
  for(j in 1:ncol(pruned.scores))
  {
    if(pruned.scores[i,j]>=0.5)
    {
      p.predict.labels[i,j]=1
    }
  }
}

p.measure.result=MHevaluate(p.predict.labels,true.labels)
p.prauc_result=PRAUCCalculate(pruned.scores,true.labels)

#####下方用于验证PRAUC正确性，并无实际应用
label.num=ncol(pruned.scores)
each.class.auc=rep(0,label.num)
each.class.freq=rep(0,label.num)
each.class.weight=rep(0,label.num)
for(i in 1:label.num)
{
  single.predict.scores=pruned.scores[,i]
  single.target.label=true.labels[,i]
  single.pr.result=precision.at.all.recall.levels(single.predict.scores, single.target.label)
  each.class.auc[i]=AUPRC(list(single.pr.result), comp.precision=TRUE)
  each.class.freq[i]=sum(single.target.label)
}
average.auprc=sum(each.class.auc)/label.num

average.auprc.w=0
for(i in 1:label.num)
{
  each.class.weight[i]=each.class.freq[i]/sum(each.class.freq)
  average.auprc.w=average.auprc.w+each.class.auc[i]*each.class.weight[i]
}
setwd(paste(data.path,"arff",aa,"\\",sep=""))
filename <- paste("clus",aa,".txt",sep = "")
write.table(p.measure.result,file = filename,row.names = FALSE,col.names = FALSE,sep = ",")
