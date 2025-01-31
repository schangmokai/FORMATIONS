
## Secret dans kubernetes
Dans kubernetes, les Secrets sont utilisés pur le stockage des données sensibles comme le password, les certificats etc.

### Impératif

- Secret à partir d'un Literal (Impérative)

```
  kubectl create secret generic monsecret --from-literal=USERNAME=mokai --from-literal=PASSWORD=mokai
```
- Secret à partir d'un Fichier de config (Impérative)
```
  kubectl create secret generic monsecret --from-file=application.properties
```

### Déclarative
- ConfigMap à partir d'un Fichier de yaml(Déclarative)

pour encode le username et le password avant de passer au secret

```
echo -n 'mokai' | base64
```

bW9rYWk=

```
secret.yaml
```
```
apiVersion: v1
kind: Secret
metadata:
  name: monsecret
data:
  USERNAME: bW9rYWk=
  PASSWORD: bW9rYWk=
```

#### pour decoder un secret
```
echo -n 'bW9rYWk=' | base64 --decode
```

```
kubectl apply -f config-map.yaml
```

### visualisation de la liste des secrets

```
kubectl get secret
```

### Afficher la description d'un secret

```
kubectl describe secret monsecret
```

### Utilisation dans des pods
- Injection d'un secret dans un pod


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

### Pod avec injection de secret (envFrom)

```
monpod_secret.yaml
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
       - secretRef:
           name: monsecret
```

```
kubectl apply -f monpod_secret.yaml
```

### Pod avec injection de secret (env)

```
monpod_secret.yaml
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
       - name: PASSWORD
         valueFrom:
           secretKeyRef:
             name: monsecret
             key: PASSWORD
```
```
kubectl apply -f monpod_secret.yaml
```



### Pod avec injection de secret (volume)

```
kubectl create secret generic secretdemo --from-file=application.properties --from-file=index.html
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
       - name: secret-volume
         mountPath: /usr/share/nginx/html
   volumes:
   - name: secret-volume
     secret:
       name: secretdemo
```

### Pour lister les variables d'environnement dans le pod
```
kubectl exec -ti monpod -- env
```