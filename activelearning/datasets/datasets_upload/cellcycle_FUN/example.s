[General]
Compatibility = MLJ08
Verbose = -1

[Data]
File = labelled.arff
TestSet = test.arff

[Active]
ActiveDataset = unlabelled.arff
BudgetPerIteration = 10
ActiveAlgorithm = Random
BatchSize = 10
LabelCost = [1,1,1,1,1,1]
WriteActiveTestError = True
Iteration = 3
OptimizingIterations = 50
PopulationSize = 25
Alpha = 0.9

[Hierarchical]
Type = TREE
HSeparator = /
ClassificationThreshold = [0,2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,66,68,70,72,74,76,78,80,82,84,86,88,90,92,94,96,98,100]


[Ensemble]
EnsembleMethod = RForest
Iterations = 50 

[Model]
MinimalWeight = 5.0
