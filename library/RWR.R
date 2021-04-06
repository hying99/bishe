##'Random walks with restart on transMat
##'
##'
##'@param transMat: a matrix to describe the transition probability
##'@param alpha: the restart probability of random walk
##'@param maxIter: the max iteration
##'
##'Coded by Guangyuan Fu (fugy@swu.edu.cn), 
##'College of Computer and Information Science,Southwest University.
##'version 1.0 date:2016-04-20
RWR<-function(transMat,alpha,maxIter){
  Nfun<-nrow(transMat)
  eyeMat<-diag(1,Nfun,Nfun)
  oldW<-eyeMat
  newW<-oldW
  threshold<-0.000001
  iter<-0
  delta<-10*threshold
  while (delta>threshold && iter<maxIter) {
    newW<-alpha*eyeMat+(1-alpha)*oldW%*%transMat
    delta<-sum(abs(newW-oldW))
    oldW<-newW
    iter<-iter+1
  }
  return(newW)
}