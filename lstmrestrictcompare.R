####评估前置分类器是LSTM的各种层级约束算法####
#####生成结果对比表格#####
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")
aa <- 1:5
resulthm <- matrix(nrow = 5,ncol = 5)
resulthu <- matrix(nrow = 5,ncol = 5)
colnames(resulthm) <- paste("dataset",aa,sep = "")
colnames(resulthu) <- paste("dataset",aa,sep = "")
row.names(resulthm) <- c("TPR","TOP-DOWN","DOWN-TOP","CLUS-HMC","HMC-DAG-LSTM")
row.names(resulthu) <- c("TPR","TOP-DOWN","DOWN-TOP","CLUS-HMC","HMC-DAG-LSTM")

for (aa in 1:5)
{
  TPRfile <- paste("LSTMTPR",aa,".txt",sep = "")
  topdownfile <- paste("LSTMtopdown",aa,".txt",sep = "")
  downtopfile <- paste("LSTMdowntop",aa,".txt",sep = "")
  #DAGimprovefile <- paste("DAGimprove",aa,".txt",sep = "")
  clusfile <- paste("clus",aa,".txt",sep = "")
  DAGlabelfile <- paste("DAGlabellstm",aa,"_1221.txt",sep = "")
  #DAGlabelnnfile <- paste("DAGlabelnn",aa,".txt",sep = "")
  
  TPRresult <- read.table(TPRfile,header = FALSE,sep = ",")
  topdownresult <- read.table(topdownfile,header = FALSE,sep = ",")
  downtopresult <- read.table(downtopfile,header = FALSE,sep = ",")
  #DAGimproveresult <- read.table(DAGimprovefile,header = FALSE,sep = ",")
  clusresult <-  read.table(clusfile,header = FALSE,sep = ",")
  DAGlabelresult <- read.table(DAGlabelfile,header = FALSE,sep = ",")
  #DAGlabelnnresult <- read.table(DAGlabelnnfile,header = FALSE,sep = ",")
  
  resulthm[1,aa] <- TPRresult[1,3]
  resulthu[1,aa] <- TPRresult[1,6]
  resulthm[2,aa] <- topdownresult[1,3]
  resulthu[2,aa] <- topdownresult[1,6]
  resulthm[3,aa] <- downtopresult[1,3]
  resulthu[3,aa] <- downtopresult[1,6]
  resulthm[4,aa] <- clusresult[1,3]
  resulthu[4,aa] <- clusresult[1,6]
  resulthm[5,aa] <- DAGlabelresult[1,3]
  resulthu[5,aa] <- DAGlabelresult[1,6]

}
# barplot(resulthm,col=c('red','green','blue','yellow','grey','purple','brown'),beside=TRUE,xlab = "dataset",ylab = "Macro.hf",ylim = c(0,1.2),axes = TRUE)
# legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","TOP-DOWN","DOWN-TOP","DAGimprove","CLUS-HMC","HMC-DAG-SVM","HMC-DAG-MLP"),fill  = c('red','green','blue','yellow','grey','purple','brown') )
# 
# barplot(resulthu,col=c('red','green','blue','yellow','grey','purple','brown'),beside=TRUE,xlab = "dataset",ylab = "Micro.hf",ylim = c(0,1.2),axes = TRUE)
# legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","TOP-DOWN","DOWN-TOP","DAGimprove","CLUS-HMC","HMC-DAG-SVM","HMC-DAG-MLP"),fill  = c('red','green','blue','yellow','grey','purple','brown') )

write.csv(resulthm,"lstmhmresult.csv",quote = FALSE,eol = "\n",row.names = TRUE)
write.csv(resulthu,"lstmhuresult.csv",quote = FALSE,eol = "\n",row.names = TRUE)
