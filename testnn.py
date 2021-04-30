import numpy as np
import torch
import torch.nn as nn
import torchvision
import torchvision.transforms as transforms
import keras

# Device configuration
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# Hyper-parameters 
input_size = 50
hidden_size = 20
num_classes = 204
num_epochs = 5
batch_size = 16
learning_rate = 0.001

# load dataset 
trainclasspath = "C:/Users/1231/Desktop/dataprocessing/data/204dataset1/trainclass.csv"
trainclass = np.loadtxt(trainclasspath, dtype=np.float32, delimiter=",")
traindatasetpath = "C:/Users/1231/Desktop/dataprocessing/data/204dataset1/traindataset.csv"
traindataset = np.loadtxt(traindatasetpath, dtype=np.float32, delimiter=",")

testclasspath = "C:/Users/1231/Desktop/dataprocessing/data/204dataset1/testclass.csv"
testclass = np.loadtxt(testclasspath, dtype=np.float32, delimiter=",")
testdatasetpath = "C:/Users/1231/Desktop/dataprocessing/data/204dataset1/testdataset.csv"
testdataset = np.loadtxt(testdatasetpath, dtype=np.float32, delimiter=",")

trainclass = torch.from_numpy(trainclass)
traindataset = torch.from_numpy(traindataset)
testclass = torch.from_numpy(testclass)
testdataset = torch.from_numpy(testdataset)

features = traindataset
labels = trainclass

# Fully connected neural network with one hidden layer
class NeuralNet(nn.Module):
    def __init__(self, input_size, hidden_size, num_classes):
        super(NeuralNet, self).__init__()
        self.fc1 = nn.Linear(input_size, hidden_size) 
        self.relu = nn.ReLU()
        self.fc2 = nn.Linear(hidden_size, num_classes)  
    
    def forward(self, x):
        out = self.fc1(x)
        out = self.relu(out)
        out = self.fc2(out)
        return out

model = NeuralNet(input_size, hidden_size, num_classes).to(device)

# Loss and optimizer
criterion=nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.parameters(), lr=learning_rate)  

# Train the model
for epoch in range(num_epochs):
    for feature in features:  
        for label in labels:
        # Move tensors to the configured device
            feature = feature.to(device)
            label = label.to(device)
        
        # Forward pass
            output = model(feature)
            loss = criterion(output, label)
        
        # Backward and optimize
            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
        
        if (i+1) % 100 == 0:
                print ('Epoch [{}/{}], Step [{}/{}], Loss: {:.4f}' 
                   .format(epoch+1, num_epochs, i+1, total_step, loss.item()))
