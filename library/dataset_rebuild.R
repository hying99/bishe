####由于目前训练集 验证集 测试集数据分配产生的差异性很大，采用此函数重新生成三种数据集 20170328
####此函数仅输出重采样后的数据集，不支持对数据的属性选择,也不输出标签
####由于函数采用采样方法重新生成三种数据集，故每次生成结果均不相???
####row.names.enable用于选择是否输出各行名称，即基因的名称；因数据处需求要，默认为输出
####按照修改后的ReadData函数进行了修改 20170524
DatasetRebuild<-function (file.prefix,factor.col.index,factor.levels,data.path,file.savepath, 
                          write.data.enable=TRUE ,row.names.enable=TRUE)
{
  setwd(data.path)
  #读入训练集数据
  train.original=ReadData(paste("originaldata//",file.prefix,"0.train",sep = ""),factor.col.index = factor.col.index,factor.levels = factor.levels)
  train.original.data=train.original[[1]]#基因的数据信息
  #读入验证集
  original.valid.file=paste("originaldata//",file.prefix,"0.valid",sep = "")
  valid.original=ReadData(original.valid.file,factor.col.index = factor.col.index,factor.levels = factor.levels)#读入valid基因特征属性
  valid.original.data=valid.original[[1]]#valid基因的数据信息
  #读入测试集
  original.test.file=paste("originaldata//",file.prefix,"0.propertest",sep = "")
  test.original=ReadData(original.test.file,factor.col.index = factor.col.index,factor.levels = factor.levels)#读入test基因特征属性
  test.original.data=test.original[[1]]#test基因的数据信息
  #将三个数据集合并
  all.data=rbind(train.original.data,valid.original.data,test.original.data)
  #得到合并后的数据集有注释信息的基因名称列表
  common.genes <- Get.all.common.genes(go.general.table.BP, all.data)
  #得到有注释的基因对应的数???
  select.all.data=all.data[common.genes,]
  #得到现有数据的总样本数
  all.sample.num=nrow(select.all.data)
  #对现有的数据样本的顺序进行抽样重排列
  all.sample.index=sample(c(1:all.sample.num),all.sample.num)
  #生成抽样排序后的整体数据集
  sorted.all.data=select.all.data[all.sample.index,]
  #确定训练集 验证集 测试集所含有的样本的数量
  train.sample.num=round(all.sample.num*4/9)
  valid.sample.num=round(all.sample.num*2/9)
  test.sample.num=all.sample.num-train.sample.num-valid.sample.num
  #得到新生成的训练集 验证集 测试集
  train.data=sorted.all.data[1:train.sample.num,]
  valid.data=sorted.all.data[(1+train.sample.num):(train.sample.num+valid.sample.num),]
  test.data=sorted.all.data[(1+train.sample.num+valid.sample.num):(all.sample.num),]
  #为与原数据存储函数区分，此处生成的文件名为re_traindataset.csv，而非0_traindataset.csv
  if(write.data.enable)
  {
    setwd(file.savepath)
    #存储训练集的数据
    write.train.fname=paste(file.prefix,"re_traindataset.csv",sep = "")
    write.table(train.data,file=write.train.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names =row.names.enable,col.names = FALSE)
    #存储验证集的数据
    write.valid.fname=paste(file.prefix,"re_validdataset.csv",sep = "")
    write.table(valid.data,file=write.valid.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = row.names.enable,col.names = FALSE)
    #存储测试集的数据
    write.test.fname=paste(file.prefix,"re_testdataset.csv",sep = "")
    write.table(test.data,file=write.test.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names =row.names.enable,col.names = FALSE)
  }
}


