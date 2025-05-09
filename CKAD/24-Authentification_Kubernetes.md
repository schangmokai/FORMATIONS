## Authentification dans kubernetes

#  deprecated METHODS since 1.19

Il s'agit des mécanismes qui permettent de s'authentifier auprès de l'api-serveur avant d'envoyer la requête à Kubernetes.

### On distinque 4 type d'authentifications:

1. Static password file
2. Static Token File
3. Certificates
4. Identity Services (LDAP)

### 1. Static password file


Premièrement creer un fichier.csv contenant les password 

user-details.csv

```
password123, user1, u0001
password123, user2, u0002
password123, user3, u0003
```

Modififier le service kube-apiserver.service

```
kubectl edit svc kube-apiserver
```
Et ajouter cette ligne
```
--basic-auth-file=user-details.csv
```

Modifier aussi le pod static kube-apiserver.yaml.

```
nano /etc/kubernetes/manifests/kube-apiserver.yaml
```
Et ajouter cette ligne
```
- --basic-auth-file=user-details.csv
```

### pour tester

```
curl -v -k https://masterip:6443/api/v1/pods -u "uer1:password123"
```

### 1. Static Token File

Premièrement creer un fichier.csv contenant les password

user-token-details.csv

```
slksldklsdlJHFJDFHDFKJHldknsldfisdf, user1, u0001
KJDKDHqlkqlsKLLSqlsSKSJlQqKsljsLKQd, user2, u0002
KJDKDdkdjhjkdhKDKJDHDKDjddkjdDDLDje, user3, u0003
```

Modififier le service kube-apiserver.service

```
kubectl edit svc kube-apiserver
```
Et ajouter cette ligne
```
--token-auth-file=user-token-details.csv
```

Modifier aussi le pod static kube-apiserver.yaml.

```
nano /etc/kubernetes/manifests/kube-apiserver.yaml
```
Et ajouter cette ligne
```
- --token-auth-file=user-token-details.csv
```

NB: nous pouvons créer un role et un role binding pour donne uniquement la possibilité à user2 de lister les pods.

```
---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  namespace: default
  name: pod-reader
rules:
- apiGroups: # "" indicates the core API group
  resources: ["pods
  verbs: ["get", "watch", "lis

---
# This role binding allows "jane" to read pods in the "default" namespace.
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: read-pods
  namespace: default
subjects:
- kind: User
  name: user1 # Name is case sensitive
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role #this must be Role or ClusterRole
  name: pod-reader # this must match the name of the Role or ClusterRole you wish to bind to
  apiGroup: rbac.authorization.k8s.io
```

```
curl -v -k https://masterNodeIP:6443/api/v1/pods -u "user1:password123"
```

## Le fichier KubeConfig

Dans ce fichier nous avons l'ensemble des clusters et les users pour chaque custer

Pour changer de context

```
kubectl config use-context toto@develop
```

Defualt config

```
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJR2to>
    server: https://controlplane:6443
  name: kubernetes
contexts:
- context:
    cluster: kubernetes
    user: kubernetes-admin
  name: kubernetes-admin@kubernetes
current-context: kubernetes-admin@kubernetes
kind: Config
preferences: {}
users:
- name: kubernetes-admin
  user:
    client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURLVENDQWhHZ0F3SUJBZ0lJZDBPYnd>
    client-key-data: LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFb2dJQkFBS0NBUUVBcVpFb2ljRkQ3YmJ>
```

Exemple de config File

```
apiVersion: v1
kind: Config

clusters:
- name: production
  cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443

- name: development
  cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443

- name: kubernetes-on-aws
  cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443

- name: test-cluster-1
  cluster:
    certificate-authority: /etc/kubernetes/pki/ca.crt
    server: https://controlplane:6443

contexts:
- name: test-user@development
  context:
    cluster: development
    user: test-user

- name: aws-user@kubernetes-on-aws
  context:
    cluster: kubernetes-on-aws
    user: aws-user

- name: test-user@production
  context:
    cluster: production
    user: test-user

- name: research
  context:
    cluster: test-cluster-1
    user: dev-user

users:
- name: test-user
  user:
    client-certificate: /etc/kubernetes/pki/users/test-user/test-user.crt
    client-key: /etc/kubernetes/pki/users/test-user/test-user.key
- name: dev-user
  user:
    client-certificate: /etc/kubernetes/pki/users/dev-user/developer-user.crt
    client-key: /etc/kubernetes/pki/users/dev-user/dev-user.key
- name: aws-user
  user:
    client-certificate: /etc/kubernetes/pki/users/aws-user/aws-user.crt
    client-key: /etc/kubernetes/pki/users/aws-user/aws-user.key

current-context: test-user@development
preferences: {}
```




#  New METHODS

###################################################################################################

## New methode to create an user in kubernetes

1. L'utilisateur qui vient d'intégrer l'entreprise doit générer un paire de clé sur sa machine.

```
openssl genrsa -out mokai.key 2048
openssl req -new -key mokai.key -out mokai.csr -subj "/CN=mokai"
```

2. transformer le .csr généré en base64

```
cat mokai.csr | base64 | tr -d "\n"
```

3. create a certificateSigningRequest

```
nano mokai-csr-yaml.yaml
```
```
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  name: mokai
spec:
  request: <valeur du csr en base 64>
  signerName: kubernetes.io/kube-apiserver-client
  expirationSeconds: 86400  # one day
  usages:
  - client auth
```

## Exemple

```
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  name: mokai
spec:
  request: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ1ZUQ0NBVDBDQVFBd0VERU9NQXdHQTFVRUF3d0ZiVzlyWVdrd2dnRWlNQTBHQ1NxR1NJYjNEUUVCQVFVQQpBNElCRHdBd2dnRUtBb0lCQVFEVDgxRjZrVVR3NzVOYXJJRjNKUDU4eDM3L3R6Wkp5NExuYU54Y0xDRi9BYm5aCkJQSFBFZkYxZkhGSUJaN2k0ZG5JWmhldmMrTGxIZ0ZJUE14d1NrclJWTWVnUUFnUmhtRFV5YUtBb0NKaldpQWcKVE9veFFpQk1mZHNNVzhIN2F6YW92QjBITm9ocnFIVVBTeGRXWFJqT0F0bHgrWDdic1hINmJLc3lrb1ZGbXR1awpJOXVmSy96a0M0cnpLeTQxays2eTlJMlh2cW1nRVo0VTRzS1Z5bjNXOTFuSjBTcFpTT2FkOG96aTUyQ2x6c0w4CmozTklndnM3dnpWYVhhZnB3SzdNczFpUzVhMEROMnAzMHlJTmVaNkRaNkg5SGY2SHFseEQ2VUhnamVhcWhHSWIKVVRtQ0JwZk1qaWJLVEZKNTZ6cE5POHZMT01ubURUYk5UYzlTTXBkVkFnTUJBQUdnQURBTkJna3Foa2lHOXcwQgpBUXNGQUFPQ0FRRUFvSmlLcWt3VVdocG1ZTHQyNkNrU3psbWJvQlZldzhuRVVLcGg0RzNiRmt0cTA2NFhqWSsrClJEcnkzWHFNMGlZSWw1em4zTUo2RU9qWnNHYnhGd216QjUwYzIxMWpScHIwVEE4aGFQSFU2RldXS0lDZGtycWgKMGZCQW53OXBhVFpCT2tYRVpsL0xBR3REbE1YNnZkWW1aV0hBNTM4Sk1ZaVV2VVBYM01qb21NVDZaVW8xWGs5bgpxd3NxbWNzVkdLeUFJMGNQR0VERUlvMXZDaE1rMUtaWWJEcU5aNWpVY2REbEtSY0QvUS9aZGNUTlNPV2lxWHVqCnFrNElDcFJ1MFZkb0M2TlFXbk9NT1VCWEhtdG1VSmFoNXY2aGRwcU9XVjZqYzFUbTdKNkY3Ujd6K3Fneno2QisKc0taMFBpS3BWdXJuS2xrOGZ2dm5meHpHOTF1Y2FGOGZpQT09Ci0tLS0tRU5EIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLQo=
  signerName: kubernetes.io/kube-apiserver-client
  expirationSeconds: 86400  # one day
  usages:
  - client auth
```

## Apply le fichier

```
kubectl apply -f mokai-csr-yaml.yaml
```

## pour vérifier notre csr

```
kubectl get csr
```

Nous constatons que le csr est bien crée mais est au status pending nous allons par la suite l'approuver.

```
kubectl certificate approve mokai
```
## pour vérifier notre csr

```
kubectl get csr
```

## pour visualiser le certificat en base 64

```
kubectl get csr/mokai -o yaml
```

## pour extraire le certificat signé

```
kubectl get csr mokai -o jsonpath='{.status.certificate}'| base64 -d > mokai.crt
```

## Maintenant nous allons créer un rôle et un rôle binding pour mokai

```
kubectl create role developer --verb=create --verb=get --verb=list --verb=update --verb=delete --resource=pods
```

## Maintenant nous allons créer un rôle et un rôle binding pour mokai

```
kubectl create rolebinding developer-binding-mokai --role=developer --user=mokai
```

## Ajouter au kubeConfig

```
kubectl config set-credentials mokai --client-key=mokai.key --client-certificate=mokai.crt --embed-certs=true
```

## Visualiser le context

```
kubectl config get-contexts
```

## Ajouter le context

```
kubectl config set-context mokai --cluster=kubernetes --user=mokai
```
## Visualiser le context

```
kubectl config use-context mokai
```

```
kubectl get pod
```

## creation de l'utilisateur mokai

```
sudo useradd mokai -m
sudo passwd mokai
sudo mkdir /home/mokai/.kube
sudo cp .kube/config /home/mokai/.kube/config
sudo nano /home/mokai/.kube/config   // retirer la config admin du fichier
sudo chown -R mokai:mokai /home/mokai/.kube/
sudo su - mokai
```