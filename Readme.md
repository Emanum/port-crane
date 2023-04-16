# Port Crane

![Port-crane banner](port-crane3.png)

Port Crane is a simple tool for providing a conainerized application to users. For now it is designed to be used with [Docker](https://www.docker.com/) and [Kubernetes](https://kubernetes.io/).

There is a Dashboard where users can see the available applications and start/delete an instance of an application. 

## Prequisites


### **Install on MAC apple silicon**

``` bash

curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-darwin-arm64
sudo install minikube-darwin-arm64 /usr/local/bin/minikube

minikube start --driver=docker --alsologtostderr
minicube status

minicube dashboard

```

### **Prepare PIP**

``` bash
cd pythonAPI
pip install 
```

# Run 

```bash
pipenv shell
python main.py
```


```bash
curl -X POST -H "Content-Type: application/json"  http://localhost:5000/instances
curl -X DELETE -H "Content-Type: application/json"  http://localhost:5000/instances/instance-1
```


## Install Microk8s on Ubuntu 22.04

```bash
sudo apt update
sudp apt install snapd
# follow https://microk8s.io/docs/getting-started
sudo snap install microk8s --classic --channel=1.26
sudo usermod -a -G microk8s $USER
sudo chown -f -R $USER ~/.kube
su - $USER

microk8s status --wait-ready

microk8s enable dashboard
microk8s enable ingress
microk8s enable dns
microk8s enable cert-manager

microk8s status --wait-ready
```

In addition you probably also want to install docker
https://microk8s.io/docs/registry-images

```bash
sudo apt-get install docker.io
sudo usermod -aG docker ${USER}
su - ${USER}
```


## Configure letsencrypt
https://microk8s.io/docs/addon-cert-manager

```bash
microk8s kubectl apply -f - <<EOF
---
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
 name: lets-encrypt
spec:
 acme:
   email: microk8s@example.com
   server: https://acme-v02.api.letsencrypt.org/directory
   privateKeySecretRef:
     # Secret resource that will be used to store the account's private key.
     name: lets-encrypt-private-key
   # Add a single challenge solver, HTTP01 using nginx
   solvers:
   - http01:
       ingress:
         class: public
EOF
```

Verify that the ClusterIssuer was created successfully with `microk8s kubectl get clusterissuer -o wide`

Next you can create a Deployment (contains actual pod definition) and a Service (makes the deployment accessible without using the ip directly).
Afterward, you can create an ingress rule to make the service accessible from the outside. See exampleKubeFiles/coder/*.yml for an example.

