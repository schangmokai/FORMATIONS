### Pour insttaler ArgoCD, il nous faut avoir un custer kuberetes UP.

https://argo-cd.readthedocs.io/en/stable/getting_started/

### La commande suivante permet de faire l'installation.

```
kubectl create ns argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl get svc -n argocd
kubectl patch svc argocd-server -n argocd -p '{"spec":{"type": "LoadBalancer"}}'
kubectl get svc -n argocd
kubectl port-forward service/argocd-server -n argocd 9002:443
https://localhost:9002/
```

###  Récupération du password par defaut

```
curl -sSL -o argocd-linux-amd64 https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
sudo install -m 555 argocd-linux-amd64 /usr/local/bin/argocd
rm argocd-linux-amd64
argocd admin initial-password -n argocd
7oG7Fg2mwcbF7DDt
```

## NB

Argo cd est exposé en https exemple d'ingress pour argoCD

```
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: crm-ingress-argocd
  namespace: argocd
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/backend-protocol: "HTTPS"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - cbs.argocd.prod
      secretName: crm-argocd-tls-secret
  rules:
    - host: cbs.argocd.prod
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: argocd-server
                port:
                  number: 443
```

### Creation d'une application sur ArgoCD

Vu que notre ArgoCd est directement dans le cluster Kubernetes, la premiere des chose est de s'authentifier sur argoCD avec le client argocd.
Le port format c'est juste pour une authentification avec argocd après on le desactive.
```
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

### Connexion a ArgoCd

```
argocd login localhost:8080 --username admin --password passwordadmin --insecure
```
### Ajouter 
```
argocd repo add https://10.145.40.242/devops_cbs/crm-service-authentification-chart.git \
--username tonusername \
--password tonpassword
```
Exemple

```
argocd repo add https://10.145.40.242/devops_cbs/crm-service-authentification-chart.git \
--username devops_cbs \
--password GFicTeck@2025.\
--insecure-skip-server-verification
```

Ou alors aller directement sur l'interface d'administration d'argocd.

```
Settings > Repositories > CONNECT REPO USING HTTPS
```

### FIN