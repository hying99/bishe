##################################################################
# Script to format datasets GO to the binary form (class vector) #
# to be used by HMC-LMLP.										 #
# The level of a GO term is defined by the longest path between it and the root node.											 #
# The inputs files should be first formated.					 #
#																 #
# Ricardo Cerri - 17/04/2013									 #
##################################################################

#datasets <- c("cellcycle","church","derisi","eisen","gasch1","gasch2","pheno","spo","expr","seq")
#dirDatasets <- c("Cellcycle","Church","Derisi","Eisen","Gasch1","Gasch2","Pheno","Spo","Expr","Seq")

datasets <- c("traindata")
dirDatasets <- c("traindata")
#illegalClasses contains the root classes of each one of the Gene Ontology hierarchies
#As all instances are assigned to these classes, lets remove them
illegalClasses <- c("GO0003674","GO0005575","GO0008150")

pathToOriginalData <- "C:/Users/1231/Documents/share/我的坚果云/NeuraNETL2GO_download/NeuraNETL2GO_download/data/"

#Search for the direct parent(s) of class
getDirectParents <- function(class,classStructure){

	parentPositions <- grep(paste("/",class,sep=""),classStructure)
	parents <- vector()
	if(length(parentPositions) > 0){
		for(i in 1:length(parentPositions)){
			parents <- c(parents,unlist(strsplit(classStructure[parentPositions[i]],"/"))[1])
		}
	}

	return(parents)
}

#Get all possible paths from the root to a class，完成对每一类搜索从根节点到其的全部路径
getClassParentsLevel <- function(class,classStructure,path,pos){

	#Search for the direct parent(s) of class
	parents <- getDirectParents(class,classStructure)
	
	#新的class放到前面
	path <- paste(class,"/",path,sep="")
	
	#迭代，直到class没有父类（自身是root）
	if(length(parents) > 0){
		for(i in 1:length(parents)){
			getClassParentsLevel(parents[i],classStructure,path,pos)
		}
	}
	#产生listAllClassesLevels dataset
	else{	
		listAllClassesLevels[[pos]] <<- c(listAllClassesLevels[[pos]],path)	
	}
	return()
}
	
#Separate the classes per level
getClassesPerLevel <- function(listAllClassesLevels){

	levelClasses <- vector("numeric",length(listAllClassesLevels))

	for(j in 1:length(levelClasses)){
		numEdges <- vector()
		allPaths <- listAllClassesLevels[[j]][2:length(listAllClassesLevels[[j]])]
		for(k in 1:length(allPaths)){
			numEdges <- c(numEdges,length(unlist(strsplit(allPaths[k],"/"))))
		}
		levelClasses[j] <- max(numEdges)
	}

	#cat(levelClasses,"\n")

	#Number of levels
	numLevels <- length(unique(levelClasses))
	
	#List to store classes per level
	namesClasses <- vector("list",numLevels)
	for(i in 2:numLevels){
		regExpClass <- paste("^",i,"$",sep="")
		posClassesLevel <- grep(regExpClass,levelClasses)
		for(j in 1:length(posClassesLevel)){
			namesClasses[[i]] <- c(namesClasses[[i]],listAllClassesLevels[[posClassesLevel[j]]][1])
		}
	}	

	return(namesClasses)
}

#Create vector of classes separated per level
getNamesColumns <- function(classesPerLevel){
	
	namesColumns <- vector()

	for(i in 2:length(classesPerLevel)){
		nameClass <- paste("class_",classesPerLevel[[i]],"_level",i-1,sep="")
		namesColumns <- c(namesColumns,nameClass)
	}

	return(namesColumns)
}	

#Main script loop
for(i in 1:length(datasets)){
	
	#First read the class structure of the original data to determine class levels
	originalData <- scan(paste(pathToOriginalData,dirDatasets[i],'/',"HieraGOData20.txt",sep=""), what="character", dec=".", sep="\n")
  classStructure <- scan(paste(pathToOriginalData,dirDatasets[i],'/',"HieraGOData20.txt",sep=""), what="character", dec=".", sep="\n")
  #classStructure <- originalData[grep("hierarchical",originalData)]
	#classStructure <- unlist(strsplit(classStructure,"hierarchical"))[2]
	classStructure <- gsub(" ","",classStructure)
	classStructure <- unlist(strsplit(classStructure,","))

	#Remove illegalClasses
	for(j in 1:length(illegalClasses)){
		classStructure <- gsub(illegalClasses[j],"root",classStructure)
		classStructure <- classStructure[classStructure != "root/root"]		
	}

	#Get number of classes
	allClasses <- unlist(strsplit(classStructure,"/"))
	allClasses <- unique(allClasses)

	#List of classes. Each position of the list corresponts to a class.
	#The list contains all possible paths from the root to a given class
    listAllClassesLevels <<- vector("list",length(allClasses))
	for(j in 1:length(allClasses)){
		listAllClassesLevels[[j]] <- c(listAllClassesLevels[[j]],allClasses[j])
	}

	#debug(getClassParentsLevel)
	#debug(getDirectParents)
	#Get all possible paths from the root to a class
	#For each class, store the set of all classes belonging to the path
	#between the class and the root node
	allClassesPaths <- vector("list",length(allClasses))
	for(j in 1:length(allClasses)){
		path <- ""
		getClassParentsLevel(allClasses[j],classStructure,path,j)
	  #深度优先遍历
		parentClasses <- listAllClassesLevels[[j]][2:length(listAllClassesLevels[[j]])]	
		parentClasses <- unlist(strsplit(parentClasses,"/"))
		parentClasses <- unique(parentClasses)
		parentClasses <- parentClasses[parentClasses != "root"]

		allClassesPaths[[j]] <- parentClasses
	}

	#Now lets determine the level of each class
	classesPerLevel <- getClassesPerLevel(listAllClassesLevels)

	#Create vector of classes separated per level
	namesColumns <- getNamesColumns(classesPerLevel)
	
	#Index the positions of the classes (allClassesPaths) according to the levels
	indexClassesLevels <- vector("list",length(allClasses))
	indexAllClassesPathsLevels <- vector("list",length(allClasses))
	for(j in 1:length(allClasses)){
  #把allclassespaths里面的标签GO(chr)转换成了index(int)，
		indexClassesLevels[[j]] <- grep(paste(allClassesPaths[[j]],collapse="|"),namesColumns)
		
		indexHead <- grep(listAllClassesLevels[[j]][1],namesColumns)
		indexAllClassesPathsLevels[[j]] <- c(indexAllClassesPathsLevels[[j]],indexHead)

		for(k in 2:length(listAllClassesLevels[[j]])){
			vetClasses <- unlist(strsplit(listAllClassesLevels[[j]][k],"/"))
			vetClasses <- vetClasses[vetClasses != "root"]
			indexesPerLevel <- grep(paste(vetClasses,collapse="|"),namesColumns)
			indexAllClassesPathsLevels[[j]] <- c(indexAllClassesPathsLevels[[j]],paste(indexesPerLevel,collapse="/"))
		}
	}

	#Read the training, validation and test data
	datasetTrain <- read.csv(paste(pathToOriginalData,datasets[i],'/',"traingdata(train).csv",sep=""),header=TRUE,dec='.')
    datasetValid <- read.csv(paste(pathToOriginalData,datasets[i],'/',"traingdata(validate).csv",sep=""),header=TRUE,dec='.')
    #Index of last element of training data
    indexLastTraining <- nrow(datasetTrain)

    #Concatenate train and valid data to generate the files with the same indexes
    dataset <- rbind(datasetTrain,datasetValid)
    # 
    # #Index of last element of valid data
    # indexLastValid <- nrow(dataset)
    # 
    # #Concatenate train, valid and test data to generate the files with the same indexes
    # dataset <- rbind(dataset,datasetTest)

	#Remove useless
    #Will treat these datasets different, as they have only categorical attributes
    #if(datasets[i] == "pheno" || datasets[i] == "hom" || datasets[i] == "struct"){
    #	columns <- vector()
    #   for(col in 1:ncol(dataset)){
    #        if(length(union(dataset[,col],dataset[,col])) == 1){
    #            columns <- c(columns,col)
    #        }
    #    }
    #    if(length(columns) > 0){
    #        dataset <- dataset[,-columns]
    #    }
    #    dataset[dataset==0] = -1
    #}

	#Get all classes for all examples
    nrowDataset <- nrow(dataset)
    ncolDataset <- ncol(dataset)

	#Create dataframe of boolean classes
    #matrix行是4085个数据集，列是已分好level的classes,把matrix转成dataframe，colnames换成已分好level的classes
    dataFrameClasses <- as.data.frame(matrix(data=0,nrow=nrowDataset,ncol=length(namesColumns)))
    colnames(dataFrameClasses) <- namesColumns

	#Set to 1 the positions of the classes of the instances
    #对dataset的最后一列（所属一串GO信息）进行拆分
	for(j in 1:nrowDataset){
		classes <- as.character(dataset[j,ncolDataset])
		if(length(grep("@",classes) > 0)){
			classes <- unlist(strsplit(classes,"@"))
		}
		
		#remove the illegal classes
		posIllegal <- grep(paste(illegalClasses,collapse="|"),classes)
		if(length(posIllegal) > 0){
			classes <- classes[-posIllegal]
		}
	#找到数据集的GO标签在allclasses中的位置，在dataframeclasses中对应位置将全部父类标记为1（通过indexClassesLevels的数据）
		#There are instances that are assigned only to the illegal classes.
		#In this case they will not be classified in any class 	
		if(length(classes) > 0){
			#Set to 1 the class position and the position of all superclasses of the class
		  #allPositions是样本所属类的位置，indexClassesLevels[allPositions]是类的所有父类的索引位置。
			allPositions <- grep(paste(classes,collapse="|"),allClasses)
			#zhangjp
			#zhangjp
			dataFrameClasses[j,unique(unlist(indexClassesLevels[allPositions]))] <- 1
		}
		cat(j,"\n")
	}
    dataset2 <- dataset
	#Concatenate data with boolen classes
    dataset <- dataset[,-ncolDataset]
}
    datasetFinal <- cbind(dataset,dataFrameClasses)
    write.csv(datasetFinal,"datasetFinal.csv",row.names = FALSE)
    #
     #Separate train, valid and test data
    #  datasetFinalTrain <- datasetFinal[1:indexLastTraining,]
    #  colnames(datasetFinalTrain) <- colnames(datasetFinal)
    # #
    #  datasetFinalValid <- datasetFinal[(indexLastTraining+1):indexLastValid,]
    #  colnames(datasetFinalValid) <- colnames(datasetFinal)
    # #
    #  datasetFinalTest <- datasetFinal[(indexLastValid+1):nrow(datasetFinal),]
    #  colnames(datasetFinalTest) <- colnames(datasetFinal)
    # 
    # #Write the data
    # write.csv(datasetFinalTrain,paste(datasets[i],".GO.trainRI.bin.csv",sep=""),row.names=FALSE)
    # write.csv(datasetFinalValid,paste(datasets[i],".GO.validRI.bin.csv",sep=""),row.names=FALSE)
    # write.csv(datasetFinalTest,paste(datasets[i],".GO.testRI.bin.csv",sep=""),row.names=FALSE)

# 	#Write information about classes,形成txt文件
# 	fileDAGrelationships <- paste(datasets[i],".GO.structure.txt",sep="")
#     arq <- file(fileDAGrelationships,open='w')
#     sink(arq)
#     cat(originalData[grep("hierarchical",originalData)])
#     sink()
#     close(arq)	
# 
# 	fileAllClassesLevels <- paste(datasets[i],".AllClassesLevels.txt",sep="")
#     arq <- file(fileAllClassesLevels,open='w')
#     sink(arq)
# 	for(j in 2:length(listAllClassesLevels)){
# 		paths <- gsub("root/","",listAllClassesLevels[[j]])
# 		paths <- gsub("/$","",paths)
# 		paths <- paste(paths,collapse="@")
# 		cat(paths,"\n")		
# 	}
#     sink()
#     close(arq)
# 
# 	fileIndexAllClassesPathsLevels <- paste(datasets[i],".indexAllClassesPathsLevels.txt",sep="")
#     arq <- file(fileIndexAllClassesPathsLevels,open='w')
#     sink(arq)
# 	for(j in 2:length(indexAllClassesPathsLevels)){
# 		indexes <- paste(indexAllClassesPathsLevels[[j]],collapse="@")
# 		cat(indexes,"\n")		
# 	}
#     sink()
#     close(arq)
# }

