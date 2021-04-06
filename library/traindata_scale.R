#替换原read_data函数对数据预处理的功能，对训练数据集进行处理
TraindataScale<-function (training.original.data,factor.col.num,delete.outlier=FALSE,replace.outlier=FALSE,no.del.replace=FALSE,NAreplace=TRUE,Zrescale=TRUE)
{
  col.num=ncol(training.original.data)
  row.num=nrow(training.original.data)
  numeric.col=c(1:(col.num-factor.col.num))
  #绘制散点图和箱形图，记录数据的合理范围
  sp=list()
  sp_name=c()
  sp.eachnum=matrix(0,nrow = col.num,ncol = 1)
  #数据预处理目前仅对数据类型的各列数据进行处理
  for(j in numeric.col)
  {
    #dotchart(matrix.cellcycle.data[1:10,1])
    #绘制箱形图，查找异常值
    sp[[j]]=boxplot(training.original.data[,j],horizontal=TRUE,plot=FALSE)
    sp_name=c(sp_name,names(sp[[j]]$out))
    sp.eachnum[j,1]=length(names(sp[[j]]$out))
  }
  
  sp_name=unique(sp_name)
  training.original.name=rownames(training.original.data)
  #此处判断异常值该如何处理
  if(delete.outlier==TRUE)
  {
    #默认剔除具有异常值的样本
    select.name=setdiff(training.original.name,sp_name)
    training.original.data=training.original.data[select.name,]
  } else if(replace.outlier==TRUE)
  {
    #先将样本的异常值转换为NA
    for(j in numeric.col)
    {
      each.col.min=sp[[j]]$stats[1]
      each.col.max=sp[[j]]$stats[5]
      for(i in 1:row.num)
      {
        if(!is.na(training.original.data[i,j]))
        {
          if(training.original.data[i,j]>each.col.max)
          {
            training.original.data[i,j]=NA
          } else  if(training.original.data[i,j]<each.col.min)
          {
              training.original.data[i,j]=NA
          }
           
        }
        
      }
    }
  } else if(no.del.replace==TRUE)#不进行异常值替换和删除
  {
    
  } else#当delete.outlier replace.outlier均为false时，进行如下替换，一般情况下都进行如下替换
  { #将大于上界的异常值替换为上界，小于下界的异常值替换为下界
    for(j in numeric.col)
    {
      each.col.min=sp[[j]]$stats[1]
      each.col.max=sp[[j]]$stats[5]
      for(i in 1:row.num)
      {
        if(!is.na(training.original.data[i,j]))
        {
          if(training.original.data[i,j]>each.col.max)
          {
            training.original.data[i,j]=each.col.max
          } else if(training.original.data[i,j]<each.col.min)
          {
            training.original.data[i,j]=each.col.min
          } 
        }
        
      }
    }
  }
  
  each.col.status=matrix(0,nrow = col.num,ncol = 1)
  if(NAreplace==TRUE)#对数据中存在的NA进行替换
  {
    #计算每列的最小值、最大值、平均值
    for(j in numeric.col)
    {
      #each.col.status[j,1]=min(matrix.data[,j],na.rm = TRUE)
      #each.col.status[j,2]=max(matrix.data[,j],na.rm = TRUE)
      each.col.status[j,1]=mean(training.original.data[,j],na.rm = TRUE)
    }
    
    #连续属性，将NA值用对应列的均值替换
    for(i in 1:nrow(training.original.data))
    {
      for(j in numeric.col)
      {
        if(is.na(training.original.data[i,j]))
        {
          training.original.data[i,j]=each.col.status[j,1]
        }
      }
    }
  }
  
  if(Zrescale==TRUE)
  {
    #将各属性值缩放至【0,1】区间
    for(j in numeric.col)
    {
      training.original.data[,j]=rescale(training.original.data[,j])
    }
  }
  remain.data=training.original.data
  
  result=list(remain.data,sp)
}