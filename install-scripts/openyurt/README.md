# OpenYurt Install Script
The Script installs first Kubernetes version 1.16.15 with Kubeadm and then converts the kubernetes Cluster into an OpenYurt Cluster  
Make sure to set the correct IPs in the __host.ini__ and __group_vars__
  
To run the script
```bash
ansible-playbook cluster_install.yml -i hosts.ini
```
Currently the script needs to run as root
