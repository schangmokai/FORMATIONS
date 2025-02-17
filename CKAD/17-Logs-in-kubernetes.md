## Logs

Les logs son utiliser pour visualiser le comportement de notre application.

1. Si mon pod à un seul container

```
apiVersion: v1
kind: Pod
metadata:
  name: monpod
  labels:
    name: demopod
    app: front-end
spec:
   containers:
   - name: demopod
     image: nginx
     ports:
       - containerPort: 8080
```

Pour visualiser les logs

```
kubectl logs monpod --follow
```

2. Si mon pod à plusieurs containers il faut spécifier le nom du container par exemple democontainer1

```
apiVersion: v1
kind: Pod
metadata:
  name: monpod
  labels:
    name: demopod
    app: front-end
spec:
   containers:
   - name: democontainer1
     image: nginx
     ports:
       - containerPort: 8080
   - name: democontainer2
     image: nginx
     ports:
       - containerPort: 80
```

```
kubectl logs monpod democontainer1 --follow
```