####组合不同权重组合和不同节点参数####
####四种权重组合####
####两个节点矩阵####
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")
prefix <- "DAGlabellstm"
middle <- "_1221"
aa <- 1:5

for (aa in 1:5) {
  wresult <- matrix(nrow = 2,ncol = 4)
  colnames(wresult) <- c("w1=w2=w3=w4=1","w1=w4=1 w2=w3=2","w1=w3=2 w2=w4=1","w1=w2=2 w3=w4=1")
  row.names(wresult) <- c("hm","hu")
  
  f1 <- paste(prefix,aa,".txt",sep = "")
  f2 <- paste(prefix,aa,"_1221",".txt",sep = "")
  f3 <- paste(prefix,aa,"_2121",".txt",sep = "")
  f4 <- paste(prefix,aa,"_2211",".txt",sep = "")
  
  f1result <- read.table(f1,header = FALSE,sep = ",")
  f2result <- read.table(f2,header = FALSE,sep = ",")
  f3result <- read.table(f3,header = FALSE,sep = ",")
  f4result <- read.table(f4,header = FALSE,sep = ",")
  
  wresult[1,1] <- f1result[1,3]
  wresult[2,1] <- f1result[1,6]
  wresult[1,2] <- f2result[1,3]
  wresult[2,2] <- f2result[1,6]
  wresult[1,3] <- f3result[1,3]
  wresult[2,3] <- f3result[1,6]
  wresult[1,4] <- f4result[1,3]
  wresult[2,4] <- f4result[1,6]
  write.csv(wresult,file = paste("dataset",aa,"weights.csv",sep = ""),quote = FALSE,eol = "\n",row.names = TRUE)

  cresult <- matrix(nrow = 2,ncol = 2)
  colnames(cresult) <- c("ci differs in i","ci=1")
  row.names(cresult) <- c("hm","hu")
  
  g1 <- paste(prefix,aa,middle,".txt",sep = "")
  g2 <- paste(prefix,aa,middle,"c.txt",sep = "")
  
  g1result <- read.table(g1,header = FALSE,sep = ",")
  g2result <- read.table(g2,header = FALSE,sep = ",")
  
  cresult[1,1] <- g1result[1,3]
  cresult[2,1] <- g1result[1,6]
  cresult[1,2] <- g2result[1,3]
  cresult[2,2] <- g2result[1,6]
  write.csv(cresult,file = paste("dataset",aa,"cvalue.csv",sep = ""),quote = FALSE,eol = "\n",row.names = TRUE)
  }


