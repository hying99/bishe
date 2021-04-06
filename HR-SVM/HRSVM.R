#####产生HR-SVM需要的dat_train，dat_test#####
setwd("C:/Users/1231/Desktop/dataprocessing/HR-SVM")
source("generate_dat.R")
dat_train <- generatedat(train.select.table2,train.select.data2)
dat_test <- generatedat(test.select.table2,test.select.data2)
####产生新的withroot.nodes.to.children2####
withroot.nodes.to.children2 <- vector("list",length = length(except.root.labels2) + 1)
with.root.labels2 <- c("GO0008150",except.root.labels2)
names(withroot.nodes.to.children2) <- with.root.labels2
for (i in 1:(length(except.root.labels2) + 1)) {
  if (i == 1)
    {
  withroot.nodes.to.children2[[i]] <- (which(except.root.labels2 %in% go.for.level2[[1]]))+1
  }
  if (i != 1)
  {
    if(FALSE %in% is.na(nodes.to.children2[[i-1]] ))
  {
    withroot.nodes.to.children2[[i]] <- (nodes.to.children2[[i-1]] +1)
    }
    else
    {
      withroot.nodes.to.children2[[i]] <- NA
    }
  }
}
setwd("C:/Users/1231/Desktop/dataprocessing/HR-SVM/hrsvmdata")#首先将工作路径设置为代码的路径
#####产生hf2#####
k=0;
hf2 <- matrix(data = 0, nrow = 1000, ncol = 1);
for(i in 1:(ncol(test.select.table2)+1))
{
  for(j in 1:ncol(test.select.table2))
  {
    if(!is.na(withroot.nodes.to.children2[[i]][j]))
    {
      if(i!=(ncol(test.select.table2)+1))
      {
        k=k+1;
        hf2[k,1]=paste(i,withroot.nodes.to.children2[[i]][j]);
      }else
      {
        k=k+1;
        hf2[k,1]=paste(0,withroot.nodes.to.children2[[ncol(test.select.table2)+1]][j]);
      }
      
      
    }else
    {
      k=k;
    }
  }
}

tr.hf2 <-matrix(data = 0, nrow = k, ncol = 1);
tr.hf2=hf2[1:k,]
hfname=paste(datasetindex,"train.hf",sep = "_")
write.table(tr.hf2,hfname,eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE)
#####产生train.dat文件#####
train_datname=paste(datasetindex,"train.dat",sep = "_")
write.table(dat_train,train_datname,eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE)
#####产生test.dat文件#####
test_datname=paste(datasetindex,"test.dat",sep = "_")
write.table(dat_test,test_datname,eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE)