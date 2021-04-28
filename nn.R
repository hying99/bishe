####采用neural netwrok作为基础分类器，运行前需先运行rebuilddataprocess或其他文件，得到数据及类别信息
####20171109
library(keras)
cat('The neural network starts' ,"\n")
label.length=length(except.root.labels2)
#label.length=15
#####建立长度为204的model.list#####
model.list=vector("list",length = length(except.root.labels2))
nn.predict.labels=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
nn.predict.scores=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
rownames(nn.predict.labels)=rownames(test.select.table2)
colnames(nn.predict.labels)=colnames(test.select.table2)
rownames(nn.predict.scores)=rownames(test.select.table2)
colnames(nn.predict.scores)=colnames(test.select.table2)
#####建立评价指标List nn.results.evaluation#####
nn.results.evaluation=matrix(0,ncol(test.select.table2),6)
rownames(nn.results.evaluation)=colnames(test.select.table2)
colnames(nn.results.evaluation)=c('Prec','Rec','Spe','F','Acc','Npos')
#####设置超参数#####
batch.size=32
epochs=100
patience=5#####对应于early stopping的一个参数，若5步范围内精度未得到提升，就停止训练。防止过拟合#####
for (i in 1:label.length)
####在这里设置断点####
  # for (i in 67:67)
{
  gene.index=i#得到处理节点的序号
  cat('The processing class is ' ,gene.index,"\n")
  input.data2 =as.matrix(train.select.data2)#训练数据
  input.label2=t(t(as.numeric(train.select.table2[,gene.index]))) #训练数据的标签
  # input.label2[input.label2==2]=0
  valid.input.data2=as.matrix(valid.select.data2)
  valid.input.label2=t(t(as.numeric(valid.select.table2[,gene.index])))
  col.num=ncol(input.data2)#训练数据属性的个数，即维度,50
  first.hneuron.num=round((col.num)/2)#隐藏层节点的数量,25
  second.hneuron.num=first.hneuron.num
  #second.hneuron.num=round((first.hneuron.num+1)/2)#隐藏层节点的数量
  model <- keras_model_sequential()
  
  # add layers and compile the model
  model %>%
    layer_dense(units = first.hneuron.num, activation = 'relu', input_shape = c(col.num)) %>%
    layer_dropout(rate = 0.5) %>%
    layer_dense(units = second.hneuron.num, activation = 'relu') %>%
    layer_dropout(rate = 0.5) %>%
    layer_dense(units = 1, activation = 'sigmoid')
  model %>% compile(
    #loss = loss_mean_squared_error,
    loss = loss_binary_crossentropy,
    optimizer = optimizer_adagrad(),
    #optimizer = optimizer_rmsprop(),
    metrics = c(metric_binary_accuracy)
  )
  
  
  history <- model %>% fit(
    input.data2, input.label2,
    epochs = epochs, batch_size = batch.size,
    #validation_split = 0.2
    validation_data = list(valid.input.data2,valid.input.label2),
    callbacks=list(callback_early_stopping(monitor="val_loss",min_delta=0,patience=patience,verbose=1,mode=c("auto")))
  )
  ####接下来的这步有问题，应该是在predict_classes这里####
  nn.predict.labels[,gene.index]=predict_classes(model,as.matrix(test.select.data2),batch_size = batch.size)
  nn.predict.scores[,gene.index]=predict_proba(model,as.matrix(test.select.data2),batch_size = batch.size)
  model.list[[i]]=model
  test.true.label=t(t(test.select.table2[,i]))
  nn.results.evaluation[i,]=F.measure.single(nn.predict.labels[,i],test.true.label)
}
names(model.list)=except.root.labels2

prob.for.genes2=matrix(0,nrow(test.select.table2),(ncol(test.select.table2)*2))
for(i in 1:nrow(test.select.table2))
{
  for(j in 1:label.length)
  {
    prob.for.genes2[i,(2*j-1)]=nn.predict.scores[i,j]
    prob.for.genes2[i,(2*j)]=1-nn.predict.scores[i,j]
  }
}


####下面是结果评价部分#####
measure.result.nn=MHevaluate(nn.predict.labels,test.select.table2)
prauc.result.nn=PRAUCCalculate(nn.predict.scores,test.select.table2)
bn.result=BNcompute(prob.for.genes2,except.root.labels22,go.for.level2,go.leaf.nodes2,test.select.table2)
bn.first.labels=bn.result[[1]]
bn.first.scores=bn.result[[2]]
bn.predict.labels=bn.result[[3]]
bn.predict.scores=bn.result[[4]]
prauc.first.bn=PRAUCCalculate(bn.first.scores,test.select.table2)
measure.first.bn=MHevaluate(bn.first.labels,test.select.table2)
prauc.result.bn=PRAUCCalculate(bn.predict.scores,test.select.table2)
measure.result.bn=MHevaluate(bn.predict.labels,test.select.table2)


result.output.en=TRUE
data.path <- "C:/Users/1231/Desktop/dataprocessing/data"
result.savepath=paste(data.path,"//204result",sep = "")
setwd(result.savepath)
today <-Sys.Date()

output.fname=paste("nn_result",datasetindex,".txt",sep = "")
if(result.output.en==TRUE)
{

  write.table(today,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
  write("\n The results given by NN\n",file = output.fname,append = TRUE)
  write.table(measure.result.nn,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
  write.table(prauc.result.nn,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)

  write("\n The results given by BN in the first step\n",file = output.fname,append = TRUE)
  write.table(measure.first.bn,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
  write.table(prauc.first.bn,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)

  write("\n The final results given by BN \n",file = output.fname,append = TRUE)
  write.table(measure.result.bn,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
  write.table(prauc.result.bn,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
}
