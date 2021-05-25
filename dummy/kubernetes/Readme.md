# Kuberenetes

https://kubernetes.io/docs/tasks/configure-pod-container/translate-compose-kubernetes/

Enable Ingress in minikube:
```bash
minikube addons enable ingress
```

Apply all yaml files (inside kubernetes folder):
```bash
kubectl apply -f . --recursive
```

Setup Github token in Kubernetes:
```bash
kubectl create secret docker-registry github-regsecr --docker-server=docker.pkg.github.com --docker-username=<username> --docker-password=<PAT>
```