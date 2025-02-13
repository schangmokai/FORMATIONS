
## ConfigMap dans kubernetes
Dans kubernetes, Les configmaps sont utilisées pour garder les données de configuration des applications


### Impératif

- ConfigMap à partir d'un Literal (Impérative)

```
  kubectl create configmap maconfig --from-literal=USERNAME=mokai --from-literal=PASSWORD=mokai
```
- ConfigMap à partir d'un Fichier de config (Impérative)
```
  kubectl create configmap maconfig --from-file=application.properties
```

### Déclarative
- ConfigMap à partir d'un Fichier de yaml(Déclarative)
```
config-map.yaml
```
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: maconfig2
data:
  USERNAME: mokai
  PASSWORD: mokai
```

```
kubectl apply -f config-map.yaml
```
### visualisation de la liste des configMap

```
kubectl get configMap
kubectl get cm
```

### Afficher la description d'un configMap

```
kubectl describe configMap maconfig
```

### Utilisation dans des pods
- Injection d'un configMap dans un pod


### Pod simple

```
monpod.yaml
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
```

### Pod avec injection de configMap (envFrom)

```
monpod_config_map.yaml
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
     envFrom:
       - configMapRef:
           name: maconfig2
```

```
kubectl apply -f monpod_config_map.yaml
```

### Pod avec injection de configMap (env)

```
monpod_config_map.yaml
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
     env:
       - name: USERNAME
         valueFrom:
           configMapKeyRef:
             name: maconfig2
             key: USERNAME
```

```
kubectl apply -f monpod_config_map.yaml
```

### Pod avec injection de configMap (volume)

```
kubectl create configmap configdemo --from-file=application.properties --from-file=index.html
```
```
monpod_config_map.yaml
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
     volumeMounts:
       - name: config-volume
         mountPath: /usr/share/nginx/html
   volumes:
   - name: config-volume
     configMap:
       name: configdemo
```

### Pour lister les variable d'environnement dans le pod
```
kubectl exec -ti monpod -- env
```