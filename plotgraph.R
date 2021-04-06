#生成包含所选节点的结构图
setwd("C:/Users/1231/Desktop/dataprocessing/library")
source("plot_label_graph.R")
library(hcgene)
except.root.nodes2 <- gsub("GO","GO:",except.root.labels2)
# select.nodes2 <- c(select.nodes2,"GO:0008150")
BP.univ.graph2 <- Build.universal.graph.ontology.down(ontology = "BP")
# CC.univ.graph2 <- Build.universal.graph.ontology.down(ontology = "CC")
# MF.univ.graph2 <- Build.universal.graph.ontology.down(ontology = "MF")

# CC.nodes2 <- c("GO:0044710", "GO:0044763", "GO:0044699", "GO:0044707", "GO:0044767", "GO:0007067", "GO:0044700", "GO:0044702")
# CC.nodes.pos2 <- which(select.nodes2 %in% CC.nodes2)
# #CC.nodes.pos2 <- grep(paste(CC.nodes2,collapse = "|"),select.nodes2)
# BP.nodes2 <- select.nodes2[-CC.nodes.pos2]
# graph.select.node2 <- subGraph(except.root.labels2, BP.univ.graph2)
graphnew2 <- PlotLabelGraph(except.root.nodes2,BP.univ.graph2,plot.en=TRUE,num.only=TRUE,output.en=FALSE,"GOstructure")

