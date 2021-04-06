#用于生成csv形式的训练、验证及测试文件的函数 20170330
#对于每个数据集 均生成两个文件，一个是全体数据，一个是数据对应的标签文件
#read.original变量用于判断原始数据的来源
BuildMatlabDataset<-function (read.original=TRUE,file.prefix, factor.col,data.path,file.savepath,delete.outlier,
                              replace.outlier, no.del.replace,NAreplace,Zrescale, write.data.enable,
                              write.class.enable)
{
  setwd(data.path)
  if(read.original)#选择读取原始的训练文件
  {
    train.original=ReadData(paste("originaldata//",file.prefix,"0.train",sep = ""),factor.col = factor.col)
    train.data=train.original[[1]]#基因的数据信息
    
  }else#选择读取重采样后的训练文件
  {
    file.name=paste("rebuilddata//",file.prefix,"re_traindataset.csv",sep = "")
    train.data=read.csv(file.name,header = FALSE,row.names = 1)
  }
  
  trainscale.result=TraindataScale(train.data,factor.col,delete.outlier=delete.outlier,replace.outlier = replace.outlier,
                                   no.del.replace =no.del.replace,NAreplace=NAreplace,Zrescale=Zrescale)
  remain.data=trainscale.result[[1]]
  sp=trainscale.result[[2]]
  
  #得到几个数据集共有的基因的名称列表
  common.genes <- Get.all.common.genes(go.general.table.BP, remain.data)
  remain.select.data=remain.data[common.genes,]
  #得到common genes中每个基因对应的全部GO标签列表
  match.go.general=go.general.list.BP[common.genes]
  
  #将标签列表转换为TABLE形式，行名称为基因名称，列名称为GO标签
  match.go.table=Build.GO.class.labels(match.go.general)
  
  #得到目前的所有基因共有多少个不重复的GO标签
  all.go.labels=Get.classes(match.go.general)
  
  #得到DAG图中包含select.num个样本以上的节点列表及对应的基因名称
  go.label.list=DataCleaning(match.go.general,match.go.table,select.num = 100)
  all.go.labels.sel=Get.classes(go.label.list)
  
  #得到修改后的DAG图中各节点的层级
  graph.BP.general.sel <- subGraph(all.go.labels.sel, BP.univ.graph)
  graph.BP.level.sel=GraphLevel(graph.BP.general.sel)
  go.level.statistics=LevelStatistics(graph.BP.level.sel)
  go.for.each.level=go.level.statistics[[1]]
  each.level.nodes.num=go.level.statistics[[2]]
  total.levels=length(go.for.each.level)
  
  #选择根结点至total.levels的所有go节点，select.node.3为向量形式
  select.node=NodeSelectByLevel(go.level.statistics,total.levels,add.root.node = TRUE)
  except.root.labels=setdiff(select.node,"GO:0008150")
  
  #产生训练集中各基因注释的GO标签
  except.root.table=match.go.table[,except.root.labels]
  #生成训练集
  if(write.data.enable)
  {
    setwd(file.savepath)
    if(read.original)
    {
      write.data.fname=paste(file.prefix,"0_traindataset.csv",sep = "")
    }else
    {
      write.data.fname=paste(file.prefix,"1_traindataset.csv",sep = "")
    }
    write.table(remain.select.data,file=write.data.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
    
  }
  if(write.class.enable)
  {
    setwd(file.savepath)
    if(read.original)
    {
      write.class.fname=paste(file.prefix,"0_trainclass.csv",sep = "")
    }else
    {
      write.class.fname=paste(file.prefix,"1_trainclass.csv",sep = "")
    }
    write.table(except.root.table,file=write.class.fname,sep = ",",eol="\n",quote=FALSE,na="0",row.names = FALSE,col.names = FALSE)
  }
  
  #生成验证集
  setwd(data.path)
  
  select.attributes.en=FALSE
  if(read.original)#选择读取原始的验证集文件
  {
    #读入valid基因特征属性
    valid.original=ReadData(paste("originaldata//",file.prefix,"0.valid",sep = ""),factor.col = factor.col)
    valid.data=valid.original[[1]]#基因的数据信息
    write.data.fname=paste(file.prefix,"0_validdataset.csv",sep = "")
    write.class.fname=paste(file.prefix,"0_validclass.csv",sep = "")
    
  }else#选择读取重采样后的验证集文件
  {
    file.name=paste("rebuilddata//",file.prefix,"re_validdataset.csv",sep = "")
    valid.data=read.csv(file.name,header = FALSE,row.names = 1)
    write.data.fname=paste(file.prefix,"1_validdataset.csv",sep = "")
    write.class.fname=paste(file.prefix,"1_validclass.csv",sep = "")
  }
  
  if(select.attributes.en==FALSE)
  {
    valid.scaled.data=ValiddataScale(valid.data,factor.col,sp,replace.outlier=replace.outlier,no.del.replace = no.del.replace,NAreplace=NAreplace,Zrescale=Zrescale)
    setwd(file.savepath)
    valid.data.total=BuildValidset(valid.scaled.data,go.general.table.BP,go.general.list.BP,except.root.labels,
                                   write.data.enable=write.data.enable,write.class.enable=write.class.enable,write.data.fname=write.data.fname,
                                   write.class.fname=write.class.fname,select.attributes.en=select.attributes.en,select.attributes)
    valid.select.data=valid.data.total[[1]]
    valid.select.table=valid.data.total[[2]]
  }
  
  #生成测试集
  setwd(data.path)#此处不可删去，因为上一步已经更改了文件存储地址为file.savepath
  if(read.original)#选择读取原始的测试集文件
  {
    #读入test基因特征属性
    test.original=ReadData(paste("originaldata//",file.prefix,"0.propertest",sep = ""),factor.col = factor.col)
    test.data=test.original[[1]]#基因的数据信息
    write.data.fname=paste(file.prefix,"0_testdataset.csv",sep = "")
    write.class.fname=paste(file.prefix,"0_testclass.csv",sep = "")
    
  }else#选择读取重采样后的测试集文件
  {
    file.name=paste("rebuilddata//",file.prefix,"re_testdataset.csv",sep = "")
    test.data=read.csv(file.name,header = FALSE,row.names = 1)
    write.data.fname=paste(file.prefix,"1_testdataset.csv",sep = "")
    write.class.fname=paste(file.prefix,"1_testclass.csv",sep = "")
  }
  
  select.attributes.en=FALSE
  if(select.attributes.en==FALSE)
  {
    test.scaled.data=ValiddataScale(test.data,factor.col,sp,replace.outlier=replace.outlier,no.del.replace = no.del.replace,NAreplace=NAreplace,Zrescale=Zrescale)
    setwd(file.savepath)
    test.data.total=BuildValidset(test.scaled.data,go.general.table.BP,go.general.list.BP,except.root.labels,
                                  write.data.enable=write.data.enable,write.class.enable=write.class.enable,write.data.fname=write.data.fname,
                                  write.class.fname=write.class.fname,select.attributes.en=select.attributes.en,select.attributes)
    test.select.data=test.data.total[[1]]
    test.select.table=test.data.total[[2]]
  }
  
}



