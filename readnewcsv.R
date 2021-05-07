####python输出的new.csv####

setwd("C:\\Users\\1231\\Desktop\\dataprocessing")
new1 <- read.csv("new1.csv",header = FALSE)
measure.resultnew=MHevaluate(new1,test.select.table2)
setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")
fname <- paste("lstm",datasetindex,".txt",sep = "")
write.table(measure.resultnew,fname,sep = ",",quote = FALSE,row.names = FALSE)




# setwd("C:\\Users\\1231\\Desktop\\dataprocessing")
# for (i in 1:5) {
#   csvname <- paste("new",i,".csv",sep = "")
#   new <- read.csv(csvname,header = FALSE)
#   datasetindex <- i
#   setwd("C:/Users/1231/Desktop/dataprocessing")
#   source("dataset_select2.R")
#   source("dataset_divide.R")
#   ###选择训练集 1、2、3、4、5
#   datasetresult = DatasetSelect(dataset.index = datasetindex)
#   select.table <- datasetresult[[1]]
#   select.data <- datasetresult[[2]]
#   
#   ###拆分训练集train:test=2:1
#   selecttabletodivide <- DatasetDivide(dataset = select.table)
#   train.select.table2 <- selecttabletodivide[[1]]
#   valid.select.table2 <- selecttabletodivide[[2]]
#   test.select.table2 <- selecttabletodivide[[3]]
#   
#   measure.resultnew=MHevaluate(new,test.select.table2)
#   setwd("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204result")
#   fname <- paste("lstm",i,".txt",sep = "")
#   write.table(measure.resultnew,fname,sep = ",",quote = FALSE,row.names = FALSE)
# }




