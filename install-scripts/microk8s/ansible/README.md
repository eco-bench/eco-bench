Create 3 Ubuntu 20.04 VMs and replace the ips in the hosts.ini  
After that run the ansible playbook
```bash
ansible-playbook cluster_install.yml -i hosts.ini
```
Executing the Playbook took 9 mins in the Google Cloud  
The Playbook can currently __not be scaled__ meaning you can only set up a 3 Node Cluster