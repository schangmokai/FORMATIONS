## NodeAffinity dans kubernetes


Generalement dans kubernetes, pour faire de sorte qu'un pod s'exécute sur un noeud bien précis, nous pouvons utiliser les NodeAffinity.


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
   affinity:
     nodeAffinity:
       requiredDuringSchedulingIgnoredDuringExecution:
         nodeSelectorTerms:
         - matchExpressions:
           - key: size
             operator: NotIn
             values:
             - Small
```

On distingue plusieurs type de NodeAffinity

1. requiredDuringSchedulingIgnoredDuringExecution
2. requiredDuringSchedulingRequiredDuringExecution
3. preferredDuringSchedulingIgnoredDuringExecution


## Comme operator on distingue:

1. In
2. NotIn
3. Exists