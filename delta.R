delta <- test.select.table2 - y
for (i in 1:nrow(y)) {
  if(1 %in% delta[i,] || -1 %in% delta[i,])
  {
    variance<- sum(abs(delta[i,which(delta[i,] != 0)]))
    cat(paste(i,variance),"\n")
  }
    }
