### installation de prometheus + sidecar thanos

Sur tous les noueds créer ce point de montage pour node-exporter

```
helm repo add fluxcd https://fluxcd-community.github.io/helm-charts
helm repo update
helm install flux2 fluxcd/flux2 --namespace flux-system --create-namespace
kubectl get crd | grep helm
curl -s https://fluxcd.io/install.sh | sudo bash
kubectl create ns monitoring
```

``` 
findmnt -o TARGET,PROPAGATION /
mount --make-shared /
findmnt -o TARGET,PROPAGATION /
``` 

``` 
nano thanos-config.yaml
``` 

``` 
apiVersion: v1
kind: Secret
metadata:
  name: thanos-objstore-config
  namespace: monitoring
type: Opaque
data:
  thanos.yaml: dHlwZTogIlMzIgpjb25maWc6CiAgYnVja2V0OiAicHJvbWV0aGV1cyIKICBlbmRwb2ludDogIm1pbmlvLm1vbml0b3Jpbmcuc3ZjLmNsdXN0ZXIubG9jYWw6OTAwMCIKICBhY2Nlc3Nfa2V5OiAiYWRtaW4iCiAgc2VjcmV0X2tleTogIk1pbmlvRGV2T3BzQDIwMjUjIgogIGluc2VjdXJlOiB0cnVlCg==
```

pour avoir le hash, on utilise le fichier config.txt

``` 
nano config.txt
``` 

``` 
type: "S3"
config:
  bucket: "prometheus"
  endpoint: "minio.monitoring.svc.cluster.local:9000"
  access_key: "admin"
  secret_key: "MinioDevOps@2025#"
  insecure: true
```
```
cat config.txt | base64
```

NB: on copie le hash généré et on le met dans thanos.yaml

```
kubectl apply -f thanos-config.yaml
```

### prometheus Opérator

``` 
nano prometheus-helmRepo.yaml
``` 

``` 
apiVersion: source.toolkit.fluxcd.io/v1
kind: HelmRepository
metadata:
  name: prometheus-community
  namespace: monitoring
spec:
  url: https://prometheus-community.github.io/helm-charts
  interval: 10m
``` 

``` 
kubectl apply -f prometheus-helmRepo.yaml
``` 

``` 
nano prometheus-operator.yaml
``` 

``` 
apiVersion: helm.toolkit.fluxcd.io/v2
kind: HelmRelease
metadata:
  name: prometheus-operator
  namespace: monitoring
spec:
  releaseName: prometheus-operator
  interval: 5m
  chart:
    spec:
      chart: kube-prometheus-stack
      version: 30.0.1
      sourceRef:
        kind: HelmRepository
        name: prometheus-community
        namespace: monitoring
  values:
    prometheusOperator:
      enabled: true
    nodeExporter:
      enabled: true
    prometheus:
      enabled: true
      prometheusSpec:
        replicas: 2
        scrapeInterval: 5m
        retention: 6h
        serviceMonitorSelectorNilUsesHelmValues: false
        ruleSelectorNilUsesHelmValues: false
        thanos:
          objectStorageConfig:
            key: thanos.yaml
            name: thanos-objstore-config
        prometheusExternalLabelNameClear: true
    grafana:
      enabled: true
      adminPassword: GrafanaDevOps@2025#
      additionalDataSources:
      - name: Thanos
        url: http://thanos-query-frontend.monitoring:9090
        type: prometheus
        access: proxy
        isDefault: true
      defaultDashboardsEnabled: true
      sidecar:
        datasources:
          defaultDatasourceEnabled: false
    kubeControllerManager:
      enable: true
      service:
        enable: true
        port: 10257
        targetPort: 10257
      serviceMonitor:
        enable: true
        https: true
        insecureSkipVerify: true
        serverName: 127.0.0.1
    kube-state-metrics:
      enabled: true
    kubeEtcd:
      enabled: true
      serviceMonitor:
        enabled: true
        scheme: https
        insecureSkipVerify: true
        caFile: /etc/prometheus/secrets/etcd-certs/ca.crt
        certFile: /etc/prometheus/secrets/etcd-certs/healthcheck-client.crt
        keyFile: /etc/prometheus/secrets/etcd-certs/healthcheck-client.key
    kubeScheduler:
      service:
        port: 10259
        targetPort: 10259
      serviceMonitor:
        https: true
        insecureSkipVerify: true
        serverName: 127.0.0.1
``` 

``` 
kubectl apply -f prometheus-operator.yaml
``` 

Grafana:

``` 
kubectl get pod -A
kubectl get HelmRepository -n monitoring
kubectl get HelmRelease -n monitoring
kubectl get helmchart -n monitoring

username: admin
password: GrafanaDevOps@2025#
``` 