import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers,Sequential



level = [12,22,27,40,36,21,19,11,8,5,3]
for num in level:
    input_shape = (50, num)
    print("Build LSTM RNN model ...")
    model = Sequential()
    model.add(layers.Conv2D(3, kernel_size=3, strides=1, padding='same', input_shape=(50, num, 1)))
    model.add(layers.Flatten())
    model.add(layers.Dense(100))
    model.add(layers.LSTM(units=11, dropout=0.05, recurrent_dropout=0.35, return_sequences=False))
    model.add(layers.Dense(units=204, activation="softmax"))

    print("Compiling ...")
# Keras optimizer defaults:
# Adam   : lr=0.001, beta_1=0.9,  beta_2=0.999, epsilon=1e-8, decay=0.
# RMSprop: lr=0.001, rho=0.9,                   epsilon=1e-8, decay=0.
# SGD    : lr=0.01,  momentum=0.,                             decay=0.
    opt = tf.train.AdamOptimizer
    model.compile(loss="categorical_crossentropy", optimizer=opt, metrics=["accuracy"])
    model.summary()
'''
print("Training ...")
batch_size = 32  # num of training examples per minibatch
num_epochs = 100

model.fit(
    genre_features.train_X,
    genre_features.train_Y,
    batch_size=batch_size,
    epochs=num_epochs,
)

print("\nValidating ...")
score, accuracy = model.evaluate(
    genre_features.dev_X, genre_features.dev_Y, batch_size=batch_size, verbose=1
)
print("Dev loss:  ", score)
print("Dev accuracy:  ", accuracy)


print("\nTesting ...")
score, accuracy = model.evaluate(
    genre_features.test_X, genre_features.test_Y, batch_size=batch_size, verbose=1
)
print("Test loss:  ", score)
print("Test accuracy:  ", accuracy)

# Creates a HDF5 file 'lstm_genre_classifier.h5'
model_filename = "lstm_genre_classifier_lstm.h5"
print("\nSaving model: " + model_filename)
model.save(model_filename)
'''