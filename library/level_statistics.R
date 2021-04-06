#统计有向无环图层级信息，返回每层所具有的GO标签名称及数量
LevelStatistics<-function (graph.level)
{
  max.level=max(graph.level)
  level.index=order(graph.level,decreasing=TRUE)
  go.level.upward=graph.level[level.index]
  go.for.each.level=list()
  each.level.nodes.num=list()
  for(i in 1:max.level)
  {
    go.for.each.level[[i]]=names(go.level.upward[which(go.level.upward==i)])
    each.level.nodes.num[[i]]=length(go.for.each.level[[i]])
  }
  names(go.for.each.level)=c(1:max.level)
  names(each.level.nodes.num)=c(1:max.level)
  
  go.level.statistics=list(go.for.each.level,each.level.nodes.num)
  names(go.level.statistics)=c("go labels in each level","nodes num in each level")
  return (go.level.statistics)
}
