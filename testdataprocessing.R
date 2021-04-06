#读取datasetFinalTest文件
test.data2 <- data2[3721:nrow(data2),]
#将特征值与标签值分离
#建立样本与特征值的test.select.data2，第二个list文件
test.select.data2 <- test.data2[,c(2:51)]
rownames(test.select.data2) <- test.data2[,1]
#建立样本与二值标签的test.select.table2
test.select.table2 <- except.root.table2[(nrow(train.data2)+nrow(valid.data2)+1):(nrow(train.data2)+nrow(valid.data2)+nrow(test.data2)),]
#建立第三个list文件，样本与标签名的testgdata
testgdata <- vector("list",length = length(test.data2[,1]))
names(testgdata) <- test.data2[,1]
for (i in 1:nrow(test.select.table2)) {
  for (j in 1:ncol(test.select.table2)) {
    if(test.select.table2[i,j] == 1){
      testgdata[[i]] <- c(testgdata[[i]],except.root.labels2[j])
    }
  }
}
names(testgdata) <- test.data2[,1]
#建立第一个样本和特征值的文件factorslist
factorslistte <- as.matrix(test.select.data2)
#将三个文件整合成一个list，test.data2.total  
test.data2.total <- list(factorslistte,test.select.table2,testgdata)
#输出.csv
write.table(test.select.data2,"newtestdataset.csv",sep = ",",row.names = FALSE,col.names = FALSE)
write.table(test.select.table2,"newtestclass.csv",sep = ",",row.names = FALSE,col.names = FALSE)
