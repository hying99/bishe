#替换原read_data函数对数据预处理的功能，对验证及测试数据集进行处理
ValiddataScale<-function (valid.original.data,factor.col.num,sp,replace.outlier=TRUE,no.del.replace=FALSE,NAreplace=TRUE,Zrescale=TRUE)
{
  
  valid.col.num=ncol(valid.original.data)
  valid.row.num=nrow(valid.original.data)
  numeric.col=c(1:(valid.col.num-factor.col.num))#根据输入的factor属性列数量，求numeric属性的列号
  valid.scaled.data=matrix(0,nrow = valid.row.num,ncol = valid.col.num)
  rownames(valid.scaled.data)=rownames(valid.original.data)
  
  each.col.status=matrix(0,nrow = valid.col.num,ncol = 1)
  
  if(replace.outlier==TRUE)
  { #将异常值替换为NA
    for(j in numeric.col)
    {
      each.col.min=sp[[j]]$stats[1]
      each.col.max=sp[[j]]$stats[5]
      for(i in 1:valid.row.num)
      {
        if(!is.na(valid.original.data[i,j]))
        {
          if(valid.original.data[i,j]>each.col.max)
          {
            valid.original.data[i,j]=NA
          } else if(valid.original.data[i,j]<each.col.min)
          {
            valid.original.data[i,j]=NA
          } 
        }
      }
    }
  }else if(no.del.replace==TRUE) #此处为真，则不对数据进行异常值替换操作
  {
    
  }  else
  { #将大于上届的异常值替换为上届，小于上届的异常值替换为下届
    for(j in numeric.col)
    {
      each.col.min=sp[[j]]$stats[1]
      each.col.max=sp[[j]]$stats[5]
      for(i in 1:valid.row.num)
      {
        if(!is.na(valid.original.data[i,j]))
        {
          if(valid.original.data[i,j]>each.col.max)
          {
            valid.original.data[i,j]=each.col.max
          } else if(valid.original.data[i,j]<each.col.min)
          {
            valid.original.data[i,j]=each.col.min
          } 
        }
        
      }
    }
  }
  
  if(NAreplace==TRUE)
  {
    #计算每列的最小值、最大值、平均值
    for(j in numeric.col)
    {
      #each.col.status[j,1]=min(matrix.data[,j],na.rm = TRUE)
      #each.col.status[j,2]=max(matrix.data[,j],na.rm = TRUE)
      each.col.status[j,1]=mean(valid.original.data[,j],na.rm = TRUE)
    }
    
    #连续属性，将NA值用对应列的均值替换
    for(i in 1:valid.row.num)
    {
      for(j in numeric.col)
      {
        if(is.na(valid.original.data[i,j]))
        {
          valid.original.data[i,j]=each.col.status[j,1]
        }
      }
    }
  }
  
  #将数据规划到【0 1】区间
  if(Zrescale==TRUE)
  {
    for(j in numeric.col)
    {
      each.col.min=sp[[j]]$stats[1]
      each.col.max=sp[[j]]$stats[5]
      for(i in 1:valid.row.num)
      {
          range=each.col.max-each.col.min
          if(range==0)
          {
            valid.scaled.data[i,j]=0
          } else
          {
            valid.scaled.data[i,j]=(valid.original.data[i,j]-each.col.min)/(range) 
          }
        
      }
    }
  }else
  {
    valid.scaled.data=valid.original.data
  }
  
  result=valid.scaled.data
}
  






