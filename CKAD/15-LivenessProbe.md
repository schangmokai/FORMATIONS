## ReadinessProbe

C'est une sonde dans kubernetes qui vérifie que l'application (container dans le pod) est up et prêt à recevoir le trafic avant de mettre le pod running.

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

Pour vérifier que l'application est up le développeur sais quelle commande ou alors quelle requête envoyer à l'application.

1. HTTP TEST (application web api, ...)

```
readinessProbe:
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
     readinessProbe:
       httpGet:
         path: /api/ready
         port: 8080
         httpHeaders:
         - name: Custom-Header
           value: Awesome
       initialDelaySeconds: 3
       periodSeconds: 3
```

Donc Dès le pod va démarré, kubernetes vas envoyer une requête http sur le port 8080 à l'url /api/ready s'il a un retour 200 alors il met le status du pod à ready.

2. TCP TEST (base de données)

```
readinessProbe:
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
     readinessProbe:
       tcpSocket:
         port: 3306
```

3. EXEC COMMAND (pour exécuter une commande

```
readinessProbe:
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
     readinessProbe:
       exec:
         command:
           - cat
           - /app/is_ready
```

Si tu sais que initialement ton application peut mettre 10 Seconde pour démarré, alors dans la readiness ou le liveness tu as des options possibles:

```
initialDelaySeconds: 10
periodSeconds: 5
failureThreshold: 6
```

initialDelaySeconds: 10 (10 est le temps au bout duquel on fait le premier check)

periodSeconds: 5  (5 est le nombre de séconde qui sépare deux checks)

failureThreshold: 6 (par defaut la valeur c'est 3 ici 6 est le nombre d'echec au bout duquel le pod sera au status faild)