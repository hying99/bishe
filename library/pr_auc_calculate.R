####编写的用于实现AU(/PRC)的函数
#20171128
Pr_Auc_Calculate <- function(predict,target,plot.en=FALSE)
{
  #判断输入的数据是什么类型，如果是data.frame,则将其转化为向量
  #建议输入的类型是矩阵matrix
  if(is.data.frame(predict))
  {
    predict=as.matrix(predict)
  }
  if(is.data.frame(target))
  {
    target=as.matrix(target)
  }
  
  sample.num=nrow(predict)
  class.num=ncol(predict)
  if (sample.num !=nrow(target))
  {
    stop("precision_recall: sample number of target and predict does not match")
  } 
  
  if (class.num !=ncol(target))
  {
    stop("precision_recall: class number of target and predict does not match")
  } 
    
  if(length(which(predict > 0)) == 0)
  {
    return(list(res=0,precision=rep(0,n),recall=rep(0,n)))
  }
  #threshold=unique.default(predict)
  #threshold = sort(threshold, decreasing=TRUE)
  #threshold=c(1,threshold,0)
  threshold=seq(1,0,-0.01)
  thres.num=length(threshold)
  TP=matrix(0,nrow = thres.num,ncol=class.num) 
  FN=matrix(0,nrow = thres.num,ncol=class.num) 
  FP=matrix(0,nrow = thres.num,ncol=class.num) 
  precision = matrix(0,nrow = thres.num,ncol=1)
  recall = matrix(0,nrow = thres.num,ncol=1)
  
  for(k in 1: thres.num)
  {
    cat('K is ' ,k,"\n")
    for(j in 1:class.num)
    {
      tp=0
      tn=0
      fp=0
      fn=0
      for(i in 1:sample.num)
      {
        if(predict[i,j]>=threshold[k])
        {
          if(target[i,j]==1)
          {
           tp=tp+1 
          }else
          {
            fp=fp+1
          }
        }else
        {
          if(target[i,j]==1)
          {
            fn=fn+1 
          }
        }
      }
      TP[k,j]=tp
      FN[k,j]=fn
      FP[k,j]=fp
    }
    if(sum(TP[k,])==0)
    {
      precision[k,1]=1
      recall[k,1]=0
    }else
    {
      precision[k,1]=sum(TP[k,])/(sum(TP[k,])+sum(FP[k,]))
      recall[k,1]=sum(TP[k,])/(sum(TP[k,])+sum(FN[k,]))
    }
  }
  
  AU.prc=trap.rule.integral(recall[,1], precision[,1])
  if(plot.en==TRUE)
  {
    plot(recall,precision,type="l",lwd=5,col=4,xlab="Recall",ylab="Precision",main="PR curve")
    grid(5,5,lwd=1)
  }
  
  return(AU.prc)
   #f.score <- (2 * precision * recall)/(precision + recall)
   #f.score[is.nan(f.score)] <- 0
  #return(list(precision=precision,recall=recall,f.score=f.score))
  #return(list(TP=TP,FN=FN,FP=FP))
}