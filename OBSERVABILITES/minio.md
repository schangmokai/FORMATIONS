## Installation de MINIO

Doc: https://min.io/docs/minio/kubernetes/upstream/index.html

### monter un serveur nfs

```
ls /srv/
sudo vim /etc/exports
/srv/minio 10.142.0.0/24(rw, sync,o_root_squash, no_subtree_check)
sudo mkdir /srv/minio
sudo chmod 777 /srv/minio/
sudo exportfs
sudo exportfs -a
sudo exportfs
```

mkdir minio

### PV
``` 
kubectl apply -f https://raw.githubusercontent.com/fluxcd/helm-operator/master/deploy/crds.yaml

kubectl create ns monitoring
``` 

``` 
nano minio-pv.yaml
``` 

```
apiVersion: v1
kind: PersistentVolume
metadata:
  name: minio
  namespace: monitoring
spec:
  storageClassName: ""
  capacity:
    storage: 20Gi
  accessModes:
    - ReadWriteMany
  persistentVolumeReclaimPolicy: Retain
  nfs:
    server: 192.168.12.20
    path: "/srv/minio"
```
```
kubectl apply -f minio-pv.yaml
```

### PVC

``` 
nano minio-pvc.yaml
``` 

```
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: minio
  namespace: monitoring
spec:
  storageClassName: ""
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 20Gi
```

### MINIO

``` 
nano minio.yaml
``` 

```
apiVersion: helm.fluxcd.io/v1
kind: HelmRelease
metadata:
  name: minio
  namespace: monitoring
spec:
  releaseName: minio
  helmVersion: v3
  chart:
    repository: https://helm.min.io/
    name: minio
    version: 8.0.5 
  values:
    accessKey: "admin"
    secretKey: "MinioDevOps@2025#"
    mountPath: "/data"
    persistence:
      enabled: true
      existingClaim: "minio"
    nasgateway:
      enabled: true
      replicas: 1
    livenessProbe:
      timeoutSeconds: 5
```

```
===================================================================
==================        UPDATE 2025      ========================
===================================================================
```

### installation du nécessaire pour utiliser les HelmRelease

```
helm repo add fluxcd https://fluxcd-community.github.io/helm-charts
helm repo update
helm install flux2 fluxcd/flux2 --namespace flux-system --create-namespace
kubectl get crd | grep helm
curl -s https://fluxcd.io/install.sh | sudo bash
kubectl create ns monitoring
```

si la version de kubernetes est inférieur 1.31.0-0

```
curl -s https://fluxcd.io/install.sh | sudo bash
flux install
kubectl create ns monitoring
```

### creation du pv

```
nano pv.yaml
```
```
apiVersion: v1
kind: PersistentVolume
metadata:
  name: minio
  labels:
    type: local
spec:
  storageClassName: manual
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: /data/minio
```
```
kubectl apply -f pv.yaml
```

### creation du pvc
```
nano pvc.yaml
```

```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: minio
  namespace: monitoring
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
```
```
kubectl apply -f pvc.yaml
```

### creation du helmRepository
```
nano helmRepository.yaml
```

```
apiVersion: source.toolkit.fluxcd.io/v1
kind: HelmRepository
metadata:
  name: minio
  namespace: monitoring
spec:
  interval: 10m0s
  url: https://charts.min.io/
```
```
kubectl apply -f helmRepository.yaml
```

### creation du helmRelease minio

```
nano minio.yaml
```

```
apiVersion: helm.toolkit.fluxcd.io/v2
kind: HelmRelease
metadata:
  name: minio
  namespace: monitoring
spec:
  releaseName: minio
  interval: 5m
  chart:
    spec:
      chart: minio
      version: 5.4.0
      sourceRef:
        kind: HelmRepository
        name: minio
        namespace: monitoring
  values:
    mode: standalone
    replicas: 1
    rootUser: admin
    rootPassword: MinioDevOps@2025#
    persistence:
      enabled: true
      existingClaim: "minio"
    nasgateway:
      enabled: true
      replicas: 1
    livenessProbe:
      timeoutSeconds: 5
    resources:
      requests:
        memory: "256Mi"
        cpu: "100m"
      limits:
        memory: "512Mi"
        cpu: "500m"
```
kubectl apply -f minio.yaml

```
kubectl get pod -A
kubectl get HelmRepository -n monitoring
kubectl get HelmRelease -n monitoring
kubectl get helmchart -n monitoring

kubectl get helmrepository -A

helm repo add minio https://charts.min.io/
helm repo update
helm search repo minio --versions

mkdir -p /data/minio
sudo chown -R 1000:1000 /data/minio
sudo chmod -R u+rwX /data/minio

```

