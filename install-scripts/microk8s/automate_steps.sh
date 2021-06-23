#!/bin/bash
cd provisioning && ./provision.sh
cd .. && ansible-playbook -i hosts.ini ansible/cluster_install.yml
ansible-playbook -i hosts.ini ansible/apply_applications.yml
echo 'Sleep for 600 Seconds'
sleep 600
cd provisioning && ./destroy.sh