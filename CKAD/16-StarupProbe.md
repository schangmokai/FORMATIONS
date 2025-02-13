## LivenessProbe

C'est une sonde dans kubernetes qui entre en jeux une fois que l'application est ready et vérifie l'état de l'application a des intervals régulier.

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
       - containerPort: 8080
```

Pour vérifier que l'application est up le développeur sait quelle commande ou alors quelle requête envoyer à l'application.

1. HTTP TEST (application web api, ...)

```
livenessProbe:
  httpGet:
    path: /healthz
    port: 8080
    httpHeaders:
    - name: Custom-Header
      value: Awesome
  initialDelaySeconds: 3
  periodSeconds: 3
```
### Exemple

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
     image: mon-app
     ports:
       - containerPort: 8080
     livenessProbe:
       httpGet:
         path: /api/ready
         port: 8080
         httpHeaders:
         - name: Custom-Header
           value: Awesome
       initialDelaySeconds: 3
       periodSeconds: 3
```

Donc Dès le pod va démarré et sera ready, kubernetes vas envoyer une requête http sur le port 8080 à l'url /api/ready et par defaut vas redemarrer le container à l'intérieur du pod si celui-ci echoue.

2. TCP TEST (base de données)

```
livenessProbe:
  tcpSocket:
    port: 3306
 ```

### Exemple

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
     image: mysql
     ports:
       - containerPort: 3306
     livenessProbe:
       tcpSocket:
         port: 3306
```

3. EXEC COMMAND (pour exécuter une commande

```
livenessProbe:
  exec:
    command:
      - cat
      - /app/is_ready
 ```

### Exemple

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
     livenessProbe:
       exec:
         command:
           - cat
           - /app/is_ready
```

Si tu sais que initialement ton application peut mettre 10 Seconde pour démarré, alors dans ton livenessProbe, le initialDelaySeconds doit être supérieur au initialDelaySeconds du readynessProbe

```
initialDelaySeconds: 10
periodSeconds: 5
failureThreshold: 6
```

initialDelaySeconds: 10 (10 est le temps au bout duquel on fait le premier check)

periodSeconds: 5  (5 est le nombre de séconde qui sépare deux checks)

failureThreshold: 6 (par defaut la valeur c'est 3 ici 6 est le nombre d'echec au bout duquel le pod sera au status faild)