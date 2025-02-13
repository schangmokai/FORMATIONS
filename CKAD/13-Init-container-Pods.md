## Pod multi containers

Il s'agit la d'un pod dans lequel nous pouvons retrouver plusieurs containers

On distingue le pod SideCar,Adapter, Abassadeur ..

### SIDECAR

c'est un container dans notre pods qui peut par exemple collecter les logs pour un system de visualisation des logs

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
       - containerPort: 80
   - name: log-agent
     image: log-agent
```


### ADAPTER

C'est un contaiter qui recupère par exemple les logs collectté par le SIdeCar et le transforme a un format bien lisible.

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
       - containerPort: 80
   - name: log-agent
     image: log-agent
   - name: log-transform
     image: log-transform
```

### AMBASSADEUR

c'est en realité un container proxy pour la connexion de notre applications aux differentes base de données en fonction de l'environneement dans lequel il est déployé.


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
       - containerPort: 80
   - name: log-agent
     image: log-agent
   - name: log-transform
     image: log-transform
   - name: proxy-database
     image: proxy-database
```