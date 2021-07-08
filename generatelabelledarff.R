#####建立可被clus读取的arff文件#####
#####其格式为属性值+标签，标签之间用@分隔，属性值是NA的用？代替#####
#####最后一个属性是关于边的#####
setwd("C:\\Users\\1231\\Desktop\\dataprocessing")
library(RWeka)
source("find_labels.R")
source("attribute_adjust.R")
#####从HieraGOdata20.txt中导入所有边并通过如果路径a/b中,a,b任意一个是已知节点范围之外的节点就去掉的逻辑筛选掉不存在的边#####
originaledgedata <- scan("HieraGOdata20.txt",what = character())
originaledgedata <- unlist(strsplit(originaledgedata,","))
edgelist <- list()
invalidnum <- vector()
#####由于except.root.labels2中无GO0008150（之前说大家都属于它，给删掉了），GO0008150应当是根节点的存在但edgelist里面有root,有GO0008150，把root都改成GO0008150#####
#####故在except.root.labels2加入GO0008150#####
add.labels <- c("GO0008150",except.root.labels2)
for (i in 1:length(originaledgedata)) {
  edgelist[[i]] <- unlist(strsplit(originaledgedata[i],"/"))
  if (FALSE %in% (edgelist[[i]] %in% add.labels))    
  {        
    invalidnum <- c(invalidnum,i)
  }
}
edgelist <- edgelist[-invalidnum]


edgedata <- vector()
for (i in 1:length(edgelist)) {
  edgedata[i] <- paste(edgelist[[i]],collapse = "/")
}
edgedata <- gsub("GO0008150","root",edgedata)
# lastattribute <- vector()
# for (i in 1:length(listexrootClassesPaths)) {
#   for (j in 1:length(listexrootClassesPaths[[i]])) {
#     lastattribute <- c(lastattribute,listexrootClassesPaths[[i]][j])
#   }
#   
# }
# lastattribute <- gsub("/GO","%GO",lastattribute)
# lastattribute <- gsub("/","",lastattribute)
# lastattribute <- gsub("%","/",lastattribute)

#####划分labelled和unlabelled#####
tolabeltable <- rbind(train.select.table2,valid.select.table2)
tolabeldata <- rbind(train.select.data2,valid.select.data2)
numofrow <- nrow(tolabeltable)
numoflabelled <- round(numofrow/10)
labelledtable <- tolabeltable[1:numoflabelled,]
unlabelledtable <- tolabeltable[((numoflabelled+1):numofrow),]
labelleddata <- tolabeldata[1:numoflabelled,]
unlabelleddata <- tolabeldata[((numoflabelled+1):numofrow),]

#####拼接属性和标签#####
labelledlabels <- Findlabels(labelledtable)
unlabelledlabels <- Findlabels(unlabelledtable)
testlabels <- Findlabels(test.select.table2)

labelled.arff <- cbind(labelleddata,labelledlabels)
unlabelled.arff <- cbind(unlabelleddata,unlabelledlabels)
test.arff <- cbind(test.select.data2,testlabels)
#####设置存储路径#####
#####要改aa#####
aa <- 1
arff.path <- paste("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\204arff\\arff",aa,sep = "")
setwd(arff.path)
#####形成arff文件#####
write.arff(labelled.arff,"labelled1.arff") 
write.arff(unlabelled.arff,"unlabelled1.arff") 
write.arff(test.arff,"test1.arff") 


#####把最后一个attribute改成class#####
Adjustattribute("labelled1.arff","labelled.arff")
Adjustattribute("unlabelled1.arff","unlabelled.arff")
Adjustattribute("test1.arff","labeltest.arff")

write.table(except.root.labels2,"evalclasses.txt",quote = FALSE,sep = "\n",row.names = FALSE,col.names = FALSE)
