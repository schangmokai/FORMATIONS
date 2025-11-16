## Metrics-server

### pour télécharger metrics-server

```
git clone https://github.com/kubernetes-sigs/metrics-server.git
```

### pour lancer

```
kubectl create -f deploy/1.8+/
```


### avec les nouvelle versions

```
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

### pour vérifier les consommations des nodes

```
kubectl top node
```


### pour vérifier les consommations des pods

```
kubectl top pod
```