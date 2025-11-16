## HPA

Il est question ici d'ajuster les resources d'un pods à un instant t.

### pour utiliser le VPA

```
kubectl apply -f https://github.com/kubernetes/autoscaler/releases/latest/download/vertical-pod-autoscaler.yaml
```
OU


```
kubectl apply -f vpa-crds.yml
kubectl apply -f vpa-rbac.yml

git clone https://github.com/kubernetes/autoscaler.git
cd autoscaler/
./vertical-pod-autoscaler/hack/vpa-up.sh
```

### Deploiement

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mon-deployment
  labels:
    name: mon-deployment
    app: front-end
spec:
  replicas: 1
  strategy:
   type: RollingUpdate
   rollingUpdate:
     maxUnavailable: 1
  selector:
     matchLabels:
       app: front-end
       name: myapp
  template:
    metadata:
      name: mon-pod
      labels:
        name: myapp
        app: front-end
    spec:
      containers:
      - name: nginx-container
        image: nginx:1.9.1
        ports:
         - containerPort: 8080
        resources:
          resuests:
            cpu: "250"
          limits:
            cpu: "500"
```

### VPA exemple

```
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: demo-vpa
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: mon-deployment
  updatePolicy:
    updateMode: "Auto"
  resourcePolicy:
    containerPolicies:
      - containerName: "nginx-container"
        minAllowed:
          cpu: "250m"
        maxAllowed:
          cpu: "2"
        controlledResources: ["cpu]
```


