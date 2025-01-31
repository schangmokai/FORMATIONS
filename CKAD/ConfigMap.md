
## ConfigMap dans kubernetes
Dans kubernetes, on distingue plusieurs types de service

- Service de type ClusterIP

```
apiVersion: v1
kind: Service
metadata:
  name: monservice
  labels:
    name: demoservice
    app: front-end
spec:
  type: ClusterIP
  ports:
    - port: 80
      targetPort: 80
  selector:
    name: demopod
    app: front-end

```

- Service de type NodePort
- Service de type LoadBalancer



### pour accéder à un service de type cluster IP dans un pod

```
curl http://monservice.default.svc.cluster.local:80
ou 
curl monservice:80
```