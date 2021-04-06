
PrecisionRecallCalculate <- function(predict,target)
{
  #判断输入的数据是什么类型，如果是data.frame,则将其转化为向量
  #建议输入的类型是矩阵matrix
  if(is.data.frame(predict))
  {
    predict=as.matrix(predict)
    aa=sapply(list(predict), as.vector)
    scores <- as.vector(aa)
  }else
  {
    scores = predict
  }
  if(is.data.frame(target))
  {
    target=as.matrix(target)
    bb=sapply(list(target), as.vector)
    labels <- as.vector(bb)
  }else
  {
    labels = target
  }
  
  n<-length(scores)
  if (n!=length(labels))
    stop("precision_recall: length of labels and scores does not match")
  if(length(which(labels > 0)) == 0)
    return(list(res=0,precision=rep(0,n),recall=rep(0,n)))
  scores.ordered <- order(scores, decreasing=TRUE)	
  sort.scores <- sort(scores, decreasing=TRUE)
  TP  <- rep(0, n)
  FN  <- rep(0, n)
  FP  <- rep(0, n)
  precision <- rep(0, n)
  recall <- rep(0, n)
  np=0
  for(i in 1:n)
  {
    if(labels[i]==1)
    {
      np=np+1
    }
  }
  tp=np
  fn=0
  tn=0
  fp=n-np
  TP[n]=tp
  FN[n]=fn
  FP[n]=fp
  
  for(i in (n-1):1)
  {
    if(labels[scores.ordered[i+1]]==1)
    {
      tp=tp-1
      fn=fn+1
    } else
    {
      tn=tn+1
      fp=fp-1
    }
    if(sort.scores[i]==sort.scores[i+1])
    {
      TP[i]=TP[i+1]
      FN[i]=FN[i+1]
      FP[i]=FP[i+1]
    } else
    {
      
      TP[i]=tp
      FN[i]=fn
      FP[i]=fp
    }
    
  }
  precision=TP/(TP+FP)
  recall=TP/(TP+FN)
  f.score <- (2 * precision * recall)/(precision + recall)
  f.score[is.nan(f.score)] <- 0
  return(list(precision=precision,recall=recall,f.score=f.score))
  #return(list(TP=TP,FN=FN,FP=FP))
}