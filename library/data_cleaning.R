#用于选择每个基因符合样本数量要求的标签
#go.label.list，输入的基因列表，每行的名称为一个基因名，每行的内容是该基因所具有的所有功能标签
#table.gene.class，各基因与所有的功能所组成的table，每一行为基因名，每一列为一个功能标签
DataCleaning<-function (go.label.list,table.gene.class,select.num=50)
{
#   class.example.count=Count.examples.per.class(table.gene.class)
#   each.class.eample.num=class.example.count[[1]]#得到每个GO 标签下具有样本的数量向量
#   final.class.vector=each.class.eample.num[each.class.eample.num>select.num-1]#得到符合样本数量要求的向量
#   final.class.names=names(final.class.vector)#得到最终的GO标签向量
  final.class.names <- Select.functional.classes.by.cardinality(table.gene.class, select.num)
  for (i in 1:length(go.label.list))
  {
    go.label.list[[i]]=intersect(go.label.list[[i]],final.class.names)#得到每个基因所具有的GO标签，删除含有样本数量较少的标签
  }
  return(go.label.list)#得到删减后的基因类标签列表
}
