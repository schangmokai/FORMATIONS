### cration des utilisateurs dans hvault

Avant de commencer le lancement des commandes, le super admin doit se connecter sur le serveur du coffre fort.
une fois connecté il bascule sur le compte root: 

```
sudo su
```
Ensuite sur le compte ubuntu

```
sudo ubuntu
cd 
```

0. login

```
export VAULT_ADDR='http://127.0.0.1:8200'
vault login
```
Ici il faut entrer le token root pour faire des manipulations. 

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