####采用neural netwrok作为基础分类器，运行前需先运行rebuilddataprocess或其他文件，得到数据及类别信息
####20171109
cat('The neural network starts' ,"\n")
label.length=length(except.root.labels)
#label.length=15
model.list=list()
nn.predict.labels=matrix(0,nrow(test.select.table),ncol(test.select.table))
nn.predict.scores=matrix(0,nrow(test.select.table),ncol(test.select.table))
rownames(nn.predict.labels)=rownames(test.select.table)
colnames(nn.predict.labels)=colnames(test.select.table)
rownames(nn.predict.scores)=rownames(test.select.table)
colnames(nn.predict.scores)=colnames(test.select.table)
nn.results.evaluation=matrix(0,ncol(test.select.table),6)
rownames(nn.results.evaluation)=colnames(test.select.table)
colnames(nn.results.evaluation)=c('Prec','Rec','Spe','F','Acc','Npos')
batch.size=32
epochs=100
patience=5
for (i in 1:label.length)
#for (i in 15:15)
{
  gene.index=i#得到处理节点的序号
  cat('The processing class is ' ,gene.index,"\n")
  input.data =data.total[[gene.index]]$X#训练数据
  input.label=t(t(as.numeric(data.total[[gene.index]]$labels))) #训练数据的标签
  input.label[input.label==2]=0
  valid.input.data=valid.select.data
  valid.input.label=t(t(valid.select.table[,gene.index]))
  col.num=ncol(input.data)#训练数据属性的个数，即维度
  first.hneuron.num=round((col.num+1)/2)#隐藏层节点的数量
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
    input.data, input.label,
    epochs = epochs, batch_size = batch.size,
    #validation_split = 0.2
    validation_data = list(valid.input.data,valid.input.label),
    callbacks=list(callback_early_stopping(monitor="val_loss",min_delta=0,patience=patience,verbose=1,mode=c("auto")))
    )
  
  nn.predict.labels[,gene.index]=predict_classes(model,test.select.data,batch_size = batch.size)
  nn.predict.scores[,gene.index]=predict_proba(model,test.select.data,batch_size = batch.size)
  model.list[[i]]=model
  test.true.label=t(t(test.select.table[,i]))
  nn.results.evaluation[i,]=F.measure.single(nn.predict.labels[,i],test.true.label)
}
names(model.list)=except.root.labels

prob.for.genes=matrix(0,nrow(test.select.table),(ncol(test.select.table)*2))
for(i in 1:nrow(test.select.table))
{
  for(j in 1:label.length)
  {
    prob.for.genes[i,(2*j-1)]=nn.predict.scores[i,j]
    prob.for.genes[i,(2*j)]=1-nn.predict.scores[i,j]
  }
}

measure.result.nn=MHevaluate(nn.predict.labels,test.select.table)
prauc.result.nn=PRAUCCalculate(nn.predict.scores,test.select.table)
bn.result=BNcompute(prob.for.genes,except.root.labels,go.for.level,go.leaf.nodes,test.select.table)
bn.first.labels=bn.result[[1]]
bn.first.scores=bn.result[[2]]
bn.predict.labels=bn.result[[3]]
bn.predict.scores=bn.result[[4]]
prauc.first.bn=PRAUCCalculate(bn.first.scores,test.select.table)
measure.first.bn=MHevaluate(bn.first.labels,test.select.table)
prauc.result.bn=PRAUCCalculate(bn.predict.scores,test.select.table)
measure.result.bn=MHevaluate(bn.predict.labels,test.select.table)


result.output.en=TRUE
result.savepath=paste(data.path,"//result",sep = "")
setwd(result.savepath)
today <-Sys.Date()

output.fname=paste(file.prefix,"0nn_result",".txt",sep = "")
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

