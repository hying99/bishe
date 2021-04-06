#改写的用于实现NegGOA的程序 20170220
#输入的节点包含根结点，最后一项为根结点
NegGoaSelection<-function(select.node,graph.BP.level,match.go.table,common.genes)
{
  GoDepths=graph.BP.level[select.node]+1
  #生成各节点的子节点列表
  child.ids=list()
  for(i in 1:length(select.node))
  {
    inter.ids=AnnotationDbi::get(select.node[[i]], GOBPCHILDREN)
    child.ids[[i]]=intersect(select.node,inter.ids)
  }  
  #生成各节点的祖先节点列表
  ancestors.ids=list()
  for(i in 1:length(select.node))
  {
    inter.ids=AnnotationDbi::get(select.node[[i]], GOBPANCESTOR)
    ancestors.ids[[i]]=intersect(select.node,inter.ids)
    ancestors.ids[[i]]=c(select.node[[i]],ancestors.ids[[i]])
  }  
  #生成各节点的子孙节点列表
  descendant.ids=list()
  for(i in 1:length(select.node))
  {
    inter.ids=AnnotationDbi::get(select.node[[i]], GOBPOFFSPRING)
    descendant.ids[[i]]=intersect(select.node,inter.ids)
    
  }  
  #生成GO节点互联矩阵
  dagMat=matrix(data=0,nrow =length(select.node) ,ncol = length(select.node))
  for(i in 1:length(select.node) )
  {
    for(j in 1:length(select.node) )
    {
      if(i==j)
      {
        dagMat[i,j]= 1   
      } else if(select.node[j] %in% child.ids[[i]])
      {
        dagMat[i,j]= 1
      }
    }
  }
  #得到全部GO节点的个数
  nTerms=length(select.node)
  #生成各节点的information content
  structIC=matrix(1,nTerms,1)
  for (ii in 1:nTerms) {
    if (length(descendant.ids[[ii]])>0){
      descendant<-descendant.ids[[ii]]
      structIC[ii]<-(1-log(length(descendant)+1)/log(nTerms))
      
    }else{
      structIC[ii]<-1
    }
  }
  structIC[is.nan(structIC)]<-0
  structIC[is.infinite(structIC)]<-0
  #生成语义相似度矩阵
  lin_sim<-matrix(0,nTerms,nTerms)
  for (ii in 1:(nTerms-1)) 
    {
      for (jj in (ii+1):nTerms) 
      {
        com_ances<-intersect(ancestors.ids[[ii]],ancestors.ids[[jj]])
        p_Idx<-getGOIdx(com_ances,select.node)
        if(length(p_Idx)>0)
        {
          pca<-max(structIC[p_Idx])  #%the most specific common ancestor
          sim<-2*pca/(structIC[[ii]]+structIC[[jj]])
          lin_sim[ii,jj]<-sim
          lin_sim[jj,ii]<-sim
        }else
        {
          cat('no shared ancestor for %s and %s\n', select.node[ii], select.node[jj])
        }
    }
    lin_sim[ii,ii]=1
  }
  lin_sim[nTerms,nTerms]=1
  #此处
  gnd=as.matrix(match.go.table[common.genes,select.node])
  
  Prob_sim<-t(gnd)%*%gnd
  for (ii in 1:nrow(Prob_sim)) 
  {
    Prob_sim[ii,]<- Prob_sim[ii,]/sum(gnd[,ii])
  }
  Prob_sim[is.nan(Prob_sim)]<-0
  Prob_sim[is.infinite(Prob_sim)]<-0
  
  NegativeProb=getNegPotential(gnd,dagMat,Prob_sim,lin_sim)
  
  sorted.prob=apply(NegativeProb,2,sort,decreasing=T)
  sorted.index=apply(NegativeProb,2,order,decreasing=T)
  neg.index=list()
  pos.index=list()
  negpos.num=matrix(0,nTerms,2)
  for(i in 1:nTerms)
  {
    neg.index[[i]]=which(sorted.prob[,i]>=0)
    negpos.num[i,1]=length(neg.index[[i]])
    pos.index[[i]]=which(sorted.prob[,i]<0)
    negpos.num[i,2]=length(pos.index[[i]])
  }
  
  neggoa.result=list(neg.index=neg.index,pos.index=pos.index,negpos.num=negpos.num)
  
  return(neggoa.result)
  
}