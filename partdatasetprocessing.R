###设置路径
setwd("C:/Users/1231/Desktop/dataprocessing")
source("dataset_select2.R")
source("dataset_divide.R")
prefix <- paste(length(except.root.labels2),"dataset",sep = "")
###选择训练集 1、2、3、4、5
datasetindex <- 1
datasetresult = DatasetSelect(dataset.index = datasetindex)
select.table <- datasetresult[[1]]
select.data <- datasetresult[[2]]

###拆分训练集train:test=2:1
selecttabletodivide <- DatasetDivide(dataset = select.table)
train.select.table2 <- selecttabletodivide[[1]]
valid.select.table2 <- selecttabletodivide[[2]]
test.select.table2 <- selecttabletodivide[[3]]

selectdatatodivide <- DatasetDivide(dataset=select.data)
train.select.data2 <- selectdatatodivide[[1]]
valid.select.data2 <- selectdatatodivide[[2]]
test.select.data2 <- selectdatatodivide[[3]]


#######下面的都运行过了为了产生数据不需要每次都运行一遍#######
#####设定存储路径，204个节点的就是204dataset#####
datapath <- paste(prefix,datasetindex,sep = "")
setwd(paste("C:/Users/1231/Desktop/dataprocessing/data/",datapath,sep = ""))
###生成csv文件
write.table(train.select.data2,"traindataset.csv",sep=",",row.names = FALSE,col.names = FALSE)
write.table(train.select.table2,"trainclass.csv",sep = ",",row.names = FALSE,col.names = FALSE)
write.table(valid.select.data2,"validdataset.csv",sep = ",",row.names = FALSE,col.names = FALSE)
write.table(valid.select.table2,"validclass.csv",sep = ",",row.names = FALSE,col.names = FALSE)
write.table(test.select.data2,"testdataset.csv",sep = ",",row.names = FALSE,col.names = FALSE)
write.table(test.select.table2,"testclass.csv",sep = ",",row.names = FALSE,col.names = FALSE)
###针对每个节点拆分训练集
trainnum <- ncol(train.select.table2)
for (k in 1:trainnum) {
  traindataset <- train.select.data2
  cat(k,"\n")
  traindataset[,ncol(traindataset) + 1] <- train.select.table2[,k]
  traindataset <- traindataset[order(-traindataset$V51),]
  write.table(traindataset,paste(k,".csv",sep = ""),sep = ",",row.names = FALSE,col.names = FALSE)
}

