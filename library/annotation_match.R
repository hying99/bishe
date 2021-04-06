AnnotationMatch<-function (gene.vec,annotation_list,Nametoupper=FALSE,writefile=FALSE)
{
  if(Nametoupper==TRUE)
  {
    gene.names=toupper(gene.vec)
  }
  else
  {
    gene.names=gene.vec
  }
  h=annotation_list[gene.names]
  match_label=0
  
  for(i in 1:length(h))
  {
    if(!is.null(h[[i]]))
    {
      if(match_label==0)
      {
        match_index=i#标识哪些序号带有所选择的内容，且不为NULL
      }
      if(match_label==1)
      {
        #annotation_final=c(annotation_final,annotation_list[i])
        match_index=c(match_index,i)
      }
      match_label=1
    }
  }
  
  match_final=h[match_index]#生成最终的匹配列
  if(writefile==TRUE)
  {
    Write.gene.classes.associations(match_final, "list_ann")#将匹配信息写入list_ann文件中
  }
  match_result=list(match_final,match_index)
}