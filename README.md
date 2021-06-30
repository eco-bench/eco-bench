# eco-bench
Setup for our edge container orchestrators (ECOs) comparison benchmarking


## Planing
https://ecobench.atlassian.net/jira/software/c/projects/ECO/boards/1/roadmap?timeline=MONTHS

## Directory Structure
```
.
├── .github/workflows               # Github Actions
├── docs                            # Documentation and notes
├── dummy                           # Smart plant factory dummy application
│   ├── camera-edge-device          # Camera
│   ├── image-processing-edge       # Image edge component
│   ├── image-processing-cloud      # Image cloud component
│   ├── sensor-edge-device          # Sensors
│   ├── sensor-edge                 # Sensor edge component
│   ├── sensor-cloud                # Sensor cloud component
│   ├── edge-irrigation-system      # Irrigation system
│   └── kubernetes                  # Kubernetes resources for the application
├── install-scripts                 # Scripts to automatically configure and provision ECOs
│   ├── kube-edge                   # Ansible script for kube-edge
│   ├── microk8s                    # Ansible script for microk8s
│   ├── openyurt                    # Ansible script for openyurt
│   └── provisioning                # Terraform script to provision VMs on Google Cloud
├── docker-compose                  # Docker-compose for the smart plant factory
└── benchmarking                    # Plotting and monitoring script
```
## Installation
We have an install script `automate_steps.sh` to provision VMs, install mikrok8s, and deploy our use case application. The Script is located under `install-scripts/microk8s`.  
The following steps are required to execute the script from you local machine:
1. Terraform and Ansilbe installed
2. service-account.json in provisioning folder, in google cloud console go to IAM & Verwaltung > Dienstkonten > Dienstkonto erstellen  > Zugriff Standartt/Inhaber. After generating the Service Account click on the Service Account > Schlüssel and generate a new Key. Copy the key to the service-account.json file.
3. Create (or use an existing) Key Pair, and place it under Compute Engine > Metadaten > SSH-Schlüssel > bearbeiten. 
4. In the ansible inventory file you will need your user name for the connection. Go to provisioning/inventory.tmpl and change it. 
5. If your key file is not at the default location `~/.ssh/id_rsa` you will need to provide the exact location and add this `--key-file "~/.ssh/mykey.pem"` whenever you run `ansible-playbook` in `automate_steps.sh`.  

If you have completed all this steps execute the script from `install-scripts/microk8s`.
