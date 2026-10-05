# Sur le master node, vérifier l'expiration des certificats
sudo kubeadm certs check-expiration

# Renouveler tous les certificats
sudo kubeadm certs renew all

# Copier le nouveau admin.conf
sudo cp /etc/kubernetes/admin.conf ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config

# Redémarrer les composants (si nécessaire)
sudo systemctl restart kubelet


### si c'est problème d'accès a la commande kubectl 

### si vous constaté les difference dans les fichier ci-dessous

# Comparer votre config avec celle du système
diff ~/.kube/config /etc/kubernetes/admin.conf

# Si différents, utilisez le fichier système
sudo cp /etc/kubernetes/admin.conf ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
chmod 600 ~/.kube/config


## OU ENCORE

si cette commande marche

sudo kubectl --kubeconfig=/etc/kubernetes/admin.conf get nodes

alors le fichier ~/.kube/config n'est plus à jours.

# Sauvegarder l'ancien fichier (au cas où)
cp ~/.kube/config ~/.kube/config.backup.$(date +%Y%m%d)

# Remplacer par le fichier à jour
sudo cp /etc/kubernetes/admin.conf ~/.kube/config

# Ajuster le propriétaire et les permissions
sudo chown $(id -u):$(id -g) ~/.kube/config
chmod 600 ~/.kube/config

# Tester
kubectl get nodes