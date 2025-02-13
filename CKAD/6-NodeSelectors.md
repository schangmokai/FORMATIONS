## NodeSelector dans kubernetes


Generalement dans kubernetes, pour faire de sorte qu'un pod s'exécute sur un noeud bien précis, nous pouvons utiliser les NodeSeletors.


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
   nodeSelector:
     <label-key>: <label-value>
```

Le selector Se base sur un Label de node pour selectionner le Node

Pour créer un Label pour un noeud kubernetes

```
kubectl label nodes <node-name>  <label-key>=<label-value> 
```

### Exemple

```
kubectl label nodes knode1 size=Large 
```

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
   nodeSelector:
     size: Large
```