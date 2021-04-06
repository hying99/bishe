clear;
clc;
start = 1;
theEnd =204;
tic;
OA = [];
file_title='1';
testDataFile = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title,'\testdataset.csv'];
    testData = csvread(testDataFile);
testLabelFile = ['C:\Users\1231\Desktop\dataprocessing\data\','204dataset',file_title,'\testclass.csv'];
    testLabel = csvread(testLabelFile);
    decision=[];
for k = start:theEnd
    count = k;
    dd = num2str(count);
    fileName = ['C:\Users\1231\Desktop\dataprocessing\data\' ,'204dataset',file_title,'\',dd,'.csv'];
    data = csvread(fileName);
%remain = [1 2 3 7 8 19 23 27 29 33 38 44 45 46 47 49:52 55 56 59:62 64 67:71];

% 60+,89-;
%data = csvread('C:\Users\Administrator\Desktop\unNamed\go0002181.csv',1, 1);
labels = data(:,end);
NumOfPos = size(find(labels==1),1);
NumOfNeg = size(find(labels==0),1);
data = data(:,1:50);
%data = data(:,remain);
pos = data(1:NumOfPos,:)';
neg = data(NumOfPos+1:end,:)';

trainNumPos= NumOfPos;

trainNumNeg= NumOfNeg;
% if trainNumPos>trainNumNeg
%     trainNumPos = floor(trainNumNeg*0.6);
% else if trainNumNeg>trainNumPos*2
%         trainNumNeg=floor(trainNumPos*1.3);
%     end
% end

% if trainNumPos>trainNumNeg
%     trainNumPos = trainNumNeg;
% else if trainNumNeg>trainNumPos
%         trainNumNeg=trainNumPos;
%     end
% end
%trainNumNeg = trainNumPos*2;
train = [pos(:,1:trainNumPos) neg(:,1:trainNumNeg)];
labels = [ones(1,trainNumPos) zeros(1,trainNumNeg)];
%test = [pos(:,trainNumPos+1:end) neg(:,trainNumNeg+1:end)];
%testLabel = [ones(1,NumOfPos-trainNumPos) zeros(1,NumOfNeg-trainNumNeg)];

%[train,test,Comp_dim]=GetProjection(train,60,train,test); %KPCA降维
%[bestCVaccuracy,bestc,bestg] =  SVMcgForClass(labels',train');
%cmd = ['-t 2 -c ',num2str(bestc),' -g ',num2str(bestg)];
 cmd = ['-t 2 -c 10 -g .8 -b 1'];
%cmd = ['-t 2 -c 16 -g 0.0039 -b 1'];
model = svmtrain(labels',train', cmd);         %基于KNMF
trueLabel = testLabel(:,k);
%trueLabel = (trueLabel-1)*(-1);
[predicted_label, accuracy, decision_values]  = svmpredict(trueLabel, testData, model,'-b 1');
% res = [predicted_label'; trueLabel'];
% plot(predicted_label);
% plot(trueLabel);
% fprintf('标签%d 初始精度:%f',k,accuracy(1,1));
%pause;
OA = [OA;accuracy(1,1)];
decision=[decision decision_values];
continue;
accuracy
fffff = decision_values;
predicted_labe2l = predicted_label';
[ddd indexs] = sort(decision_values,'descend');
%predicted_label = predicted_label';
poolOfThreshold = [];
%% step1
for i=1:size(testLabel,2)-1
    ind1 = indexs(i);
    ind2 = indexs(i+1);
    L1 = testLabel(1,indexs(i));
    L2 = testLabel(1,indexs(i+1));
    if testLabel(1,indexs(i))*testLabel(1,indexs(i+1))<0%指两者标签相同
%         d1 = decision_values(indexs(i));
%         d2 = decision_values(indexs(i+1));
%        if decision_values(indexs(i),1)*decision_values(indexs(i+1),1)<0%指两者概论不同
            poolOfThreshold = [poolOfThreshold ;(decision_values(i,1)+decision_values(i+1,1))/2];%把两个
%        end
    end
        
end
%% step2
newIndex = randperm(size(test,2));
testLabel = testLabel';
predicted_label = predicted_label(newIndex,1);
trueLabels = testLabel(newIndex,1);
decision_values = decision_values(newIndex,1);
S = 4;
nums = floor(size(testLabel,1)/5);
scores=zeros(size(poolOfThreshold,1),1);
%把所有东西分入S个组
    for j=1:S-1  
        preLabs{j}=predicted_label(1+(j-1)*nums:j*nums,1);%预测标签
        trueLabel{j} = trueLabels(1+(j-1)*nums:j*nums,1);%真实标签
        decisions{j} = decision_values(1+(j-1)*nums:j*nums,1);%预测值
    end
    preLabs{j+1}=predicted_label(j*nums+1:end,1);%预测标签
    trueLabel{j+1} = testLabel(j*nums+1:end,1);%真实标签
    decisions{j+1} = decision_values(j*nums+1:end,1);%预测值
%% step3    
for i=1:S
    maxFm= 0;
    fitableThres = 0;
    for j=1:size(poolOfThreshold,1)% 提取出一个阈值来
        curThreshold = poolOfThreshold(j,1);
        % ?????
        prelab = preLabs{i};
        trueLab = trueLabel{i};
        decis = decisions{i};
        [AA BB] = find(decisions{i}>curThreshold);
        prelab(AA)=100;
        [CC DD] = find(decisions{i}<=curThreshold);
        prelab(CC)=-100;
        trueLab = trueLab*100;
         posIndex = find(trueLab==100);
            shouldBPos = prelab(posIndex,1);
        %% 按OA算
        if(1)
           
            haha = prelab-trueLab;
            OA = size(find(haha==0),1)/size(prelab,1);
            if OA>maxFm
                maxFm = OA;
                index = j;
                fitableThres = curThreshold;
            end
        else
        %% 按Fm算
           TP = size(find(shouldBPos>0),1);
           FN = size(find(shouldBPos<0),1);
            negIndex = find(trueLab==-100);
            shouldBNeg = prelab(negIndex,1);
            FP = size(find(shouldBNeg<0),1);
           TN = size(find(shouldBNeg>0),1);
           Rec = TP/(TP+FN);
           Pre = TP/(TP+FP);
           Fbeta = 2*Pre*Rec/(Pre+Rec);

            if Fbeta>maxFm
                maxFm = Fbeta;
                index = j;
                fitableThres = curThreshold;
            end
        end
        
    end
    scores(index,1) =  scores(index,1)+1;
end


% [a,b]=max(scores);
% bestThreshold = poolOfThreshold(b,1);
%     %bestThreshold = 0.3;
% [a b]=find(fffff>bestThreshold);
% [c d]=find(fffff<bestThreshold);
% refinelab = zeros(size(test,2),1);
% refinelab(a,1)=1;
% refinelab(c,1)=-1;
% temp = refinelab-testLabel;
% [q w] = find(temp==0);
% fprintf('OA修正后精度');
% final = size(q,1)/size(test,2)
%pause;
end
toc;
save decision decision;
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
rec=tp/(tp+fn)
spe=tn/(tn+fp)
pre=tp/(tp+fp)
fm=(2*pre*rec)/(pre+rec)
for i=start:theEnd
    fprintf('第%d 个标签的分类精度：%f;\n',i,OA(i-start+1,1));
end