# Port Crane

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


