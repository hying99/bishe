#####生成前置分类器是svm的层级约束的结果对比表格#####  
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")  
aa <- 1:5  
resulthm <- matrix(nrow = 4,ncol = 5)  
resulthu <- matrix(nrow = 4,ncol = 5)  
colnames(resulthm) <- paste("dataset",aa,sep = "")  
colnames(resulthu) <- paste("dataset",aa,sep = "")  
row.names(resulthm) <- c("TPR","TOP-DOWN","DOWN-TOP","DAGlabel")  
row.names(resulthu) <- c("TPR","TOP-DOWN","DOWN-TOP","DAGlabel")   
for (aa in 1:5)    
  {      
  TPRfile <- paste("SVMTPR",aa,".txt",sep = "")      
  topdownfile <- paste("SVMtopdown",aa,".txt",sep = "")      
  downtopfile <- paste("SVMdowntop",aa,".txt",sep = "")      
  DAGlabelfile <- paste("DAGlabelsvm",aa,".txt",sep = "")         
  TPRresult <- read.table(TPRfile,header = FALSE,sep = ",")      
  topdownresult <- read.table(topdownfile,header = FALSE,sep = ",")      
  downtopresult <- read.table(downtopfile,header = FALSE,sep = ",")      
  DAGlabelresult <- read.table(DAGlabelfile,header = FALSE,sep = ",")           
  resulthm[1,aa] <- TPRresult[1,3]      
  resulthu[1,aa] <- TPRresult[1,6]      
  resulthm[2,aa] <- topdownresult[1,3]      
  resulthu[2,aa] <- topdownresult[1,6]     
  resulthm[3,aa] <- downtopresult[1,3]     
  resulthu[3,aa] <- downtopresult[1,6]     
  resulthm[4,aa] <- DAGlabelresult[1,3]      
  resulthu[4,aa] <- DAGlabelresult[1,6]       
  }  
barplot(resulthm,col=c('red','green','blue','yellow'),         
        beside=TRUE,xlab = "dataset",ylab = "Macro.hf",ylim = c(0,1.2),axes = TRUE)  
legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","TOP-DOWN","DOWN-TOP","DAGlabel"),   
       fill  = c('red','green','blue','yellow') )   
barplot(resulthu,col=c('red','green','blue','yellow'),
        beside=TRUE,xlab = "dataset",ylab = "Micro.hf",ylim = c(0,1.2),axes = TRUE)  
legend("topleft",inset = 0.01,cex=0.5,legend = c("TPR","TOP-DOWN","DOWN-TOP","DAGlabel"),        
       fill  = c('red','green','blue','yellow') )   
write.csv(resulthm,"svmhmresult.csv",quote = FALSE,eol = "\n",row.names = TRUE)  
write.csv(resulthu,"svmhuresult.csv",quote = FALSE,eol = "\n",row.names = TRUE) 