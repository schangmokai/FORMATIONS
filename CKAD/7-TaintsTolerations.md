## Taint et Tolerance dans kubernetes


La définition des taint se fait sur les noeud et la tolération se fait sur les pods

Pour tainter un noeud on utilise la commande suivante:

```
kubectl taint nodes knode1 key=value:taint-effect
```

## taint-effect

1. NoSchedule
2. PreferNoSchedule
3. NoExecute

### Exemple

```
kubectl taint nodes knode1 app=blue:NoSchedule
```

Vue que le noeud à un taint si je veux quand même qu'un pod se deploie sur ce noeud, le pod doit avoir une tolerations égale à la valeur définie dans la taint.

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
       value: "blue"
       effect: "NoSchedule"
```

Généralement ont utilise les taints et les tolération pour dedie des noeuds a n'accepter que certaines applications.

NB: Un pods qui a une tolération peut bien s'exécuter sur un noeud qui n'a pas de taint.

Par contre si je veux qu'un pod s'exécute sur un noeud bien précis, je vais utiliser la notion d'afinité.

## Noeud master

Par defaut le noeud master a une taint NoSchedule.

NB: Cette taint peut être retiré pour permettre au noeud master de recevoir aussi les pods.

```
kubectl edit node master
```
Et on retire la taint
