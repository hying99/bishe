clear;
clc;
start = 1;
theEnd =204;
%index=[6 8 9 10  11 16:18 22 25 26 36 40 42 44 45 47 48 49 53 56 58 63 64 65 66 68 71 77 85 86 89 90 91 92 93];
tic;
OA = [];
TP = [];
TN = [];
FP = [];
FN = [];
REC = [];
SPE = []; 
PRE = [];
FM = [];
para_search=2;
file_title='5';
validDataFile = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title,'\validdataset.csv'];
validData = csvread(validDataFile);
validLabelFile = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title,'\validclass.csv'];
validLabel = csvread(validLabelFile);

testDataFile = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title,'\testdataset.csv'];
testData = csvread(testDataFile);
testLabelFile = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title,'\testclass.csv'];
testLabel = csvread(testLabelFile);    

decision_test=[];
    
for k = start:theEnd
    
    dd = num2str(k);
    
    fileName = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title, '\' dd '.csv'];
    data = csvread(fileName);

    labels = data(:,end);
    NumOfPos = size(find(labels==1),1);
    NumOfNeg = size(find(labels==0),1);
    colNum=size(data,2);
    data = data(:,1:(colNum-1));

    pos = data(1:NumOfPos,:)';
    neg = data(NumOfPos+1:end,:)';

    trainNumPos= NumOfPos;
    trainNumNeg= NumOfNeg;    

    train = [pos(:,1:trainNumPos) neg(:,1:trainNumNeg)];
   % labels = [ones(1,floor(trainNumPos/3)), zeros(1,floor(trainNumNeg/3))];
    labels = [ones(1,trainNumPos), zeros(1,trainNumNeg)];
   
 
    
    %% 进行一次Mahal-like测度计算
%if(enAble(3)==1 ||enAble(4)==1||enAble(5)==1||enAble(6)==1)
% realNum =TrainNum_List(1,1);    %% $$$
% 
% clsNum = 12;
% 
% S= zeros(clsNum*realNum,clsNum*realNum);
% ss = ones(realNum,realNum);
% for i=1:clsNum
%     S((i-1)*realNum+1:i*realNum,(i-1)*realNum+1:i*realNum) = ss;
% end
% D = ones(clsNum*realNum,clsNum*realNum);
% D = D-S;

%% =================以上是学习马氏距离的过程=======================

%% =================以下是进行聚类分析的过程(20161117添加)=======================
if(0)
Train_Feature2 = train(:,1:floor(trainNumPos));
Train_Feature3 = train(:,trainNumPos+1:trainNumPos+floor(trainNumNeg));
sumOfTrainSamples= size(Train_Feature2,2)+size(Train_Feature3,2);
theOnePos = ones(size(Train_Feature2,2),size(Train_Feature2,2));
theOneNeg = ones(size(Train_Feature3,2),size(Train_Feature3,2));

Train_Feature4 = [Train_Feature2 Train_Feature3];
TT = zeros(sumOfTrainSamples,sumOfTrainSamples);
TT(1:size(Train_Feature2,2),1:size(Train_Feature2,2))=theOnePos;
TT(size(Train_Feature2,2)+1:end,size(Train_Feature2,2)+1:end)=theOneNeg;
C = zeros(sumOfTrainSamples*sumOfTrainSamples/2,4);
count=1;
for i=1:sumOfTrainSamples
    for j=i+1:sumOfTrainSamples
        C(count,1)=i;
        C(count,2)=j;
        C(count,4)=TT(i,j);
        if TT(i,j)>0.5
            C(count,3)=1;
        else
            C(count,3)=-1;
        end
        count = count+1;
    end
end 

global config debug
init_config;
res = zeros(1,1);
tol = config.thresh;
gamma = config.gamma;
gamma=10000;
max_iters = size(C,1);
Comp_dim=77;
[G,slack_vars,Mahal,G0] = logdet_learn_LRK_mex(C, Train_Feature4'*Train_Feature4, Train_Feature4', tol,gamma,max_iters,Comp_dim);
    
    %train = Mahal*train;
    train = Mahal*Train_Feature4;
    labels = [ones(1,size(Train_Feature2,2)), zeros(1,size(Train_Feature3,2))];
    test = (Mahal*testData')';
    %save dataSaved train testData;
else
%     Train_Feature2 = train(:,1:floor(trainNumPos/2));
%     Train_Feature3 = train(:,trainNumPos+1:trainNumPos+floor(trainNumNeg/2));
%     Train_Feature4 = [Train_Feature2 Train_Feature3];
%     train = Train_Feature4;
%     labels = [ones(1,size(Train_Feature2,2)), zeros(1,size(Train_Feature3,2))];
    test=testData;
%     load dataSaved;
end
    %%
    %[train,test,Comp_dim]=GetProjection(train,60,train,test); %KPCA降维
    if(para_search==0)
       [bestfm,bestc] =  SVMcgForClass_liner(labels',train',validLabel(:,k),validData);
       cmd = ['-t 0 -c ', num2str(bestc), ' -b 1'];
    elseif(para_search==1)
      [bestfm,bestc,bestd] =  SVMcgForClass_poly(labels',train',validLabel(:,k),validData);
      %SVMcgForClass_poly(train_label,train,valid_label,valid,cmin,cmax,cstep,dmin,dmax,dstep)
      cmd = ['-t 1 -c ', num2str(bestc),' -d ', num2str(bestd) ' -b 1'];
    elseif(para_search==2)
       [bestfm,bestc,bestg] =  SVMcgForClass(labels',train'); 
       cmd = ['-t 2 -c ',num2str(bestc),' -g ',num2str(bestg) ' -h 0 -b 1'];% RBF
    else
       %cmd = '-t 2 -c 10 -g .8 -h 0 -b 1 ';
        %cmd = '-t 2 -c 1 -g 0.8 -h 0 -b 1 ';
      cmd = '-t 0 -c 5 -b 1';
         %cmd='-t 1 -c 1  -d 1.5 -b 1';
        % cmd='-t 1 -c 1  -d 2.8 -r -0.1 -b 1';
    end    
    fprintf('\n------------第%d次\n',k);
    %cmd=['-t 1 -c 1 -g 0.5 -d 2.8 -r -0.1 -b 1'];
    %cmd = ['-t 2 -c 0.57435 -g 0.035897 -b 1'];
    model = svmtrain(labels',train', cmd);         %此处要求输入的训练数据为n*dim形式，标签是列矩阵
    
    trueLabel = testLabel(:,k);

    [predicted_label, accuracy, decision_values]  = svmpredict(trueLabel, test, model,'-b 1');
    tp=0;
    tn=0;
    fp=0;
    fn=0;

    for i=1:size(trueLabel,1)
        if(predicted_label(i)==1)
          if(trueLabel(i)==1)
              tp=tp+1;
          else
              fp=fp+1;
          end
        else
          if(trueLabel(i)==1)
              fn=fn+1;
          else
              tn=tn+1;
          end
        end
    end
    rec=tp/(tp+fn);
    spe=tn/(tn+fp);
    pre=tp/(tp+fp);
    fm=(2*pre*rec)/(pre+rec);
    OA = [OA;accuracy(1,1)];
    TP = [TP;tp];
    TN = [TN;tn];
    FP = [FP;fp];
    FN = [FN;fn];
    REC = [REC;rec];
    SPE = [SPE;spe];
    PRE = [PRE;pre];
    FM = [FM;fm];
    decision_test=[decision_test decision_values];
   % clear trueLabel testData model train Mahal data;

end
toc;
save newdataset_decision decision_test;

save pre_result PRE; 
save rec_result REC; 
save fm_result FM; 
fid=fopen('1.txt','w');
for i=start:theEnd
    fprintf('第%d 个acc：%f\t rec：%f\t spe：%f\t pre：%f\t fm：%f;\n',i,OA(i-start+1,1),REC(i-start+1,1),SPE(i-start+1,1),PRE(i-start+1,1),FM(i-start+1,1));
    fprintf(fid,'第%d 个acc：%f\t rec：%f\t spe：%f\t pre：%f\t fm：%f;\n',i,OA(i-start+1,1),REC(i-start+1,1),SPE(i-start+1,1),PRE(i-start+1,1),FM(i-start+1,1));
end
fclose(fid);