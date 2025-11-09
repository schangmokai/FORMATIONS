### Étapes d’installation sur Ubuntu Desktop

```
sudo apt update
sudo apt install lynis -y
```

### Lancement

```
sudo lynis audit system
```

### Voir le rapport complet

```
/var/log/lynis.log
/var/log/lynis-report.dat
```

### Pour l'utilisation du CIS officielle

0. prerequi intaller java
 
```
 sudo apt install default-jre -y
```

https://www.cisecurity.org/cis-cat-lite
(Gratuit mais tu dois créer un compte CIS pour télécharger)

1. Télécharger CIS-CAT-Lite-v4.x.x.zip

2. unzip CIS-CAT-Lite-v4.x.x.zip -d ciscat
   cd ciscat

3. chmod +x CIS-CAT.sh

4. sudo ./CIS-CAT.sh -b benchmark/Ubuntu_Linux_22.04_LTS_Benchmark_v2.0.0-xccdf.xml -a

### Le rapport final se trouve dans reports/

## Desactiver le port usb de maniere permanante

```
echo "blacklist usb_storage" | sudo tee /etc/modprobe.d/blacklist-usb-storage.conf
```
### pour charger 

```
sudo update-initramfs -u
```

### restart

```
sudo reboot
```
### Desactiver le compte root

```
sudo passwd -l root
sudo passwd -S root

```

### Pour redefinir le password root

```
sudo passwd root
```


### Désactivation des ports USB

```
sudo modprobe -r uas
sudo 
```



