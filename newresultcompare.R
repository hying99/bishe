####SVM,MLP,lstm的原始结果和DAGlabel结果对比####

####构造结果汇总文件####
frame1hm <- matrix(nrow = 5,ncol = 3)
frame1hu <- matrix(nrow = 5,ncol = 3)
frame2hm <- matrix(nrow = 5,ncol = 3)
frame2hu <- matrix(nrow = 5,ncol = 3)
row.names(frame1hm) <- paste("dataset",1:5,sep = "")
row.names(frame1hu) <- paste("dataset",1:5,sep = "")
row.names(frame2hm) <- paste("dataset",1:5,sep = "")
row.names(frame2hu) <- paste("dataset",1:5,sep = "")
colnames(frame1hm) <- c("SVM","MLP","LSTM")
colnames(frame1hu) <- c("SVM","MLP","LSTM")
colnames(frame2hm) <- c("SVM-DAG","MLP-DAG","LSTM-DAG")
colnames(frame2hu) <- c("SVM-DAG","MLP-DAG","LSTM-DAG")

####读数据####
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")
for (p in 1:5) {
  SVMfile <- paste("svm",p,".txt",sep = "")
  SVMresult <- read.table(SVMfile,header = FALSE,sep = ",")
  MLPfile <- paste("nn",p,".txt",sep = "")
  MLPresult <- read.table(MLPfile,header = TRUE,sep = ",")
  LSTMfile <- paste("lstm",p,".txt",sep = "")
  LSTMresult <- read.table(LSTMfile,header = TRUE,sep = ",")
  SVMlabelfile <- paste("DAGlabelsvm",p,".txt",sep = "")
  SVMlabelresult <- read.table(SVMlabelfile,header = FALSE,sep = ",")
  MLPlabelfile <- paste("DAGlabelnn",p,".txt",sep = "")
  MLPlabelresult <- read.table(MLPlabelfile,header = FALSE,sep = ",")
  LSTMlabelfile <- paste("DAGlabellstm",p,".txt",sep = "")
  LSTMlabelresult <- read.table(LSTMlabelfile,header = FALSE,sep = ",")
  ####原始结果####
  frame1hm[p,1] <- SVMresult[1,3]
  frame1hm[p,2] <- MLPresult[1,3]
  frame1hm[p,3] <- LSTMresult[1,3]
  frame1hu[p,1] <- SVMresult[1,6]
  frame1hu[p,2] <- MLPresult[1,6]
  frame1hu[p,3] <- LSTMresult[1,6]
  ####DAGlabel结果####
  frame2hm[p,1] <- SVMlabelresult[1,3]
  frame2hm[p,2] <- MLPlabelresult[1,3]
  frame2hm[p,3] <- LSTMlabelresult[1,3]
  frame2hu[p,1] <- SVMlabelresult[1,6]
  frame2hu[p,2] <- MLPlabelresult[1,6]
  frame2hu[p,3] <- LSTMlabelresult[1,6]  
}

write.csv(frame1hm,"hmresult1.csv",quote = FALSE,row.names = TRUE)
write.csv(frame1hu,"huresult1.csv",quote = FALSE,row.names = TRUE)
write.csv(frame2hm,"hmresultDAG.csv",quote = FALSE,row.names = TRUE)
write.csv(frame2hu,"huresultDAG.csv",quote = FALSE,row.names = TRUE)


hmresult <- cbind(frame1hm,frame2hm)
write.csv(hmresult,"hmresult0.csv",quote = FALSE,row.names = TRUE)
huresult <- cbind(frame1hu,frame2hu)
write.csv(huresult,"huresult0.csv",quote = FALSE,row.names = TRUE)