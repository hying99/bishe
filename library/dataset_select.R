####用于选择使用哪一个数据集的函数 20170330
#### 每个数字对应一个数据集
#### 1 cellcycle 2 derisi 3 eisen 4 gasch1 5 gasch2 6 church 7 spo 8 seq 9 struc 10 hom
#### 目前只实现了1 2 3 4 5 7 
#### 20170524 修改factor.col为factor.col.index 增加了factor.col.num和factor.levels
#### factor.col.num表示转化为数值后具有的factor列总数
#### factor.levels表示各factor取值的具体情况

DatasetSelect<-function (dataset.index)
{
  if(dataset.index==1)
  {
    file.prefix="cellcycle"
    factor.col.index=c()
    factor.col.num=0
    factor.levels=list()
  }else if(dataset.index==2)
  {
    file.prefix="derisi"
    factor.col.index=c() 
    factor.col.num=0
    factor.levels=list()
  }else if(dataset.index==3)
  {
    file.prefix="eisen"
    factor.col.index=c()
    factor.col.num=0
    factor.levels=list()
  }else if(dataset.index==4)
  {
    file.prefix="gasch1"
    factor.col.index=c()
    factor.col.num=0
    factor.levels=list()
  }else if(dataset.index==5)
  {
    file.prefix="gasch2"
    factor.col.index=c()
    factor.col.num=0
    factor.levels=list()
  }else if(dataset.index==6)
  {
    file.prefix="church"
    factor.col.index=c(1)
    factor.levels=list()
    factor.levels[[1]]=c("A","B","C","D","A-D")
    factor.col.num=5
  }else if(dataset.index==7)
  {
    file.prefix="spo"
    factor.col.index=c(78,79,80)
    factor.levels=list()
    factor.levels[[1]]=c("1","2","3","4","5")
    factor.levels[[2]]=c("no","yes")
    factor.levels[[3]]=c("no","yes")
    factor.col.num=9
  }else if(dataset.index==8)
  {
    file.prefix="seq"
    factor.col.index=c(473,474,476, 477, 478)
    factor.levels=list()
    factor.levels[[1]]=c("w","c")
    factor.levels[[2]]=c("1","2","3","4","5","6","7","8")
    factor.levels[[3]]=c("0","1","2","3","4","5","6","7","8","9","22")
    factor.levels[[4]]=c("0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","20")
    factor.levels[[5]]=c("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","mit")
    factor.col.num=56
  }else if(dataset.index==9)
  {
    file.prefix="struc"
    factor.col.index=c()
    factor.col.num=0
    factor.levels=list()
  }else if(dataset.index==10)
  {
    file.prefix="hom"
    factor.col.index=c()
    factor.col.num=0
    factor.levels=list()
  }
  result=list(file.prefix=file.prefix,factor.col.index=factor.col.index,factor.col.num=factor.col.num,factor.levels=factor.levels)
  return(result)
}