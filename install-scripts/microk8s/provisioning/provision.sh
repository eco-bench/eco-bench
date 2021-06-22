#!/bin/bash
terraform init \
&& terraform apply -var-file tfvars.json -state cluster.tfstate -auto-approve

bash ./generate-inventory.sh cluster.tfstate > ../hosts.ini
