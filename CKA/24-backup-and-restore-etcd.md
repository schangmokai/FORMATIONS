## Backup and restore ET-CD

Doc: https://kubernetes.io/docs/tasks/administer-cluster/configure-upgrade-etcd/


#### 1. backup

```
ETCDCTL_API=3 etcdctl --endpoints=https://127.0.0.1:2379 \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  snapshot save /opt/snapshot-pre-boot.db
```

#### 2. snapshot status

```
etcdutl snapshot status  /opt/snapshot-pre-boot.db -w table
etcdctl --write-out=table snapshot status /opt/snapshot.db 
```

#### 3. restore etcd

3.1. créer le repertoire des backup

```
mkdir -p /var/lib/etcdrestore
```
3.2. Restoration

```
mv /var/lib/etcd /var/lib/etcd.old
mv /etc/kubernetes/manifests/*.yaml  /tmp
systemctl stop kubelet

etcdutl --data-dir /var/lib/etcdrestore snapshot restore /opt/snapshot-pre-boot.db
ETCDCTL_API=3 etcdctl --data-dir /var/lib/etcdrestore snapshot restore /opt/snapshot-pre-boot.db
ETCDCTL_API=3 etcdctl --data-dir /var/lib/etcd snapshot restore /opt/snapshot-pre-boot.db


ETCDCTL_API=3 etcdctl snapshot restore /opt/snapshot-pre-boot.db \
  --data-dir=/var/lib/etcd \
  --name=controlplane \
  --initial-advertise-peer-urls=https://192.168.121.223:2380 \
  --initial-cluster=controlplane=https://192.168.121.223:2380


mv /tmp/*.yaml /etc/kubernetes/manifests/
systemctl start kubelet
```

3.3. Monter le nouveau repertoire dans le repertoire

Nous allons simplement modifier le fichier /etc/kubernetes/manifests/etcd.yaml

```
nano /etc/kubernetes/manifests/etcd.yaml
```

Et ajouter ce qui suit.

```
- --data-dir=/var/lib/etcdrestore

volumeMounts:
    - mountPath: /var/lib/etcdrestore
      name: etcd-data
      
volumes:
  - hostPath:
      path: /var/lib/etcdrestore
      type: DirectoryOrCreate
    name: etcd-data
```

En remplacement de 

```
- --data-dir=/var/lib/etcd
```