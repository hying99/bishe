DataProcess<-function (file.prefix, factor.col, work.path,data.path,file.savepath,delete.outlier,
                              replace.outlier, NAreplace,Zrescale, write.enable,
                       result.process,matfile.path,file.type, file.middle,result.savepath)

{
  setwd(data.path)
  matrix.cellcycle=ReadData(paste("originaldata//",file.prefix,"0.train",sep = ""),factor.col = factor.col)
  setwd(work.path)
  matrix.cellcycle.data=matrix.cellcycle[[1]]#基因的数据信息
  
  training.cellcycle.data=matrix.cellcycle.data
  #training.cellcycle.name=rownames(training.cellcycle.data)#基因名称列表
  #对输入数据剔除异常值，并进行归一化处理
  trainscale.result=TraindataScale(training.cellcycle.data,factor.col,delete.outlier=delete.outlier,replace.outlier = replace.outlier,NAreplace=NAreplace,Zrescale=Zrescale)
  remain.data=trainscale.result[[1]]
  sp=trainscale.result[[2]]
  
  #得到几个数据集共有的基因的名称列表
  common.genes <- Get.all.common.genes(go.general.table.BP, remain.data)
  #得到common genes中每个基因对应的全部GO标签列表
  match.go.general=go.general.list.BP[common.genes]
  
  #将标签列表转换为TABLE形式，行名称为基因名称，列名称为GO标签
  match.go.table=Build.GO.class.labels(match.go.general)
  
  #得到目前的所有基因共有多少个不重复的GO标签
  all.go.labels=Get.classes(match.go.general)
  
  #得到DAG图中包含50个样本以上的节点列表及对应的基因名称
  go.label.list=DataCleaning(match.go.general,match.go.table,select.num = 100)
  all.go.labels.50=Get.classes(go.label.list)
  #except.root.labels=setdiff(all.go.labels.50,"GO:0008150")#除去根结点后所剩节点
  
  #得到修改后的DAG图中各节点的层级
  graph.BP.general.50 <- subGraph(all.go.labels.50, BP.univ.graph)
  graph.BP.level.50=GraphLevel(graph.BP.general.50)
  go.level.statistics=LevelStatistics(graph.BP.level.50)
  go.for.each.level=go.level.statistics[[1]]
  each.level.nodes.num=go.level.statistics[[2]]
  total.levels=length(go.for.each.level)
  
  
  select.node.3=NodeSelectByLevel(go.level.statistics,total.levels,add.root.node = TRUE)#选择根结点至第三层的所有go节点
  except.root.labels.3=setdiff(select.node.3,"GO:0008150")
  graph.select.node.3 <- subGraph(select.node.3, BP.univ.graph)
  PlotLabelGraph(except.root.labels.3,BP.univ.graph,num.only=FALSE,plot.en = TRUE,output.en = FALSE,write.pic.name = "go.graph.level.ps")
  
  each.go.level.num=graph.BP.level.50[except.root.labels.3]
  each.go.weight=unname(each.go.level.num)
  
  for (i in 1:length(each.go.level.num))
  {
    each.go.weight[i]=(total.levels+1-each.go.weight[i])/(total.levels+1)
  }
  
  #go.leaf.nodes.3=GetLeafNode1(graph.select.node.3)
  
  for (i in 1:length(go.label.list))
  {
    go.label.list[[i]]=intersect(go.label.list[[i]],select.node.3)
  }
  
  root.table.3=Build.GO.class.labels(go.label.list)
  
  
  
  if(write.enable==TRUE)
  {
    cat("build training dataset")
    setwd(file.savepath)
    select.attributes.en=FALSE
    data.total=BuildTrainDataset(root.table.3, except.root.labels.3, data.matrix=remain.data,
                                 ontology = "BP", adjust.ratio=0.2,ratio.negative = 0, common.genes = common.genes,
                                 seed = 1,select.attributes.en=select.attributes.en,write.en=write.enable)
    
  }
  
  
  
  setwd(data.path)
  original.valid.file=paste("originaldata//",file.prefix,"0.valid",sep = "")
  select.attributes.en=FALSE
  cat("process valid dataset")
  
  write.data.fname=paste(file.prefix,"0_validdataset.csv",sep = "")
  write.class.fname=paste(file.prefix,"0_validclass.csv",sep = "")
  valid.cellcycle=ReadData(original.valid.file,factor.col = factor.col)#读入valid基因特征属性
  valid.cellcycle.data=valid.cellcycle[[1]]#valid基因的数据信息
  valid.scaled.data=ValiddataScale(valid.cellcycle.data,factor.col,sp,replace.outlier=FALSE,NAreplace=TRUE,Zrescale=TRUE)
  setwd(file.savepath)
  valid.data.total=BuildValidset(valid.scaled.data,go.general.table.BP,go.general.list.BP,except.root.labels.3,
                                 write.data.enable=write.enable,write.class.enable=write.enable,write.data.fname=write.data.fname,
                                 write.class.fname=write.class.fname,select.attributes.en=select.attributes.en,select.attributes)
  valid.select.data=valid.data.total[[1]]
  valid.select.table=valid.data.total[[2]]
  
  
  setwd(data.path)
  cat("process test dataset")
  #original.test.file="originaldata//cellcycle0.propertest"
  original.test.file=paste("originaldata//",file.prefix,"0.propertest",sep = "")
  select.attributes.en=FALSE
  
  write.data.fname=paste(file.prefix,"0_testdataset.csv",sep = "")
  write.class.fname=paste(file.prefix,"0_testclass.csv",sep = "")
  test.cellcycle=ReadData(original.test.file,factor.col = factor.col)#读入test基因特征属性
  test.cellcycle.data=test.cellcycle[[1]]#test基因的数据信息
  setwd(file.savepath)
  test.scaled.data=ValiddataScale(test.cellcycle.data,factor.col,sp,replace.outlier=FALSE,NAreplace=TRUE,Zrescale=TRUE)
  test.data.total=BuildValidset(test.scaled.data,go.general.table.BP,go.general.list.BP,except.root.labels.3,
                                write.data.enable=write.enable,write.class.enable=write.enable,write.data.fname=write.data.fname,
                                write.class.fname=write.class.fname,select.attributes.en=select.attributes.en,select.attributes)
  test.select.data=test.data.total[[1]]
  test.select.table=test.data.total[[2]]
  
  
  
  if(result.process==TRUE)
  {
    cat("process final result")
    setwd(matfile.path)#设置mat文件存储路径
    mat.file=paste(file.prefix,file.middle,file.type,"_decision.mat",sep = "")
    probability.data=readMat(mat.file,fixNames = FALSE)
    prob.for.genes=probability.data$decision
    setwd(work.path)#设置工作路径
    
    go.for.level.3=go.for.each.level[1:total.levels]#选取前三层节点进行处理
    go.leaf.nodes.3=GetLeafNode1(graph.select.node.3)
    #用于生产各节点的编号，以及节点与子节点的编号映射列表
    total.index=MakeIndex(except.root.labels.3)
    nodes.to.index=total.index[[1]]
    nodes.to.children=total.index[[2]]
    
    #TPR 两步计算公式
    downtop.w.prob=DownTopParent(go.for.level.3,go.leaf.nodes.3,nodes.to.index,nodes.to.children,prob.for.genes,each.go.weight)
    topdown.w.prob=TopDownStep(go.for.level.3,go.leaf.nodes.3,nodes.to.index,nodes.to.children,downtop.w.prob)
    
    downtop.prob=DownTopStep(go.for.level.3,go.leaf.nodes.3,nodes.to.index,nodes.to.children,prob.for.genes)
    topdown.prob=TopDownStep(go.for.level.3,go.leaf.nodes.3,nodes.to.index,nodes.to.children,downtop.prob)
    
    
    predict.labels=matrix(0,nrow(test.select.table),ncol(test.select.table))
    predict.scores=matrix(0,nrow(test.select.table),ncol(test.select.table))
    rownames(predict.labels)=rownames(test.select.table)
    colnames(predict.labels)=colnames(test.select.table)
    rownames(predict.scores)=rownames(test.select.table)
    colnames(predict.scores)=colnames(test.select.table)
    for(i in 1:nrow(topdown.prob))
    {
       for(j in 1:length(except.root.labels.3))
        {
          predict.scores[i,j]=topdown.prob[i,(2*j-1)]
          if(topdown.prob[i,(2*j-1)]>0.5)
          {
            predict.labels[i,except.root.labels.3[j]]=1
          }
        }
      }
      
    test.label.index=nodes.to.index[colnames(test.select.table)]
    measure.result=MHevaluate(predict.labels,test.select.table)
    F.each.class=F.measure.single.over.classes(test.select.table, predict.labels)
    prauc_result=PRAUCCalculate(predict.scores,test.select.table)
    
    
    predict.w.labels=matrix(0,nrow(test.select.table),ncol(test.select.table))
    predict.w.scores=matrix(0,nrow(test.select.table),ncol(test.select.table))
    rownames(predict.w.labels)=rownames(test.select.table)
    colnames(predict.w.labels)=colnames(test.select.table)
    rownames(predict.w.scores)=rownames(test.select.table)
    colnames(predict.w.scores)=colnames(test.select.table)
      for(i in 1:nrow(topdown.w.prob))
      {
        for(j in 1:length(except.root.labels.3))
        {
          predict.w.scores[i,j]=topdown.w.prob[i,(2*j-1)]
          if(topdown.w.prob[i,(2*j-1)]>0.5)
          {
            predict.w.labels[i,except.root.labels.3[j]]=1
          }
        }
      }
      
    test.label.index.w=nodes.to.index[colnames(test.select.table)]
    measure.result.w=MHevaluate(predict.w.labels,test.select.table)
    F.each.class.w=F.measure.single.over.classes(test.select.table, predict.w.labels)
    prauc_result.w=PRAUCCalculate(predict.w.scores,test.select.table)
    
    
    setwd(result.savepath)
    today <-Sys.Date()
    output.fname=paste(file.prefix,file.middle,file.type,"_result",".txt",sep = "")
    
      
    write.table(today,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
    write.table(measure.result,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    write.table(measure.result.w,file=output.fname,sep = " ,",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    write.table(prauc_result,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    write.table(prauc_result.w,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    
    final.result=list(remain.data,test.select.data,test.select.table,downtop.prob,topdown.prob)
  } else
  {
    final.result=list(remain.data,test.select.data,test.select.table)
  }
  
  return(final.result)
}



