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


### FIN