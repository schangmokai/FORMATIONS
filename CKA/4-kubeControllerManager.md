## Le kube controller Manager

On distingue plusieurs controllers dans le controller Manager en fonction de la réssource qui est gérée.

1. node Controller
2. service Controller
3. service-account Controller
4. deployment Controller
5. replicaset Controller
6. pv Controller
7. pvc Controller
8. job Controller
9. cronJob Controller
10. namespace Controller
11. etc

### Configuration du controller manager

Il est possible dans le controller manager de selectionner les controller à activer.

### visualisation les process démarré de kube-controller-manager

```
ps -aux |  grep kube-controller-manager
```

