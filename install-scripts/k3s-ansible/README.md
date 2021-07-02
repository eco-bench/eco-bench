# Build a Kubernetes cluster using k3s via Ansible

Author: <https://github.com/itwars>

## K3s Ansible Playbook

Build a Kubernetes cluster using Ansible with k3s. The goal is easily install a Kubernetes cluster on machines running:

- [X] Debian
- [X] Ubuntu

on processor architecture:

- [X] x64
- [X] arm64
- [X] armhf

## System requirements

Deployment environment must have Ansible 2.4.0+
Master and nodes must have passwordless SSH access

## Usage
Second, edit `inventory/k3s-cluster/hosts.ini` to match the system information gathered above. For example:

```bash
[all:vars]
ansible_connection=ssh
ansible_user=root
ansible_ssh_private_key_file=~/.ssh/id_rsa

[cloud]
k3s-master ansible_host=135.181.192.36

[edge]
k3s-worker-0 ansible_host=95.217.214.198

[k3s]
k3s-master ansible_host=135.181.192.36
k3s-worker-0 ansible_host=95.217.214.198
```

Edit the ansible_user in `inventory/k3s-cluster/group_vars/all.yml` and set it to your gcp name.

Open port TCP:6443 in the gcp firewall rules.

Start provisioning of the cluster using the following command:

```bash
sudo ansible-playbook site.yml -i inventory/k3s-cluster/hosts.ini --key-file ~/.ssh/google_compute_engine
```

## Kubeconfig

To get access to your **Kubernetes** cluster just

```bash
scp debian@master_ip:~/.kube/config ~/.kube/config
```

Unable to connect to the server error, use commands with this flag (not secure but works for testing):
https://stackoverflow.com/questions/46360361/invalid-x509-certificate-for-kubernetes-master
```bash
kubectl --insecure-skip-tls-verify
```