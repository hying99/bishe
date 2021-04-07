import torch
import torch.nn as nn
import torchvision
import torchvision.transforms as transforms
import pandas as pd

# Device configuration
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# Hyper-parameters 
input_size = 50
hidden_size = 20
num_classes = 204
num_epochs = 5
batch_size = 100
learning_rate = 0.001

# MNIST dataset 
train_dataset = pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/traindataset.csv")
train_class = pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/trainclass.csv")

test_dataset = pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/testdataset.csv")
test_class = pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/testclass.csv")


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
criterion = nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.parameters(), lr=learning_rate)  

# Train the model
for epoch in range(num_epochs):
    for data in train_dataset:  
        for labels in train_class:
        # Move tensors to the configured device
            data = data.to(device)
            labels = labels.to(device)
        
        # Forward pass
            outputs = model(data)
            loss = criterion(outputs, labels)
        
        # Backward and optimize
            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
        
            if (i+1) % 100 == 0:
                print ('Epoch [{}/{}], Step [{}/{}], Loss: {:.4f}' 
                   .format(epoch+1, num_epochs, i+1, total_step, loss.item()))

# Test the model
# In test phase, we don't need to compute gradients (for memory efficiency)
with torch.no_grad():
    correct = 0
    total = 0
    for data in train_dataset:  
        for labels in train_class:
            data = data.to(device)
            labels = labels.to(device)
            outputs = model(data)
            _, predicted = torch.max(outputs.data, 1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()

    print('Accuracy of the network on the 10000 test images: {} %'.format(100 * correct / total))

# Save the model checkpoint
torch.save(model.state_dict(), 'model.ckpt')