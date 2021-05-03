# 1. Infrastructure setup
## 1.1 Installation

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

## 1.2 Provisioning

**NOTE** Our working dir for this section is `./contrib/terraform/gcp`

1. Download the service account file https://console.cloud.google.com/iam-admin/serviceaccounts/details/112765715056595122606/keys?project=eco-bench&supportedpurview=project
   and replace the filename and location inside the **tfvars.json**

2. To provision a cluster run:
   ```
   && terraform init \
   && terraform apply -var-file tfvars.json -state dev-cluster.tfstate -auto-approve
   ```
   Confirm with **yes**

3. Next you have to generate an inventory file 
   ```bash
   bash ./generate-inventory.sh dev-cluster.tfstate > ../../../inventory/kubeedge-cluster/inventory.ini
   ```
   I use **bash** here specifically since my default shell is zsh and **mapfile** works different there.


## 1.3 Install Kubernetes

**NOTE** Our working dir for this section is `./`

```
ansible-playbook -i inventory/kubeedge-cluster/inventory.ini cluster.yml -b -v
```

This step will also download to `inventory/kubeedge-cluster/artifacts/admin.conf` which you will need for step 1.4 


## 1.4 Install KubeEdge CloudCore

For this step we will need the `admin.conf` generated from the last step.

:warning: Furthermore we will change our working directory for this section to `./keadm` relative to this README 

1. Copy the `admin.con` into the `./keadm` folder. 
   

2. Run
   ```
   ./start-local.sh
   ```
   This will start a docker container with keadm, gcloud sdk and kubectl install
   

3. Initialize the CloudCore
   ```
   keadm init
   ```

## 1.5 Create an EdgeNode

**NOTE** Our working dir for this section is `./EdgeNode`
 To create an edge node instance with docker and keadm install run
```bash
terraform init \
&& terraform apply
```
Confirm with **yes**


To destroy the instance simply run 
```bash
terraform destroy 
```
Confirm with **yes**

## 1.6 Destroy the Cluster

**NOTE** Our working dir for this section is `./contrib/terraform/gcp` inside the kubespray repo
   ```
   && terraform destroy  -var-file tfvars.json -state dev-cluster.tfstate
   ```
Confirm with **yes**


# Resources
- https://upcloud.com/community/tutorials/deploy-kubernetes-using-kubespray/
- https://www.redhat.com/sysadmin/kubespray-deploy-kubernetes
