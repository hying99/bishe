###由总数据集产生训练集和测试集
DatasetDivide = function(dataset)
{
  ###训练集占2/3
  datarange1 = floor(2*nrow(dataset)/3)
  traindataset <- dataset[1:datarange1,]
  ###验证集占2/9
  datarange2 = floor(2*nrow(dataset)/9)
  validdataset <- dataset[((datarange1+1):(datarange1+datarange2)),]
  ###测试集占1/9
  testdataset <- dataset[(datarange1+datarange2+1):nrow(dataset),]
  datasettodivide <- list(traindataset,validdataset,testdataset)
  return(datasettodivide)
}