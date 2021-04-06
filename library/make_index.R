#用于生成个GO标签的索引序列号，以及每个节点与其父节点、子节点、祖先节点、子孙节点的序列号对应关系
#输入为待测节点的集合 20170310
MakeIndex<-function (input.labels,include.root=FALSE)
{
  #生成每个节点对应的序号
  nodes.to.index=list()
  if(include.root==TRUE)
  {
    for(i in 1:length(input.labels))
    {
      nodes.to.index[i]=i-1
    }
  }else
  {
    for(i in 1:length(input.labels))
    {
      nodes.to.index[i]=i
    }
  }
  
 
  
  names(nodes.to.index)=input.labels
  
  #生成每个节点的子节点列表，各节点的子节点集合均用序号表示
  nodes.to.children=list()
  #生成每个节点的祖先节点列表，各节点的祖先节点集合均用序号表示
  nodes.to.ancestors=list()
  #生成每个节点的父节点列表，各节点的父节点列表均用序号表示
  nodes.to.parents=list()
  #生成每个节点的子孙节点列表，各节点的父节点列表均用序号表示
  nodes.to.descendants=list()
  
  
  for(i in 1:length(input.labels))
  {
    #生成每个节点的子节点列表
    child.inter.ids=AnnotationDbi::get(input.labels[[i]], GOBPCHILDREN)
    child.ids=intersect(input.labels,child.inter.ids)
    if(length(child.ids)>0)
    {
      inter.child.vec=vector()
      for(j in 1:length(child.ids))
      {
        inter.child.vec=c(inter.child.vec,nodes.to.index[[child.ids[j]]])
      }
      nodes.to.children[[i]]=inter.child.vec
    }    else
    {
      nodes.to.children[[i]]=NA
    }
    #生成每个节点的祖先节点列表
    ancestor.inter.ids=AnnotationDbi::get(input.labels[i], GOBPANCESTOR)
    ancestor.ids=intersect(input.labels,ancestor.inter.ids)
    if(length(ancestor.ids)>0)
    {
      inter.ancestor.vec=vector()
      for(j in 1:length(ancestor.ids))
      {
        inter.ancestor.vec=c(inter.ancestor.vec,nodes.to.index[[ancestor.ids[j]]])
      }
      nodes.to.ancestors[[i]]=inter.ancestor.vec
    }    else
    {
      nodes.to.ancestors[[i]]=NA
    }
    
    #生成每个节点的父节点列表
    parent.inter.ids=AnnotationDbi::get(input.labels[i], GOBPPARENTS)
    parent.ids=intersect(input.labels,parent.inter.ids)
    if(length(parent.ids)>0)
    {
      inter.parent.vec=vector()
      for(j in 1:length(parent.ids))
      {
        inter.parent.vec=c(inter.parent.vec,nodes.to.index[[parent.ids[j]]])
      }
      nodes.to.parents[[i]]=inter.parent.vec
    }    else
    {
      nodes.to.parents[[i]]=NA
    }
    
    #生成每个节点的子孙节点列表
    des.inter.ids=AnnotationDbi::get(input.labels[i], GOBPOFFSPRING)
    descendant.ids=intersect(input.labels,des.inter.ids)
    if(length(descendant.ids)>0)
    {
      inter.descendant.vec=vector()
      for(j in 1:length(descendant.ids))
      {
        inter.descendant.vec=c(inter.descendant.vec,nodes.to.index[[descendant.ids[j]]])
      }
      nodes.to.descendants[[i]]=inter.descendant.vec
    }    else
    {
      nodes.to.descendants[[i]]=NA
    }
  }
  #用节点名称给list中各元素命名
  names(nodes.to.children)=input.labels
  names(nodes.to.ancestors)=input.labels
  names(nodes.to.parents)=input.labels
  names(nodes.to.descendants)=input.labels
  
  total.index=list(nodes.to.index,nodes.to.children,nodes.to.ancestors,nodes.to.parents,nodes.to.descendants)
  return (total.index)
  
}




