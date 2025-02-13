
## SecurityContext dans kubernetes

Dans kubernetes, nous utilisons securityContext pour définir les utilisateurs qui lancent un pod


## Définition d'un pod simple

Lorsque le securityContext est défini au niveau des spec alors il est appliqué sur tous les containers du pod

Pod Simple en root

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
     image: ubuntu
     command: ["sleep", "3600"]
```

```
kubectl exec -ti monpod -- whoami
```

Pod avec securityContext

```
apiVersion: v1
kind: Pod
metadata:
  name: monpod
  labels:
    name: demopod
    app: front-end
spec:
   securityContext:
     runAsUser: 1000
     runAsGroup: 3000
     fsGroup: 2000
     supplementalGroups: [4000]
   containers:
   - name: demopod
     image: ubuntu
     command: ["sleep", "3600"]
```

```
kubectl exec -ti monpod -- whoami
```

##  Définition d'un pod avec securityContext au niveau du container

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
     image: ubuntu
     command: ["sleep", "3600"]
     securityContext:
       runAsUser: 1000
```

```
kubectl exec -ti monpod -- whoami
```

##  Définition d'un pod avec securityContext au niveau du container et des Spec

```
apiVersion: v1
kind: Pod
metadata:
  name: monpod
  labels:
    name: demopod
    app: front-end
spec:
   securityContext:
     runAsUser: 1001
   containers:
   - name: demopod
     image: ubuntu
     command: ["sleep", "3600"]
     securityContext:
       runAsUser: 1000
```

### Pour voir l'utilisateur qui est run dans le pod

```
kubectl exec -ti monpod -- whoami
```

##  SecurityContext Capabilities

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
     image: ubuntu
     command: ["sleep", "3600"]
     securityContext:
       runAsUser: 1000
       capabilities:
         add: ["MAC_ADMIN"]
```

