## TAINT ET TOLERATIONS

Par defaut, tous les pods peuvent êtres déployé sur n'importe quel noeuds.

Si nous souhaiton qu'pods soit déployé sur un noeuds bien précis, ou alors qu'un noeuds n'acepte qu'un pod bien précis alors la notion de taint et de tolerence entre en jeux.

La taint est implémenté sur le noeud, et la tolerence sur le pods.

### pour tainter un noeud la commande est la suivanate.

```
kubectl taint node node-name key=value:taint-effect
```

### on distingue 3 taint-effect

1. NoSchedule
2. PreferNoSchedule
3. NoExecute

Exemple

```
kubectl taint nodes knode01 app=blue:NoSchedule
```

### pour ajouter une tolération à un pods pour un noeuds ayant une taint


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
   tolerations:
   - key: "app"
     operator: "Equal"
     value: blue
     effect: NoSchedule
```

NB: un pod ayant une tolérence peut bien être déployé sur un noeuds sans tolérence.

### pour retirer une tainture

```
kubectl taint nodes node1 key1=value1:NoSchedule-
```

