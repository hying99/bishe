#####产生供HR-SVM使用的dat_train,dat_test的function#####
generatedat <- function(select.table,select.data)
  {
  data<-matrix(data = 0, nrow =  nrow(select.data), ncol = ncol(select.data)+1);
  data_labels<-matrix(data = 0, nrow =  nrow(select.data), ncol =ncol(select.data));
  data_classes<-matrix(data = 0, nrow =  nrow(select.data), ncol = 1);
  
  for(i in 1: nrow(select.data))
  {
    for(j in 1:ncol(select.data))
    {
      
      
      t=paste(j,select.data[i,j],sep = ":");
      data_labels[i,j]<-t;
      
    } 
    
    
  }
  
  for(m in 1: nrow(select.data))
  {
    
    p=0;
    for(n in 1:ncol(select.table))
    {
      if(select.table[m,n]==1)
      {
        p=paste(p,n,sep = ",");
        data_classes[m,1]<-p;
      }  else
      {
        ;
      }
    }
    
  }
  
  data<-cbind(data_classes,data_labels);
  return(data)
}