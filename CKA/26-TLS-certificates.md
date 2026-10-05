## TLS certificate in kubernetes

### pour ouvrir un certificat et avoir tous les details

```
openssl x509 -in file- path.crt -text -noout
```

### SÉCURITÉ DU CLUSTER


### personne physique

Toutes les requetes de l'utilisateur vers le cluster kubernetes sont envoyés à l'api-server qui authentifie l'utilisateur avant d'effectuer sa requête

#### comment l'api serveur authentifie l'utilisateur ?

1- tu peux avoir les tokens et certificat dauthentification dans un fichier c'est ce qu'o utilise habituelement 
2- tu peux t'authentifier via un LDAP et autre.


### Application

Les application utilisent de service account


### TLS certificate

C'est une methode de communication de confiance.

1- Le serveur possède un certificat, une clé privé et une clé publique.
2- lors de l'authentification, le serveur envois au client un certificat possédent la clé publique.
3- Le client signe son message en utilisant la clé publique du serveur et à partir de ce moment seul le serveur peut utiliser la clé privé pou décripter le message du cleint.


### nous avons des certificat auto signé et des certificats signé pra les autorité de confiance.


### pour faire signé le certificat par une autorité deconfiance

il faut créer un CSR et une Key

```
openssl genrsa -out my-bank.key 2048
openssl req -new -key my-bank.key -out my-bank.csr -subj "/C=US/ST=CA/O=MyORG, Inc./CN=my-back.com"
```

### comment générer les certificat du cluster

1- creation du certificat Authority (CA de kubernetes)

#### generate keys
```
openssl genrsa -out ca.key 2048
```

#### certificate signing request
```
openssl req -new -key ca.key -subj "/CN=KUBERNETES-CA" -out ca.csr
```

#### Signature du certificat
```
openssl x509 -req -in ca.csr -signkey ca.key -out ca.crt
```


2- creation des droit d'accès au cluster par l'utilisateur admin.


#### l'utilisateur admin doit créer sa propre clé sur sa machine
```
openssl genrsa -out admin.key 2048
```

#### l'utilisateur admin doit créer un CSR
```
openssl req -new -key admin.key -subj "/CN=kube-admin" -out admin.csr
```

#### génération du certificat signé pour l'admin
```
openssl x509 -req -in admin.csr -CA ca.crt -CAker ca.key -out admin.crt
```

même principe pour générer les certificat de tous les composant kubernetes

```
kube-api server
kube-scheduler
kube-controller-manager
kube-proxy
kubelet server
etcd
```

### pour avoir les details d'un certificat:

openssl x509 -in /etc/kubernetes/pki/apiserver.crt -text -noout


### pour switcher de context partant d'un fichier

```
kubectl config use-context research --kubeconfig /root/my-kube-config
```

### pour utiliser my-kube-config comme config par defaut

```
mv /root/my-kube-config /root/.kube/config

kubectl config view
```
