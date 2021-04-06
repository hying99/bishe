getGOIdx<-function(selGOs,funGOs){
  num<-length(selGOs)
  idxs<-c()
  for (ii in 1:num) {
    idxs<-c(idxs,which(selGOs[ii]==funGOs))
  }
  return(idxs)
}