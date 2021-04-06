#读取datasetFinalValid文件
valid.data2 <- data2[2791:3720,]
#建立样本与特征值的valid.select.data2，第二个list文件
valid.select.data2 <- valid.data2[,c(2:51)]
rownames(valid.select.data2) <- valid.data2[,1]
#建立样本与二值标签的valid.select.table2
valid.select.table2 <- except.root.table2[(nrow(train.data2)+1):(nrow(train.data2)+nrow(valid.data2)),]
#建立第三个list文件，样本与标签名的validgdata
validgdata <- vector("list",length = length(valid.data2[,1]))
names(validgdata) <- valid.data2[,1]
for (i in 1:nrow(valid.select.table2)) {
  for (j in 1:ncol(valid.select.table2)) {
    if(valid.select.table2[i,j] == 1){
      validgdata[[i]] <- c(validgdata[[i]],except.root.labels2[j])
    }
  }
}
#建立第一个样本和特征值的文件factorslist
factorslistva <- as.matrix(valid.select.data2)
#将三个文件整合成一个list，valid.data2.total  
valid.data2.total <- list(factorslistva,valid.select.table2,validgdata)
#建立.csv文件
write.table(valid.select.data2,"newvaliddataset.csv",sep = ",",row.names = FALSE,col.names = FALSE)
write.table(valid.select.table2,"newvalidclass.csv",sep = ",",row.names = FALSE,col.names = FALSE)
