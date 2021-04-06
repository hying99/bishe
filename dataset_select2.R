DatasetSelect <- function(dataset.index)
{
  ###4031个数据，划分为5个数据集
  ###dataset1-4的数据数量
  datarange <- floor((nrow(except.root.table2))/5)
  ###dataset5的数据数量
  ###datarange2 <- nrow(except.root.table2) - datarange1 * 4
  if (dataset.index == 1)
  {
    partdatatable <- except.root.table2[(1:datarange),]
    partdata <- dataset[1:datarange,c(2:51)]
  }else if (dataset.index == 2)
  {
    partdatatable <- except.root.table2[((datarange + 1):(2*datarange)),]
    partdata <- dataset[((datarange + 1):(2*datarange)),c(2:51)]
  }else if (dataset.index == 3)
  {
    partdatatable <- except.root.table2[((2*datarange +1) :(3*datarange)),]
    partdata <- dataset[((2*datarange +1) :(3*datarange)),c(2:51)]
  }else if (dataset.index == 4)
  {
    partdatatable <- except.root.table2[((3*datarange +1) :(4*datarange)),]
    partdata <- dataset[((3*datarange +1) :(4*datarange)),c(2:51)]
  }else if (dataset.index == 5)
  {
    partdatatable <- except.root.table2[((4*datarange +1) :nrow(except.root.table2)),]
    partdata <- dataset[((4*datarange +1) :nrow(except.root.table2)),c(2:51)]
  }
  row.names(partdata) <- row.names(partdatatable)
  partdataresult <- list(partdatatable,partdata)
  return(partdataresult)
}