#节点for chloss
#包含根节点
# select.nodes2 <- gsub("GO","GO:",except.root.labels2)
select.nodes2 <- c("GO0008150",except.root.labels2)
nodes.total.num=length(select.nodes2)

#each.go.level.num2,go.for.level2的向量表示，某一个GO属于哪个层次
each.go.level.num2 <- vector(length = length(except.root.labels2))
names(each.go.level.num2) <- except.root.labels2
for (i in 1:length(except.root.labels2)) {
  each.go.level.num2[except.root.labels2[i]] <- grep(except.root.labels2[i],go.for.level2)
  
}
#each.level.nodes.num2,每一个层次有几个节点
each.level.nodes.num2 <- vector("list")
for (j in 1:length(go.for.level2)) {
  each.level.nodes.num2[[j]] <- length(go.for.level2[[j]])
}
names(each.level.nodes.num2) <- 1:length(go.for.level2) 
#含根节点的nodes
nodes.to.parents2.ch <- vector("list",length = nodes.total.num)
nodes.to.children2.ch <- vector("list",length = nodes.total.num)
nodes.to.ancestors2.ch <- vector("list",length = nodes.total.num)
nodes.to.descendants2.ch <- vector("list",length = nodes.total.num)
nodes.to.index2.ch <- vector("list",length = nodes.total.num)
include.root.labels2 <- c("GO0008150",except.root.labels2)
names(nodes.to.index2.ch) <- include.root.labels2
names(nodes.to.parents2.ch) <- include.root.labels2
for (m in 1:length(include.root.labels2)) {
  
nodes.to.index2.ch[[include.root.labels2[m]]] <- m-1
  

nodes.to.parents2.ch[[include.root.labels2[1]]] <- NA
    
names(nodes.to.children2.ch) <- include.root.labels2
nodes.to.children2.ch[[include.root.labels2[1]]] <- which(except.root.labels2%in%go.for.level2[[1]])


names(nodes.to.ancestors2.ch) <- include.root.labels2
if (NA %in% nodes.to.ancestors2[[include.root.labels2[m]]])
{
  nodes.to.ancestors2.ch[[include.root.labels2[m]]] <- 0L
}
else
{
nodes.to.ancestors2.ch[[include.root.labels2[m]]] <- c(0L,nodes.to.ancestors2[[include.root.labels2[m]]])
}
nodes.to.ancestors2.ch[[include.root.labels2[1]]] <- NA

names(nodes.to.descendants2.ch) <- include.root.labels2
nodes.to.descendants2.ch[[include.root.labels2[1]]] <- 1:length(except.root.labels2)
for (k in 2:length(include.root.labels2)) {

nodes.to.parents2.ch[[include.root.labels2[k]]] <- nodes.to.parents2[[except.root.labels2[k-1]]]    
nodes.to.children2.ch[[include.root.labels2[k]]] <- nodes.to.children2[[except.root.labels2[k-1]]]  
nodes.to.descendants2.ch[[include.root.labels2[k]]] <- nodes.to.descendants2[[except.root.labels2[k-1]]]  

}

for (j in 1:length(go.for.level2[[1]])) {
  nodes.to.parents2.ch[[go.for.level2[[1]][j]]] <- 0L
}
 
    }


