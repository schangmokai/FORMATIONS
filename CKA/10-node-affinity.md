## NODE AFFINITY

Permet de spécifier à Kubernetes de deployer le pods sur un neoud ayant une particularité bien précise.

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
             operator: In
             values:
             - Large
             - Medium
             
             Ou encore
             
         - key: size
             operator: NotIn
             values:
             - Small
             
             Ou encore
             
         - key: size
             operator: Exists
```

NB: la cle size correspond au label du noeud kubernetes

### On distingue deux type de node affinity (Available)

1. requiredDuringSchedulingIgnoredDuringExecution
2. preferredDuringSchedulingIgnoredDuringExecution

### Et un seul Planned

1. requiredDuringSchedulingRequiredDuringExecution

