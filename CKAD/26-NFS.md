### mise en place d'un serveur NFS


```
sudo apt-get install nfs-kernel-server
	 
sudo vim /etc/exports
/srv/exports 192.168.56.0/24(rw,sync,no_root_squash)
/srv/un_repertoire  @IP(rw,sync,no_subtree_check,no_root_squash)
sudo exportfs -a
```

### Exploitation dans un volume docker

```
# Installer le client NFS
sudo apt install -y nfs-common

# Créer le point de montage
sudo mkdir -p /mnt/nfs/uploads

# Monter le NFS
sudo mount -t nfs @IP:/srv/exports /mnt/nfs/uploads
sudo mount -t nfs 51.20.174.116:/srv/exports /mnt/nfs/uploads
sudo mount -t nfs 51.20.174.116:/srv/exports /home/fdoumtsop/app/config/phenix-nhpc-agg/uploads

# Vérifier
df -h | grep nfs
ls /mnt/nfs/uploads

```


### pour desactiver

```
sudo umount /mnt/nfs/uploads
```