# K3S
## Installation (maually)
Master:
```bash
curl -sfL https://get.k3s.io | sh -
```

Agent:
```bash
curl -sfL https://get.k3s.io | K3S_URL=https://myserver:6443 K3S_TOKEN=mynodetoken sh -
```

K3S_URL = <domain | ip>:6443

K3S_TOKEN = `cat /var/lib/rancher/k3s/server/node-token`

## Deployment
Execute on Master:
```bash
kubectl create deployment nginx --image=nginx 
```

Expose port this single nginx pod:
```bash
kubectl expose deployment nginx --type=NodePort --port=80
```

Verfify:
```bash
kubectl get services
```

http://<domain | ip>:32751/

(the port can differ, check in get services the exposed port or specify via --target-port)

## Dashboard
[https://rancher.com/docs/k3s/latest/en/installation/kube-dashboard/](https://rancher.com/docs/k3s/latest/en/installation/kube-dashboard/)

Get more information about pods:
```bash
kubectl get pods -o wide --all-namespaces
```

Only get the pods in the namespace kubernetes-dashboard:
```bash
kubectl -n kubernetes-dashboard  get pods
```

Port forwarding for the dashboard (doesn't work):
```bash
kubectl port-forward -n kubernetes-dashboard service/kubernetes-dashboard 8080:443
```

Logs for debugging:
```bash
kubectl logs <pod-name>
```

## Resources:
[https://www.researchgate.net/profile/Yao-Pan-11/publication/338263213_A_Performance_Comparison_of_Cloud-Based_Container_Orchestration_Tools/links/5e156e59a6fdcc283761b720/A-Performance-Comparison-of-Cloud-Based-Container-Orchestration-Tools.pdf](https://www.researchgate.net/profile/Yao-Pan-11/publication/338263213_A_Performance_Comparison_of_Cloud-Based_Container_Orchestration_Tools/links/5e156e59a6fdcc283761b720/A-Performance-Comparison-of-Cloud-Based-Container-Orchestration-Tools.pdf)

[https://github.com/k3s-io/k3s/tree/master/tests/perf](https://github.com/k3s-io/k3s/tree/master/tests/perf)

[https://github.com/kubernetes/perf-tests/tree/master/clusterloader2](https://github.com/kubernetes/perf-tests/tree/master/clusterloader2)

[https://github.com/Artemmkin/infrastructure-as-code-tutorial/blob/master/docs/06-ansible.md](https://github.com/Artemmkin/infrastructure-as-code-tutorial/blob/master/docs/06-ansible.md)

[https://github.com/k3s-io/k3s-ansible](https://github.com/k3s-io/k3s-ansible)

[https://biblio.ugent.be/publication/8635821/file/8635824.pdf](https://biblio.ugent.be/publication/8635821/file/8635824.pdf)

[https://www.youtube.com/watch?v=tvreJem3xIw&list=LL&index=1](https://www.youtube.com/watch?v=tvreJem3xIw&list=LL&index=1)

[http://www.brendangregg.com](http://www.brendangregg.com)

[https://www.phoronix-test-suite.com/](https://www.phoronix-test-suite.com/)

[https://openbenchmarking.org/](https://openbenchmarking.org/)