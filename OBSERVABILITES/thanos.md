### installation de thanos

```
helm repo add fluxcd https://fluxcd-community.github.io/helm-charts
helm repo update
helm install flux2 fluxcd/flux2 --namespace flux-system --create-namespace
kubectl get crd | grep helm
curl -s https://fluxcd.io/install.sh | sudo bash
kubectl create ns monitoring
```
NB: pour bitnami il faut activer oci:

```
flux install --components-extra=image-reflector-controller,image-automation-controller
```

helm repo add bitnami https://charts.bitnami.com/bitnami
helm search repo bitnami/thanos --versions

### creation du thanosRepository

``` 
nano thanos-helmRepo.yaml
``` 

``` 
apiVersion: source.toolkit.fluxcd.io/v1
kind: HelmRepository
metadata:
  name: thanos
  namespace: monitoring
spec:
  url: https://stevehipwell.github.io/helm-charts/
  interval: 10m
``` 

kubectl apply -f thanos-helmRepo.yaml

``` 
nano thanos.yaml
``` 

``` 
apiVersion: helm.toolkit.fluxcd.io/v2
kind: HelmRelease
metadata:
  name: thanos
  namespace: monitoring
spec:
  interval: 5m
  releaseName: thanos
  chart:
    spec:
      chart: thanos
      version: 1.21.1
      sourceRef:
        kind: HelmRepository
        name: thanos
        namespace: monitoring
  values:
    metrics:
      enabled: true
      serviceMonitor:
        enabled: true

    storegateway:
      enabled: true
      persistence:
        enabled: false
      objstoreConfig:
        type: S3
        config:
          bucket: "prometheus"
          endpoint: "minio.monitoring.svc.cluster.local:9000"
          access_key: "admin"
          secret_key: "MinioDevOps@2025#"
          insecure: true

    query:
      enabled: true
      dnsDiscovery:
        enabled: true
        sidecarsService: prometheus-operated
        sidecarsNamespace: monitoring
      extraFlags:
        - --query.auto-downsampling
      replicaLabel: prometheus_replica

    queryFrontend:
      config: |-
        type: IN-MEMORY
        config:
          max_size: 1GB
          max_size_items: 0
          validity: 0s

    compactor:
      enabled: true
      persistence:
        enabled: false
      objstoreConfig:
        type: S3
        config:
          bucket: "prometheus"
          endpoint: "minio.monitoring.svc.cluster.local:9000"
          access_key: "admin"
          secret_key: "MinioDevOps@2025#"
          insecure: true
      retentionResolutionRaw: 2d
      retentionResolution5m: 30d
      retentionResolution1h: 1y

    bucketweb:
      enabled: true
```

kubectl apply -f thanos.yaml

``` 
kubectl get pod -A
kubectl get HelmRepository -n monitoring
kubectl get HelmRelease -n monitoring
kubectl get helmchart -n monitoring

username: admin
password: GrafanaDevOps@2025#
``` 

``` 
kubectl delete helmrelease thanos -n monitoring
kubectl delete helmchart monitoring-thanos -n monitoring

flux reconcile source helm thanos -n monitoring
flux reconcile helmrelease thanos -n monitoring
``` 

```
===================================================================
==================        UPDATE 2025 OCI  ========================
===================================================================
```

```
helm repo add fluxcd https://fluxcd-community.github.io/helm-charts
helm repo update
helm install flux2 fluxcd/flux2 --namespace flux-system --create-namespace
kubectl get crd | grep helm
curl -s https://fluxcd.io/install.sh | sudo bash
kubectl create ns monitoring
```

nano flux-oci-helmRepo.yaml

``` 
apiVersion: source.toolkit.fluxcd.io/v1
kind: HelmRepository
metadata:
  name: thanos
  namespace: monitoring
spec:
  type: "oci"
  interval: 5m0s
  url: oci://registry-1.docker.io/bitnamicharts
``` 
kubectl apply -f flux-oci-helmRepo.yaml

nano thanos-oci.yaml
``` 
apiVersion: helm.toolkit.fluxcd.io/v2
kind: HelmRelease
metadata:
  name: thanos
  namespace: monitoring
spec:
  interval: 5m
  releaseName: thanos
  chart:
    spec:
      chart: thanos
      version: 17.2.3
      sourceRef:
        kind: HelmRepository
        name: thanos
        namespace: monitoring
  values:
    metrics:
      enabled: true
      serviceMonitor:
        enabled: true
    objstoreConfig: |-
      type: "S3"
      config:
        bucket: "prometheus"
        endpoint: "minio.monitoring.svc.cluster.local:9000"
        access_key: "admin"
        secret_key: "MinioDevOps@2025#"
        insecure: true
    query:
      enabled: true
      dnsDiscovery:
        enabled: true
        sidecarsService: prometheus-operated
        sidecarsNamespace: monitoring
      extraFlags:
        - --query.auto-downsampling
      replicaLabel: prometheus_replica
    queryFrontend:
      config: |-
        type: IN-MEMORY
        config:
          max_size: 1GB
          max_size_items: 0
          validity: 0s
    storegateway:
      enabled: true
      persistence:
        enabled: false
    compactor:
      enabled: true
      persistence:
        enabled: false
      retentionResolutionRaw: 2d
      retentionResolution5m: 30d
      retentionResolution1h: 1y
    bucketweb:
      enabled: true
``` 
kubectl apply -f thanos-oci.yaml

