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

