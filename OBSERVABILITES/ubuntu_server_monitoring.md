### installation d'un node Exporte


```
# Télécharger Node Exporter
cd /tmp
wget https://github.com/prometheus/node_exporter/releases/download/v1.8.2/node_exporter-1.8.2.linux-amd64.tar.gz
tar xvf node_exporter-1.8.2.linux-amd64.tar.gz
sudo mv node_exporter-1.8.2.linux-amd64/node_exporter /usr/local/bin/

# Créer service systemd
sudo nano /etc/systemd/system/node_exporter.service

```
### Ajouter le contenu ci-dessous

```
[Unit]
Description=Node Exporter
After=network.target

[Service]
User=nobody
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target

```

### Redemarrer le service 

```
sudo systemctl daemon-reload
sudo systemctl enable node_exporter
sudo systemctl start node_exporter
```

### vérifier que le node_exorter fonctionne bien

```
http://10.10.10.10:9100/metrics
```

### Comme nous avons installé prometheus via Prometheus Operator (probablement via le chart Helm kube-prometheus-stack)

Pour permettre à notre prometheus de scrapper les metrique de notre server ubuntu

#### ubuntu-scrapeconfig.yaml

```
apiVersion: monitoring.coreos.com/v1alpha1
kind: ScrapeConfig
metadata:
  name: ubuntu-node
  namespace: monitoring   # même namespace que Prometheus
spec:
  staticConfigs:
    - targets:
        - "192.168.1.50:9100"   # IP et port de Node Exporter sur ton Ubuntu
```
### appliquer
```
kubectl apply -f ubuntu-scrapeconfig.yaml
```

### vérifier
```
kubectl get scrapeconfig -n monitoring
```

