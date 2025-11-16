## HPA

Il est question ici d'ajouter le nombre d'instance de notre container du aux ressources

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

### pour creer un HPA pour ce deploiement

```
kubectl autoscale deployment nginx-deployment --min=1 --max=3 --cpu="80%"
```

### creation du HPA de manière declarative

```
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: mon-deployment-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: mon-deployment
  minReplicas: 1
  maxReplicas: 10
  metrics:
    - type: Resource
      resource: cpu
      target:
        type: Utilization
        averageUtilization: 50
```

