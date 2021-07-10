#!/usr/bin/env bash

max_seconds=$1
host_ip=$3
database=metrics
SECONDS=0

echo "" > stats.json
eco_name=$2
echo "db.$eco_name.insertMany([" >> stats.json

clean_up() {
  # Send data to db when the process is killed
  echo "In clean up Part"
  echo "Sending Data to MongoDB"
  echo "was in clean_up() method" >> stats.json
  mongo $host_ip/$database < stats.json
  exit
}

# Handle kill signals
trap clean_up SIGHUP SIGINT SIGTERM

while [ true ]; do
    timestamp=$(printf '%s' "$(date)");
    hostname=$(echo $(hostname));
    cpu_usage=$(top -b -d1 -n1 |grep -i "Cpu(s)" |awk '{print $2}')

    memory_all=$(top -b -d1 -n1 |grep "MiB Mem")
    memory_total=$(echo $memory_all |awk '{print $4}');
    memory_free=$(echo $memory_all |awk '{print $6}');
    memory_used=$(echo $memory_all |awk '{print $8}');

    io_all=$(iotop -b -n 1 -o);
    io_total_read=$(echo $io_all |grep -e Total | awk '{print $4}');
    io_total_write=$(echo $io_all |grep -e Total | awk '{print $10}');
    io_current_read=$(echo $io_all |grep -e Current | awk '{print $4}')
    io_current_write=$(echo $io_all |grep -e Current | awk '{print $10}')

    # memory_percentage=$(100 * ($memory_used / $memory_total));
    echo "{ 'HOST': '$hostname', 'timestamp': '$timestamp', 'CPU': '$cpu_usage', 'MEM_TOTAL': '$memory_total', 'MEM_FREE': '$memory_free', 'MEM_USED': '$memory_used', 'FIO_TOTAL_READ': '$io_total_read', 'FIO_TOTAL_WRITE': '$io_total_write', 'FIO_CURRENT_READ': '$io_current_read', 'FIO_CURRENT_WRITE': '$io_current_write'}," >> stats.json;
    sleep 2;
done;
echo "])" >> stats.json

exit 0
