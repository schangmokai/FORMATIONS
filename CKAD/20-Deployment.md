## deployment dans kubernetes

Dans kubernetes pour deployer nos applications et s'assurer leur disponibilité, nous utilisons les deployment

### Pour créer un deployment

```
kubectl create deployment mon-deployment --image=nignbx
```

### Pour avoir le status d'avancement de mon processus de deployment

```
kubectl rollout status  deployment mon-deployment
```

### Pour l'historique des deployments

```
kubectl rollout histpry  deployment mon-deployment
```

### Il est possible de modifier l'image dans une deployment



```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 3
  strategy:
   type: RollingUpdate
   rollingUpdate:
     maxUnavailable: 1
  selector:
     matchLabels:
       app: front-end
       name: myapp
  template:
    metadata:
      name: mon-pod
      labels:
        name: myapp
        app: front-end
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.9.1
        ports:
         - containerPort: 8080
```

```
kubectl set image deployment nginx-container=nginx:16.2
```

### pour revenir sur une version en arriere de notre deployment

```
kubectl rollout undo deplyment mon-deployment
```

### pour revenir sur une version précise de notre deployment

```
kubectl rollout history deployment mon-deployment --revision=1
```

## On distingue plusieurs stratégie de deployment

## 1. Recreate

   Dans cette strategie, tous les pods sont arrettés ensuite les nouveaux pods sont recrés.

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 3
  strategy:
   type: Recreate
  selector:
     matchLabels:
       app: front-end
       name: myapp
  template:
    metadata:
      name: mon-pod
      labels:
        name: myapp
        app: front-end
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.9.1
        ports:
         - containerPort: 8080
```

## 2. rolling-update (c'esst la stratégie de deploiement par defaut dans kubernetes)

   Dans cette stratégie, un pod est arrêté et remplacé par la nouvelle version ...

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 3
  strategy:
   type: RollingUpdate
   rollingUpdate:
     maxUnavailable: 1
  selector:
     matchLabels:
       app: front-end
       name: myapp
  template:
    metadata:
      name: mon-pod
      labels:
        name: myapp
        app: front-end
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.9.1
        ports:
         - containerPort: 8080
```

## 3. blue green

La stratégy blue/green consiste en quoi : 

Nous avons un deploiement(blue) et un service. Le deploiement expose des pods avec le label égal version: v1

On crée un autre deployment appelé green qui lui expose des pods avec label égal version: v2

Une fois que le deuxième déployment est up, testé et validé, on change juste le label du service en version: v2 pour qu'il pointe sur le nouveau deploiement.

DEMO:

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 3
  selector:
     matchLabels:
       version: v1
  template:
    metadata:
      name: mon-pod
      labels:
        version: v1
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.0
        ports:
         - containerPort: 8080
```
Service

```
apiVersion: v1
kind: Service
metadata:
  name: monservice
spec:
  selector:
    version: v1
```

ICI le service pointe sur le deploiement v1

Créeont maintenant le deploiement v2

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 3
  selector:
     matchLabels:
       version: v1
  template:
    metadata:
      name: mon-pod
      labels:
        version: v2
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.0
        ports:
         - containerPort: 8080
```

changons notre service pour quil pointe sur v2

```
apiVersion: v1
kind: Service
metadata:
  name: monservice
spec:
  selector:
    version: v2
```


## 4. Canary

Elle consiste à créer un deploiement avec une instance du pods en v2 ensuite router un pourcentage du traffic sur ce pourcentage et par la fin si tous les tests son ok router le reste du trafic sur les nouveaux pods.

Le premier déploiement aura 3 pods et le deuxiement deploiement(canary) aura un pod v2 dans le deuxieme deploiement je vais ajouter un label qui sera également commun au premier deploiement.

app: front-end

Mon service va selectionner les pods avec le nouveau label app: front-end.

DEMO:

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 3
  selector:
     matchLabels:
       version: v1
       app: front-end
  template:
    metadata:
      name: mon-pod
      labels:
        version: v1
        app: front-end
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.0
        ports:
         - containerPort: 8080
```

Creont maintenant le déploiement v2

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 1
  selector:
     matchLabels:
       version: v2
       app: front-end
  template:
    metadata:
      name: mon-pod
      labels:
        version: v2
        app: front-end
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.0
        ports:
         - containerPort: 8080
```

Changeons notre service pour quil pointe sur les deux déploiements

```
apiVersion: v1
kind: Service
metadata:
  name: monservice
spec:
  selector:
    app: front-end
```