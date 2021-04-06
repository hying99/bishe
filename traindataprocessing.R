#读取datasetFinalTrain文件
data2<- read.csv("C:/Users/1231/Desktop/dataprocessing/datasetFinal.csv",header=TRUE,dec='.')
train.data2 <- data2[1:2790,]
#将特征值与标签值分离
#建立样本与特征值的train.select.data2，第二个list文件
train.select.data2 <- train.data2[,c(2:51)]
rownames(train.select.data2) <- train.data2[,1]
#建立样本与二值标签的train.select.table2
train.select.table2 <- except.root.table2[1:nrow(train.data2),]
#建立第三个list文件，样本与标签名的traingdata
traingdata <- vector("list",length = length(train.data2[,1]))
names(traingdata) <- train.data2[,1]
for (i in 1:nrow(train.select.table2)) {
  for (j in 1:ncol(train.select.table2)) {
    if(train.select.table2[i,j] == 1){
      traingdata[[i]] <- c(traingdata[[i]],except.root.labels2[j])
    }
  }
}

#建立第一个样本和特征值的文件factorslist
factorslisttr <- as.matrix(train.select.data2)
#将三个文件整合成一个list，train.data2.total  
train.data2.total <- list(factorslisttr,train.select.table2,traingdata)

#输出.csv文件
write.table(train.select.data2,"newtraindataset.csv",sep=",",row.names = FALSE,col.names = FALSE)
write.table(train.select.table2,"newtrainclass.csv",sep = ",",row.names = FALSE,col.names = FALSE)
#建立train.data.total2的list
#把行名设成第一列ID
#rownames(train.data2) <- train.data2[,1]
#train.data2 <- train.data2[,-1]