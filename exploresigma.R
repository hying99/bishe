#####分析sigma大的值有什么特点
#####先排序#####
sigmarank <- order(colMeans(sigma),decreasing = TRUE)
#####sigma大是因为层级结构深吗#####
sigmalevel <- levelClasses[sigmarank-1]
#####sigma大是因为子节点多吗#####
sigmachildnum <- vector()
for (i in 1:length(sigmarank)) {
  sigmachildnum[i] <- length(nodes.to.children2.ch[[(sigmarank[i])]])
}
#####sigma大是因为本身预测概率值大吗#####
probrank <- order(colMeans(prob.is.one),decreasing = TRUE)
#####DAGlabel极端性证明#####
num <- vector(length = nrow(y))
# error <- vector(mode = "integer")
for (i in 1:nrow(y)) { 
  num[i] = 0
  for (j in 1:ncol(y)) { 
   if(test.select.table2[i,j] != y[i,j])
   {
     num[i] <- num[i] + 1
     # error[i] <- paste(error[i],levelClasses[j],sep = ",")
   }
  }
}
evidence <- matrix(nrow = length(num),ncol = 3)
to1labels <- vector("integer",length = length(num))
to0labels <- vector("integer",length = length(num))
colnames(evidence) <- c("violatelabels","0to1labels","1to0labels")
for (q in 1:length(num)) {
      to1labels[q] <- length(which(y[q,which(test.select.table2[q,] != y[q,])] == 1))
      to0labels[q] <- length(which(y[q,which(test.select.table2[q,] != y[q,])] == 0))
  
  evidence[q,1] <- num[q]
  evidence[q,2] <- to1labels[q]
  evidence[q,3] <- to0labels[q]
}
evidence <- evidence[which(evidence[,1] != 0),]
write.csv(evidence,"DAGchangedetails.csv",quote = FALSE,row.names = FALSE)
#####绘制饼状图#####
to1num <- length(which(evidence[,2] == 0))
to0num <- length(which(evidence[,3] == 0))
othernum <- nrow(evidence) - to1num - to0num
num <- c(to1num,to0num,othernum)
labels <- c("0to1num","1to0num","othernum")
pie(num,labels = labels)