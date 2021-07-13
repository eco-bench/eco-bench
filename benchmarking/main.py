import json
import os
from datetime import datetime
from typing import TextIO
import sys

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from pymongo.collection import Collection
from pymongo.database import Database
from ssh_pymongo import MongoSession

sns.set_style("whitegrid")

data_path = "./data/"
collections = ["k3s", "microk8s", "openyurt"]
# collections2 = ["k3slatency", "microk8slatency", "openyurtlatency"]
states = ["application", "idle", "latency"]
latency_types = [0, 1, 2]
latency_title = ['Latency Edge Device to Edge', 'Latency Edge to Edge', 'Latency Edge to Cloud']

def get_data_from_mongodb(collection, state, db, latency=False):
    mycol: Collection = db[collection]
    if latency:
        mycol = db[collection + state]
    cursor = mycol.find({})
    type_documents_count = mycol.find({}).count()
    file: TextIO
    with open(f'data/{collection}-{state}.json', 'w') as file:
        file.write('[')
        document: object
        for i, document in enumerate(cursor, 1):
            document.pop('_id')
            file.write(json.dumps(document, default=str))
            if i != type_documents_count:
                file.write(',')
        file.write(']')

def benchmarking_plot(title, state, attribute, yLabel, boxplot=False, latency=False, latency_type=0):
    data1 = None
    data2 = None
    data3 = None
    ax = None

    if not latency:
        timestamps1, values1, eco1 = data_for_plot(open(data_path + "microk8s-" + state + ".json").read(), attribute, False, 'microk8s') # When MEM_USED put on True
        timestamps2, values2, eco2 = data_for_plot(open(data_path + "k3s-" + state + ".json").read(), attribute, False, 'k3s')
        timestamps3, values3, eco3 = data_for_plot(open(data_path + "openyurt-" + state + ".json").read(), attribute, False, 'openyurt')

        data1 = pd.DataFrame({'microk8s': values1, 'time': timestamps1})
        data2 = pd.DataFrame({'k3s': values2, 'time': timestamps2})
        data3 = pd.DataFrame({'openyurt': values3, 'time': timestamps3})
    else:
        data1 = data_for_latency_plot(open(data_path + "microk8s-latency" + ".json").read(), latency_type, 'microk8s')
        data2 = data_for_latency_plot(open(data_path + "k3s-latency" + ".json").read(), latency_type, 'k3s')
        data3 = data_for_latency_plot(open(data_path + "openyurt-latency" + ".json").read(), latency_type, 'openyurt')

    data4 = data1.merge(data2, how='left', on='time')
    data4 = data4.merge(data3, how='left', on='time')
    data4 = data4.drop(['time'], axis=1)

    if boxplot:
        ax = sns.boxplot(data=data4)
        ax.set(xlabel='Eco')
    else:
        data4 = data4.interpolate('linear')
        ax = sns.lineplot(data=data4)
        ax.set(xlabel='Time in Seconds')

    ax.set(ylabel=yLabel, title=title)

    dir_name = 'results'
    if not os.path.exists(dir_name):
        os.mkdir(dir_name)

    fig = ax.get_figure()

    plot_type = "boxplot" if boxplot else "lineplot"
    fig_name = dir_name + '/' + attribute + "-" + state + "-" + plot_type + '.png'
    if latency:
        fig_name = dir_name + '/' + attribute + "-" + state + str(latency_type) + "-" + plot_type + '.png'

    fig.savefig(fig_name)
    plt.clf()

def data_for_plot(json_data, attribute, calc, eco):
    parsed_json = (json.loads(json_data))
    values = []
    timestamps = []
    last_datetime = datetime.strptime(parsed_json[0]['timestamp'], '%a %b %d %H:%M:%S %Z %Y')

    for index, x in enumerate(parsed_json):
        # Hack for annoying bug
        if x['CPU'] == "us,":
            continue
        
        datetime_object = datetime.strptime(x['timestamp'], '%a %b %d %H:%M:%S %Z %Y')
        datetime_diff = int((datetime_object-last_datetime).total_seconds())
        timestamps.append(datetime_diff)

        att = float(x[attribute])
        if (attribute == 'MEM_USED' and calc):
            att = att / 1000

        values.append(att)

    return (timestamps, values, [eco] * len(values))

def data_for_latency_plot(json_data, latency_type, eco):
    # Filter out type
    parsed_json = (json.loads(json_data))
    timestamps = []
    timeDelta = []

    dt2 = datetime.fromtimestamp(parsed_json[0]['timestamp'] // 1000000000)
    last_datetime = datetime.strftime(dt2, '%a %b %d %H:%M:%S %Z %Y')

    # Filter json data for type
    for item in parsed_json:
        if item["type"] == latency_type:
            dt = datetime.fromtimestamp(item['timestamp'] // 1000000000)
            datetime_object = datetime.strftime(dt, '%a %b %d %H:%M:%S %Z %Y')
            datetime_diff = int((dt-dt2).total_seconds())
            timestamps.append(datetime_diff)

            timeDelta.append(item["timeDelta"])
            
    return pd.DataFrame({eco: timeDelta, 'time': timestamps})

if __name__ == '__main__':
    user = os.environ['SERVER_USER']
    ssh_key_path = os.environ['SSH_KEY']
    mongo_db_ip =  os.environ['MONGODB_IP']

    session = MongoSession(
        host=mongo_db_ip,
        user=user,
        key=ssh_key_path,
        uri='mongodb://127.0.0.1:27017')
    db = session.connection['metrics']

    for coll in collections:
        get_data_from_mongodb(coll, states[0], db)
        get_data_from_mongodb(coll, states[2], db, latency=True)

    session.stop()
    
    benchmarking_plot('CPU over time', states[0], 'CPU', 'CPU in Percentage')
    benchmarking_plot('Memory usage over time', states[0], 'MEM_USED', 'Mem in MiB')
    benchmarking_plot('File IO total read', states[0], 'FIO_TOTAL_READ', 'Reads in Percentage')
    benchmarking_plot('File IO total write', states[0], 'FIO_TOTAL_WRITE', 'Writes in Percentage')
    benchmarking_plot('CPU over time', states[0], 'CPU', 'CPU in Percentage', boxplot=True)
    benchmarking_plot('Memory usage over time', states[0], 'MEM_USED', 'Mem in MiB', boxplot=True)
    benchmarking_plot('File IO total read', states[0], 'FIO_TOTAL_READ', 'Reads in Percentage', boxplot=True)
    benchmarking_plot('File IO total write', states[0], 'FIO_TOTAL_WRITE', 'Writes in Percentage', boxplot=True)

    for i, latency_type in enumerate(latency_types):
        benchmarking_plot(latency_title[i], states[2], 'timeDelta', 'Time per Request', latency=True, latency_type=latency_type)