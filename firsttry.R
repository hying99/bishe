#####¸Ä#####
sigma.labels <- matrix(nrow = nrow(sigma),ncol = ncol(sigma))
for (i in 1:nrow(sigma)) {
  for (j in 1:ncol(sigma)) {
    if(sigma[i,j] > 0)
      {
      sigma.labels[i,j] = 1
    }
    else
    {
      sigma.labels[i,j] = 0
    }
  }
}
sigma.labels2 <- sigma.labels[,-1]
measure.result0=MHevaluate(sigma.labels2,test.select.table2)
for (m in 1:nrow(sigma.labels2)) {
  for (n in 1:ncol(sigma.labels2)) {
    if(sigma.labels2[m,n] > 0 && TRUE %in% (sigma.labels2[m,nodes.to.ancestors2[n]] < 0))
    {
      print(m,n,"\n")
    }
  }
}
