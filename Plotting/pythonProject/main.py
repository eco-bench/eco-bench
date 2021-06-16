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


def benchmarking_plot(title, attribute):
    # client = MongoClient(host='35.192.54.171')
    # db = client['metrics']
    # collection = db['metrics_collection_new']
    # val = collection.find()
    data = data_for_plot(open("micro_data.json").read(), attribute, True)
    data2 = data_for_plot(open("k3s_data2.json").read(), attribute, False)

    plt.plot(data['time'], data['value'], label="microk8s")
    plt.plot(data2['time'], data2['value'], label="k3s")

    plt.xticks(range(0,30))
    plt.title(title)
    plt.legend()
    plt.show()

def data_for_plot(json_data, attribute, calc):
    parsed_json = (json.loads(json_data))
    values = []
    timestamps = []
    last_datetime = datetime.strptime(parsed_json[0]['timestamp'], '%a %b %d %H:%M:%S %Z %Y')

    for index, x in enumerate(parsed_json):
        datetime_object = datetime.strptime(x['timestamp'], '%a %b %d %H:%M:%S %Z %Y')
        datetime_diff = int((datetime_object-last_datetime).total_seconds())
        timestamps.append(datetime_diff)

        att = float(x[attribute])
        if (attribute == 'MEM_USED' and calc):
            att = att / 1000

        values.append(att)

    data = {'value': values,
            'time': timestamps}

    return data

if __name__ == '__main__':
    benchmarking_plot('CPU over time', 'CPU')
    benchmarking_plot('Memory usage over time', 'MEM_USED')
