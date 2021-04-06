##用于按需求将训练集、验证集和测试集数据保存成csv文件，以便在matlab中运行
##使用时需承接rebuilddataprocess.R文件
##跟matlabfile.R功能相近，matlabfile.R是函数形式，适合批量处理
##本文件是命令行形式，便于修改
##该文件 20180424创建
#生成训练集
write.data.enable=TRUE
write.class.enable=TRUE
file.savepath="F://R//DATA//matlabfile"


train.data=remain.select.data
train.label.matrix=except.root.table
valid.data=valid.select.data
valid.label.matrix=valid.select.table
test.data=test.select.data
test.label.matrix=test.select.table

setwd(data.path)
if(write.data.enable)
{
  setwd(file.savepath)
  if(read.original)
  {
    train.data.fname=paste(file.prefix,"0_traindataset.csv",sep = "")
  }else
  {
    train.data.fname=paste(file.prefix,"1_traindataset.csv",sep = "")
  }
  write.table(train.data,file=train.data.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
  
}
if(write.class.enable)
{
  setwd(file.savepath)
  if(read.original)
  {
    train.class.fname=paste(file.prefix,"0_trainclass.csv",sep = "")
  }else
  {
    train.class.fname=paste(file.prefix,"1_trainclass.csv",sep = "")
  }
  write.table(train.label.matrix,file=train.class.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
}

#生成验证集
setwd(data.path)

if(write.data.enable)
{
  setwd(file.savepath)
  if(read.original)
  {
    valid.data.fname=paste(file.prefix,"0_validdataset.csv",sep = "")
  }else
  {
    valid.data.fname=paste(file.prefix,"1_validdataset.csv",sep = "")
  }
  write.table(valid.data,file=valid.data.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
  
}
if(write.class.enable)
{
  setwd(file.savepath)
  if(read.original)
  {
    valid.class.fname=paste(file.prefix,"0_validclass.csv",sep = "")
  }else
  {
    valid.class.fname=paste(file.prefix,"1_validclass.csv",sep = "")
  }
  write.table(valid.label.matrix,file=valid.class.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
}



#生成测试集
setwd(data.path)#此处不可删去，因为上一步已经更改了文件存储地址为file.savepath



if(write.data.enable)
{
  setwd(file.savepath)
  if(read.original)
  {
    test.data.fname=paste(file.prefix,"0_testdataset.csv",sep = "")
  }else
  {
    test.data.fname=paste(file.prefix,"1_testdataset.csv",sep = "")
  }
  write.table(test.data,file=test.data.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
  
}
if(write.class.enable)
{
  setwd(file.savepath)
  if(read.original)
  {
    test.class.fname=paste(file.prefix,"0_testclass.csv",sep = "")
  }else
  {
    test.class.fname=paste(file.prefix,"1_testclass.csv",sep = "")
  }
  write.table(test.label.matrix,file=test.class.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
}




