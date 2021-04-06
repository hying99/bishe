setwd("C:/Users/1231/Desktop/dataprocessing")
source("traindataprocessing.R")
source("validatedataprocessing.R")
source("testdataprocessing.R")
#读入数据，针对每个节点拆分训练集
newtraindataset <- read.csv("C:/Users/1231/Desktop/dataprocessing/newtraindataset.csv",header = TRUE)
newtrainclass <- read.csv("C:/Users/1231/Desktop/dataprocessing/newtrainclass.csv",header = TRUE)
trainnum <- ncol(newtrainclass)
setwd("C:/Users/1231/Desktop/dataprocessing/data/data204")
for (k in 1:trainnum) {
  traindataset <- newtraindataset
  cat(k,"\n")
  traindataset[,ncol(traindataset) + 1] <- newtrainclass[,k]
  traindataset <- traindataset[order(-traindataset$V51),]
  write.table(traindataset,paste(k,".csv",sep = ""),sep = ",",row.names = FALSE,col.names = FALSE)
}