
# Hvault et rotation de credential avec Java

### Lien de téléchargement de Hvault

https://developer.hashicorp.com/vault/install

### mise à jours des repos
```
wget -O - https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
```

```
###############################################################################
#########################  VAULT DEV       ####################################
###############################################################################
```


### installation de hvault
Intallation
```
sudo apt update && sudo apt install vault
```
Vérification de la version de vault

```
vault -version
```

### 1. démarrage de vault en mode DEV

```
vault server -dev -dev-root-token-id=root &
```

NB: le & a la fin c'est pour le mettre en background

```
export VAULT_ADDR='http://127.0.0.1:8200'
```

```
vault status
```

```
vault VAULT_TOKEN="root"
```

Pour voir les informations du token que nous avon sexporté

```
vault token lookup
```


```
###############################################################################
#########################  VAULT PROD      ####################################
###############################################################################
```

```
export VAULT_ADDR=http://10.145.2.220:8200
vault status
sudo vault server -config=/etc/vault.d/vault.hcl
sudo vault server -config=/etc/vault.d/vault.hcl &
vault status
export VAULT_ADDR=http://10.145.2.220:8200
vault status
```


### 2. démarrage de vault en mode PROD pour avoir de la persistance de la données

première des choses c'est de modifier le fichier de configuration /etc/vault.d/vault.hcl

```
cat /etc/vault.d/vault.hcl
```

On active l'UI

```
ui = true
```

On active le stockage par defaut

```
storage "file" {
path = "/opt/vault/data"
}
```
On active le mode https

```
# HTTPS listener
listener "tcp" {
  address       = "0.0.0.0:8200"
  tls_cert_file = "/opt/vault/tls/tls.crt"
  tls_key_file  = "/opt/vault/tls/tls.key"
}
```

CONTENU DU FICHIER

```
# Copyright (c) HashiCorp, Inc.
# SPDX-License-Identifier: BUSL-1.1

# Full configuration options can be found at https://developer.hashicorp.com/vault/docs/configuration

ui = true

#mlock = true
#disable_mlock = true

storage "file" {
  path = "/opt/vault/data"
}

#storage "consul" {
#  address = "127.0.0.1:8500"
#  path    = "vault"
#}

# HTTP listener
#listener "tcp" {
 # address = "127.0.0.1:8200"
 # tls_disable = 1
#}

# HTTPS listener
listener "tcp" {
  address       = "0.0.0.0:8200"
  tls_cert_file = "/opt/vault/tls/tls.crt"
  tls_key_file  = "/opt/vault/tls/tls.key"
}

# Enterprise license_path
# This will be required for enterprise as of v1.8
#license_path = "/etc/vault.d/vault.hclic"

# Example AWS KMS auto unseal
#seal "awskms" {
#  region = "us-east-1"
#  kms_key_id = "REPLACE-ME"
#}

# Example HSM auto unseal
#seal "pkcs11" {
#  lib            = "/usr/vault/lib/libCryptoki2_64.so"
#  slot           = "0"
#  pin            = "AAAA-BBBB-CCCC-DDDD"
#  key_label      = "vault-hsm-key"
#  hmac_key_label = "vault-hsm-hmac-key"
#}



```

Pour lancer vault en mode prod

```
sudo vault server -config=/etc/vault.d/vault.hcl &
```

Après le demarrage, pareil:

```
export VAULT_ADDR='https://127.0.0.1:8200'
```

```
export  VAULT_SKIP_VERIFY='true'
```

```
vault status
```

On verra que notre serveur est UP mais pas Sealed

Initialisation de vault

```
vault operator init
```
Il va nous donner les différents init key (5 exactements) bien vouloir les sauvegarder
Vault sera UP mais toujours Sealed.

```
vault operator unseal key1
vault operator unseal key2
vault operator unseal key3
```

## Rotation des credentials dans Hvault

### Activation d'un backend database


```
vault login
```

et entré le token

```
vault secrets enable database
```

### configuration du backend database pour crm


```
vault write database/config/crm-database \
    plugin_name=mysql-database-plugin \
    connection_url="{{username}}:{{password}}@tcp(127.0.0.1:3307)/" \
    allowed_roles="crm-database-role" \
    username="root" \
    password="4L1qUwzRfWG8PW-E"
```

```
vault write database/roles/crm-database-role \
    db_name=crm \
    creation_statements="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}'; GRANT ALL PRIVILEGES ON crm.* TO '{{name}}'@'%';" \
    default_ttl="60m" \
    max_ttl="90m"
```

### configuration du backend database pour crmbackend


```
vault write database/config/crmbackend-database \
    plugin_name=mysql-database-plugin \
    connection_url="{{username}}:{{password}}@tcp(127.0.0.1:3307)/" \
    allowed_roles="crmbackend-database-role" \
    username="root" \
    password="4L1qUwzRfWG8PW-E"
```

```
vault write database/roles/crmbackend-database-role \
    db_name=crmbackend \
    creation_statements="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}'; GRANT ALL PRIVILEGES ON crmbackend.* TO '{{name}}'@'%';" \
    default_ttl="60m" \
    max_ttl="90m"
```

### configuration du backend database pour crmtransaction


```
vault write database/config/crmtransaction-database \
    plugin_name=mysql-database-plugin \
    connection_url="{{username}}:{{password}}@tcp(127.0.0.1:3307)/" \
    allowed_roles="crmtransaction-database-role" \
    username="root" \
    password="4L1qUwzRfWG8PW-E"
```

```
vault write database/roles/crmtransaction-database-role \
    db_name=crmtransaction \
    creation_statements="CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}'; GRANT ALL PRIVILEGES ON crmtransaction.* TO '{{name}}'@'%';" \
    default_ttl="60m" \
    max_ttl="90m"
```


### Interaction avec Hvaul pour la recupération des credential

```
vault read database/creds/crm-database-role
```

### ceation de backend de type KV

```
vault secrets enable kv
```

```
vault kv put secret/crm-service-backend user.username=mokai user.password=12345
```

## Point très crusial comment récupérer en toutes sécurité le root token pour lui donner à application spring-boot.


```
Creation de la methode authentification kubernetes dans hvault:
===============================================================
```

```
apiVersion: v1
kind: ServiceAccount
metadata:
  name: cbs-app-sa
  namespace: default
```

---

```
apiVersion: v1
kind: Secret
metadata:
  name: cbs-app-sa-token
  namespace: default
  annotations:
    kubernetes.io/service-account.name: cbs-app-sa
type: kubernetes.io/service-account-token
```

---
```
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: cbs-app-sa-role-binding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: system:auth-delegator
subjects:
  - kind: ServiceAccount
    name: cbs-app-sa
    namespace: default
```


```
vault secrets enable -path=secret kv
vault kv put secret/database username="mokai" password="mokai"
vault kv put secret/database/crm username="mokai" password="mokai"
vault kv put secret/database/crmbackend username="mokai" password="mokai"
vault kv put secret/database/crmtransaction username="mokai" password="mokai"
vault kv get secret/database/crm
vault kv get secret/database/crmbackend
vault kv get secret/database/crmtransaction
```

vault auth enable kubernetes



# kubectl cluster-info  pour avoir IP de kubernetes'

```
url_kubernetes: https://192.168.56.10:6443
```

# Récupération du cbs-app-sa-token.jwt

```
kubectl get secret cbs-app-sa-token -n default -o jsonpath="{.data.token}" | base64 --decode
kubectl get secret cbs-app-sa-token -n default -o jsonpath="{.data.ca\.crt}" | base64 --decode
```

# creer la config kubernetes dans hvault

```
vault write auth/kubernetes/config \
token_reviewer_jwt="cbs-app-sa-token.jwt" \
kubernetes_host="https://192.168.56.10:6443" \
kubernetes_ca_cert="kubernetes-ca.crt"
```

vault read auth/kubernetes/config


# creer un policy

```
vault policy write cbs-app-policy - <<EOF

path "secret/database/crm" {
capabilities = ["read"]
}

path "secret/database/crmbackend" {
capabilities = ["read"]
}

path "secret/database/crmtransaction" {
capabilities = ["read"]
}

path "database/config/crm-database" {
capabilities = ["read", "list", "create", "update"]
}

path "database/roles/crm-database-role" {
capabilities = ["read", "list", "create", "update"]
}

path "database/config/crmbackend-database" {
capabilities = ["read", "list", "create", "update"]
}

path "database/roles/crmbackend-database-role" {
capabilities = ["read", "list", "create", "update"]
}

path "database/config/crmtransaction-database" {
capabilities = ["read", "list", "create", "update"]
}

path "database/roles/crmtransaction-database-role" {
capabilities = ["read", "list", "create", "update"]
}
EOF
```

# Lister et visualiser un policy

```
vault policy list
vault policy read cbs-app-policy
```

# createion du role authentification cbs-app-role

```
vault write auth/kubernetes/role/cbs-app-role \
bound_service_account_names=cbs-app-sa \
bound_service_account_namespaces=default \
policies=cbs-app-policy \
ttl=24h
```
vault read auth/kubernetes/role/cbs-app-role
vault read auth/kubernetes/role/cbs-app-role

# pour ajouter un autre namespace à un role

```
vault write auth/kubernetes/role/cbs-app-role \
bound_service_account_names=cbs-app-sa \
bound_service_account_namespaces=default,cbs-prod,new-namespace \
policies=cbs-app-policy \
ttl=24h
```

vault read auth/kubernetes/role/cbs-app-role
vault read auth/kubernetes/role/cbs-app-role


```
###############################################################################
#########################  VAULT PROD  WITH NGINX    ##########################
###############################################################################
```


## Vault pour un environnement de prod

### 📁 Arborescence

```
hvault-reverse-proxy/
├── docker-compose.yml
├── nginx/
│   ├── nginx.conf
│   ├── ssl/
│   │   ├── cert.pem
│   │   └── key.pem
```

### 1. 🔐 Génère ou place ton certificat SSL

```
mkdir -p nginx/ssl
openssl req -x509 -nodes -days 89365 -newkey rsa:2048 \
  -keyout nginx/ssl/key.pem \
  -out nginx/ssl/cert.pem \
  -subj "/CN=10.145.2.220"
```

### 2. ⚙️ Fichier nginx.conf

```
events {}

http {
  server {
    listen 443 ssl;
    server_name 10.145.2.220;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    ssl_protocols TLSv1.2 TLSv1.3;

    location / {
      proxy_pass http://10.145.2.220:8200;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto https;
    }
  }
}

```

### 3. 🐳 Fichier docker-compose.yml

docker-compose.yml

```
services:
  nginx:
    image: nginx:alpine
    container_name: nginx-vault-proxy
    ports:
      - "443:443"  # HTTPS exposé sur port 443
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    networks:
      - devops_network_app 
networks:
  devops_network_app:
    driver: bridge
```

### ▶️ Étape 5 : Lancer vault

```
docker compose up -d
```

```
curl -k https://10.145.2.220
https://10.145.2.220
```

### cration des utilisateurs dans hvault

0. login

```
export VAULT_ADDR='http://127.0.0.1:8200'
vault login
```

1. Activation du moteur d'authentification
```
vault auth enable userpass
```
2. creation d'un utilisateur admin
```
vault write auth/userpass/users/admin password="MonSuperMotDePasse"
```
3. creation d'une policy pour l'admin
```
# admin.hcl
path "*" {
capabilities = ["create", "read", "update", "delete", "list", "sudo"]
}
```
4. charger la policy dans vault
```
vault policy write admin admin.hcl
```
5. associer la policy à l'utilisateur admin

```
vault write auth/userpass/users/admin policies="admin"
```

NB: si l'utilisateur doit avoir uniquement accès à certaines url:

à l'étape 3 creation d'une policy nous devons avoir ceci:

```
# user-secrets.hcl

# Donner accès en lecture au mot de passe DB
path "secret/data/app/db-password" {
  capabilities = ["read"]
}

# Donner accès en lecture à l’API Key
path "secret/data/app/api-key" {
  capabilities = ["read"]
}
```

### Pour mettre à jours une policy existante

1. verifier le contenu de la policy
```
vault policy read user-secrets
```

2. mise à jours du contenu du fichier

```
#user-secrets.hcl

# Accès existants
path "secret/data/app/db-password" {
  capabilities = ["read"]
}

path "secret/data/app/api-key" {
  capabilities = ["read"]
}

# Nouveau secret ajouté
path "secret/data/app/config" {
  capabilities = ["read"]
}

```

3. charger la nouvelle policy

```
vault policy write user-secrets user-secrets.hcl
```



