import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import json
from datetime import datetime
from pymongo import MongoClient
import seaborn as sns
sns.set_style("whitegrid")

data_path = "./data/"

# def get_data_from_mongodb(eco):
#     client = MongoClient(host='35.192.54.171:27017')
#     db = client['metrics']
#     collection = db[eco]
#     val = collection.find()

def benchmarking_plot(title, attribute, yLabel, boxplot=False):
    data = data_for_plot(open(data_path + "microk8s-idle.json").read(), attribute, True, 'microk8s')
    data2 = data_for_plot(open(data_path + "k3s-idle.json").read(), attribute, False, 'k3s')
    ax = None

    if boxplot:
        data3 = pd.concat([data, data2])
        ax = sns.boxplot(x="eco", y="value", data=data3)
        ax.set(xlabel='Eco')
    else:
        ax = sns.lineplot(data=data, x='time', y='value', label="mircok8s")
        sns.lineplot(data=data2, x='time', y='value', label="k3s")
        plt.legend()
        ax.set(xlabel='Time in Seconds')

    ax.set(ylabel=yLabel, title=title)
    plt.show()

def data_for_plot(json_data, attribute, calc, eco):
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

    return pd.DataFrame({'time': timestamps, 'value': values, 'eco': [eco] * len(values)})


if __name__ == '__main__':
    benchmarking_plot('CPU over time', 'CPU', 'CPU in Percentage')
    benchmarking_plot('CPU over time', 'CPU', 'CPU in Percentage', boxplot=True)
    # benchmarking_plot('Memory usage over time', 'MEM_USED', 'Mem in MiB')
    # benchmarking_plot('File IO total read', 'FIO_TOTAL_READ', 'Reads in Percentage')
    # benchmarking_plot('Fiel IO total write', 'FIO_TOTAL_WRITE', 'Writes in Percentage')
