#按照层级选择部分节点，从第一层开始选择，直至指定的层次
NodeSelectByLevel<-function (go.level.statistics,select.to.level=1,add.root.node=TRUE)
{
  go.for.each.level=go.level.statistics[[1]]
  each.level.nodes.num=go.level.statistics[[2]]
  max.level=length(go.for.each.level)
  select.nodes=vector()
  if(select.to.level>max.level)
  {
    stop("the level you choose is larger than the max level")
  }
  else
  {
    for(i in 1:select.to.level)
    {
      select.nodes=c(select.nodes,go.for.each.level[[i]])
    }
  }
  if(add.root.node==TRUE)
  {
    select.nodes=c("GO:0008150",select.nodes)
  }
  
  
  return (select.nodes)
}