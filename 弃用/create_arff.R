###生成含有标签值和特征值arff文件###
Createarff <- function(select.data,label)
{
  attribute <- vector()
  arff <- matrix()
  for (i in 1:nrow(select.data)) {
    attribute[i] <- paste(select.data[i,],collapse = ",")
  }
  arff <- cbind(attribute,label)
  return(arff)
}