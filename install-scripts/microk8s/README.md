What you need to run `automate_steps.sh`. 
0. Terraform and Ansilbe installed
1. service-account.json in provisioning folder, in google cloud console go to IAM & Verwaltung > Dienstkonten > Dienstkonto erstellen  > Zugriff Standartt/Inhaber. After generating the Service Account click on the Service Account > Schlüssel and generate a new Key. Copy the key to the service-account.json file.
2. Create (or use an existing) Key Pair, and place it under Compute Engine > Metadaten > SSH-Schlüssel > bearbeiten. 
3. In the ansible inventory file you will need your user name for the connection. Go to provisioning/inventory.tmpl and change it. 
4. If your key file is not at the default location `~/.ssh/id_rsa` you will need to provide the exact location and add this `--key-file "~/.ssh/mykey.pem"` whenever you run `ansible-playbook` in `automate_steps.sh`.
