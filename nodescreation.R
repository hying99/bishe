#20201216建立新数据集的GO变量
#去掉listexrootClassesLevels的第一个元素
listexrootClassesPaths <- vector("list")
#构造eachnodeslevels，一去掉list里的第一个元素（节点自身），二把路径（一长串string“a/b/c/d”拆成有顺序的多个元素”a””b””c””d”
eachnodeslevels <- vector("list",length = length(listexrootClassesPaths))
for (t in 1:length(listexrootClassesLevels)) {
  listexrootClassesPaths[[t]] <- listexrootClassesLevels[[t]][-1]
  eachnodeslevels[[t]] <- strsplit(listexrootClassesPaths[[t]],"/")
  
}
eachnodeslevels2 <- eachnodeslevels

levelClasses <- vector("numeric",length(listexrootClassesLevels))

for(j in 1:length(levelClasses)){
  numEdges <- vector()
  allPaths <- listexrootClassesLevels[[j]][2:length(listexrootClassesLevels[[j]])]
  for(k in 1:length(allPaths)){
    numEdges <- c(numEdges,length(unlist(strsplit(allPaths[k],"/"))))
  }
  levelClasses[j] <- max(numEdges)
}

#cat(levelClasses,"\n")

#Number of levels
numLevels <- max(unique(levelClasses))
namesClasses <- vector("list",numLevels)
for(i in 2:numLevels){
  regExpClass <- paste("^",i,"$",sep="")
  posClassesLevel <- grep(regExpClass,levelClasses)
  for(j in 1:length(posClassesLevel)){
    namesClasses[[i]] <- c(namesClasses[[i]],listexrootClassesLevels[[posClassesLevel[j]]][1])
  }
}	
go.for.level2 <- namesClasses
go.for.level2[[1]] <- NULL


#建立索引nodes.to.index2

nodes.to.index2 <- as.list(1:length(except.root.labels2))
names(nodes.to.index2) <- except.root.labels2



#搜索子节点,建立nodes.to.children,nodes.to.parents,
children.nodes2 <- vector("list")
parents.nodes2 <- vector("list")
index2 <- vector("list")
children.index2 <- vector("list")
nextindex2 <- vector("list")

#names(index) <- except.root.labels2
nodes.to.children2 <- vector("list")
nodes.to.parents2 <- vector("list")
nodes.to.ancestors2 <- vector("list")
nodes.to.descendants2 <- vector("list",length = length(except.root.labels2))
#names(index) <- except.root.labels2
go.leaf.nodes2 <- vector()
for (i in 1:length(except.root.labels2)) 
{
  cat(i,"\n")
  #搜索class[i]在全部listexrootclassesPaths的位置索引
  parentclasses2 <- except.root.labels2[i]
  #index2是except.root.labels2[i]在listexrootClassesPaths出现的位置
  index2[[parentclasses2]] <- grep(parentclasses2,listexrootClassesPaths)
  #class[i]不只出现一次，index2[i]里有多个元素
  for (k in 1:length(index2[[i]])) {
    #搜索在含某class的listexrootclassesPaths的内部位置
    aa <- vector()
    
    for (j in 1:length(eachnodeslevels[[index2[[i]][k]]])) {
      #某个节点的paths数量
      #cat(j,"\n")
      bb <- vector()
      
      if(sum(grepl(parentclasses2,eachnodeslevels[[index2[[i]][k]]][[j]])) != 0)
      {
      #bb是except.root.labels2[i]在具体某个节点的j个paths里面出现的位置
      bb <- grep(parentclasses2,eachnodeslevels[[index2[[i]][k]]][[j]])
      #在eachnodeslevels里找子节点父节点
      children.nodes2[[parentclasses2]] <- c(children.nodes2[[parentclasses2]],eachnodeslevels[[index2[[i]][k]]][[j]][bb+1])
      parents.nodes2[[parentclasses2]] <- c(parents.nodes2[[parentclasses2]],eachnodeslevels[[index2[[i]][k]]][[j]][bb-1])
      aa <- c(aa,bb)
       }
    }
    
    nextindex2[[k]] <- list(aa)
    #children.index2[[i]]的数量是节点总共在路径中出现的次数，children.index[[i]]里有几个list对应着index2[[i]]里元素个数，这个节点总共出现几次
    #index2[[i]]里是大路径的索引k，索引1对应children.index2[[i]]里的第一个list，list里的元素就是在这个大路径k下的多个小路径，这个class所在的位置，但NA是缺失的
    children.index2[[except.root.labels2[i]]] <- c(children.index2[[except.root.labels2[i]]],nextindex2[[k]])
    
    }
  children.nodes2[[i]] <- unique(children.nodes2[[i]])
  parents.nodes2[[i]] <- unique(parents.nodes2[[i]])
  nodes.to.children2[[parentclasses2]] <- grep(paste(children.nodes2[[i]],collapse = "|"),except.root.labels2)
  nodes.to.parents2[[parentclasses2]] <- grep(paste(parents.nodes2[[i]],collapse = "|"),except.root.labels2)
  children.nodes2[[i]] <- na.omit(children.nodes2[[i]])
  #查找叶子节点
  if (length(nodes.to.children2[[except.root.labels2[i]]]) == 0)
  {
    go.leaf.nodes2 <- c(go.leaf.nodes2,names(nodes.to.children2[except.root.labels2[i]]))
  }
  
  nodes.to.ancestors2[[except.root.labels2[i]]] <- grep(paste(exrootClassesPaths[[i]],collapse = "|"),except.root.labels2)
  ipos <- paste("^",i,"$",sep="")
  nodes.to.ancestors2[[except.root.labels2[i]]] <- nodes.to.ancestors2[[except.root.labels2[i]]][-grep(ipos,nodes.to.ancestors2[[except.root.labels2[i]]])]
  #建立nodes.to.descendants2
  # nodes.to.descendants2[[except.root.labels2[i]]] <- nodes.to.children2[[except.root.labels2[i]]]
  # 
  # if(length(nodes.to.children2[[except.root.labels2[i]]]) > 0)
  # {
  # for (m in 1:length(nodes.to.children2[[except.root.labels2[i]]])) {
  #   nodes.to.descendants2[[except.root.labels2[i]]] <- c(nodes.to.descendants2[[except.root.labels2[i]]], nodes.to.children2[[except.root.labels2[nodes.to.children2[[except.root.labels2[i]]][m]]]])
  # }
  # }
  # nodes.to.descendants2[[except.root.labels2[i]]] <- unique(nodes.to.descendants2[[except.root.labels2[i]]])
  
}
#20210131改，遍历子孙节点
names(nodes.to.descendants2) <- except.root.labels2
 for (m in 1:length(except.root.labels2)) {
   for (n in 1:length(except.root.labels2)) {
     if ( m %in% nodes.to.ancestors2[[except.root.labels2[n]]] == TRUE)
       nodes.to.descendants2[[except.root.labels2[m]]] <- c(nodes.to.descendants2[[except.root.labels2[m]]],n) 
   }
   if (length(nodes.to.descendants2[[except.root.labels2[m]]]) == 0)
     nodes.to.descendants2[[except.root.labels2[m]]] <- NA
   if (length(nodes.to.children2[[except.root.labels2[m]]]) == 0)
     nodes.to.children2[[except.root.labels2[m]]] <- NA
   if (length(nodes.to.ancestors2[[except.root.labels2[m]]]) == 0)
     nodes.to.ancestors2[[except.root.labels2[m]]] <- NA
   if (length(nodes.to.parents2[[except.root.labels2[m]]]) == 0)
     nodes.to.parents2[[except.root.labels2[m]]] <- NA
 }
 

