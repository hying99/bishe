###select.labels
Labelselect <- function(select.table)
{
  samplenames <- row.names(select.table)
  labelsnames <- colnames(select.table)
  select.labels <- vector("list",length = nrow(select.table))
  names(select.labels) <- samplenames
  for (i in 1:nrow(select.table)) {
    for (j in 1:ncol(select.table)) {
      if (select.table[i,j] == 1)
        select.labels[[samplenames[i]]] <- c(select.labels[[samplenames[i]]],labelsnames[j])
    }
  }
  return(select.labels)
}