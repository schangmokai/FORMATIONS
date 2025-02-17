## InitContainers

Ces containers sont utilisés dans les pods multi-containers et lorsque le pod est lancé, si le initContainer n'a pas completement demarré, auccun n'autre containers n'est démarré.


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
  - name: init-myservice
    image: busybox
    command: \['sh', '-c', 'git clone &nbsp;;'\]
```


### InitContainers faild

Si le initContainers echoue le pod va redémarrer continuellement jusqu'au succes de l'init
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
  - name: init-myservice
    image: busybox:1.28
    command: \['sh', '-c', 'until nslookup myservice; do echo waiting for myservice; sleep 2; done;'\]
  - name: init-mydb
    image: busybox:1.28
    command: \['sh', '-c', 'until nslookup mydb; do echo waiting for mydb; sleep 2; done;'\]
```