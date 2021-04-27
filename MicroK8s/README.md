# Micro K8s Overview
### basic information
- Intel and Arm  
- Single or MultiNode Cluster  
- Up in under a Minute  
- Runs on all Operating Systems (Mac, Windows, Linux)  
- Low-ops, minimal production Kubernetes, for devs, cloud, clusters, workstations, Edge and IoT.  
- High Availability  
- No speration between Master and Worker Nodes.   
- High availability is automatically enabled on MicroK8s for clusters with three or more nodes. (There must be more than one node available at any time,The control plane must be running on more than one node so that losing a single node would not render the cluster in-operable,The cluster state must be in a datastore that is itself highly available)  

### installation
Using Ubuntu **20.04 Server, 1 VCPU 2 GB Ram**  
Update Package Sources and install snap
```bash
$ apt update
$ apt upgrade
$ apt install snapd
```
Check install channels
```bash
$ snap info microk8s
channels:
  1.20/stable:      v1.20.5  2021-04-02 (2094) 218MB classic
  1.20/candidate:   v1.20.6  2021-04-18 (2143) 220MB classic
  1.20/beta:        v1.20.6  2021-04-18 (2143) 220MB classic
  1.20/edge:        v1.20.6  2021-04-23 (2159) 220MB classic
  latest/stable:    v1.21.0  2021-04-20 (2128) 189MB classic
  latest/candidate: v1.21.0  2021-04-11 (2126) 189MB classic
  ...
```
One liner for installation
```bash
$ snap install microk8s --channel=1.21/stable --classic
```
Check Nodes (MicroK8s bundles its own version of kubectl for accessing Kubernetes)
```bash
$ microk8s kubectl get nodes
NAME       STATUS   ROLES    AGE     VERSION
microk8s   Ready    <none>   7m53s   v1.21.0-3+121713cef81e03
```
Add another Node to the Cluster, run this on your current node
```bash
$ microk8s add-node
From the node you wish to join to this cluster, run the following:
microk8s join 168.119.125.185:25000/d2b8dbde249c98ebec798dfed8871f8e/4925053ad797
```
Now ssh to the node that should join the cluster, it should have microk8s installed and running, this might take a few minutes
```bash
$ microk8s join 168.119.125.185:25000/d2b8dbde249c98ebec798dfed8871f8e/4925053ad797
```
Confirm by running
```bash
$ microk8s kubectl get nodes
NAME              STATUS   ROLES    AGE    VERSION
microk8s          Ready    <none>   126m   v1.21.0-3+121713cef81e03
microk8s2         Ready    <none>   55s    v1.21.0-3+121713cef81e03
```
Run your first workloads
```bash
$ microk8s kubectl run nginx --image=nginx
```
Check where the pod is scheduled
```bash
$ microk8s kubectl get pods -o wide
```  
### Documentation
https://www.youtube.com/watch?v=dNT5uEeJBSw  
https://github.com/ubuntu/microk8s  
https://microk8s.io/  
https://microk8s.io/docs (Quickstart)  
https://microk8s.io/docs/clustering (Add a Node to the Cluster)  
https://microk8s.io/docs/high-availability (high availability)  
