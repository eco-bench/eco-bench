# Infrastructure setup
## Installation

1. Clone https://github.com/eco-bench/kubespray.git and checkout our config branch
   ```bash
   cd ../ \
   && git clone git@github.com:eco-bench/kubespray.git \
   && git checkout eco-bench
   ```
   :warning: From now on we will work in this repo for provisioning and kubernetes installation related tasks.


2. Install requirements 
   ```bash
   cd kubespray ; pip3 install -r requirements.txt
   ```

3. Install Terraform Python and JQ
   ```bash
   brew install python terraform jq
   ```
   or see https://www.terraform.io/downloads.html#

## Provisioning

**NOTE** Our working dir for this section is `./contrib/terraform/gcp`

1. Download the service account file https://console.cloud.google.com/iam-admin/serviceaccounts/details/112765715056595122606/keys?project=eco-bench&supportedpurview=project
   and replace the filename and location inside the **tfvars.json**

2. To provision a cluster run:
   ```
   && terraform init \
   && terraform apply -var-file tfvars.json -state dev-cluster.tfstate
   ```
   Confirm with **yes**

3. Next you have to generate an inventory file 
   ```bash
   bash ./generate-inventory.sh dev-cluster.tfstate > ../../../inventory/kubeedge-cluster/inventory.ini
   ```
   I use **bash** here specifically since my default shell is zsh and **mapfile** works different there.


## Install Kubernetes

**NOTE** Our working dir for this section is `./`

```
ansible-playbook -i inventory/kubeedge-cluster/inventory.ini cluster.yml -b -v
```
   
## Destroy

**NOTE** Our working dir for this section is `./contrib/terraform/gcp`
   ```
   && terraform destroy  -var-file tfvars.json -state dev-cluster.tfstate
   ```
Confirm with **yes**


# Resources
- https://upcloud.com/community/tutorials/deploy-kubernetes-using-kubespray/
- https://www.redhat.com/sysadmin/kubespray-deploy-kubernetes
