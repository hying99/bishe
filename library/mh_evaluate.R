MHevaluate<-function (predict.labels,true.labels)
{
  each.tp=rep(0,time=nrow(predict.labels))
  each.precision=rep(0,time=nrow(predict.labels)) 
  each.recall=rep(0,time=nrow(predict.labels))
  each.f=rep(0,time=nrow(predict.labels))
  
  for(i in 1:nrow(predict.labels))
  {
    tp.num=0
    for(j in 1:ncol(predict.labels))
    {
      if(predict.labels[i,j]==1)
      {
        if(true.labels[i,j]==1)
        {
          tp.num=tp.num+1
        }
      }
    }
    each.tp[i]=tp.num
    
    if(tp.num==0)
    {
      each.precision[i]=0
      each.recall[i]=0
    } else
    {
      each.precision[i]=tp.num/sum(predict.labels[i,])
      each.recall[i]=tp.num/sum(true.labels[i,])
    }
    
    if((each.precision[i]+each.recall[i])!=0)
    {
      each.f[i]=(2*each.precision[i]*each.recall[i])/(each.precision[i]+each.recall[i])
    }  else
    {
      each.f[i]=0
    }
  }
  macro.hprecision=sum(each.precision)/nrow(predict.labels)
  macro.hrecall=sum(each.recall)/nrow(predict.labels)
  macro.hf=sum(each.f)/nrow(predict.labels)
  
  micro.hprecision=sum(each.tp)/sum(predict.labels)
  micro.hrecall=sum(each.tp)/sum(true.labels)
  micro.hf=(2*micro.hprecision*micro.hrecall)/(micro.hprecision+micro.hrecall)
  measure.result=list(macro.hprecision,macro.hrecall,macro.hf,micro.hprecision,micro.hrecall,micro.hf)
  names(measure.result)=c("macro.hprecision","macro.hrecall","macro.hf","micro.hprecision","micro.hrecall","micro.hf")
  return(measure.result)
}