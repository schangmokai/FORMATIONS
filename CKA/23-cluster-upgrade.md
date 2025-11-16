## Operation system upgrade

### Dans le kube-control-manager nous avons la durée d'eviction d'un pod

```
kube-controller-manager --pod-eviction-timeout=5m0s
```
Lors des opération de maintenance, il est recommander de faire un drain du noeud pour ques les pods de ce noeuds se redeploie sur un autre noeud du cluster

```
kubectl drain node01
kubectl drain node01 --ignore-daemonsets
```

Une fois le noeud driné aucun pod ne peut être programmé sur ce noeud

Pour rendre à nouveau le noeud up

```
kubectl uncordon node01
```

NB: Si on souhaite juste que ne nouveau pods ne soit pas programer sur un noeuds sans toute fois retirer les pods qui y son déployé

```
kubectl cordon node01
```

## Upgrade du cluster proprement dit.

### La meilleus strategy est de le faire noeuds par noeuds.

## Noeud Master

#### 1. Une fois connecter en ssh sr le noeud master 

```
kubeadm upgrade plan
```

Il te precise à quel version de kubernetes tu est sur chaque noeuds et egalement la dernière version stable à la quelle tu peux aller

nous allons passer pr exemple de 1.11.0 à 1.13.0. Premièrement nous devons passer par 1.12.0


NB: Pour le noeud master pas besoins de drain

#### 2. mise à jours du noeuds

```
kubeadm upgrade apply v1.12.0
apt-get upgrade -y kubelet=1.12.0-00
systemctl restart kubelet
kubectl get node
```

## Noeud worker01

#### 1. Une fois connecter en ssh sr le noeud worker01

```
kubectl drain worker01
apt-get upgrade -y kubeadm=1.12.0-00
apt-get upgrade -y kubelet=1.12.0-00
kubeadm upgrade node config --kubelet-version v1.12.0
systemctl restart kubelet
kubectl uncordon worker01
kubectl get node
```


## DEMO

https://kubernetes.io/docs/tasks/administer-cluster/kubeadm/kubeadm-upgrade/

https://v1-30.docs.kubernetes.io/docs/tasks/administer-cluster/kubeadm/kubeadm-upgrade/

####  1. changement des package version Dans tous les noeuds
```
nano /etc/apt/sources.list.d/kubernetes.list
```
nous allons voir deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.29/deb/ /
Il est important de changer la version de 1.29 à 1.30 ci-dessous la commande

```
deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.30/deb/ /
```
#### 2. Determiner vers quel package migrer

```
sudo apt update
sudo apt-cache madison kubeadm
```

### Pour le noeud master (MASTER NODE)
https://v1-30.docs.kubernetes.io/docs/tasks/administer-cluster/kubeadm/kubeadm-upgrade/

#### 1. La commande sudo apt-cache madison kubeadm nous précise exactement la version à utiliser. et on remplace 1.30.x-* par la bonne version

```
# replace x in 1.30.x-* with the latest patch version
sudo apt-mark unhold kubeadm && \
sudo apt-get update && sudo apt-get install -y kubeadm='1.30.x-*' && \
sudo apt-mark hold kubeadm
```

#### 2. Pour avoir la version de kubeadm

```
kubeadm version
```

#### 3. Vérification du plan d'ugrade

```
sudo kubeadm upgrade plan
```

#### 4. Mise à jours concretement du controlplane

```
sudo kubeadm upgrade apply v1.30.x
kubectl get node
```

#### 5. Mise à jours du kubelet

NB: Avant toute mise à jour du kubelet nous deveons isolé le noeuds (le retirer du cluster)

```
kubectl drain master01 --ignore-daemonsets
```

```
# replace x in 1.30.x-* with the latest patch version
sudo apt-mark unhold kubelet kubectl && \
sudo apt-get update && sudo apt-get install -y kubelet='1.30.x-*' kubectl='1.30.x-*' && \
sudo apt-mark hold kubelet kubectl
```

#### 6. Redemarrage du kubelet

```
sudo systemctl daemon-reload
sudo systemctl restart kubelet
```

#### 7. Vérifier la version du noeud

```
kubectl get node
```


#### 8. Remettre le noeuds master dans le cluster

```
# replace <node-to-uncordon> with the name of your node
kubectl uncordon master01
```

### WORKER NODE

https://v1-30.docs.kubernetes.io/docs/tasks/administer-cluster/kubeadm/upgrading-linux-nodes/

#### 1. mise à jours du kubeadm

On se connecte en ssh sur le noeud worker01

```
nano /etc/apt/sources.list.d/kubernetes.list
deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.30/deb/ /
```

Upgrade

```
# replace x in 1.30.x-* with the latest patch version
sudo apt-mark unhold kubeadm && \
sudo apt-get update && sudo apt-get install -y kubeadm='1.30.x-*' && \
sudo apt-mark hold kubeadm
```

kueadm upgrade

```
sudo kubeadm upgrade node
```

#### 2. isolation du worker01 du cluster

On se repositionne sur le master

```
kubectl drain worker01 --ignore-daemonsets
```

#### 3. upgrade kubelet et kubectl

```
# replace x in 1.30.x-* with the latest patch version
sudo apt-mark unhold kubelet kubectl && \
sudo apt-get update && sudo apt-get install -y kubelet='1.30.x-*' kubectl='1.30.x-*' && \
sudo apt-mark hold kubelet kubectl
```

#### 4. redemarrage du kubelet

```
sudo systemctl daemon-reload
sudo systemctl restart kubelet
```

#### 5. remettre le noeud worker01 dans le cluster

```
kubectl uncordon worker01
```



