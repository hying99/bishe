#对输入数据的factor因子进行处理，将其转化为向量形式
#例如 该列只能取值为 yes no 则将yes转化为向量（1,0）,no转化为向量（0,1）
#20170524
FactorToNum<-function (input.data,factor.col.index,factor.levels)
{
  #确定共有几个为factor类型的列
  factor.num=length(factor.col.index)
  #确定输入数据的行数
  row.num=nrow(input.data)
  #对于每一个factor类型的列数据进行处理
  for(i in 1:factor.num)
  {
    #得到该列的全部数据
    factor.data=input.data[,factor.col.index[i]]
    #得到这列的factor可能的取值的具体内容
    factor.values=factor.levels[[i]]
    #得到这列的factor有多少个可能的取值
    values.num=length(factor.values)
    #建立一个用于存放转化后结果的向量
    factor.matrix=matrix(0,nrow=row.num,ncol=values.num)
    #建立一个统计各值出现次数的向量
    each.value.num=matrix(0,nrow=values.num,ncol=1)
    #统计每一个值出现的具体次数
    for(j in 1:values.num)
    {
      each.value.num[j,1]=length(which((factor.data==factor.values[j])))
    }
    #得到出现次数最多的取值在取值向量factor.values中的索引号
    max.index=which.max(each.value.num)
    #查找数据中的缺失值索引号
    question.index=which(factor.data=="?")
    #将缺失值替换为出现频率最大的值
    if(length(question.index)>0)
    {
      factor.data[question.index]=factor.values[max.index]
    }
    #将此列中factor可取各值转化为数值形式，并存入矩阵factor.matrix中
    for(j in 1:row.num)
    {
      for(k in 1:values.num)
      {
        if(factor.data[j]==factor.values[k])
        {
          factor.matrix[j,k]=1
        }
      }
    }
    #将最终的转化结果合并为一个矩阵并输出
    if(i==1)
    {
     output.data=factor.matrix 
    }else
    {
      output.data=cbind(output.data,factor.matrix)
    }
  }
  
  return (output.data)
  
}
