#!/bin/bash
terraform destroy -var-file tfvars.json -state cluster.tfstate -auto-approve
