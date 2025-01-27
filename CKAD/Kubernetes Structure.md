
## Structure d'un Object dans kubernetes


```
apiVersion:
kind:
metadata:


spec:
```

## Définition d'un pod simple

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
```

##  Définition d'un pod avec variable d'environnement

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
     env:
     - name: USERNAME
       value: mokai
     - name: PASSWORD
       value: mokai
```




