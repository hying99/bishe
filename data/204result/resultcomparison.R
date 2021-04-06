#####生成结果对比表格#####
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")
aa <- 1:5
resulthm <- matrix(nrow = 6,ncol = 5)
resulthu <- matrix(nrow = 6,ncol = 5)
colnames(resulthm) <- paste("dataset",aa,sep = "")
colnames(resulthu) <- paste("dataset",aa,sep = "")
row.names(resulthm) <- c("TPR","TOP-DOWN","DOWN-TOP","DAGimprove","CLUS-HMC","HMC-DAG-SVM")
row.names(resulthu) <- c("TPR","TOP-DOWN","DOWN-TOP","DAGimprove","CLUS-HMC","HMC-DAG-SVM")

for (aa in 1:5)
{
  TPRfile <- paste("TPR",aa,".txt",sep = "")
  topdownfile <- paste("topdown",aa,".txt",sep = "")
  downtopfile <- paste("downtop",aa,".txt",sep = "")
  DAGimprovefile <- paste("DAGimprove",aa,".txt",sep = "")
  clusfile <- paste("clus",aa,".txt",sep = "")
  DAGlabelfile <- paste("DAGlabel",aa,".txt",sep = "")
  
  TPRresult <- read.table(TPRfile,header = FALSE,sep = ",")
  topdownresult <- read.table(topdownfile,header = FALSE,sep = ",")
  downtopresult <- read.table(downtopfile,header = FALSE,sep = ",")
  DAGimproveresult <- read.table(DAGimprovefile,header = FALSE,sep = ",")
  clusresult <-  read.table(clusfile,header = FALSE,sep = ",")
  DAGlabelresult <- read.table(DAGlabelfile,header = FALSE,sep = ",")
  
  resulthm[1,aa] <- TPRresult[1,3]
  resulthu[1,aa] <- TPRresult[1,6]
  resulthm[2,aa] <- topdownresult[1,3]
  resulthu[2,aa] <- topdownresult[1,6]
  resulthm[3,aa] <- downtopresult[1,3]
  resulthu[3,aa] <- downtopresult[1,6]
  resulthm[4,aa] <- DAGimproveresult[1,3]
  resulthu[4,aa] <- DAGimproveresult[1,6]
  resulthm[5,aa] <- clusresult[1,3]
  resulthu[5,aa] <- clusresult[1,6]
  resulthm[6,aa] <- DAGlabelresult[1,3]
  resulthu[6,aa] <- DAGlabelresult[1,6]
}
barplot(resulthm,col=c('red','green','blue','yellow','grey','purple'),beside=TRUE,xlab = "dataset",ylab = "Macro.hf",ylim = c(0,1.2),axes = TRUE)
legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","TOP-DOWN","DOWN-TOP","DAGimprove","CLUS-HMC","HMC-DAG-SVM"),fill  = c('red','green','blue','yellow','grey','purple') )

barplot(resulthu,col=c('red','green','blue','yellow','grey','purple'),beside=TRUE,xlab = "dataset",ylab = "Micro.hf",ylim = c(0,1.2),axes = TRUE)
legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","TOP-DOWN","DOWN-TOP","DAGimprove","CLUS-HMC","HMC-DAG-SVM"),fill  = c('red','green','blue','yellow','grey','purple') )

write.csv(resulthm,"hmresult.csv",quote = FALSE,eol = "\n",row.names = TRUE)
write.csv(resulthu,"huresult.csv",quote = FALSE,eol = "\n",row.names = TRUE)
