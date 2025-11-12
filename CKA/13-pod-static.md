## POD STATIC

La grande difference entre un pod Static et un DaemonSet est que le DaemonSet est exécuté par l'API server alors que le static pod est directement géré par le kubelet du noeud.

On distingue plusieurs pods STatic dans kubernetes

1. api-server
2. scheduler
3. et-cd
4. etc.

Toutes définition de pod (yaml) placé dans /etc/kubernetes/manifests seront directement exécuté par le kubelet du noeud sous forme de pod static.

### pourquoi le kubelet exécute directement les pod static

c'est parceque quand la definition du kubelet (ExecStart)

    --pod-manifest-path=/etc/kubernetes/manifests

Nous pouvons également avoir :

    --config=kubeconfig.yaml

kubeconfig.yaml

    staticPath: /etc/kubernetes/manifests

### comment visualiser les pods static 

```
crictl ps
docker ps
nerdctl ps
```

NB: Le kube-scheduler ne s'occupe pas des pod stati ni des DaemonSet
