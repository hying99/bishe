#去掉不属于BP功能的CC.nodes的子孙节点，以及召回率不好的节点的子孙节点20210201
CC.nodes2 <- c("GO0044710", "GO0044763", "GO0044699", "GO0044707", "GO0044767", "GO0007067", "GO0044700", "GO0044702")
CC.nodes.pos2 <- which(except.root.labels2 %in% CC.nodes2)
CC.nodes.pos2 <- c(CC.nodes.pos2,85,87,133,135,136,140,163,189,221,224,225,236,244,246)
CC.nodes2 <- except.root.labels2[CC.nodes.pos2]
removeindex <- CC.nodes.pos2
for (i in 1:length(CC.nodes.pos2)) {
  removeindex <- na.omit(unique(c(removeindex,nodes.to.descendants2[[CC.nodes2[i]]])))
}
removelabels <- except.root.labels2[removeindex]
except.root.labels2 <- except.root.labels2[-removeindex]
except.root.table2 <- except.root.table2[,-removeindex]
exrootClassesPaths <- exrootClassesPaths[-removeindex]
listexrootClassesPaths <- listexrootClassesPaths[-removeindex]
listexrootClassesLevels <- listexrootClassesLevels[-removeindex]
setwd("C:/Users/1231/Desktop/dataprocessing")
source("nodescreation.R")