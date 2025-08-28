## KUBE-PROXY

Il est responssable après la creation de chaque service d'identifier les pods selectionner par ce service, modifier les table de routages;
pour permetre la communication entre un utilisateur externe et le pod.

Dans un cluster kubernetes grace au CNI les pods communiques entre eux car ils sont dans un même reseau de pod.

Situation.

Quand on crée un service, qui n'est pas dans le reseaux de pods, comment est ce qu'il fait pour router le traffic du client vers le bon pod.

Solution: kube-proxy

### visualisation les process démarré du kube-proxy

```
ps -aux |  grep kube-proxy
```