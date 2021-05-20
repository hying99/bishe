#####改进DAGlabel算法#####
####搜索sigma大于0且深度最深的节点####
sigma2 <- sigma[,-1]
label2 <- matrix(nrow = nrow(sigma2),ncol = ncol(sigma2))
for (m in 1:nrow(sigma2)) {
  for (n in 1:ncol(sigma2)) {
    if(sigma2[m,n] > 0 )
      {
      label2[m,n] <- 1
    }
    else
    {
      label2[m,n] <- 0
    }
  }
}
#####在sigma2里找子孙节点sigma小于0，祖先节点sigma大于0的
for (i in 1:nrow(sigma2)) 
  {
  cat(i,"\n")
  #####从下向上找违反的节点#####
  for (j in length(go.for.level2):1) 
    {
    for (k in 1:length(go.for.level2[[j]])) {
      childnode <- nodes.to.index2[[go.for.level2[[j]][k]]]
      if (FALSE %in% is.na(nodes.to.ancestors2[[childnode]]))
      {
        #####按ancestornode的层级深度排序，优先考虑层级深的#####
        nodelist <- nodes.to.ancestors2[[childnode]][order(levelClasses[nodes.to.ancestors2[[childnode]]],decreasing = TRUE)]
        for (p in 1:length(nodelist)) {
        if (sigma2[i,childnode] > 0){
        while (sigma2[i,nodelist[p]]< 0 && p != (length(nodelist)+1)) {
          ancestornode <- nodelist[p]
          #####ancestornode是第一层次的话，找根节点的sigma值合成，否则出现NA#####
          if (levelClasses[ancestornode] > 2)
          {
          nodesfamillabel2 <- c(sigma2[i,ancestornode],sigma2[i,nodes.to.ancestors2[[ancestornode]]],sigma2[i,nodes.to.descendants2[[ancestornode]]])
          sigma2[i,ancestornode] <- mean(nodesfamillabel2)
          }
          if (levelClasses[ancestornode] == 2)
          {
            nodesfamillabel2 <- c(sigma2[i,ancestornode],sigma[i,1],sigma2[i,nodes.to.descendants2[[ancestornode]]])
            sigma2[i,ancestornode] <- mean(nodesfamillabel2)
          }
          if (sigma2[i,ancestornode] < 0) {
            label2[i,ancestornode] <- 0
            label2[i,nodes.to.descendants2[[ancestornode]]] <- 0
          }
          else
          {
            label2[i,ancestornode] <- 1
            label2[i,nodes.to.ancestors2[[ancestornode]]] <- 1
          }
          p = p+1
        }
          p = p+1
        }
          }
    }
}
  }
}



measure.result.imp=MHevaluate(label2,test.select.table2)
# setwd("C:/Users/1231/Desktop/dataprocessing/data/204result")
# file.name <- paste("DAGimprove",datasetindex,".txt",sep = "")
# write.table(measure.result.imp,file = file.name,row.names = FALSE,col.names = FALSE,sep = ",")
####检测符不符合层级约束####
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
# needtochangeall[[datasetindex]] <- needtochange
# violatesigmaall[[datasetindex]] <- violatesigma
# write.table(needtochangeall[[datasetindex]],paste("needtochange",datasetindex,".txt",sep = ""),sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
needtochangelist <- strsplit(needtochange,",")
DAGwork <- vector()
# TPRwork <- vector()
DAGright <- vector()
# TPRright <- vector()
for (i in 1:length(needtochange)) {

  if(label2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] >= label2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])])
  {
    DAGwork <- c(DAGwork,i)
  }
  if(label2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] == test.select.table2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][2])] &&
     label2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])] == test.select.table2[as.numeric(needtochangelist[[i]][1]),as.numeric(needtochangelist[[i]][3])] )
  {
    DAGright <- c(DAGright,i)
  }

}