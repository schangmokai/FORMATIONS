## Security Primitive

## 1. kube-apiserver

C'est la première ligne de defence qsur notre cluster kubernetes

### Les bonne question

### Qui peut accéder à notre API-Serveur

#### 1. Service account
Creation d'un service account pour des machines (Ordinateur, Application ...)

#### 2. Static token file

#### 3. certificates

#### 4. External authentification providers - LDAP

### Que peut-il faire

#### 1. RBAC Authorization

#### 2. ABAC Authorization

#### 3. Node Authorization

#### 4. Webhook Mode

Toutes les communication à l'intérieurs du cluster kubernetes doivent êtres chiffrées.

## 2- Les differents utiliateurs à sécuriser pour accéder au cluster kubernetes

#### 1. Admins  (User)

#### 2. Developers  (User)

#### 3. Services (Bots) (Service Accounts)

