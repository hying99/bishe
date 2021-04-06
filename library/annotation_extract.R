#根据evidence code 从org.sc.sgd.db文件中提取GO信息的函数
AnnotationExtract<-function (xx,evidence_code,ontology,annotation_list)
{
  find_label=0#第一次找到满足条件的信息 的标识
  for(i in 1:length(xx))
  {
    for(j in 1:length(xx[[i]]))
    {
    
      if(xx[[i]][[j]][["Ontology"]]==ontology)
      {
      
        if(xx[[i]][[j]][["Evidence"]] %in% evidence_code)
        {
         annotation_list[[i]]=c(annotation_list[[i]],(xx[[i]][[j]][["GOID"]]))
        }
      }
    }
    if(length(annotation_list[[i]])>0)#输出包含GO标签的信息，结果为NULL的条目不输出
    {
    
      annotation_list[[i]]=annotation_list[[i]][!duplicated(annotation_list[[i]])]# 删除重复的GO标签
      if(find_label==0)
      {
        #annotation_final=annotation_list[i]
        index_final=i#标识哪些序号带有所选择的内容，且不为NULL
        
      }
      if(find_label==1)
      {
        #annotation_final=c(annotation_final,annotation_list[i])
        index_final=c(index_final,i)
      }
      find_label=1
    }
}

annotation_final=annotation_list[index_final]
#以list形式输出全匹配，匹配序号，最终匹配信息三个内容
final_result=list(annotation_list,index_final,annotation_final)
}