#####提供把最后一个attribute改成class hierachy的服务

Adjustattribute <- function(oldarff,newarff)
{
  original.data <- scan(oldarff,what = list(""),sep = "\n")
  each.row.data=list()   
  #将每一行的数据读入   
  for(i in 1:length(original.data[[1]]))   
  {     
    each.row.data[i]=original.data[[1]][i]
  }
  #找到最后一个attribute进行更改
  adjustpos <- grep("attribute",each.row.data)[length(grep("attribute",each.row.data))] 
  each.row.data[[adjustpos]] <- paste("@attribute class hierarchical",paste(edgedata,collapse = ","),sep = " ") 
  write.table(each.row.data,newarff,row.names = FALSE,col.names = FALSE, quote = FALSE,sep="\n")
}