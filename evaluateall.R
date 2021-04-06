#20201230结果评价
setwd("C:/Users/1231/Desktop/dataprocessing/library")
library(org.Sc.sgd.db)
library(hcgene)
library(RBGL)
#library(YEAST)
library(yeastCC)
library(RWeka)
library(R.matlab)
library(FSelector)
library(PerfMeas)
library(ROSE)
library(DMwR)
library(marray)
library(scales)
library(gpls)
library(plsgenomics)
library(keras)
source("annotation_extract.R")
source("annotation_match.R")
source("annotation_final.R")
source("read_data.R")
source("read_predict_arff.R") 
source("plotfigure.R")
source("write_annotation.R")
source("graph_level.R")
source("data_cleaning.R")
source("set_final_annotation.R")
source("get_leafnode.R")
source("get_leafnode_1.R")
source("divide_dataset.R")
source("original_dataset.R")
source("eachnode_dataset.R")
source("build_train_dataset.R")
source("build_train_NegGOA.R")
source("build_matlab_dataset.R")
source("get_matrixdata_change.R")
source("build_validset.R")
source("level_statistics.R")
source("node_select_by_level.R")
source("plot_label_graph.R")
source("downtop_parent.R")
source("downtop_weighted.R")
source("downtop_step.R")
source("topdown_step.R")
source("naive_downtop.R")#用于实现down top方式得出最终结果
source("make_index.R")
source("mh_evaluate.R")
source("precision_recall_calculate.R")
source("pr_auc_calculate.R")
source("prauc_calculate.R")
source("smote_samples.R")
source("add_neg_samples.R")
source("select_attributes.R")
source("write_arff.R")
source("validdata_scale.R")
source("traindata_scale.R")
source("violate_detectprob.R")
source("violate_detectlabel.R")
source("data_process.R")
source("dataset_rebuild.R")
source("dataset_select.R")
source("factor_to_num.R")
source("new_pathrule.R")#用于实现基于父子节点修改概率的后期处理方法
source("BN_compute.R")#用于实现基于贝叶斯网络修改概率的后期处理方法
source("BN_compute2.R")#用于实现基于贝叶斯网络修改概率的后期处理方法,先验概率设置为0.5
source("nn_get_dataset.R")#用于神经网络算法中生成整合父节点标签的数据集
source("get_ancestor_dataset.R")#用于神经网络算法中生成整合祖先节点标签的数据集
####第一步 将SVM的概率结果读入

#设置mat文件存储路径
library(R.matlab)
for (datasetindex in 1:5) {
  aa <- datasetindex
  setwd("C:/Users/1231/Desktop/dataprocessing")
  source("dataset_select2.R")
  source("dataset_divide.R")
  datasetresult = DatasetSelect(dataset.index = datasetindex)
  select.table <- datasetresult[[1]]
  select.data <- datasetresult[[2]]
  
  ###拆分训练集train:test=2:1
  selecttabletodivide <- DatasetDivide(dataset = select.table)
  train.select.table2 <- selecttabletodivide[[1]]
  valid.select.table2 <- selecttabletodivide[[2]]
  test.select.table2 <- selecttabletodivide[[3]]
  
  selectdatatodivide <- DatasetDivide(dataset=select.data)
  train.select.data2 <- selectdatatodivide[[1]]
  valid.select.data2 <- selectdatatodivide[[2]]
  test.select.data2 <- selectdatatodivide[[3]]
  
  datapath <- paste("204dataset",aa,sep = "")
  setwd(paste("C:/Users/1231/Desktop/dataprocessing/data/",datapath,sep = ""))
  mat.file=("newdataset_decision.mat")
  # mat.file2 <- ("decision.mat")
  probability.data2=readMat(mat.file,fixNames = FALSE)
  # probability.datahr <- readMat(mat.file2,fixNames = FALSE)
  #prob.for.genes  存储时，对每个节点来说，第一位是为label1的概率，而后是为0的概率
  prob.for.genes2=probability.data2$decision_test
  # prob.for.geneshr <- probability.datahr$decision
  
  ####第二步 进行TPR规则处理
  
  #用于检测结果是否符合TPR规则
  #violate.detect.result=ViolateDetectprob(go.for.level, go.leaf.nodes,nodes.to.index,nodes.to.children,prob.for.genes)
  #TPR 两步计算公式
  # downtop.w.prob=DownTopParent(go.for.level.3,go.leaf.nodes.3,nodes.to.index,nodes.to.children,prob.for.genes,each.go.weight)
  # topdown.w.prob=TopDownStep(go.for.level.3,go.leaf.nodes.3,nodes.to.index,nodes.to.children,downtop.w.prob)
  
  
  downtop.prob2=DownTopStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,prob.for.genes2)
  topdown.prob2=TopDownStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,downtop.prob2)
  # downtop.probhr=DownTopStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,prob.for.geneshr)
  # topdown.probhr=TopDownStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,downtop.probhr)
  #topdown.prob=NaiveDownTop(go.for.level,go.leaf.nodes,nodes.to.index,nodes.to.parents,downtop.prob)
  
  #violate.detect.down=ViolateDetectprob(go.for.level, go.leaf.nodes,nodes.to.index,nodes.to.children,downtop.prob)
  #violate.detect.top=ViolateDetectprob(go.for.level, go.leaf.nodes,nodes.to.index,nodes.to.children,topdown.prob)
  
  
  # valid.en=FALSE #用于对验证集进行结果评价
  # if(valid.en==TRUE)
  # {
  #   predict.labels=matrix(0,nrow(valid.select.table),ncol(valid.select.table))
  #   predict.scores=matrix(0,nrow(valid.select.table),ncol(valid.select.table))
  #   rownames(predict.labels)=rownames(valid.select.table)
  #   colnames(predict.labels)=colnames(valid.select.table)
  #   rownames(predict.scores)=rownames(valid.select.table)
  #   colnames(predict.scores)=colnames(valid.select.table)
  #   for(i in 1:nrow(topdown.prob))
  #   {
  #     for(j in 1:length(except.root.labels.3))
  #     {
  #       predict.scores[i,j]=topdown.prob[i,(2*j-1)]
  #       if(topdown.prob[i,(2*j-1)]>0.5)
  #       {
  #         predict.labels[i,except.root.labels.3[j]]=1
  #       }
  #     }
  #   }
  #   
  #   valid.label.index=nodes.to.index[colnames(valid.select.table)]
  #   measure.result=MHevaluate(predict.labels,valid.select.table)
  #   F.each.class=F.measure.single.over.classes(valid.select.table, predict.labels)
  #   prauc_result=PRAUCCalculate(predict.scores,valid.select.table)
  #   
  # }
  ####第三步 计算处理结果的评价指标
  
  #####TPR的结果#####
  test.en=TRUE
  if(test.en==TRUE)
  {
    predict.labels2=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
    predict.scores2=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
    rownames(predict.labels2)=rownames(test.select.table2)
    colnames(predict.labels2)=colnames(test.select.table2)
    rownames(predict.scores2)=rownames(test.select.table2)
    colnames(predict.scores2)=colnames(test.select.table2)
    for(i in 1:nrow(topdown.prob2))
    {
      for(j in 1:length(except.root.labels2))
      {
        predict.scores2[i,j]=topdown.prob2[i,(2*j-1)]
        if(topdown.prob2[i,(2*j-1)]>0.5)
        {
          predict.labels2[i,except.root.labels2[j]]=1
        }
      }
    }
    
    test.label.index2=nodes.to.index2[colnames(test.select.table2)]
    measure.result2=MHevaluate(predict.labels2,test.select.table2)
    F.each.class2=F.measure.single.over.classes(test.select.table2, predict.labels2)
    prauc_result2=PRAUCCalculate(predict.scores2,test.select.table2)
    
  }
  # #####HR-SVM结果#####
  # predict.labelshr=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  # predict.scoreshr=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  # rownames(predict.labelshr)=rownames(test.select.table2)
  # colnames(predict.labelshr)=colnames(test.select.table2)
  # rownames(predict.scoreshr)=rownames(test.select.table2)
  # colnames(predict.scoreshr)=colnames(test.select.table2)
  # for(i in 1:nrow(topdown.probhr))
  # {
  #   for(j in 1:length(except.root.labels2))
  #   {
  #     predict.scoreshr[i,j]=topdown.probhr[i,(2*j-1)]
  #     if(topdown.probhr[i,(2*j-1)]>0.5)
  #     {
  #       predict.labelshr[i,except.root.labels2[j]]=1
  #     }
  #   }
  # }
  
  # test.label.indexhr=nodes.to.index2[colnames(test.select.table2)]
  # measure.resulthr=MHevaluate(predict.labelshr,test.select.table2)
  # F.each.classhr=F.measure.single.over.classes(test.select.table2, predict.labelshr)
  # prauc_resulthr=PRAUCCalculate(predict.scoreshr,test.select.table2)
  
  
  #####downtop结果#####
  predict.labelsdt=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  predict.scoresdt=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  rownames(predict.labelsdt)=rownames(test.select.table2)
  colnames(predict.labelsdt)=colnames(test.select.table2)
  rownames(predict.scoresdt)=rownames(test.select.table2)
  colnames(predict.scoresdt)=colnames(test.select.table2)
  for(i in 1:nrow(downtop.prob2))
  {
    for(j in 1:length(except.root.labels2))
    {
      predict.scoresdt[i,j]=downtop.prob2[i,(2*j-1)]
      if(downtop.prob2[i,(2*j-1)]>0.5)
      {
        predict.labelsdt[i,except.root.labels2[j]]=1
      }
    }
  }
  
  test.label.indexdt=nodes.to.index2[colnames(test.select.table2)]
  measure.resultdt=MHevaluate(predict.labelsdt,test.select.table2)
  F.each.classdt=F.measure.single.over.classes(test.select.table2, predict.labelsdt)
  prauc_resultdt=PRAUCCalculate(predict.scoresdt,test.select.table2)
  
  #####topdown结果#####
  topdown.probtd=TopDownStep(go.for.level2,go.leaf.nodes2,nodes.to.index2,nodes.to.children2,prob.for.genes2)
  
  predict.labelstd=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  predict.scorestd=matrix(0,nrow(test.select.table2),ncol(test.select.table2))
  rownames(predict.labelstd)=rownames(test.select.table2)
  colnames(predict.labelstd)=colnames(test.select.table2)
  rownames(predict.scorestd)=rownames(test.select.table2)
  colnames(predict.scorestd)=colnames(test.select.table2)
  for(i in 1:nrow(topdown.probtd))
  {
    for(j in 1:length(except.root.labels2))
    {
      predict.scorestd[i,j]=topdown.probtd[i,(2*j-1)]
      if(topdown.probtd[i,(2*j-1)]>0.5)
      {
        predict.labelstd[i,except.root.labels2[j]]=1
      }
    }
  }
  
  test.label.indextd=nodes.to.index2[colnames(test.select.table2)]
  measure.resulttd=MHevaluate(predict.labelstd,test.select.table2)
  F.each.classtd=F.measure.single.over.classes(test.select.table2, predict.labelstd)
  prauc_resulttd=PRAUCCalculate(predict.scorestd,test.select.table2)
  
  #使用newpathrule方法处理的结果
  final.result2=NewPathrule(prob.for.genes2,except.root.labels2,go.for.level2,go.leaf.nodes2,test.select.table2)
  final.predict.labels2=final.result2[[1]]
  final.predict.scores2=final.result2[[2]]
  aa2=Pr_Auc_Calculate(final.predict.scores2,test.select.table2,plot.en=TRUE)
  prauc_result.final2=PRAUCCalculate(final.predict.scores2,test.select.table2)
  measure.result.final2=MHevaluate(final.predict.labels2,test.select.table2)
  
  
  
  #使用BN方法处理的结果
  bn.result2=BNcompute(prob.for.genes2,except.root.labels2,go.for.level2,go.leaf.nodes2,test.select.table2)
  bn.predict.labels2=bn.result2[[3]]
  bn.predict.scores2=bn.result2[[4]]
  prauc_result.bn=PRAUCCalculate(bn.predict.scores2,test.select.table2)
  measure.result.bn=MHevaluate(bn.predict.labels2,test.select.table2)
  
  #使用BN2方法处理的结果
  bn2.result2=BNcompute2(prob.for.genes2,except.root.labels2,go.for.level2,go.leaf.nodes2,test.select.table2)
  bn2.predict.labels2=bn2.result2[[1]]
  bn2.predict.scores2=bn2.result2[[2]]
  prauc_result.bn2=PRAUCCalculate(bn2.predict.scores2,test.select.table2)
  measure.result.bn2=MHevaluate(bn2.predict.labels2,test.select.table2)
  
  
  # if(test.en==TRUE)#此部分代码用于实现权值TPR算法
  # {
  #   predict.w.labels=matrix(0,nrow(test.select.table),ncol(test.select.table))
  #   predict.w.scores=matrix(0,nrow(test.select.table),ncol(test.select.table))
  #   rownames(predict.w.labels)=rownames(test.select.table)
  #   colnames(predict.w.labels)=colnames(test.select.table)
  #   rownames(predict.w.scores)=rownames(test.select.table)
  #   colnames(predict.w.scores)=colnames(test.select.table)
  #   for(i in 1:nrow(topdown.w.prob))
  #   {
  #     for(j in 1:length(except.root.labels.3))
  #     {
  #       predict.w.scores[i,j]=topdown.w.prob[i,(2*j-1)]
  #       if(topdown.w.prob[i,(2*j-1)]>0.5)
  #       {
  #         predict.w.labels[i,except.root.labels.3[j]]=1
  #       }
  #     }
  #   }
  #   
  #   test.label.index.w=nodes.to.index[colnames(test.select.table)]
  #   measure.result.w=MHevaluate(predict.w.labels,test.select.table)
  #   F.each.class.w=F.measure.single.over.classes(test.select.table, predict.w.labels)
  #   prauc_result.w=PRAUCCalculate(predict.w.scores,test.select.table)
  #   
  # }
  
  ####第四步 用于将输出结果进行存储
  data.path <- "C:/Users/1231/Desktop/dataprocessing/data"
  result.output.en=TRUE
  result.savepath=paste(data.path,"/204result",sep = "")
  setwd(result.savepath)
  today <-Sys.Date()
  run.num=1
  output.fname=paste("newdataset_result",aa,".txt",sep = "")
  fname=paste("TPR",aa,".txt",sep = "")
  # fhrname = paste("HR-SVM",aa,".txt",sep = "")
  f1name = paste("topdown",aa,".txt",sep = "")
  f2name = paste("downtop",aa,".txt",sep = "")
  if(result.output.en==TRUE)
  {
    
    write.table(today,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
    write.table(measure.result2,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    #write.table(measure.result.w,file=output.fname,sep = " ,",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    write.table(prauc_result2,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    #write.table(prauc_result.w,file=output.fname,sep = " , ",eol="\n",quote=FALSE,row.names = FALSE,col.names = TRUE,append = TRUE)
    write.table(measure.result2,file=fname,sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
    # write.table(measure.resulthr,file=fhrname,sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
    write.table(measure.resulttd,file=f1name,sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
    write.table(measure.resultdt,file=f2name,sep = ",",eol="\n",quote=FALSE,row.names = FALSE,col.names = FALSE,append = FALSE)
  }
  
}






