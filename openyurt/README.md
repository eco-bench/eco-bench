Installation 2 Ubuntu 20.04 VMs, 4GB RAM, 2 vCPU
Install necessary packets
```bash
$ apt update
$ apt upgrade
$ apt install git make golang -y
```
Clone openyurt repo and compile yurtctl
```bash
$ git clone https://github.com/openyurtio/openyurt.git
$ cd openyurt/
$ make build WHAT=cmd/yurtctl
```
Add to path
```bash
$ echo $PATH
$ mv _output/bin/yurtctl /usr/local/bin/yurtctl
$ yurtctl --help
```
Install kubernetes packages
```bash
$ sudo apt-get install -y apt-transport-https ca-certificates curl
$ sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
$ echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
$ sudo apt-get update
```
Search for a specific Kubernetes Version
```bash
$ apt list -a kubeadm
$ apt install -y kubeadm=1.16.15-00 kubelet=1.16.15-00 kubectl=1.16.15-00
```
Install docker
```bash
$ curl https://releases.rancher.com/install-docker/19.03.sh | sh
$ docker ps
```
Optional if updating a cluster, to build latest images run
```bash
make release
```
Start Kubernetes Node
```bash
$ kubeadm init --pod-network-cidr=192.168.0.0/16
$ mkdir -p $HOME/.kube
$ sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
$ sudo chown $(id -u):$(id -g) $HOME/.kube/config
$ kubectl version
$ kubectl edit nodes openyurt01 (remove taints)
```
if you want to join an existing cluster run this insted, documentation https://computingforgeeks.com/join-new-kubernetes-worker-node-to-existing-cluster/
```bash
kubeadm join \
  <control-plane-host>:<control-plane-port> \
  --token <token> \
  --discovery-token-ca-cert-hash sha256:<hash>
```
Install CNI, documentation is here https://docs.projectcalico.org/getting-started/kubernetes/quickstart
```bash
$ kubectl create -f https://docs.projectcalico.org/manifests/tigera-operator.yaml
$ kubectl create -f https://docs.projectcalico.org/manifests/custom-resources.yaml
$ watch kubectl get pods -n calico-system
```
Convert Cluster into Open Yurt Cluster
```bash
$ yurtctl convert --cloud-nodes openyurt01 --provider kubeadm --deploy-yurttunnel
```
Mark desired edge nodes as autonomous to prevent pods from beeing evicted during disconnection
```bash
$ yurtctl convert --cloud-nodes <cloud-node-name> --provider kubeadm --deploy-yurttunnel
```
to confirm
```bash
$ kubectl describe node | grep Labels -A 5
Labels:      openyurt.io/is-edge-worker=true
$ kubectl describe node | grep Annotations -A 5
Annotations: node.beta.alibabacloud.com/autonomy: true
```
Run cluster-info to verify your configuration
```bash
$ yurtctl cluster-info
```
