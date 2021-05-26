import pandas as pd
import numpy as np
import keras
from keras.models import Sequential
from keras.layers import Dense,LSTM #在这里导入dropout
from keras.optimizers import Adam
from keras.callbacks import EarlyStopping
import time
from numpy.random import seed
from keras.wrappers.scikit_learn import KerasClassifier
from sklearn.metrics import make_scorer,f1_score
from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import KFold
from sklearn.preprocessing import LabelEncode
seed(1)
import tensorflow as tf
tf.random.set_seed(4)
physical_devices = tf.config.list_physical_devices('GPU') 
tf.config.experimental.set_memory_growth(physical_devices[0], True)
import matplotlib
from matplotlib import pyplot as plt
# lstm自动学习
start_time = time.time()
# input x,y
a = '5'
x_train = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/traindataset.csv"),header=None))
#x_traindata = np.repeat(x_train, repeats=11, axis=0)
x_traindata = np.reshape(x_train,(-1,1,50))
y_traindata = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/trainclass.csv"),header=None))
#y_traindata = np.repeat(y_train, repeats=11, axis=0)
#y_traindata = y_traindata.reshape(-1,204)

x_valid = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/validdataset.csv"),header=None))
#x_validdata = np.repeat(x_valid, repeats=11, axis=0)
x_validdata = np.reshape(x_valid,(-1,1,50))
y_validdata = np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/validclass.csv"),header=None))
#y_validdata = np.repeat(y_valid, repeats=11, axis=0)
#y_validdata = y_validdata.reshape(-1,204)

x_test= np.array(pd.read_csv(("C:/Users/1231/Desktop/dataprocessing/data/204dataset"+a+"/testdataset.csv"),header=None))
#x_testdata = np.repeat(x_test, repeats=11,axis=0)
x_testdata = np.reshape(x_test,(-1,1,50))
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
def create_model(neurons=200,lr=0.005):
    model = Sequential()
    model.add(LSTM(units=neurons,return_sequences=False))
    #model.add(Dropout(0.1))
    #model.add(Dense(units=300))
    model.add(Dense(units=204,activation="sigmoid"))
    # Keras optimizer defaults:
    # Adam   : lr=0.0001, beta_1=0.9,  beta_2=0.999, epsilon=1e-8, decay=0.
    # RMSprop: lr=0.001, rho=0.9,                   epsilon=1e-8, decay=0.
    # SGD    : lr=0.01,  momentum=0.,                             decay=0.
    adam = Adam(lr=lr)
    model.compile(loss="binary_crossentropy", optimizer=adam, metrics=["binary_accuracy"])
    return model
model = KerasClassifier(build_fn=create_model,epochs=100,batch_size=32)
grid = {}
grid['neurons']=range(200,1200,100)
grid['lr']=[0.005,0.008,0.01,0.02,0.05,0.1]
kfold = KFold(n_splits=5,shuffle=True)
scorer = make_scorer(f1_score,average='macro')  #用不了不知道为什么.......
# acc_scorer = make_scorer(accuracy_score)
grid_search = GridSearchCV(estimator=model,param_grid=grid,scoring=scorer,cv=kfold)
results = grid_search.fit(x_testdata,y_testdata)

print('Best:%f using %s'%(results.best_score_,results.best_params_))

means = results.cv_results_['mean_test_score']
stds = results.cv_results_['std_test_score']
params = results.cv_results_['params']
for mean,std,param in zip(means,stds,params):
    print('%f(+-%f) with: %r'%(mean,std,param))
