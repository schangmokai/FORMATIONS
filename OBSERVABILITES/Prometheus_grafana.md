

## Installation de Prometheus_grafana

### Installation en utilisant helm

https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack

```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

### installation de prometheus and grafana

```
helm install prometheus prometheus-community/kube-prometheus-stack
```

### Vérification des composants kubernetes installer

```
kubectl get all
```

### Pour desinstaller

```
helm uninstall prometheus
```

### Pour mettre à jours

```
helm upgrade prometheus prometheus-community/kube-prometheus-stack
```

### Pour avoir le password de grafana

```
kubectl get service
```

```
kubectl get secret prometheus-grafana -o yaml
```

### Il faut décoder le password qui est en base64

```
kubectl get secret prometheus-grafana -o jsonpath="{.data.admin-password}" | base64 --decode; echo
``

## new password
```
username: admin
password: prom-operator
```
