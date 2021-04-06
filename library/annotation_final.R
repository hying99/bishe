#用于根据ontology以及evidence code提取基因列表及其对应的GO注释标签
AnnotationFinal<-function (ONTOLOGY="BP")
{
  x <- org.Sc.sgdGO
  gene_names <- mappedkeys(x)
  xx <- as.list(x[gene_names])
  
  #生成与xx列表等长的annotation_list列表用于存放以后提取的数据，并对此list用基因名称命名
  annotation_list=list(NULL)
  length(annotation_list)<-length(xx)
  names(annotation_list)<-gene_names
  
  #制定需要提取的证据代码
  evidence_code=c("EXP","IPI","IMP","IDA","IGI","IEP","TAS","IC")
  #evidence_code=c("TAS")
  
  #调用提取函数，提取相关信息
  final=AnnotationExtract(xx,evidence_code,ontology=ONTOLOGY,annotation_list)
  
  #函数的输出为一个list，包含三个值，第三个值为最终的输出文件，该文件只包含带有GO标签的基因
  annotation_final=final[[3]]
}