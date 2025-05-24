
## installation

```
helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard --create-namespace --namespace kubernetes-dashboard
```

## desinstaller

```
helm delete kubernetes-dashboard --namespace kubernetes-dashboard
```

## Pod security policy and admission

```
kubectl label --overwrite ns kubernetes-dashboard pod-security.kubernetes.io/enforce=baseline
```

## Pour y accéder

## NB: Le service a exposer dans l'ingress est le "kubernetes-dashboard-kong-proxy" au 443

et dans l'ingress bien vouloir ajouter

```
annotations:
    nginx.ingress.kubernetes.io/backend-protocol: "HTTPS"
    nginx.ingress.kubernetes.io/ssl-passthrough: "true"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
```

### 1. 📦 Créer un ServiceAccount

```
kubectl create serviceaccount dashboard-admin-sa -n kubernetes-dashboard
```

### 2. 🔐 Lui donner les droits d’administrateur

```
kubectl create clusterrolebinding dashboard-admin-sa \
  --clusterrole=cluster-admin \
  --serviceaccount=kubernetes-dashboard:dashboard-admin-sa
```

### 3. 🔑 Générer le token (méthode recommandée sur Kubernetes ≥1.24)

```
kubectl -n kubernetes-dashboard create token dashboard-admin-sa --duration=1999h
kubectl -n kubernetes-dashboard create token dashboard-admin-sa --duration=24h
```
