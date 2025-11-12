## Admission controller

Son rôle est d'abord d'authentifier un utilisateur


kubelet ==> authentication ==> Authorization ==> Admission Controllers ==> create pod
                                                          ||
                                                     AlsaysPullImages
                                                     EventRateLimit
                                                     NamespaceAutoProvision

### pour avoir la liste des admission controller deployer dans notre cluster

```
kube-apiserver -h | grep enable-admission-plugins
```

### Pour demarrer le plugins admission controller 

1. Dans le fichier kube-apiserver.service

```
--enable-admission-plugins=NodeRestriction,NamespaceAutoProvision
```

pour le descativer

```
--disable-admission-plugins=DefaultSwtorageClass
```

2. Dans le fichier /etc/kubernetes/manifests/kube-apiserver.yaml

```
--enable-admission-plugins=NodeRestriction,NamespaceAutoProvision
```

NB: Le contrôleur d’admission NamespaceLifecycle veille à ce que toute requête adressée à un espace de noms (namespace) inexistant soit rejetée.
Il protège également les espaces de noms par défaut — notamment default, kube-system et kube-public — afin qu’ils ne puissent pas être supprimés.

3. pour des messure de sécurité

```
--disable-admission-plugins=DefaultStorageClass
```

4. pour voir les admission controller ativé

```
ps -ef | grep kube-apiserver | grep admission-plugins
```