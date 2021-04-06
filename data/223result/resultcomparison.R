#####生成结果对比表格#####
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\223result")
aa <- 1:5
resulthm <- matrix(nrow = 3,ncol = 5)
resulthu <- matrix(nrow = 3,ncol = 5)
colnames(resulthm) <- paste("dataset",aa,sep = "")
colnames(resulthu) <- paste("dataset",aa,sep = "")
row.names(resulthm) <- c("TPR","CLUS-HMC","HMC-DAG-SVM")
row.names(resulthu) <- c("TPR","CLUS-HMC","HMC-DAG-SVM")

for (aa in 1:5)
{
  originalfile <- paste("original",aa,".txt",sep = "")
  DAGlabelfile <- paste("DAGlabel",aa,".txt",sep = "")
  clusfile <- paste("clus",aa,".txt",sep = "")
  originalresult <- read.table(originalfile,header = FALSE,sep = ",")
  DAGlabelresult <- read.table(DAGlabelfile,header = FALSE,sep = ",")
  clusresult <-  read.table(clusfile,header = FALSE,sep = ",")
  resulthm[1,aa] <- originalresult[1,3]
  resulthu[1,aa] <- originalresult[1,6]
  resulthm[2,aa] <- clusresult[1,3]
  resulthu[2,aa] <- clusresult[1,6]
  resulthm[3,aa] <- DAGlabelresult[1,3]
  resulthu[3,aa] <- DAGlabelresult[1,6]
}
barplot(resulthm,col=c('red','green','blue'),beside=TRUE,xlab = "dataset",ylab = "Macro.hf",ylim = c(0,1.2),axes = TRUE)
legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","CLUS-HMC","HMC-DAG-SVM"),fill  = c('red','green','blue') )

barplot(resulthu,col=c('red','green','blue'),beside=TRUE,xlab = "dataset",ylab = "Micro.hf",ylim = c(0,1.2),axes = TRUE)
legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","CLUS-HMC","HMC-DAG-SVM"),fill  = c('red','green','blue') )

