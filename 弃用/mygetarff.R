###产生data.total中的select.data，select.total，select.labels###
###包含根节???###
setwd("C:/Users/1231/Desktop/dataprocessing")
source("dataset_select2.R")
source("dataset_divide.R")
source("dataset_labelselect.R")
source("create_arff.R")
###选择训练??? 1???2???3???4???5
datasetindex <- 1
datasetresult = DatasetSelect(dataset.index = datasetindex)
select.table <- datasetresult[[1]]
###加入根节???
rootcol <- matrix(data = 1,nrow = nrow(select.table),ncol = 1)
row.names(rootcol) <- row.names(select.table)
colnames(rootcol) <- "GO0008150"
select.table <- cbind(rootcol,select.table)
select.data <- datasetresult[[2]]

###拆分训练集train:test=2:1
selecttabletodivide <- DatasetDivide(dataset = select.table)
train.select.table2 <- selecttabletodivide[[1]]
valid.select.table2 <- selecttabletodivide[[2]]
test.select.table2 <- selecttabletodivide[[3]]

selectdatatodivide <- DatasetDivide(dataset=select.data)
train.select.data2 <- selectdatatodivide[[1]]
valid.select.data2 <- selectdatatodivide[[2]]
test.select.data2 <- selectdatatodivide[[3]]

###产生select.labels
train.select.labels2 <- Labelselect(train.select.table2)
valid.select.labels2 <- Labelselect(valid.select.table2)
test.select.labels2 <- Labelselect(test.select.table2)

###产生data.total
train.data.total2 <- list(train.select.data2,train.select.table2,train.select.labels2)
valid.data.total2 <- list(valid.select.data2,valid.select.table2,valid.select.labels2)
test.data.total2 <- list(test.select.data2,test.select.table2,test.select.labels2)

##产生.arff文件，此arff文件与标签值属性值都有关（好像不用属性值）
train.arff <- Createarff(train.select.data2,train.select.table2)
valid.arff <- Createarff(valid.select.data2,valid.select.table2)
test.arff <- Createarff(test.select.data2,test.select.table2)

write.arff(train.arff,"train.arff")
write.arff(valid.arff,"valid.arff")
write.arff(test.arff,"test.arff")


# ###产生.arff文件
# write.arff(train.select.table2,"train.arff")
# write.arff(valid.select.table2,"valid.arff")
# write.arff(test.select.table2,"test.arff")


# trainarfforiginal <- scan("train.arff",what = list(""),sep = "\n")
# each.row.train <- list()
# for (i in 1:length(trainarfforiginal[[1]])) {
#   each.row.train[i] <- strsplit(trainarfforiginal[[1]][i],split = " ")
#   each.row.train[[i]] <- gsub("relation","RELATION",each.row.train[[i]])
#   each.row.train[[i]] <- gsub("attribute","ATTRIBUTE",each.row.train[[i]])
#   each.row.train[[i]] <- gsub("data","DATA",each.row.train[[i]])
#   each.row.train[[i]] <- gsub("numeric","{0,1}",each.row.train[[i]])
#   each.row.train[[i]] <- paste(each.row.train[[i]],collapse = " ")
# }
# 
# write.arff(each.row.train,"newtrain.arff")


