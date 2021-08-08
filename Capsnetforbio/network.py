import gensim
from keras.constraints import max_norm
from keras.models import load_model
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM
from keras.layers import Input, Dense, Embedding, Conv2D, MaxPool2D
from keras.layers import Reshape, Flatten, Dropout, Concatenate
from keras.optimizers import Adam, RMSprop, Adagrad
from keras.models import Model
import keras.losses
from keras.layers import Embedding
from keras import layers, models
import numpy as np
import os
import math
from capsulelayers import CapsuleLayer, PrimaryCap, Length
from keras import backend as K
import sys
import pickle
from sklearn.preprocessing import MultiLabelBinarizer
ml = MultiLabelBinarizer()

def create_model_capsule( sequence_length,init_layer,  learning_rate=0.01,  routings=3,dense_capsule_dim=16,num_classes=204):
    """
    Implementation of capsule network
    """
    over_time_conv = 3

    inputs = Input(shape=(sequence_length,), dtype='int32')

    primarycaps = PrimaryCap(inputs, dim_capsule=8, n_channels= 50, kernel_size=over_time_conv,
    strides=2, padding='valid', name = 'primarycaps')

    dense = CapsuleLayer(num_capsule=num_classes, dim_capsule=dense_capsule_dim, routings=routings,
                             name='digitcaps')(primarycaps)

    out_caps = Length(name='capsnet')(dense)
    model = Model(inputs=inputs, outputs=out_caps)

    model.compile(optimizer=Adam(lr=learning_rate),
                  loss=[margin_loss],
                  metrics=['categorical_accuracy'])


    #initilizes the weight of transformation matrix W
    if init_layer:
        weights = model.layers[-2].get_weights()[0]
        co_occurences = co_occurence_weights(weights[0].shape[1], num_classes)
        print(len(co_occurences), len(co_occurences[0]))
        for i, co_occurence in enumerate(co_occurences):
            if i >= weights.shape[1]:
                break
            for j, weight in enumerate(co_occurence):
                #initilzes the  weights between dim of primary and one complete  dense capsule
                weights[j][i][0] = weights[j][i][0] if weight != 0 else 0
                #weights[j][i][0][0] = weight if weight != 0 else weights[j][i][0][0]
        model.layers[-2].set_weights([weights])
    print(model.summary())
    return model


def margin_loss(y_true, y_pred):
    """
    Margin loss as described in Sabour et al. (2017)
    """
    L = y_true * K.square(K.maximum(0., 0.9 - y_pred)) + \
        0.5 * (1 - y_true) * K.square(K.maximum(0., y_pred - 0.1))

    return K.mean(K.sum(L, 1))


def co_occurence_weights(num_units, num_classes):
    """
     loads the co-occurence matrix with respective weights
    """
    parent_child = []
    w = math.sqrt(6) / math.sqrt(num_units + num_classes)
    _, occurences = data_loader.read_all_genres()

    for occurence in occurences:
        if occurence[0].issubset(set(ml.classes_)):
            frequency = occurence[1]
            w_f = w # * math.sqrt(frequency)
            binary_rel = ml.transform([occurence[0]])
            parent_child.append([w_f if x==1 else 0 for x in binary_rel[0]])
    print(len(occurences))
    return parent_child