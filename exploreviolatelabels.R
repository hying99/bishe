#####此部分为探究违反层级约束的节点对所处的层级结构#####
descendantnodes <- vector()
ancestornodes <- vector()
sigmadescendants <- vector()
sigmaancestors <- vector()
violatelabels <- list()
#####用needtochange最多的第一组做实验，这组也是性能差距最大的#####
# for (i in 1:length(needtochangeall)) {
  

  datasetindex = 1
  setwd("C:/Users/1231/Desktop/dataprocessing")
  source("chloss2.R")
  needtochangelabels <- strsplit(needtochangeall[[1]],",")
  for (j in 1:length(needtochangelabels)) {
    descendantnodes <- c(descendantnodes,needtochangelabels[[j]][3])
    # descendantnodes <- unique(descendantnodes)
    descendantlevels <- levelClasses[as.numeric(descendantnodes)]
    sigmadescendants <- c(sigmadescendants,sigma[as.numeric(needtochangelabels[[j]][1]),as.numeric(needtochangelabels[[j]][2])])
    ancestornodes <- c(ancestornodes,needtochangelabels[[j]][2])
    ancestorlevels <- levelClasses[as.numeric(ancestornodes)]
    sigmaancestors <- c(sigmaancestors,sigma[as.numeric(needtochangelabels[[j]][1]),as.numeric(needtochangelabels[[j]][3])])
  }
  # }
violatelabels[[1]] <- descendantnodes
violatelabels[[2]] <- descendantlevels
violatelabels[[3]] <- sigmadescendants
violatelabels[[4]] <- ancestornodes
violatelabels[[5]] <- ancestorlevels
violatelabels[[6]] <- sigmaancestors
violatelabelsdataframe <- as.data.frame(violatelabels)
colnames(violatelabelsdataframe) <- c("descendantnodes","descendantlevels","sigmadescendants","ancestornodes","ancestorlevels","sigmaancestors")
write.csv(violatelabelsdataframe,"violatelabels.csv",row.names = FALSE)
