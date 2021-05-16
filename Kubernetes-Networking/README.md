### Nodeport

Steps to execute for test set up
```bash
$ kubectl run nginx --image=nginx
$ kubectl expose pod nginx --name=nodeport --port=80 --target-port=80 --type=NodePort
$ curl localhost:32310
```

### Ingress 

We install an Ingress Controller with some additional Configuration in the Cluster. Helm is needed as a prerequisite

| Parameter |        Description    | 
| ------------- | ------------- | 
| controller.kind=DaemonSet      |If you created a daemonset, ports 80 and 443 of the Ingress controller container are mapped to the same ports of the node where the container is running. To access the Ingress controller, use those ports and an IP address of any node of the cluster where the Ingress controller is running.|
| controller.hostNetwork=true      |Enabling this option exposes every system daemon to the NGINX Ingress controller on any network interface, including the host's loopback. | 
```bash
$ kubectl apply -f Ingress.yml Pod.yml Service.yml 
$ helm install ingress-nginx/ingress-nginx --set controller.kind=DaemonSet,controller.hostNetwork=true --generate-name
```
Verify that ports are open on your cluster nodes
```bash
$ sudo netstat -tunelp | grep '443\|80'
```
Verify that you can reach the applications
```bash
$ curl -kL http://localhost/apple
apple
$ curl -kL http://localhost/banana
banana
$ curl -kL http://localhost/notfound
default backend - 404
```
This should also work in your browser if you exchange localhost with the public ip of the node

### Resources
https://www.youtube.com/watch?v=GhZi4DxaxxE  
https://kubernetes.io/docs/tutorials/services/source-ip/ (Nodeport)  
https://medium.com/google-cloud/kubernetes-nodeport-vs-loadbalancer-vs-ingress-when-should-i-use-what-922f010849e0 (Ingress vs LoadBalancer vs NodePort)  
https://matthewpalmer.net/kubernetes-app-developer/articles/kubernetes-ingress-guide-nginx-example (Ingress vs LoadBalancer vs NodePort)  
https://stackoverflow.com/questions/58001524/expose-ngnix-ingress-controller-as-daemon-set   
https://stackoverflow.com/questions/56915354/how-to-install-nginx-ingress-with-hostnetwork-on-bare-metal  
https://medium.com/google-cloud/kubernetes-nodeport-vs-loadbalancer-vs-ingress-when-should-i-use-what-922f010849e0  
