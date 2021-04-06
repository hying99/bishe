#####搜寻某一个数据集每个所含的全部GO标签并用@分隔#####
Findlabels <- function(select.table)
{
  labellist <- vector()
  for (i in 1:nrow(select.table)) {
    num <- vector()
    for (j in 1:ncol(select.table)) {
      if (select.table[i,j] == 1)
      {
        num <- c(num,j)
      } 
    }
    labellist[i] <- paste(except.root.labels2[num],collapse = "@")
  }
  return(labellist)
}