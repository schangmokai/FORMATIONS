## Labels

Les labels permettes à kubernetes de selectionner les objets


```
kubectl get pods --selector env=pro
```

```
kubectl get pods --selector env=pro,bu=mokai,name=toto
```

```
kubectl get pods --selector env=pro --no-headers | wc -l
```

```
kubectl get all -A --selector env=pro --no-headers | wc -l
```