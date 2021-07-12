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
seconds = 30

def get_data_from_mongodb(collection, state, db):
    mycol: Collection = db[collection]
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

def fixValues(timestamps, values, eco):
    times = [x for x in range(seconds)]
    for i, time in enumerate(timestamps):
        if time != times[i]:
            timestamps.insert(i, times[i])
            values.insert(i, None)
            eco.append(eco[0])
    return timestamps, values, eco

def benchmarking_plot(title, state, attribute, yLabel, boxplot=False):
    timestamps1, values1, eco1 = data_for_plot(open(data_path + "microk8s-" + state + ".json").read(), attribute, False, 'microk8s') # When MEM_USED put on True
    timestamps1, values1, eco1 = fixValues(timestamps1, values1, eco1)

    timestamps2, values2, eco2 = data_for_plot(open(data_path + "k3s-" + state + ".json").read(), attribute, False, 'k3s')
    timestamps2, values2, eco2 = fixValues(timestamps2, values2, eco2)

    timestamps3, values3, eco3 = data_for_plot(open(data_path + "openyurt-" + state + ".json").read(), attribute, False, 'openyurt')
    timestamps3, values3, eco3 = fixValues(timestamps3, values3, eco3)

    data1 = pd.DataFrame({'microk8s': values1, 'time': timestamps1})
    data2 = pd.DataFrame({'k3s': values2, 'time': timestamps2})
    data3 = pd.DataFrame({'openyurt': values3, 'time': timestamps3})

    data4 = data1.merge(data2, how='left', on='time')
    data4 = data4.merge(data3, how='left', on='time')
    data4 = data4.drop(['time'], axis=1)

    ax = None

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
    fig.savefig(dir_name + '/' + attribute + "-" + state + "-" + plot_type + '.png')
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
            

if __name__ == '__main__':
    seconds = sys.argv[1]
    
    collections = ["k3s", "microk8s", "openyurt", "k3s-latency", "microk8s-latency", "openyurt-latency"]
    states = ["application", "idle"]
    
    data = data_for_latency_plot(open(data_path + "microk8s-latency-" + states[0] + ".json").read(), "0")
    print(data)
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
        # TODO for idle is needed that benchmark script is run before and after application deployment
        # get_data_from_mongodb(coll, states[1], db)

    session.stop()
    
    benchmarking_plot('CPU over time', states[0], 'CPU', 'CPU in Percentage')
    benchmarking_plot('Memory usage over time', states[0], 'MEM_USED', 'Mem in MiB')
    benchmarking_plot('File IO total read', states[0], 'FIO_TOTAL_READ', 'Reads in Percentage')
    benchmarking_plot('File IO total write', states[0], 'FIO_TOTAL_WRITE', 'Writes in Percentage')
    benchmarking_plot('CPU over time', states[0], 'CPU', 'CPU in Percentage', boxplot=True)
    benchmarking_plot('Memory usage over time', states[0], 'MEM_USED', 'Mem in MiB', boxplot=True)
    benchmarking_plot('File IO total read', states[0], 'FIO_TOTAL_READ', 'Reads in Percentage', boxplot=True)
    benchmarking_plot('File IO total write', states[0], 'FIO_TOTAL_WRITE', 'Writes in Percentage', boxplot=True)
