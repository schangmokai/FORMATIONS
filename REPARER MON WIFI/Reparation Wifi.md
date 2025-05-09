## DEBUT DU DOCUMENT

### Mettre à jour les paquets
```
sudo apt update
```
### Installer un paquet
```
sudo apt install curl
```
### 1- verifier l'existance du repertoire '/run/systemd/resolve/stub-resolv.conf'
Si le fichier n'existe pas, bien vouloir le créer

```
 cd /run/systemd/
```
```
 sudo mkdir resolve
```
```
 cd resolve/
```
```
 sudo nano stub-resolv.conf
```


### 2- A l'intérieur du fichier ajouter les deux lignes suivantes:

```
 nameserver 8.8.8.8
 nameserver 8.8.4.4
```

### 3- Redemarer

```
sudo systemctl restart systemd-resolved
sudo nano /etc/resolv.conf
nameserver 127.0.0.1
sudo systemctl restart systemd-resolved
```

## FIN DU DOCUMENT






