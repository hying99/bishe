#####生成前四个level的数据集，减小数据量，方便调试,在nodesclean.R之后运行，运行结束后运行partdatasetprocessing.R#####
go.for.level3 <- go.for.level2[1:3]
remainlabels <- unlist(go.for.level3)
remainlabelspos <- which(except.root.labels2 %in% remainlabels)
standardvec <- 1:204
deleteindex <- standardvec[-which(standardvec %in% remainlabelspos)]
except.root.labels2 <- except.root.labels2[-deleteindex]
except.root.table2 <- except.root.table2[,-deleteindex]
exrootClassesPaths <- exrootClassesPaths[-deleteindex]
listexrootClassesPaths <- listexrootClassesPaths[-deleteindex]
listexrootClassesLevels <- listexrootClassesLevels[-deleteindex]
setwd("C:/Users/1231/Desktop/dataprocessing")
source("nodescreation.R")