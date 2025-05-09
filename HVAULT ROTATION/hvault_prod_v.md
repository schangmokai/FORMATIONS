
# Hvault et rotation de credential avec Java

### Lien de téléchargement de Hvault

https://developer.hashicorp.com/vault/install

### mise à jours des repos
```
wget -O - https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
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

1. Activer le backend d'authentification AppRole

```
vault auth enable approle
```

2. Créer un rôle AppRole

```
vault write auth/approle/role/crm-spring-app \
    token_policies="default" \
    secret_id_ttl="24h" \
    token_ttl="24h" \
    token_max_ttl="72h"
```

3. Obtenir le role_id

```
vault read auth/approle/role/crm-spring-app/role-id
```

NB: Ceci nous retourne le role_id

4. Générer le secret_id

```
vault write -f auth/approle/role/crm-spring-app/secret-id
```

NB: Ceci nous retourne le secret_id

5. Vérification de l'authentification via AppRole

```
vault write auth/approle/login role_id="<role_id>" secret_id="<secret_id>"
```

NB: Ceci retournera un token Vault si l'authentification est OK

5. Utilisation du role_id et du secret_id dans Vault Agent

Créer un fichier vault-agent.hcl

```
vault {
  address = "http://<VAULT_SERVER_IP>:8200"  # Adresse de votre serveur Vault externe
}

auto_auth {
  method "approle" {
    mount_path = "auth/approle"
    config = {
      role_id   = "<role_id>"
      secret_id = "<secret_id>"
    }
  }
  sink "file" {
    path = "/vault/token"
  }
}

log_level = "debug"

```

6. création d'un configMap à partir du fichier vault-agent.hcl

```
kubectl create configMap vault-agent-config --from-file=vault-agent.hcl
```

7. exemple de Pod utilisant ma configMap

```
apiVersion: v1
kind: Pod
metadata:
  name: spring-application-with-vault-agent
spec:
  containers:
    - name: spring-app
      image: your-spring-app-image
      volumeMounts:
        - name: vault-token
          mountPath: /vault
          readOnly: true
    - name: vault-agent
      image: hashicorp/vault:latest
      command: ["vault", "agent", "-config=/etc/vault-agent/vault-agent.hcl"]
      volumeMounts:
        - name: vault-token
          mountPath: /vault
        - name: vault-agent-config
          mountPath: /etc/vault-agent
          readOnly: true
  volumes:
    - name: vault-token
      emptyDir: {}
    - name: vault-agent-config
      configMap:
        name: vault-agent-config

```

8. Et mon bootstrap.properties ressemblera à ceci

```
spring.cloud.vault.token=file:/vault/token
spring.cloud.vault.uri=http://127.0.0.1:8200
spring.cloud.vault.scheme=http
spring.cloud.vault.database.role=spring-database-role
spring.cloud.vault.enabled=true

```

 AU lieu de ceci

```
spring.cloud.vault.host=127.0.0.1
spring.cloud.vault.port=8200
spring.cloud.vault.uri=http://127.0.0.1:8200
spring.cloud.vault.token=
spring.cloud.vault.scheme=http
spring.cloud.vault.database.role=spring-database-role
```

Avec ce qui précéde, si le pod tombe alors que le secret-id a déjà plus de 24h il ne pourra plus redemarrer. car le secret-id n'est plus valide.

Pour palier à cela, nous devons demander à notre agent vault de renouveller le secret-id 30 minute avant l'expiration du secret-id.

```
vault {
  address = "http://<VAULT_SERVER_IP>:8200"  # Adresse du Vault externe
}

auto_auth {
  method "approle" {
    mount_path = "auth/approle"
    config = {
      role_id   = "<role_id>"  # Remplacez <role_id> par votre role_id
      secret_id = "<secret_id>"  # Remplacez <secret_id> par le secret_id initial
    }
  }

  # Le Vault Agent va mettre à jour le token et le secret_id en cas d'expiration
  sink "file" {
    path = "/vault/token"  # Le token sera écrit ici
  }

  # Ce paramètre permet à Vault Agent de récupérer un nouveau secret_id lorsque le secret_id précédent expire
  renewal_window = "30m"  # Par exemple, renouveler 30 minutes avant l'expiration
}

log_level = "debug"

```

## Ajouter les certificats à mon vault

```
cd /opt/vault/tls
```

```
sudo openssl req -newkey rsa:2048 -nodes -keyout vault-key.pem -x509 -days 365 -out vault-cert.pem
```

```
```



