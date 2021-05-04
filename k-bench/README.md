# k-bench
k-bench is a framework to benchmark the control and data plane aspects of a Kubernetes infrastructure.  
Offical GitHub Site from k-bench https://github.com/vmware-tanzu/k-bench  

Clone Repo from git and run install script
```bash
$ git clone https://github.com/vmware-tanzu/k-bench.git
$ cd k-bench
$ sudo ./install.sh
```
Check installation
```bash
$ kbench -h
```
Install and confiugre kubectl
```bash
$ sudo snap install kubectl --classic
$ cd $HOME
$ microk8s config > config
```
To confirm that it is working run a kubectl command
```bash
$ kubectl get pods
```
Run a benchmark with kbench
```bash
$ cd k-bench/
$ kbench
Starting benchmark, writing logs to kbench.log...
Running workload, please check kbench log for details...
Completed running benchmark, exit.
```
During execution you can run a tail on the logs in a seperate window
```bash
$ tail -f kbench.log
```
This will use the default ./config/default/config.json for running the benchmark  
We can inspect the file in a nice and readable way with jq
```bash
$ sudo apt install jq -y
$ cat ./config/default/config.json | jq
```
Benchmarks can be also executed with the run.sh script
```bash
$ cd k-bench/
$ ./run.sh -h
$ ./run.sh -r "kbench-microk8s-test-run"  -t "cp_heavy_12client,dp_fio" -o "./"
```
This create a folder results_kbench-microk8s-test-run_02-May-2021-08-49-57-am for us
```bash
$ cd results_kbench-microk8s-test-run_02-May-2021-08-49-57-am/cp_heavy_12client
$ ls
cp_heavy_12client  dp_fio
Inside the folder we can inspect the results in the logs file and see the configuration of the task in the json config
```