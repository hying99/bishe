####用于实现AU(/PRC) (/AUPRC)及 (/AUPRCw)的函数
####关于AU(/PRC)的求解 目前有两个函数可以实现 第一个是PrecisionRecallCalculate 另一个是Pr_Auc_Calculate
####这里选用Pr_Auc_Calculate进行实现
####20171129
PRAUCCalculate <- function(predict,target)
{
  #求取AU（PRC）的值
  #pr.result=PrecisionRecallCalculate(predict,target)
  #precision.recall.curves.plot(list(pr.result),plot.precision=TRUE)
  #AU.prc=AUPRC(list(pr.result), comp.precision=TRUE)[[1]]
  AU.prc=Pr_Auc_Calculate(predict,target,plot.en=TRUE)
  #求取各类AUPRC的平均值和加权值
  label.num=ncol(target)
  each.class.auc=rep(0,label.num)
  each.class.freq=rep(0,label.num)
  each.class.weight=rep(0,label.num)
  #求取average.auprc值
  for(i in 1:label.num)
  {
    single.predict.scores=predict[,i]
    single.target.label=target[,i]
    #single.pr.result=PrecisionRecallCalculate(single.predict.scores,single.target.label)
    #调用函数库中的函数求取各类的pr曲线下面积
    single.pr.result=precision.at.all.recall.levels(single.predict.scores, single.target.label)
    each.class.auc[i]=AUPRC(list(single.pr.result), comp.precision=TRUE)
    each.class.freq[i]=sum(single.target.label)
  }
  average.auprc=sum(each.class.auc)/label.num
  
  average.auprc.w=0
  for(i in 1:label.num)
  {
    each.class.weight[i]=each.class.freq[i]/sum(each.class.freq)
    average.auprc.w=average.auprc.w+each.class.auc[i]*each.class.weight[i]
  }
  
  result=list(AU.prc=AU.prc,average.auprc=average.auprc,average.auprc.w=average.auprc.w,each.class.auc=each.class.auc)
  return(result)
}


