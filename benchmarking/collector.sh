#!/usr/bin/env bash

# CPU and Memory:
max_seconds=$1
# Mongo DB Endpoint
# user=testUser
# password=passw0rd
host_ip=mongodb://localhost:8080
database=metrics
SECONDS=0

echo "db.metrics_collection_new.insertMany([" >> stats.json

while [ $SECONDS -le $max_seconds ]; do
    # Empty Stats File
    # "" > stats.json
    timestamp=$(printf '%s' "$(date)");
    hostname=$(echo hostname);
    cpu_usage=$(top -b -d1 -n1|grep -i "Cpu(s)"|head -c13|tail -c5);
    # |cut -d ' ' -f3|cut -d '%' -f1);
    memory_all=$(top -b | head -4 | tail -1);
    memory_total=$(echo $memory_all | head -c16 | tail -c6);
    memory_free=$(echo $memory_all | head -c29 | tail -c6);
    memory_used=$(echo $memory_all | head -c43 | tail -c6);
    io_all=$(iotop -b -n 1 -o);
    io_total_read=$(echo $io_all |grep -e Total | awk '{print $4}');
    io_total_write=$(echo $io_all |grep -e Total | awk '{print $10}');
    io_current_read=$(echo $io_all |grep -e Current | awk '{print $15}')
    io_current_write=$(echo $io_all |grep -e Current | awk '{print $21}')
    # memory_percentage=$(100 * ($memory_used / $memory_total));
    echo "{ 'timestamp': '$timestamp', 'CPU': '$cpu_usage', 'MEM_TOTAL': '$memory_total', 'MEM_FREE': '$memory_free', 'MEM_USED': '$memory_used', 'FIO_TOTAL_READ': '$io_total_read', 'FIO_TOTAL_WRITE': '$io_total_write', 'FIO_CURRENT_READ': '$io_current_read', 'FIO_CURRENT_WRITE': '$io_current_write'}," >> stats.json;
    sleep 2;
done;
echo "])" >> stats.json

mongo $host_ip/$database < stats.json

# watch -n 2
# top -b | head -4 | tail -2 | perl -anlE 'say sprintf("used: %s   total: %s  => RAM Usage: %.1f%%", \$F[7], \$F[3], 100*\$F[7]/\$F[3]) if /MiB Mem/'
# grep -E --color 'Mem|Cache|Swap' /proc/meminfo
exit 0
