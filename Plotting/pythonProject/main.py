# This is a sample Python script.

# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.

import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import json
from datetime import datetime
from pandas import DataFrame
from pymongo import MongoClient


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press ⌘F8 to toggle the breakpoint.

    # client = MongoClient(host='35.192.54.171')
    # db = client['metrics']
    # collection = db['metrics_collection_new']
    # val = collection.find()
    data = data_for_plot(open("data.json").read())
    data2 = data_for_plot(open("data_k3s.json").read())

    plt.plot(data['value'], label="microk8s")
    plt.xticks(range(len(data['time'])), data['time'])
    plt.xticks(rotation=30)

    plt.plot(data2['value'], label="k3s")
    plt.xticks(range(len(data2['time'])), data2['time'])
    plt.xticks(rotation=30)
    plt.title('CPU over Time')
    plt.legend()
    plt.show()


def data_for_plot(json_data):
    # json_data: str = open("data.json").read()
    parsed_json = (json.loads(json_data))
    values = []
    timestamps = []
    for x in parsed_json:
        datetime_object = datetime.strptime(x['timestamp'], '%a %b %d %H:%M:%S %Z %Y').strftime('%H:%M:%S')
        timestamps.append(datetime_object)
        values.append(x['CPU'])
    data = {'value': values,
            'time': timestamps}
    return data


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
