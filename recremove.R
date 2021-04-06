#去掉recall值小的节点20210201
SVMresult <- read.table("C:/Users/1231/Desktop/dataprocessing/data/data223/223.txt",header = FALSE)
RECSVM <- SVMresult[,3]
RECSVM <- strsplit(RECSVM,'rec：')
for (i in 1:length(RECSVM)) {
       RECSVM[[i]] <- RECSVM[[i]][-1]
}
RECinvalidindex <- vector()
for (i in 1:length(RECSVM)) 
  {
       if (RECSVM[i] < 0.5)
         {
         RECinvalidindex <- c(RECinvalidindex,i)
         }
}
RECinvalidlabels <- except.root.labels2[RECinvalidindex]
write.table(RECinvalidlabels,"RECinvalidlabels.txt",row.names = FALSE,col.names = FALSE)