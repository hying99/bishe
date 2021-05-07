import pandas as pd
import numpy as np
import keras
from keras.models import Sequential
from keras.layers import Dense,Dropout,LSTM  #在这里导入dropout
from keras.optimizers import Adam
from keras.callbacks import EarlyStopping

# input x,y
x_train = np.array(pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/traindataset.csv",header=None))
x_traindata = np.reshape(x_train,(537,1,50))
y_traindata = np.array(pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/trainclass.csv",header=None))
#y_traindata = y_train.reshape(537,1,-1)
x_valid = np.array(pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/validdataset.csv",header=None))
x_validdata = np.reshape(x_valid,(179,1,50))
y_validdata = np.array(pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/validclass.csv",header=None))
#y_validdata = y_valid.reshape(179,1,-1)
x_test= np.array(pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/testdataset.csv",header=None))
x_testdata = np.reshape(x_test,(90,1,50))
y_testdata= np.array(pd.read_csv("C:/Users/1231/Desktop/dataprocessing/data/204dataset1/testclass.csv",header=None))
#y_testdata = y_test.reshape(90,1,-1)

""" level = [12,22,27,40,36,21,19,11,8,5,3]
for num in level:
    input_shape = (50, num)
    print("Build LSTM RNN model ...")
    model = Sequential()
    model.add(layers.Conv2D(3, kernel_size=3, strides=1, padding='same', input_shape=(50, num, 1)))
    model.add(layers.Flatten())
    model.add(layers.Dense(100))
    model.add(layers.LSTM(units=11, dropout=0.05, recurrent_dropout=0.35, return_sequences=False))
    model.add(layers.Dense(units=204, activation="softmax"))

    print("Compiling ...") """
# bulid lstm model
print("Build LSTM RNN model ...")
model = Sequential()
model.add(LSTM(units=11, dropout=0.05, recurrent_dropout=0.5, return_sequences=False))
model.add(Dense(units=300, activation="relu"))
model.add(Dropout(0.4))
model.add(Dense(units=204, activation="sigmoid"))
# Keras optimizer defaults:
# Adam   : lr=0.0001, beta_1=0.9,  beta_2=0.999, epsilon=1e-8, decay=0.
# RMSprop: lr=0.001, rho=0.9,                   epsilon=1e-8, decay=0.
# SGD    : lr=0.01,  momentum=0.,                             decay=0.
adam = Adam(lr=0.01)
model.compile(loss="binary_crossentropy", optimizer=adam, metrics=["binary_accuracy"])
print("Compiling ...")

batch_size = 64  # num of training examples per minibatch
num_epochs = 100

print("Training ...")
model.fit(
    x_traindata,
    y_traindata,
    batch_size=batch_size,
    epochs=num_epochs,
    callbacks=EarlyStopping(patience=5,verbose=1,monitor='loss')
)
model.summary()

print("\nValidating ...")
score, accuracy = model.evaluate(
    x_validdata, y_validdata, batch_size=batch_size, verbose=1
)
print("Dev loss:  ", score)
print("Dev accuracy:  ", accuracy)


print("\nTesting ...")
score, accuracy = model.evaluate(
    x_testdata, y_testdata, batch_size=batch_size, verbose=1
)
print("Test loss:  ", score)
print("Test accuracy:  ", accuracy)

predict_labels = (model.predict(x_testdata) > 0.5).astype('int32')
predict_scores = model.predict(x_testdata)
np.savetxt('new1.csv', predict_labels, delimiter = ',')
# Creates a HDF5 file 'lstm_genre_classifier.h5'

model_filename = "lstm.h5py"
print("\nSaving model: " + model_filename)
model.save(model_filename)

#print(predict_labels.shape())