#!/usr/bin/env bash

# CPU and Memory:
max_seconds=$1
# Mongo DB Endpoint
user=testUser
password=passw0rd
host_ip=35.222.15.91
database=metrics
SECONDS=0

while [ $SECONDS -le $max_seconds ]; do
    # Empty Stats File
    "" > stats.json
    timestamp=$(printf '%s' "$(date)");
    cpu_usage=$(top -b -d1 -n1|grep -i "Cpu(s)"|head -c13|tail -c5);
    # |cut -d ' ' -f3|cut -d '%' -f1);
    memory_all=$(top -b | head -4 | tail -1);
    memory_total=$(echo $memory_all | head -c16 | tail -c6);
    memory_free=$(echo $memory_all | head -c29 | tail -c6);
    memory_used=$(echo $memory_all | head -c43 | tail -c6);
    # memory_percentage=$(100 * ($memory_used / $memory_total));
    echo "db.metrics_collection_new.insert(" >> stats.json
    echo "{ 'timestamp': '$timestamp', 'CPU': '$cpu_usage', 'MEM_TOTAL': '$memory_total', 'MEM_FREE': '$memory_free', 'MEM_USED': '$memory_used'}" >> stats.json;
    echo ")" >> stats.json
    mongo -u $user -p $password $host_ip/$database < stats.json
    sleep 2;
done;

# watch -n 2
# top -b | head -4 | tail -2 | perl -anlE 'say sprintf("used: %s   total: %s  => RAM Usage: %.1f%%", \$F[7], \$F[3], 100*\$F[7]/\$F[3]) if /MiB Mem/'
# grep -E --color 'Mem|Cache|Swap' /proc/meminfo
exit 0