# This is a sample Python script.

# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.

import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from pandas import DataFrame
from pymongo import MongoClient


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press ⌘F8 to toggle the breakpoint.

    # client = MongoClient(host='35.192.54.171')
    # db = client['metrics']
    # collection = db['metrics_collection_new']
    # val = collection.find()
    data = {'value': [1, 2, 3, 4, 5],
            'time': ['10:00', '10:10', '10:20', '10:30', '10:40']}
    plt.plot(data['value'])
    plt.xlabel("Time")
    plt.ylabel("Value")
    plt.xticks(range(len(data['time'])), data['time'])

    plt.show()


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
