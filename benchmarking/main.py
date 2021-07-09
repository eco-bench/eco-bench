import json
import os
from datetime import datetime
from typing import TextIO

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from pymongo.collection import Collection
from pymongo.database import Database
from ssh_pymongo import MongoSession

sns.set_style("whitegrid")

data_path = "./data/"

def get_data_from_mongodb(eco, user, ssh_key_path, mongo_db_ip):
    session = MongoSession(
        host=mongo_db_ip,
        user=user,
        key=ssh_key_path,
        uri='mongodb://127.0.0.1:27017')

    db: Database = session.connection['metrics']
    mycol: Collection = db[eco]
    cursor = mycol.find({})
    type_documents_count = mycol.find({}).count()
    file: TextIO
    with open(f'data/{eco}-application.json', 'w') as file:
        file.write('[')
        document: object
        for i, document in enumerate(cursor, 1):
            document.pop('_id')
            file.write(json.dumps(document, default=str))
            if i != type_documents_count:
                file.write(',')
        file.write(']')
    session.stop()

def fixValues(timestamps, values, eco):
    times = [x for x in range(30)]
    for i, time in enumerate(timestamps):
        if time != times[i]:
            timestamps.insert(i, times[i])
            values.insert(i, None)
            eco.append(eco[0])
    return timestamps, values, eco

def benchmarking_plot(title , attribute, yLabel, boxplot=False):
    timestamps1, values1, eco1 = data_for_plot(open(data_path + "microk8s-idle.json").read(), attribute, False, 'microk8s') # When MEM_USED put on True
    timestamps2, values2, eco2 = data_for_plot(open(data_path + "k3s-idle.json").read(), attribute, False, 'k3s')

    timestamps1, values1, eco1 = fixValues(timestamps1, values1, eco1)
    timestamps2, values2, eco2 = fixValues(timestamps2, values2, eco2)
    
    data1 = pd.DataFrame({'microk8s': values1, 'time': timestamps1})
    data2 = pd.DataFrame({'k3s': values2, 'time': timestamps2})
    data3 = data1.merge(data2, how='left', on='time')
    data3 = data3.drop(['time'], axis=1)

    ax = None

    if boxplot:
        ax = sns.boxplot(data=data3)
        ax.set(xlabel='Eco')
    else:
        data3 = data3.interpolate('linear')
        ax = sns.lineplot(data=data3)
        ax.set(xlabel='Time in Seconds')

    ax.set(ylabel=yLabel, title=title)

    dir_name = 'results'
    if not os.path.exists(dir_name):
        os.mkdir(dir_name)

    fig = ax.get_figure()
    fig.savefig(dir_name + '/' + attribute + '.png')
    plt.clf()

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

    return (timestamps, values, [eco] * len(values))

if __name__ == '__main__':
    # user = os.environ['SERVER_USER']
    # ssh_key_path = os.environ['SSH_KEY']
    # mongo_db_ip =  os.environ['MONGODB_IP']
    # get_data_from_mongodb('k3s', user, ssh_key_path, mongo_db_ip)

    benchmarking_plot('CPU over time', 'CPU', 'CPU in Percentage')
    benchmarking_plot('Memory usage over time', 'MEM_USED', 'Mem in MiB')
    benchmarking_plot('File IO total read', 'FIO_TOTAL_READ', 'Reads in Percentage')
    benchmarking_plot('File IO total write', 'FIO_TOTAL_WRITE', 'Writes in Percentage')
    # benchmarking_plot('CPU over time', 'CPU', 'CPU in Percentage', boxplot=True)
    # benchmarking_plot('Memory usage over time', 'MEM_USED', 'Mem in MiB', boxplot=True)
    # benchmarking_plot('File IO total read', 'FIO_TOTAL_READ', 'Reads in Percentage', boxplot=True)
    # benchmarking_plot('File IO total write', 'FIO_TOTAL_WRITE', 'Writes in Percentage', boxplot=True)
