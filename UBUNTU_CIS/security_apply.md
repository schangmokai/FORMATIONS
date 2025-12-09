Dans cubic première action:

echo "127.0.1.1 cubic" >> /etc/hosts



### USB DESACTIVATION

sudo modprobe -r uas
sudo modprobe -r usb_storage
echo "blacklist usb_storage" | sudo tee /etc/modprobe.d/disable-usb-storage.conf
echo "blacklist uas" | sudo tee -a /etc/modprobe.d/disable-usb-storage.conf
cat /etc/modprobe.d/disable-usb-storage.conf
sudo reboot


### deactiver le compte root

1- Par défaut il est désactivé sur ubuntu 24.04

sudo grep root /etc/shadow
[sudo] Mot de passe de fincteck-01: 
root:!*:20134:0:99999:7:::

Le "!" signifie que le compte n'a pas de password donc il est désactivé

2- Si vraiment il est activé et qu'on souhaite le desactiver
 
   sudo passwd -l root

3- interdire  toutes ouverture de session en ssh avec root

   sudo usermod -s /usr/sbin/nologin root


### sudo retreint

Ubuntu restreint déjà l'accès à root
pour voir la liste des user autoriser à utiliser root

getent group sudo

pour retirer un utilisateur du groupe

sudo deluser nom_utilisateur sudo
 

### chiffrement LUKS pour disque et email

### Antivirus/EDR checkpoint installé

   Antivirus
   #########

   sudo apt install clamav clamav-daemon -y

   Service de mise à jours automatique
   ###################################

   sudo systemctl start clamav-freshclam

   Pour scanner un dossier
   #######################

   clamscan -r /home/ton_user

Installation de l'EDR CHECKPOINT

Download Checkpoint

https://support.checkpoint.com/results/download/137269?utm_source=finctek

unzip install_deb_1.22.12.sh.zip

#### Cas 1 : Installation complète (EDR + Anti-Malware) via serveur distant

sudo ./install_deb_1.22.12.sh install \
  --source remote \
  --url https://<ton_management_server_url> \
  --key <clé fournie par le management>

#### Cas 2 : Installation locale depuis un paquet

sudo ./install_deb_1.22.12.sh install \
  --source local \
  --file_name sandblast-agent_1.22.12_amd64.deb

#### Cas 3 : Installer uniquement EDR

sudo ./install_deb_1.22.12.sh install \
  --source remote \
  --product edr \
  --url https://<ton_management_server_url> \
  --key <clé>

#### Cas 4 : Installer uniquement Anti‑Malware (AM)

sudo ./install_deb_1.22.12.sh install \
  --source remote \
  --product am \
  --url https://<ton_management_server_url> \
  --key <clé>

#### Cas 5 : Ajouter des options de proxy

sudo ./install_deb_1.22.12.sh install \
  --source remote \
  --url https://<server_url> \
  --key <clé> \
  --https_proxy http://proxy:3128 \
  --no_proxy 127.0.0.1,localhost

#### Cas 6 : Activer les logs de debug + auto-réparation

sudo ./install_deb_1.22.12.sh install \
  --source remote \
  --url https://<server_url> \
  --key <clé> \
  --debug \
  --selfhealing

#### desinstaller

sudo ./install_deb_1.22.12.sh uninstall

sudo ./install_deb_1.22.12.sh uninstall --product edr


### Activation de l'auditd (envoyer les logs au SIEM)

sudo apt update
sudo apt install auditd audispd-plugins -y

1. Activer et démarrer le service

sudo systemctl enable auditd
sudo systemctl start auditd

2. Vérifie qu’il tourne correctement

sudo systemctl status auditd


## pour verifier

0. create user

   sudo adduser test

   sudo ausearch -x useradd

1. placer une règle sur le fichier  des password

   sudo auditctl -w /etc/passwd -p rwxa -k passwd_watch

2. Verifier les logs liés à cette règle

   sudo ausearch -k passwd_watch

   sudo ausearch -f /etc/passwd | grep -B5 useradd

   sudo ausearch -x useradd
3. supprimer le user test crée

   sudo deluser test

   sudo deluser --remove-home test

   sudo ausearch -x userdel


### Pare-feu: règle strictes

   sudo ufw default deny incoming
   sudo ufw default deny outgoing
   sudo ufw default deny routed

### Installation

   1. Thunderbird OK
   2. OnlyOffice OK
    
     sudo apt update
sudo apt install -y wget gnupg2
wget -qO - https://download.onlyoffice.com/GPG-KEY-ONLYOFFICE | sudo gpg --dearmor -o /usr/share/keyrings/onlyoffice.gpg
echo "deb [signed-by=/usr/share/keyrings/onlyoffice.gpg] https://download.onlyoffice.com/repo/debian/ squeeze main" | sudo tee /etc/apt/sources.list.d/onlyoffice.list

   3. Firefox  OK

sudo add-apt-repository ppa:mozillateam/ppa -y
sudo apt update
sudo apt install -y firefox

   4. ClientFortigate OK
   5. Python OK 
   6. KSuite (Kdrive) 
   7. Remmina

sudo apt update
sudo apt install -y remmina remmina-plugin-rdp remmina-plugin-vnc remmina-plugin-secret remmina-plugin-spice remmina-plugin-nx
sudo apt install -y software-properties-common
sudo add-apt-repository ppa:remmina-ppa-team/remmina-next -y
sudo apt update
sudo apt install -y remmina remmina-plugin-* libfreerdp*

   8. Python QuantLib

# 1. Installe Python et outils de build
sudo apt update
sudo apt install -y python3-venv python3-pip python3-dev build-essential

# 2. Crée un venv local à l'utilisateur
python3 -m venv ~/quantlib-venv

# 3. Active le venv
source ~/quantlib-venv/bin/activate

# 4. Upgrade pip et setuptools
pip install --upgrade pip setuptools wheel

# 5. Installe QuantLib-Python
pip install QuantLib-Python

# 6. Test rapide
python -c "import QuantLib as ql; print('QuantLib OK, date:', ql.Date().todaysDate())"


### Extraction de l'image

   sudo apt update
   snap install cubic
   sudo add-apt-repository ppa:cubic-wizard/release
   sudo apt update
   sudo apt install cubic

   cubic

### extraction de l'image avec clonezilla

   download: https://clonezilla.org/downloads/download.php?branch=alternative
   amd4
   iso

   sudo dd if=clonezilla-live-20251017-questing-amd64.iso of=/dev/sda bs=4M status=progress oflag=sync
   sync
   sudo eject /dev/sda

### installation de OpenVPN

   sudo apt install openvpn -y
   openvpn --version

### Lancer openfortivpn depuis une icone

nano ~/.local/share/applications/OpenFortiVPN.desktop

paste
=====

[Desktop Entry]
Name=OpenFortiVPN
Comment=Connect to FortiGate VPN
Exec=gnome-terminal -- bash -c "sudo openfortivpn -c /etc/openfortivpn/config; exec bash"
Icon=network-vpn
Terminal=false
Type=Application
Categories=Network;VPN;

chmod +x ~/.local/share/applications/OpenFortiVPN.desktop


### kDrive

wget https://download.storage.infomaniak.com/drive/desktopclient/kDrive-3.7.6.20250908-amd64.AppImage
sudo apt install -y libfuse2
chmod +x kDrive-3.7.6.20250908-amd64.AppImage
./kDrive-3.7.6.20250908-amd64.AppImage

Pour faire aparaitre l'icone:

kDrive.png

sudo nano /usr/share/applications/kDrive.desktop

Paste
=====

[Desktop Entry]
Name=kDrive
Comment=Client Infomaniak kDrive
Exec=/opt/kdrive/kDrive-3.7.6.20250908-amd64.AppImage
Icon=/opt/kdrive/kDrive.png
Terminal=false
Type=Application
Categories=Network;FileTransfer;

wget -O /opt/kdrive/kDrive.png https://play-lh.googleusercontent.com/lV6ZpVBoczzBlp_bofZQ0jxMjFM4ASYOpk1f0-W08FQPNOFdwAUlksh-MBqhP43IQTg

sudo chmod +x /usr/share/applications/kDrive.desktop


## installation de keypass

sudo apt update
sudo apt install keepassxc

keepassxc-appimage


## installation de edge

sudo apt update
sudo apt install software-properties-common apt-transport-https wget

wget -q https://packages.microsoft.com/keys/microsoft.asc -O- | sudo apt-key add -

sudo add-apt-repository "deb [arch=amd64] https://packages.microsoft.com/repos/edge stable main"

sudo apt update
sudo apt install microsoft-edge-stable


### Script de mise à jours d'ubuntu

sudo nano /usr/local/bin/update-ubuntu.sh

#!/bin/bash

# Mise à jour de la liste des paquets
apt update -y

# Mise à niveau automatique
apt upgrade -y

# Nettoyage des paquets inutiles
apt autoremove -y
apt autoclean -y



sudo chmod +x /usr/local/bin/update-ubuntu.sh

sudo crontab -e

0 3 * * 2 /usr/local/bin/update-ubuntu.sh >> /var/log/update-ubuntu.log 2>&1


### remmina en invite de commande 

xfreerdp /v:10.145.10.98 /u:fdoumtsop

### préparation de l'icone pour freerdp

sudo nano /usr/share/applications/xfreerdp.desktop

Paste
=====

[Desktop Entry]
Name=xfreerdp
Comment=Client xfreerdp
Exec=xfreerdp /v:10.145.10.98 /u:fdoumtsop /dynamic-resolution /size:1920x1080
Icon=/opt/xfreerdp/xfreerdp.png
Terminal=true
Type=Application
Categories=Network;FileTransfer;


sudo mkdir -p /opt/xfreerdp
sudo wget -O /opt/xfreerdp/xfreerdp.png https://raw.githubusercontent.com/hfg-gmuend/openmoji/master/color/svg/1F5A5.svg
sudo chmod +x /usr/share/applications/xfreerdp.desktop
