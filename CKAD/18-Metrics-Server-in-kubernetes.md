## Apply Metric-Server to the cluster

```
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

Pour valide que les metric du cluster peuvent être récupérées

```
kubectl top node
```