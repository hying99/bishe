##' Calculate the potential negatives by downward random walks with restart
##' with hierarchical struture similarity and empirical conditional probability
##' 
##'Guangyuan Fu, Jun Wang, Guoxian Yu, College of Computer and Information
##'Science, Southwest University. Contact gxyu@swu.edu.cn, fugy@swu.edu.cn
##'
##'@param labMat: protein-function association matrix, where each row is a
##'               gene/protein, and each column is a GO term. Entry 1 indicates that
##'               the protein is annotated with the term, and 0 means the annotation is unknown.
##'@param dagMat: the adjacent matrix of the DAG branch
##'               in which dagMat[i,j]=1 means j is the child of i
##'@param ProbSim: empirical conditional probability for each pair of terms
##'@param linSim: Lin similarity for each pair of terms based on structureIC 
##'
##'Coded by Guangyuan Fu , 
##'version 1.0 date:2016-04-20
##'
getNegPotential<-function(labMat,dagMat,ProbSim,linSim){
  num_k<-4 #number of steps of random walks
  beta<-0.5 #restart probability for random walk
  alpha<-0.5 #parameter to adjust hierarchical structure similarity and empirical similarity
  
  #normalize the transition probability
  dt<-rowSums(ProbSim)
  idx_dt<-which(dt>0)
  dt[idx_dt]<-1/dt[idx_dt]
  ProbSim<-diag(dt)%*%ProbSim
  
  invT<-rowSums(labMat)
  idx<-which(invT>0)
  invT[idx]<-1/invT[idx]
  invT<-diag(invT)
  
  ##downward random walks with restart with hierarchical struture similarity
  transMat1<-dagMat*linSim
  dl<-rowSums(linSim)
  idx_dl<-which(dl>0)
  dl[idx_dl]<-1/dl[idx_dl]
  transMat1<-diag(dl)%*%transMat1
  transMat1<-transMat1-diag(diag(transMat1))
  transMat1<-RWR(transMat1,alpha,num_k)
  
  ##calculate the potential probability of Positives using lin's structure similarity
  postiveProb1<-invT%*%labMat%*%transMat1
  postiveProb1[which(labMat==1)]<- 1
  
  ##downward random walks with restart with empirical conditional probability
  transMat2<-RWR(ProbSim,alpha,num_k)
  
  ##calculate the potential probability of Positives using empirical conditional probability
  postiveProb2<-invT%*%labMat%*%transMat2
  postiveProb2[which(labMat==1)]<- 1
  
  ##integrate the two probability of positives
  postiveProb<-beta*postiveProb1+(1-beta)*postiveProb2
  negativeProb<- 1-postiveProb
  
  ##set the known labels as -1 and all the labels of proteins without any GO annotation as -0.5
  negativeProb[which(labMat==1)]<- -1
  noGO<-which(rowSums(labMat)==0)
  negativeProb[noGO,]<- -0.5
  
  return(negativeProb)
  
  
}