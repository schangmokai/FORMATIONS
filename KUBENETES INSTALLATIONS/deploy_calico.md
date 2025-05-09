
## Calico deploy

https://docs.tigera.io/calico/latest/getting-started/kubernetes/self-managed-onprem/onpremises

```
curl https://raw.githubusercontent.com/projectcalico/calico/v3.29.2/manifests/calico.yaml -O
```
```
kubectl apply -f calico.yaml
```