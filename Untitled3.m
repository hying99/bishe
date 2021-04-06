clear;
clc;
times=237;
tic;
OA = [];
for k = 1:times
    count = k;
    dd = num2str(count);
    fileName = ['F:\SVMs\283�ļ�\283�ļ�\' dd '.csv'];
    data = csvread(fileName,1, 1);
%remain = [1 2 3 7 8 19 23 27 29 33 38 44 45 46 47 49:52 55 56 59:62 64 67:71];

% 60+,89-;
%data = csvread('C:\Users\Administrator\Desktop\unNamed\go0002181.csv',1, 1);
labels = data(:,end);
NumOfPos = size(find(labels==1),1);
NumOfNeg = size(find(labels==2),1);
data = data(:,1:76);
%data = data(:,remain);
pos = data(1:NumOfPos,:)';
neg = data(NumOfPos+1:end,:)';

trainNumPos= NumOfPos/2;
trainNumNeg= NumOfNeg/2;
%trainNumNeg = trainNumPos*2;
train = [pos(:,1:trainNumPos) neg(:,1:trainNumNeg)];
labels = [ones(1,trainNumPos) (-1)*ones(1,trainNumNeg)];
test = [pos(:,trainNumPos+1:end) neg(:,trainNumNeg+1:end)];
testLabel = [ones(1,NumOfPos-trainNumPos) (-1)*ones(1,NumOfNeg-trainNumNeg)];

%[train,test,Comp_dim]=GetProjection(train,60,train,test); %KPCA��ά
%[bestCVaccuracy,bestc,bestg] =  SVMcgForClass(labels',train');
%cmd = ['-t 2 -c ',num2str(bestc),' -g ',num2str(bestg)];
cmd = ['-t 2 -c 100 -g .05 -b 0'];
model = svmtrain(labels',train', cmd);         %����KNMF
[predicted_label, accuracy, decision_values]  = svmpredict(testLabel', test', model,'-b 0');
fprintf('��ǩ%d ��ʼ����:%f',k,accuracy(1,1));
%pause;
OA = [OA;accuracy(1,1)];
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
    if testLabel(1,indexs(i))*testLabel(1,indexs(i+1))<0%ָ���߱�ǩ��ͬ
%         d1 = decision_values(indexs(i));
%         d2 = decision_values(indexs(i+1));
%        if decision_values(indexs(i),1)*decision_values(indexs(i+1),1)<0%ָ���߸��۲�ͬ
            poolOfThreshold = [poolOfThreshold ;(decision_values(i,1)+decision_values(i+1,1))/2];%������
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
%�����ж�������S����
    for j=1:S-1  
        preLabs{j}=predicted_label(1+(j-1)*nums:j*nums,1);%Ԥ���ǩ
        trueLabel{j} = trueLabels(1+(j-1)*nums:j*nums,1);%��ʵ��ǩ
        decisions{j} = decision_values(1+(j-1)*nums:j*nums,1);%Ԥ��ֵ
    end
    preLabs{j+1}=predicted_label(j*nums+1:end,1);%Ԥ���ǩ
    trueLabel{j+1} = testLabel(j*nums+1:end,1);%��ʵ��ǩ
    decisions{j+1} = decision_values(j*nums+1:end,1);%Ԥ��ֵ
%% step3    
for i=1:S
    maxFm= 0;
    fitableThres = 0;
    for j=1:size(poolOfThreshold,1)% ��ȡ��һ����ֵ��
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
        %% ��OA��
        if(1)
           
            haha = prelab-trueLab;
            OA = size(find(haha==0),1)/size(prelab,1);
            if OA>maxFm
                maxFm = OA;
                index = j;
                fitableThres = curThreshold;
            end
        else
        %% ��Fm��
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
% fprintf('OA�����󾫶�');
% final = size(q,1)/size(test,2)
%pause;
end
toc;
for i=1:237
    fprintf('��%d ����ǩ�ķ��ྫ�ȣ�%f;\n',i,OA(i,1));
end