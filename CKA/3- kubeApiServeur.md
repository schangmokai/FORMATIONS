## API SERVEUR

C'est le point de reference dans kubernetes. Toutes les requêtes passent par l'api server.

### les grandes responsabilités du kube API Server

1. Authentification de l'utilisateur
2. validation de la requête
3. recupération des données dans l'ETCD
4. mise à jours de l'ETCD
5. Planifier
6. Kubelet


### visualisation les process démarré de kube-apiserver

```
ps -aux |  grep kube--apiserver
```