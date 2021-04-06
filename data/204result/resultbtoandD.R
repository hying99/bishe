#####生成结果对比表格#####
setwd("C:\\Users\\Administrator\\Desktop\\dataprocessing\\data\\204result")
aa <- 1:5
resulthm <- matrix(nrow = 2,ncol = 5)
resulthu <- matrix(nrow = 2,ncol = 5)
colnames(resulthm) <- paste("dataset",aa,sep = "")
colnames(resulthu) <- paste("dataset",aa,sep = "")
row.names(resulthm) <- c("original","HMC-DAG-SVM")
row.names(resulthu) <- c("original","HMC-DAG-SVM")

for (aa in 1:5)
{
  originalfile <- paste("original",aa,".txt",sep = "")
  # TPRfile <- paste("TPR",aa,".txt",sep = "")
  DAGlabelfile <- paste("DAGlabel",aa,".txt",sep = "")
  # clusfile <- paste("clus",aa,".txt",sep = "")
  originalresult <- read.table(originalfile,header = FALSE,sep = ",")
  # TPRresult <- read.table(TPRfile,header = FALSE,sep = ",")
  DAGlabelresult <- read.table(DAGlabelfile,header = FALSE,sep = ",")
  # clusresult <-  read.table(clusfile,header = FALSE,sep = ",")
  resulthm[1,aa] <- originalresult[1,3]
  resulthu[1,aa] <- originalresult[1,6]
  # resulthm[2,aa] <- TPRresult[1,3]
  # resulthu[2,aa] <- TPRresult[1,6]
  # resulthm[3,aa] <- clusresult[1,3]
  # resulthu[3,aa] <- clusresult[1,6]
  resulthm[2,aa] <- DAGlabelresult[1,3]
  resulthu[2,aa] <- DAGlabelresult[1,6]
}
barplot(resulthm,col=c('red','green'),beside=TRUE,xlab = "dataset",ylab = "Macro.hf",ylim = c(0,1.2),axes = TRUE)
legend("topleft",inset = 0.01,cex=0.5,legend = c("original","HMC-DAG-SVM"),fill  = c('red','green') )

barplot(resulthu,col=c('red','green'),beside=TRUE,xlab = "dataset",ylab = "Micro.hf",ylim = c(0,1.2),axes = TRUE)
legend("topleft",inset = 0.01,cex=0.5,legend = c("original","HMC-DAG-SVM"),fill  = c('red','green') )

write.csv(resulthm,"hmresulto.csv",quote = FALSE,eol = "\n",row.names = TRUE)
write.csv(resulthu,"huresulto.csv",quote = FALSE,eol = "\n",row.names = TRUE)
