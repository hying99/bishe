import numpy as np
import pandas as pd

class biodata():
    def data_loader(self):
        self.traindata = np.array(pd.read_csv("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\61dataset1\\traindataset.csv"))
        self.trainclass = np.array(pd.read_csv("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\61dataset1\\trainclass.csv"))
        self.validdata = np.array(pd.read_csv("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\61dataset1\\validdataset.csv"))
        self.validclass = np.array(pd.read_csv("C:\\Users\\1231\\Desktop\\dataprocessing\\data\\61dataset1\\validclass.csv"))
        self.