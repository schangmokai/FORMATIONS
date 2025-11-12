## Priority Classes

C'est un nouvel objet dans kubernetes qui ne depends pas d'un namespace et qui définis la priorité des differents composant kubernetes 

1. composant kubernetes
2. Databases
3. critical Apps
4. Jobs
5. etc

NB: La priorité est définie entre  [-2.147.483.648 et  1.000.000.000]
Pour lister les priority Classes

### creation d'une priorityClass

```
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: high-priority
value: 1000000000
description: "priority class for mission critical pods"
```

### comment ajouter notre priority class à un pod

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
   priorityClassName: high-priority
```

### pour lister
```
kubectl get priorityclass
```

### que ce passe t'il si nous avons un enssemble de pod avec priorité 9 déjà deployé et que nous souhaitons déployer de nouveaux pods avec la priorité 10

Par defaut: 

```
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: high-priority
value: 1000000000
description: "priority class for mission critical pods"
preemptionPolicy: PreemptLowerPriority
```

### preemptionPolicy: PreemptLowerPriority

C'est à dire de retirer les pods ayant la plus basse priorité e de remettre ceux ayant la plus grande priorité.

### Si nous voulons conserver les pods déjà déployé

```
preemptionPolicy: never
```

### pour voir les pods par ordre de priorité

```
kubectl get pods -o custom-columns="NAME:.metadata.name,PRIORITY:.spec.priorityClassName"
```
