## Multi-container pods design patterns

### On en distingue trois:

#### 1. Co-located containers
   Dans ce model, les deux containers toursne independament dans le pods

Exemple

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
   - name: log-agent
     image: log-agent
```
#### 2. Regular init containers
   Dans ce model le container init demarre d'abord et ensuite dès qu'il finis son exécution le main contaimer prends le relais

Exemple

```
apiVersion: v1
kind: Pod
metadata:
  name: myapp-pod
  labels:
    app: myapp
spec:
  containers:
  - name: myapp-container
    image: busybox:1.28
    command: \['sh', '-c', 'echo The app is running! && sleep 3600'\]
  initContainers:
  - name: db-checker
    image: busybox
    command: 'wait-for-db-to-start.sh'
  - name: api-checker
    image: busybox
    command: 'wait-for-api-to-start.sh'
```

#### 3. Sidecar containers
   C'est exactement comme le init contaimer mais les deux continues de tourner dans le pods.

Exemple

```
apiVersion: v1
kind: Pod
metadata:
  name: myapp-pod
  labels:
    app: myapp
spec:
  containers:
  - name: myapp-container
    image: nginx
    ports:
      - containerPort: 80
  initContainers:
  - name: log-shipper
    image: busybox
    command: 'setup-log-shipper.sh'
    restartPolicy: Always  ## simple difference
```