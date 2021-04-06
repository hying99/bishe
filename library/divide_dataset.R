DivideDataset<-function (matrix.gene.data,ratio=2/3)
{   
  row.num=nrow(matrix.gene.data) 
  set.seed(1)
  row.index=sample(1:row.num, size = row.num) 
  training.samples.num=round(row.num*ratio)
  test.samples.num=row.num-training.samples.num
  training.dataset=matrix.gene.data[row.index[1:training.samples.num],]
  test.dataset=matrix.gene.data[row.index[(training.samples.num+1):row.num],]
  matrix.data.list=list(training.dataset,test.dataset)
  
  return(matrix.data.list)
}