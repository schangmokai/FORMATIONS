
## Installation du Kubernetes Dashboard (UI d'administration)

Le Kubernetes Dashboard est une interface web qui permet de gérer les ressources de ton cluster. Voici les étapes pour l'installer et y accéder


### 1. Installer le Kubernetes Dashboard

```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```
### 2. Vérifie que les pods du Dashboard tournent correctement :

```
kubectl get pods -n kubernetes-dashboard
```

### 3. Créer un compte admin pour accéder au Dashboard

file role-admin-user.yaml

```
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
```

### 4. Vérifie que l'utilisateur a bien été créé :

```
kubectl get serviceaccount -n kubernetes-dashboard
```

### 5. Récupérer le Token d'accès

```
kubectl -n kubernetes-dashboard create token admin-user
```
Copier le token dans un fichier il sera demandé pour ouvrir le dashboad

### 6. Démarrer un proxy pour accéder au Dashboard

```
kubectl proxy
```
### 7. Dans le navigateur, tapper le lien ci-dessous

```
http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
```

Copie coller le token de l'étape précédente


### 8. Exposer le Dashboard via un NodePort (si besoin d'accès externe)

Si tu veux accéder au Dashboard sans passer par kubectl proxy, tu peux modifier le service pour l'exposer en NodePort :

```
kubectl edit svc kubernetes-dashboard -n kubernetes-dashboard
```

```
kubectl get svc -n kubernetes-dashboard
```

```
https://<node-ip>:<nodeport>
```

























