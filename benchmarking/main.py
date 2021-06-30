from typing import TextIO
import json
from datetime import datetime
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from pymongo.collection import Collection
from pymongo.database import Database
from ssh_pymongo import MongoSession
import os

sns.set_style("whitegrid")

data_path = "./data/"
user = ''
ssh_key_path = ''
mongo_db_ip = ''

def get_data_from_mongodb(eco):
    print(eco)
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

def benchmarking_plot(title , attribute, yLabel, boxplot=False):
    data = data_for_plot(open(data_path + "microk8s-application.json").read(), attribute, False, 'microk8s')
    data2 = data_for_plot(open(data_path + "k3s-application.json").read(), attribute, False, 'k3s')
    ax = None

    if boxplot:
        data3 = pd.concat([data, data2])
        ax = sns.boxplot(x="eco", y="value", data=data3)
        ax.set(xlabel='Eco')
    else:
        ax = sns.lineplot(data=data, x='time', y='value', label="mircok8s")
        sns.lineplot(data=data2, x='time', y='value', label="k3s")
        ax.set(xlabel='Time in Seconds')

    ax.set(ylabel=yLabel, title=title)

    # Save the file to the current dir
    dir_name = 'results'
    if not os.path.exists(dir_name):
        os.mkdir(dir_name)
    fig = ax.get_figure()
    fig.savefig(dir_name + '/' + attribute + '.png')

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

    # Debugging
    print(os.environ['SSH_KEY'])
    print(os.environ['MONGODB_IP'])

    user = os.environ['SERVER_USER']
    ssh_key_path = os.environ['SSH_KEY']
    mongo_db_ip =  os.environ['MONGODB_IP']
    get_data_from_mongodb('microk8s')

    benchmarking_plot('CPU over time', 'CPU', 'CPU in Percentage')
    benchmarking_plot('CPU over time', 'CPU', 'CPU in Percentage', boxplot=True)
    benchmarking_plot('Memory usage over time', 'MEM_USED', 'Mem in MiB')
    benchmarking_plot('File IO total read', 'FIO_TOTAL_READ', 'Reads in Percentage')
    benchmarking_plot('File IO total write', 'FIO_TOTAL_WRITE', 'Writes in Percentage')
