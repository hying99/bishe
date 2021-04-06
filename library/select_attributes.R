SelectAttributes<-function (original.data,min.attr.ratio=0.25,min.gainratio=0.95)
{
  original.samples=original.data$X
  attr.num=ncol(original.samples)
  original.labels=original.data$labels
  n.pos=original.data$n.pos
  n.neg=original.data$n.neg
  
  if((n.pos!=0)&&(n.neg!=0))
  {
    inter.data=data.frame(data=original.samples,class=original.labels)
    gainratio.result=GainRatioAttributeEval(class~., inter.data)
  }  else
  {
    gainratio.result=NA
  }
  
  if(all(gainratio.result==0)||is.na(gainratio.result))
  {
    select.samples=original.samples
    select.attributes=c(1:attr.num)
  }  else
  {
    min.attr.num=floor(attr.num*min.attr.ratio)
    gainratio.ordered <- order(gainratio.result, decreasing=TRUE)	
    sort.gainratio <- sort(gainratio.result, decreasing=TRUE)
    scaled.gainratio=sort.gainratio/(sum(sort.gainratio))
    temp.gainratio.sum=0
    i=0
    while(i<attr.num)
    {
      i=i+1
      temp.gainratio.sum=temp.gainratio.sum+scaled.gainratio[i]
      if(temp.gainratio.sum>=min.gainratio)
      {
        if(i>=min.attr.num)
        {
          select.attr.pos=i
          i=attr.num
        }
      }      else
      {
        if(i==attr.num)
        {
          select.attr.pos=i
        }
      }
    }
    select.attributes=gainratio.ordered[1:select.attr.pos]
    select.attributes=sort(select.attributes,decreasing = FALSE)
    select.samples=original.samples[,select.attributes]
    
  }
  result=list(X=select.samples,labels=original.labels,n.pos=n.pos,n.neg=n.neg,select.attributes=select.attributes)
  return(result)
  
}