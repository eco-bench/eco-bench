#!/bin/bash
terraform init \
&& terraform apply -var-file tfvars.json -auto-approve

bash ./generate-inventory.sh cluster.tfstate > ../hosts.ini
