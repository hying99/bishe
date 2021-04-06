clear;
clc;
[train_label,train_data] = libsvmread('F:\IE downloads\yeast_train.svm\yeast_train.svm')
model = svmtrain(train_label,train_data,'-t 2 -c 100 -g .05 -b 0')
%  SPECTF = csvread('SPECTF.train'); % read a csv file
%  labels = SPECTF(:, 1); % labels from the 1st column
%  features = SPECTF(:, 2:end); 
%  features_sparse = sparse(features); % features must be in a sparse matrix
%  libsvmwrite('SPECTFlibsvm.train', labels, features_sparse);