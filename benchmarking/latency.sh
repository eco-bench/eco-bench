#!/usr/bin/env bash

host_ip=$1
eco_name=$2
database=metrics
log_location=/var/log/pods

echo "" > stats.json
echo "db.${eco_name}latency.insertMany([" >> stats.json

files=("pod/camera-edge-device" "pod/image-processing-edge" "pod/image-processing-cloud" "pod/sensor-device-deployment" "pod/sensor-ew2-deployment" "pod/sensor-cw-deployment" "pod/sensor-ew1-deployment")
# files=grep -rlw "system" /var/log/pods

for i in "${files[@]}"
do
    pod_name=$(sudo kubectl get all |grep "$i" |awk '{print $1}')
    logs=$(sudo kubectl logs $pod_name)
    latency_logs=$(echo "$logs" | grep latency)

    while IFS= read -r line; do
        json_line=$(echo "$line" |awk '{print $4}')
        [[ -z "${json_line// }" ]] || echo "$json_line," >> stats.json
    done <<< "$latency_logs"
done

echo "])" >> stats.json
mongo $host_ip/$database < stats.json