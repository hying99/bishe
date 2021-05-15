import pandas as pd
import numpy as np
import keras
from keras.models import Sequential
from keras.layers import Dense,Dropout,LSTM,GRU  #在这里导入dropout
from keras.optimizers import Adam,SGD,RMSprop
from keras.callbacks import EarlyStopping
import time
from numpy.random import seed
from tensorflow.python.keras.layers.recurrent_v2 import GRU
seed(1)
import tensorflow as tf
tf.random.set_seed(12)
physical_devices = tf.config.list_physical_devices('GPU') 
tf.config.experimental.set_memory_growth(physical_devices[0], True)
# 按lstm人为构造时序
start_time = time.time()
# input x,y
a = '1'
x_train = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/traindataset.csv"),header=None))
x_traindata = np.repeat(x_train, repeats=6, axis=0)
x_traindata = np.reshape(x_traindata,(-1,6,50))
y_traindata = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/trainclass.csv"),header=None))
#y_traindata = np.repeat(y_train, repeats=11, axis=0)
#y_traindata = y_traindata.reshape(-1,204)

x_valid = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/validdataset.csv"),header=None))
x_validdata = np.repeat(x_valid, repeats=6, axis=0)
x_validdata = np.reshape(x_validdata,(-1,6,50))
y_validdata = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/validclass.csv"),header=None))
#y_validdata = np.repeat(y_valid, repeats=11, axis=0)
#y_validdata = y_validdata.reshape(-1,204)

x_test= np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/testdataset.csv"),header=None))
x_testdata = np.repeat(x_test, repeats=6,axis=0)
x_testdata = np.reshape(x_testdata,(-1,6,50))
y_testdata= np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/testclass.csv"),header=None))
#y_testdata = np.repeat(y_test, repeats=11, axis=0)
#y_testdata = y_testdata.reshape(-1,204)

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
model.add(LSTM(units=1000,return_sequences=False))
#model.add(Dropout(0.1))
#model.add(Dense(units=300))
model.add(Dense(units=204,activation="sigmoid"))
# Keras optimizer defaults:
# Adam   : lr=0.0001, beta_1=0.9,  beta_2=0.999, epsilon=1e-8, decay=0.
# RMSprop: lr=0.001, rho=0.9,                   epsilon=1e-8, decay=0.
# SGD    : lr=0.01,  momentum=0.,                             decay=0.
adam = Adam(lr=0.008)
model.compile(loss="binary_crossentropy", optimizer=adam, metrics=["binary_accuracy"])
print("Compiling ...")

batch_size = 1 # num of training examples per minibatch
num_epochs = 100

print("Training ...")
model.fit(
    x_traindata,
    y_traindata,
    batch_size=batch_size,
    shuffle=False,
    validation_data=(x_validdata,y_validdata),
    epochs=num_epochs,
    callbacks=EarlyStopping(patience=5,verbose=1,monitor='val_loss',mode='auto')
)

end_time = time.time()
print('Running time: %s Seconds'%(end_time-start_time))

model.summary()

print("\nValidating ...")
score, accuracy = model.evaluate(
    x_validdata, y_validdata, batch_size=batch_size, 
    verbose=1
)
print("Dev loss:  ", score)
print("Dev accuracy:  ", accuracy)


print("\nTesting ...")
score, accuracy = model.evaluate(
    x_testdata, y_testdata, batch_size=batch_size, 
    verbose=1
)
print("Test loss:  ", score)
print("Test accuracy:  ", accuracy)

predict_labels = (model.predict(x_testdata) > 0.5).astype('int32')
predict_scores = model.predict(x_testdata)
np.savetxt(('new'+a+'.csv'), predict_labels, delimiter = ',')
np.savetxt(('new'+a+'prob.csv'),predict_scores,delimiter=',') 
# Creates a HDF5 file 'lstm_genre_classifier.h5'

model_filename = "lstm.h5py"
print("\nSaving model: " + model_filename)
model.save(model_filename) 

#print(predict_labels.shape())