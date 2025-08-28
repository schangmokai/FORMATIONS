## KUBELET

C'est le responsable il est comme le capitaine pour le master dans un noeud.

C'est lui qui ordone par exemple le déploiement d'un container dans un node sur la demande de l'api-server


### visualisation les process démarré de kubelet

```
ps -aux |  grep kubelet
```