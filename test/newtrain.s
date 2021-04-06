
[General]
Compatibility = MLJ08

[Data]
File = newtrain.arff
PruneSet = newvalid.arff
TestSet = newtest.arff

[Attributes]
ReduceMemoryNominalAttrs = yes

[Hierarchical]
Type = DAG
WType = ExpAvgParentWeight
HSeparator = /
%ClassificationThreshold = [20,50,80]
%SingleLabel = yes

[Tree]
Optimize = {NoClusteringStats, NoINodeStats}
ConvertToRules = No
%FTest = [0.001,0.005,0.01,0.05,0.1,0.125]
 FTest = 0.001

[Model]
MinimalWeight = 5.0

[Output]
WritePredictions = Test