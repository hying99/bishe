#####绘制父节点与子节点的决策边界#####
#####并试图证明全部测试集均在两者重合部分，或父节点为1子节点为0区域#####

#####找到父比子含标签数多的节点#####

  for (j in 1:ncol(train.select.table2)) {
    if(FALSE %in% is.na(nodes.to.parents2[[j]]))
      {
      for (k in 1:length(nodes.to.parents2[[j]])) {
        if(sum(train.select.table2[,j]) < sum(train.select.table2[,nodes.to.parents2[[j]][k]]))
        {
          print(paste(nodes.to.parents2[[j]][k],j,sep = ","))
        }
      }
      
    }
  }
#####(p,c) (7,4)(12,29)(170,213)(160,205)(116,169)#####
library(e1071)
library(ggplot2)
parentsample <- read.csv("C:/Users/1231/Desktop/dataprocessing/data/223dataset1/160.csv",header = FALSE)
childsample <- read.csv("C:/Users/1231/Desktop/dataprocessing/data/223dataset1/205.csv",header = FALSE)
parentsample$V51 <- as.factor(parentsample$V51)
childsample$V51 <- as.factor(childsample$V51)
#####parentnode获取前两个主成分#####
pcaparent <- princomp(parentsample[,1:50],cor = FALSE)
parent_score <- as.data.frame(pcaparent$scores[,1:2])
parent_score$label <- parentsample$V51
ggplot(parent_score,aes(x=Comp.1,y= Comp.2,colour = label,shape = label))+
  geom_point()+theme(legend.position = "right")+
  labs(x = "主成分得分1", y = "主成分得分2", title = "主成分降维散点图" )

svmfit <- svm(label ~ .,data=parent_score,kernel = "radial")

plot(svmfit,parent_score)

summary(pcaparent)
#####childnode#####
pcachild <- princomp(childsample[,1:50],cor = FALSE)
child_score <- as.data.frame(pcachild$scores[,1:2])
child_score$label <- childsample$V51
# ggplot(child_score,aes(x=Comp.1,y= Comp.2,colour = label,shape = label))+
#   geom_point()+theme(legend.position = "right")+
#   labs(x = "主成分得分1", y = "主成分得分2", title = "主成分降维散点图" )

svmfit2 <- svm(label ~ .,data=child_score,kernel = "radial")

plot(svmfit2,child_score)

summary(pcachild)
