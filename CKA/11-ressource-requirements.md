## LES RESSOURCES DANS KUBERNETES

Nous parlons ici des ressources comme le CPU et la RAM



1. Request

CPU: 1
RAM: 1Gi

Ceci etant configuré, si le noeuds n'a pas au moins ces ressources disponnible, le pods de peut pas être planifier sur ce noeuds.

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
     resources:
       requests:
         memory: "1Gi"
         cpu: 2
       
```

2. Limit

Spécifie la quantité de reource à ne pas depassé.

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
     resources:
       requests:
         memory: "1Gi"
         cpu: 2
       limits:
         memory: "2Gi"
         cpu: 4
```

Par defaut les ressources ne sont pas spécifiés pour un pods du coups par defaut le pods peut consommer autant de resource que dispose le noeuds.