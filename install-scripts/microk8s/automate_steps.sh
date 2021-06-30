#!/bin/bash
cd provisioning && terraform init && terraform apply -var-file tfvars.json -auto-approve
echo 'wait for 60 seconds till ssh daemon is up and running'
sleep 60
cd .. && ansible-playbook -i provisioning/inventory.ini ansible/cluster_install.yml
ansible-playbook -i provisioning/inventory.ini ansible/apply_applications.yml
echo 'Sleep for 600 Seconds, Benchmarking can happen during this time'
sleep 600
cd provisioning && terraform destroy -var-file tfvars.json
