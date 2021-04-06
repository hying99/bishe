#####predict.labels2是未经DAGlabel处理的
#####y是经DAGlabel处理的
#####此程序为比对用DAGlabel是否做到让分类结果真正满足层级约束要求
effectresult <- matrix(0,nrow = 5,ncol = 7)
row.names = c(paste("dataset",1:5,sep = ""))
colnames(effectresult) <- c("tochangelabels","DAGchange","TPRchange","DAGwork","TPRwork","DAGright","TPRright")
effectresult <- as.data.frame(effectresult)
needtochangeall <- list()
violatesigmaall <- list()
# #####原始值，TPR，DAGlabel后与真实标签不同的分别有多少个#####
# originalviolatelabels <- vector()
# TPRviolatelabels <- vector()
# DAGlabelviolatelabels <- vector()
# for (i in 1:nrow(predict.labels2)) {
#   for (j in 1:ncol(predict.labels2)) {
#     ###找两者不同的pair
#     if(prob.labels2[i,j] != test.select.table2[i,j])
#     {
#       originalviolatelabels <- c(originalviolatelabels,paste(i,j,sep = ",",collapse = " "))
#     }
#     if(predict.labels2[i,j] != test.select.table2[i,j])
#     {
#       TPRviolatelabels <- c(TPRviolatelabels,paste(i,j,sep = ",",collapse =" "))
#     }
#     if(y[i,j] != test.select.table2[i,j])
#     {
#       DAGlabelviolatelabels <- c(DAGlabelviolatelabels,paste(i,j,sep = ",",collapse = " "))
#     }
#   }
# }
for (datasetindex in 1:5) {
  ###导入test.select.table2
  setwd("C:/Users/1231/Desktop/dataprocessing")
  source("dataset_select2.R")
  source("dataset_divide.R")
  ###选择训练集 1、2、3、4、5
  datasetresult = DatasetSelect(dataset.index = datasetindex)
  select.table <- datasetresult[[1]]
  select.data <- datasetresult[[2]]
  
  ###拆分训练集train:test=2:1
  selecttabletodivide <- DatasetDivide(dataset = select.table)
  train.select.table2 <- selecttabletodivide[[1]]
  valid.select.table2 <- selecttabletodivide[[2]]
  test.select.table2 <- selecttabletodivide[[3]]
  ###导入predict.labels2
  
  source("chloss2.R")
  downtop.prob2=DownTopStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,prob.for.genes2)
  topdown.prob2=TopDownStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,downtop.prob2)
  predict.labels2=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  predict.scores2=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  rownames(predict.labels2)=rownames(test.select.table2)
  colnames(predict.labels2)=colnames(test.select.table2)
  rownames(predict.scores2)=rownames(test.select.table2)
  colnames(predict.scores2)=colnames(test.select.table2)
  for(i in 1:nrow(topdown.prob2))
  {
    for(j in 1:length(except.root.labels2))
    {
      predict.scores2[i,j]=topdown.prob2[i,(2*j-1)]
      if(topdown.prob2[i,(2*j-1)]>0.5)
      {
        predict.labels2[i,except.root.labels2[j]]=1
      }
    }
  }
  #####导入prob.is.one#####
  #####prob.is.one转成0/1值#####
  prob.labels2 <- data.frame()
  for (i in 1:nrow(prob.is.one)) {
    for (j in 1:ncol(prob.is.one)) {
      if(prob.is.one[i,j] > 0.5)
      {
        prob.labels2[i,except.root.labels2[j]]=1
      }
      else
      {
        prob.labels2[i,except.root.labels2[j]]=0
      }
    }
  }
  measure.result.original <- MHevaluate(prob.labels2,test.select.table2)
  setwd("C:/Users/1231/Desktop/dataprocessing/data/204result")
  write.table(measure.result.original,"original",datasetindex,".txt",sep = ",",eol="\n",row.names = FALSE,col.names = FALSE,quote = FALSE)
  #####导入y#####
  setwd("C:/Users/1231/Desktop/dataprocessing") 
  source("DAGlabel.R")
  
  
  #####TPR修改的对数#####
  changedlabels <- vector()
  for (u in 1:nrow(prob.is.one))
  {
    for (v in 1:ncol(prob.is.one))
    {
      
      if(  predict.labels2[u,v] != prob.labels2[u,v] ){
        
        changedlabels <-  c(changedlabels,paste(u,v,prob.labels2[u,v],predict.labels2[u,v],sep = ",",collapse = " "))
        
      }
    }
  }
  
  
  #####DAGlabel修改的对数#####
  changedlabels2 <- vector()
  for (u in 1:nrow(prob.is.one))
  {
    for (v in 1:ncol(prob.is.one))
    {
      
      if(  y[u,v] != prob.labels2[u,v] ){
        
        changedlabels2 <-  c(changedlabels2,paste(u,v,prob.labels2[u,v],y[u,v],sep = ",",collapse = " "))
        
      }
    }
  }
  
  # 
  # #####DAGlabel修正错的标签#####
  # 
  # wrongchanges <- vector()
  # for (u in 1:nrow(prob.is.one)) 
  # {
  #   for (v in 1:ncol(prob.is.one))
  #   {
  #     
  #     if(  y[u,v] != prob.labels2[u,v] && y[u,v] != test.select.table2[u,v]){
  #       
  #       wrongchanges <-  c(wrongchanges,paste(u,v,prob.labels2[u,v],y[u,v],sep = ",",collapse = " "))
  #     }
  #   }
  # }
  # #####分错，把1变成0的有92个 #####
  # onetozeroF <- vector()
  # for(i in 1:length(wrongchanges))
  # {
  #   if(as.numeric(strsplit(wrongchanges,",")[[i]][3]) == 1)
  #    { 
  #     onetozeroF <- c(onetozeroF,i)
  #   }
  # }
  # #####分对，把1变成0的#####
  # onetozeroT <- vector()
  # for(i in 1:length(changedlabels2))
  # {
  #   if(as.numeric(strsplit(changedlabels2,",")[[i]][3]) == 1)
  #   { 
  #     onetozeroT <- c(onetozeroT,i)
  #   }
  # }
  #####初始SVM分类结果里面违反层级约束needtochange的个数#####
  needtochange <- vector()
  violatesigma <- vector()
  c11 <- vector()
  for (u in 1:nrow(prob.labels2)) {
    for (v in 1:ncol(prob.labels2)) {
      if(FALSE %in% is.na(nodes.to.descendants2[[v]]))
      {
        for (q in 1:length(nodes.to.descendants2[[v]])) {
          if (prob.labels2[u,v] < prob.labels2[u,nodes.to.descendants2[[v]][q]])
          {
            needtochange <- c(needtochange,paste(u,v,nodes.to.descendants2[[v]][q],sep = ",",collapse = ""))
            violatesigma <- c(violatesigma,paste(sigma[u,v],sigma[u,nodes.to.descendants2[[v]][q]],sep = ",",collapse = " "))
            }
        }
      }
    }
  }  
  needtochangeall[[datasetindex]] <- needtochange
  violatesigmaall[[datasetindex]] <- violatesigma
  write.table(needtochangeall[[datasetindex]],paste("needtochange",datasetindex,".txt",sep = ""),sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
  needtochangelist <- strsplit(needtochange,",")
  DAGwork <- vector()
  TPRwork <- vector()
  DAGright <- vector()
  TPRright <- vector()
  for (i in 1:length(needtochange)) {
    
    if(y[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] >= y[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])])
    {
      DAGwork <- c(DAGwork,i)
    }
    if(y[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] == test.select.table2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] &&
       y[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])] == test.select.table2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])] )
    {
      DAGright <- c(DAGright,i)
    }
    if(predict.labels2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] >= predict.labels2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])])
    {
      TPRwork <- c(TPRwork,i)
    }
    if(predict.labels2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] == test.select.table2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] &&
       predict.labels2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])] == test.select.table2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])] )
    {
      TPRright <- c(TPRright,i)
    }
  }
  # #####DAGlabel把父节点改成1是不是因为子节点是1父节点是0#####
  # WORK <- vector()
  # for(i in 1:length(changedlabels2))
  # {
  #   if(as.numeric(strsplit(changedlabels2,",")[[i]][4]) == 1 && FALSE %in% nodes.to.descendants2[[v]])
  #   { 
  #     for (q in 1:length(nodes.to.descendants2[[v]])) {
  #       if(prob.labels2[as.numeric(strsplit(changedlabels2,",")[[i]][1]),nodes.to.descendants2(as.numeric(strsplit(changedlabels2,",")[[i]][2]))] == 1)
  #       {
  #         WORK <- c(WORK,paste(u,v,sep = ",",collapse = " "))
  #       }
  #     }
  #     
  #       }
  # }
  # #####DAGlabel把子节点改成0是不是因为父节点是0子节点是1#####
  # WORK2 <- vector()
  # for(i in 1:length(changedlabels2))
  # {
  #   if(as.numeric(strsplit(changedlabels2,",")[[i]][4]) == 0 && FALSE %in% nodes.to.ancestors2[[v]])
  #   { 
  #     for (q in 1:length(nodes.to.ancestors2[[v]])) {
  #       if(prob.labels2[as.numeric(strsplit(changedlabels2,",")[[i]][1]),nodes.to.ancestors2(as.numeric(strsplit(changedlabels2,",")[[i]][2]))] == 0)
  #       {
  #         WORK2 <- c(WORK2,paste(u,v,sep = ",",collapse = " "))
  #       }
  #     }
  #     
  #   }
  # }
  effectresult[datasetindex,1] <- length(needtochange)
  effectresult[datasetindex,2] <- length(changedlabels2)
  effectresult[datasetindex,3] <- length(changedlabels)
  effectresult[datasetindex,4] <- length(DAGwork)
  effectresult[datasetindex,5] <- length(TPRwork)
  effectresult[datasetindex,6] <- length(DAGright)
  effectresult[datasetindex,7] <- length(TPRright)
  
}
 
write.csv(effectresult,"effectresult.csv",quote = FALSE,row.names = TRUE)


