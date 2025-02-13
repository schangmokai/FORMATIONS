## LivenessProbe

Ces containers sont utilisés dans les pods multi-containers et lorsque le pod est lancé, si le initContainer n'a pas completement demarré, auccun n'autre containers n'est démarré.


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