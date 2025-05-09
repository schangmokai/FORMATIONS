
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