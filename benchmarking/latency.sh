#!/usr/bin/env bash

max_seconds=$1
host_ip=$3
database=metrics
log_location=/var/log/pods

"" > stats.json
echo "db.$eco_name-latency.insertMany([" >> stats.json

files=("pod/camera-edge-device" "pod/image-processing-edge" "pod/image-processing-cloud" "pod/sensor-device-deployment" "pod/sensor-ew2-deployment" "pod/sensor-cw-deployment" "pod/sensor-ew1-deployment")
# files=grep -rlw "system" /var/log/pods

for i in "${files[@]}"
do
    pod_name=$(sudo kubectl get all |grep "$i" |awk '{print $1}')
    logs=$(sudo kubectl logs $pod_name)
    latency_logs=$(echo "$logs" | grep latency)
    echo "$latency_logs"
done

echo "])" >> stats.json
mongo $host_ip/$database < stats.json