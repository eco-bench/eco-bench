
# Setup
## 1. Install Roles
```bash
$ ansible-galaxy install -r requirements.yml --ignore-certs
```

Use the above command with `--ignore-certs` if you have an issue with the certificate validation like I had on macOS.

## 2. Run Playbook
```bash
$ ansible-playbook kubernetes-installation.yml -i <path_to_hosts.ini>
```

