
## Ressource dans kubernetes

- Dans kubernetes, chaque noeud à un nombre de limité de ressource en terme de CPU et de RAM
- Il en est de même pour chaque namespace (CPU et de RAM)
- Pareil pour chaque pod (CPU et de RAM)


## Dimensionnement de ressource dans kubernetes

Pour éviter qu'une application consommant beaucoup de ressources (CPU, RAM) n’épuise celles allouées au nœud, au namespace ou au pod, il est primordial de définir, pour chaque ressource, la quantité nécessaire à son fonctionnement ainsi qu'une limite à ne pas dépasser.

##  Définition d'un pod avec les ressources

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
         cpu: 100m
       limits:
         memory: "2Gi"
         cpu: 200m
```

- NB : Par défaut, lorsque les ressources ne sont pas définies, le pod peut consommer jusqu'à la limite des ressources allouées au nœud.

Il est possible de définir les ressource par defaut a alouer à un pods.

### LimitRange

```
apiVersion: v1
kind: LimitRange
metadata:
  name: cpu-resource-constraint
spec:
   limits:
   - default: 
       cpu: 500m
     defaultRequest:
       cpu: 500m
     max:
       cpu: "1"
     min:
       cpu: 100m
     type: Container
```